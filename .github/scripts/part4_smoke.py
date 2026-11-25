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
    rx=re.compile(pattern,re.IGNORECASE)
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
    adb("shell",f"run-as {PKG} sh -c 'cat > {PREF}'",data=ET.tostring(root,encoding="utf-8",xml_declaration=True))
    print("FIXTURE",lesson,screen,gate,flush=True)
    launch()

def hide_ime():
    ime=adb("shell","dumpsys","input_method").decode(errors="replace")
    if re.search(r"(?:mInputShown|mIsInputViewShown|mShowRequested|isInputViewShown)=true",ime):
        adb("shell","input","keyevent","4")
        wait(.3)

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
adb("shell","settings","put","secure","show_ime_with_hard_keyboard","1")
launch()
# Module entries must all be visible on the first screen without scrolling.
for title in ("Học bài","Chơi game","Phần thưởng","Tiến độ"):
    find("^"+title+"$",cls="android.widget.Button",scrollable=False)
capture("M01-home-four-modules")
def route(expected):
    assert snapshot()["nav"]["screen"]==expected, (expected,snapshot()["nav"])
def module_back():
    click(r"^Quay lại$",desc=True,scrollable=False)
    route("home")
def business_state():
    s=snapshot()
    s.pop("nav",None)
    return s

click(r"^Học bài$",scrollable=False)
route("learn")
find(r"^Học bài$",cls="android.widget.TextView",scrollable=False)
capture("M02-learning")
baseline=business_state()
module_back()
click(r"^Chơi game$",scrollable=False)
route("adventure")
find(r"^Chơi game$",cls="android.widget.TextView",scrollable=False)
capture("M03-game")
assert business_state()==baseline
# Switch modules through the actual persistent navigation bar.
click(r"^Phần thưởng$",scrollable=False)
route("awards")
find(r"^Phần thưởng$",cls="android.widget.TextView",scrollable=False)
capture("M04-rewards")
assert business_state()==baseline
restart()
route("awards")
find(r"^Phần thưởng$",cls="android.widget.TextView",scrollable=False)
click(r"^Tiến độ$",scrollable=False)
route("progress")
find(r"^Tiến độ$",cls="android.widget.TextView",scrollable=False)
capture("M05-progress")
assert business_state()==baseline
restart()
route("progress")
click(r"^Ôn lại câu chưa đúng")
route("review")
find(r"Chưa có câu nào cần ôn lại")
click(r"^Quay lại$",desc=True,scrollable=False)
route("progress")
module_back()
assert business_state()==baseline
click(r"^Học bài$",scrollable=False)
route("learn")
# Actual navigation and completion of an original SGK example.
click(r"^Bài 26\s+\d+/\d+")
click(r"^Mở Sách giáo khoa$")
click(r"Bắt đầu / tiếp tục mục chưa hoàn thành")
find(r"48 : 2",scrollable=False)
capture("02-sgk-example")
click(r"^Em đã hiểu$")
find(r"Đã học xong")
s=snapshot()
assert s["done"]["SGK.26.75.KP.1.a"] is True
find(r"1 đã hoàn thành")
capture("03-example-done")
restart()
find(r"Đã học xong")
assert snapshot()["done"]["SGK.26.75.KP.1.a"]
# Actual movement through the map; first steps are not fixture-based.
# Return through the shell, not a seeded navigation fixture.
adb("shell","input","keyevent","4")
route("list")
adb("shell","input","keyevent","4")
route("lesson")
adb("shell","input","keyevent","4")
route("learn")
module_back()
click(r"^Chơi game$",scrollable=False)
route("adventure")
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
restart()
click(r"Trái",scrollable=False)
wait(1.1)
assert 40<=check_motion(26)<x, (x,check_motion(26))
print("RESTORED_GAME_LOOP_PASS",flush=True)
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
find(r"Rót hết nước trong bình")
capture("08-source-gate")
click(r"^Đáp án$",cls="android.widget.EditText",desc=True)
adb("shell","input","text","1")
hide_ime()
click(r"^Kiểm tra$")
s=snapshot()
gate_id="SGK.32.89.HD.1.b"
assert s["review"][gate_id] is True and not s["done"].get(gate_id,False)
assert gate_id not in s["claims"]
click(r"^Đáp án$",cls="android.widget.EditText",desc=True)
adb("shell","input","keyevent","123","67")
adb("shell","input","text","1000")
hide_ime()
click(r"^Kiểm tra$")
s=snapshot()
assert s["done"][gate_id] and s["claims"][gate_id] and gate_id not in s["review"]
assert s["adventures"]["32"]["motion"]["answered"]
capture("09-reward")
click(r"^Qua cổng")
assert snapshot()["adventures"]["32"]["motion"]["gate"]==1
click(r"Bản đồ",scrollable=False)
route("adventure")
after_score=business_state()
click(r"^Phần thưởng$",scrollable=False)
route("awards")
find(r"10 sao hồng",cls="android.widget.TextView")
capture("M06-rewards-after-game")
click(r"^Tiến độ$",scrollable=False)
route("progress")
find(r"2 / 959 mục đã hoàn thành",cls="android.widget.TextView")
capture("M07-progress-after-game")
assert business_state()==after_score
print("FOUR_MODULE_NAVIGATION_AND_SHARED_STATE_PASS",flush=True)
# Optional challenge is not a forced blocker.
fixture(26,"gate",gate=2,pending=True)
click(r"^Thử thách này để sau$")
capture("09b-defer-dialog")
click(r"^Đi tiếp$",scrollable=False)
s=snapshot()
assert s["adventures"]["26"]["motion"]["gate"]==3
assert "BONUS.26.1" not in s["claims"]
capture("09c-deferred-advanced")
print("DEFERRED_BONUS_PASS",flush=True)
# Actual screenshots of all four visual regions using clean stage fixtures.
for l,name in [(30,"10-lake"),(36,"11-crystal"),(43,"12-castle")]:
    fixture(l,"game")
    capture(name)
    no_crash()
