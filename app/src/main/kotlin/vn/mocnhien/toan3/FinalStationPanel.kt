package vn.mocnhien.toan3

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/** All remaining arithmetic and review missions use persistent native controls. */
internal class FinalStationPanel(context:Context, store:ProgressStore, lesson:Int, private val next:Runnable):LinearLayout(context) {
    private val ui=NativeUi(context)
    private val session=FinalStationSession(store,lesson)
    private val mission=session.mission
    private val value=ui.badge("",NativeUi.GOLD)
    private val drawing=FinalStationArt(context,mission,session.snapshot()){i->handle(session.move(i))}
    private val buttons=ArrayList<Button>()
    private val totals=ArrayList<TextView>()
    private val trace=ui.text("",21,true)
    private val message=ui.text("",18,true)
    private val hint=ui.text("Gợi ý: "+mission.hint,18,false)
    private lateinit var hintButton:Button
    private var showHint=false
    private var finished=false
    init {
        orientation=VERTICAL
        ui.caption(this,"Trạm thực hành • Bài "+lesson)
        ui.label(this,mission.title,if(ui.compact())22 else 25,true)
        ui.label(this,mission.brief,if(ui.compact())18 else 21,false)
        ui.add(this,value)
        val two=ui.sideBySide() && mission.kind!=TrailMission.Kind.GROUP
        val stage=if(two)ui.row() else ui.column()
        stage.gravity=Gravity.TOP
        ui.add(this,stage)
        val picture=ui.column();val controls=ui.column()
        if(two){ui.weight(stage,picture,1f);ui.weight(stage,controls,1.25f)}
        else{ui.add(stage,picture);ui.add(stage,controls)}
        val height=if(mission.kind==TrailMission.Kind.GRID) {
            if(ui.compact())188f else 250f
        } else if(ui.compact())122f else 165f
        picture.addView(drawing,LayoutParams(-1,ui.dp(height)))
        if(mission.kind==TrailMission.Kind.GROUP)makeGroups(controls)
        else if(mission.kind==TrailMission.Kind.GRID){
            ui.label(controls,"Chạm lần lượt các giao điểm trên lưới.",17,false)
            ui.add(controls,trace)
            ui.action(controls,"Xóa điểm vừa chọn",NativeUi.PALE){handle(session.move(25))}
        }else {
            if(mission.kind==TrailMission.Kind.STEPS)ui.add(controls,trace)
            val cols=if(mission.kind==TrailMission.Kind.BUILD) {if(ui.widthDp()>=640&&!two)5 else 3} else 2
            for(base in 0 until mission.actionCount() step cols){
                val row=ui.row();ui.add(controls,row)
                for(i in base until minOf(mission.actionCount(),base+cols)){
                    val label=mission.label(i)
                    val button=ui.button(label,if(label=="Hoàn tác")NativeUi.LAV else NativeUi.PALE){handle(session.move(i))}
                    if(mission.kind==TrailMission.Kind.STEPS)button.contentDescription=if(label=="Hoàn tác")label else "Phép tính "+label
                    button.setTextSize(if(mission.kind==TrailMission.Kind.BUILD)18f else 20f)
                    ui.weight(row,button,1f);buttons.add(button)
                }
            }
        }
        ui.add(this,message);ui.add(this,hint)
        val row=ui.row();ui.add(this,row)
        hintButton=ui.button("Gợi ý",NativeUi.LAV){showHint=!showHint;refresh()}
        ui.weight(row,hintButton,1f)
        ui.smallAction(row,"Làm lại",NativeUi.PALE){handle(session.reset())}
        ui.smallAction(row,"Kiểm tra",NativeUi.GREEN){handle(session.check())}
        refresh()
    }
    private fun makeGroups(parent:LinearLayout){
        val per=mission.actionCount()/mission.bins
        val cols=if(ui.widthDp()>=520)2 else 1
        for(base in 0 until mission.bins step cols){
            val row=ui.row();row.gravity=Gravity.TOP;ui.add(parent,row)
            for(i in base until minOf(mission.bins,base+cols)){
                val card=ui.card(NativeUi.LAV);ui.weight(row,card,1f)
                val total=ui.text("",20,true);totals.add(total);ui.add(card,total)
                for(pair in 0 until per step 2){
                    val keys=ui.row();ui.add(card,keys)
                    for(j in pair until minOf(per,pair+2)){
                        val action=i*per+j;val label=mission.label(action)
                        val b=ui.button(label,if(j%2==0)NativeUi.MINT else NativeUi.WHITE){handle(session.move(action))}
                        b.contentDescription="Rương "+(i+1)+": "+label
                        ui.weight(keys,b,1f)
                    }
                }
            }
        }
    }
    private fun handle(result:FinalStationSession.Outcome){
        if(finished)return
        when(result){
            FinalStationSession.Outcome.CHANGED->message.text=""
            FinalStationSession.Outcome.NO_CHANGE->message.text="Không thể thực hiện bước này. Em thử cách khác nhé."
            FinalStationSession.Outcome.NOT_READY->{message.text="Chưa hoàn thành. Em có thể thử lại!";showHint=true}
            FinalStationSession.Outcome.SAVE_FAILED->{message.text="Chưa lưu được. Hãy thử lại.";Toast.makeText(context,message.text,Toast.LENGTH_SHORT).show()}
            FinalStationSession.Outcome.COMPLETE->{finished=true;next.run();return}
        }
        refresh()
    }
    private fun refresh(){
        val s=session.snapshot()
        val current=session.remainingOrValue()
        value.text=when(mission.kind){
            TrailMission.Kind.GROUP->"Kho còn "+current+" "+mission.unit
            TrailMission.Kind.BUILD->"Đã ghép / đặt: "+current+" "+mission.unit
            TrailMission.Kind.STEPS->"Giá trị hiện tại: "+current+(if(mission.lesson==40)" kg" else "")
            TrailMission.Kind.GRID->"Đã chọn "+s.count{it>=0}+"/4 đỉnh"
            else->""
        }
        when(mission.kind){
            TrailMission.Kind.GROUP->totals.forEachIndexed{i,t->t.text="Rương "+(i+1)+" · "+s[i]+" viên"}
            TrailMission.Kind.BUILD->buttons.forEachIndexed{i,b->
                val chosen=mission.selected(s,i)
                b.text=(if(chosen)"✓ " else "")+mission.label(i)
                b.setTextColor(if(chosen)NativeUi.GREEN else NativeUi.INK)
                b.contentDescription="Miếng "+(i+1)+": "+mission.label(i)+(if(chosen)", đã chọn" else ", chưa chọn")
            }
            TrailMission.Kind.STEPS->trace.text=session.trace()
            TrailMission.Kind.GRID->trace.text=if(s.none{it>=0})"Chưa chọn đỉnh" else s.filter{it>=0}.mapIndexed{i,p->(i+1).toString()+". Hàng "+(p/5+1)+", cột "+(p%5+1)}.joinToString("\n")
            else->Unit
        }
        drawing.update(s)
        message.visibility=if(message.text.isEmpty())View.GONE else View.VISIBLE
        hint.visibility=if(showHint)View.VISIBLE else View.GONE
        hintButton.text=if(showHint)"Ẩn gợi ý" else "Gợi ý"
    }
}
