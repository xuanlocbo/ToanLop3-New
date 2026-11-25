package vn.mocnhien.toan3;

import android.view.Gravity;
import android.widget.*;
import org.json.JSONObject;
import java.util.*;

/** Game module owns the map; Activity owns the existing game View lifecycle. */
final class GameModule extends ModulePage {
    GameModule(ModuleContext ctx,LinearLayout body){super(ctx,body);}
    void show(int lesson){
        LinearLayout info=ui.card(NativeUi.WHITE);ui.add(body,info);
        ui.banner(info,AdventurePlan.theme(lesson),true,ui.compact()?68:108);
        ui.label(info,"Chơi game",26,true);
        ui.label(info,"Phiêu lưu cùng Thỏ Hồng",21,true);
        int resume=ctx.gameResume();
        if(resume>=0){final int r=resume;ui.action(info,"Chơi tiếp · Bài "+r,NativeUi.PINK,()->openAdventure(r));}
        ui.label(info,"Vượt chướng ngại vật, giải toán và tìm kho báu!",20,false);
        ui.caption(info,"Mỗi màn có một trạm thực hành và 4 cổng toán. Hai cổng kho báu có thể làm sau.");
        for(int i=0;i<regions.length;i++){
            LinearLayout group=ui.card(regionColors[i]);ui.add(body,group);
            ui.banner(group,i,false,82);
            ui.label(group,regionArt[i]+" "+regions[i],22,true);
            for(int l=regionStart[i];l<=regionEnd[i];l++){
                final int stage=l;AdventureWorld.State a=store.adventureState(l);int best=store.adventureBest(l);
                LinearLayout item=ui.card(NativeUi.WHITE);ui.add(group,item);
                ui.label(item,"Bài "+l+" • "+AdventurePlan.name(l)+(best>=0?"  ✓":""),22,true);
                ui.label(item,title(l),17,false);
                ui.label(item,"Trạm: "+TrailMission.of(l).title+(store.trailComplete(l)?" ✓":""),16,true);
                ui.label(item,(a.finished?"Đã về đích":("Đã qua "+a.gate+"/4 cổng"))+" • ✿ "+a.flowerCount()+"/12"+(best>=0?" • Kỷ lục "+best+"/12":""),16,true);
                LinearLayout row=ui.row();ui.add(item,row);
                ui.smallAction(row,a.finished?"Xem lại màn":a.x==40&&!a.pending?"Bắt đầu":"Tiếp tục",NativeUi.PINK,()->openAdventure(stage));
                if(a.x!=40||a.gate>0||a.petals>0||a.falls>0||a.pending||a.finished)
                    ui.smallAction(row,"Chơi lại",NativeUi.PALE,()->new android.app.AlertDialog.Builder(ui.c)
                        .setTitle("Chơi lại Bài "+stage+"?")
                        .setMessage("Bắt đầu màn chơi từ đầu. Sao học tập và huy hiệu đã đạt vẫn được giữ.")
                        .setNegativeButton("Hủy",null).setPositiveButton("Chơi lại",(d,w)->{
                            if(store.resetAdventure(stage))openAdventure(stage);else Toast.makeText(ui.c,"Chưa lưu được.",Toast.LENGTH_LONG).show();
                        }).show());
            }
        }
    }
}
