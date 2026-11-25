package vn.mocnhien.toan3;

import android.content.Context;
import android.graphics.*;
import android.view.View;

/** Reusable original 2D storybook art. All measurements are decorative; the game engine owns collisions. */
final class StoryArt {
    static final int INK=0xff57364d, CREAM=0xfffffbf4, PINK=0xffef85b4, ROSE=0xffb93e7a, GOLD=0xffffcb62;
    static final int[][] PALETTES={
        {0xff72d6ff,0xffe4f7ff,0xff9bcfe8,0xffbee7a6,0xff65bc48,0xffd09a6c},
        {0xffd4eff8,0xfffff2ed,0xffa6d4e6,0xffbce5df,0xff68b8ad,0xff8e9cde},
        {0xffe2d8ff,0xffffeaf5,0xffbda9e8,0xffdacafa,0xffb3a0e0,0xff9f79c9},
        {0xffffdfdb,0xfffff4e4,0xfff3b5cd,0xfff6d3d7,0xffe6a0ba,0xffd783a2}
    };
    static final class Painter {
        final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
        final Path path=new Path();
        final RectF r=new RectF();
        void fill(int color){p.setShader(null);p.setStyle(Paint.Style.FILL);p.setColor(color);}
        void circle(Canvas c,float x,float y,float a,int col){fill(col);c.drawCircle(x,y,a,p);}
        void oval(Canvas c,float x,float y,float w,float h,int col){fill(col);r.set(x,y,x+w,y+h);c.drawOval(r,p);}
        void rounded(Canvas c,float x,float y,float w,float h,float radius,int col){fill(col);r.set(x,y,x+w,y+h);c.drawRoundRect(r,radius,radius,p);}
        void line(Canvas c,float x,float y,float xx,float yy,float width,int col){fill(col);p.setStrokeWidth(width);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,xx,yy,p);}
        void polygon(Canvas c,int col,float... a){path.reset();path.moveTo(a[0],a[1]);for(int i=2;i<a.length;i+=2)path.lineTo(a[i],a[i+1]);path.close();fill(col);c.drawPath(path,p);}
        void sparkle(Canvas c,float x,float y,float size,int col){polygon(c,col,x,y-size,x+size*.27f,y-size*.27f,x+size,y,x+size*.27f,y+size*.27f,x,y+size,x-size*.27f,y+size*.27f,x-size,y,x-size*.27f,y-size*.27f);}
        void heart(Canvas c,float x,float y,float size,int col){
            path.reset();path.moveTo(x,y+size*.65f);path.cubicTo(x-size*1.5f,y-size*.3f,x-size*.55f,y-size*1.1f,x,y-size*.35f);
            path.cubicTo(x+size*.55f,y-size*1.1f,x+size*1.5f,y-size*.3f,x,y+size*.65f);path.close();fill(col);c.drawPath(path,p);
        }
        void flower(Canvas c,float x,float y,float size,int col){
            for(int i=0;i<5;i++){double a=i*Math.PI*2/5-Math.PI/2;circle(c,x+(float)Math.cos(a)*size,y+(float)Math.sin(a)*size,size*.67f,col);}
            circle(c,x,y,size*.63f,0xffe9a64b);circle(c,x-1,y-1,size*.46f,GOLD);
        }
        void cloud(Canvas c,float x,float y,float s){
            c.save();c.translate(x,y);c.scale(s,s);
            oval(c,-30,0,68,17,0x9cffffff);circle(c,-10,2,13,0xc8ffffff);circle(c,7,-3,18,0xdfffffff);circle(c,25,4,12,0xc8ffffff);
            c.restore();
        }
        void crystal(Canvas c,float x,float foot,float size,int color){
            c.save();c.translate(x,foot);c.scale(size,size);
            polygon(c,0xff846bbd,-19,0,-15,-36,-3,-55,15,-35,19,0);
            polygon(c,color,-16,-1,-12,-34,-3,-52,3,-31,4,-1);
            polygon(c,0xfff5e8ff,-3,-52,15,-35,3,-31,-12,-34);
            polygon(c,0xffd3baf8,3,-31,15,-35,19,0,4,-1);
            sparkle(c,1,-38,4,CREAM);c.restore();
        }
        void tree(Canvas c,float x,float foot,float scale,int theme,int index){
            c.save();c.translate(x,foot);c.scale(scale,scale);
            if(theme==2){
                crystal(c,-8,0,1.05f,0xffbda0ed);crystal(c,19,1,.65f,0xffe8c2f4);
            }else if(theme==1&&index%3==0){
                polygon(c,0xff78b5c8,-27,0,0,-71,26,0);
                polygon(c,0xffcfeaf4,-17,-28,0,-71,17,-28,5,-35,0,-28,-7,-35);
                line(c,0,0,0,-26,3,0xff5b9cad);
            }else{
                int canopy=theme==0?0xff9dd0a8:theme==1?0xffa5d9d8:0xffedafce;
                int dark=theme==0?0xff74b998:theme==1?0xff7ebfbd:0xffd991b5;
                rounded(c,-5,-42,10,42,4,0xffb58491);
                oval(c,-26,-55,52,42,dark);
                circle(c,0,-52,22,canopy);circle(c,-16,-44,15,canopy);circle(c,16,-43,16,canopy);
                // Soft layered lighting and foliage: decorative only.
                fill(canopy);p.setShader(new RadialGradient(-9,-59,33,0xfff1ffe5,canopy,Shader.TileMode.CLAMP));
                if(theme==0)c.drawOval(-21,-72,18,-33,p);
                p.setShader(null);
                oval(c,-15,-66,17,10,0x77ffffff);
                for(int j=0;j<5;j++){float xx=-21+j*10,yy=-41+(j%2)*7;
                    oval(c,xx,yy,8,4,0x3272a96b);
                }
                for(int i=0;i<3;i++){float xx=-15+i*16;float yy=-49-(i%2)*11;
                    if(theme==0)heart(c,xx,yy,4.4f,0xffed88b5);else flower(c,xx,yy,4.2f,CREAM);}
                line(c,-2,-12,-2,-27,2,0xffd5a7a1);
            }
            c.restore();
        }
        void castle(Canvas c,float x,float y,float s,int theme){
            c.save();c.translate(x,y);c.scale(s,s);
            int col=theme==2?0xffa594d2:0xffd98ab2, light=theme==2?0xffe3d6fc:0xfff5c3d7;
            rounded(c,-43,-47,86,47,5,light);
            for(int i=-1;i<=1;i++){
                float tx=i*37;float hh=i==0?73:55;
                rounded(c,tx-13,-hh,26,hh,4,light);
                polygon(c,col,tx-17,-hh,tx,-hh-25,tx+17,-hh);
                rounded(c,tx-4,-hh+13,8,16,4,col);
                line(c,tx,-hh-25,tx,-hh-37,2.3f,col);
                polygon(c,GOLD,tx,-hh-37,tx+13,-hh-33,tx,-hh-28);
            }
            rounded(c,-10,-24,20,24,9,col);c.restore();
        }
        void rabbit(Canvas c,float x,float foot,float size,int direction,float stride,boolean blink,boolean happy){
            c.save();c.translate(x,foot);c.scale(direction*size,size);
            oval(c,-17,-3,34,6,0x28613960);
            circle(c,-13,-16,7,0xffead9e2);circle(c,-14,-17,5.7f,CREAM);
            // Small dress and satchel, with a soft outline and stitching.
            oval(c,-13,-29,28,27,0xffb96a96);oval(c,-12,-28,26,25,0xffef9dc0);
            polygon(c,0xffe275a8,-12,-12,14,-12,15,-4,-14,-4);
            rounded(c,-13,-7+stride,12,7,3,0xffe4cfdc);rounded(c,4,-7-stride,12,7,3,CREAM);
            oval(c,-15,-24,10,13,CREAM);oval(c,8,-25,9,12,CREAM);
            line(c,-7,-24,7,-8,2,0xffc67d9c);rounded(c,2,-17,12,12,4,0xfff4cf73);rounded(c,4,-15,8,4,2,0xffffe9aa);
            // Ears have individual motion but never change the collision bounds.
            float wave=stride*1.5f;
            c.save();c.rotate(-13+wave,-6,-39);oval(c,-12,-66,13,31,0xffd9bfd0);oval(c,-11,-65,11,29,CREAM);oval(c,-8,-61,5,20,PINK);c.restore();
            c.save();c.rotate(14-wave,8,-40);oval(c,2,-67,13,32,0xffd9bfd0);oval(c,3,-66,11,30,CREAM);oval(c,6,-62,5,20,PINK);c.restore();
            oval(c,-17,-47,34,32,0xffddc8d6);oval(c,-16,-46,32,30,CREAM);
            fill(CREAM);p.setShader(new RadialGradient(-7,-39,28,0xffffffff,0xfff2d5e4,Shader.TileMode.CLAMP));
            c.drawOval(-16,-46,16,-16,p);p.setShader(null);
            oval(c,-10,-43,10,4,0x9cffffff);
            if(blink||happy){line(c,-5,-33,-1,-32,1.6f,INK);line(c,7,-33,11,-34,1.6f,INK);}
            else{oval(c,-6,-36,4.8f,6.8f,INK);oval(c,6,-36,5.3f,7,INK);circle(c,-4,-34.5f,1.1f,CREAM);circle(c,8,-34.5f,1.2f,CREAM);}
            oval(c,-12,-29,8,4.5f,0xfffac2d6);oval(c,9,-29,7,4.5f,0xfffac2d6);
            heart(c,4,-28,2.6f,0xffd86b97);line(c,4,-26,4,-24,1.1f,INK);
            line(c,4,-24,1,-23,1.1f,INK);line(c,4,-24,7,-23,1.1f,INK);
            // Pink bow and the little flower emblem.
            polygon(c,ROSE,-10,-43,-20,-49,-20,-38);polygon(c,PINK,-10,-43,0,-49,0,-38);circle(c,-10,-43,3.3f,GOLD);
            flower(c,-6,-15,3.6f,CREAM);
            c.restore();
        }
        void chest(Canvas c,float x,float foot,float size,boolean open){
            c.save();c.translate(x,foot);c.scale(size,size);
            oval(c,-29,-3,58,7,0x24633d61);
            rounded(c,-25,-29,50,29,6,0xffad6c60);rounded(c,-24,-27,48,24,5,0xffe7a66e);
            if(open){
                polygon(c,0xffbe7772,-25,-30,-22,-51,22,-51,25,-30);
                rounded(c,-22,-50,44,15,4,0xffedc078);
                for(int i=0;i<4;i++)sparkle(c,-19+i*13,-37-(i%2)*12,5+i%2,GOLD);
            }else{rounded(c,-26,-37,52,18,7,0xfff8cb83);line(c,-24,-23,24,-23,2,0xffbb795e);}
            for(int i:new int[]{-17,17})rounded(c,i-2,-28,4,26,2,GOLD);
            rounded(c,-7,-26,14,16,4,0xffad775e);rounded(c,-5,-24,10,12,3,GOLD);circle(c,0,-18,2.4f,INK);
            c.restore();
        }
    }
    /** Decorative landscape strip; no clickable or instructional text is embedded in artwork. */
    static final class Banner extends View {
        final int theme;final boolean hero;final Painter art=new Painter();
        Banner(Context c,int theme,boolean hero){super(c);this.theme=Math.max(0,Math.min(3,theme));this.hero=hero;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);float width=getWidth(),height=getHeight();if(width<1||height<1)return;
            int[] palette=PALETTES[theme];float s=height/110f,w=width/s;
            c.save();c.scale(s,s);
            Path clip=new Path();clip.addRoundRect(new RectF(0,0,w,110),14,14,Path.Direction.CW);c.clipPath(clip);
            art.fill(palette[0]);art.p.setShader(new LinearGradient(0,0,0,110,palette[0],palette[1],Shader.TileMode.CLAMP));c.drawRect(0,0,w,110,art.p);art.p.setShader(null);
            art.circle(c,w*.78f,24,17,0xfffff1bb);
            for(int i=0;i<Math.min(18,3+(int)(w/110));i++){float x=i*113+15;art.cloud(c,x,14+(i%2)*8,.55f);art.oval(c,x-60,62-(i%3)*7,146,75,palette[2]);}
            art.oval(c,-15,80,w+30,61,palette[3]);
            art.oval(c,-20,97,w+40,50,palette[4]);
            float mid=w*.5f;
            if(theme==3||hero)art.castle(c,mid+Math.min(95,w*.24f),90,.75f,theme);
            else if(theme==2){art.crystal(c,mid+30,97,1.2f,0xffceb5f4);art.crystal(c,mid+65,97,.7f,0xffd6b5f1);}
            else if(theme==1){art.oval(c,mid-5,80,Math.min(115,w*.44f),30,0xff8bd4e5);art.line(c,mid+5,91,mid+30,91,2,CREAM);art.line(c,mid+31,100,mid+58,100,2,CREAM);}
            for(float x=21;x<w-12;x+=Math.max(88,w/6))if(Math.abs(x-mid)>48)art.tree(c,x,100,.68f,theme,((int)x)%7);
            art.rabbit(c,hero?mid-Math.min(90,w*.22f):mid-18,100,1.18f,1,0,false,hero);
            art.chest(c,hero?mid+Math.min(9,w*.03f):mid+34,100,.48f,false);
            for(int i=0;i<6;i++)art.sparkle(c,mid-62+i*25,28+(i%3)*13,3+i%3,CREAM);
            c.restore();
        }
    }
}
