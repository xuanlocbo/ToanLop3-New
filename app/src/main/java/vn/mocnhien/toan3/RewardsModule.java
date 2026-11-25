package vn.mocnhien.toan3;

import android.view.Gravity;
import android.widget.*;
import org.json.JSONObject;
import java.util.*;

/** Rewards are displayed from the existing ledger-backed store, never recomputed as new claims. */
final class RewardsModule extends ModulePage {
    RewardsModule(ModuleContext ctx,LinearLayout body){super(ctx,body);}
    void show(){
        ui.label(body,"Phần thưởng",26,true);
        int medals=0,flowers=0;for(int l=26;l<=44;l++){int b=store.adventureBest(l);if(b>=0){medals++;flowers+=b;}}
        LinearLayout game=ui.card(NativeUi.LAV);ui.add(body,game);ui.label(game,"🐇 Huy hiệu phiêu lưu: "+medals+"/19 • ✿ "+flowers+"/228",22,true);
        LinearLayout summary=ui.card(NativeUi.GOLD);ui.add(body,summary);ui.label(summary,"★  "+store.balance()+" sao hồng",30,true);int n=0;for(int l=26;l<=44;l++)if(chest(l))n++;ui.label(summary,"🎁  "+n+" / 19 kho báu đã mở",23,true);progress(summary,completed(),bank.questions.size());
        for(int l=26;l<=44;l++){final int ll=l;LinearLayout c=ui.card(chest(l)?NativeUi.GOLD:NativeUi.WHITE);ui.add(body,c);ui.label(c,"Bài "+l+" • "+(chest(l)?"Kho báu đã mở 🎁":"Kho báu đang chờ"),20,true);progress(c,done(l),count(l));ui.action(c,"Mở bài "+l,NativeUi.PALE,()->openLesson(ll));}
    }
}
