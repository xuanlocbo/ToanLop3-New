package vn.mocnhien.toan3;

import android.app.Activity;
import android.os.Bundle;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.*;
import org.json.*;
import java.util.*;

/** Offline native learning shell. All questions are selected by curriculum ID, not fixed counts. */
public final class MainActivity extends Activity implements QuestionPanel.Host {
    private Curriculum bank;
    private ProgressStore store;
    private NativeUi ui;
    private LinearLayout root,body;
    private ScrollView scroll;
    private TextView stars;
    private ToneGenerator tone;
    private String screen="home",book="sgk",question="",lastId="";
    private int lesson=26,restoreScroll=0;
    private boolean reviewMode=false;
    private final String[] regions={"Vườn kẹo ngọt","Hồ cầu vồng","Thung lũng sao","Đồi mây hồng"};
    private final String[] regionArt={"🌸","🌈","⭐","☁"};
    private final int[] regionColors={0xffffd8e8,0xffdff4f4,0xffeee4fc,0xffffe5d9};
    private final int[] regionStart={26,30,36,41},regionEnd={29,35,40,44};

    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);ui=new NativeUi(this);
        getWindow().setStatusBarColor(NativeUi.BG);
        getWindow().setNavigationBarColor(NativeUi.BG);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        try{
            bank=new Curriculum(this);store=new ProgressStore(this,bank);
            JSONObject nav=store.nav();
            screen=nav.optString("screen","home");lesson=Math.max(26,Math.min(44,nav.optInt("lesson",26)));
            book=nav.optString("book","sgk");question=nav.optString("question");lastId=nav.optString("lastId");
            reviewMode=nav.optBoolean("reviewMode");restoreScroll=Math.max(0,nav.optInt("scroll"));
            if(!Arrays.asList("home","lesson","list","question","review","awards","settings").contains(screen))screen="home";
            if(!Arrays.asList("sgk","vbt","bonus").contains(book))book="sgk";
            if(screen.equals("question")&&!bank.questions.containsKey(question))screen="home";
            if(!bank.questions.containsKey(lastId))lastId="";
            try{tone=new ToneGenerator(AudioManager.STREAM_MUSIC,45);}catch(Exception ignored){}
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
    void go(String destination){
        hideKeyboard();screen=destination;restoreScroll=0;saveNav(0);render();
    }
    void home(){reviewMode=false;go("home");}
    void openLesson(int l){lesson=l;reviewMode=false;go("lesson");}
    void openBook(String b){book=b;reviewMode=false;go("list");}
    void openQuestion(JSONObject q,boolean fromReview){
        if(q==null)return;
        if(!store.unlocked(q)){Toast.makeText(this,"Hãy hoàn thành nhiệm vụ trước.",Toast.LENGTH_LONG).show();return;}
        lesson=q.optInt("lesson");book=q.optString("book").toLowerCase(Locale.ROOT);question=q.optString("id");lastId=question;
        reviewMode=fromReview;go("question");
    }
    String title(int l){return bank.lessons.get(l).optString("title","Bài "+l);}
    String bookName(String b){return b.equals("sgk")?"Sách giáo khoa":b.equals("vbt")?"Vở bài tập":"Kho báu";}
    int completed(){return store.completed(bank.questions.values());}
    int count(int l){return bank.lesson(l).size();}
    int done(int l){return store.completed(bank.lesson(l));}
    int reviewCount(){int n=0;for(String id:bank.questions.keySet())if(store.review(id))n++;return n;}
    boolean chest(int l){return store.done("BONUS."+l+".1")&&store.done("BONUS."+l+".2");}
    void render(){
        root=ui.column();root.setBackgroundColor(NativeUi.BG);ui.pad(root,9);
        LinearLayout header=ui.row();header.setBackground(ui.bg(NativeUi.WHITE,18));ui.pad(header,6);ui.add(root,header);
        if(!screen.equals("home")){Button back=ui.button("‹",NativeUi.PALE,this::back);back.setContentDescription("Quay lại");header.addView(back,new LinearLayout.LayoutParams(ui.dp(55),-2));}
        LinearLayout name=ui.column();header.addView(name,new LinearLayout.LayoutParams(0,-2,1));
        ui.label(name,"MỘC NHIÊN • TOÁN 3",23,true);
        ui.label(name,screen.equals("question")?"Bài "+lesson+" • "+title(lesson):"Hành trình học toán vui",15,false);
        stars=ui.text("★ "+store.balance(),21,true);stars.setTextColor(NativeUi.PINK);header.addView(stars,new LinearLayout.LayoutParams(-2,-2));
        scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.setClipToPadding(false);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        body=ui.column();ui.pad(body,5);scroll.addView(body,new ScrollView.LayoutParams(-1,-2));
        switch(screen){
            case "lesson":lessonPage();break;
            case "list":questionList();break;
            case "question":questionPage();break;
            case "review":reviewPage();break;
            case "awards":awardsPage();break;
            case "settings":settingsPage();break;
            default:homePage();break;
        }
        if(!screen.equals("question")){
            LinearLayout nav=ui.row();nav.setBackground(ui.bg(NativeUi.WHITE,18));ui.add(root,nav);
            ui.smallAction(nav,"Hành trình",screen.equals("home")?NativeUi.PALE:NativeUi.WHITE,this::home);
            ui.smallAction(nav,"Ôn tập",screen.equals("review")?NativeUi.PALE:NativeUi.WHITE,()->go("review"));
            ui.smallAction(nav,"Thành tích",screen.equals("awards")?NativeUi.PALE:NativeUi.WHITE,()->go("awards"));
            ui.smallAction(nav,"Cài đặt",screen.equals("settings")?NativeUi.PALE:NativeUi.WHITE,()->go("settings"));
        }
        setContentView(root);
        int y=restoreScroll;restoreScroll=0;if(y>0)scroll.post(()->scroll.scrollTo(0,y));
    }
    void progress(LinearLayout p,int current,int total){
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        bar.setMax(Math.max(1,total));bar.setProgress(current);bar.setProgressTintList(android.content.res.ColorStateList.valueOf(NativeUi.PINK));
        p.addView(bar,new LinearLayout.LayoutParams(-1,ui.dp(14)));
        ui.label(p,current+" / "+total+" mục đã hoàn thành",16,true);
    }
    void homePage(){
        LinearLayout intro=ui.card(NativeUi.WHITE);ui.add(body,intro);
        ui.label(intro,"🌸  Chào mừng đến vương quốc toán học!",25,true);
        progress(intro,completed(),bank.questions.size());
        if(!lastId.isEmpty()){JSONObject last=bank.questions.get(lastId);ui.action(intro,"▶  Tiếp tục Bài "+last.optInt("lesson")+" • "+bookName(last.optString("book").toLowerCase(Locale.ROOT)),NativeUi.PINK,()->openQuestion(last,false));}
        LinearLayout row=ui.row();ui.add(body,row);
        for(int i=0;i<regions.length;i++){
            final int z=i;LinearLayout region=ui.card(regionColors[i]);ui.weight(row,region,1);
            ui.label(region,regionArt[i]+" "+regions[i],20,true);
            ui.label(region,"Bài "+regionStart[i]+" – "+regionEnd[i],15,false);
            for(int l=regionStart[i];l<=regionEnd[i];l++){final int ll=l;ui.action(region,"Bài "+l+"  •  "+done(l)+"/"+count(l),done(l)==count(l)?NativeUi.MINT:NativeUi.WHITE,()->openLesson(ll));}
        }
        ui.label(body,"959 mục học và thử thách • 19 bài • học ngoại tuyến",15,false);
    }
    void lessonPage(){
        LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);ui.label(c,"Bài "+lesson+" — "+title(lesson),26,true);progress(c,done(lesson),count(lesson));
        if(!lastId.isEmpty()&&bank.questions.get(lastId).optInt("lesson")==lesson){JSONObject last=bank.questions.get(lastId);ui.action(c,"Tiếp tục câu đang học",NativeUi.PINK,()->openQuestion(last,false));}
        LinearLayout row=ui.row();ui.add(body,row);
        for(String b:new String[]{"sgk","vbt","bonus"}){
            List<JSONObject> qs=bank.section(lesson,b);LinearLayout box=ui.card(b.equals("bonus")?NativeUi.GOLD:NativeUi.PALE);ui.weight(row,box,1);
            ui.label(box,(b.equals("sgk")?"📖 ":b.equals("vbt")?"✏ ":"🎁 ")+bookName(b),23,true);progress(box,store.completed(qs),qs.size());
            ui.label(box,b.equals("bonus")?(chest(lesson)?"Kho báu đã mở!":"Hai thử thách • tối đa 300 sao"):"Khám phá và luyện tập",17,false);
            ui.action(box,"Mở "+bookName(b),NativeUi.WHITE,()->openBook(b));
        }
        ui.action(body,"Về hành trình",NativeUi.PALE,this::home);
    }
    void questionList(){
        List<JSONObject> list=bank.section(lesson,book);
        ui.label(body,"Bài "+lesson+" • "+bookName(book),24,true);progress(body,store.completed(list),list.size());
        JSONObject start=null;for(JSONObject q:list)if(!store.done(q.optString("id"))&&store.unlocked(q)){start=q;break;}
        if(start!=null){final JSONObject next=start;ui.action(body,"▶  Bắt đầu / tiếp tục mục chưa hoàn thành",NativeUi.PINK,()->openQuestion(next,false));}
        for(int i=0;i<list.size();i++)questionItem(list.get(i),i+1,false);
    }
    void questionItem(JSONObject q,int index,boolean fromReview){
        String id=q.optString("id");boolean unlocked=store.unlocked(q),done=store.done(id);
        LinearLayout box=ui.card(done?NativeUi.MINT:NativeUi.WHITE);ui.add(body,box);
        String prefix=fromReview?"Bài "+q.optInt("lesson")+" • "+q.optString("book"):"Mục "+index;
        if(!"BONUS".equals(q.optString("book")))prefix+=" • Trang "+q.optInt("page")+" • Câu "+q.optInt("exercise");
        ui.label(box,prefix+(done?"  ✓":unlocked?"":"  🔒"),15,true);
        String prompt=q.optString("prompt");ui.label(box,prompt,20,true);
        ui.action(box,unlocked?(done?"Xem lại":store.draft(id).length()>0?"Tiếp tục":"Làm bài"):"Hoàn thành bước trước",unlocked?NativeUi.PALE:0xffe5e0e3,()->openQuestion(q,fromReview));
    }
    void questionPage(){
        JSONObject q=bank.questions.get(question);
        if(q==null){ui.label(body,"Không tìm thấy câu này.",23,true);ui.action(body,"Về hành trình",NativeUi.PALE,this::home);return;}
        List<JSONObject> section=bank.section(lesson,book);int idx=section.indexOf(q);
        ui.label(body,bookName(book)+" • Mục "+(idx+1)+"/"+section.size()+" • "+store.completed(section)+" đã hoàn thành",16,true);
        LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);c.addView(new QuestionPanel(this,q,store,this),new LinearLayout.LayoutParams(-1,-2));
        ui.action(body,reviewMode?"Về ôn tập":"Danh sách mục học",NativeUi.PALE,()->go(reviewMode?"review":"list"));
    }
    void reviewPage(){
        ui.label(body,"Ôn lại câu chưa đúng • "+reviewCount()+" mục",26,true);int i=0;
        for(JSONObject q:bank.questions.values())if(store.review(q.optString("id")))questionItem(q,++i,true);
        if(i==0){LinearLayout c=ui.card(NativeUi.MINT);ui.add(body,c);ui.label(c,"🌸 Chưa có câu nào cần ôn lại.",24,true);ui.label(c,"Khi làm sai, câu hỏi sẽ tự xuất hiện ở đây.",19,false);}
    }
    void awardsPage(){
        LinearLayout summary=ui.card(NativeUi.GOLD);ui.add(body,summary);ui.label(summary,"★  "+store.balance()+" sao hồng",30,true);int n=0;for(int l=26;l<=44;l++)if(chest(l))n++;ui.label(summary,"🎁  "+n+" / 19 kho báu đã mở",23,true);progress(summary,completed(),bank.questions.size());
        for(int l=26;l<=44;l++){final int ll=l;LinearLayout c=ui.card(chest(l)?NativeUi.GOLD:NativeUi.WHITE);ui.add(body,c);ui.label(c,"Bài "+l+" • "+(chest(l)?"Kho báu đã mở 🎁":"Kho báu đang chờ"),20,true);progress(c,done(l),count(l));ui.action(c,"Mở bài "+l,NativeUi.PALE,()->openLesson(ll));}
    }
    void settingsPage(){
        LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);ui.label(c,"Cài đặt",25,true);
        ui.action(c,store.sound()?"Âm thanh: đang bật":"Âm thanh: đang tắt",NativeUi.PALE,()->{if(store.setSound(!store.sound()))render();else Toast.makeText(this,"Chưa lưu được cài đặt.",Toast.LENGTH_LONG).show();});
        ui.label(c,"Tiến độ, bài đang làm và phần thưởng được lưu tự động trên thiết bị.",20,true);
        ui.label(c,"Học ngoại tuyến • Không đăng nhập • Không quảng cáo",19,false);
        ui.label(c,"SGK + VBT Toán 3, bài 26–44. Thử thách nâng cao tự soạn.",18,false);
        ui.label(c,"Bản phát triển 1.1 • Android native",16,false);
    }
    @Override public void changed(){if(stars!=null)stars.setText("★ "+store.balance());}
    @Override public void next(){
        if(reviewMode){go("review");return;}
        List<JSONObject> qs=bank.section(lesson,book);int current=-1;
        for(int i=0;i<qs.size();i++)if(qs.get(i).optString("id").equals(question)){current=i;break;}
        for(int i=current+1;i<qs.size();i++){JSONObject q=qs.get(i);if(!store.done(q.optString("id"))&&store.unlocked(q)){openQuestion(q,false);return;}}
        for(JSONObject q:qs)if(!store.done(q.optString("id"))&&store.unlocked(q)){openQuestion(q,false);return;}
        go("lesson");
    }
    @Override public void feedback(boolean correct){if(store.sound()&&tone!=null)tone.startTone(correct?ToneGenerator.TONE_PROP_ACK:ToneGenerator.TONE_PROP_NACK,160);}
    void back(){switch(screen){case "question":go(reviewMode?"review":"list");break;case "list":go("lesson");break;default:home();break;}}
    @Override public void onBackPressed(){if(store!=null&&!screen.equals("home"))back();else super.onBackPressed();}
    @Override protected void onPause(){if(store!=null)saveNav(scroll==null?0:scroll.getScrollY());super.onPause();}
    @Override protected void onDestroy(){if(tone!=null)tone.release();super.onDestroy();}
}
