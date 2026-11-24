package vn.mocnhien.toan3;
import android.content.Context;
import android.graphics.*;
import android.view.*;
import org.json.*;
import java.util.*;

/** Click grid intersections in order. The sample and editable grid use the same cell scale. */
final class GridCanvas extends View {
    interface Listener{void point(int x,int y);}
    final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    final JSONArray vertices;final boolean closed;final Listener listener;
    final int cols=10,rows=7;
    float step,ox,oy;
    GridCanvas(Context c,JSONArray points,boolean closed,Listener listener){super(c);vertices=points;this.closed=closed;this.listener=listener;setContentDescription(listener==null?"Hình mẫu":"Lưới vẽ hình, chạm từng giao điểm");setClickable(listener!=null);}
    void line(Canvas c,float a,float b,float x,float y,int color,float w){p.setColor(color);p.setStrokeWidth(w);c.drawLine(a,b,x,y,p);}
    @Override protected void onDraw(Canvas c){super.onDraw(c);c.drawColor(0xfffff0f6);step=Math.min((getWidth()-24f)/cols,(getHeight()-24f)/rows);if(step<=0)return;ox=(getWidth()-cols*step)/2;oy=(getHeight()-rows*step)/2;
        for(int i=0;i<=cols;i++)line(c,ox+i*step,oy,ox+i*step,oy+rows*step,0xffd5becb,1);
        for(int j=0;j<=rows;j++)line(c,ox,oy+j*step,ox+cols*step,oy+j*step,0xffd5becb,1);
        for(int i=0;i<vertices.length();i++){JSONArray a=vertices.optJSONArray(i);if(a==null)continue;float x=ox+a.optInt(0)*step,y=oy+a.optInt(1)*step;
            if(i>0){JSONArray b=vertices.optJSONArray(i-1);line(c,ox+b.optInt(0)*step,oy+b.optInt(1)*step,x,y,0xffc94a83,4);}
            if(closed&&i==vertices.length()-1){JSONArray b=vertices.optJSONArray(0);line(c,x,y,ox+b.optInt(0)*step,oy+b.optInt(1)*step,0xffc94a83,4);}
            p.setColor(i==0?0xff248450:0xff643550);c.drawCircle(x,y,5,p);}
    }
    @Override public boolean onTouchEvent(MotionEvent e){if(listener==null||step<=0)return false;if(e.getActionMasked()==MotionEvent.ACTION_UP){int x=Math.round((e.getX()-ox)/step),y=Math.round((e.getY()-oy)/step);if(x>=0&&x<=cols&&y>=0&&y<=rows){listener.point(x,y);performClick();}return true;}return true;}
    @Override public boolean performClick(){super.performClick();return true;}
}
