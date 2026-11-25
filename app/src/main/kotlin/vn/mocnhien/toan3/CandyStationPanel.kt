package vn.mocnhien.toan3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/**
 * Native interactive stations for Bài 26–29. Build controls once, then update
 * their state in place: no full layout reconstruction, no automatic answers.
 */
internal class CandyStationPanel(
    context: Context, store: ProgressStore, lesson: Int,
    private val next: Runnable
) : LinearLayout(context) {
    private val ui = NativeUi(context)
    private val session = CandyStationSession(store, lesson)
    private val mission = session.mission
    private val totals = mutableListOf<TextView>()
    private val mainValue = ui.badge("", NativeUi.GOLD)
    private val drawing = CandyArt(context)
    private val message = ui.text("", 18, true)
    private val hint = ui.text("Gợi ý: "+mission.hint, 18, false)
    private lateinit var hintButton: Button
    private var hintVisible = false
    private var finished = false

    init {
        orientation = VERTICAL
        ui.caption(this, "Trạm thực hành • Bài "+lesson)
        ui.label(this, mission.title, if (ui.compact()) 22 else 25, true)
        ui.label(this, mission.brief, if (ui.compact()) 18 else 21, false)
        ui.add(this, mainValue)
        drawing.layoutParams = LayoutParams(-1, ui.dp(if(ui.compact()) 92f else 128f))
        addView(drawing)
        if (mission.kind == TrailMission.Kind.GROUP) makeGroups() else makeMachine()
        ui.add(this, message)
        ui.add(this, hint)
        val actions = ui.row()
        ui.add(this, actions)
        hintButton = ui.button("Gợi ý", NativeUi.LAV) {
            hintVisible = !hintVisible
            refresh()
        }
        ui.weight(actions, hintButton, 1f)
        ui.smallAction(actions, "Làm lại", NativeUi.PALE) { handle(session.reset()) }
        ui.smallAction(actions, "Kiểm tra", NativeUi.GREEN) { handle(session.check()) }
        refresh()
    }

    private fun makeGroups() {
        val columns = if(ui.widthDp() >= 520) 2 else 1
        val name = CandyStationSession.containerName(mission.lesson)
        for (base in 0 until mission.bins step columns) {
            val row = ui.row()
            row.gravity = Gravity.TOP
            ui.add(this, row)
            for (i in base until minOf(mission.bins, base + columns)) {
                val card = ui.card(NativeUi.PALE)
                ui.weight(row, card, 1f)
                val value = ui.text("", 20, true)
                ui.add(card, value)
                totals.add(value)
                val controls = ui.row()
                ui.add(card, controls)
                val minus = ui.button("−1", NativeUi.WHITE) { handle(session.move(i*2+1)) }
                minus.contentDescription = "$name ${i+1}: bớt 1"
                ui.weight(controls, minus, 1f)
                val plus = ui.button("+1", NativeUi.MINT) { handle(session.move(i*2)) }
                plus.contentDescription = "$name ${i+1}: thêm 1"
                ui.weight(controls, plus, 1f)
            }
        }
    }

    private fun makeMachine() {
        val controls = ui.column()
        ui.add(this, controls)
        for (base in 0 until mission.actionCount() step 2) {
            val row = ui.row()
            ui.add(controls, row)
            for (i in base until minOf(mission.actionCount(),base+2)) {
                val name = mission.label(i)
                val button = ui.button(name, if(name=="Hoàn tác") NativeUi.LAV else NativeUi.PALE) {
                    handle(session.move(i))
                }
                button.contentDescription = if(name=="Hoàn tác") name else "Phép tính $name"
                ui.weight(row,button,1f)
            }
        }
    }

    private fun handle(result: CandyStationSession.Outcome) {
        if(finished) return
        when(result) {
            CandyStationSession.Outcome.CHANGED -> message.text = ""
            CandyStationSession.Outcome.NO_CHANGE -> message.text = "Không thể thực hiện bước này. Em thử cách khác nhé."
            CandyStationSession.Outcome.NOT_READY -> {
                message.text = "Chưa hoàn thành. Em có thể thử lại!"
                hintVisible = true
            }
            CandyStationSession.Outcome.SAVE_FAILED -> {
                message.text = "Chưa lưu được. Hãy thử lại."
                Toast.makeText(context, "Chưa lưu được. Hãy thử lại.", Toast.LENGTH_SHORT).show()
            }
            CandyStationSession.Outcome.COMPLETE -> {
                finished = true
                next.run()
                return
            }
        }
        refresh()
    }

    private fun refresh() {
        val state = session.snapshot()
        if(mission.kind == TrailMission.Kind.GROUP) {
            mainValue.text = "Kho còn ${session.remainingOrValue()} ${mission.unit}"
            val name=CandyStationSession.containerName(mission.lesson)
            for(i in state.indices) totals[i].text="$name ${i+1} · ${state[i]} ${mission.unit}"
        } else {
            mainValue.text=session.trace()
        }
        drawing.updateState(state, session.remainingOrValue())
        message.visibility=if(message.text.isEmpty()) View.GONE else View.VISIBLE
        hint.visibility=if(hintVisible) View.VISIBLE else View.GONE
        hintButton.text=if(hintVisible) "Ẩn gợi ý" else "Gợi ý"
    }

    /** Draw only the child's committed manipulation, never display hidden targets. */
    private inner class CandyArt(context: Context) : View(context) {
        private val paint=Paint(Paint.ANTI_ALIAS_FLAG)
        private var amounts=IntArray(0)
        private var current=0
        fun updateState(values: IntArray, result: Int) {
            amounts=values.copyOf()
            current=result
            contentDescription=if(mission.kind==TrailMission.Kind.GROUP)
                CandyStationSession.containerName(mission.lesson)+": "+amounts.joinToString(", ")+", kho còn "+current
                else "Máy tính: "+current
            invalidate()
        }
        private fun color(value:Int) { paint.color=value; paint.style=Paint.Style.FILL }
        private fun box(c:Canvas,x:Float,y:Float,r:Float,b:Float,rad:Float,col:Int) {
            color(col);c.drawRoundRect(x,y,r,b,rad,rad,paint)
        }
        private fun text(c:Canvas,value:String,x:Float,y:Float,size:Float,col:Int) {
            color(col);paint.textAlign=Paint.Align.CENTER;paint.typeface=Typeface.DEFAULT_BOLD
            paint.textSize=size;c.drawText(value,x,y,paint)
        }
        override fun onDraw(c:Canvas) {
            super.onDraw(c)
            val w=width.toFloat();val h=height.toFloat()
            if(w<10f||h<10f)return
            c.drawColor(0xfffff5f9.toInt())
            val density=resources.displayMetrics.density
            if(mission.kind==TrailMission.Kind.GROUP) {
                val count=mission.bins+1;val slot=w/count
                val labelSize=minOf(16*density,slot*.17f,h*.15f)
                for(i in 0 until count) {
                    val center=(i+.5f)*slot
                    val amount=if(i<amounts.size)amounts[i] else current
                    val r=slot*.40f
                    val label=if(i==mission.bins)"Kho" else
                        CandyStationSession.containerName(mission.lesson)+" "+(i+1)
                    text(c,label,center,h*.19f,labelSize,NativeUi.INK)
                    box(c,center-r,h*.27f,center+r,h*.77f,6*density,
                        if(i==mission.bins)NativeUi.GOLD else if(mission.lesson==26)0xffc9eceb.toInt() else 0xffead8f9.toInt())
                    val dot=minOf(slot*.065f,h*.057f)
                    // Up to 23 tokens: preserve an exact one-to-one count in these four missions.
                    for(j in 0 until amount) {
                        val x=center-r*.76f+(j%6)*r*.30f
                        val y=h*.36f+(j/6)*h*.107f
                        color(if(mission.lesson==26)0xffb75e91.toInt() else 0xff8f6bc3.toInt())
                        c.drawCircle(x,y,dot,paint)
                    }
                    text(c,amount.toString(),center,h*.94f,minOf(22*density,h*.18f),NativeUi.INK)
                }
            } else {
                val radius=minOf(h*.31f,w*.16f)
                val left=w*.24f;val right=w*.76f;val cy=h*.48f
                color(NativeUi.LAV);c.drawCircle(left,cy,radius,paint)
                color(NativeUi.MINT);c.drawCircle(right,cy,radius,paint)
                text(c,mission.start.toString(),left,cy+radius*.23f,radius*.72f,NativeUi.INK)
                text(c,current.toString(),right,cy+radius*.23f,radius*.72f,NativeUi.INK)
                text(c,"→",w*.5f,cy+radius*.21f,radius*.75f,NativeUi.PINK)
                text(c,"Bắt đầu",left,h*.94f,minOf(16*density,h*.15f),NativeUi.INK)
                text(c,"Hiện tại",right,h*.94f,minOf(16*density,h*.15f),NativeUi.INK)
            }
        }
    }
}
