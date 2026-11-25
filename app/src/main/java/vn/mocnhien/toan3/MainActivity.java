package vn.mocnhien.toan3;

import android.app.Activity;
import android.os.Bundle;
import android.media.AudioManager;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;
import org.json.*;
import java.util.*;

/** Offline native learning shell. All questions are selected by curriculum ID, not fixed counts. */
public final class MainActivity extends Activity implements QuestionPanel.Host, ModuleContext.Navigation {
    private Curriculum bank;
    private ProgressStore store;
    private NativeUi ui;
    private ModuleContext modules;
    private LinearLayout root,body;
    private ScrollView scroll;
    private TextView stars,questionProgress;
    private GameAudio audio;
    private AdventurePanel gamePanel;
    private String screen="home",book="sgk",question="",lastId="";
    private int lesson=26,restoreScroll=0;
    private boolean reviewMode=false;

    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);ui=new NativeUi(this);
        getWindow().setStatusBarColor(NativeUi.BG);
        getWindow().setNavigationBarColor(NativeUi.BG);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        try{
            bank=new Curriculum(this);AdventurePlan.verify(bank);store=new ProgressStore(this,bank);modules=new ModuleContext(ui,bank,store,this);
            JSONObject nav=store.nav();
            screen=nav.optString("screen","home");lesson=Math.max(26,Math.min(44,nav.optInt("lesson",26)));
            book=nav.optString("book","sgk");question=nav.optString("question");lastId=nav.optString("lastId");
            reviewMode=nav.optBoolean("reviewMode");restoreScroll=Math.max(0,nav.optInt("scroll"));
            if(!AppModule.known(screen))screen="home";
            if(!Arrays.asList("sgk","vbt","bonus").contains(book))book="sgk";
            if(screen.equals("question")&&!bank.questions.containsKey(question))screen="home";
            if(screen.equals("gate")&&!store.adventureState(lesson).pending)screen="game";
            if(!bank.questions.containsKey(lastId))lastId="";
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            try{audio=new GameAudio(this);audio.enabled(store.sound());}catch(Exception e){android.util.Log.w("MocNhien","Sound unavailable",e);}
            render();
        }catch(Exception e){
            android.util.Log.e("MocNhien","Cannot load curriculum or progress",e);
            LinearLayout failure=ui.card(NativeUi.BG);ui.pad(failure,24);
            ui.label(failure,"Chưa mở được bài học",26,true);
            ui.label(failure,"Dữ liệu chưa được xóa. Hãy mở lại ứng dụng hoặc kiểm tra bản cài đặt.",20,false);
            ui.action(failure,"Thử mở lại",NativeUi.PINK,this::recreate);setContentView(failure);
        }
    }
    void hideKeyboard(){View v=getCurrentFocus();if(v!=null){InputMethodManager m=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);if(m!=null)m.hideSoftInputFromWindow(v.getWindowToken(),0);v.clearFocus();}}
    JSONObject nav(int y){
        JSONObject n=new JSONObject();Json.put(n,"screen",screen);Json.put(n,"lesson",lesson);Json.put(n,"book",book);Json.put(n,"question",question);
        Json.put(n,"lastId",lastId);Json.put(n,"reviewMode",reviewMode);Json.put(n,"scroll",Math.max(0,y));return n;
    }
    boolean saveNav(int y){if(store==null)return false;if(store.setNav(nav(y)))return true;Toast.makeText(this,"Chưa lưu được vị trí học. Hãy thử lại.",Toast.LENGTH_LONG).show();return false;}
    public void go(String destination){
        if(!AppModule.known(destination))throw new IllegalArgumentException("Unknown screen");
        hideKeyboard();
        if(gamePanel!=null){if(!gamePanel.pauseAndSave())return;gamePanel.dispose();gamePanel=null;}
        if(!destination.equals("question"))reviewMode=false;
        screen=destination;restoreScroll=0;saveNav(0);render();
    }
    public void home(){reviewMode=false;go("home");}
    public void openLesson(int l){lesson=l;reviewMode=false;go("lesson");}
    public void openBook(String b){book=b;reviewMode=false;go("list");}
    public void openQuestion(JSONObject q,boolean fromReview){
        if(q==null)return;
        if(!store.unlocked(q)){Toast.makeText(this,"Hãy hoàn thành nhiệm vụ trước.",Toast.LENGTH_LONG).show();return;}
        lesson=q.optInt("lesson");book=q.optString("book").toLowerCase(Locale.ROOT);question=q.optString("id");lastId=question;
        reviewMode=fromReview;go("question");
    }
    String title(int l){return modules.title(l);}
    String bookName(String b){return modules.bookName(b);}
    void render(){
        questionProgress=null;
        if(screen.equals("game")){renderGame();return;}
        root=ui.column();root.setBackground(ui.wash(NativeUi.BG,0xffffeaf1,0));ui.pad(root,ui.compact()?5:9);
        LinearLayout header=ui.row();header.setBackground(ui.outlined(NativeUi.WHITE,20,NativeUi.OUTLINE));ui.pad(header,ui.compact()?3:6);ui.add(root,header);
        if(!screen.equals("home")){Button back=ui.button("‹",NativeUi.PALE,this::back);back.setContentDescription("Quay lại");header.addView(back,new LinearLayout.LayoutParams(ui.dp(55),-2));}
        LinearLayout name=ui.column();header.addView(name,new LinearLayout.LayoutParams(0,-2,1));
        ui.label(name,"Mộc Nhiên · Toán 3",ui.compact()?20:23,true);
        if(screen.equals("question")||screen.equals("gate"))ui.caption(name,"Bài "+lesson+" • "+title(lesson));
        else if(!ui.compact())ui.caption(name,"Vương quốc toán học");
        stars=ui.badge("★ "+store.balance(),NativeUi.GOLD);stars.setTextColor(NativeUi.INK);header.addView(stars,new LinearLayout.LayoutParams(-2,-2));
        Button settings=ui.button("⚙",NativeUi.PALE,()->go("settings"));
        settings.setContentDescription("Cài đặt");header.addView(settings,new LinearLayout.LayoutParams(ui.dp(48),-2));
        scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setClipToPadding(false);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        body=ui.column();ui.pad(body,5);scroll.addView(body,new ScrollView.LayoutParams(-1,-2));
        switch(screen){
            case "learn":new LearningModule(modules,body).show(lastId);break;
            case "lesson":new LearningModule(modules,body).showLesson(lesson,lastId);break;
            case "list":new LearningModule(modules,body).showList(lesson,book);break;
            case "question":questionPage();break;
            case "adventure":new GameModule(modules,body).show(lesson);break;
            case "gate":gatePage();break;
            case "review":new ProgressModule(modules,body).showReview();break;
            case "awards":new RewardsModule(modules,body).show();break;
            case "progress":new ProgressModule(modules,body).show();break;
            case "settings":settingsPage();break;
            default:new ModuleHome(modules,body).show(lastId);break;
        }
        if(!screen.equals("home")&&!screen.equals("question")&&!screen.equals("gate")){
            LinearLayout nav=ui.row();nav.setBackground(ui.outlined(NativeUi.WHITE,20,NativeUi.OUTLINE));ui.add(root,nav);
            for(AppModule m:AppModule.values()){
                ui.smallAction(nav,m.label,AppModule.owner(screen,reviewMode)==m?m.color:NativeUi.WHITE,()->go(m.route));
            }
        }
        setContentView(root);
        int y=restoreScroll;restoreScroll=0;if(y>0)scroll.post(()->scroll.scrollTo(0,y));
    }
    public void openAdventure(int l){lesson=l;reviewMode=false;go("game");}
    void renderGame(){
        root=ui.column();root.setBackgroundColor(NativeUi.BG);ui.pad(root,6);
        LinearLayout top=ui.row();top.setBackground(ui.bg(NativeUi.WHITE,14));ui.add(root,top);
        Button back=ui.button("‹ Bản đồ",NativeUi.PALE,()->go("adventure"));
        top.addView(back,new LinearLayout.LayoutParams(ui.dp(110),-2));
        TextView title=ui.text("Bài "+lesson+" • "+AdventurePlan.name(lesson),21,true);
        top.addView(title,new LinearLayout.LayoutParams(0,-2,1));
        stars=ui.badge("★ "+store.balance(),NativeUi.GOLD);top.addView(stars,new LinearLayout.LayoutParams(-2,-2));
        scroll=null;body=null;
        gamePanel=new AdventurePanel(this,store,lesson,new AdventurePanel.Host(){
            public void openGate(){go("gate");}
            public void returnMap(){go("adventure");}
            public void nextStage(){if(lesson<AdventurePlan.LAST)openAdventure(lesson+1);else go("adventure");}
            public void sound(SoundPalette.Cue cue){play(cue);}
        });
        root.addView(gamePanel,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);gamePanel.start();
    }
    void gatePage(){
        AdventureWorld.State a=store.adventureState(lesson);
        if(!a.pending){ui.label(body,"Hãy tới cổng trong màn chơi.",22,true);ui.action(body,"Trở lại màn chơi",NativeUi.PINK,()->go("game"));return;}
        if(a.gate==0&&!store.trailComplete(lesson)){
            LinearLayout station=ui.card(NativeUi.WHITE);ui.add(body,station);
            Runnable next=()->{play(SoundPalette.Cue.STATION);render();};
            View panel=CandyStationSession.supports(lesson)?new CandyStationPanel(this,store,lesson,next):RainbowStationSession.supports(lesson)?new RainbowStationPanel(this,store,lesson,next):new FinalStationPanel(this,store,lesson,next);
            station.addView(panel,new LinearLayout.LayoutParams(-1,-2));
            ui.action(body,"Về màn chơi (giữ nhiệm vụ đang làm)",NativeUi.PALE,()->go("game"));return;
        }
        JSONObject q=bank.questions.get(AdventurePlan.questionId(lesson,a.gate));
        ui.label(body,"🐇 Bài "+lesson+" • Cổng "+(a.gate+1)+"/4 • "+AdventurePlan.name(lesson),23,true);
        ui.label(body,a.gate<2?"Giải đúng để mở cổng.":"Thử thách kho báu! Giải đúng để nhận thưởng lớn; có thể làm sau.",18,false);
        LinearLayout card=ui.card(NativeUi.WHITE);ui.add(body,card);
        card.addView(new QuestionPanel(this,q,store,this,lesson,a.gate),new LinearLayout.LayoutParams(-1,-2));
        if(a.gate>=2&&!a.answered){
            ui.action(body,"Thử thách này để sau",NativeUi.PALE,()->new android.app.AlertDialog.Builder(this)
                .setTitle("Làm thử thách sau?")
                .setMessage("Em vẫn có thể về đích. Bỏ qua không nhận thêm sao; phần đang làm được lưu để học tiếp trong Kho báu.")
                .setNegativeButton("Ở lại",null).setPositiveButton("Đi tiếp",(d,w)->{
                    if(store.skipAdventureBonus(lesson,a.gate))go("game");else Toast.makeText(this,"Chưa lưu được. Hãy thử lại.",Toast.LENGTH_LONG).show();
                }).show());
        }
        ui.action(body,"Về màn chơi (giữ bài đang làm)",NativeUi.PALE,()->go("game"));
    }
    void questionPage(){
        JSONObject q=bank.questions.get(question);
        if(q==null){ui.label(body,"Không tìm thấy câu này.",23,true);ui.action(body,"Về hành trình",NativeUi.PALE,this::home);return;}
        List<JSONObject> section=bank.section(lesson,book);int idx=section.indexOf(q);
        questionProgress=ui.text(bookName(book)+" • Mục "+(idx+1)+"/"+section.size()+" • "+store.completed(section)+" đã hoàn thành",16,true);
        ui.add(body,questionProgress);
        LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);c.addView(new QuestionPanel(this,q,store,this),new LinearLayout.LayoutParams(-1,-2));
        ui.action(body,reviewMode?"Về ôn tập":"Danh sách mục học",NativeUi.PALE,()->go(reviewMode?"review":"list"));
    }
    void settingsPage(){
        LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);ui.label(c,"Cài đặt",25,true);
        ui.action(c,store.sound()?"Âm thanh: đang bật":"Âm thanh: đang tắt",NativeUi.PALE,()->{if(store.setSound(!store.sound())){if(audio!=null)audio.enabled(store.sound());if(store.sound())play(SoundPalette.Cue.PETAL);render();}else Toast.makeText(this,"Chưa lưu được cài đặt.",Toast.LENGTH_LONG).show();});
        ui.label(c,"Tiến độ, bài đang làm và phần thưởng được lưu tự động trên thiết bị.",20,true);
        ui.label(c,"Học ngoại tuyến • Không đăng nhập • Không quảng cáo",19,false);
        ui.label(c,"SGK + VBT Toán 3, bài 26–44. Thử thách nâng cao tự soạn.",18,false);
        ui.caption(c,"Bản 1.2.0-p15 • Vương quốc toán học");
    }
    @Override public void changed(){
        if(stars!=null)stars.setText("★ "+store.balance());
        if(questionProgress!=null&&screen.equals("question")){
            List<JSONObject> section=bank.section(lesson,book);JSONObject q=bank.questions.get(question);
            questionProgress.setText(bookName(book)+" • Mục "+(section.indexOf(q)+1)+"/"+section.size()+" • "+store.completed(section)+" đã hoàn thành");
        }
    }
    @Override public void next(){
        if(screen.equals("gate")){AdventureWorld.State a=store.adventureState(lesson);if(store.advanceAdventure(lesson,a.gate))go("game");else Toast.makeText(this,"Chưa qua được cổng. Hãy kiểm tra kết quả.",Toast.LENGTH_LONG).show();return;}
        if(reviewMode){go("review");return;}
        List<JSONObject> qs=bank.section(lesson,book);int current=-1;
        for(int i=0;i<qs.size();i++)if(qs.get(i).optString("id").equals(question)){current=i;break;}
        for(int i=current+1;i<qs.size();i++){JSONObject q=qs.get(i);if(!store.done(q.optString("id"))&&store.unlocked(q)){openQuestion(q,false);return;}}
        for(JSONObject q:qs)if(!store.done(q.optString("id"))&&store.unlocked(q)){openQuestion(q,false);return;}
        go("lesson");
    }
    void play(SoundPalette.Cue cue){if(store!=null&&store.sound()&&audio!=null)audio.play(cue);}
    @Override public void feedback(boolean correct){hideKeyboard();play(correct?SoundPalette.Cue.CORRECT:SoundPalette.Cue.TRY_AGAIN); }
    void back(){go(AppModule.back(screen,reviewMode));}
    @Override public void onBackPressed(){if(store!=null&&!screen.equals("home"))back();else super.onBackPressed();}
    @Override protected void onPause(){if(audio!=null)audio.foreground(false);if(gamePanel!=null)gamePanel.pauseAndSave();if(store!=null)saveNav(scroll==null?0:scroll.getScrollY());super.onPause();}
    @Override protected void onResume(){super.onResume();if(audio!=null)audio.foreground(true);if(gamePanel!=null)gamePanel.start();}
    @Override protected void onDestroy(){if(gamePanel!=null){gamePanel.dispose();gamePanel=null;}if(audio!=null){audio.release();audio=null;}super.onDestroy();}
}