# The setting persists, without requiring audio capture on CI.
click(r"Bản đồ",scrollable=False)
route("adventure")
click(r"^Cài đặt$",desc=True,scrollable=False)
route("settings")
click(r"^Âm thanh: đang bật$")
assert snapshot()["sound"] is False
restart()
find(r"Âm thanh: đang tắt",scrollable=False)
capture("13-settings")
no_crash()
assert len(set(p.read_bytes() for p in OUT.glob("*.png")))>=12
for name in ("M01-home-four-modules","M02-learning","M03-game","M04-rewards","M05-progress","M06-rewards-after-game","M07-progress-after-game"):
    assert (OUT/(name+".png")).is_file(),name
print("PART01_MODULE_EMULATOR_SMOKE_PASS",flush=True)
print("PART4_EMULATOR_SMOKE_PASS",flush=True)
# Part 02: open each actual mini-lesson, reveal steps, and verify no state mutation.
before_guides=business_state()
click(r"^Học bài$",scrollable=False)
route("learn")
for l in range(26,30):
    click(r"^Bài "+str(l)+r"\s+\d+/\d+")
    route("lesson")
    click(r"^Mở hướng dẫn Bài "+str(l)+r"$")
    find(r"Hiểu bài trước khi luyện")
    click(r"^Xem bước 1$")
    find(r"^Xem bước 2$",cls="android.widget.Button")
    capture("P02-guide-"+str(l)+"-step1")
    click(r"^Xem bước 2$")
    click(r"^Xem bước 3$")
    find(r"^Xem lại từ đầu$",cls="android.widget.Button")
    capture("P02-guide-"+str(l)+"-solved")
    assert business_state()==before_guides, "guide changed scored data"
    adb("shell","input","keyevent","4")
    route("learn")
assert business_state()==before_guides
no_crash()
print("PART02_GUIDES_AND_STATE_PASS",flush=True)


# Part 03: actual optional measurement lesson, one guided example in each lesson.
before_measurement=business_state()
for l in range(30,36):
    click(r"^Bài "+str(l)+r"\s+\d+/\d+")
    route("lesson")
    click(r"^Mở hướng dẫn Bài "+str(l)+r"$")
    click(r"^Xem bước 1$")
    find(r"^Xem bước 2$",cls="android.widget.Button")
    capture("P03-guide-"+str(l)+"-step1")
    click(r"^Xem bước 2$")
    click(r"^Xem bước 3$")
    find(r"^Xem lại từ đầu$",cls="android.widget.Button")
    capture("P03-guide-"+str(l)+"-solved")
    assert business_state()==before_measurement, "measurement guide changed progress"
    adb("shell","input","keyevent","4")
    route("learn")
no_crash()
print("PART03_MEASUREMENT_GUIDES_PASS",flush=True)

# Part 04: one real stepwise guide in each lesson; no rewards or progress mutations.
before_arithmetic=business_state()
for l in range(36,41):
    click(r"^Bài "+str(l)+r"\s+\d+/\d+")
    route("lesson")
    click(r"^Mở hướng dẫn Bài "+str(l)+r"$")
    click(r"^Xem bước 1$")
    find(r"^Xem bước 2$",cls="android.widget.Button")
    capture("P04-guide-"+str(l)+"-step1")
    click(r"^Xem bước 2$")
    click(r"^Xem bước 3$")
    find(r"^Xem lại từ đầu$",cls="android.widget.Button")
    capture("P04-guide-"+str(l)+"-solved")
    assert business_state()==before_arithmetic, "arithmetic guide changed progress"
    adb("shell","input","keyevent","4")
    route("learn")
no_crash()
print("PART04_ARITHMETIC_GUIDES_PASS",flush=True)

# Part 05: real review screens, all four lessons, no state mutation.
before_review_guides=business_state()
for l in range(41,45):
    click(r"^Bài "+str(l)+r"\s+\d+/\d+")
    route("lesson")
    click(r"^Mở hướng dẫn Bài "+str(l)+r"$")
    click(r"^Xem bước 1$")
    find(r"^Xem bước 2$",cls="android.widget.Button")
    capture("P05-guide-"+str(l)+"-step1")
    click(r"^Xem bước 2$")
    click(r"^Xem bước 3$")
    find(r"^Xem lại từ đầu$",cls="android.widget.Button")
    capture("P05-guide-"+str(l)+"-solved")
    assert business_state()==before_review_guides, "review guide changed progress"
    adb("shell","input","keyevent","4")
    route("learn")
