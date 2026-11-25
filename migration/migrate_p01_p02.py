#!/usr/bin/env python3
"""Local, two-phase migration of registered exact debug APKs (p01 onward); no root/network."""
import argparse, hashlib, json, os, re, subprocess, sys, tempfile, zipfile
from pathlib import Path
import xml.etree.ElementTree as ET

PKG="vn.mocnhien.toan3"
PREF="shared_prefs/native_curriculum_progress_v2.xml"
HASHES={"p01":"b84c4bacb182681fe79c3f23df37b937465996abf62866d9648b870d7299164e",
        "p02":"feccf5b3689aba11081014191da9f499a3ad166f5df16c4e68a00c988cf99cb9",
        "p03":"e912743d457e27ea56dbc9bf89fe40ab317d76c270dfc206d315a59c09335f19"}
BANK="ae6cf6e94c017bdcbfdc008cb91c4c9472a53a34e9a20a4bcf70c009a38193e3"
MAPS=("drafts","done","review","claims","nav","adventures","adventureBest","deferredBonus")
class MigrationError(RuntimeError): pass
def need(ok,message):
    if not ok: raise MigrationError(message)
def sha(raw): return hashlib.sha256(raw).hexdigest()
def strict_pairs(pairs):
    d={}
    for k,v in pairs:
        need(k not in d,"Duplicate JSON key")
        d[k]=v
    return d
def bad_constant(v): raise MigrationError("Invalid JSON number")
def parse_json(raw):
    return json.loads(raw,object_pairs_hook=strict_pairs,parse_constant=bad_constant)
def validate(raw):
    need(0<len(raw)<=8*1024*1024,"Invalid progress size")
    need(b"<!DOCTYPE" not in raw.upper() and b"<!ENTITY" not in raw.upper(),"Unsafe XML")
    root=ET.fromstring(raw)
    need(root.tag=="map" and len(root)==1,"Unexpected preferences structure")
    node=root[0]
    need(node.tag=="string" and node.attrib=={"name":"snapshot"} and len(node)==0,"Missing snapshot")
    s=parse_json(node.text or "")
    need(isinstance(s,dict),"Invalid progress")
    for k in MAPS: need(isinstance(s.get(k),dict),"Missing progress map: "+k)
    need("sound" not in s or isinstance(s["sound"],bool),"Invalid sound")
    return s
def version(path):
    path=Path(path)
    h=sha(path.read_bytes())
    found=[v for v,d in HASHES.items() if h==d]
    need(len(found)==1,"APK is not an exact registered build")
    with zipfile.ZipFile(path) as z:
        need(z.testzip() is None and sha(z.read("assets/runtime_v2.json"))==BANK,"APK integrity failed")
    return found[0]
def write_new(p,raw):
    with p.open("xb") as f:
        f.write(raw); f.flush(); os.fsync(f.fileno())
    need(p.read_bytes()==raw,"Backup verification failed")
def verify(folder,serial):
    folder=Path(folder)
    m=parse_json((folder/"manifest.json").read_bytes())
    need(m.get("schema")==1 and m.get("package")==PKG and m.get("serial")==serial,"Wrong backup or device")
    need(m.get("source") in HASHES,"Unknown source")
    h=m.get("sha256")
    need(isinstance(h,dict) and len(h)==3 and "progress.xml" in h,"Invalid manifest")
    apk_names=set(h)-{"progress.xml"}
    need(apk_names<={v+".apk" for v in HASHES} and m["source"]+".apk" in apk_names,"Invalid APK pair")
    for name,digest in h.items(): need(sha((folder/name).read_bytes())==digest,"Damaged backup: "+name)
    for name in apk_names: need(version(folder/name)==Path(name).stem,"Wrong APK")
    raw=(folder/"progress.xml").read_bytes()
    validate(raw)
    return raw
