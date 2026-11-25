package vn.mocnhien.toan3;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import org.json.*;
import java.util.*;

/** Draw only explicit teaching models. Never serialize the answer/check object into a view. */
public final class MathDiagram extends View {
    private final JSONObject v,d;private final String type;
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    private Canvas c;
    static final int INK=0xff643550,PINK=0xffcd518b,BLUE=0xff8bd9eb,MINT=0xffbde9c9,GOLD=0xffffd469,PALE=0xfffff0f6,GRID=0xffdecfd7,WHITE=0xffffffff;
    static final Set<String> TYPES=new HashSet<>(Arrays.asList("ruler","ruler_object","cup","cups","pour","thermometer","clinical_thermometer","match_thermometers","dial_scale","scales","balance","balance_sequence","group_scale","geometry_grid","geometry_circle_grid","circle_diagram","array_grid","solid_stack","painted_cuboid","inscribed_circle","target_score","shape_values","bar_compare","bar_multiple","polyline","route_length","dice_board","temperature_order","object_lengths","compare_length","object_weights"));
    public MathDiagram(Context ctx,JSONObject visual,JSONObject draft){super(ctx);v=visual;d=draft;type=v.optString("type");setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);setContentDescription("Hình minh họa bài toán");}
    public static boolean supports(String s){return TYPES.contains(s);}
    void color(int a){p.setColor(a);p.setStyle(Paint.Style.FILL);}
    void rect(float x,float y,float w,float h,int col){color(col);c.drawRoundRect(x,y,x+w,y+h,8,8,p);}
    void circle(float x,float y,float r,int col){color(col);c.drawCircle(x,y,r,p);}
    void oval(float x,float y,float w,float h,int col){color(col);c.drawOval(x,y,x+w,y+h,p);}
    void line(float x,float y,float xx,float yy,float width,int col){color(col);p.setStrokeWidth(width);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x,y,xx,yy,p);}
    void label(String s,float x,float y,float size,int col){color(col);p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));p.setTextSize(size);p.setTextAlign(Paint.Align.CENTER);c.drawText(s,x,y,p);}
    void polygon(float[] coords,int col){Path path=new Path();path.moveTo(coords[0],coords[1]);for(int i=2;i<coords.length;i+=2)path.lineTo(coords[i],coords[i+1]);path.close();color(col);c.drawPath(path,p);p.setStyle(Paint.Style.STROKE);p.setColor(INK);p.setStrokeWidth(1.5f);c.drawPath(path,p);p.setStyle(Paint.Style.FILL);}
    @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);c=canvas;c.save();float scale=Math.min(getWidth()/900f,getHeight()/320f);c.translate((getWidth()-900*scale)/2,(getHeight()-320*scale)/2);c.scale(scale,scale);rect(0,0,900,320,PALE);
        switch(type){
            case "ruler":ruler();break;
            case "ruler_object":if(v.has("lengthMm")){JSONObject r=new JSONObject();Json.put(r,"maxMm",40);JSONObject seg=new JSONObject();Json.put(seg,"startMm",0);Json.put(seg,"endMm",v.optInt("lengthMm"));drawRuler(r,seg); }break;
            case "cup":case "cups":case "pour":cups();break;
            case "thermometer":case "clinical_thermometer":thermo(v,450,0,1);break;
            case "match_thermometers":{int i=0;for(Object o:Json.list(Json.arr(v,"thermometers"))){JSONObject t=(JSONObject)o;JSONObject x=new JSONObject();Json.put(x,"rangeC",new JSONArray(Arrays.asList(-10,50)));Json.put(x,"valueC",t.optInt("approxC"));Json.put(x,"object",t.optString("id"));thermo(x,170+i++*280,0,.9f);}break;}
            case "dial_scale":dial(v,450,150,1.05f);break;
            case "scales":{JSONArray a=Json.arr(v,"scales");for(int i=0;i<a.length();i++)dial(a.optJSONObject(i),225+i*450,150,.9f);break;}
            case "balance":case "group_scale":scale(v,450,0,1.0f);break;
            case "balance_sequence":{JSONArray a=Json.arr(v,"scales");float step=900f/Math.max(1,a.length());for(int i=0;i<a.length();i++)scale(a.optJSONObject(i),step*(i+.5f),20,Math.min(.9f,step/450));break;}
            case "geometry_grid":case "geometry_circle_grid":geometry();break;
            case "circle_diagram":circleDiagram();break;
            case "array_grid":arrayGrid();break;
            case "solid_stack":solidStack();break;
            case "painted_cuboid":{JSONArray a=Json.arr(v,"dimensions");cubeBlock(a.optInt(0),a.optInt(1),a.optInt(2),450,245,40,true,null);break;}
            case "inscribed_circle":{rect(320,20,260,260,MINT);circle(450,150,130,WHITE);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setColor(PINK);c.drawCircle(450,150,130,p);p.setStyle(Paint.Style.FILL);circle(450,150,4,INK);label(v.optInt("squareSideCm")+" cm",450,308,25,INK);break;}
            case "target_score":targets();break;
            case "shape_values":shapes();break;
            case "bar_compare":case "bar_multiple":bars();break;
            case "polyline":polyline();break;
            case "route_length":{JSONArray a=Json.arr(v,"segments");if(a.length()>0){line(90,160,810,160,8,INK);for(int i=0;i<=a.length();i++)circle(90+i*720f/a.length(),160,9,PINK);for(int i=0;i<a.length();i++)label(a.optInt(i)+" "+v.optString("unit"),90+(i+.5f)*720/a.length(),135,25,INK);}else{label(v.optInt("initialMm")+" mm",450,70,30,INK);rect(100,110,700,45,GOLD);label("Cắt: "+Json.list(Json.arr(v,"removedMm")),450,220,25,INK);}break;}
            case "dice_board":board();break;
            case "temperature_order":case "object_lengths":case "compare_length":case "object_weights":objects();break;
        }c.restore();
    }
    void ruler(){drawRuler(v,Json.obj(v,"segment"));}
    void drawRuler(JSONObject r,JSONObject seg){
        int max=Math.max(10,r.optInt("maxMm",50));float start=65,step=770f/max;
        rect(40,140,820,112,GOLD);
        for(int i=0;i<=max;i++){float x=start+i*step;line(x,140,x,i%10==0?205:i%5==0?190:176,1.8f,INK);if(i%10==0||max<=15&&i%5==0)label(""+i,x,235,21,INK);}
        float a=start+(float)seg.optDouble("startMm",0)*step,b=start+(float)seg.optDouble("endMm",0)*step;
        line(a,98,b,98,8,PINK);line(a,108,a,140,1.8f,INK);line(b,108,b,140,1.8f,INK);
        JSONArray labels=Json.arr(r,"labels");label(labels.optString(0,"A"),a,77,25,INK);label(labels.optString(1,"B"),b,77,25,INK);label("mm",828,279,23,INK);
    }
    void cups(){
        JSONArray a=Json.arr(v,"cups");
        if(type.equals("cup")){a=new JSONArray();a.put(v);}
        if(type.equals("pour")){a=new JSONArray();JSONArray caps=Json.arr(v,"capacitiesMl");int[] state={0,0};JSONArray acts=Json.arr(d,"actions");for(Object o:Json.list(acts)){JSONObject t=(JSONObject)o;int i=t.optInt("cup"),f=t.optInt("from"),to=t.optInt("to");if(t.optString("type").equals("fill"))state[i]=caps.optInt(i);else if(t.optString("type").equals("empty"))state[i]=0;else if(t.optString("type").equals("pour")){int m=Math.min(state[f],caps.optInt(to)-state[to]);state[f]-=m;state[to]+=m;}}for(int i=0;i<2;i++){JSONObject q=new JSONObject();Json.put(q,"id",i==0?"A":"B");Json.put(q,"capacityMl",caps.optInt(i));Json.put(q,"valueMl",state[i]);a.put(q);}}
        int max=1;for(Object o:Json.list(a)){JSONObject q=(JSONObject)o;max=Math.max(max,Math.max(q.optInt("capacityMl"),q.optInt("valueMl")));}
        float step=900f/Math.max(1,a.length());for(int i=0;i<a.length();i++){
            JSONObject t=a.optJSONObject(i);int cap=t.optInt("capacityMl",max),value=t.optInt("valueMl"),tick=v.optInt("tickMl",cap>500?100:50);
            float x=step*(i+.5f)-65,h=210f*cap/max,top=260-h;rect(x-5,top-5,130,h+10,INK);rect(x,top,120,h,WHITE);
            float fill=h*value/Math.max(1,cap);if(fill>0)rect(x,260-fill,120,fill,BLUE);
            int labelStep=tick;while(cap/Math.max(1,labelStep)>10)labelStep*=2;
            for(int k=0;k<=cap;k+=Math.max(1,tick)){float y=260-h*k/cap;line(x+90,y,x+120,y,2,INK);if(k%labelStep==0)label(""+k,x+155,y+6,17,INK);}
            label(t.optString("id","Ca"),x+60,295,24,INK);
            if(type.equals("pour"))label(value+" / "+cap+" ml",x+60,30,22,PINK);
        }
        if(!type.equals("pour"))label("ml",865,305,18,INK);
    }
    void thermo(JSONObject t,float x,float y,float z){
        c.save();c.translate(x,y);c.scale(z,z);JSONArray range=Json.arr(t,"rangeC");int low=range.optInt(0,-30),high=range.optInt(1,50);float val=(float)t.optDouble("valueC");
        if(high<=low){c.restore();return;}
        rect(-23,25,46,238,INK);rect(-17,31,34,226,WHITE);
        float top=238-(val-low)*196/(high-low);rect(-8,top,16,248-top,PINK);circle(0,263,25,INK);circle(0,263,19,PINK);
        float small=(float)t.optDouble("stepC",high-low<=10?.1:1);int major=t.optInt("majorTickC",high-low<=10?1:5);
        int count=Math.round((high-low)/small);for(int i=0;i<=count;i++){float n=low+i*small,yy=238-(n-low)*196/(high-low);boolean m=Math.abs(n/major-Math.round(n/major))<.001;line(23,yy,m?46:34,yy,2,INK);if(m)label(""+Math.round(n),73,yy+6,18,INK);}
        label("°C",-48,45,20,INK);label(t.optString("object",""),0,310,23,INK);c.restore();
    }
    void dial(JSONObject t,float x,float y,float z){
        c.save();c.translate(x,y);c.scale(z,z);
        int max=t.optInt("maxG",1000),tick=t.optInt("majorTickG",250),minor=t.optInt("minorTickG",50);
        if(max<=0||minor<=0||tick<=0){c.restore();return;}
        rect(-105,-117,210,220,MINT);circle(0,-5,94,INK);circle(0,-5,88,WHITE);
        for(int k=0;k<max;k+=minor){double a=2*Math.PI*k/max-Math.PI/2;float co=(float)Math.cos(a),si=(float)Math.sin(a);line(co*(k%tick==0?68:77),-5+si*(k%tick==0?68:77),co*86,-5+si*86,2,INK);if(k%tick==0)label(""+k,co*49,-5+si*49+5,16,INK);}
        double angle=t.optInt("valueG")*2*Math.PI/max-Math.PI/2;line(0,-5,(float)Math.cos(angle)*72,-5+(float)Math.sin(angle)*72,5,PINK);circle(0,-5,7,INK);label("g",0,40,17,INK);label(t.optString("object"),0,138,24,INK);c.restore();
    }
    List<String> pan(JSONObject t,String side){
        List<String> a=new ArrayList<>();for(Object w:Json.list(t.optJSONArray(side+"WeightsG")))a.add(w+" g");
        for(Object o:Json.list(t.optJSONArray(side+"Objects")))a.add(String.valueOf(o));
        if(type.equals("group_scale")&&side.equals("left")&&t.has("totalG"))a.add(t.optInt("totalG")+" g");
        if(type.equals("group_scale")&&side.equals("right")&&t.has("rightObject"))for(int i=0;i<t.optInt("count");i++)a.add(t.optString("rightObject")+(t.has("eachG")?" "+t.optInt("eachG")+"g":""));
        if(type.equals("group_scale")&&side.equals("left")&&t.has("total"))a.add(t.optInt("total")+" "+t.optString("unit"));
        if(type.equals("group_scale")&&side.equals("right")&&t.has("object"))for(int i=0;i<t.optInt("count");i++)a.add(t.optString("object"));
        return a;
    }
    void scale(JSONObject t,float x,float y,float z){
        c.save();c.translate(x,y);c.scale(z,z);
        int dy=t.optString("relation").equals("right_heavier")?40:0;
        polygon(new float[]{-25,280,25,280,0,100},GOLD);line(-155,100-dy,155,100+dy,10,INK);circle(0,100,12,PINK);
        for(int i=0;i<2;i++){float xx=i==0?-155:155,yy=100+(i==0?-dy:dy);line(xx,yy,xx,yy+64,3,INK);line(xx-105,yy+64,xx+105,yy+64,6,INK);
            List<String>a=pan(t,i==0?"left":"right");int n=Math.max(1,a.size());float w=200f/n;
            for(int j=0;j<a.size();j++){
                float bx=xx-100+j*w;rect(bx+2,yy+12,w-4,50,i==0?GOLD:MINT);
                String name=a.get(j),id=ArtPack.object(getContext(),name);
                boolean drawn=w>=40&&ArtPack.fit(getContext(),c,id,bx+5,yy+13,w-10,31);
                label(name,bx+w/2,drawn?yy+57:yy+44,Math.min(drawn?14:20,(w-6)/Math.max(2,name.length())*1.7f),INK);
            }
        }c.restore();
    }
    void geometry(){
        JSONObject pts=Json.obj(v,"points");List<String> keys=Json.keys(pts);if(keys.isEmpty())return;
        double minX=1e9,maxX=-1e9,minY=1e9,maxY=-1e9;for(String k:keys){JSONArray t=pts.optJSONArray(k);minX=Math.min(minX,t.optDouble(0));maxX=Math.max(maxX,t.optDouble(0));minY=Math.min(minY,t.optDouble(1));maxY=Math.max(maxY,t.optDouble(1));}
        float s=(float)Math.min(730/Math.max(1,maxX-minX),240/Math.max(1,maxY-minY));
        float ox=(float)(450-(minX+maxX)*s/2),oy=(float)(160-(minY+maxY)*s/2);
        Map<String,float[]> pp=new HashMap<>();for(String k:keys){JSONArray t=pts.optJSONArray(k);pp.put(k,new float[]{ox+(float)t.optDouble(0)*s,oy+(float)t.optDouble(1)*s});}
        for(int i=(int)Math.floor(minX)-1;i<=maxX+1;i++)line(ox+i*s,15,ox+i*s,305,1,GRID);
        for(int i=(int)Math.floor(minY)-1;i<=maxY+1;i++)line(60,oy+i*s,840,oy+i*s,1,GRID);
        if(type.equals("geometry_circle_grid")){float[] center=pp.get(v.optString("circleCenter")),a=pp.get(Json.arr(v,"circleThrough").optString(0));if(center!=null&&a!=null){p.setColor(PINK);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);c.drawCircle(center[0],center[1],(float)Math.hypot(a[0]-center[0],a[1]-center[1]),p);p.setStyle(Paint.Style.FILL);}}
        for(Object o:Json.list(Json.arr(v,"segments"))){JSONArray t=(JSONArray)o;float[] a=pp.get(t.optString(0)),b=pp.get(t.optString(1));line(a[0],a[1],b[0],b[1],4,INK);}
        JSONArray hi=Json.arr(v,"highlightSegment");if(hi.length()==2){float[] a=pp.get(hi.optString(0)),b=pp.get(hi.optString(1));if(a!=null&&b!=null)line(a[0],a[1],b[0],b[1],5,PINK);}
        for(String k:keys){float[] a=pp.get(k);circle(a[0],a[1],5,INK);label(k,a[0]+(a[0]<450?-15:15),a[1]-10,23,INK);}
    }
    void circleDiagram(){
        float x=450,y=159,r=126;circle(x,y,r,WHITE);p.setColor(INK);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(4);c.drawCircle(x,y,r,p);p.setStyle(Paint.Style.FILL);
        JSONObject angles=Json.obj(v,"pointAnglesDeg");Map<String,float[]> pp=new HashMap<>();pp.put(v.optString("center","O"),new float[]{x,y});
        for(String k:Json.keys(angles)){double a=Math.toRadians(angles.optDouble(k));pp.put(k,new float[]{x+r*(float)Math.cos(a),y+r*(float)Math.sin(a)});}
        for(Object o:Json.list(Json.arr(v,"segments"))){JSONArray s=(JSONArray)o;float[] a=pp.get(s.optString(0)),b=pp.get(s.optString(1));line(a[0],a[1],b[0],b[1],4,PINK);}
        for(String k:pp.keySet()){float[] a=pp.get(k);circle(a[0],a[1],4,INK);float dx=(a[0]-x)/r,dy=(a[1]-y)/r;label(k,a[0]+dx*20+(k.equals("O")?15:0),a[1]+dy*20+7,22,INK);}
    }
    void arrayGrid(){int rows=v.optInt("rows"),cols=v.optInt("columns");if(rows<=0||cols<=0)return;float s=Math.min(78,Math.min(750f/cols,250f/rows));for(int r=0;r<rows;r++)for(int k=0;k<cols;k++){float x=450+(k-(cols-1)/2f)*s,y=160+(r-(rows-1)/2f)*s;circle(x,y,s*.35f,(r+k)%2==0?PINK:GOLD);circle(x-s*.1f,y-s*.12f,s*.08f,WHITE);}}
    void cube(float x,float y,float s,int col){polygon(new float[]{x,y,x+s,y+s*.38f,x+s,y+s*1.38f,x,y+s},col);polygon(new float[]{x+s,y+s*.38f,x+s*2,y,x+s*2,y+s,x+s,y+s*1.38f},0xffdb9ab6);polygon(new float[]{x,y,x+s,y-s*.38f,x+s*2,y,x+s,y+s*.38f},GOLD);}
    void cubeBlock(int nx,int ny,int nz,float ox,float oy,float s,boolean painted,JSONArray extras){
        if(nx<1||ny<1||nz<1)return;
        for(int z=0;z<nz;z++)for(int j=0;j<ny;j++)for(int i=0;i<nx;i++){
            float x=ox+(i-j)*s-s,y=oy+(i+j-(nx+ny-2)/2f)*s*.38f-z*s;
            cube(x,y,s,painted?0xff8dd6b2:(i+j)%2==0?0xffd29be6:0xffffd46a);
        }
        for(Object o:Json.list(extras)){JSONArray a=(JSONArray)o;int i=a.optInt(0),j=a.optInt(1),z=a.optInt(2);cube(ox+(i-j)*s-s,oy+(i+j-(nx+ny-2)/2f)*s*.38f-z*s,s,0xffd29be6);}
    }
    void solidStack(){JSONArray dim=Json.arr(Json.obj(v,"base"),"dimensions");cubeBlock(dim.optInt(0),dim.optInt(1),dim.optInt(2),450,205,30,false,Json.arr(Json.obj(v,"extraCubes"),"positions"));
        JSONArray a=Json.arr(v,"above");float y=170;for(int i=0;i<a.length();i++){JSONObject t=a.optJSONObject(i);float w=160-i*35;if(t.optString("shape").equals("sphere"))circle(450,y-27,27,PINK);else{rect(450-w/2,y-41,w,41,i%2==0?BLUE:PINK);oval(450-w/2,y-52,w,22,i%2==0?0xffb9eff7:0xfff0a4c8);oval(450-w/2,y-11,w,22,i%2==0?BLUE:PINK);}y-=51;}}
    void targets(){JSONArray a=Json.arr(v,"players");float step=900f/Math.max(1,a.length());for(int i=0;i<a.length();i++){JSONObject t=a.optJSONObject(i);float x=step*(i+.5f);circle(x,150,93,GOLD);circle(x,150,44,PINK);int j=0;for(Object zone:Json.list(t.optJSONArray("zones"))){String z=String.valueOf(zone);float rr=z.equals("outside")?112:z.equals("inner")||z.equals("red")?27:69;double angle=-1.1+j++*2.09;circle(x+rr*(float)Math.cos(angle),150+rr*(float)Math.sin(angle),10,INK);}label(t.optString("name"),x,30,24,INK);label(t.isNull("score")?"? điểm":t.optInt("score")+" điểm",x,293,25,INK);}}
    void shapes(){JSONArray a=Json.arr(v,"examples");List<JSONObject> all=new ArrayList<>();for(Object o:Json.list(a))all.add((JSONObject)o);all.add(Json.obj(v,"target"));float step=900f/all.size();for(int i=0;i<all.size();i++){JSONObject t=all.get(i);float x=step*(i+.5f);int sq=t.optInt("squares"),ci=t.optInt("circles"),n=sq+ci;for(int j=0;j<n;j++){double angle=2*Math.PI*j/n;float xx=x+75*(float)Math.cos(angle),yy=155+75*(float)Math.sin(angle);if(j<sq)rect(xx-15,yy-15,30,30,MINT);else circle(xx,yy,16,PINK);}label(t.has("total")?""+t.optInt("total"):"?",x,166,30,INK);}}
    void bars(){JSONArray ls=Json.arr(v,"labels");int mult=v.optInt("multiple",1);float base=140,second=base;if(type.equals("bar_multiple")){base=540f/Math.max(1,mult);second=base*mult;}else if(v.has("lengths")){JSONArray n=Json.arr(v,"lengths");int a=n.optInt(0),b=n.optInt(1);base=500f*a/Math.max(a,b);second=500f*b/Math.max(a,b);}else{int a=v.optInt("base",1),b=a+v.optInt("extra");base=500f*a/Math.max(a,b);second=500f*b/Math.max(a,b);}
        label(ls.optString(0,"Nhóm 1"),125,100,22,INK);rect(245,62,base,48,MINT);label(ls.optString(1,"Nhóm 2"),125,202,22,INK);rect(245,164,second,48,GOLD);
        if(type.equals("bar_multiple"))for(int j=1;j<mult;j++)line(245+j*base,164,245+j*base,212,2,INK);
        if(v.has("base"))label(v.optInt("base")+" "+v.optString("unit"),245+base/2,94,22,INK);if(v.has("total"))label(v.optInt("total")+" "+v.optString("unit"),245+second/2,195,22,INK);
        if(v.has("lengths")){JSONArray a=Json.arr(v,"lengths");label(a.optInt(0)+" "+v.optString("unit"),245+base/2,94,22,INK);label(a.optInt(1)+" "+v.optString("unit"),245+second/2,195,22,INK);}
        if(v.has("extra"))label((v.optInt("extra")>=0?"+":"")+v.optInt("extra")+" "+v.optString("unit"),760,260,25,PINK);
    }
    void polyline(){JSONArray a=Json.arr(v,"segments");int n=a.length();if(n==0)return;for(int i=0;i<n;i++){float x=100+i*700f/n,y=i%2==0?215:100,xx=100+(i+1)*700f/n,yy=i%2==0?100:215;JSONObject t=a.optJSONObject(i);line(x,y,xx,yy,5,PINK);circle(x,y,5,INK);circle(xx,yy,5,INK);String name=t.optString("name");if(name.length()>1){label(name.substring(0,1),x,y+27,22,INK);label(name.substring(1),xx,yy+27,22,INK);}String len=t.has("length")?t.optInt("length")+" "+v.optString("unit"):t.has("factor")?"Gấp "+t.optInt("factor")+" lần":"Ngắn hơn "+t.optInt("shorterBy")+" "+v.optString("unit");label(len,(x+xx)/2,(y+yy)/2-23,23,INK);}}
    void objects(){JSONArray a=Json.arr(v,"objects");float step=900f/Math.max(1,a.length());for(int i=0;i<a.length();i++){JSONObject t=a.optJSONObject(i);float x=step*(i+.5f);rect(x-80,72,160,132,i%2==0?MINT:GOLD);String name=t.optString("name",t.optString("label"));boolean drawn=ArtPack.fit(getContext(),c,ArtPack.object(getContext(),name),x-65,77,130,99);label(name,x,drawn?195:140,Math.min(drawn?20:25,250f/Math.max(4,name.length())),INK);String val=t.has("length")?t.optInt("length")+" "+v.optString("unit"):t.has("value")?t.optInt("value")+" "+t.optString("unit",v.optString("unit")):t.optString("id");label(val,x,250,23,INK);}}
    void board(){JSONArray a=Json.arr(v,"board");for(Object o:Json.list(a)){JSONObject t=(JSONObject)o;JSONArray xy=Json.arr(t,"pos");float x=220+xy.optInt(0)*76,y=18+xy.optInt(1)*44;int col=t.optString("type").equals("animal")?MINT:t.optInt("index")==0?GOLD:WHITE;rect(x,y,73,40,col);String s=t.optString("type").equals("math")?t.optInt("a")+t.optString("o")+t.optInt("b"):t.optInt("index")==0?"ĐÍCH":t.optString("animal");label(s,x+36,y+25,s.length()>9?10:13,INK);int current=d.optInt("boardPosition",0);if(t.optInt("index")==current)circle(x+8,y+8,7,PINK);if(t.optString("type").equals("animal"))label("→"+t.optInt("jump"),x+36,y+38,10,INK);}}
}
