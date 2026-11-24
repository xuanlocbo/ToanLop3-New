package vn.mocnhien.toan3;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.*;
import java.util.*;

/** All current drafts, navigation, completion and award claims are one committed snapshot. */
public final class ProgressStore {
    interface Storage { String load(); boolean commit(String value); }
    private final Storage storage;
    private final Curriculum curriculum;
    private final RewardLedger ledger;
    private JSONObject state;
    public ProgressStore(Context c,Curriculum bank) {
        this(bank,androidStorage(c));
    }
    private static Storage androidStorage(Context c){
        SharedPreferences prefs=c.getSharedPreferences("native_curriculum_progress_v2",Context.MODE_PRIVATE);
        return new Storage(){
            public String load(){return prefs.getString("snapshot",null);}
            public boolean commit(String value){return prefs.edit().putString("snapshot",value).commit();}
        };
    }
    ProgressStore(Curriculum bank,Storage storage) {
        this.storage=storage;curriculum=bank;ledger=new RewardLedger(bank.rewards);
        String saved=storage.load();
        try{state=saved==null?new JSONObject():new JSONObject(saved);}
        catch(JSONException e){throw new IllegalStateException("Không đọc được tiến độ đã lưu; dữ liệu cũ chưa bị xóa.",e);}
        for(String k:new String[]{"drafts","done","review","claims","nav","adventures","adventureBest","deferredBonus"})
            if(state.optJSONObject(k)==null)Json.put(state,k,new JSONObject());
    }
    public synchronized JSONObject draft(String id){
        JSONObject own=Json.obj(state,"drafts").optJSONObject(id);
        return Json.copy(own!=null?own:Json.obj(Json.obj(state,"deferredBonus"),id));
    }
    public synchronized JSONObject nav(){return Json.copy(Json.obj(state,"nav"));}
    public synchronized boolean done(String id){return Json.obj(state,"done").optBoolean(id);}
    public synchronized boolean review(String id){return Json.obj(state,"review").optBoolean(id);}
    public synchronized boolean sound(){return state.optBoolean("sound",true);}
    public synchronized int balance(){return ledger.restore(Json.keys(Json.obj(state,"claims"))).balance;}
    public synchronized int completed(Collection<JSONObject> qs){int n=0;for(JSONObject q:qs)if(done(q.optString("id")))n++;return n;}
    public synchronized boolean unlocked(JSONObject q){for(Object p:Json.list(q.optJSONArray("prerequisites")))if(!done(String.valueOf(p)))return false;return true;}
    private boolean save(JSONObject next){
        if(!storage.commit(next.toString()))return false;
        state=next;return true;
    }
    public synchronized boolean setNav(JSONObject nav){JSONObject n=Json.copy(state);Json.put(n,"nav",Json.copy(nav));return save(n);}
    public synchronized boolean setSound(boolean b){JSONObject n=Json.copy(state);Json.put(n,"sound",b);return save(n);}
    public synchronized boolean saveDraft(String id,JSONObject draft){
        if(!curriculum.questions.containsKey(id))return false;
        JSONObject n=Json.copy(state);Json.put(Json.obj(n,"drafts"),id,Json.copy(draft));return save(n);
    }
    public synchronized boolean resetDraft(String id){return saveDraft(id,new JSONObject());}
    public static final class Result {
        public final boolean saved,correct;public final int earned;
        Result(boolean s,boolean c,int e){saved=s;correct=c;earned=e;}
    }
    private int recordResult(JSONObject n,JSONObject q,boolean correct){
        String id=q.optString("id");
        if(correct){
            Json.put(Json.obj(n,"done"),id,true);Json.obj(n,"review").remove(id);Json.obj(n,"deferredBonus").remove(id);
            RewardLedger.Award award=ledger.claim(ledger.restore(Json.keys(Json.obj(n,"claims"))),id,true);
            JSONObject claims=new JSONObject();for(String k:award.snapshot.claims)Json.put(claims,k,true);
            Json.put(n,"claims",claims);return award.earned;
        }
        if(Json.obj(q,"grading").optBoolean("scoreEligible"))Json.put(Json.obj(n,"review"),id,true);
        return 0;
    }
    private JSONObject gradedDraft(JSONObject draft,boolean correct,int earned){
        JSONObject d=Json.copy(draft);Json.put(d,"status",correct?"correct":"retry");
        Json.put(d,"submitted",true);Json.put(d,"earned",earned);return d;
    }
    public synchronized Result submit(String id,JSONObject draft){
        JSONObject q=curriculum.questions.get(id);
        if(q==null||!unlocked(q))return new Result(false,false,0);
        boolean correct=NativeGrader.grade(q,draft);JSONObject n=Json.copy(state);
        int earned=recordResult(n,q,correct);
        Json.put(Json.obj(n,"drafts"),id,gradedDraft(draft,correct,earned));
        return save(n)?new Result(true,correct,earned):new Result(false,false,0);
    }
    private JSONObject adventure(JSONObject snapshot,int lesson){
        AdventurePlan.require(lesson);
        return Json.obj(Json.obj(snapshot,"adventures"),String.valueOf(lesson));
    }
    private JSONObject mutableAdventure(JSONObject snapshot,int lesson){
        JSONObject adventures=Json.obj(snapshot,"adventures");
        String key=String.valueOf(lesson);
        JSONObject a=adventures.optJSONObject(key);
        if(a==null){a=new JSONObject();Json.put(adventures,key,a);}return a;
    }
    public synchronized AdventureWorld.State adventureState(int lesson){
        return AdventureWorld.State.read(Json.obj(adventure(state,lesson),"motion"));
    }
    public synchronized int adventureBest(int lesson){
        AdventurePlan.require(lesson);return Json.obj(state,"adventureBest").optInt(String.valueOf(lesson),-1);
    }
    public synchronized boolean saveAdventureMotion(int lesson,AdventureWorld.State motion){
        AdventureWorld.State old=adventureState(lesson);
        if(!AdventureWorld.canSaveMotion(old,motion))return false;
        JSONObject n=Json.copy(state);Json.put(mutableAdventure(n,lesson),"motion",motion.json());
        if(motion.finished){
            JSONObject best=Json.obj(n,"adventureBest");
            Json.put(best,String.valueOf(lesson),Math.max(best.optInt(String.valueOf(lesson),-1),motion.flowerCount()));
        }
        return save(n);
    }
    /** Station draft is local to the first gate. Legacy snapshots with an already
     * answered or cleared gate are considered complete, preserving existing play. */
    public synchronized boolean trailComplete(int lesson){
        AdventureWorld.State s=adventureState(lesson);
        return s.gate>0||s.answered||Json.obj(adventure(state,lesson),"trail").optBoolean("complete");
    }
    public synchronized String trailDraft(int lesson){
        return Json.obj(adventure(state,lesson),"trail").optString("draft","");
    }
    public synchronized boolean saveTrailDraft(int lesson,String encoded){
        AdventureWorld.State s=adventureState(lesson);TrailMission m=TrailMission.of(lesson);
        if(!s.pending||s.gate!=0||trailComplete(lesson)||encoded==null||!encoded.equals(m.encode(m.read(encoded))))return false;
        JSONObject n=Json.copy(state);JSONObject trail=Json.obj(mutableAdventure(n,lesson),"trail");
        Json.put(trail,"draft",encoded);Json.put(mutableAdventure(n,lesson),"trail",trail);return save(n);
    }
    public synchronized boolean completeTrail(int lesson,String encoded){
        AdventureWorld.State s=adventureState(lesson);TrailMission m=TrailMission.of(lesson);
        if(!s.pending||s.gate!=0||trailComplete(lesson)||encoded==null||!encoded.equals(trailDraft(lesson))
            ||!encoded.equals(m.encode(m.read(encoded)))||!m.solved(m.read(encoded)))return false;
        JSONObject n=Json.copy(state);JSONObject trail=Json.obj(mutableAdventure(n,lesson),"trail");
        Json.put(trail,"complete",true);Json.put(mutableAdventure(n,lesson),"trail",trail);return save(n);
    }
    public synchronized boolean resetAdventure(int lesson){
        AdventurePlan.require(lesson);JSONObject n=Json.copy(state);
        Json.obj(n,"adventures").remove(String.valueOf(lesson));return save(n);
    }
    public synchronized JSONObject adventureDraft(int lesson,int gate){
        AdventureWorld.State s=adventureState(lesson);
        if(!s.pending||s.gate!=gate)return new JSONObject();
        JSONObject own=adventure(state,lesson).optJSONObject("draft");
        return Json.copy(own!=null?own:Json.obj(Json.obj(state,"deferredBonus"),AdventurePlan.questionId(lesson,gate)));
    }
    public synchronized boolean saveAdventureDraft(int lesson,int gate,JSONObject draft){
        AdventureWorld.State s=adventureState(lesson);
        if(!s.pending||s.answered||s.gate!=gate)return false;
        JSONObject n=Json.copy(state);Json.put(mutableAdventure(n,lesson),"draft",Json.copy(draft));return save(n);
    }
    public synchronized Result submitAdventure(int lesson,int gate,JSONObject draft){
        AdventureWorld.State s=adventureState(lesson);
        if(!s.pending||s.gate!=gate||s.answered||gate==0&&!trailComplete(lesson))return new Result(false,false,0);
        JSONObject q=curriculum.questions.get(AdventurePlan.questionId(lesson,gate));
        if(q==null||!unlocked(q))return new Result(false,false,0);
        boolean correct=NativeGrader.grade(q,draft);JSONObject n=Json.copy(state);
        int earned=recordResult(n,q,correct);s.answered=correct;
        JSONObject a=mutableAdventure(n,lesson);Json.put(a,"motion",s.json());
        Json.put(a,"draft",gradedDraft(draft,correct,earned));
        // Award, study completion, gate answer and draft commit together.
        return save(n)?new Result(true,correct,earned):new Result(false,false,0);
    }
    /** Optional treasure gates may be bypassed without granting any credit or coins. */
    public synchronized boolean skipAdventureBonus(int lesson,int gate){
        AdventureWorld.State s=adventureState(lesson);
        if(gate<2||gate>=AdventurePlan.GATES||!s.pending||s.answered||s.gate!=gate)return false;
        JSONObject n=Json.copy(state);
        JSONObject a=mutableAdventure(n,lesson);
        String id=AdventurePlan.questionId(lesson,gate);
        JSONObject pending=adventureDraft(lesson,gate);
        if(pending.length()>0&&!done(id))Json.put(Json.obj(n,"deferredBonus"),id,pending);
        s.answered=true;s.advance();Json.put(a,"motion",s.json());a.remove("draft");
        return save(n);
    }
    public synchronized boolean advanceAdventure(int lesson,int gate){
        AdventureWorld.State s=adventureState(lesson);
        if(!s.pending||!s.answered||s.gate!=gate)return false;
        JSONObject n=Json.copy(state);s.advance();JSONObject a=mutableAdventure(n,lesson);
        Json.put(a,"motion",s.json());a.remove("draft");return save(n);
    }
}
