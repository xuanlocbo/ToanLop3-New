package vn.mocnhien.toan3;
import java.util.*;
/** Compile with the four content classes using javac; no Android dependencies. */
public final class LearningContentCheck {
 static void require(boolean ok,String msg){if(!ok)throw new AssertionError(msg);}
 public static void main(String[] args){
  int s=0,v=0,b=0;Set<String> ids=new HashSet<>();
  for(int l=26;l<=44;l++){
   for(LearningContent.Mode mode:new LearningContent.Mode[]{LearningContent.Mode.SGK,LearningContent.Mode.VBT,LearningContent.Mode.CHALLENGE}){
    List<BookBank.Q> questions=LearningContent.questions(l,mode);
    require(!questions.isEmpty(),"empty "+l+" "+mode);
    if(mode==LearningContent.Mode.CHALLENGE)require(questions.size()==2,"bonus count "+l);
    for(BookBank.Q q:questions){
     require(ids.add(q.id()),"duplicate "+q.id());require(q.lesson==l&&!q.prompt.isEmpty()&&!q.hint.isEmpty()&&!q.explain.isEmpty(),"missing material "+q.id());
     if(mode==LearningContent.Mode.SGK){s++;require(q.page>=LearningContent.SGK_PAGES[l-26]&&q.page<LearningContent.SGK_PAGES[l-25],"SGK page "+q.id());}
     if(mode==LearningContent.Mode.VBT){v++;require(q.page>=LearningContent.VBT_PAGES[l-26]&&q.page<LearningContent.VBT_PAGES[l-25],"VBT page "+q.id());}
     if(mode==LearningContent.Mode.CHALLENGE)b++;
     if(q.kind==BookBank.Kind.DIVISION)require(q.b>0&&q.answer.equals((q.a/q.b)+","+(q.a%q.b)),"division "+q.id());
     if(q.kind==BookBank.Kind.MULTIPLY)require(q.answer.equals(Integer.toString(q.a*q.b)),"multiply "+q.id());
     if(q.kind==BookBank.Kind.CHOICE||q.kind==BookBank.Kind.GEOMETRY)require(q.options.length==0||Arrays.asList(q.options).contains(q.answer),"options "+q.id());
     if(q.kind!=BookBank.Kind.DRAW&&q.kind!=BookBank.Kind.PRACTICE)require(LearningContent.check(q,q.answer),"grader "+q.id());else require(!LearningContent.check(q,"done"),"manual false completion "+q.id());
     require(!LearningContent.check(q,null),"null accepted "+q.id());
    }
   }
   List<BookBank.Q> game=LearningContent.questions(l,LearningContent.Mode.GAME);
   require(game.size()==8,"game selection "+l);
   for(BookBank.Q q:game)require(q.lesson==l&&q.kind!=BookBank.Kind.PRACTICE,"game topic "+q.id());
  }
  require(s>=420&&v>=476&&b==38,"content regression");
  System.out.println("LEARNING_CONTENT_PASS SGK="+s+" VBT="+v+" BONUS="+b);
 }
}