no_crash()
print("PART05_REVIEW_GUIDES_PASS",flush=True)

# Exact source geometry question via a clean navigation-only test fixture.
s=snapshot()
s["nav"].update({"screen":"question","lesson":44,"book":"vbt","question":"VBT.44.1.113.3.1","scroll":0,"reviewMode":False})
adb("shell","am","force-stop",PKG)
root=ET.Element("map")
ET.SubElement(root,"string",name="snapshot").text=json.dumps(s,ensure_ascii=False,separators=(",",":"))
adb("shell","run-as",PKG,"mkdir","-p","shared_prefs")
adb("shell","run-as",PKG,"rm","-f",PREF+".bak")
adb("shell",f"run-as {PKG} sh -c 'cat > {PREF}'",data=ET.tostring(root,encoding="utf-8",xml_declaration=True))
launch()
route("question")
find(r"Quan sát hình chữ nhật ABCD")
find(r"Hình minh họa hình học trên lưới",desc=True)
capture("P05-circumcircle-full")
assert business_state()==before_review_guides, "geometry view changed progress"
no_crash()
print("PART05_GEOMETRY_VIEW_PASS",flush=True)


# Part 06: real Kotlin station controls. Fixtures are isolated emulator progress only.
# Seed the group missions close to completion to exercise real +/- controls quickly.
def candy_fixture(lesson,draft):
    s=snapshot()
    s["nav"].update({"screen":"gate","lesson":lesson,"book":"sgk","scroll":0,"reviewMode":False})
    s.setdefault("adventures",{})[str(lesson)]={"motion":{
        "version":1,"x":458,"y":0,"vy":0,"gate":0,"petals":0,"falls":0,
        "pending":True,"answered":False,"finished":False},
        "trail":{"draft":draft,"complete":False}}
    adb("shell","am","force-stop",PKG)
    root=ET.Element("map")
    ET.SubElement(root,"string",name="snapshot").text=json.dumps(s,ensure_ascii=False,separators=(",",":"))
    adb("shell","run-as",PKG,"mkdir","-p","shared_prefs")
    adb("shell","run-as",PKG,"rm","-f",PREF+".bak")
    adb("shell",f"run-as {PKG} sh -c 'cat > {PREF}'",data=ET.tostring(root,encoding="utf-8",xml_declaration=True))
    launch()
    route("gate")

def trail(lesson):
    return snapshot()["adventures"][str(lesson)]["trail"]

for l,seed,description,solved in [(26,"5,5,4","Thuyền 3: thêm 1","5,5,5"),(29,"5,5,5,4","Rương 4: thêm 1","5,5,5,5")]:
    candy_fixture(l,seed)
    before=snapshot()
    capture("P06-group-"+str(l)+"-before")
    click(r"^"+description+r"$",desc=True)
    assert trail(l)["draft"]==solved,trail(l)
    restart()
    assert trail(l)["draft"]==solved
    find(r"Kho còn "+("2 kiện" if l==26 else "3 viên"))
    capture("P06-group-"+str(l)+"-restored")
    click(r"^Kiểm tra$")
    after=snapshot()
    assert trail(l)["complete"]
    for key in ("claims","done","review","drafts"):
        assert after[key]==before[key],key
    assert after["adventures"][str(l)]["motion"]["pending"]
    assert not after["adventures"][str(l)]["motion"]["answered"]
    capture("P06-group-"+str(l)+"-gate")

for l,seed,wrong,first,second,solved in [
    (27,"-1","− 4",":4",None,"0"),
    (28,"-1,-1","+10","− 3","+10","0,1")]:
    candy_fixture(l,seed)
    before=snapshot()
    click(r"^Phép tính "+re.escape(wrong)+r"$",desc=True)
    click(r"^Kiểm tra$")
    find(r"Chưa hoàn thành")
    assert not trail(l).get("complete",False)
    click(r"^Hoàn tác$",desc=True)
    assert trail(l)["draft"]==seed
    click(r"^Phép tính "+re.escape(first)+r"$",desc=True)
    if second:
        restart()
        assert trail(l)["draft"]=="0,-1"
        click(r"^Phép tính "+re.escape(second)+r"$",desc=True)
    assert trail(l)["draft"]==solved
    capture("P06-machine-"+str(l)+"-solved")
    restart()
    assert trail(l)["draft"]==solved
    click(r"^Kiểm tra$")
    assert trail(l)["complete"]
    after=snapshot()
    for key in ("claims","done","review","drafts"):
        assert after[key]==before[key],key
    assert not after["adventures"][str(l)]["motion"]["answered"]
    capture("P06-machine-"+str(l)+"-gate")
no_crash()
print("PART06_CANDY_STATIONS_RESUME_AND_SCORE_PASS",flush=True)
