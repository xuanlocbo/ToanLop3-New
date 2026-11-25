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
    final StoryArt.Painter art=new StoryArt.Painter();
    final Path outline=new Path();
    int face=1;boolean walking;
    private long effectAt=0;private int effectPetals=0;private boolean finishEffect=false;
    void celebrate(int petals,boolean finish){
        if(petals==0&&!finish)return;
        effectPetals=petals;finishEffect=finish;effectAt=SystemClock.uptimeMillis();invalidate();
    }
    void stopEffects(){effectAt=0;effectPetals=0;finishEffect=false;invalidate();}
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
    void poly(int col,float... a){Path path=outline;path.reset();path.moveTo(a[0],a[1]);for(int i=2;i<a.length;i+=2)path.lineTo(a[i],a[i+1]);path.close();fill(col);c.drawPath(path,p);}
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);if(getWidth()<=0||getHeight()<=0)return;c=canvas;
        int theme=AdventurePlan.theme(world.lesson);
        // Keep enough horizontal view ahead of the rabbit, even on a narrow window.
        float scale=Math.min(getHeight()/HEIGHT,getWidth()/300f),w=getWidth()/scale;
        float offset=(getHeight()-HEIGHT*scale)*.5f;
        long now=SystemClock.uptimeMillis();
        float pulse=(float)Math.sin(now/220.0);
        float camera=Math.max(0,Math.min(AdventureWorld.FINISH+100-w,state.x-w*.37f));
        c.drawColor(StoryArt.PALETTES[theme][1]);c.save();c.translate(0,offset);c.scale(scale,scale);
        int[] palette=StoryArt.PALETTES[theme];
        fill(palette[0]);p.setShader(new LinearGradient(0,0,0,HEIGHT,palette[0],palette[1],Shader.TileMode.CLAMP));c.drawRect(0,0,w,HEIGHT,p);p.setShader(null);
        circle(w-44,36,28,0x40fff8cb);circle(w-44,36,23,0xffffedac);
        for(int i=0;i<9;i++){
            float x=(i*157+32-camera*.10f)%(w+90);if(x<0)x+=w+90;
            if(i%2==0)art.sparkle(c,x,26+i%3*17,2.5f+(i%3),0xbfffffff);
        }
        for(int i=0;i<12;i++){
            float x=i*195-camera*.18f-40;
            if(x< -200||x>w+120)continue;art.cloud(c,x+30,27+(i%3)*12,1.15f);
        }
        for(int i=0;i<18;i++){
            float x=i*180-camera*.32f-110;
            if(x< -230||x>w+80)continue;
            if(theme==1&&(world.lesson==33||world.lesson==34)){
                float peak=45+(i%3)*18;
                poly(palette[2],x,GROUND,x+94,peak,x+190,GROUND);
                poly(0xeaffffff,x+61,peak+46,x+94,peak,x+127,peak+46,x+107,peak+36,x+94,peak+48,x+82,peak+34);
            }else{
                oval(x,79+(i%3)*13,210,164,palette[2]);
                oval(x+23,106+(i%2)*10,190,143,palette[3]);
            }
        }
        if(theme==3||theme==2){
            for(int i=0;i<7;i++){
                float bx=110+i*385-camera*.38f;
                if(bx>-120&&bx<w+120)art.castle(c,bx,GROUND-7,.72f+(i%2)*.08f,theme);
            }
        }
        c.save();c.translate(-camera,0);
        for(int i=0;i<19;i++){
            float x=70+i*118;if(x<camera-70||x>camera+w+70)continue;
            art.tree(c,x,GROUND,.93f+(i%3)*.11f,theme,i+world.lesson);
        }
        fill(0xff8cd0e5);p.setShader(new LinearGradient(0,GROUND,0,HEIGHT,0xffb4e9f1,0xff7fb5dc,Shader.TileMode.CLAMP));c.drawRect(camera,GROUND,camera+w,HEIGHT,p);p.setShader(null);
        for(int i=(int)(camera/37);i<=(camera+w)/37+1;i++){
            float drift=3*pulse,yy=GROUND+13+(i%2)*7;
            line(i*37+drift,yy,i*37+18+drift,yy,1.7f,0xdfffffff);
        }
        int start=0;
        for(AdventureWorld.Hazard h:world.hazards)if(h.pit()){land(start,h.left,theme,camera,w);start=h.right;}
        land(start,AdventureWorld.FINISH+140,theme,camera,w);
        for(AdventureWorld.Hazard h:world.hazards){
            if(h.right<camera-40||h.left>camera+w+40)continue;
            if(h.pit()){
                line(h.left-16,GROUND,h.left-16,GROUND-42,3,INK);rect(h.left-34,GROUND-60,37,20,GOLD);text("↑",h.left-16,GROUND-44,17,INK);
            }else{
                float yy=GROUND-h.height,ww=h.right-h.left;
                rect(h.left,yy,ww,h.height,palette[5]);
                rect(h.left+2,yy+2,ww-4,7,0xeafff3e9);
                rect(h.left+3,yy+12,ww-6,h.height-14,0x33ffffff);
                if(theme==2)art.sparkle(c,h.left+ww*.5f,yy+h.height*.57f,7,0xfff5ddff);
                else art.flower(c,h.left+ww*.5f,yy+h.height*.57f,5,0xffffd6e6);
            }
        }
        for(int i=0;i<AdventureWorld.MAX_PETALS;i++)if((state.petals&(1<<i))==0){
            float x=world.petals[i][0],y=GROUND-world.petals[i][1];
            if(x>=camera-20&&x<=camera+w+20){
                circle(x,y,13,0x36fff1bc);
                art.flower(c,x,y,7.3f+pulse*.7f,0xffffb9dd);
                art.sparkle(c,x+10,y-10,3,0xfffff9d8);
            }
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
        art.chest(c,f,GROUND,1.15f,state.finished);
        if(state.finished){art.flower(c,f,GROUND-68,9,PINK);text("VỀ ĐÍCH!",f-22,GROUND-86,15,INK);}
        float stride=walking&&state.y==0?(float)Math.sin(now/72.0)*3:0;
        float foot=GROUND-state.y;
        if(state.y==0&&walking)foot-=Math.abs(stride)*.35f;
        if(state.y>0){oval(state.x-13,GROUND-2,26,4,0x285e4561);}
        art.rabbit(c,state.x,foot,1f,face,stride,now%4200<120,state.finished);
        if(effectAt>0){
            float age=(now-effectAt)/760f;
            if(age>=0&&age<1){
                for(int i=0;i<AdventureWorld.MAX_PETALS;i++)if((effectPetals&(1<<i))!=0){
                    for(int j=0;j<6;j++){
                        double a=j*Math.PI/3;
                        int alpha=(int)(220*(1-age));int col=(alpha<<24)|(j%2==0?0x00ffcc65:0x00ed78b0);
                        float r=6+age*25;
                        art.sparkle(c,world.petals[i][0]+(float)Math.cos(a)*r,GROUND-world.petals[i][1]+(float)Math.sin(a)*r-7*age,4*(1-age)+1,col);
                    }
                }
                if(finishEffect)for(int i=0;i<14;i++){
                    float xx=state.x-70+i*11,yy=GROUND-80-(i%4)*13+20*age;
                    art.sparkle(c,xx,yy,4+(i%3),((int)(220*(1-age))<<24)|(i%2==0?0x00ffcf62:0x00e884b6));
                }
                if(isAttachedToWindow()&&getWindowVisibility()==VISIBLE)postInvalidateOnAnimation();
            }else effectAt=0;
        }
        c.restore();
        // Thin progress track stays fixed on screen.
        rect(12,7,w-24,7,0xf0ffffff);rect(12,7,Math.max(1,(w-24)*state.x/AdventureWorld.FINISH),7,StoryArt.ROSE);
        for(int g=0;g<AdventurePlan.GATES;g++){
            float at=12+(w-24)*(g+1)*AdventureWorld.SEGMENT/AdventureWorld.FINISH;
            circle(at,10.5f,3.4f,g<state.gate?0xff479f7b:0xffd88db5);
        }
        c.restore();
    }
    void land(int start,int end,int theme,float camera,float visibleWidth){
        if(end<=start||end<camera-80||start>camera+visibleWidth+80)return;
        int[] palette=StoryArt.PALETTES[theme];
        fill(palette[5]);p.setShader(new LinearGradient(0,GROUND,0,HEIGHT,palette[5],SOIL[theme],Shader.TileMode.CLAMP));c.drawRect(start,GROUND,end,HEIGHT,p);p.setShader(null);
        fill(palette[4]);c.drawRect(start,GROUND,end,GROUND+10,p);
        line(start+1,GROUND+1,end-1,GROUND+1,2.5f,palette[3]);
        for(int x=visibleFirst(start+10,16,camera);x<end-8&&x<camera+visibleWidth+80;x+=16)oval(x,GROUND+6,12,7,palette[4]);
        for(int x=visibleFirst(start+13,43,camera);x<end-10&&x<camera+visibleWidth+80;x+=43){oval(x,GROUND+24,12,5,0x36ffffff);line(x+22,GROUND+42,x+34,GROUND+42,2,0x36ffffff);}
        for(int x=visibleFirst(start+27,75,camera);x<end-20&&x<camera+visibleWidth+80;x+=75)art.flower(c,x,GROUND-3,3.3f,0xffffcfdf);
    }
    static int visibleFirst(int first,int step,float camera){return first+Math.max(0,(int)Math.floor((camera-80-first)/step))*step;}
    void gate(float x,int g){
        boolean open=g<state.gate;int col=open?0xff7fc9a9:g<2?0xffdd86b4:GOLD;
        int edge=open?0xff469e7a:g<2?0xffb866a0:0xffc99544;
        rect(x-9,GROUND-83,19,83,edge);rect(x+44,GROUND-83,19,83,edge);
        rect(x-6,GROUND-82,13,81,col);rect(x+47,GROUND-82,13,81,col);
        rect(x-12,GROUND-95,78,29,edge);rect(x-10,GROUND-93,74,25,col);
        rect(x-7,GROUND-91,68,4,0x8dffffff);
        art.flower(c,x-1,GROUND-101,4,0xffffe9f3);art.flower(c,x+55,GROUND-101,4,0xffffe9f3);
        text(g<2?(g==0?"SGK":"VBT"):"★ "+(g-1),x+27,GROUND-75,14,INK);
        if(!open){
            for(int i=0;i<3;i++)rect(x+7,GROUND-63+i*19,40,6,col);
            rect(x+20,GROUND-40,17,17,INK);p.setColor(INK);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);c.drawArc(x+22,GROUND-47,x+35,GROUND-32,180,180,false,p);p.setStyle(Paint.Style.FILL);
            circle(x+28,GROUND-33,2,GOLD);
        }else text("✓",x+26,GROUND-30,27,0xff309966);
    }
}
