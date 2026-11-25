package vn.mocnhien.toan3;

import android.content.Context;
import android.graphics.*;
import android.view.View;

/** Native diagrams update in place; all coordinates and counts come from the committed mission. */
final class FinalStationArt extends View {
    final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);final StoryArt.Painter art=new StoryArt.Painter();final TrailMission m;int[] s;final java.util.function.IntConsumer point;
    Canvas c;float w,h,gridSize,gridX,gridY,downX,downY;
    boolean tap;
    FinalStationArt(Context ctx,TrailMission m,int[] s,java.util.function.IntConsumer point){super(ctx);this.m=m;this.s=s.clone();this.point=point;
        setContentDescription(m.kind==TrailMission.Kind.GRID?"Lưới ô vuông. Chạm các điểm để nối hình.":"Mô phỏng "+m.title);
    }
    void update(int[] state){s=state.clone();invalidate();}
    void color(int col){p.setColor(col);p.setStyle(Paint.Style.FILL);}
    void line(float x,float y,float xx,float yy,int col,float size){color(col);p.setStrokeWidth(size);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,xx,yy,p);}
    void box(float x,float y,float xx,float yy,int col){color(col);c.drawRoundRect(x,y,xx,yy,7,7,p);}
    void text(String t,float x,float y,float size,int col){color(col);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(size);p.setTextAlign(Paint.Align.CENTER);c.drawText(t,x,y,p);}
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);c=canvas;w=getWidth();h=getHeight();if(w<=0||h<=0)return;
        p.setShader(new LinearGradient(0,0,w,h,0xfffff9f5,0xffffe5f0,Shader.TileMode.CLAMP));
        c.drawRoundRect(0,0,w,h,16,16,p);p.setShader(null);
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
        }else if(m.kind==TrailMission.Kind.STEPS){
            float mid=h*.48f;
            box(w*.06f,h*.12f,w*.40f,h*.88f,0xffe8d9fa);
            box(w*.60f,h*.12f,w*.94f,h*.88f,0xffccece2);
            text("Bắt đầu",w*.23f,h*.32f,Math.min(17*dp,h*.17f),NativeUi.INK);
            text("Hiện tại",w*.77f,h*.32f,Math.min(17*dp,h*.17f),NativeUi.INK);
            String unit=m.lesson==40?" kg":"";
            text(m.start+unit,w*.23f,h*.66f,Math.min(24*dp,h*.28f),NativeUi.INK);
            text(m.result(s)+unit,w*.77f,h*.66f,Math.min(24*dp,h*.28f),NativeUi.INK);
            text("→",w*.5f,mid+7*dp,23*dp,NativeUi.PINK);
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
                int count=m.actionCount(),cols=Math.min(8,count);float cell=Math.min(w/(cols+1),h*.81f/((count+cols-1)/cols)),start=(w-cols*cell)/2;
                for(int i=0;i<count;i++){float x=start+(i%cols)*cell,y=h*.04f+(i/cols)*cell;
                    box(x+3,y+3,x+cell-3,y+cell-3,m.selected(s,i)?colors[i%4]:0xffe9dce5);
                    if(m.lesson==41){
                    art.sparkle(c,x+cell/2,y+cell/2,cell*.28f,m.selected(s,i)?0xffc16b9a:0xffa59ab3);
                }else text(""+m.tile(i),x+cell/2,y+cell*.64f,Math.min(16*dp,cell*.32f),NativeUi.INK);}
                text(value+" / "+m.target+" "+m.unit,w/2,h*.95f,17*dp,NativeUi.INK);
            }
        }
    }
    @Override public boolean onTouchEvent(android.view.MotionEvent e){
        if(m.kind!=TrailMission.Kind.GRID||gridSize<=0)return false;
        if(e.getAction()==android.view.MotionEvent.ACTION_DOWN){downX=e.getX();downY=e.getY();tap=true;return true;}
        if(e.getAction()==android.view.MotionEvent.ACTION_CANCEL){tap=false;return true;}
        float slop=android.view.ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if(Math.abs(e.getX()-downX)>slop||Math.abs(e.getY()-downY)>slop)tap=false;
        if(e.getAction()==android.view.MotionEvent.ACTION_UP&&tap){tap=false;
            int x=Math.round((e.getX()-gridX)/gridSize),y=Math.round((e.getY()-gridY)/gridSize);
            if(x>=0&&x<5&&y>=0&&y<5&&Math.abs(e.getX()-gridX-x*gridSize)<gridSize*.42f&&Math.abs(e.getY()-gridY-y*gridSize)<gridSize*.42f){performClick();point.accept(y*5+x);}return true;
        }
        return true;
    }
    @Override public boolean performClick(){super.performClick();return true;}
}
