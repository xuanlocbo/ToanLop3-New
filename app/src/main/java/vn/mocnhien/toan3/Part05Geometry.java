package vn.mocnhien.toan3;
import org.json.*;
import java.util.*;

/** Coordinate checks for the reviewed grid diagrams. No question data or grading rules are changed. */
final class Part05Geometry {
    private Part05Geometry(){}
    static double[] point(JSONObject v,String key){
        JSONArray a=Json.obj(v,"points").optJSONArray(key);
        if(a==null||a.length()!=2)return null;
        double x=a.optDouble(0),y=a.optDouble(1);
        return Double.isFinite(x)&&Double.isFinite(y)?new double[]{x,y}:null;
    }
    static boolean midpoint(JSONObject v,String a,String b,String p){
        double[] x=point(v,a),y=point(v,b),z=point(v,p);
        return x!=null&&y!=null&&z!=null&&!a.equals(b)&&!a.equals(p)&&!b.equals(p)
            &&Math.abs(2*z[0]-x[0]-y[0])<1e-7&&Math.abs(2*z[1]-x[1]-y[1])<1e-7;
    }
    // Merge coincident rays: opposite rays are not counted as elementary corners.
    static List<double[]> rays(JSONObject v,String vertex){
        List<double[]> result=new ArrayList<>();
        double[] o=point(v,vertex);if(o==null)return result;
        for(Object x:Json.list(Json.arr(v,"segments"))){
            JSONArray s=(JSONArray)x;String a=s.optString(0),b=s.optString(1);
            String other=a.equals(vertex)?b:b.equals(vertex)?a:"";
            if(other.isEmpty())continue;
            double[] p=point(v,other);if(p==null)continue;
            double dx=p[0]-o[0],dy=p[1]-o[1],len=Math.hypot(dx,dy);
            if(len<1e-8)continue;dx/=len;dy/=len;
            boolean duplicate=false;for(double[] r:result)if(Math.hypot(dx-r[0],dy-r[1])<1e-7)duplicate=true;
            if(!duplicate)result.add(new double[]{dx,dy});
        }
        return result;
    }
    static int angles(JSONObject v,String vertex,boolean right){
        List<double[]> r=rays(v,vertex);int count=0;
        for(int i=0;i<r.size();i++)for(int j=i+1;j<r.size();j++){
            double[] a=r.get(i),b=r.get(j);
            double cross=a[0]*b[1]-a[1]*b[0],dot=a[0]*b[0]+a[1]*b[1];
            if(Math.abs(cross)<1e-7)continue; // ignore straight and zero angles
            if((Math.abs(dot)<1e-7)==right)count++;
        }
        return count;
    }
    static int allRight(JSONObject v){
        int sum=0;for(String p:Json.keys(Json.obj(v,"points")))sum+=angles(v,p,true);return sum;
    }
    static boolean drawnRadius(JSONObject v,String s){
        if(s.length()!=2)return false;String center=v.optString("center","O");
        String a=s.substring(0,1),b=s.substring(1);
        if(!a.equals(center)&&!b.equals(center))return false;
        if(!Json.obj(v,"pointAnglesDeg").has(a.equals(center)?b:a))return false;
        for(Object raw:Json.list(Json.arr(v,"segments"))){
            JSONArray t=(JSONArray)raw;
            if((a.equals(t.optString(0))&&b.equals(t.optString(1)))||(b.equals(t.optString(0))&&a.equals(t.optString(1))))return true;
        }
        return false;
    }
    static boolean drawnDiameter(JSONObject v,String s){
        if(s.length()!=2)return false;String center=v.optString("center","O"),a=s.substring(0,1),b=s.substring(1);
        JSONObject angles=Json.obj(v,"pointAnglesDeg");if(!angles.has(a)||!angles.has(b))return false;
        double diff=Math.abs(angles.optDouble(a)-angles.optDouble(b))%360;
        return Math.abs(diff-180)<1e-7&&drawnRadius(v,center+a)&&drawnRadius(v,center+b);
    }
    static int paintedCount(JSONArray d,int target){
        int a=d.optInt(0),b=d.optInt(1),c=d.optInt(2);
        if(a<1||b<1||c<1||a>50||b>50||c>50)throw new IllegalArgumentException("dimensions");
        int result=0;
        for(int x=0;x<a;x++)for(int y=0;y<b;y++)for(int z=0;z<c;z++){
            int faces=(x==0?1:0)+(x==a-1?1:0)+(y==0?1:0)+(y==b-1?1:0)+(z==0?1:0)+(z==c-1?1:0);
            if(faces==target)result++;
        }
        return result;
    }
    // Bounds include the entire circumcircle, not just the rectangle vertices.
    static double[] bounds(JSONObject v){
        JSONObject points=Json.obj(v,"points");
        double minX=Double.POSITIVE_INFINITY,minY=minX,maxX=Double.NEGATIVE_INFINITY,maxY=maxX;
        for(String name:Json.keys(points)){
            double[] p=point(v,name);if(p==null)continue;
            minX=Math.min(minX,p[0]);maxX=Math.max(maxX,p[0]);minY=Math.min(minY,p[1]);maxY=Math.max(maxY,p[1]);
        }
        if(!Double.isFinite(minX))return new double[]{0,0,1,1};
        if("geometry_circle_grid".equals(v.optString("type"))){
            double[] o=point(v,v.optString("circleCenter")),a=point(v,Json.arr(v,"circleThrough").optString(0));
            if(o!=null&&a!=null){
                double radius=Math.hypot(a[0]-o[0],a[1]-o[1]);
                minX=Math.min(minX,o[0]-radius);maxX=Math.max(maxX,o[0]+radius);
                minY=Math.min(minY,o[1]-radius);maxY=Math.max(maxY,o[1]+radius);
            }
        }
        return new double[]{minX,minY,maxX,maxY};
    }
}
