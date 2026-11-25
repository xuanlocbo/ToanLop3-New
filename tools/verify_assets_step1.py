#!/usr/bin/env python3
"""Step 1 only: validate the checked-in art archive, render every image, and make review sheets."""
import base64
import collections
import hashlib
import io
import json
import math
import os
import re
import subprocess
import sys
import zipfile
from pathlib import Path, PurePosixPath
import xml.etree.ElementTree as ET

import cairosvg
from PIL import Image, ImageDraw, ImageFont

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "asset-step1-qa"
OUT.mkdir(exist_ok=True)
ZIP = ROOT / "MocNhien-Toan3-assets-step1.zip"
BASE = "8320b128928deb6e04fd40099a1280b3bb1740fc"
BASE_RUNTIME_SHA = "ae6cf6e94c017bdcbfdc008cb91c4c9472a53a34e9a20a4bcf70c009a38193e3"
COUNTS = {"ui": 5, "characters": 9, "objects": 77, "animals": 21,
          "instruments": 23, "icons": 28, "game": 20, "backgrounds": 4,
          "stages": 19, "layouts": 12}
TITLES = {"ui": "Ảnh giao diện gốc", "characters": "Nhân vật",
          "objects": "Đồ vật sinh hoạt", "animals": "Động vật",
          "instruments": "Dụng cụ và hình toán học", "icons": "Icon chức năng",
          "game": "Vật thể trò chơi", "backgrounds": "Bối cảnh",
          "stages": "19 màn chơi", "layouts": "Bố cục tham chiếu"}
errors = []
warnings = [
    "Kiểm tra tự động không thay thế việc xem và duyệt từng hình.",
    "Bản #51 chưa được xác định bằng ID workflow run; đối chiếu commit v1.2.1-ui.",
    "Hình SVG và bố cục mới chưa được xác nhận trùng khớp thiết kế đã duyệt.",
    "Ánh xạ theo từ khóa là gợi ý; cần xác minh ngữ cảnh từng câu trước khi tích hợp.",
    "Không dùng mẫu bố cục hoặc hình tĩnh để thay thế dữ liệu đo lường/hình học động.",
]
report = {"base_commit": BASE, "archive_sha256": None, "archive_files": None,
          "artwork_count": None, "question_count": None, "visual_type_count": None,
          "original_webp_identical": [], "categories": {}, "renders": [],
          "sheets": [], "errors": errors, "warnings": warnings}

def require(ok, label):
    if not ok:
        errors.append(label)
        print("FAIL:", label, flush=True)
    return bool(ok)

def git(*args, data=None):
    return subprocess.run(["git", *args], cwd=ROOT, input=data,
                          stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                          check=True).stdout

def load(z, path):
    return json.loads(z.read(path))

def keytag(tag):
    return tag.rsplit("}", 1)[-1]

def font(size, bold=False):
    name = "DejaVuSans-Bold.ttf" if bold else "DejaVuSans.ttf"
    return ImageFont.truetype("/usr/share/fonts/truetype/dejavu/" + name, size)

def wrap_text(draw, value, f, max_width, max_lines=2):
    words = str(value).split()
    lines = []
    line = ""
    for word in words:
        nxt = (line + " " + word).strip()
        if line and draw.textlength(nxt, font=f) > max_width:
            lines.append(line)
            line = word
        else:
            line = nxt
    if line:
        lines.append(line)
    if len(lines) > max_lines:
        lines = lines[:max_lines]
        while lines[-1] and draw.textlength(lines[-1] + "…", font=f) > max_width:
            lines[-1] = lines[-1][:-1]
        lines[-1] += "…"
    return lines

