package vn.mocnhien.toan3

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView

/** Native controls on the approved pink, illustrated home screen. */
internal class ModuleHome(context: ModuleContext, container: LinearLayout) : ModulePage(context, container) {
    fun show(lastId: String?) {
        ui.hero(body)
        val finished=(26..44).count{count(it)>0 && done(it)==count(it)}
        val summary=ui.card(NativeUi.WHITE)
        ui.add(body,summary)
        val line=ui.row()
        summary.addView(line)
        val caption=ui.column()
        line.addView(caption,LinearLayout.LayoutParams(0,-2,1f))
        ui.label(caption,"Tiến độ học tập",if(ui.compact())17 else 20,true)
        ui.caption(caption,"$finished/19 bài • ${completed()}/${bank.questions.size} mục")
        val percent=(completed()*100/bank.questions.size.coerceAtLeast(1))
        val badge=ui.badge("$percent%",NativeUi.PALE)
        badge.setTextColor(NativeUi.PINK)
        line.addView(badge)
        ui.meter(summary,completed(),bank.questions.size,NativeUi.PINK)

        val first=ui.row()
        val second=ui.row()
        if(ui.sideBySide()){
            val tiles=ui.row();tiles.gravity=Gravity.TOP
            ui.add(body,tiles);ui.weight(tiles,first,1f);ui.weight(tiles,second,1f)
        }else{ui.add(body,first);ui.add(body,second)}
        ui.tile(first,"book","Bài học",NativeUi.PINK,NativeUi.PALE){go("learn")}
        ui.tile(first,"practice","Luyện tập",NativeUi.VIOLET,NativeUi.LAV){go("practice")}
        ui.tile(second,"game","Trò chơi",NativeUi.GREEN,NativeUi.MINT){go("adventure")}
        ui.tile(second,"reward","Phần thưởng",NativeUi.ORANGE,NativeUi.GOLD){go("awards")}

        val resume=ctx.gameResume()
        val last=lastId?.let{bank.questions[it]}
        if(resume>=0 || last!=null){
            val resumeCard=ui.card(NativeUi.WHITE)
            ui.add(body,resumeCard)
            ui.label(resumeCard,"Tiếp tục",19,true)
            if(last!=null)ui.action(resumeCard,"Học tiếp · Bài ${last.optInt("lesson")}",NativeUi.PINK){openQuestion(last,false)}
            if(resume>=0)ui.action(resumeCard,"Chơi tiếp · Bài $resume",NativeUi.LAV){openAdventure(resume)}
        }
    }
}
