package vn.mocnhien.toan3;
import org.json.JSONObject;

/** Fixed 60 Hz, integer physics. The game never generates or grades a math answer. */
public final class AdventureWorld {
    public static final int SEGMENT=480,FINISH=2070,HALF=10,SPEED=5,JUMP=14,MAX_PETALS=12;
    public static final int PETAL_MASK=(1<<MAX_PETALS)-1;
    public final int lesson;
    public final Hazard[] hazards=new Hazard[8];
    public final int[][] petals=new int[MAX_PETALS][2];
    public static final class Hazard {
        public final int left,right,height;
        Hazard(int left,int right,int height){this.left=left;this.right=right;this.height=height;}
        public boolean pit(){return height==0;}
    }
    public AdventureWorld(int lesson){
        AdventurePlan.require(lesson);this.lesson=lesson;
        for(int g=0;g<AdventurePlan.GATES;g++){
            int base=g*SEGMENT;
            int gap=base+155+(lesson*7+g*13)%31;
            int width=56+(lesson*3+g*5)%21;
            int block=base+327+(lesson*11+g*7)%17;
            hazards[2*g]=new Hazard(gap,gap+width,0);
            hazards[2*g+1]=new Hazard(block,block+32+(lesson+g)%7,25+(lesson*3+g*7)%13);
            petals[g*3]=new int[]{base+95,23};
            petals[g*3+1]=new int[]{gap+width/2,85};
            petals[g*3+2]=new int[]{base+416,23};
        }
    }
    public static int checkpoint(int cleared){return cleared==0?40:cleared*SEGMENT+32;}
    public static int stop(int cleared){return cleared>=AdventurePlan.GATES?FINISH:(cleared+1)*SEGMENT-22;}
    public static final class State {
        public int x=40,y=0,vy=0,gate=0,petals=0,falls=0;
        public boolean pending=false,answered=false,finished=false;
        public JSONObject json(){
            JSONObject o=new JSONObject();Json.put(o,"version",1);Json.put(o,"x",x);Json.put(o,"y",y);Json.put(o,"vy",vy);
            Json.put(o,"gate",gate);Json.put(o,"petals",petals);Json.put(o,"falls",falls);Json.put(o,"pending",pending);
            Json.put(o,"answered",answered);Json.put(o,"finished",finished);return o;
        }
        public State copy(){return read(json());}
        public static State read(JSONObject o){
            if(o.length()==0)return new State();
            if(o.optInt("version")!=1)throw new IllegalArgumentException("Invalid adventure state version");
            State s=new State();s.x=o.optInt("x",-1);s.y=o.optInt("y",-999);s.vy=o.optInt("vy",-999);
            s.gate=o.optInt("gate",-1);s.petals=o.optInt("petals",-1);s.falls=o.optInt("falls",-1);
            s.pending=o.optBoolean("pending");s.answered=o.optBoolean("answered");s.finished=o.optBoolean("finished");
            if(!s.valid())throw new IllegalArgumentException("Invalid adventure state");return s;
        }
        public boolean valid(){
            if(gate<0||gate>AdventurePlan.GATES||x<checkpoint(gate)||x>stop(gate)||y< -84||y>100||vy< -24||vy>JUMP
                ||petals<0||(petals&~PETAL_MASK)!=0||falls<0||answered&&!pending)return false;
            if(pending&&(gate>=AdventurePlan.GATES||x!=stop(gate)||y!=0||vy!=0||finished))return false;
            if(finished&&(gate!=AdventurePlan.GATES||x!=FINISH||y!=0||vy!=0||pending||answered))return false;
            return pending||finished||x<stop(gate);
        }
        /** Only the store calls this after a correct, committed answer. */
        public void advance(){
            if(!pending||!answered||finished||gate>=AdventurePlan.GATES)throw new IllegalStateException("Gate not answered");
            gate++;pending=false;answered=false;x=checkpoint(gate);y=vy=0;
        }
        public int flowerCount(){return Integer.bitCount(petals);}
    }
    public boolean ground(int x){
        for(Hazard h:hazards)if(h.pit()&&x>=h.left&&x<h.right)return false;
        return true;
    }
    public boolean grounded(State s){return s.y==0&&s.vy==0&&ground(s.x);}
    public void respawn(State s){s.x=checkpoint(s.gate);s.y=s.vy=0;if(s.falls<Integer.MAX_VALUE)s.falls++;}
    /** Returns true when a gate/finish is reached, or a fall occurs. */
    public boolean step(State s,int direction,boolean jump){
        if(!s.valid())throw new IllegalArgumentException("Invalid step state");
        if(s.pending||s.finished)return false;
        direction=Integer.compare(direction,0);
        int beforeY=s.y;
        boolean standing=grounded(s);
        if(jump&&standing)s.vy=JUMP;
        int nx=Math.max(checkpoint(s.gate),Math.min(stop(s.gate),s.x+direction*SPEED));
        if(s.vy!=0||!standing||!ground(nx)){
            s.vy--;s.y+=s.vy;
        }
        if(s.y<=0&&ground(nx)){
            if(beforeY<0){respawn(s);return true;}
            s.y=s.vy=0;
        }
        if(s.y< -84){respawn(s);return true;}
        for(Hazard h:hazards)if(!h.pit()&&nx+HALF>h.left&&nx-HALF<h.right&&s.y<h.height){
            respawn(s);return true;
        }
        s.x=nx;
        for(int i=0;i<petals.length;i++)if(Math.abs(s.x-petals[i][0])<=19&&Math.abs(s.y+23-petals[i][1])<=21)s.petals|=1<<i;
        if(s.x>=stop(s.gate)){
            s.y=s.vy=0;if(s.gate<AdventurePlan.GATES)s.pending=true;else s.finished=true;return true;
        }
        return false;
    }
    /** Preserve authoritative progress and prohibit stale or backward gate saves. */
    public static boolean canSaveMotion(State old,State proposed){
        if(!proposed.valid()||old.gate!=proposed.gate||old.answered!=proposed.answered
            ||old.falls>proposed.falls||(old.petals&proposed.petals)!=old.petals)return false;
        if(old.pending||old.finished)return old.json().toString().equals(proposed.json().toString());
        return true;
    }
}
