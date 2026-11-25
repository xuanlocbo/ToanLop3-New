package vn.mocnhien.toan3

import android.app.AlertDialog
import android.widget.LinearLayout
import android.widget.Toast

/** Kotlin map: same 19 routes, game state and reward authority as the Java map. */
internal class GameModule(context: ModuleContext, container: LinearLayout) : ModulePage(context, container) {
    fun show(lesson: Int) {
        val info=ui.card(NativeUi.WHITE)
        ui.add(body,info)
        ui.banner(info,AdventurePlan.theme(lesson),true,if(ui.compact())68 else 108)
        ui.label(info,"Chơi game",26,true)
        ui.label(info,"Phiêu lưu cùng Thỏ Hồng",21,true)
        val resume=ctx.gameResume()
        if(resume>=0)ui.action(info,"Chơi tiếp · Bài $resume",NativeUi.PINK){openAdventure(resume)}
        ui.label(info,"Vượt chướng ngại vật, giải toán và tìm kho báu!",20,false)
        ui.caption(info,"Mỗi màn có một trạm thực hành và 4 cổng toán. Hai cổng kho báu có thể làm sau.")
        for(i in regions.indices) {
            val group=ui.card(regionColors[i])
            ui.add(body,group)
            ui.banner(group,i,false,82)
            ui.label(group,regionArt[i]+" "+regions[i],22,true)
            for(stage in regionStart[i]..regionEnd[i]) {
                val a=store.adventureState(stage)
                val best=store.adventureBest(stage)
                val item=ui.card(NativeUi.WHITE)
                ui.add(group,item)
                val stageHeader=ui.row()
                ui.add(item,stageHeader)
                stageHeader.addView(ui.packArt("stage_"+stage,60),LinearLayout.LayoutParams(ui.dp(106),ui.dp(60)))
                val stageText=ui.column()
                stageHeader.addView(stageText,LinearLayout.LayoutParams(0,-2,1f))
                ui.label(stageText,"Bài $stage • "+AdventurePlan.name(stage)+(if(best>=0)"  ✓" else ""),20,true)
                ui.label(stageText,title(stage),17,false)
                ui.label(item,"Trạm: "+TrailMission.of(stage).title+(if(store.trailComplete(stage))" ✓" else ""),16,true)
                if(CandyStationSession.supports(stage))ui.caption(item,CandyStationSession.objective(stage))
                val progress=if(a.finished)"Đã về đích" else "Đã qua "+a.gate+"/4 cổng"
                ui.label(item,progress+" • ✿ "+a.flowerCount()+"/12"+(if(best>=0)" • Kỷ lục $best/12" else ""),16,true)
                val row=ui.row()
                ui.add(item,row)
                val action=if(a.finished)"Xem lại màn" else if(a.x==40&&!a.pending)"Bắt đầu" else "Tiếp tục"
                ui.smallAction(row,action,NativeUi.PINK){openAdventure(stage)}
                if(a.x!=40||a.gate>0||a.petals>0||a.falls>0||a.pending||a.finished) {
                    ui.smallAction(row,"Chơi lại",NativeUi.PALE) {
                        AlertDialog.Builder(ui.c)
                            .setTitle("Chơi lại Bài $stage?")
                            .setMessage("Bắt đầu màn chơi từ đầu. Sao học tập và huy hiệu đã đạt vẫn được giữ.")
                            .setNegativeButton("Hủy",null)
                            .setPositiveButton("Chơi lại") { _, _ ->
                                if(store.resetAdventure(stage))openAdventure(stage)
                                else Toast.makeText(ui.c,"Chưa lưu được.",Toast.LENGTH_LONG).show()
                            }.show()
                    }
                }
            }
        }
    }
}
