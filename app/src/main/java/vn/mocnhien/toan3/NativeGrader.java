package vn.mocnhien.toan3;

import org.json.*;
import java.util.*;

/** Grading of typed learner input, never of button positions. */
public final class NativeGrader {
    public static final Set<String> KINDS=Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
        "study","division","number","multi","choice","steps","compose_story",
        "practice_compose_story","route_match","division_story","ruler","balance","scale",
        "match","cup","pour","thermometer","practice","order","practice_measure","balance_select",
        "board_game","multiplication","match_value","number_fill","digit_fill","operator_fill",
        "point_select","draw","segment_multi","angle_multi")));
    public static boolean supports(String kind){return KINDS.contains(kind);}
    public static boolean grade(JSONObject q,JSONObject d){
        String kind=q.optString("kind");Object a=q.opt("answer");JSONObject v=Json.obj(q,"visual");
        JSONObject key=a instanceof JSONObject?(JSONObject)a:new JSONObject();
        Object value=d.opt("value");
        switch(kind){
            case "study":return d.optBoolean("read");
            case "practice":case "practice_measure":case "practice_compose_story":
                if(!d.optBoolean("adult"))return false;
                JSONArray fields=v.optJSONArray("fields");
                if(fields==null)fields=v.optJSONArray("columns");
                if(fields==null)return !d.optString("note").trim().isEmpty();
                JSONObject notes=Json.obj(d,"notes");
                for(Object f:Json.list(fields))if(notes.optString(String.valueOf(f)).trim().isEmpty())return false;
                return true;
            case "number":case "ruler":case "scale":case "balance":case "cup":case "thermometer":
                return Json.integer(value,Integer.MIN_VALUE)!=Integer.MIN_VALUE&&Json.same(a,value);
            case "multiplication":return Json.integer(value,Integer.MIN_VALUE)!=Integer.MIN_VALUE&&Json.same(a,value)&&written(v,d,false);
            case "choice":case "match_value":case "point_select":
                return Json.same(a,value)&&(!Json.obj(q,"grading").optBoolean("requiresReason")||!d.optString("reason").trim().isEmpty());
            case "division":case "division_story":
                return Json.same(key.opt("quotient"),d.opt("quotient"))&&
                    Json.same(key.opt("remainder"),d.opt("remainder"))&&(!"long_division".equals(v.optString("type"))||written(v,d,true));
            case "steps":case "compose_story":
                return Json.same(key.opt("intermediate"),d.opt("intermediate"))&&Json.same(key.opt("final"),d.opt("final"))&&
                    worked(q,d)&&(!kind.equals("compose_story")||d.optBoolean("adult")&&!d.optString("story").trim().isEmpty());
            case "multi":return a instanceof JSONArray&&Json.unordered((JSONArray)a,Json.arr(d,"values"));
            case "segment_multi":return a instanceof JSONArray&&segments((JSONArray)a,Json.arr(d,"values"));
            case "order":return Json.same(a,d.optJSONArray("values"));
            case "match":return Json.same(a,d.optJSONObject("mapping"));
            case "route_match":return Json.same(key.opt("bridge"),d.opt("bridge"))&&Json.same(key.opt("basket"),d.opt("basket"));
            case "digit_fill":return Json.same(key.optJSONArray("digits"),d.optJSONArray("values"));
            case "number_fill":return accepted(key,Json.arr(d,"values"));
            case "operator_fill":
                if(!key.optBoolean("requireAll"))return accepted(key,Json.arr(d,"values"));
                return Json.unordered(Json.arr(key,"accepted"),Json.arr(d,"sequences"));
            case "pour":return pour(q,d);
            case "balance_select":return balance(q,d);
            case "draw":return polygon(Json.arr(key,"vertices"),Json.arr(d,"vertices"),d.optBoolean("closed"));
            case "angle_multi":return angles(q,Json.arr(d,"angles"));
            case "board_game":return BoardState.finished(q,d);
            default:return false;
        }
    }
    static boolean written(JSONObject v,JSONObject d,boolean div){
        JSONArray steps=Json.arr(v,"steps");
        String[] keys=div?new String[]{"partialDividend","quotientDigit","product","remainder"}:new String[]{"product","resultDigit","carryOut"};
        for(int i=0;i<steps.length();i++){JSONObject step=steps.optJSONObject(i);if(step==null)return false;
            for(String key:keys){Object actual=d.opt("work"+i+"_"+key);if(Json.integer(actual,Integer.MIN_VALUE)==Integer.MIN_VALUE||!Json.same(step.opt(key),actual))return false;}
        }return true;
    }
    static boolean worked(JSONObject q,JSONObject d){
        JSONArray work=Json.arr(q,"workedSteps");
        for(int i=0;i<work.length();i++){
            JSONObject t=work.optJSONObject(i);JSONArray operands=Json.arr(t,"operands");
            Object x=d.opt("s"+i+"a"),y=d.opt("s"+i+"b"),op=d.opt("s"+i+"op");
            if(!Json.same(t.opt("op"),op))return false;
            boolean straight=Json.same(operands.opt(0),x)&&Json.same(operands.opt(1),y);
            boolean reverse=t.optBoolean("acceptReversed")&&Json.same(operands.opt(0),y)&&Json.same(operands.opt(1),x);
            if(!straight&&!reverse)return false;
        }return true;
    }
    static boolean accepted(JSONObject key,JSONArray answer){
        for(Object x:Json.list(key.optJSONArray("accepted")))if(Json.same(x,answer))return true;return false;
    }
    static boolean segments(JSONArray expected,JSONArray actual){
        List<String> a=new ArrayList<>(),b=new ArrayList<>();
        for(Object s:Json.list(expected))a.add(segment(String.valueOf(s)));
        for(Object s:Json.list(actual))b.add(segment(String.valueOf(s)));
        Collections.sort(a);Collections.sort(b);return a.equals(b)&&new HashSet<>(b).size()==b.size();
    }
    static String segment(String s){char[] a=s.toCharArray();Arrays.sort(a);return new String(a);}
    public static int[] water(JSONObject q,JSONObject d){
        JSONObject v=Json.obj(q,"visual");JSONArray ca=Json.arr(v,"capacitiesMl"),initial=Json.arr(v,"initialMl");
        if(ca.length()!=2)return null;
        int[] caps={ca.optInt(0),ca.optInt(1)},s={initial.optInt(0),initial.optInt(1)};
        if(caps[0]<=0||caps[1]<=0||s[0]<0||s[1]<0||s[0]>caps[0]||s[1]>caps[1])return null;
        JSONArray actions=Json.arr(d,"actions");if(actions.length()>1000)return null;
        for(Object o:Json.list(actions)){
            if(!(o instanceof JSONObject))return null;JSONObject t=(JSONObject)o;
            int i=t.optInt("cup",-1),from=t.optInt("from",-1),to=t.optInt("to",-1);
            switch(t.optString("type")){
                case "fill":if(i<0||i>1)return null;s[i]=caps[i];break;
                case "empty":if(i<0||i>1)return null;s[i]=0;break;
                case "pour":if(from<0||from>1||to<0||to>1||from==to)return null;
                    int move=Math.min(s[from],caps[to]-s[to]);s[from]-=move;s[to]+=move;break;
                default:return null;
            }
        }
        return s;
    }
    static boolean pour(JSONObject q,JSONObject d){int[] s=water(q,d);int target=Json.obj(q,"answer").optInt("targetMl",-1);return s!=null&&target>=0&&(s[0]==target||s[1]==target);}
    public static List<Integer> weights(JSONArray available,JSONArray selected){
        Set<Integer> used=new HashSet<>();List<Integer> out=new ArrayList<>();
        for(Object o:Json.list(selected)){int i=Json.integer(o,-1);
            if(i<0||i>=available.length()||!used.add(i))return null;
            out.add(available.optInt(i));}
        return out;
    }
    static boolean balance(JSONObject q,JSONObject d){
        JSONObject a=Json.obj(q,"answer"),v=Json.obj(q,"visual");
        if(a.has("riceKg")){
            JSONArray avail=Json.arr(v,"availableWeightsKg"),left=Json.arr(d,"left"),right=Json.arr(d,"right");
            List<Integer> l=weights(avail,left),r=weights(avail,right);
            if(l==null||r==null||left.length()+right.length()!=avail.length())return false;
            for(Object i:Json.list(left))if(Json.contains(right,i))return false;
            String rice=d.optString("riceSide");int diff=Json.sum(l)-Json.sum(r);
            return rice.equals("right")?diff==a.optInt("riceKg"):rice.equals("left")&&-diff==a.optInt("riceKg");
        }
        List<Integer> chosen=weights(Json.arr(v,"availableWeightsG"),Json.arr(d,"selected"));
        if(chosen==null||chosen.isEmpty())return false;
        int total=Json.sum(chosen);
        if(a.has("count")&&chosen.size()!=a.optInt("count"))return false;
        if(a.has("weighings")){
            String keep=d.optString("keep");
            if(!keep.equals("weighed")&&!keep.equals("remaining"))return false;
            if(d.optInt("weighings")!=1)return false;
            total=keep.equals("remaining")?a.optInt("initialG")-total:total;
        }
        return total==a.optInt("targetG",-1);
    }
    static boolean polygon(JSONArray target,JSONArray actual,boolean closed){
        int n=target.length();if(!closed||n<3||actual.length()!=n)return false;
        int[][] a=points(target),b=points(actual);if(a==null||b==null)return false;
        Set<String> seen=new HashSet<>();for(int[] p:b)if(!seen.add(p[0]+","+p[1]))return false;
        for(int start=0;start<n;start++)for(int dir:new int[]{1,-1}){
            boolean match=true;for(int i=0;i<n;i++){
                int j=(start+dir*i+n*n)%n;
                if(a[i][0]-a[0][0]!=b[j][0]-b[start][0]||a[i][1]-a[0][1]!=b[j][1]-b[start][1]){match=false;break;}
            }
            if(match)return true;
        }
        return false;
    }
    static int[][] points(JSONArray a){
        int[][] p=new int[a.length()][2];for(int i=0;i<a.length();i++){
            JSONArray t=a.optJSONArray(i);if(t==null||t.length()!=2)return null;
            for(int j=0;j<2;j++){p[i][j]=Json.integer(t.opt(j),Integer.MIN_VALUE);if(p[i][j]==Integer.MIN_VALUE)return null;}
        }return p;
    }
    static boolean angles(JSONObject q,JSONArray actual){
        JSONObject points=Json.obj(Json.obj(q,"visual"),"points");
        JSONArray expected=Json.arr(Json.obj(q,"answer"),"angles");Set<String> a=new HashSet<>(),b=new HashSet<>();
        for(Object o:Json.list(expected)){if(!(o instanceof JSONObject))return false;String k=angleKey(points,(JSONObject)o);if(k==null)return false;a.add(k);}
        for(Object o:Json.list(actual)){if(!(o instanceof JSONObject))return false;String k=angleKey(points,(JSONObject)o);if(k==null||!b.add(k))return false;}
        return a.equals(b)&&actual.length()==expected.length();
    }
    static String angleKey(JSONObject points,JSONObject angle){
        String vertex=angle.optString("vertex");JSONArray arms=Json.arr(angle,"arms"),o=points.optJSONArray(vertex);
        if(o==null||arms.length()!=2)return null;
        List<String> dirs=new ArrayList<>();
        for(Object p:Json.list(arms)){
            JSONArray v=points.optJSONArray(String.valueOf(p));if(v==null)return null;
            double x=v.optDouble(0)-o.optDouble(0),y=v.optDouble(1)-o.optDouble(1),norm=Math.hypot(x,y);if(norm<1e-9)return null;
            dirs.add(Math.round(x/norm*1000000)+","+Math.round(y/norm*1000000));
        }
        if(dirs.get(0).equals(dirs.get(1)))return null;
        Collections.sort(dirs);return vertex+":"+dirs.get(0)+"|"+dirs.get(1);
    }
}
