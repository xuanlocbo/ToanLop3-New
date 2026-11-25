package vn.mocnhien.toan3;

import android.view.Gravity;
import android.widget.*;
import org.json.JSONObject;
import java.util.*;

/** Stateless page adapter: all business state lives in the single ModuleContext. */
abstract class ModulePage {
    final ModuleContext ctx;
    final NativeUi ui;
    final Curriculum bank;
    final ProgressStore store;
    final LinearLayout body;
    static final String[] regions={"Vườn kẹo ngọt","Hồ cầu vồng","Thung lũng sao","Đồi mây hồng"};
    static final String[] regionArt={"✿","≈","★","☁"};
    static final int[] regionColors={0xffffe3ee,0xffe1f4f3,0xffeee5fb,0xffffe9de};
    static final int[] regionStart={26,30,36,41},regionEnd={29,35,40,44};
    ModulePage(ModuleContext ctx,LinearLayout body){
        this.ctx=ctx;this.ui=ctx.ui;this.bank=ctx.bank;this.store=ctx.store;this.body=body;
    }
    void go(String s){ctx.nav.go(s);}
    void home(){ctx.nav.home();}
    void openLesson(int l){ctx.nav.openLesson(l);}
    void openBook(String b){ctx.nav.openBook(b);}
    void openQuestion(JSONObject q,boolean review){ctx.nav.openQuestion(q,review);}
    void openAdventure(int l){ctx.nav.openAdventure(l);}
    String title(int l){return ctx.title(l);}
    String bookName(String b){return ctx.bookName(b);}
    int completed(){return ctx.completed();}
    int count(int l){return ctx.count(l);}
    int done(int l){return ctx.done(l);}
    int reviewCount(){return ctx.reviewCount();}
    boolean chest(int l){return ctx.chest(l);}
    void progress(LinearLayout p,int current,int total){
        ProgressBar bar=new ProgressBar(ui.c,null,android.R.attr.progressBarStyleHorizontal);
        bar.setMax(Math.max(1,total));bar.setProgress(current);bar.setProgressTintList(android.content.res.ColorStateList.valueOf(NativeUi.PINK));
        bar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(0xffeedce6));
        LinearLayout.LayoutParams progressLp=new LinearLayout.LayoutParams(-1,ui.dp(12));progressLp.setMargins(ui.dp(5),ui.dp(4),ui.dp(5),ui.dp(3));p.addView(bar,progressLp);
        ui.label(p,current+" / "+total+" mục đã hoàn thành",16,true);
    }
    void questionItem(JSONObject q,int index,boolean fromReview){
        String id=q.optString("id");boolean unlocked=store.unlocked(q),done=store.done(id);
        LinearLayout box=ui.card(done?NativeUi.MINT:NativeUi.WHITE);ui.add(body,box);
        String prefix=fromReview?"Bài "+q.optInt("lesson")+" • "+q.optString("book"):"Mục "+index;
        if(!"BONUS".equals(q.optString("book")))prefix+=" • Trang "+q.optInt("page")+" • Câu "+q.optInt("exercise");
        ui.label(box,prefix+(done?"  ✓":unlocked?"":"  🔒"),15,true);
        String prompt=q.optString("prompt");ui.label(box,prompt,20,true);
        ui.action(box,unlocked?(done?"Xem lại":store.draft(id).length()>0?"Tiếp tục":"Làm bài"):"Hoàn thành bước trước",unlocked?NativeUi.PALE:0xffe5e0e3,()->openQuestion(q,fromReview));
    }
}
