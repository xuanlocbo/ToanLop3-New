package vn.mocnhien.toan3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

/** Persistent controls; every change is committed by the existing authoritative mission and store. */
internal class RainbowStationPanel(
    context: Context, store: ProgressStore, lesson: Int, private val next: Runnable
) : LinearLayout(context) {
    private val ui=NativeUi(context)
    private val session=RainbowStationSession(store,lesson)
    private val mission=session.mission
    private val value=ui.badge("",NativeUi.GOLD)
    private val art=MeasurementArt(context)
    private val controls=ArrayList<Button>()
    private val message=ui.text("",18,true)
    private val hint=ui.text("Gợi ý: "+mission.hint,18,false)
    private lateinit var hintButton: Button
    private var hintVisible=false
    private var completed=false

    init {
        orientation=VERTICAL
        ui.caption(this,"Trạm thực hành • Bài "+lesson)
        ui.label(this,mission.title,if(ui.compact())22 else 25,true)
        ui.label(this,mission.brief,if(ui.compact())18 else 21,false)
        ui.add(this,value)
        val side=ui.sideBySide()
        val stage=if(side)ui.row() else ui.column()
        stage.gravity=Gravity.TOP
        ui.add(this,stage)
        val picture=ui.column()
        val keys=ui.column()
        if(side){
            ui.weight(stage,picture,1f);ui.weight(stage,keys,1.15f)
        }else{
            ui.add(stage,picture);ui.add(stage,keys)
        }
        picture.addView(art,LayoutParams(-1,ui.dp(if(ui.compact())142f else 192f)))
        val columns=if(mission.kind==TrailMission.Kind.BUILD&&side)3 else if(mission.kind==TrailMission.Kind.BUILD)4 else 2
        for(base in 0 until mission.actionCount() step columns) {
            val row=ui.row();ui.add(keys,row)
            for(i in base until minOf(mission.actionCount(),base+columns)) {
                val b=ui.button(mission.label(i),NativeUi.PALE){handle(session.move(i))}
                ui.weight(row,b,1f);controls.add(b)
            }
        }
        ui.add(this,message);ui.add(this,hint)
        val actions=ui.row();ui.add(this,actions)
        hintButton=ui.button("Gợi ý",NativeUi.LAV){hintVisible=!hintVisible;refresh()}
        ui.weight(actions,hintButton,1f)
        ui.smallAction(actions,"Làm lại",NativeUi.PALE){handle(session.reset())}
        ui.smallAction(actions,"Kiểm tra",NativeUi.GREEN){handle(session.check())}
        refresh()
    }

    private fun handle(outcome: RainbowStationSession.Outcome) {
        if(completed)return
        when(outcome) {
            RainbowStationSession.Outcome.CHANGED -> message.text=""
            RainbowStationSession.Outcome.NO_CHANGE -> message.text="Không thể thực hiện bước này. Em thử cách khác nhé."
            RainbowStationSession.Outcome.NOT_READY -> {
                message.text="Chưa hoàn thành. Em có thể thử lại!";hintVisible=true
            }
            RainbowStationSession.Outcome.SAVE_FAILED -> {
                message.text="Chưa lưu được. Hãy thử lại."
                Toast.makeText(context,message.text,Toast.LENGTH_SHORT).show()
            }
            RainbowStationSession.Outcome.COMPLETE -> {completed=true;next.run();return}
        }
        refresh()
    }

    private fun refresh() {
        val state=session.snapshot()
        value.text=when(mission.kind) {
            TrailMission.Kind.BUILD -> "Đã ghép / đặt: "+session.remainingOrValue()+" "+mission.unit
            TrailMission.Kind.POUR -> "A: "+state[0]+" ml     B: "+state[1]+" ml"
            TrailMission.Kind.TEMP -> "Hiện tại: "+state[0]+"°C"
            else -> ""
        }
        controls.forEachIndexed { i,b ->
            if(mission.kind==TrailMission.Kind.BUILD) {
                val chosen=mission.selected(state,i)
                b.text=(if(chosen)"✓ " else "")+mission.label(i)
                b.setTextColor(if(chosen)NativeUi.GREEN else NativeUi.INK)
                b.contentDescription="Miếng "+(i+1)+": "+mission.label(i)+(if(chosen)", đã chọn" else ", chưa chọn")
            }
        }
        art.update(state,session.remainingOrValue())
        message.visibility=if(message.text.isEmpty())View.GONE else View.VISIBLE
        hint.visibility=if(hintVisible)View.VISIBLE else View.GONE
        hintButton.text=if(hintVisible)"Ẩn gợi ý" else "Gợi ý"
    }

    private inner class MeasurementArt(context: Context):View(context) {
        private val p=Paint(Paint.ANTI_ALIAS_FLAG)
        private var state=mission.initial()
        private var result=mission.result(state)
        private val ink=NativeUi.INK
        private val blue=0xff51b8cc.toInt()
        private val purple=0xffb7a0e5.toInt()
        private val rose=0xffe782b2.toInt()
        private val light=0xffeef9fa.toInt()
        private val colors=intArrayOf(0xffefa5c3.toInt(),0xff8dd6cd.toInt(),0xffc5b0e9.toInt(),0xffffcf86.toInt())
        fun update(s:IntArray,total:Int) {
            state=s.copyOf();result=total
            contentDescription=when(mission.kind) {
                TrailMission.Kind.BUILD -> "Mô phỏng "+mission.title+": "+result+" "+mission.unit
                TrailMission.Kind.POUR -> "Mô phỏng hai ca: A "+state[0]+" ml, B "+state[1]+" ml"
                TrailMission.Kind.TEMP -> "Mô phỏng nhiệt kế: "+state[0]+"°C"
                else -> mission.title
            }
            invalidate()
        }
        private fun fill(color:Int) {p.color=color;p.style=Paint.Style.FILL}
        private fun line(c:Canvas,x:Float,y:Float,r:Float,b:Float,color:Int,width:Float=2f) {
            fill(color);p.strokeWidth=width;p.strokeCap=Paint.Cap.ROUND;c.drawLine(x,y,r,b,p)
        }
        private fun box(c:Canvas,x:Float,y:Float,r:Float,b:Float,color:Int,corner:Float=5f) {
            if(r<=x||b<=y)return
            fill(color);c.drawRoundRect(x,y,r,b,corner,corner,p)
        }
        private fun text(c:Canvas,s:String,x:Float,y:Float,size:Float,color:Int=ink) {
            fill(color);p.typeface=Typeface.DEFAULT_BOLD;p.textSize=size
            p.textAlign=Paint.Align.CENTER;c.drawText(s,x,y,p)
        }
        override fun onDraw(c:Canvas) {
            super.onDraw(c)
            if(width<10||height<10)return
            val scale=minOf(width/320f,height/170f)
            c.drawColor(light);c.save()
            c.translate((width-320f*scale)/2f,(height-170f*scale)/2f);c.scale(scale,scale)
            when(mission.kind) {
                TrailMission.Kind.BUILD -> if(mission.lesson==30)ruler(c) else balance(c)
                TrailMission.Kind.POUR -> cups(c)
                TrailMission.Kind.TEMP -> thermometer(c)
                else -> Unit
            }
            c.restore()
        }
        private fun ruler(c:Canvas) {
            val maximum=maxOf(42,result,1)
            val unit=268f/maximum
            val x=26f
            text(c,"Cầu cần ghép: 4 cm 2 mm",160f,20f,14f)
            box(c,x,32f,x+42*unit,52f,0xffbce4df.toInt())
            for(i in 0..maximum) {
                if(i%10==0||i==maximum) {
                    val at=x+i*unit;line(c,at,56f,at,66f,ink,1f)
                    if(i%10==0)text(c,i.toString(),at,79f,10f)
                }else if(unit>=3f)line(c,x+i*unit,57f,x+i*unit,62f,ink,1f)
            }
            text(c,"Các tấm ván đã chọn",160f,99f,13f)
            var left=x
            for(i in 0 until mission.actionCount())if(mission.selected(state,i)){
                val end=left+mission.tile(i)*unit
                box(c,left,109f,end,135f,colors[i%colors.size],2f)
                line(c,left,109f,left,135f,ink,1f)
                if(end-left>=24f)text(c,mission.tile(i).toString(),(left+end)/2,126f,10f)
                left=end
            }
            if(result==0)text(c,"Chọn tấm ván",160f,127f,12f)
            text(c,result.toString()+" mm",160f,157f,18f)
        }
        private fun balance(c:Canvas) {
            val target=mission.target
            text(c,if(mission.lesson==34)"Gạo: 1 kg" else "Giỏ hoa quả: 350 g",160f,19f,14f)
            val left=if(result<target)91f else if(result>target)57f else 74f
            val right=148f-left
            line(c,160f,79f,160f,135f,ink,4f)
            box(c,126f,135f,194f,145f,purple)
            line(c,65f,left,255f,right,ink,4f)
            for(i in 0..1) {
                val x=if(i==0)65f else 255f
                val y=if(i==0)left else right
                line(c,x,y,x,y+30f,ink)
                line(c,x,y+13f,x-40f,y+32f,ink)
                line(c,x,y+13f,x+40f,y+32f,ink)
                box(c,x-47f,y+32f,x+47f,y+41f,if(i==0)rose else blue)
                text(c,(if(i==0)target else result).toString()+" g",x,y+58f,13f)
            }
            val words=(0 until mission.actionCount()).filter{mission.selected(state,it)}.map{mission.tile(it).toString()}
            text(c,if(words.isEmpty())"Chưa đặt quả cân" else words.joinToString(" + "),160f,164f,if(words.size>4)10f else 12f)
        }
        private fun cups(c:Canvas) {
            val max=maxOf(mission.tile(0),mission.tile(1))
            val bottom=135f;val maxHeight=101f
            for(i in 0..1) {
                val x=if(i==0)91f else 229f
                val cap=mission.tile(i)
                val top=bottom-maxHeight*cap/max
                val level=bottom-maxHeight*state[i]/max
                text(c,(if(i==0)"Ca A" else "Ca B")+" • "+cap+" ml",x,19f,12f)
                box(c,x-44f,top,x+44f,bottom,0xffdceff3.toInt(),2f)
                if(state[i]>0)box(c,x-41f,level,x+41f,bottom-2f,if(i==0)blue else rose,1f)
                line(c,x-44f,top,x-44f,bottom,ink)
                line(c,x+44f,top,x+44f,bottom,ink)
                line(c,x-44f,bottom,x+44f,bottom,ink)
                val tick=if(cap>=300)100 else 50
                for(v in tick..cap step tick) {
                    val y=bottom-maxHeight*v/max
                    line(c,x+30f,y,x+44f,y,ink,1f)
                }
                text(c,state[i].toString()+" ml",x,158f,18f)
            }
        }
        private fun thermometer(c:Canvas) {
            val top=19f;val bottom=131f;val cx=100f
            box(c,cx-10f,top,cx+10f,bottom+10f,0xffe0ebf0.toInt(),10f)
            val y=bottom-(bottom-top)*state[0]/50
            box(c,cx-6f,y,cx+6f,bottom+8f,rose,3f)
            fill(rose);c.drawCircle(cx,bottom+12f,16f,p)
            for(v in 0..50 step 10){
                val yy=bottom-(bottom-top)*v/50
                line(c,cx+13f,yy,cx+25f,yy,ink,1.5f)
                text(c,v.toString(),cx+43f,yy+4f,11f)
            }
            text(c,"Hiện tại",232f,62f,15f)
            text(c,state[0].toString()+"°C",232f,97f,30f)
            text(c,"Buổi sáng: "+mission.start+"°C",232f,125f,12f)
        }
    }
}
