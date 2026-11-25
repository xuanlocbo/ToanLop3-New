package vn.mocnhien.toan3;

import android.view.Gravity;
import android.widget.*;
import org.json.JSONObject;
import java.util.*;

/** Home is the product shell, not another lesson list. */
final class ModuleHome extends ModulePage {
    ModuleHome(ModuleContext ctx,LinearLayout body){super(ctx,body);}
    void show(String lastId){
        LinearLayout hero=ui.card(NativeUi.WHITE);ui.add(body,hero);
        LinearLayout row=ui.sideBySide()?ui.row():ui.column();ui.add(hero,row);
        LinearLayout illustration=ui.column();
        if(ui.sideBySide())ui.weight(row,illustration,1);else ui.add(row,illustration);
        ui.banner(illustration,0,true,ui.compact()?54:100);
        LinearLayout welcome=ui.column();
        if(ui.sideBySide())ui.weight(row,welcome,1.35f);else ui.add(row,welcome);
        ui.label(welcome,"Vương quốc toán học",ui.compact()?21:26,true);
        ui.caption(welcome,"Bài 26–44  •  Cùng Thỏ Hồng");
        AppModule[] modules=AppModule.values();
        int columns=ui.widthDp()>=400?2:1;
        String[] subtitles={"Kiến thức và bài tập","19 màn phiêu lưu","Sao, huy hiệu và kho báu","Kết quả và ôn câu sai"};
        for(int start=0;start<modules.length;start+=columns){
            LinearLayout cards=ui.row();ui.add(body,cards);
            for(int i=start;i<Math.min(modules.length,start+columns);i++){
                final AppModule m=modules[i];
                Button b=ui.button(m.label,m.color,()->go(m.route));
                b.setContentDescription(m.label);
                b.setTextSize(ui.compact()?21:23);
                b.setMinHeight(ui.dp(ui.compact()?52:68));
                if(ui.compact())ui.weight(cards,b,1);
                else{
                    LinearLayout card=ui.card(m.color);ui.weight(cards,card,1);
                    ui.add(card,b);ui.caption(card,subtitles[i]);
                }
            }
        }
        int resume=ctx.gameResume();
        if(resume>=0||bank.questions.containsKey(lastId)){
            LinearLayout resumeCard=ui.card(NativeUi.WHITE);ui.add(body,resumeCard);
            ui.label(resumeCard,"Tiếp tục",19,true);
            if(bank.questions.containsKey(lastId)){
                JSONObject last=bank.questions.get(lastId);
                ui.action(resumeCard,"Học tiếp · Bài "+last.optInt("lesson"),NativeUi.PALE,()->openQuestion(last,false));
            }
            if(resume>=0){final int l=resume;ui.action(resumeCard,"Chơi tiếp · Bài "+l,NativeUi.LAV,()->openAdventure(l));}
        }
        ui.caption(body,completed()+" / "+bank.questions.size()+" mục đã hoàn thành");
    }
}
