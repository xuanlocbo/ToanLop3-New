package vn.mocnhien.toan3;
import org.json.*;

/** Replayable one-player version of the source board. Turns and pending rolls survive restart. */
public final class BoardState {
    public int position=0, landing=-1, die=0, turns=0;
    public boolean finished=false,valid=true;
    public JSONObject tile;
    public static BoardState replay(JSONObject q,JSONObject d){
        BoardState s=new BoardState();
        JSONArray board=Json.arr(Json.obj(q,"visual"),"board"),events=Json.arr(d,"turns");
        if(board.length()!=28||events.length()>2000){s.valid=false;return s;}
        for(int i=0;i<events.length();i++){
            JSONObject e=events.optJSONObject(i);
            if(e==null||s.finished||s.landing!=-1){s.valid=false;return s;}
            int die=e.optInt("die",-1);if(die<1||die>6){s.valid=false;return s;}
            s.die=die;s.turns++;
            int target=s.position+die;
            if(target>28)continue;
            if(target==28){s.position=0;s.finished=true;continue;}
            JSONObject t=board.optJSONObject(target);
            if(t==null){s.valid=false;return s;}
            if("animal".equals(t.optString("type"))){target=t.optInt("jump",-1);t=board.optJSONObject(target);}
            if(t==null||!"math".equals(t.optString("type"))){s.valid=false;return s;}
            if(!e.has("value")){
                if(i!=events.length()-1){s.valid=false;return s;}
                s.landing=target;s.tile=t;return s;
            }
            if(Json.same(t.opt("z"),e.opt("value")))s.position=target;
        }
        return s;
    }
    public static boolean finished(JSONObject q,JSONObject d){BoardState s=replay(q,d);return s.valid&&s.finished;}
}
