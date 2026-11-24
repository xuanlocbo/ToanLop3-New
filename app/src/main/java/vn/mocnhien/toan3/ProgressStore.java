package vn.mocnhien.toan3;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.*;
import java.util.*;

/** All current drafts, navigation, completion and award claims are one committed snapshot. */
public final class ProgressStore {
    private final SharedPreferences prefs;
    private final Curriculum curriculum;
    private final RewardLedger ledger;
    private JSONObject state;
    public ProgressStore(Context c,Curriculum bank) {
        prefs=c.getSharedPreferences("native_curriculum_progress_v2",Context.MODE_PRIVATE);
        curriculum=bank;ledger=new RewardLedger(bank.rewards);
        String saved=prefs.getString("snapshot",null);
        try{state=saved==null?new JSONObject():new JSONObject(saved);}
        catch(JSONException e){throw new IllegalStateException("Không đọc được tiến độ đã lưu; dữ liệu cũ chưa bị xóa.",e);}
        for(String k:new String[]{"drafts","done","review","claims","nav"})
            if(state.optJSONObject(k)==null)Json.put(state,k,new JSONObject());
    }
    public synchronized JSONObject draft(String id){return Json.copy(Json.obj(Json.obj(state,"drafts"),id));}
    public synchronized JSONObject nav(){return Json.copy(Json.obj(state,"nav"));}
    public synchronized boolean done(String id){return Json.obj(state,"done").optBoolean(id);}
    public synchronized boolean review(String id){return Json.obj(state,"review").optBoolean(id);}
    public synchronized boolean sound(){return state.optBoolean("sound",true);}
    public synchronized int balance(){return ledger.restore(Json.keys(Json.obj(state,"claims"))).balance;}
    public synchronized int completed(Collection<JSONObject> qs){int n=0;for(JSONObject q:qs)if(done(q.optString("id")))n++;return n;}
    public synchronized boolean unlocked(JSONObject q){for(Object p:Json.list(q.optJSONArray("prerequisites")))if(!done(String.valueOf(p)))return false;return true;}
    private boolean save(JSONObject next){
        if(!prefs.edit().putString("snapshot",next.toString()).commit())return false;
        state=next;return true;
    }
    public synchronized boolean setNav(JSONObject nav){JSONObject n=Json.copy(state);Json.put(n,"nav",nav);return save(n);}
    public synchronized boolean setSound(boolean b){JSONObject n=Json.copy(state);Json.put(n,"sound",b);return save(n);}
    public synchronized boolean saveDraft(String id,JSONObject draft){
        if(!curriculum.questions.containsKey(id))return false;
        JSONObject n=Json.copy(state);Json.put(Json.obj(n,"drafts"),id,draft);return save(n);
    }
    public synchronized boolean resetDraft(String id){return saveDraft(id,new JSONObject());}
    public static final class Result {
        public final boolean saved,correct;public final int earned;
        Result(boolean s,boolean c,int e){saved=s;correct=c;earned=e;}
    }
    public synchronized Result submit(String id,JSONObject draft){
        JSONObject q=curriculum.questions.get(id);
        if(q==null||!unlocked(q))return new Result(false,false,0);
        boolean correct=NativeGrader.grade(q,draft);
        JSONObject n=Json.copy(state);JSONObject d=Json.copy(draft);
        Json.put(d,"status",correct?"correct":"retry");
        Json.put(d,"submitted",true);
        int earned=0;
        if(correct){
            Json.put(Json.obj(n,"done"),id,true);Json.obj(n,"review").remove(id);
            RewardLedger.Award award=ledger.claim(ledger.restore(Json.keys(Json.obj(n,"claims"))),id,true);
            JSONObject claims=new JSONObject();for(String k:award.snapshot.claims)Json.put(claims,k,true);
            Json.put(n,"claims",claims);earned=award.earned;
        } else if(Json.obj(q,"grading").optBoolean("scoreEligible")) {
            Json.put(Json.obj(n,"review"),id,true);
        }
        Json.put(d,"earned",earned);Json.put(Json.obj(n,"drafts"),id,d);
        return save(n)?new Result(true,correct,earned):new Result(false,false,0);
    }
}
