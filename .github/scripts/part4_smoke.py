"""Part 4: UI smoke tests on a clean, isolated Android emulator.
Deep screens use explicitly seeded test snapshots; no personal data or release data is modified.
Actual controls are used for all assertions after each fixture is installed.
"""
import json
import os
import re
import subprocess
import time
import xml.etree.ElementTree as ET
from pathlib import Path

PKG = "vn.mocnhien.toan3"
PREF = "shared_prefs/native_curriculum_progress_v2.xml"
OUT = Path("screenshots")
OUT.mkdir(exist_ok=True)

def adb(*args, data=None, timeout=40):
    result = subprocess.run(["adb", *map(str,args)], input=data, stdout=subprocess.PIPE,
                            stderr=subprocess.PIPE, timeout=timeout)
    if result.returncode:
        raise RuntimeError(f"adb {args[:3]}: {result.stderr.decode(errors='replace')}")
    return result.stdout

def wait(seconds=.35):
    time.sleep(seconds)

def capture(name):
    wait(.25)
    data = adb("exec-out", "screencap", "-p")
    assert data.startswith(b"\x89PNG\r\n\x1a\n"), name
    import struct
    width,height = struct.unpack(">II",data[16:24])
    assert width > height and width >= 720, (name,width,height)
    (OUT / (name+".png")).write_bytes(data)
    print("SCREENSHOT",name,width,height,len(data),flush=True)

def hierarchy():
    for attempt in range(4):
        try:
            adb("shell","uiautomator","dump","/sdcard/mocnhien-ui.xml",timeout=20)
            return ET.fromstring(adb("exec-out","cat","/sdcard/mocnhien-ui.xml"))
        except (RuntimeError,ET.ParseError,subprocess.TimeoutExpired):
            wait(.4)
    raise AssertionError("UI hierarchy unavailable")

def norm(t):
    return " ".join((t or "").split())

def bounds(n):
    return tuple(map(int,re.findall(r"\d+",n.attrib["bounds"])))

def scroll(direction=1):
    tree=hierarchy()
    candidates=[n for n in tree.iter("node") if n.get("scrollable")=="true"]
    if candidates:
        x1,y1,x2,y2=bounds(max(candidates,key=lambda n:(bounds(n)[2]-bounds(n)[0])*(bounds(n)[3]-bounds(n)[1])))
    else:
        root=next(tree.iter("node"))
        x1,y1,x2,y2=bounds(root)
    x=(x1+x2)//2
    a=y1+int((y2-y1)*.8);b=y1+int((y2-y1)*.2)
    if direction<0:a,b=b,a
    adb("shell","input","swipe",x,a,x,b,300)
    wait(.2)

def find(pattern,cls=None,desc=False,scrollable=True):
    rx=re.compile(pattern)
    for attempt in range(13 if scrollable else 3):
        tree=hierarchy()
        nodes=[]
        for n in tree.iter("node"):
            if n.get("enabled")!="true" or cls and n.get("class")!=cls:continue
            t=norm(n.get("content-desc" if desc else "text"))
            if not rx.search(t):continue
            x1,y1,x2,y2=bounds(n)
            if x2-x1>6 and y2-y1>6:nodes.append(n)
        if nodes:return nodes[0]
        if scrollable:scroll(1 if attempt<7 else -1)
        else:wait(.3)
    capture("failure-selector")
    (OUT/"failure-ui.xml").write_text(ET.tostring(tree,encoding="unicode"))
    raise AssertionError(f"UI selector not found: {pattern}")

