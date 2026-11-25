#!/usr/bin/env python3
"""Compile the reviewed source asset archive into native Android bitmap assets.
No HTML, WebView, curriculum edits, fixed measurement readings, or answer data.
"""
import hashlib, io, json, re, sys, zipfile, shutil
from pathlib import Path
from xml.etree import ElementTree as ET
import cairosvg
from PIL import Image, ImageDraw
ROOT=Path(__file__).resolve().parents[1]
PACK=ROOT/"MocNhien-Toan3-assets-step1.zip"
BASE="8320b128928deb6e04fd40099a1280b3bb1740fc"
SOURCE=ROOT/"app/src/main/assets/curriculum"
OUT=Path(sys.argv[1])
REPORT=Path(sys.argv[2])
def read(z,n):
    return json.loads(z.read(n))
def write(p,x):
    p.parent.mkdir(parents=True,exist_ok=True)
    p.write_text(json.dumps(x,ensure_ascii=False,separators=(",",":")),encoding="utf8")
def norm(x): return " ".join(x.casefold().split())
def phrase(text,w):
    return re.search(r"(?<!\w)"+re.escape(w)+r"(?!\w)",text,re.UNICODE) is not None
# Only concrete, unambiguous nouns actually present in the visible prompt.
# Do not infer an answer or display the number of objects from candidate matching.
SAFE={
 "bowl":["bát"],"bowl_water":["bát nước"],"chopsticks":["đũa"],"spoon":["thìa","muỗng"],"plate":["đĩa"],
 "cup":["cốc","chén"],"teapot":["ấm nước"],"bottle":["chai nước","chai"],"milk_carton":["hộp sữa"],
 "pitcher":["bình nước"],"apple":["quả táo","táo"],"orange":["quả cam"],"pear":["quả lê"],"mango":["quả xoài"],
 "pomelo":["quả bưởi"],"grapes":["chùm nho"],"strawberry":["dâu tây"],"tomato":["cà chua"],"gourd":["bí đao"],
 "egg":["quả trứng","trứng"],"basket":["giỏ","rổ"],"fruit_basket":["giỏ hoa quả"],
 "rice_bag":["bao gạo","gạo"],"corn_bag":["bao ngô"],"flour_bag":["bột mì"],"sugar_bag":["gói đường","túi đường"],
 "salt_bag":["gói muối","túi muối"],"pepper_bag":["hạt tiêu"],"seasoning_bag":["mì chính","bột ngọt","bột canh"],
 "noodle_bag":["gói mì"],"cereal_bag":["ngũ cốc"],"apple_bag":["túi táo"],"tomato_bag":["túi cà chua"],
 "candy":["kẹo"],"cake":["cái bánh","chiếc bánh","miếng bánh"],"cookie":["bánh quy"],"book":["quyển sách","cuốn sách","quyển truyện"],
 "notebook":["quyển vở","vở"],"pencil":["bút chì"],"colored_pencil":["bút sáp","bút màu"],"pen":["bút bi","bút máy","cái bút","chiếc bút"],
 "eraser":["cục tẩy"],"pencil_case":["hộp bút"],"paper_clip":["kẹp giấy","ghim"],"binder_clip":["kẹp giấy bướm"],
 "gift_box":["hộp quà"],"box":["kiện hàng","thùng hàng"],"wood_plank":["tấm gỗ","tấm ván"],"rope":["sợi dây","đoạn dây"],
 "table":["cái bàn","chiếc bàn","bàn học"],"chair":["cái ghế","chiếc ghế"],"ball":["quả bóng"],"coin":["đồng xu"],
 "dumbbell":["quả tạ","tạ tay"],"laptop":["máy tính xách tay"],"car":["ô tô"],"truck":["xe tải"],"bicycle":["xe đạp"],
 "boat":["thuyền"],"bridge":["cây cầu","chiếc cầu"],"plant":["cây giống"],"seed":["hạt giống"],"flower":["bông hoa"],
 "star":["ngôi sao"],"gem":["viên đá quý","viên pha lê"],"marble":["viên bi"],"dice":["xúc xắc"],"flag":["lá cờ"],
 "robot":["rô-bốt","robot"],"wooden_doll":["người gỗ"],"cat":["mèo"],"dog":["chó"],"pig":["lợn","heo"],
 "cow":["con bò"],"calf":["con bê"],"horse":["con ngựa"],"rabbit_white":["thỏ trắng","con thỏ"],"rabbit_brown":["thỏ nâu"],
 "chicken":["con gà"],"bird":["con chim"],"goose":["ngỗng","chim nước"],"large_bird":["chim lớn"],"fish":["con cá"],
 "butterfly":["bướm"],"bee":["con ong","đàn ong"],"ant":["con kiến"],"cicada":["ve sầu"],"grasshopper":["châu chấu","cào cào"],
 "mantis":["bọ ngựa"],"dragonfly":["chuồn chuồn"],"cricket":["con dế"],"chess_rook":["quân xe"],
 "chess_knight":["quân mã"],"chess_pawn":["quân tốt"],"chess_king":["quân vua"]
}
# Existing precision diagrams keep native numeric rendering. Do not add a
# decorative item row for them or for answer-choice and geometry exercises.
ILLUSTRATE={"(none)","equal_groups","boats","seating","division_context","animal_groups","object",
 "real_world","real_world_table","real_world_measure","quantity_removed","cut_segments","nested_groups",
 "grouping_example","capacity_grouping","bar_groups"}
