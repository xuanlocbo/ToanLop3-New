package vn.mocnhien.toan3

import android.view.Gravity
import android.widget.LinearLayout

/** Kotlin progress dashboard; read only, all values from the shared committed snapshot. */
internal class ProgressModule(context:ModuleContext, container:LinearLayout):ModulePage(context,container) {
    fun show(){
        val summary=ui.card(NativeUi.WHITE);ui.add(body,summary)
        ui.label(summary,"Tiến độ",26,true)
        progress(summary,completed(),bank.questions.size)
        ui.label(summary,"Phiêu lưu: "+ctx.clearedGames()+"/19 màn đã về đích",20,true)
        ui.caption(summary,"Đã lưu trên thiết bị • Có thể học tiếp khi mở lại ứng dụng")
        ui.action(summary,"Ôn lại câu chưa đúng ("+reviewCount()+")",NativeUi.PINK){go("review")}
        val metrics=ui.card(NativeUi.MINT);ui.add(body,metrics)
        for(b in listOf("sgk","vbt","bonus")){
            val qs=ctx.section(b)
            ui.label(metrics,bookName(b),20,true)
            progress(metrics,store.completed(qs),qs.size)
        }
        ui.label(body,"Theo từng bài",23,true)
        val cols=if(ui.widthDp()>=640)2 else 1
        for(region in regions.indices){
            ui.label(body,regions[region],21,true)
            for(base in regionStart[region]..regionEnd[region] step cols){
                val row=ui.row();row.gravity=Gravity.TOP;ui.add(body,row)
                for(l in base..minOf(regionEnd[region],base+cols-1)){
                    val c=ui.card(NativeUi.WHITE);ui.weight(row,c,1f)
                    ui.label(c,"Bài "+l+" · "+title(l),19,true)
                    progress(c,done(l),count(l))
                    val a=store.adventureState(l)
                    ui.caption(c,"Game: "+a.gate+"/4 cổng · "+(if(store.adventureBest(l)>=0)"Đã về đích" else "Chưa về đích"))
                    ui.action(c,"Mở bài "+l,NativeUi.PALE){openLesson(l)}
                }
            }
        }
    }
    fun showReview(){
        ui.label(body,"Ôn lại câu chưa đúng • "+reviewCount()+" mục",26,true)
        var i=0
        for(q in bank.questions.values)if(store.review(q.optString("id")))questionItem(q,++i,true)
        if(i==0){
            val c=ui.card(NativeUi.MINT);ui.add(body,c)
            ui.label(c,"🌸 Chưa có câu nào cần ôn lại.",24,true)
            ui.label(c,"Khi làm sai, câu hỏi sẽ tự xuất hiện ở đây.",19,false)
        }
    }
}
