package vn.mocnhien.toan3;

import android.view.Gravity;
import android.widget.*;
import org.json.JSONObject;
import java.util.*;

/** Read-only progress module: derives all counts from the shared snapshot. */
final class ProgressModule extends ModulePage {
    ProgressModule(ModuleContext ctx,LinearLayout body){super(ctx,body);}
    void show(){
        LinearLayout summary=ui.card(NativeUi.WHITE);ui.add(body,summary);
        ui.label(summary,"Tiến độ",26,true);
        progress(summary,completed(),bank.questions.size());
        ui.label(summary,"Phiêu lưu: "+ctx.clearedGames()+"/19 màn đã về đích",20,true);
        ui.action(summary,"Ôn lại câu chưa đúng ("+reviewCount()+")",NativeUi.PINK,()->go("review"));
        LinearLayout metrics=ui.card(NativeUi.MINT);ui.add(body,metrics);
        for(String b:new String[]{"sgk","vbt","bonus"}){
            List<JSONObject> qs=ctx.section(b);
            ui.label(metrics,bookName(b),20,true);
            progress(metrics,store.completed(qs),qs.size());
        }
        ui.label(body,"Theo từng bài",23,true);
        for(int l=26;l<=44;l++){
            final int lesson=l;
            LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);
            ui.label(c,"Bài "+l+" · "+title(l),19,true);
            progress(c,done(l),count(l));
            AdventureWorld.State a=store.adventureState(l);
            ui.caption(c,"Game: "+a.gate+"/4 cổng · "+(store.adventureBest(l)>=0?"Đã về đích":"Chưa về đích"));
            ui.action(c,"Mở bài "+l,NativeUi.PALE,()->openLesson(lesson));
        }
    }
    void showReview(){
        ui.label(body,"Ôn lại câu chưa đúng • "+reviewCount()+" mục",26,true);int i=0;
        for(JSONObject q:bank.questions.values())if(store.review(q.optString("id")))questionItem(q,++i,true);
        if(i==0){LinearLayout c=ui.card(NativeUi.MINT);ui.add(body,c);ui.label(c,"🌸 Chưa có câu nào cần ôn lại.",24,true);ui.label(c,"Khi làm sai, câu hỏi sẽ tự xuất hiện ở đây.",19,false);}
    }
}
