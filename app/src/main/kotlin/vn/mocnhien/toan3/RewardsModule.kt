package vn.mocnhien.toan3

import android.view.Gravity
import android.widget.LinearLayout

/** Native Kotlin presentation, reading the existing ledger without creating claims. */
internal class RewardsModule(context:ModuleContext, container:LinearLayout):ModulePage(context,container) {
    fun show(){
        val best=(26..44).map{store.adventureBest(it)}.filter{it>=0}
        val summary=ui.card(NativeUi.WHITE);ui.add(body,summary)
        val stats=ui.row();summary.addView(stats)
        stats.addView(ui.art("reward",94),LinearLayout.LayoutParams(ui.dp(110f),ui.dp(94f)))
        val scores=ui.column();stats.addView(scores,LinearLayout.LayoutParams(0,-2,1f))
        ui.label(scores,"★  "+store.balance()+" sao hồng",26,true)
        ui.label(scores,"🏅  "+best.size+" / 19 huy hiệu",20,true)
        ui.label(summary,"🎁  "+(26..44).count{chest(it)}+" / 19 kho báu đã mở",20,true)
        progress(summary,completed(),bank.questions.size)
        ui.caption(summary,"Sao, kho báu và huy hiệu đã đạt sẽ không mất khi chơi lại.")
        val game=ui.card(NativeUi.LAV);ui.add(body,game)
        ui.label(game,"Thành tích phiêu lưu",21,true)
        ui.caption(game,"Huy hiệu: "+best.size+"/19 • Hoa: "+best.sum()+"/228")
        ui.label(body,"Kho báu theo bài học",22,true)
        val cols=if(ui.widthDp()>=640)2 else 1
        for(region in regions.indices){
            ui.label(body,regionArt[region]+" "+regions[region],22,true)
            for(base in regionStart[region]..regionEnd[region] step cols){
                val row=ui.row();row.gravity=Gravity.TOP;ui.add(body,row)
                for(l in base..minOf(regionEnd[region],base+cols-1)){
                    val earned=chest(l);val c=ui.card(if(earned)NativeUi.GOLD else NativeUi.WHITE)
                    ui.weight(row,c,1f)
                    ui.label(c,"Bài "+l+" • "+(if(earned)"Kho báu đã mở 🎁" else "Kho báu đang chờ"),20,true)
                    val challenges=(1..2).count{store.done("BONUS."+l+"."+it)}
                    ui.caption(c,"Thử thách: "+challenges+"/2 • "+(if(store.adventureBest(l)>=0)"Đã nhận huy hiệu" else "Chưa về đích"))
                    progress(c,done(l),count(l))
                    ui.action(c,"Mở bài "+l,NativeUi.PALE){openLesson(l)}
                }
            }
        }
    }
}
