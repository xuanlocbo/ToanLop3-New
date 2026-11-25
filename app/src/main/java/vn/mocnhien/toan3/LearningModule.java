package vn.mocnhien.toan3;

import android.view.Gravity;
import android.widget.*;
import org.json.JSONObject;
import java.util.*;

/** Learning owns the lesson index, lesson overview and all source-based exercise lists. */
final class LearningModule extends ModulePage {
    LearningModule(ModuleContext ctx,LinearLayout body){super(ctx,body);}
    void show(String lastId){index(lastId,false,this::openLesson);}
    void showPractice(String lastId,java.util.function.IntConsumer open){index(lastId,true,open);}
    private void index(String lastId,boolean practice,java.util.function.IntConsumer open){
        LinearLayout intro=ui.card(NativeUi.WHITE);ui.add(body,intro);
        LinearLayout lead=ui.row();intro.addView(lead);
        ImageView icon=ui.art(practice?"practice":"book",ui.compact()?36:54);
        lead.addView(icon,new LinearLayout.LayoutParams(ui.dp(82),ui.dp(ui.compact()?36:54)));
        LinearLayout info=ui.column();lead.addView(info,new LinearLayout.LayoutParams(0,-2,1));
        ui.label(info,practice?"Vở bài tập":"Lớp 3 · Tập 1",22,true);
        ui.caption(info,practice?"Bài 26–44 • Luyện các bài đã học":"Sách giáo khoa • Vở bài tập • Kho báu");
        JSONObject last=bank.questions.get(lastId);
        if(last!=null&&(!practice||"VBT".equalsIgnoreCase(last.optString("book")))){
            LinearLayout resume=ui.card(NativeUi.GOLD);ui.add(body,resume);
            ui.label(resume,"Tiếp tục học",19,true);
            int l=last.optInt("lesson");
            ui.label(resume,"Bài "+l+" · "+title(l),21,true);
            List<JSONObject> items=practice?bank.section(l,"vbt"):bank.lesson(l);
            ui.meter(resume,store.completed(items),items.size(),NativeUi.PINK);
            ui.action(resume,"Tiếp tục học · Bài "+l,NativeUi.PINK,()->openQuestion(last,false));
        }
        ui.label(body,"Bài 26 – 44",21,true);
        // A single, easily scanned lesson list, with real curriculum titles and progress.
        for(int l=26;l<=44;l++){
            final int ll=l;
            List<JSONObject> items=practice?bank.section(l,"vbt"):bank.lesson(l);
            int n=store.completed(items),total=items.size(),percent=100*n/Math.max(1,total);
            boolean complete=n==total&&total>0;
            LinearLayout card=ui.card(NativeUi.WHITE);ui.add(body,card);
            LinearLayout row=ui.row();card.addView(row);
            LinearLayout book=ui.column();book.setGravity(Gravity.CENTER);
            book.setBackground(ui.wash(0xffffd3e8,0xffffe6f1,16));
            row.addView(book,new LinearLayout.LayoutParams(ui.dp(62),ui.dp(66)));
            TextView number=ui.text(""+l,25,true);number.setGravity(Gravity.CENTER);
            number.setTextColor(NativeUi.PINK);book.addView(number,new LinearLayout.LayoutParams(-1,-2));
            LinearLayout info=ui.column();info.setPadding(ui.dp(8),0,0,0);
            row.addView(info,new LinearLayout.LayoutParams(0,-2,1));
            Button button=ui.button("Bài "+l,NativeUi.WHITE,()->open.accept(ll));
            button.setTextSize(19);button.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            button.setBackground(ui.bg(0x00ffffff,12));button.setPadding(ui.dp(4),0,0,0);
            button.setMinHeight(0);button.setMinimumHeight(0);
            info.addView(button,new LinearLayout.LayoutParams(-1,-2));
            ui.label(info,title(l),17,true);
            LinearLayout track=ui.row();info.addView(track);
            ProgressBar meter=ui.meter(n,total,complete?NativeUi.GREEN:NativeUi.PINK);
            LinearLayout.LayoutParams mp=new LinearLayout.LayoutParams(0,ui.dp(11),1);
            mp.setMargins(ui.dp(4),ui.dp(3),ui.dp(4),ui.dp(3));track.addView(meter,mp);
            TextView percentText=ui.text(percent+"%",15,true);percentText.setTextColor(complete?NativeUi.GREEN:NativeUi.PINK);track.addView(percentText);
            ui.caption(info,n+"/"+total+" mục"+(complete?" • Đã học":n>0?" • Đang học":""));
            TextView arrow=ui.text("›",30,true);arrow.setTextColor(NativeUi.PINK);row.addView(arrow);
            card.setOnClickListener(v->open.accept(ll));book.setOnClickListener(v->open.accept(ll));arrow.setOnClickListener(v->open.accept(ll));
        }
    }
    void showLesson(int lesson,String lastId){
        LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);ui.banner(c,AdventurePlan.theme(lesson),false,88);ui.label(c,title(lesson),25,true);progress(c,done(lesson),count(lesson));
        if(bank.questions.containsKey(lastId)&&bank.questions.get(lastId).optInt("lesson")==lesson){JSONObject last=bank.questions.get(lastId);ui.action(c,"Tiếp tục câu đang học",NativeUi.PINK,()->openQuestion(last,false));}
        LinearLayout row=ui.widthDp()>=640?ui.row():ui.column();row.setGravity(Gravity.TOP);ui.add(body,row);
        for(String b:new String[]{"sgk","vbt","bonus"}){
            List<JSONObject> qs=bank.section(lesson,b);LinearLayout box=ui.card(b.equals("bonus")?NativeUi.GOLD:b.equals("sgk")?NativeUi.PALE:NativeUi.LAV);
            if(ui.widthDp()>=640)ui.weight(row,box,1);else ui.add(row,box);
            ui.label(box,(b.equals("sgk")?"📖 ":b.equals("vbt")?"✏ ":"🎁 ")+bookName(b),23,true);progress(box,store.completed(qs),qs.size());
            ui.label(box,b.equals("bonus")?(chest(lesson)?"Kho báu đã mở!":"Hai thử thách • tối đa 300 sao"):"Khám phá và luyện tập",17,false);
            ui.action(box,"Mở "+bookName(b),NativeUi.WHITE,()->openBook(b));
        }
        Part02LearningUi.addGuide(ui,body,lesson);
        Part03LearningUi.addGuide(ui,body,lesson);
        Part04LearningUi.addGuide(ui,body,lesson);
        Part05LearningUi.addGuide(ui,body,lesson);
        ui.action(body,"🐇  Chơi màn Bài "+lesson,NativeUi.PINK,()->openAdventure(lesson));
        ui.action(body,"Về Học bài",NativeUi.PALE,()->go(AppModule.LEARNING.route));
    }
    void showList(int lesson,String book){
        List<JSONObject> list=bank.section(lesson,book);
        ui.label(body,"Bài "+lesson+" • "+bookName(book),24,true);progress(body,store.completed(list),list.size());
        JSONObject start=null;for(JSONObject q:list)if(!store.done(q.optString("id"))&&store.unlocked(q)){start=q;break;}
        if(start!=null){final JSONObject next=start;ui.action(body,"▶  Bắt đầu / tiếp tục mục chưa hoàn thành",NativeUi.PINK,()->openQuestion(next,false));}
        for(int i=0;i<list.size();i++)questionItem(list.get(i),i+1,false);
    }
}
