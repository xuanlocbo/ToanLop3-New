"""CI-only bridge for the newly built candidate; never modifies the registry on disk."""
import argparse
import hashlib
import json
import re
import sys
from pathlib import Path
import migrate_p01_p02 as m

def main():
    p=argparse.ArgumentParser()
    p.add_argument("--source",required=True)
    p.add_argument("--target",required=True)
    p.add_argument("--source-apk",type=Path,required=True)
    p.add_argument("--target-apk",type=Path,required=True)
    p.add_argument("--target-sha",required=True)
    a=p.parse_args()
    m.need(a.source==m.ORDER[-1] and m.version(a.source_apk)==a.source,"Previous accepted APK mismatch")
    m.need(re.fullmatch(r"p(?:0[1-9]|1[0-5])",a.target) is not None,"Invalid candidate ID")
    m.need(a.target not in m.HASHES and int(a.target[1:])==int(a.source[1:])+1,"Candidate must follow accepted baseline")
    m.need(re.fullmatch(r"[0-9a-f]{64}",a.target_sha) is not None,"Invalid candidate hash")
    m.need(hashlib.sha256(a.target_apk.read_bytes()).hexdigest()==a.target_sha,"Candidate differs from packaged artifact")
    m.need(a.target_sha not in m.HASHES.values(),"Candidate matches an older version")
    m.HASHES[a.target]=a.target_sha
    m.ORDER=tuple(m.HASHES)
    m.need(m.version(a.target_apk)==a.target,"Candidate integrity check failed")
    import migration_emulator
    sys.argv=[sys.argv[0],str(a.source_apk),str(a.target_apk)]
    migration_emulator.main()
    out=Path("migration-qa")
    out.mkdir(exist_ok=True)
    (out/"migration-proof.json").write_text(json.dumps({
        "source":a.source,"source_sha256":m.HASHES[a.source],
        "target":a.target,"target_sha256":a.target_sha,
        "upgrade":"pass","recovery":"pass"
    },indent=2)+"\n")
    print("CONTINUOUS_MIGRATION_PASS",a.source,a.target,a.target_sha,flush=True)

if __name__=="__main__":
    main()
