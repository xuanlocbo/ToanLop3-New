package vn.mocnhien.toan3;

import android.view.Gravity;
import android.widget.*;
import org.json.JSONObject;
import java.util.*;

/** Learning owns the lesson index, lesson overview and all source-based exercise lists. */
final class LearningModule extends ModulePage {
    LearningModule(ModuleContext ctx,LinearLayout body){super(ctx,body);}
    void show(String lastId){
        LinearLayout intro=ui.card(NativeUi.WHITE);ui.add(body,intro);
        ui.label(intro,"Học bài",26,true);
        ui.caption(intro,"Kiến thức, sách giáo khoa và vở bài tập • Bài 26–44");
        if(bank.questions.containsKey(lastId)){
            JSONObject last=bank.questions.get(lastId);
            ui.action(intro,"Tiếp tục học · Bài "+last.optInt("lesson"),NativeUi.PINK,()->openQuestion(last,false));
        }
        int columns=ui.widthDp()>=1000?4:ui.widthDp()>=510?2:1;
        for(int start=0;start<regions.length;start+=columns){
            LinearLayout row=ui.row();row.setGravity(Gravity.TOP);ui.add(body,row);
            for(int i=start;i<Math.min(regions.length,start+columns);i++){
                LinearLayout region=ui.card(regionColors[i]);ui.weight(row,region,1);
                ui.label(region,regions[i],21,true);
                ui.caption(region,"Bài "+regionStart[i]+" – "+regionEnd[i]);
                for(int l=regionStart[i];l<=regionEnd[i];l++){
                    final int ll=l;boolean finished=done(l)==count(l);
                    ui.action(region,"Bài "+l+"   "+done(l)+"/"+count(l)+(finished?"  ✓":""),finished?NativeUi.MINT:NativeUi.WHITE,()->openLesson(ll));
                    ui.caption(region,title(l));
                }
            }
        }
    }
    void showLesson(int lesson,String lastId){
        LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);ui.banner(c,AdventurePlan.theme(lesson),false,88);ui.label(c,"Bài "+lesson+" — "+title(lesson),26,true);progress(c,done(lesson),count(lesson));
        if(bank.questions.containsKey(lastId)&&bank.questions.get(lastId).optInt("lesson")==lesson){JSONObject last=bank.questions.get(lastId);ui.action(c,"Tiếp tục câu đang học",NativeUi.PINK,()->openQuestion(last,false));}
        LinearLayout row=ui.widthDp()>=640?ui.row():ui.column();row.setGravity(Gravity.TOP);ui.add(body,row);
        for(String b:new String[]{"sgk","vbt","bonus"}){
            List<JSONObject> qs=bank.section(lesson,b);LinearLayout box=ui.card(b.equals("bonus")?NativeUi.GOLD:b.equals("sgk")?NativeUi.PALE:NativeUi.LAV);
            if(ui.widthDp()>=640)ui.weight(row,box,1);else ui.add(row,box);
            ui.label(box,(b.equals("sgk")?"📖 ":b.equals("vbt")?"✏ ":"🎁 ")+bookName(b),23,true);progress(box,store.completed(qs),qs.size());
            ui.label(box,b.equals("bonus")?(chest(lesson)?"Kho báu đã mở!":"Hai thử thách • tối đa 300 sao"):"Khám phá và luyện tập",17,false);
            ui.action(box,"Mở "+bookName(b),NativeUi.WHITE,()->openBook(b));
        }
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
