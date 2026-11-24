"""Capture actual screenshots from the APK installed on a landscape Android emulator."""
import subprocess, time, struct
from pathlib import Path
OUT=Path("screenshots"); OUT.mkdir(exist_ok=True)
PKG="vn.mocnhien.toan3"
def adb(*args):
    return subprocess.check_output(["adb",*map(str,args)],stderr=subprocess.STDOUT)
def capture(name):
    time.sleep(.55)
    data=adb("exec-out","screencap","-p")
    assert data[:8]==b"\x89PNG\r\n\x1a\n", "Invalid screenshot"
    (OUT/(name+".png")).write_bytes(data)
    print(name, len(data), flush=True)
    return struct.unpack(">II",data[16:24])
def tap(x,y):
    adb("shell","input","tap",round(ox+x*scale),round(oy+y*scale))
    time.sleep(.30)
def alive():
    assert adb("shell","pidof",PKG).strip(), "App is not running"
    assert b"FATAL EXCEPTION" not in adb("logcat","-d","-s","AndroidRuntime:E"), "App crashed"
adb("logcat","-c")
apk=next(Path("apk").glob("*.apk"))
print(adb("install","-r",apk).decode())
print(adb("shell","am","start","-W","-n",PKG+"/.MainActivity").decode())
time.sleep(3)
w,h=capture("01-hanh-trinh")
assert w>h, (w,h)
scale=min(w/1440,h/810); ox=(w-1440*scale)/2; oy=(h-810*scale)/2
alive()
tap(134,346); capture("02-chia-gio-tao")
for i in range(10):
    tap(558+i%10*82,350+i//10*51)
    tap(668 if i<5 else 948,500)
capture("03-chia-gio-da-xep")
tap(1230,612); capture("04-ket-qua")
tap(950,660); capture("05-phan-thuong")
tap(950,660); capture("06-thu-hoach")
tap(130,68); tap(472,443); capture("07-dong-nuoc")
tap(610,527); tap(620,606); capture("08-rot-nuoc")
tap(130,68); tap(850,749); capture("09-thanh-tich")
tap(1120,749); capture("10-cai-dat")
alive()
assert len(set(p.read_bytes() for p in OUT.glob("*.png")))>=8, "Screens are not distinct"
print("SCREENSHOT_CAPTURE_PASS",flush=True)