def bundle(folder,serial,old,new,raw):
    oldv,newv=version(old),version(new)
    need(oldv!=newv,"Source and target must differ")
    validate(raw)
    folder=Path(folder)
    folder.mkdir(parents=True,exist_ok=False)
    files={oldv+".apk":Path(old).read_bytes(),newv+".apk":Path(new).read_bytes(),"progress.xml":raw}
    for k,v in files.items(): write_new(folder/k,v)
    write_new(folder/"manifest.json",json.dumps({"schema":1,"package":PKG,"serial":serial,"source":oldv,
        "sha256":{k:sha(v) for k,v in files.items()}},indent=2).encode())
    need(verify(folder,serial)==raw,"Backup verification failed")
class Device:
    def __init__(self,serial,adb="adb"):
        need(re.fullmatch(r"[A-Za-z0-9_.:-]+",serial or "") is not None,"Explicit device serial required")
        self.serial,self.adb=serial,adb
    def call(self,*args,data=None,check=True,timeout=120):
        r=subprocess.run([self.adb,"-s",self.serial,*map(str,args)],input=data,stdout=subprocess.PIPE,
                         stderr=subprocess.PIPE,timeout=timeout)
        if check and r.returncode: raise MigrationError("ADB failed: "+(r.stdout+r.stderr).decode("utf8","replace"))
        return r
    def text(self,*args): return self.call(*args).stdout.decode("utf8","strict").strip()
    def preflight(self):
        need(self.text("get-state")=="device","ADB unavailable")
        need(self.text("shell","am","get-current-user")=="0","Only primary user supported")
        need(re.findall(r"UserInfo\{(\d+):",self.text("shell","pm","list","users"))==["0"],"Multiple users/profiles unsupported")
    def stop(self): self.call("shell","am","force-stop",PKG)
    def installed_path(self):
        r=self.call("shell","pm","path",PKG,check=False)
        paths=[s[8:] for s in r.stdout.decode("utf8","replace").splitlines() if s.startswith("package:")]
        if not paths:
            need(self.text("get-state")=="device","ADB disconnected")
            need("package:"+PKG not in self.text("shell","pm","list","packages",PKG).splitlines(),"Unknown package state")
            return None
        need(r.returncode==0 and len(paths)==1 and paths[0].endswith("/base.apk"),"Unsupported split APK")
        return paths[0]
    def pull_apk(self,dest):
        p=self.installed_path()
        need(p is not None,"App not installed")
        self.call("pull",p,dest)
        return version(dest)
    def current(self):
        if self.installed_path() is None: return None
        with tempfile.TemporaryDirectory() as t: return self.pull_apk(Path(t)/"installed.apk")
    def run_as(self,*args,**kwargs): return self.call("shell","run-as",PKG,*args,**kwargs)
    def exists(self,path):
        self.run_as("id")
        r=self.run_as("test","-e",path,check=False)
        need(r.returncode in (0,1),"Cannot inspect preferences")
        return r.returncode==0
    def read(self):
        need(not self.exists(PREF+".bak"),"Pending Android preference backup; open/close old app first")
        if not self.exists(PREF): return None
        raw=self.call("exec-out","run-as",PKG,"cat",PREF).stdout
        validate(raw)
        return raw
    def restore(self,raw):
        validate(raw)
        old=self.read()
        need(old is None or old==raw,"Existing data differs; refusing overwrite")
        if old is None:
            cmd=("umask 077; mkdir -p shared_prefs && cat > "+PREF+".migration.tmp && chmod 600 "+
                 PREF+".migration.tmp && mv "+PREF+".migration.tmp "+PREF)
            self.call("shell","-T","run-as "+PKG+" sh -c '"+cmd+"'",data=raw)
        need(self.read()==raw,"Restore verification failed")
    def install(self,apk,replace):
        r=self.call("install",*(["-r"] if replace else []),apk,check=False,timeout=180)
        out=(r.stdout+r.stderr).decode("utf8","replace")
        if r.returncode==0 and "Success" in out: return "success"
        if replace and "INSTALL_FAILED_UPDATE_INCOMPATIBLE" in out: return "signature_mismatch"
        if replace and "INSTALL_FAILED_VERSION_DOWNGRADE" in out: return "version_downgrade"
        raise MigrationError("Install failed. Backup retained: "+out)
    def uninstall(self):
        need("Success" in self.text("uninstall",PKG),"Uninstall failed")
        need(self.installed_path() is None,"Package still installed")
