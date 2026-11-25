"""Install the exact #48 APK, seed an isolated emulator, and verify in-place upgrade.
The production package is never uninstalled as part of the upgrade check.
"""
import json
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from pathlib import Path

sys.path.insert(0,str(Path(__file__).resolve().parents[2]/"migration"))
from test_migrate_p01_p02 import sample

PKG="vn.mocnhien.toan3"
PREF="shared_prefs/native_curriculum_progress_v2.xml"

def adb(*args,data=None,timeout=90):
    r=subprocess.run(["adb",*map(str,args)],input=data,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,timeout=timeout)
    if r.returncode: raise RuntimeError(r.stdout.decode(errors="replace"))
    return r.stdout

def launch():
    adb("shell","am","start","-W","-n",PKG+"/.MainActivity")
    time.sleep(1.5)
    assert adb("shell","pidof",PKG).strip()
    logs=adb("logcat","-d","-s","AndroidRuntime:E").decode(errors="replace")
    assert not ("FATAL EXCEPTION" in logs and PKG in logs),logs[-4000:]

def snapshot():
    raw=adb("exec-out","run-as",PKG,"cat",PREF)
    return json.loads(ET.fromstring(raw).find("./string[@name='snapshot']").text)

def business(s):
    return {k:v for k,v in s.items() if k!="nav"}

def main():
    old,new=map(Path,sys.argv[1:3])
    devices=[l for l in adb("devices").decode().splitlines() if l.endswith("\tdevice")]
    assert len(devices)==1 and devices[0].startswith("emulator-"),devices
    assert "Success" in adb("install",old).decode()
    adb("shell","pm","clear",PKG)
    launch()
    adb("shell","am","force-stop",PKG)
    root=ET.fromstring(sample())
    node=root.find("./string[@name='snapshot']")
    seeded=json.loads(node.text)
    seeded["nav"].update(screen="question",lesson=26,book="sgk",question="SGK.26.75.HD.1.a",lastId="SGK.26.75.HD.1.a")
    node.text=json.dumps(seeded,ensure_ascii=False)
    adb("shell","run-as",PKG,"mkdir","-p","shared_prefs")
    adb("shell","run-as",PKG,"rm","-f",PREF+".bak")
    adb("shell",f"run-as {PKG} sh -c 'cat > {PREF}'",data=ET.tostring(root,encoding="utf-8",xml_declaration=True))
    launch()
    adb("shell","am","force-stop",PKG)
    before=snapshot()
    assert business(before)==business(seeded), "Baseline failed to restore the fixture"
    assert "Success" in adb("install","-r",new).decode(),"In-place update failed"
    assert snapshot()==before,"Upgrade altered the saved snapshot"
    adb("logcat","-c")
    launch()
    assert business(snapshot())==business(before),"New app altered learning or game progress"
    assert snapshot()["nav"]["question"]==before["nav"]["question"]
    out=Path("ui-upgrade-qa");out.mkdir(exist_ok=True)
    png=adb("exec-out","screencap","-p")
    assert png.startswith(b"\x89PNG\r\n\x1a\n")
    (out/"restored-question.png").write_bytes(png)
    adb("shell","am","force-stop",PKG);launch()
    assert business(snapshot())==business(before),"Restart changed the saved progress"
    (out/"upgrade.json").write_text(json.dumps({"from_run":45560209705,"in_place":"pass","progress":"pass","restart":"pass"},indent=2))
    print("EXACT_P15_UI_UPDATE_WITHOUT_DATA_LOSS_PASS",flush=True)
if __name__=="__main__":main()
