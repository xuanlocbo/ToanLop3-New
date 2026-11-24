package vn.mocnhien.toan3;
import org.json.*;
import java.util.*;
final class Json {
    static JSONObject obj(JSONObject p,String k){JSONObject x=p.optJSONObject(k);return x==null?new JSONObject():x;}
    static JSONArray arr(JSONObject p,String k){JSONArray x=p.optJSONArray(k);return x==null?new JSONArray():x;}
    static List<Object> list(JSONArray a){List<Object> r=new ArrayList<>();if(a!=null)for(int i=0;i<a.length();i++)r.add(a.opt(i));return r;}
    static List<String> keys(JSONObject o){List<String> r=new ArrayList<>();o.keys().forEachRemaining(r::add);return r;}
    static JSONObject put(JSONObject o,String k,Object v){try{o.put(k,v==null?JSONObject.NULL:v);return o;}catch(JSONException e){throw new IllegalArgumentException(e);}}
    static JSONObject copy(JSONObject o){try{return new JSONObject(o.toString());}catch(JSONException e){throw new IllegalArgumentException(e);}}
    static JSONArray array(Collection<?> a){return new JSONArray(a);}
    static String str(Object o){return o==null||o==JSONObject.NULL?"":String.valueOf(o);}
    static boolean same(Object a,Object b){if(a==null||b==null||a==JSONObject.NULL||b==JSONObject.NULL)return false;
        if(a instanceof JSONArray&&b instanceof JSONArray){JSONArray x=(JSONArray)a,y=(JSONArray)b;if(x.length()!=y.length())return false;for(int i=0;i<x.length();i++)if(!same(x.opt(i),y.opt(i)))return false;return true;}
        if(a instanceof JSONObject&&b instanceof JSONObject){JSONObject x=(JSONObject)a,y=(JSONObject)b;if(x.length()!=y.length())return false;for(String k:keys(x))if(!same(x.opt(k),y.opt(k)))return false;return true;}
        if(a instanceof Number||b instanceof Number){try{return Double.parseDouble(str(a).trim())==Double.parseDouble(str(b).trim())&&!str(a).trim().isEmpty()&&!str(b).trim().isEmpty();}catch(Exception e){return false;}}
        return str(a).trim().equals(str(b).trim());
    }
    static String canonical(Object x){if(x instanceof JSONArray){List<String> s=new ArrayList<>();for(Object a:list((JSONArray)x))s.add(canonical(a));return s.toString();}return str(x).trim();}
    static boolean unordered(JSONArray a,JSONArray b){List<String>x=new ArrayList<>(),y=new ArrayList<>();for(Object o:list(a))x.add(canonical(o));for(Object o:list(b))y.add(canonical(o));Collections.sort(x);Collections.sort(y);return x.equals(y)&&new HashSet<>(x).size()==x.size();}
    static int integer(Object o,int fallback){try{String s=str(o).trim();if(!s.matches("-?[0-9]+"))return fallback;return Integer.parseInt(s);}catch(Exception e){return fallback;}}
    static int sum(Collection<?> a){int s=0;for(Object n:a){int x=integer(n,Integer.MIN_VALUE);if(x==Integer.MIN_VALUE)return Integer.MIN_VALUE;s+=x;}return s;}
    static boolean contains(JSONArray a,Object x){for(Object o:list(a))if(same(o,x))return true;return false;}
}
