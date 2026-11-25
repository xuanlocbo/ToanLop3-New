import json, tempfile, unittest
from pathlib import Path
from unittest.mock import patch
import xml.etree.ElementTree as ET
import migrate_p01_p02 as m
def sample():
    s={k:{} for k in m.MAPS}
    s["nav"]={"screen":"home","lesson":26,"book":"sgk","scroll":0,"reviewMode":False}
    s["sound"]=False
    s["done"]={"SGK.26.75.KP.1.a":True,"SGK.32.89.HD.1.b":True}
    s["claims"]={"SGK.32.89.HD.1.b":True}
    s["review"]={"SGK.26.75.HD.1.a":True}
    s["drafts"]={"SGK.26.75.KP.1.a":{"read":True,"status":"correct","submitted":True,"earned":0},
                 "SGK.26.75.HD.1.a":{"quotient":"4","work0_partialDividend":"8","showHint":True}}
    s["adventures"]={"32":{"motion":{"version":1,"x":40,"y":0,"vy":0,"gate":0,"petals":0,"falls":0,
                       "pending":False,"answered":False,"finished":False},"trail":{"draft":"500,0"}}}
    s["adventureBest"]={"26":2}; s["deferredBonus"]={"BONUS.26.1":{"value":"7"}}
    root=ET.Element("map"); ET.SubElement(root,"string",name="snapshot").text=json.dumps(s)
    return ET.tostring(root,encoding="utf8",xml_declaration=True)
class Fake:
    serial="test-device"
    def __init__(self,raw,current="p01",status="signature_mismatch"):
        self.raw,self.installed,self.status=raw,current,status
        self.removed=False; self.installs=[]
    def preflight(self): pass
    def stop(self): pass
    def current(self): return self.installed
    def read(self): return self.raw
    def install(self,apk,replace):
        self.installs.append((apk.stem,replace))
        if replace and self.status!="success": return self.status
        self.installed=apk.stem; return "success"
    def uninstall(self):
        self.removed=True; self.raw=self.installed=None
    def restore(self,raw):
        m.need(self.raw is None or self.raw==raw,"overwrite"); self.raw=raw
class Tests(unittest.TestCase):
    def setUp(self):
        t=tempfile.TemporaryDirectory(); self.addCleanup(t.cleanup)
        self.folder=Path(t.name)/"backup"; self.raw=sample()
        old,new=Path(t.name)/"p01.apk",Path(t.name)/"p02.apk"
        old.write_bytes(b"old"); new.write_bytes(b"new")
        p=patch.object(m,"version",side_effect=lambda path:Path(path).stem)
        p.start(); self.addCleanup(p.stop)
        m.bundle(self.folder,"test-device",old,new,self.raw)
    def test_exact(self):
        self.assertEqual(m.verify(self.folder,"test-device"),self.raw)
    def test_switch_recovery(self):
        d=Fake(self.raw); m.switch(d,self.folder,"p02",True)
        self.assertEqual((d.current(),d.read()),("p02",self.raw))
        self.assertTrue(d.removed)
        m.switch(d,self.folder,"p01",True)
        self.assertEqual((d.current(),d.read()),("p01",self.raw))
    def test_tamper(self):
        (self.folder/"progress.xml").write_bytes(self.raw+b" ")
        d=Fake(self.raw)
        with self.assertRaises(m.MigrationError): m.switch(d,self.folder,"p02",True)
        self.assertFalse(d.removed); self.assertEqual(d.installs,[])
    def test_changed(self):
        d=Fake(self.raw+b" ")
        with self.assertRaises(m.MigrationError): m.switch(d,self.folder,"p02",True)
        self.assertFalse(d.removed)
    def test_device(self):
        d=Fake(self.raw); d.serial="another"
        with self.assertRaises(m.MigrationError): m.switch(d,self.folder,"p02",True)
        self.assertFalse(d.removed)
    def test_other_install_failure(self):
        d=Fake(self.raw,status="unknown")
        with self.assertRaises(m.MigrationError): m.switch(d,self.folder,"p02",True)
        self.assertFalse(d.removed)
    def test_interrupt(self):
        d=Fake(None,current=None); m.switch(d,self.folder,"p02",True)
        self.assertEqual((d.current(),d.read()),("p02",self.raw))
    def test_in_place(self):
        d=Fake(self.raw,status="success"); m.switch(d,self.folder,"p02",True)
        self.assertFalse(d.removed); self.assertEqual(d.raw,self.raw)
    def test_repeated(self):
        d=Fake(self.raw,current="p02"); m.switch(d,self.folder,"p02",True)
        self.assertEqual(d.installs,[])
    def test_invalid_xml(self):
        for raw in [b"",b"<map/>",b"<!DOCTYPE x><map/>",b'<map><string name="snapshot">{"done":{},"done":{}}</string></map>']:
            with self.assertRaises((m.MigrationError,ET.ParseError)): m.validate(raw)
    def test_existing_backup(self):
        with self.assertRaises(FileExistsError):
            m.bundle(self.folder,"test-device",self.folder/"p01.apk",self.folder/"p02.apk",self.raw)
        self.assertEqual((self.folder/"progress.xml").read_bytes(),self.raw)
    def test_new_version_pair_and_reverse(self):
        folder=self.folder.parent/"next-backup"
        new=self.folder.parent/"p03.apk"
        new.write_bytes(b"third")
        m.bundle(folder,"test-device",self.folder/"p02.apk",new,self.raw)
        d=Fake(self.raw,current="p02")
        m.switch(d,folder,"p03",True)
        self.assertEqual((d.current(),d.read()),("p03",self.raw))
        d.status="version_downgrade"
        m.switch(d,folder,"p02",True)
        self.assertEqual((d.current(),d.read()),("p02",self.raw))
    def test_target_not_in_manifest(self):
        d=Fake(self.raw)
        with self.assertRaises(m.MigrationError): m.switch(d,self.folder,"p03",True)
        self.assertFalse(d.removed); self.assertEqual(d.installs,[])
    def test_unrelated_installed_version(self):
        d=Fake(self.raw,current="p03")
        with self.assertRaises(m.MigrationError): m.switch(d,self.folder,"p02",True)
        self.assertFalse(d.removed); self.assertEqual(d.installs,[])
    def test_kotlin_suffix_and_downgrade(self):
        self.assertLess(m.rank("p04"),m.rank("p05"))
        self.assertLess(m.rank("p05"),m.rank("p05-k01"))
        folder=self.folder.parent/"kotlin-backup"
        old=self.folder.parent/"p05.apk"
        new=self.folder.parent/"p05-k01.apk"
        old.write_bytes(b"java-fifth"); new.write_bytes(b"kotlin-fifth")
        m.bundle(folder,"test-device",old,new,self.raw)
        d=Fake(self.raw,current="p05")
        m.switch(d,folder,"p05-k01",True)
        self.assertEqual((d.current(),d.read()),("p05-k01",self.raw))
        d.status="version_downgrade"
        m.switch(d,folder,"p05",True)
        self.assertEqual((d.current(),d.read()),("p05",self.raw))
if __name__=="__main__": unittest.main(verbosity=2)
