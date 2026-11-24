package vn.mocnhien.toan3;

import org.junit.Test;
import static org.junit.Assert.*;
import org.json.*;
import java.nio.file.*;
import java.util.*;

/** Regression fixtures exercise each runtime record using the same draft format as native widgets. */
public class NativeCurriculumTest {
    Curriculum load() throws Exception {
        return new Curriculum(new JSONObject(Files.readString(Path.of(System.getProperty("curriculum.path")))));
    }
    void put(JSONObject d,String k,Object x){Json.put(d,k,x);}
    JSONArray indices(JSONArray available,JSONArray desired){
        JSONArray result=new JSONArray();Set<Integer> used=new HashSet<>();
        for(Object n:Json.list(desired)){int found=-1;for(int i=0;i<available.length();i++)if(!used.contains(i)&&Json.same(n,available.opt(i))){found=i;break;}
            if(found<0)throw new AssertionError("missing weight");used.add(found);result.put(found);}
        return result;
    }
    JSONArray boardTurns(JSONObject q){
        JSONArray board=Json.arr(Json.obj(q,"visual"),"board");
        Map<Integer,JSONArray> path=new HashMap<>();ArrayDeque<Integer> queue=new ArrayDeque<>();
        path.put(0,new JSONArray());queue.add(0);
        while(!queue.isEmpty()){
            int pos=queue.remove();JSONArray old=path.get(pos);
            for(int die=1;die<=6;die++){
                int target=pos+die;if(target>28)continue;
                JSONObject event=new JSONObject();put(event,"die",die);
                JSONArray next=new JSONArray(Json.list(old));next.put(event);
                if(target==28)return next;
                JSONObject tile=board.optJSONObject(target);
                if(tile==null)continue;
                if(tile.optString("type").equals("animal")){target=tile.optInt("jump");tile=board.optJSONObject(target);}
                if(tile==null||!tile.optString("type").equals("math"))continue;
                put(event,"value",tile.opt("z"));
                if(!path.containsKey(target)){path.put(target,next);queue.add(target);}
            }
        }throw new AssertionError("board cannot finish");
    }
    JSONObject draft(JSONObject q){
        JSONObject d=new JSONObject(),v=Json.obj(q,"visual"),a=Json.obj(q,"answer");
        Object answer=q.opt("answer");String kind=q.optString("kind");
        switch(kind){
            case "study":put(d,"read",true);break;
            case "practice":case "practice_measure":case "practice_compose_story":
                put(d,"adult",true);put(d,"note","Đã thực hành");
                JSONArray fields=v.optJSONArray("fields");if(fields==null)fields=v.optJSONArray("columns");
                JSONObject notes=new JSONObject();for(Object f:Json.list(fields))put(notes,String.valueOf(f),"Đã đo");
                put(d,"notes",notes);break;
            case "number":case "multiplication":case "ruler":case "scale":case "balance":case "cup":case "thermometer":
            case "choice":case "match_value":case "point_select":
                put(d,"value",answer);put(d,"reason","Đã giải thích cách tính");break;
            case "division":case "division_story":put(d,"quotient",a.opt("quotient"));put(d,"remainder",a.opt("remainder"));break;
            case "steps":case "compose_story":
                put(d,"intermediate",a.opt("intermediate"));put(d,"final",a.opt("final"));put(d,"adult",true);put(d,"story","Đã nêu bài toán.");
                JSONArray work=Json.arr(q,"workedSteps");
                for(int i=0;i<work.length();i++){JSONObject s=work.optJSONObject(i);JSONArray ops=Json.arr(s,"operands");put(d,"s"+i+"a",ops.opt(0));put(d,"s"+i+"b",ops.opt(1));put(d,"s"+i+"op",s.opt("op"));}break;
            case "multi":case "segment_multi":case "order":put(d,"values",answer);break;
            case "match":put(d,"mapping",answer);break;
            case "route_match":put(d,"bridge",a.opt("bridge"));put(d,"basket",a.opt("basket"));break;
            case "digit_fill":put(d,"values",a.opt("digits"));break;
            case "number_fill":case "operator_fill":
                if(a.optBoolean("requireAll"))put(d,"sequences",a.opt("accepted"));else put(d,"values",Json.arr(a,"accepted").opt(0));break;
            case "pour":put(d,"actions",v.opt("solutionActions"));break;
            case "balance_select":
                if(a.has("riceKg")){JSONArray avail=Json.arr(v,"availableWeightsKg");put(d,"left",indices(avail,Json.arr(a,"leftWeightsKg")));put(d,"right",indices(avail,Json.arr(a,"rightWeightsKg")));put(d,"riceSide","right");}
                else{put(d,"selected",indices(Json.arr(v,"availableWeightsG"),Json.arr(a,"selectedWeightsG")));if(a.has("weighings")){put(d,"weighings",1);put(d,"keep",a.opt("keep"));}}break;
            case "draw":put(d,"vertices",a.opt("vertices"));put(d,"closed",true);break;
            case "angle_multi":put(d,"angles",a.opt("angles"));break;
            case "board_game":put(d,"turns",boardTurns(q));break;
            default:throw new AssertionError(kind);
        }
        if(kind.equals("division")||kind.equals("multiplication")){
            JSONArray work=Json.arr(v,"steps");
            String[] keys=kind.equals("division")?new String[]{"partialDividend","quotientDigit","product","remainder"}:new String[]{"product","resultDigit","carryOut"};
            for(int i=0;i<work.length();i++)for(String k:keys)put(d,"work"+i+"_"+k,work.optJSONObject(i).opt(k));
        }
        return d;
    }
    @Test public void all959RecordsAcceptCompleteSolutionRejectEmptyDraft() throws Exception {
        Curriculum c=load();assertEquals(959,c.questions.size());assertEquals(19,c.lessons.size());Set<String> kinds=new HashSet<>();
        for(JSONObject q:c.questions.values()){
            String id=q.optString("id");kinds.add(q.optString("kind"));
            assertFalse("empty accepted: "+id,NativeGrader.grade(q,new JSONObject()));
            JSONObject d=draft(q);
            assertTrue("valid input rejected: "+id+" "+d,NativeGrader.grade(q,d));
            assertTrue("restored draft rejected: "+id,NativeGrader.grade(q,new JSONObject(d.toString())));
        }
        assertEquals(29,kinds.size());
    }
    @Test public void partialWorkAndInvalidActionsAreRejected() throws Exception {
        Curriculum c=load();int checked=0;
        for(JSONObject q:c.questions.values()){
            String k=q.optString("kind");JSONObject d=draft(q);JSONObject v=Json.obj(q,"visual");
            switch(k){
                case "division":case "multiplication":
                    if(Json.arr(v,"steps").length()==0)continue;d.remove("work0_product");break;
                case "steps":case "compose_story":d.remove("intermediate");break;
                case "practice":case "practice_measure":case "practice_compose_story":put(d,"adult",false);break;
                case "pour":put(d,"actions",new JSONArray().put(Json.put(Json.put(new JSONObject(),"type","fill"),"cup",3)));break;
                case "balance_select":
                    if(Json.obj(q,"answer").has("riceKg"))put(d,"left",new JSONArray(Arrays.asList(0,0)));else put(d,"selected",new JSONArray(Arrays.asList(0,0)));break;
                case "draw":put(d,"closed",false);break;
                case "angle_multi":put(d,"angles",new JSONArray());break;
                case "board_game":put(d,"turns",new JSONArray().put(Json.put(new JSONObject(),"die",7)));break;
                default:continue;
            }
            assertFalse(q.optString("id"),NativeGrader.grade(q,d));checked++;
        }
        assertTrue(checked>150);
    }
    @Test public void rewardClaimsAreIdempotent() throws Exception {
        Curriculum c=load();RewardLedger ledger=new RewardLedger(c.rewards);RewardLedger.Snapshot s=ledger.restore(Collections.emptySet());
        assertEquals(0,s.balance);assertEquals(0,ledger.claim(s,"BONUS.26.1",false).earned);
        RewardLedger.Award one=ledger.claim(s,"BONUS.26.1",true);assertEquals(80,one.earned);
        RewardLedger.Award two=ledger.claim(one.snapshot,"BONUS.26.2",true);assertEquals(220,two.earned);assertEquals(300,two.snapshot.balance);
        assertEquals(0,ledger.claim(two.snapshot,"BONUS.26.2",true).earned);
        assertEquals(300,ledger.restore(two.snapshot.claims).balance);
        assertEquals(0,ledger.claim(s,"CHEST.26",true).earned);
        s=ledger.restore(Collections.emptySet());int total=0;
        for(JSONObject q:c.questions.values()){RewardLedger.Award award=ledger.claim(s,q.optString("id"),true);s=award.snapshot;total+=award.earned;assertEquals(0,ledger.claim(s,q.optString("id"),true).earned);}
        assertEquals(total,s.balance);assertEquals(total,ledger.restore(s.claims).balance);
        assertEquals(19,s.claims.stream().filter(k->k.startsWith("CHEST.")).count());
    }
}