def main():
    with zipfile.ZipFile(PACK) as z:
        assert z.testzip() is None and len(z.namelist())==len(set(z.namelist()))==225
        manifest=read(z,"spec/asset-manifest.json")
        qmap=read(z,"spec/question-asset-map.json")
        stages=read(z,"spec/stage-map.json")
        assert manifest["sourceRef"]==BASE and len(manifest["assets"])==218
        ids={a["id"]:a for a in manifest["assets"]}
        assert len(ids)==218 and len(stages)==19
        assert {s["lesson"] for s in stages}==set(range(26,45))
        coverage=read(z,"spec/coverage.json")
        expected=["sgk_26_29","sgk_30_35","sgk_36_37","sgk_38_40","sgk_41_42","sgk_43_44",
                  "vbt_26_29","vbt_30_35","vbt_36_38","vbt_39_41","vbt_42_44","bonus_26_44"]
        records=coverage["sourceFiles"]
        assert [r["name"] for r in records]==[name+".json" for name in expected], "Asset pack source list differs from curriculum generator"
        rows={}
        for record in records:
            src=SOURCE/record["name"]
            raw=src.read_bytes()
            # Git blob identity is verified, not merely the filename or count.
            digest=hashlib.sha1(("blob "+str(len(raw))+"\0").encode("ascii")+raw).hexdigest()
            assert digest==record["sha"], f"Source changed: {src.name}"
            data=json.loads(raw)
            count=0
            for lesson in data["lessons"]:
                for q in lesson["items"]:
                    assert q["id"] not in rows, f"Duplicate {q['id']} in {src.name}"
                    rows[q["id"]]=q
                    count+=1
            assert count==record["count"],f"Wrong item count in {src.name}"
        assert len(rows)==len(qmap)==959 and {q["id"] for q in qmap}==set(rows), "959 unique IDs required"
        print("VISUAL_CURRICULUM_PASS",len(records),len(rows),flush=True)
        assert all(set(q["assetCandidates"])<=set(ids) for q in qmap)
        assert set(SAFE)<=set(ids)
        if OUT.exists(): shutil.rmtree(OUT)
        dst=OUT/"visuals"; images=dst/"images"; images.mkdir(parents=True)
        compiled={}; previews=[]
        for a in manifest["assets"]:
            name=a["id"]; p=a["path"]; raw=z.read(p)
            assert re.fullmatch(r"[a-z0-9_]+",name)
            if a["category"]=="layouts":
                # The preflight QA renders all 12 design references. They must
                # not be packaged into runtime assets or used as static screens.
                ET.fromstring(raw)
                continue
            if a["category"]=="ui":
                original=ROOT/"app/src/main/assets/ui"/Path(p).name
                assert original.read_bytes()==raw, f"Approved original changed: {p}"
                im=Image.open(io.BytesIO(raw)).convert("RGBA")
                compiled[name]={"path":"ui/"+Path(p).name,"width":im.width,"height":im.height,
                                "sha256":hashlib.sha256(raw).hexdigest()}
            else:
                assert a["format"]=="svg" and p.startswith("assets/")
                root=ET.fromstring(raw)
                assert root.tag.endswith("svg")
                for el in root.iter():
                    for k,v in el.attrib.items():
                        if k.endswith("href"): assert v.startswith("#"),f"External resource: {name}"
                w,h=int(a["width"]),int(a["height"])
                factor=2 if max(w,h)<=128 else 1
                png=cairosvg.svg2png(bytestring=raw,output_width=w*factor,output_height=h*factor)
                im=Image.open(io.BytesIO(png)).convert("RGBA")
                assert im.size==(w*factor,h*factor) and im.getbbox(),name
                target=images/(name+".webp")
                im.save(target,format="WEBP",lossless=True,method=5)
                with Image.open(target) as check:
                    assert check.size==im.size and check.format=="WEBP" and check.convert("RGBA").getbbox(),name
                compiled[name]={"path":"visuals/images/"+target.name,"width":im.width,"height":im.height,
                                "sha256":hashlib.sha256(target.read_bytes()).hexdigest()}
            previews.append((name,im.copy()))
        assert len(compiled)==206 and len(list(images.glob("*.webp")))==201
        expected_runtime={a["id"] for a in manifest["assets"] if a["category"]!="layouts"}
        assert set(compiled)==expected_runtime, "Runtime manifest missing or extra image"
        assert all(s["art"] in compiled for s in stages), "Missing stage artwork"
        aliases={}
        for name,words in manifest["aliases"].items():
            assert name in compiled
            for w in words: aliases.setdefault(norm(w),name)
        aliases["ca đong"]="measuring_cup"
        decorations={}
        for m in qmap:
            q=rows[m["id"]]; typ=(q.get("visual") or {}).get("type","(none)")
            if typ not in ILLUSTRATE or q.get("kind") in ("choice","multi","match","match_value","order","draw","point_select","segment_multi","angle_multi","board_game"):
                continue
            prompt=norm(q.get("prompt",""))
            hits=[]
            for asset,words in SAFE.items():
                for w in sorted(words,key=len,reverse=True):
                    if phrase(prompt,w): hits.append((asset,w));break
            hits.sort(key=lambda v:-len(v[1]))
            used=[]; labels=[]
            for asset,w in hits:
                if any(phrase(prior,w) for prior in labels): continue
                used.append({"id":asset,"label":w});labels.append(w)
                if len(used)==3:break
            if used: decorations[q["id"]]=used
        write(dst/"manifest.json",{"sourceRef":BASE,"runtimeCount":len(compiled),"layoutReferenceCount":12,
             "questionCount":len(rows),"images":compiled,"aliases":aliases,"decorations":decorations,
             "stages":[{"lesson":s["lesson"],"art":s["art"]} for s in stages]})
        # Full raster QA contact sheet. Not shipped to Android.
        REPORT.parent.mkdir(parents=True,exist_ok=True)
        cols=8; cw=155; ch=126
        sheet=Image.new("RGB",(cols*cw,((len(previews)+cols-1)//cols)*ch),"#fff3f9")
        draw=ImageDraw.Draw(sheet)
        for i,(name,im) in enumerate(previews):
            x=(i%cols)*cw;y=(i//cols)*ch
            draw.rounded_rectangle((x+3,y+3,x+cw-3,y+ch-3),radius=8,fill="white",outline="#efd4e4")
            im.thumbnail((cw-14,ch-31),Image.Resampling.LANCZOS)
            sheet.paste(im,(x+(cw-im.width)//2,y+6+(ch-31-im.height)//2),im)
            draw.text((x+8,y+ch-19),name,fill="#67415d")
        sheet.save(REPORT)
        print("VISUAL_PACK_PASS",len(compiled),"runtime images;",len(rows),"questions;",len(decorations),"prompt illustrations;",len(stages),"stages")
if __name__=="__main__": main()
