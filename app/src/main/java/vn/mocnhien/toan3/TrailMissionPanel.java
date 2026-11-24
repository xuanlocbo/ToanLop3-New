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
            LinearLayout.LayoutParams groupArt=new LinearLayout.LayoutParams(-1,ui.dp(120));
            addView(new StationDrawing(getContext(),mission,state),groupArt);
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
            if(mission.kind==TrailMission.Kind.GRID){
                ui.label(this,"Chạm vào giao điểm trên lưới để chọn đỉnh.",17,false);
                ui.action(this,"Xóa điểm vừa chọn",NativeUi.PALE,()->move(25));
            }else rowActions(this,mission.actionCount(),mission.kind==TrailMission.Kind.BUILD?5:3,0);
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
            if(m.kind==TrailMission.Kind.GROUP){
                float col=w/(m.bins+1),y=h*.23f,bottom=h*.82f;
                for(int i=0;i<=m.bins;i++){
                    float x=col*(i+.5f),bw=col*.88f;
                    int amount=i==m.bins?m.result(s):s[i];
                    box(x-bw/2,y,x+bw/2,bottom,i==m.bins?0xffffe7ba:0xffd5ede9);
                    int tens=amount/10,ones=amount%10;
                    float small=Math.min(bw*.13f,10*dp);
                    for(int j=0;j<tens;j++){
                        float px=x-bw*.36f+(j%5)*bw*.17f,py=y+9*dp+(j/5)*13*dp;
                        box(px,py,px+small*1.1f,py+small*.65f,0xffae88e5);
                    }
                    for(int j=0;j<ones;j++){
                        float px=x-bw*.32f+(j%4)*bw*.21f,py=bottom-12*dp-(j/4)*13*dp;
                        color(NativeUi.PINK);c.drawCircle(px,py,Math.min(5*dp,bw*.07f),p);
                    }
                    text(""+amount,x,bottom+15*dp,17*dp,NativeUi.INK);
                    text(i==m.bins?"Kho":"#"+(i+1),x,y-5*dp,12*dp,NativeUi.INK);
                }
            }else if(m.kind==TrailMission.Kind.POUR){
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
                int value=m.result(s);
                int[] colors={0xffeaa5c8,0xff9ed8cf,0xffc2a6ee,0xfff6d488};
                if(m.lesson==31||m.lesson==34){
                    float x=w*.5f,beamY=h*.42f,span=w*.28f;
                    float tilt=value==m.target?0:(value<m.target?-1:1)*Math.min(h*.13f,h*.13f*Math.abs(value-m.target)/Math.max(1f,m.target)*3);
                    line(x,h*.80f,x,beamY,NativeUi.INK,5*dp);
                    line(x-span,beamY-tilt,x+span,beamY+tilt,NativeUi.INK,4*dp);
                    for(int i=0;i<2;i++){float xx=x+(i==0?-span:span),yy=beamY+(i==0?-tilt:tilt);
                        line(xx,yy,xx-w*.1f,yy+h*.24f,NativeUi.INK,dp);line(xx,yy,xx+w*.1f,yy+h*.24f,NativeUi.INK,dp);
                        box(xx-w*.12f,yy+h*.24f,xx+w*.12f,yy+h*.29f,colors[i]);}
                    text(m.target+" g",x-span,beamY-tilt+h*.19f,15*dp,NativeUi.INK);
                    text(value+" g",x+span,beamY+tilt+h*.19f,15*dp,NativeUi.INK);
                    text(value==m.target?"Cân bằng":value<m.target?"Còn nhẹ":"Quá nặng",x,h*.95f,15*dp,NativeUi.INK);
                }else if(m.lesson==30||m.lesson==39){
                    float x=w*.07f,scale=w*.86f/Math.max(m.target,value),y=h*.35f;
                    float goal=m.target*scale,at=x;
                    box(x,y,x+goal,h*.68f,0xffe9dce5);
                    for(int i=0;i<m.actionCount();i++)if(m.selected(s,i)){float end=at+m.tile(i)*scale;
                        box(at+1,y+2,end-1,h*.66f,colors[i%4]);
                        if(end-at>22*dp)text(""+m.tile(i),(at+end)/2,h*.56f,12*dp,NativeUi.INK);at=end;}
                    line(x,h*.80f,x+goal,h*.80f,NativeUi.INK,2*dp);
                    for(int i=0;i<=m.target;i++)if(m.target<=42||i%5==0)line(x+i*scale,h*.8f,x+i*scale,h*.8f+(i%5==0?9:5)*dp,NativeUi.INK,dp);
                    text("0",x,h*.97f,12*dp,NativeUi.INK);
                    text(m.target+" "+m.unit,x+goal,h*.97f,12*dp,NativeUi.INK);
                }else{
                    int count=m.actionCount(),cols=Math.min(8,count);float cell=Math.min(w/(cols+1),h*.43f),start=(w-cols*cell)/2;
                    for(int i=0;i<count;i++){float x=start+(i%cols)*cell,y=h*.04f+(i/cols)*cell;
                        box(x+3,y+3,x+cell-3,y+cell-3,m.selected(s,i)?colors[i%4]:0xffe9dce5);
                        text(""+m.tile(i),x+cell/2,y+cell*.64f,Math.min(16*dp,cell*.32f),NativeUi.INK);}
                    text(value+" / "+m.target+" "+m.unit,w/2,h*.95f,17*dp,NativeUi.INK);
                }
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
