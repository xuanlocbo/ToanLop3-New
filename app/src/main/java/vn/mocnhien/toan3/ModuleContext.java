package vn.mocnhien.toan3;

import org.json.JSONObject;
import java.util.*;

/** One shared context per Activity. No duplicate data, balances, or persistence. */
final class ModuleContext {
    interface Navigation {
        void go(String screen);
        void home();
        void openLesson(int lesson);
        void openBook(String book);
        void openQuestion(JSONObject q,boolean fromReview);
        void openAdventure(int lesson);
    }
    final NativeUi ui;
    final Curriculum bank;
    final ProgressStore store;
    final Navigation nav;
    ModuleContext(NativeUi ui,Curriculum bank,ProgressStore store,Navigation nav){
        this.ui=ui;this.bank=bank;this.store=store;this.nav=nav;
    }
    String title(int l){return bank.lessons.get(l).optString("title","Bài "+l);}
    String bookName(String b){return b.equals("sgk")?"Sách giáo khoa":b.equals("vbt")?"Vở bài tập":"Kho báu";}
    int completed(){return store.completed(bank.questions.values());}
    int count(int l){return bank.lesson(l).size();}
    int done(int l){return store.completed(bank.lesson(l));}
    int reviewCount(){int n=0;for(String id:bank.questions.keySet())if(store.review(id))n++;return n;}
    boolean chest(int l){return store.done("BONUS."+l+".1")&&store.done("BONUS."+l+".2");}
    int gameResume(){
        for(int l=26;l<=44;l++){
            AdventureWorld.State a=store.adventureState(l);
            if(!a.finished&&(a.x!=40||a.gate>0||a.petals>0||a.pending||a.falls>0))return l;
        }
        return -1;
    }
    int clearedGames(){int n=0;for(int l=26;l<=44;l++)if(store.adventureBest(l)>=0)n++;return n;}
    List<JSONObject> section(String book){
        List<JSONObject> all=new ArrayList<>();
        for(int l=26;l<=44;l++)all.addAll(bank.section(l,book));
        return all;
    }
}
