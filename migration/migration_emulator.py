"""Test exact APK migration and recovery on isolated CI emulator; synthetic progress."""
import sys, time, tempfile, subprocess
from pathlib import Path
from migrate_p01_p02 import *
from test_migrate_p01_p02 import sample
def business(raw):
    s=validate(raw); s.pop("nav",None); return s
def launch(d):
    d.call("shell","am","start","-W","-n",PKG+"/.MainActivity")
    time.sleep(1)
    need(bool(d.text("shell","pidof",PKG)),"App not running")
    logs=d.text("logcat","-d","-s","AndroidRuntime:E")
    need(not ("FATAL EXCEPTION" in logs and PKG in logs),"Android crash")
def capture(d,name):
    out=Path("migration-qa"); out.mkdir(exist_ok=True)
    raw=d.call("exec-out","screencap","-p").stdout
    need(raw.startswith(b"\x89PNG\r\n\x1a\n"),"Invalid screenshot")
    (out/(name+".png")).write_bytes(raw)
def main():
    old,new=Path(sys.argv[1]),Path(sys.argv[2])
    need(version(old)=="p01" and version(new)=="p02","Wrong exact APKs")
    serials=[l.split()[0] for l in subprocess.check_output(["adb","devices"],text=True).splitlines() if l.endswith("\tdevice")]
    need(len(serials)==1 and serials[0].startswith("emulator-"),"Isolated emulator required")
    d=Device(serials[0]); d.preflight(); d.call("logcat","-c")
    need(d.current() is None,"Emulator must start clean")
    d.install(old,False); d.restore(sample())
    launch(d); d.stop()
    need(business(d.read())==business(sample()),"Old app did not load fixture")
    with tempfile.TemporaryDirectory() as t:
        folder=Path(t)/"backup"; prepare(d,folder,new); saved=verify(folder,d.serial)
        need(d.install(new,True)=="signature_mismatch","Expected actual signer mismatch")
        need(d.current()=="p01" and d.read()==saved,"Direct failure altered old app")
        print("DIRECT_SIGNATURE_MISMATCH_NONDESTRUCTIVE_PASS",flush=True)
        switch(d,folder,"p02",True)
        need(d.current()=="p02" and d.read()==saved,"Actual migration mismatch")
        launch(d); capture(d,"p02-restored"); d.stop()
        need(business(d.read())==business(saved),"p02 lost progress")
        launch(d); d.stop()
        need(business(d.read())==business(saved),"p02 restart lost progress")
        print("P01_TO_P02_ACTUAL_MIGRATION_PASS",flush=True)
        recovery=Path(t)/"recovery"; prepare(d,recovery,old); newer=verify(recovery,d.serial)
        switch(d,recovery,"p01",True)
        need(d.current()=="p01" and d.read()==newer,"Actual recovery mismatch")
        launch(d); capture(d,"p01-recovered"); d.stop()
        need(business(d.read())==business(saved),"p01 recovery lost progress")
        print("P02_TO_P01_ACTUAL_RECOVERY_PASS",flush=True)
    print("PART02_MIGRATION_PASS",flush=True)
if __name__=="__main__": main()
