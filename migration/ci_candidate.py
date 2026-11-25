"""CI-only bridge for a new candidate or an exact registered recheck; no registry writes."""
import argparse
import hashlib
import json
import re
import sys
from pathlib import Path
import migrate_p01_p02 as m

def validate_pair(a):
    m.need(re.fullmatch(r"p(?:0[1-9]|1[0-5])",a.target) is not None,"Invalid candidate ID")
    m.need(a.source in m.HASHES and m.version(a.source_apk)==a.source,"Previous accepted APK mismatch")
    m.need(re.fullmatch(r"[0-9a-f]{64}",a.target_sha) is not None,"Invalid candidate hash")
    m.need(hashlib.sha256(a.target_apk.read_bytes()).hexdigest()==a.target_sha,"Candidate differs from packaged artifact")
    if a.registered_recheck:
        m.need(tuple(m.ORDER[-2:])==(a.source,a.target),"Recheck must use the last accepted pair")
        m.need(m.HASHES[a.target]==a.target_sha,"Recheck must use the exact registered APK")
    else:
        m.need(a.source==m.ORDER[-1],"Previous accepted APK must be latest")
        consolidated=getattr(a,"consolidated_final",False)
        if consolidated:
            m.need((a.source,a.target)==("p07","p15"),"Only authorized p07 to p15 consolidation")
        else:
            m.need(int(a.target[1:])==int(a.source[1:])+1,"Candidate must follow accepted baseline")
        m.need(a.target not in m.HASHES,"Candidate already registered")
        m.need(a.target_sha not in m.HASHES.values(),"Candidate matches an older version")
        m.HASHES[a.target]=a.target_sha
        m.ORDER=tuple(m.HASHES)
    m.need(m.version(a.target_apk)==a.target,"Candidate integrity check failed")

def main():
    p=argparse.ArgumentParser()
    p.add_argument("--source",required=True)
    p.add_argument("--target",required=True)
    p.add_argument("--source-apk",type=Path,required=True)
    p.add_argument("--target-apk",type=Path,required=True)
    p.add_argument("--target-sha",required=True)
    p.add_argument("--consolidated-final",action="store_true",help="CI only: authorized p07 to p15 single final candidate")
    p.add_argument("--registered-recheck",action="store_true",help="CI only: verify the last registered exact APK pair again")
    a=p.parse_args()
    m.need(not (a.consolidated_final and a.registered_recheck),"Conflicting migration mode")
    validate_pair(a)
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
