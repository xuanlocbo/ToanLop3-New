package vn.mocnhien.toan3;

import android.content.Context;
import android.graphics.*;
import android.os.SystemClock;
import android.view.View;

/** Original lightweight vector scenery, rendered entirely with Android Canvas. */
final class AdventureScene extends View {
    final AdventureWorld world;final AdventureWorld.State state;
    final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    Canvas c;
    int face=1;boolean walking;
    static final int INK=0xff633d61,WHITE=0xfffffff9,GOLD=0xffffcf55,PINK=0xffeb83b0;
    static final int[] SKY={0xffffdce9,0xffd6f3fa,0xffe5ddff,0xffffe9d3};
    static final int[] HILL={0xffc9eeb9,0xffb2ddda,0xffc7b8ed,0xfff3beca};
    static final int[] GRASS={0xff8bc9a4,0xff73c9c1,0xffb6a4e3,0xfff4a7bb};
    static final int[] SOIL={0xffd59a8d,0xff8ebdbb,0xffaa96cb,0xffd99b9a};
    static final float GROUND=174,HEIGHT=236;
    AdventureScene(Context c,AdventureWorld world,AdventureWorld.State state){
        super(c);this.world=world;this.state=state;
        setContentDescription("Thỏ hồng phiêu lưu. Dùng các nút đi và nhảy để vượt hồ và đá.");
    }
    void fill(int color){p.setStyle(Paint.Style.FILL);p.setColor(color);p.setShader(null);}
    void rect(float x,float y,float w,float h,int col){fill(col);c.drawRoundRect(x,y,x+w,y+h,5,5,p);}
    void oval(float x,float y,float w,float h,int col){fill(col);c.drawOval(x,y,x+w,y+h,p);}
    void circle(float x,float y,float r,int col){fill(col);c.drawCircle(x,y,r,p);}
    void line(float x,float y,float xx,float yy,float w,int col){fill(col);p.setStrokeWidth(w);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,xx,yy,p);}
    void text(String t,float x,float y,float size,int col){fill(col);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(size);p.setTextAlign(Paint.Align.CENTER);c.drawText(t,x,y,p);}
    void poly(int col,float... a){Path path=new Path();path.moveTo(a[0],a[1]);for(int i=2;i<a.length;i+=2)path.lineTo(a[i],a[i+1]);path.close();fill(col);c.drawPath(path,p);}
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);if(getWidth()<=0||getHeight()<=0)return;c=canvas;
        int theme=AdventurePlan.theme(world.lesson);
        float scale=getHeight()/HEIGHT,w=getWidth()/scale;
        float camera=Math.max(0,Math.min(AdventureWorld.FINISH+100-w,state.x-w*.37f));
        c.save();c.scale(scale,scale);
        fill(SKY[theme]);p.setShader(new LinearGradient(0,0,0,HEIGHT,SKY[theme],WHITE,Shader.TileMode.CLAMP));c.drawRect(0,0,w,HEIGHT,p);p.setShader(null);
        circle(w-44,36,24,0xfffff1b7);
        for(int i=0;i<12;i++){
            float x=i*195-camera*.18f-40;
            if(x< -200||x>w+120)continue;oval(x,27+(i%3)*12,75,20,WHITE);circle(x+25,26+(i%3)*12,18,WHITE);circle(x+45,30+(i%3)*12,16,WHITE);
        }
        for(int i=0;i<18;i++){
            float x=i*180-camera*.32f-110;
            if(x< -230||x>w+80)continue;
            if(theme==1&&(world.lesson==33||world.lesson==34))poly(HILL[theme],x,GROUND,x+94,45+(i%3)*18,x+190,GROUND);
            else oval(x,85+(i%3)*13,210,157,HILL[theme]);
        }
        c.save();c.translate(-camera,0);
        for(int i=0;i<19;i++){
            float x=70+i*118;if(x<camera-70||x>camera+w+70)continue;
            tree(x,theme,i);
        }
        fill(0xffa6ddea);c.drawRect(camera,GROUND,camera+w,HEIGHT,p);
        for(int i=(int)(camera/37);i<=(camera+w)/37+1;i++)line(i*37,GROUND+13+(i%2)*7,i*37+18,GROUND+13+(i%2)*7,1.7f,WHITE);
        int start=0;
        for(AdventureWorld.Hazard h:world.hazards)if(h.pit()){land(start,h.left,theme);start=h.right;}
        land(start,AdventureWorld.FINISH+140,theme);
        for(AdventureWorld.Hazard h:world.hazards){
            if(h.right<camera-40||h.left>camera+w+40)continue;
            if(h.pit()){
                line(h.left-16,GROUND,h.left-16,GROUND-42,3,INK);rect(h.left-34,GROUND-60,37,20,GOLD);text("↑",h.left-16,GROUND-44,17,INK);
            }else{
                rect(h.left,GROUND-h.height,h.right-h.left,h.height,theme==2?0xffa696da:0xffdca7b4);
                rect(h.left+3,GROUND-h.height+2,h.right-h.left-6,7,0xffffdfeb);
                for(int i=0;i<2;i++)circle(h.left+9+i*14,GROUND-h.height+16,2.4f,INK);
            }
        }
        for(int i=0;i<AdventureWorld.MAX_PETALS;i++)if((state.petals&(1<<i))==0){
            float x=world.petals[i][0],y=GROUND-world.petals[i][1];
            if(x>=camera-20&&x<=camera+w+20)flower(x,y,7,0xffffbcdd);
        }
        for(int g=0;g<4;g++){
            float x=(g+1)*AdventureWorld.SEGMENT;
            if(x<camera-60||x>camera+w+60)continue;
            gate(x,g);
        }
        for(int g=0;g<=4;g++){
            float x=AdventureWorld.checkpoint(g)-12;
            if(x<camera-30||x>camera+w+30)continue;
            line(x,GROUND,x,GROUND-53,3,INK);
            poly(g<=state.gate?0xff79cfa1:0xffdfc5d3,x,GROUND-53,x+28,GROUND-45,x,GROUND-33);
        }
        float f=AdventureWorld.FINISH+17;
        rect(f-26,GROUND-29,52,29,GOLD);rect(f-26,GROUND-35,52,14,0xfff4ad66);rect(f-5,GROUND-24,10,11,INK);
        if(state.finished){flower(f,GROUND-55,11,PINK);text("VỀ ĐÍCH!",f-12,GROUND-76,15,INK);}
        rabbit(state.x,GROUND-state.y,face);
        c.restore();
        // Thin progress track stays fixed on screen.
        rect(12,7,w-24,7,0xfff9edf2);rect(12,7,Math.max(1,(w-24)*state.x/AdventureWorld.FINISH),7,PINK);
        c.restore();
    }
    void tree(float x,int theme,int i){
        if(theme==1&&world.lesson==33){poly(0xff91c9dc,x-24,GROUND,x,GROUND-73,x+24,GROUND);poly(WHITE,x-15,GROUND-27,x,GROUND-73,x+15,GROUND-27);return;}
        if(theme==2){poly(0xffb9a7e5,x-18,GROUND,x-12,GROUND-53,x,GROUND-75,x+15,GROUND-47,x+18,GROUND);poly(0xffe5d9ff,x-12,GROUND-53,x,GROUND-75,x,GROUND-20);return;}
        rect(x-4,GROUND-43,8,43,0xffb99195);
        circle(x,GROUND-51,22,theme==0?0xffacd8a2:0xfff4c3d7);
        circle(x-13,GROUND-43,13,theme==0?0xffacd8a2:0xfff4c3d7);
        circle(x+14,GROUND-42,14,theme==0?0xffacd8a2:0xfff4c3d7);
        if(i%2==0)flower(x+4,GROUND-54,6,theme==0?PINK:WHITE);
    }
    void land(int start,int end,int theme){
        if(end<=start)return;
        fill(SOIL[theme]);c.drawRect(start,GROUND,end,HEIGHT,p);
        fill(GRASS[theme]);c.drawRect(start,GROUND,end,GROUND+12,p);
        for(int x=start+13;x<end-10;x+=43){oval(x,GROUND+24,12,5,0x36ffffff);line(x+22,GROUND+42,x+34,GROUND+42,2,0x36ffffff);}
    }
    void flower(float x,float y,float r,int color){
        for(int i=0;i<5;i++){double a=i*Math.PI*2/5-Math.PI/2;circle(x+(float)Math.cos(a)*r,y+(float)Math.sin(a)*r,r*.72f,color);}
        circle(x,y,r*.6f,GOLD);
    }
    void gate(float x,int g){
        boolean open=g<state.gate;int col=open?0xff78cda4:g<2?0xffe79bc2:GOLD;
        rect(x-8,GROUND-83,16,83,col);rect(x+46,GROUND-83,16,83,col);rect(x-8,GROUND-91,70,24,col);
        text(g<2?(g==0?"SGK":"VBT"):"★ "+(g-1),x+27,GROUND-74,14,INK);
        if(!open){
            for(int i=0;i<3;i++)rect(x+7,GROUND-63+i*19,40,6,col);
            rect(x+20,GROUND-40,17,17,INK);p.setColor(INK);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);c.drawArc(x+22,GROUND-47,x+35,GROUND-32,180,180,false,p);p.setStyle(Paint.Style.FILL);
            circle(x+28,GROUND-33,2,GOLD);
        }else text("✓",x+26,GROUND-30,27,0xff309966);
    }
    void rabbit(float x,float foot,int direction){
        c.save();c.translate(x,foot);c.scale(direction,1);
        float stride=walking&&state.y==0?(float)Math.sin(SystemClock.uptimeMillis()/75.0)*3:0;
        oval(-14,-3,28,6,0x287b5870);
        circle(-13,-17,6,WHITE);
        oval(-11,-24,23,23,0xfff6a5cb);
        oval(-10,-5+stride,10,6,WHITE);oval(4,-5-stride,10,6,WHITE);
        oval(-12,-43,28,28,WHITE);
        c.save();c.rotate(-13,-5,-39);oval(-11,-62,11,24,WHITE);oval(-8,-59,5,17,PINK);c.restore();
        c.save();c.rotate(10,8,-39);oval(3,-62,11,24,WHITE);oval(6,-59,5,17,PINK);c.restore();
        circle(6,-31,2.6f,INK);circle(7,-32,1,WHITE);circle(12,-25,2.4f,PINK);oval(2,-25,7,4,0xffffc6d7);
        flower(-6,-18,4,GOLD);
        c.restore();
    }
}