def prepare(d,folder,apk):
    d.preflight(); version(apk); d.stop()
    with tempfile.TemporaryDirectory() as t:
        old=Path(t)/"old.apk"; d.pull_apk(old)
        raw=d.read()
        need(raw is not None and d.read()==raw,"No stable saved progress; open/close old app first")
        bundle(folder,d.serial,old,apk,raw)
    print("BACKUP_VERIFIED",sha(raw),flush=True)
def switch(d,folder,target,confirmed=False):
    d.preflight()
    folder=Path(folder)
    need(target in HASHES,"Unknown target")
    raw=verify(folder,d.serial)
    apk=folder/(target+".apk")
    manifest=parse_json((folder/"manifest.json").read_bytes())
    need(apk.name in manifest["sha256"] and version(apk)==target,"Target absent from verified backup")
    d.stop()
    old=d.current()
    if old is not None:
        need(old+".apk" in manifest["sha256"],"Installed version not in backup pair")
        current=d.read()
        need(current==raw or (old==target and current is None),"Progress changed; create fresh backup")
        if old==target:
            # The target has already been installed, e.g. after interrupted recovery.
            d.restore(raw)
            print("TARGET_VERIFIED",target,sha(raw),flush=True)
            return
    if not confirmed:
        print("Verified backup: "+str(folder.resolve()))
        print("Different signing certificates may require removing the old debug app.")
        need(input("Type SWITCH "+target+" to confirm: ").strip()=="SWITCH "+target,"Cancelled")
    # Recheck after confirmation, before any package removal.
    need(verify(folder,d.serial)==raw and parse_json((folder/"manifest.json").read_bytes())==manifest,"Backup changed")
    d.preflight(); d.stop()
    need(d.current()==old,"Installed APK changed")
    if old is not None:
        need(d.read()==raw,"Progress changed")
        status=d.install(apk,True)
        if status!="success":
            need(status=="signature_mismatch" or (int(target[1:])<int(old[1:]) and status=="version_downgrade"),"Unsafe install failure")
            need(d.current()==old and d.read()==raw,"Rejected install changed source")
            need(verify(folder,d.serial)==raw and parse_json((folder/"manifest.json").read_bytes())==manifest,"Backup changed")
            print("REPLACEMENT_REQUIRED",status,flush=True)
            d.uninstall()
            d.install(apk,False)
    else: d.install(apk,False)
    need(d.current()==target,"Target APK checksum mismatch")
    d.restore(raw)
    need(d.read()==raw,"Final restore mismatch")
    print("MIGRATION_PASS",target,sha(raw),flush=True)
def main():
    p=argparse.ArgumentParser(description=__doc__)
    p.add_argument("--adb",default="adb"); p.add_argument("--serial",required=True)
    commands=p.add_subparsers(dest="command",required=True)
    for name in ("prepare","switch","verify"):
        c=commands.add_parser(name); c.add_argument("--directory",required=True,type=Path)
        if name=="prepare": c.add_argument("--apk",required=True,type=Path)
        if name=="switch":
            c.add_argument("--target",required=True,choices=list(HASHES))
            c.add_argument("--confirm-switch",action="store_true",help="Explicit opt-in for isolated CI")
    a=p.parse_args()
    try:
        if a.command=="verify": print("BACKUP_VERIFIED",sha(verify(a.directory,a.serial)))
        elif a.command=="prepare": prepare(Device(a.serial,a.adb),a.directory,a.apk)
        else: switch(Device(a.serial,a.adb),a.directory,a.target,a.confirm_switch)
        return 0
    except (MigrationError,OSError,ValueError,TypeError,KeyError,AttributeError,EOFError,ET.ParseError,zipfile.BadZipFile,subprocess.SubprocessError) as e:
        print("STOP:",str(e),file=sys.stderr)
        print("Keep the verified local backup; do not clear data or uninstall manually.",file=sys.stderr)
        return 1
if __name__=="__main__": sys.exit(main())
