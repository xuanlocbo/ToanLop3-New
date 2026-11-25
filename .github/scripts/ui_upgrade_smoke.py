"""Verified transfer from exact build #48 on an isolated Android emulator.
An incompatible signing certificate is NOT an in-place update. Use the existing
backup/verify/switch/recovery safeguards only on the disposable CI emulator.
"""
import hashlib,json,os,sys,time,tempfile,subprocess
from pathlib import Path
import xml.etree.ElementTree as ET
sys.path.insert(0,str(Path(__file__).resolve().parents[2]/"migration"))
import migrate_p01_p02 as m
from test_migrate_p01_p02 import sample

OLD_SHA="1b6c1e5c6d219ae49652ac11bb115894b25cf3bf0b56b0cd742641266f933391"
OUT=Path("ui-upgrade-qa")
def business(raw):
    state=m.validate(raw);state.pop("nav",None);return state
def capture(d,name):
    png=d.call("exec-out","screencap","-p").stdout
    m.need(png.startswith(b"\x89PNG\r\n\x1a\n"),"Invalid screenshot")
    (OUT/(name+".png")).write_bytes(png)
def launch(d):
    d.call("shell","am","start","-W","-n",m.PKG+"/.MainActivity")
    time.sleep(1.5)
    m.need(bool(d.text("shell","pidof",m.PKG)),"App not running")
    log=d.text("logcat","-d","-s","AndroidRuntime:E")
    m.need(not ("FATAL EXCEPTION" in log and m.PKG in log),"Android crash")
def main():
    old,new=map(Path,sys.argv[1:3])
    candidate=os.environ["CANDIDATE_SHA"]
    m.need(len(candidate)==64 and hashlib.sha256(new.read_bytes()).hexdigest()==candidate,"Candidate digest mismatch")
    m.HASHES.update({"p15":OLD_SHA,"ui121":candidate})
    m.ORDER=tuple(m.HASHES)
    m.need(m.version(old)=="p15" and m.version(new)=="ui121","Exact APK pair required")
    serials=[l.split()[0] for l in subprocess.check_output(["adb","devices"],text=True).splitlines() if l.endswith("\tdevice")]
    m.need(len(serials)==1 and serials[0].startswith("emulator-"),"Isolated emulator required")
    d=m.Device(serials[0]);d.preflight();m.need(d.current() is None,"Expected clean emulator")
    OUT.mkdir(exist_ok=True)
    root=ET.fromstring(sample());node=root.find("./string[@name='snapshot']")
    state=json.loads(node.text)
    state["nav"].update(screen="question",lesson=26,book="sgk",question="SGK.26.75.HD.1.a",lastId="SGK.26.75.HD.1.a")
    node.text=json.dumps(state,ensure_ascii=False)
    raw=ET.tostring(root,encoding="utf-8",xml_declaration=True)
    d.call("logcat","-c");d.install(old,False);d.restore(raw);launch(d);capture(d,"p15-original-draft");d.stop()
    before=d.read()
    m.need(business(before)==business(raw),"Old APK did not restore fixture")
    with tempfile.TemporaryDirectory() as t:
        folder=Path(t)/"backup"
        m.prepare(d,folder,new);saved=m.verify(folder,d.serial)
        m.need(saved==before,"Backup differs from source")
        direct=d.install(new,True)
        if direct=="signature_mismatch":
            m.need(d.current()=="p15" and d.read()==saved,"Rejected update changed source")
        else:
            m.need(direct=="success" and d.current()=="ui121" and d.read()==saved,"Direct update changed source")
        m.switch(d,folder,"ui121",True)
        m.need(d.current()=="ui121" and d.read()==saved,"Restoration changed bytes")
        d.call("logcat","-c");launch(d);capture(d,"ui121-restored-question");d.stop()
        restored=d.read()
        m.need(business(restored)==business(before),"Progress changed after launch")
        m.need(m.validate(restored)["nav"]["question"]==m.validate(before)["nav"]["question"],"Unfinished question not restored")
        launch(d);d.stop();m.need(business(d.read())==business(before),"Restart changed progress")
        recovery=Path(t)/"recovery";m.prepare(d,recovery,old);saved_new=m.verify(recovery,d.serial)
        m.switch(d,recovery,"p15",True)
        m.need(d.current()=="p15" and d.read()==saved_new,"Recovery changed snapshot")
        launch(d);capture(d,"p15-recovered");d.stop()
        m.need(business(d.read())==business(before),"Recovery lost progress")
    result={"from_run":45560209705,"source_sha":OLD_SHA,"candidate_sha":candidate,
            "direct_install":direct,"migration":"pass","progress":"pass","restart":"pass","recovery":"pass",
            "environment":"isolated emulator","physical_device":"not tested"}
    (OUT/"upgrade.json").write_text(json.dumps(result,indent=2),encoding="utf8")
    print("EXACT_P15_UI_MIGRATION_AND_RECOVERY_PASS",json.dumps(result),flush=True)
if __name__=="__main__":main()