def click(pattern,cls="android.widget.Button",desc=False,scrollable=True):
    n=find(pattern,cls,desc,scrollable)
    x1,y1,x2,y2=bounds(n)
    adb("shell","input","tap",(x1+x2)//2,(y1+y2)//2)
    wait(.3)

def snapshot():
    raw=adb("exec-out","run-as",PKG,"cat",PREF)
    node=ET.fromstring(raw).find("./string[@name='snapshot']")
    assert node is not None
    return json.loads(node.text)

def launch():
    adb("shell","am","start","-W","-n",PKG+"/.MainActivity")
    wait(.8)
    assert adb("shell","pidof",PKG).strip(), "app not running"

def restart():
    adb("shell","am","force-stop",PKG)
    launch()

def fixture(lesson,screen,gate=0,pending=False):
    s=snapshot()
    s["nav"].update({"screen":screen,"lesson":lesson,"book":"sgk","scroll":0,"reviewMode":False})
    if screen in ("gate","game"):
        start=40 if gate==0 else gate*480+32
        x=(gate+1)*480-22 if pending else start
        s.setdefault("adventures",{})[str(lesson)]={"motion":{
            "version":1,"x":x,"y":0,"vy":0,"gate":gate,"petals":0,
            "falls":0,"pending":pending,"answered":False,"finished":False}}
    adb("shell","am","force-stop",PKG)
    root=ET.Element("map")
    e=ET.SubElement(root,"string",name="snapshot");e.text=json.dumps(s,ensure_ascii=False,separators=(",",":"))
    # Write only into this debug emulator package, after force-stop.
    adb("shell","run-as",PKG,"mkdir","-p","shared_prefs")
    adb("shell","run-as",PKG,"rm","-f",PREF+".bak")
    adb("shell","run-as",PKG,"sh","-c","cat > "+PREF,data=ET.tostring(root,encoding="utf-8",xml_declaration=True))
    print("FIXTURE",lesson,screen,gate,flush=True)
    launch()

def no_crash():
    assert adb("shell","pidof",PKG).strip()
    logs=adb("logcat","-d","-s","AndroidRuntime:E").decode(errors="replace")
    assert not ("FATAL EXCEPTION" in logs and PKG in logs), logs[-4000:]

def check_motion(lesson):
    x=snapshot()["adventures"][str(lesson)]["motion"]["x"]
    return x

apk=next(Path("output").glob("MocNhien-Toan3-*.apk"))
adb("logcat","-c")
adb("install","-r",apk)
adb("shell","pm","clear",PKG)
launch()
capture("01-home")
# Actual navigation and completion of an original SGK example.
click(r"^Bài 26\s+\d+/\d+")
click(r"^Mở Sách giáo khoa$")
click(r"Bắt đầu / tiếp tục mục chưa hoàn thành")
find(r"48 : 2",scrollable=False)
capture("02-sgk-example")
click(r"^Em đã hiểu$")
find(r"Đã học xong",scrollable=False)
s=snapshot()
assert s["done"]["SGK.26.75.KP.1.a"] is True
capture("03-example-done")
restart()
find(r"Đã học xong",scrollable=False)
assert snapshot()["done"]["SGK.26.75.KP.1.a"]
# Actual movement through the map; first steps are not fixture-based.
fixture(26,"home")
click(r"^Bắt đầu phiêu lưu$")
click(r"^Bắt đầu$")
find(r"Phải",cls="android.widget.Button",scrollable=False)
capture("04-adventure-forest")
click(r"Phải",scrollable=False)
wait(1.2)
x=check_motion(26)
assert 40<x<155, x
click(r"Bản đồ",scrollable=False)
restart()
click(r"^Tiếp tục$")
assert check_motion(26)==x
capture("05-game-resume")
# Deep gate fixture: use actual fill / transfer / check buttons and restore mid-task.
fixture(32,"gate",pending=True)
find(r"Pha nước cầu vồng",scrollable=False)
capture("06-pour-empty")
click(r"^Đầy A$")
assert snapshot()["adventures"]["32"]["trail"]["draft"]=="500,0"
restart()
assert snapshot()["adventures"]["32"]["trail"]["draft"]=="500,0"
click(r"^A → B$")
assert snapshot()["adventures"]["32"]["trail"]["draft"]=="200,300"
capture("07-pour-solved")
click(r"^Kiểm tra$")
assert snapshot()["adventures"]["32"]["trail"]["complete"] is True
# Answer source question in actual native input field, check wrong answer then retry.
find(r"Rót hết nước trong bình",scrollable=False)
capture("08-source-gate")
click(r"^Đáp án$",cls="android.widget.EditText",desc=True)
adb("shell","input","text","1")
adb("shell","input","keyevent","4")
click(r"^Kiểm tra$")
s=snapshot()
gate_id="SGK.32.89.HD.1.b"
assert s["review"][gate_id] is True and not s["done"].get(gate_id,False)
assert gate_id not in s["claims"]
click(r"^Đáp án$",cls="android.widget.EditText",desc=True)
adb("shell","input","keyevent","123","67")
adb("shell","input","text","1000")
adb("shell","input","keyevent","4")
click(r"^Kiểm tra$")
s=snapshot()
assert s["done"][gate_id] and s["claims"][gate_id] and gate_id not in s["review"]
assert s["adventures"]["32"]["motion"]["answered"]
capture("09-reward")
click(r"^Qua cổng")
assert snapshot()["adventures"]["32"]["motion"]["gate"]==1
# Optional challenge is not a forced blocker.
fixture(26,"gate",gate=2,pending=True)
click(r"^Thử thách này để sau$")
click(r"^Đi tiếp$")
s=snapshot()
assert s["adventures"]["26"]["motion"]["gate"]==3
assert "BONUS.26.1" not in s["claims"]
# Actual screenshots of all four visual regions using clean stage fixtures.
for l,name in [(30,"10-lake"),(36,"11-crystal"),(43,"12-castle")]:
    fixture(l,"game")
    capture(name)
    no_crash()
# The setting persists, without requiring audio capture on CI.
fixture(26,"settings")
click(r"^Âm thanh: đang bật$")
assert snapshot()["sound"] is False
restart()
find(r"Âm thanh: đang tắt",scrollable=False)
capture("13-settings")
no_crash()
assert len(set(p.read_bytes() for p in OUT.glob("*.png")))>=12
print("PART4_EMULATOR_SMOKE_PASS",flush=True)