def review_sheets(category, entries):
    if category == "layouts":
        cols, rows, tw, th = 3, 2, 390, 630
    elif category == "stages":
        cols, rows, tw, th = 3, 3, 390, 230
    elif category == "backgrounds" or category == "ui":
        cols, rows, tw, th = 2, 3, 560, 310
    else:
        cols, rows, tw, th = 5, 4, 240, 242
    per = cols * rows
    pages = math.ceil(len(entries) / per)
    for page in range(pages):
        subset = entries[page*per:(page+1)*per]
        margin, gap, header = 24, 14, 94
        width = 2*margin + cols*tw + (cols-1)*gap
        height = header + math.ceil(len(subset)/cols)*(th+gap) + margin
        sheet = Image.new("RGB", (width, height), "#fff5fa")
        draw = ImageDraw.Draw(sheet)
        draw.text((margin, 17), f"{TITLES[category]} — {page+1}/{pages}", font=font(28, True), fill="#67415d")
        draw.text((margin, 55), "Mộc Nhiên Toán 3 · Bước 1 · Bảng ảnh để duyệt", font=font(15), fill="#826879")
        for i, entry in enumerate(subset):
            x = margin + (i%cols)*(tw+gap)
            y = header + (i//cols)*(th+gap)
            draw.rounded_rectangle((x,y,x+tw,y+th), radius=14, fill="#ffffff", outline="#efd7e4", width=2)
            render_path = OUT / "rendered" / category / (entry["id"] + ".png")
            if render_path.exists():
                with Image.open(render_path) as raw:
                    art = raw.convert("RGBA")
                    art.thumbnail((tw-24, th-72), Image.Resampling.LANCZOS)
                    sheet.paste(art, (x+(tw-art.width)//2, y+10+(th-72-art.height)//2), art)
            else:
                draw.text((x+15, y+25), "LỖI KẾT XUẤT", font=font(18, True), fill="#a52b4b")
            f = font(14, True)
            for j, line in enumerate(wrap_text(draw, entry["label"], f, tw-20, 2)):
                draw.text((x+(tw-draw.textlength(line,font=f))/2,y+th-53+j*17),line,font=f,fill="#67415d")
            tag = entry["id"]
            tagfont = font(11)
            draw.text((x+(tw-draw.textlength(tag,font=tagfont))/2,y+th-15),tag,font=tagfont,fill="#826879",anchor="ls")
        dest = OUT / "contact-sheets" / f"{category}-{page+1:02}.png"
        dest.parent.mkdir(exist_ok=True)
        sheet.save(dest, optimize=True)
        report["sheets"].append(dest.relative_to(OUT).as_posix())

def render_one(z, asset, allowed_webp):
    asset_id, cat, rel = asset["id"], asset["category"], asset["path"]
    if not re.fullmatch(r"[a-zA-Z0-9_]+", asset_id):
        raise ValueError("Unsafe asset ID")
    if cat not in COUNTS:
        raise ValueError("Unknown category")
    data = z.read(rel)
    if asset["format"] == "svg":
        if b"<!DOCTYPE" in data.upper() or b"<!ENTITY" in data.upper():
            raise ValueError("Unexpected XML declaration")
        root = ET.fromstring(data)
        if keytag(root.tag) != "svg":
            raise ValueError("Not SVG")
        w, h = int(root.attrib["width"]), int(root.attrib["height"])
        if (w,h) != (asset["width"],asset["height"]) or not (0<w<=4096 and 0<h<=4096):
            raise ValueError("Wrong dimensions")
        ids = [e.attrib["id"] for e in root.iter() if "id" in e.attrib]
        if len(ids) != len(set(ids)):
            raise ValueError("Duplicate XML ID")
        for e in root.iter():
            if keytag(e.tag) in ("script", "foreignObject"):
                raise ValueError("Non-static SVG element")
            for k,v in e.attrib.items():
                if keytag(k).lower().startswith("on"):
                    raise ValueError("Event handler")
                if keytag(k) == "href":
                    if v.startswith("data:image/webp;base64,"):
                        emb = base64.b64decode(v.split(",",1)[1], validate=True)
                        if emb not in allowed_webp:
                            raise ValueError("Embedded artwork differs from original")
                    elif not v.startswith("#"):
                        raise ValueError("External reference")
                for match in re.findall(r"url\(([^)]+)\)",v):
                    match = match.strip("'\"")
                    if not match.startswith("#") or match[1:] not in ids:
                        raise ValueError("Unresolved/external SVG reference: "+match)
        png = cairosvg.svg2png(bytestring=data, output_width=w, output_height=h, unsafe=False)
        with Image.open(io.BytesIO(png)) as im:
            img = im.convert("RGBA")
    elif asset["format"] == "webp":
        with Image.open(io.BytesIO(data)) as im:
            if im.format != "WEBP":
                raise ValueError("Wrong WebP signature")
            img = im.convert("RGBA")
    else:
        raise ValueError("Unknown format")
    alpha = img.getchannel("A")
    bbox = alpha.getbbox()
    if not bbox:
        raise ValueError("Invisible/blank image")
    if min(img.size)<8:
        raise ValueError("Image too small")
    # Require visible variation; this detects empty or single-color artwork,
    # not aesthetic or mathematical correctness.
    visible = img.crop(bbox)
    rgb = Image.new("RGB", visible.size, "#ffffff")
    rgb.paste(visible, mask=visible.getchannel("A"))
    if all(lo==hi for lo,hi in rgb.getextrema()):
        raise ValueError("Single-color artwork")
    dest = OUT / "rendered" / cat / (asset_id+".png")
    dest.parent.mkdir(parents=True, exist_ok=True)
    img.save(dest, optimize=True)
    return {"id":asset_id, "category":cat, "width":img.width, "height":img.height,
            "alpha_bbox":list(bbox), "sha256":hashlib.sha256(data).hexdigest(),
            "rendered":dest.relative_to(OUT).as_posix()}

def main():
    report["archive_sha256"] = hashlib.sha256(ZIP.read_bytes()).hexdigest()
    # Guard against changing the baseline Android code or curriculum in step 1.
    changed = git("diff","--name-only",BASE,"HEAD").decode().splitlines()
    allowed = {ZIP.name, ".github/workflows/asset-step1-qa.yml", "tools/verify_assets_step1.py"}
    require(set(changed) <= allowed, "Unexpected change outside step 1: "+str(sorted(set(changed)-allowed)))
    with zipfile.ZipFile(ZIP) as z:
        names = z.namelist()
        report["archive_files"] = len(names)
        require(len(names)==225 and len(set(names))==225, "ZIP file count/unique paths")
        require(z.testzip() is None, "ZIP CRC integrity")
        require(all(not PurePosixPath(n).is_absolute() and ".." not in PurePosixPath(n).parts and not n.endswith("/") and "\\" not in n for n in names), "Safe ZIP paths")
        manifest = load(z,"spec/asset-manifest.json")
        cov = load(z,"spec/coverage.json")
        qmap = load(z,"spec/question-asset-map.json")
        smap = load(z,"spec/stage-map.json")
        design = load(z,"spec/design-tokens.json")
        assets = manifest["assets"]
        ids = {a["id"]:a for a in assets}
        report["artwork_count"] = len(assets)
        report["categories"] = dict(collections.Counter(a["category"] for a in assets))
        require(report["categories"]==COUNTS, "Exact artwork categories and counts")
        require(manifest["assetCount"]==len(assets)==len(ids)==218, "Unique manifest asset IDs")
        require(manifest["sourceRef"]==design["baseCommit"]==BASE, "Source revision recorded")
        require(len({a["path"] for a in assets})==218, "Unique artwork paths")
        expected_paths = {a["path"] for a in assets} | {
            "spec/asset-manifest.json","spec/question-asset-map.json","spec/stage-map.json",
            "spec/design-tokens.json","spec/coverage.json","README.txt","catalog.html"}
        require(set(names)==expected_paths, "Archive matches manifest")
        require(all(k in ids for k in manifest["aliases"]), "All object aliases resolve")

        originals = []
        for asset in (a for a in assets if a["category"]=="ui"):
            rel = asset["path"]
            existing = (ROOT / "app/src/main" / rel).read_bytes()
            stored = z.read(rel)
            sha = git("hash-object","--stdin",data=stored).decode().strip()
            if require(stored==existing and sha==asset["sourceBlob"], "Original image parity: "+rel):
                report["original_webp_identical"].append(rel)
            originals.append(stored)
        require(len(originals)==5, "Five original illustrations")

        source = {}
        for f in cov["sourceFiles"]:
            p=ROOT/"app/src/main/assets/curriculum"/f["name"]
            data=p.read_bytes()
            require(git("hash-object","--stdin",data=data).decode().strip()==f["sha"],"Source SHA: "+f["name"])
            rows = [q for l in json.loads(data)["lessons"] for q in l["items"]]
            require(len(rows)==f["count"],"Source count: "+f["name"])
            for q in rows:
                require(q["id"] not in source,"Duplicate question: "+q["id"])
                source[q["id"]]=q
        require(len(cov["sourceFiles"])==12 and len(source)==959,"12 source files / 959 unique IDs")
        generated=OUT/"runtime_v2.json"
        subprocess.run(["node","tools/prepare_curriculum.mjs",str(generated)],cwd=ROOT,check=True)
        runtime_sha=hashlib.sha256(generated.read_bytes()).hexdigest()
        report["runtime_sha256"]=runtime_sha
        require(runtime_sha==BASE_RUNTIME_SHA,"Runtime data unchanged from tested baseline")
        generated.unlink()
        qids={q["id"] for q in qmap}
        require(len(qmap)==len(qids)==959 and qids==set(source),"All 959 question IDs mapped")
        report["question_count"]=len(qids)
        report["questions_with_candidate_art"]=sum(bool(q["assetCandidates"]) for q in qmap)
        types=collections.Counter(q["visualType"] for q in qmap)
        report["visual_type_count"]=len(types)
        require(dict(types)==cov["visualTypes"] and set(types)==set(manifest["visualTypeRules"]),"Visual-type coverage")
        for row in qmap:
            q=source.get(row["id"])
            if not q:
                continue
            expected_type=(q.get("visual") or {}).get("type") or "(none)"
            require(row["visualType"]==expected_type and row["lesson"]==q["lesson"] and row["book"]==q["book"] and row["kind"]==q["kind"],"Metadata parity: "+row["id"])
            require(all(x in ids for x in row["assetCandidates"]),"Unresolved question asset: "+row["id"])
            require(not ({"answer","check","solution","workedSteps","sampleAnswer"} & set(row)),"Answer leakage: "+row["id"])
        for typ,rule in manifest["visualTypeRules"].items():
            require(all(x in ids for x in rule["assets"]),"Unresolved visual-type asset: "+typ)
        for typ in ("ruler","cups","pour","thermometer","dial_scale","balance","geometry_grid","solid_stack","dice_board"):
            require(manifest["visualTypeRules"].get(typ,{}).get("render")=="native_dynamic","Must remain dynamic: "+typ)
        require([s["lesson"] for s in smap]==list(range(26,45)),"19 ordered stage records")
        plan=(ROOT/"app/src/main/java/vn/mocnhien/toan3/AdventurePlan.java").read_text()
        titles=re.findall(r'"([^"]+)"',plan.split("NAMES={",1)[1].split("};",1)[0])
        require([s["title"] for s in smap]==titles,"Stage names match application")
        for s in smap:
            l=s["lesson"]
            theme="forest" if l<=29 else "snow" if l<=35 else "crystal" if l<=40 else "pink"
            require(s["background"]=="bg_"+theme,"Stage theme: "+str(l))
            require(all(x in ids for x in [s["art"],s["background"],*s["props"]]),"Unresolved stage assets: "+str(l))
        catalog=z.read("catalog.html").decode("utf-8")
        require(all(a["path"] in catalog for a in assets),"Catalog includes all images")
        require("http://" not in catalog and "https://" not in catalog,"Catalog has no remote image dependencies")

        for a in assets:
            try:
                result=render_one(z,a,originals)
                report["renders"].append(result)
                print("RENDERED",a["id"],result["width"],result["height"],flush=True)
            except Exception as e:
                require(False,"Render "+a["id"]+": "+str(e))
        for cat in COUNTS:
            review_sheets(cat,[a for a in assets if a["category"]==cat])
        require(len(report["renders"])==218,"All 218 images rendered")

try:
    main()
except Exception as exc:
    require(False, f"Unexpected QA failure: {exc!r}")
finally:
    report["automatic_checks_passed"] = not errors
    report["visual_approval"] = "PENDING_HUMAN_REVIEW"
    report["apk_built"] = False
    report["step2_started"] = False
    (OUT/"report.json").write_text(json.dumps(report,ensure_ascii=False,indent=2),encoding="utf-8")
    lines=["# Bước 1 — Kiểm tra bộ tài nguyên",
           "", "**"+("PASS — Kiểm tra tự động" if not errors else "FAIL — Có lỗi cần sửa")+"**",
           "",f"- Tệp ZIP: {report['archive_files']}/225",
           f"- Tài nguyên: {report['artwork_count']}/218",
           f"- Hình kết xuất: {len(report['renders'])}/218",
           f"- Mục học: {report['question_count']}/959",
           f"- Ảnh gốc giữ nguyên: {len(report['original_webp_identical'])}/5",
           f"- Bảng ảnh PNG: {len(report['sheets'])}",
           "", "## Điều kiện còn lại", *["- "+x for x in warnings],
           "", "## Lỗi", *(["- "+x for x in errors] if errors else ["- Không phát hiện lỗi tự động."]),
           "", "Chưa tích hợp; không xuất APK ở bước này."]
    summary="\n".join(lines)+"\n"
    (OUT/"report.md").write_text(summary,encoding="utf-8")
    if os.environ.get("GITHUB_STEP_SUMMARY"):
        with open(os.environ["GITHUB_STEP_SUMMARY"],"a",encoding="utf-8") as f:
            f.write(summary)
    print(summary,flush=True)
sys.exit(1 if errors else 0)
