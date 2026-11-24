package vn.mocnhien.toan3;
import java.util.*;

public final class RewardLedgerTest {
    static void check(boolean condition, String label) {
        if (!condition) throw new AssertionError(label);
    }
    public static void main(String[] args) {
        Map<String,Integer> map = new LinkedHashMap<>();
        map.put("SGK.test",10); map.put("SGK.example",0);
        for(int l=26;l<=44;l++){map.put("BONUS."+l+".1",80);map.put("BONUS."+l+".2",120);}
        RewardLedger ledger = new RewardLedger(map);
        RewardLedger.Snapshot s=ledger.restore(Arrays.asList("CHEST.26","unknown","SGK.example"));
        check(s.balance==0,"reject forged chest, unknown and example");
        check(ledger.claim(s,"SGK.test",false).earned==0,"incorrect gives no reward");
        RewardLedger.Award a=ledger.claim(s,"SGK.test",true);
        check(a.earned==10&&a.snapshot.balance==10,"ordinary");
        s=a.snapshot;
        check(ledger.claim(s,"SGK.test",true).earned==0,"replay");
        check(ledger.claim(s,"CHEST.26",true).earned==0,"direct chest claim");
        a=ledger.claim(s,"BONUS.26.2",true);
        check(a.earned==120&&!a.snapshot.claims.contains("CHEST.26"),"second first");
        s=ledger.restore(a.snapshot.claims);
        a=ledger.claim(s,"BONUS.26.1",true);
        check(a.earned==180&&a.snapshot.balance==310&&a.newClaims.contains("CHEST.26"),"chest");
        s=ledger.restore(a.snapshot.claims);
        check(ledger.claim(s,"BONUS.26.1",true).earned==0,"restore and replay");
        check(ledger.claim(s,"BONUS.26.2",true).earned==0,"restore second replay");
        List<String> duplicates=new ArrayList<>(s.claims);
        duplicates.addAll(s.claims);duplicates.add("CHEST.27");duplicates.add("fake");
        check(ledger.restore(duplicates).balance==310,"dedup and invalid chest");
        for(int l=27;l<=44;l++){
            a=ledger.claim(s,"BONUS."+l+".1",true);check(a.earned==80,"challenge");s=a.snapshot;
            a=ledger.claim(s,"BONUS."+l+".2",true);check(a.earned==220,"expert and chest");s=a.snapshot;
        }
        check(s.balance==5710,"all 19 treasures plus ordinary");
        check(ledger.restore(s.claims).balance==5710,"restore total");
        System.out.println("REWARD_LEDGER_PASS 19 treasures, replay, restore, incorrect and invalid claims");
    }
}
