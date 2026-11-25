package vn.mocnhien.toan3;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import org.json.*;

/** Fit the full coordinate diagram, including the circumcircle and labels, without answer overlays. */
final class Part05GeometryView extends View {
    private final JSONObject visual;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final int INK=0xff57364d,PINK=0xffb93e7a,GRID=0xffecd9e6;
    Part05GeometryView(Context c,JSONObject v){
        super(c);visual=v;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        setContentDescription("Hình minh họa hình học trên lưới");
    }
    static boolean supports(JSONObject v){String t=v.optString("type");return "geometry_grid".equals(t)||"geometry_circle_grid".equals(t);}
    private void line(Canvas c,float x,float y,float xx,float yy,int color,float stroke){
        paint.setColor(color);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(stroke);paint.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,xx,yy,paint);
    }
    @Override protected void onDraw(Canvas c){
        super.onDraw(c);float w=getWidth(),h=getHeight(),density=getResources().getDisplayMetrics().density;
        if(w<20||h<20)return;
        double[] bounds=Part05Geometry.bounds(visual);
        float pad=Math.min(32*density,Math.min(w,h)*.17f);
        float scale=(float)Math.min((w-2*pad)/Math.max(1,bounds[2]-bounds[0]),(h-2*pad)/Math.max(1,bounds[3]-bounds[1]));
        float ox=(float)(w/2-(bounds[0]+bounds[2])*scale/2),oy=(float)(h/2-(bounds[1]+bounds[3])*scale/2);
        c.drawColor(0xfffff4f8);
        for(int i=(int)Math.floor(bounds[0]);i<=Math.ceil(bounds[2]);i++)line(c,ox+i*scale,pad/2,ox+i*scale,h-pad/2,GRID,density);
        for(int i=(int)Math.floor(bounds[1]);i<=Math.ceil(bounds[3]);i++)line(c,pad/2,oy+i*scale,w-pad/2,oy+i*scale,GRID,density);
        if("geometry_circle_grid".equals(visual.optString("type"))){
            double[] o=Part05Geometry.point(visual,visual.optString("circleCenter")),a=Part05Geometry.point(visual,Json.arr(visual,"circleThrough").optString(0));
            if(o!=null&&a!=null){
                paint.setColor(PINK);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2.5f*density);
                c.drawCircle(ox+(float)o[0]*scale,oy+(float)o[1]*scale,(float)Math.hypot(a[0]-o[0],a[1]-o[1])*scale,paint);
            }
        }
        for(Object raw:Json.list(Json.arr(visual,"segments"))){
            JSONArray pair=(JSONArray)raw;drawSegment(c,pair,ox,oy,scale,INK,2.7f*density);
        }
        JSONArray hi=Json.arr(visual,"highlightSegment");if(hi.length()==2)drawSegment(c,hi,ox,oy,scale,PINK,3.5f*density);
        for(String key:Json.keys(Json.obj(visual,"points"))){
            double[] p=Part05Geometry.point(visual,key);if(p==null)continue;
            float x=ox+(float)p[0]*scale,y=oy+(float)p[1]*scale;
            paint.setStyle(Paint.Style.FILL);paint.setColor(INK);c.drawCircle(x,y,3*density,paint);
            paint.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));paint.setTextSize(16*density);paint.setTextAlign(Paint.Align.CENTER);
            float ly=y-9*density;if(ly<18*density)ly=y+20*density;
            // Halo keeps point names readable over lines.
            paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(3*density);paint.setColor(0xfffff4f8);c.drawText(key,x,ly,paint);
            paint.setStyle(Paint.Style.FILL);paint.setColor(INK);c.drawText(key,x,ly,paint);
        }
    }
    private void drawSegment(Canvas c,JSONArray pair,float ox,float oy,float scale,int color,float stroke){
        double[] a=Part05Geometry.point(visual,pair.optString(0)),b=Part05Geometry.point(visual,pair.optString(1));
        if(a!=null&&b!=null)line(c,ox+(float)a[0]*scale,oy+(float)a[1]*scale,ox+(float)b[0]*scale,oy+(float)b[1]*scale,color,stroke);
    }
}
