package vn.mocnhien.toan3;

import android.content.Context;
import android.os.SystemClock;
import android.view.*;
import android.widget.*;

/** Foreground-only game loop. Motion is persisted at important events and periodic checkpoints. */
final class AdventurePanel extends LinearLayout {
    interface Host { void openGate(); void returnMap(); void sound(boolean positive); }
    final NativeUi ui;
    final ProgressStore store;
    final AdventureWorld world;
    final AdventureWorld.State state;
    final Host host;
    final AdventureScene scene;
    final TextView status;
    final Button left,right,jump,action;
    boolean leftHeld,rightHeld,jumpQueued,running,dirty,blocked,disposed;
    int tapDirection,tapFrames,face=1;
    long lastFrame,lastSave;
    double accumulator;
    static final double FRAME=1000.0/60;
    final Runnable frame=new Runnable(){public void run(){tick();}};

    AdventurePanel(Context c,ProgressStore store,int lesson,Host host){
        super(c);setOrientation(VERTICAL);setMotionEventSplittingEnabled(true);
        this.store=store;this.host=host;ui=new NativeUi(c);
        world=new AdventureWorld(lesson);state=store.adventureState(lesson);
        setBackground(ui.bg(NativeUi.BG,16));
        status=ui.text("",17,true);status.setGravity(Gravity.CENTER);addView(status,new LinearLayout.LayoutParams(-1,-2));
        scene=new AdventureScene(c,world,state);
        addView(scene,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout controls=ui.row();controls.setMotionEventSplittingEnabled(true);ui.pad(controls,3);
        addView(controls,new LinearLayout.LayoutParams(-1,-2));
        left=ui.button("◀ Trái",NativeUi.LAV,()->tap(-1));
        right=ui.button("Phải ▶",NativeUi.LAV,()->tap(1));
        jump=ui.button("↑ Nhảy",NativeUi.PINK,()->{if(playable())jumpQueued=true;});
        left.setContentDescription("Giữ để đi sang trái");
        right.setContentDescription("Giữ để đi sang phải");
        jump.setContentDescription("Nhảy qua chướng ngại vật");
        attachDirection(left,-1);attachDirection(right,1);
        ui.weight(controls,left,1);ui.weight(controls,right,1);ui.weight(controls,jump,1);
        action=ui.button("Giải toán để mở cổng",NativeUi.GREEN,()->{
            if(disposed)return;
            if(blocked){if(flush()){blocked=false;update();start();}return;}
            if(!flush())return;
            if(state.pending)host.openGate();else if(state.finished)host.returnMap();
        });
        ui.weight(controls,action,1.7f);
        lastSave=SystemClock.uptimeMillis();update();
    }
    boolean playable(){return !disposed&&!blocked&&!state.pending&&!state.finished;}
    void tap(int d){if(playable()){tapDirection=d;tapFrames=8;}}
    void attachDirection(Button b,int d){
        final long[] down={0};
        b.setOnTouchListener((v,event)->{
            switch(event.getActionMasked()){
                case MotionEvent.ACTION_DOWN:
                    if(!playable())return false;
                    down[0]=SystemClock.uptimeMillis();tapFrames=0;
                    if(d<0)leftHeld=true;else rightHeld=true;b.setPressed(true);return true;
                case MotionEvent.ACTION_UP:
                    if(d<0)leftHeld=false;else rightHeld=false;b.setPressed(false);
                    if(SystemClock.uptimeMillis()-down[0]<100)b.performClick();return true;
                case MotionEvent.ACTION_CANCEL:
                    if(d<0)leftHeld=false;else rightHeld=false;b.setPressed(false);return true;
                default:return true;
            }
        });
    }
    void releaseControls(){leftHeld=rightHeld=jumpQueued=false;tapFrames=0;left.setPressed(false);right.setPressed(false);}
    void update(){
        boolean play=playable();left.setEnabled(play);right.setEnabled(play);jump.setEnabled(play);
        left.setVisibility(play?VISIBLE:GONE);right.setVisibility(play?VISIBLE:GONE);jump.setVisibility(play?VISIBLE:GONE);
        action.setVisibility(play?GONE:VISIBLE);
        if(blocked){
            status.setText("Chưa lưu được. Hãy lưu lại trước khi tiếp tục.");action.setText("Lưu lại");
        }else if(state.finished){
            status.setText("Hoàn thành!  ✿ "+state.flowerCount()+"/12 cánh hoa • huy hiệu đã lưu");
            action.setText("Về bản đồ");
        }else if(state.pending){
            status.setText("Cổng "+(state.gate+1)+"/4 • "+(state.answered?"Đã giải đúng!":"Giải toán để qua cổng")+"   ✿ "+state.flowerCount()+"/12");
            action.setText(state.answered?"Tiếp tục qua cổng":"Mở nhiệm vụ toán");
        }else{
            status.setText("Cổng "+state.gate+"/4   •   ✿ "+state.flowerCount()+"/12   •   Giữ Phải, chạm Nhảy");
        }
        scene.invalidate();
    }
    boolean flush(){
        if(!dirty)return true;
        if(store.saveAdventureMotion(world.lesson,state)){dirty=false;lastSave=SystemClock.uptimeMillis();return true;}
        blocked=true;running=false;removeCallbacks(frame);releaseControls();update();return false;
    }
    void start(){
        if(running||!playable()||!isAttachedToWindow())return;
        running=true;lastFrame=0;accumulator=0;postOnAnimation(frame);
    }
    boolean pauseAndSave(){
        running=false;removeCallbacks(frame);releaseControls();
        return flush();
    }
    void dispose(){disposed=true;running=false;removeCallbacks(frame);releaseControls();}
    @Override protected void onDetachedFromWindow(){running=false;removeCallbacks(frame);releaseControls();super.onDetachedFromWindow();}
    void tick(){
        if(!running||!playable()||!isAttachedToWindow()){running=false;return;}
        long now=SystemClock.uptimeMillis();
        if(lastFrame==0)lastFrame=now;
        accumulator+=Math.min(85,Math.max(0,now-lastFrame));lastFrame=now;
        int oldPetals=state.petals,oldFalls=state.falls;
        boolean event=false;
        for(int i=0;i<5&&accumulator>=FRAME&&playable();i++){
            accumulator-=FRAME;
            int direction=rightHeld==leftHeld?0:rightHeld?1:-1;
            if(direction==0&&tapFrames>0){direction=tapDirection;tapFrames--;}
            if(direction!=0)face=direction;
            int x=state.x,y=state.y,vy=state.vy;
            event|=world.step(state,direction,jumpQueued);
            jumpQueued=false;
            dirty|=x!=state.x||y!=state.y||vy!=state.vy||event||oldPetals!=state.petals;
        }
        scene.face=face;scene.walking=leftHeld||rightHeld||tapFrames>0;scene.invalidate();
        boolean flower=oldPetals!=state.petals,fall=oldFalls!=state.falls;
        if(event||flower||fall||dirty&&now-lastSave>=700){
            if(!flush())return;
            if(flower||event&&!fall)host.sound(true);
            if(fall)host.sound(false);
            update();
        }
        if(playable()&&running)postOnAnimation(frame);else{running=false;releaseControls();update();}
    }
}
