"""Regression tests for handoff replay; existing migration safeguards remain unchanged."""
import hashlib
import tempfile
import unittest
from pathlib import Path
from types import SimpleNamespace
from unittest.mock import patch
import ci_candidate as c
import migrate_p01_p02 as m

class CandidateTests(unittest.TestCase):
    def setUp(self):
        tmp=tempfile.TemporaryDirectory()
        self.addCleanup(tmp.cleanup)
        self.old=Path(tmp.name)/"p06.apk"
        self.new=Path(tmp.name)/"p07.apk"
        self.old.write_bytes(b"source")
        self.new.write_bytes(b"target")
        self.sha=lambda p:hashlib.sha256(p.read_bytes()).hexdigest()
        registry={"p06":self.sha(self.old),"p07":self.sha(self.new)}
        for mock in (patch.object(m,"HASHES",registry),patch.object(m,"ORDER",tuple(registry)),
                     patch.object(m,"version",side_effect=lambda p:Path(p).stem)):
            mock.start()
            self.addCleanup(mock.stop)
        self.args=SimpleNamespace(source="p06",target="p07",source_apk=self.old,target_apk=self.new,
                                  target_sha=self.sha(self.new),registered_recheck=True)

    def test_exact_registered_recheck_does_not_change_registry(self):
        before=dict(m.HASHES)
        c.validate_pair(self.args)
        self.assertEqual(m.HASHES,before)
        self.assertEqual(m.ORDER,tuple(before))

    def test_wrong_registered_digest_is_rejected_even_if_candidate_matches_it(self):
        before=dict(m.HASHES)
        self.new.write_bytes(b"changed")
        self.args.target_sha=self.sha(self.new)
        with self.assertRaises(m.MigrationError):
            c.validate_pair(self.args)
        self.assertEqual(m.HASHES,before)

    def test_registered_recheck_requires_latest_pair(self):
        m.HASHES["p08"]="f"*64
        m.ORDER=tuple(m.HASHES)
        with self.assertRaises(m.MigrationError):
            c.validate_pair(self.args)

    def test_new_mode_cannot_overwrite_registered_candidate(self):
        self.args.registered_recheck=False
        before=dict(m.HASHES)
        with self.assertRaises(m.MigrationError):
            c.validate_pair(self.args)
        self.assertEqual(m.HASHES,before)

    def test_new_candidate_mode_still_accepts_immediate_successor(self):
        self.args.registered_recheck=False
        m.HASHES.pop("p07")
        m.ORDER=tuple(m.HASHES)
        c.validate_pair(self.args)
        self.assertEqual(m.HASHES["p07"],self.args.target_sha)
        self.assertEqual(m.ORDER,("p06","p07"))

    def test_registered_recheck_rejects_tampered_artifact(self):
        self.new.write_bytes(b"tampered")
        with self.assertRaises(m.MigrationError):
            c.validate_pair(self.args)

    def test_consolidation_requires_explicit_exact_pair(self):
        final=self.new.with_name("p15.apk")
        final.write_bytes(b"final")
        a=SimpleNamespace(source="p07",target="p15",source_apk=self.new,target_apk=final,
                          target_sha=self.sha(final),registered_recheck=False,consolidated_final=False)
        with self.assertRaises(m.MigrationError):
            c.validate_pair(a)
        a.consolidated_final=True
        c.validate_pair(a)
        self.assertEqual(m.ORDER,("p06","p07","p15"))
        self.assertEqual(m.HASHES["p15"],self.sha(final))

    def test_consolidation_does_not_allow_other_skips(self):
        wrong=self.new.with_name("p14.apk")
        wrong.write_bytes(b"other")
        a=SimpleNamespace(source="p07",target="p14",source_apk=self.new,target_apk=wrong,
                          target_sha=self.sha(wrong),registered_recheck=False,consolidated_final=True)
        with self.assertRaises(m.MigrationError):
            c.validate_pair(a)
        self.assertEqual(m.ORDER,("p06","p07"))

if __name__=="__main__":
    unittest.main()
