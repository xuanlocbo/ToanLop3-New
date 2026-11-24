package vn.mocnhien.toan3;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.widget.*;
import java.util.Arrays;

/** A station shares the existing theme and progress store. No duplicate reward claims. */
final class TrailMissionPanel extends LinearLayout {
    final NativeUi ui;final TrailMission mission;final ProgressStore store;final Runnable next;
    int[] state;boolean showHint=false;String feedback="";
    TrailMissionPanel(Context c,ProgressStore store,int lesson,Runnable next){
        super(c);setOrientation(VERTICAL);ui=new NativeUi(c);this.store=store;this.next=next;
        mission=TrailMission.of(lesson);state=mission.read(store.trailDraft(lesson));render();
    }
    void warn(){Toast.makeText(getContext(),"Chưa lưu được. Hãy thử lại.",Toast.LENGTH_SHORT).show();}
    void move(int i){
        int[] after=mission.move(state,i);
        if(Arrays.equals(state,after)){feedback="Không thể thực hiện bước này. Hãy thử cách khác.";render();return;}
        if(store.saveTrailDraft(mission.lesson,mission.encode(after))){state=after;feedback="";render();}else warn();
    }
    void reset(){
        int[] s=mission.initial();
        if(store.saveTrailDraft(mission.lesson,mission.encode(s))){state=s;feedback="";render();}else warn();
    }
    void check(){
        if(!mission.solved(state)){feedback="Chưa hoàn thành. Em có thể thử lại!";showHint=true;render();return;}
        if(store.completeTrail(mission.lesson,mission.encode(state))){next.run();}else warn();
    }
    void rowActions(LinearLayout parent,int count,int perRow,int offset){
        for(int i=0;i<count;i+=perRow){
            LinearLayout row=ui.row();ui.add(parent,row);
            for(int j=i;j<Math.min(count,i+perRow);j++){
                final int id=j+offset;
                ui.smallAction(row,mission.label(id),mission.selected(state,id)?NativeUi.MINT:NativeUi.PALE,()->move(id));
            }
        }
    }
    void render(){
        removeAllViews();
        ui.label(this,"Trạm thực hành • Bài "+mission.lesson,17,true);
        ui.label(this,mission.title,25,true);ui.label(this,mission.brief,21,false);
        if(mission.kind==TrailMission.Kind.GROUP){
            ui.label(this,"Kho còn "+mission.result(state)+" "+mission.unit,23,true);
            int per=mission.total>60?4:2;
            for(int b=0;b<mission.bins;b++){
                LinearLayout card=ui.card(NativeUi.PALE);ui.add(this,card);
                ui.label(card,"Nhóm "+(b+1)+"   •   "+state[b]+" "+mission.unit,21,true);
                rowActions(card,per,per,b*per);
            }
        }else{
            if(mission.kind==TrailMission.Kind.BUILD)ui.label(this,"Đã ghép / đặt: "+mission.result(state)+" "+mission.unit,23,true);
            if(mission.kind==TrailMission.Kind.STEPS)ui.label(this,mission.trace(state),24,true);
            if(mission.kind==TrailMission.Kind.POUR||mission.kind==TrailMission.Kind.TEMP||mission.kind==TrailMission.Kind.GRID||mission.kind==TrailMission.Kind.BUILD){
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,ui.dp(mission.kind==TrailMission.Kind.GRID?230:145));
                addView(new StationDrawing(getContext(),mission,state),lp);
            }
            if(mission.kind==TrailMission.Kind.POUR)ui.label(this,"A: "+state[0]+" ml     B: "+state[1]+" ml",22,true);
            if(mission.kind==TrailMission.Kind.TEMP)ui.label(this,"Hiện tại: "+state[0]+"°C",23,true);
            rowActions(this,mission.actionCount(),mission.kind==TrailMission.Kind.GRID?5:mission.kind==TrailMission.Kind.BUILD?5:3,0);
        }
        if(!feedback.isEmpty())ui.label(this,feedback,20,true);
        if(showHint)ui.label(this,"Gợi ý: "+mission.hint,19,false);
        LinearLayout actions=ui.row();ui.add(this,actions);
        ui.smallAction(actions,"Gợi ý",NativeUi.LAV,()->{showHint=!showHint;render();});
        ui.smallAction(actions,"Làm lại",NativeUi.PALE,this::reset);
        ui.smallAction(actions,"Kiểm tra",NativeUi.GREEN,this::check);
    }
    final class StationDrawing extends View {
        final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);final TrailMission m;final int[] s;
        Canvas c;float w,h,gridSize,gridX,gridY;
        StationDrawing(Context ctx,TrailMission m,int[] s){super(ctx);this.m=m;this.s=s.clone();
            setContentDescription(m.kind==TrailMission.Kind.GRID?"Lưới ô vuông. Chạm các điểm để nối hình.":"Mô phỏng "+m.title);
        }
        void color(int col){p.setColor(col);p.setStyle(Paint.Style.FILL);}
        void line(float x,float y,float xx,float yy,int col,float size){color(col);p.setStrokeWidth(size);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,xx,yy,p);}
        void box(float x,float y,float xx,float yy,int col){color(col);c.drawRoundRect(x,y,xx,yy,7,7,p);}
        void text(String t,float x,float y,float size,int col){color(col);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(size);p.setTextAlign(Paint.Align.CENTER);c.drawText(t,x,y,p);}
        @Override protected void onDraw(Canvas canvas){
            super.onDraw(canvas);c=canvas;w=getWidth();h=getHeight();if(w<=0||h<=0)return;
            box(0,0,w,h,0xfffff3f8);
            float dp=getResources().getDisplayMetrics().density;
            if(m.kind==TrailMission.Kind.POUR){
                for(int i=0;i<2;i++){float x=w*(.29f+.42f*i),bw=Math.min(w*.22f,90*dp),top=h*.14f,bottom=h*.85f,hh=bottom-top;
                    box(x-bw/2,top,x+bw/2,bottom,0xffd8edf4);
                    float level=hh*s[i]/m.tile(i);
                    if(level>0)box(x-bw/2+3,bottom-level,x+bw/2-3,bottom-3,0xff78cee3);
                    for(int k=0;k<=5;k++)line(x+bw/2-16*dp,bottom-hh*k/5,x+bw/2-4*dp,bottom-hh*k/5,0xff387b94,dp);
                    text((i==0?"A":"B")+" • "+m.tile(i)+" ml",x,top-4*dp,14*dp,NativeUi.INK);
                }
            }else if(m.kind==TrailMission.Kind.TEMP){
                float x=w*.5f,y=h*.87f,top=h*.1f,r=8*dp;
                box(x-r,top,x+r,y,0xffe6dbe2);box(x-r*.65f,y-(y-top)*s[0]/50,x+r*.65f,y,0xffef6688);
                color(0xffef6688);c.drawCircle(x,y,12*dp,p);
                for(int k=0;k<=50;k+=10){float a=y-(y-top)*k/50;line(x+r,a,x+20*dp,a,NativeUi.INK,dp);text(""+k,x+35*dp,a+4*dp,12*dp,NativeUi.INK);}
            }else if(m.kind==TrailMission.Kind.GRID){
                gridSize=Math.min(w*.72f,h*.88f)/4;gridX=(w-gridSize*4)/2;gridY=(h-gridSize*4)/2;
                for(int i=0;i<5;i++){line(gridX+i*gridSize,gridY,gridX+i*gridSize,gridY+4*gridSize,0xffe0bacc,dp);line(gridX,gridY+i*gridSize,gridX+4*gridSize,gridY+i*gridSize,0xffe0bacc,dp);}
                int prev=-1,first=-1,n=0;
                for(int i:s){if(i<0)break;float x=gridX+i%5*gridSize,y=gridY+i/5*gridSize;
                    if(prev>=0)line(gridX+prev%5*gridSize,gridY+prev/5*gridSize,x,y,NativeUi.PINK,3*dp);
                    color(NativeUi.PINK);c.drawCircle(x,y,5*dp,p);if(first<0)first=i;prev=i;n++;}
                if(n==4)line(gridX+prev%5*gridSize,gridY+prev/5*gridSize,gridX+first%5*gridSize,gridY+first/5*gridSize,NativeUi.PINK,3*dp);
            }else if(m.kind==TrailMission.Kind.BUILD){
                int value=m.result(s);float length=w*.8f;float x=w*.1f;
                float filled=length*Math.min(1f,value/(float)m.target);
                box(x,h*.30f,x+length,h*.67f,0xffe8d7e2);
                if(filled>0)box(x,h*.30f,x+filled,h*.67f,value==m.target?0xff95d8af:value>m.target?0xffeb9b9b:0xffeca5c5);
                text(value+" / "+m.target+" "+m.unit,w/2,h*.54f,20*dp,NativeUi.INK);
            }
        }
        @Override public boolean onTouchEvent(android.view.MotionEvent e){
            if(m.kind!=TrailMission.Kind.GRID||gridSize<=0)return false;
            if(e.getAction()==android.view.MotionEvent.ACTION_UP){
                int x=Math.round((e.getX()-gridX)/gridSize),y=Math.round((e.getY()-gridY)/gridSize);
                if(x>=0&&x<5&&y>=0&&y<5&&Math.abs(e.getX()-gridX-x*gridSize)<gridSize*.42f&&Math.abs(e.getY()-gridY-y*gridSize)<gridSize*.42f){performClick();move(y*5+x);}return true;
            }
            return true;
        }
        @Override public boolean performClick(){super.performClick();return true;}
    }
}
