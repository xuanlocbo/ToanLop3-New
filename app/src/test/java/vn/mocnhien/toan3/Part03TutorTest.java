package vn.mocnhien.toan3;
import org.junit.Test;
import org.json.JSONObject;
import static org.junit.Assert.*;
public final class Part03TutorTest {
    @Test public void preservesAllQuestionsAndScopesHints() throws Exception {
        Curriculum bank=new NativeCurriculumTest().load();
        int[] sgk={22,15,11,16,13,38},vbt={23,15,13,9,17,22};
        int total=0,illustrations=0;
        for(int l=26;l<=44;l++){
            boolean in=l>=30&&l<=35;
            Part03Tutor.Guide[] guides=Part03Tutor.guides(l);
            assertEquals(in?3:0,guides.length);
            if(in){
                assertEquals(sgk[l-30],bank.section(l,"sgk").size());
                assertEquals(vbt[l-30],bank.section(l,"vbt").size());
            }
            for(Part03Tutor.Guide g:guides){
                assertEquals(3,g.steps.length);
                if(!g.visual.isEmpty()){
                    JSONObject v=new JSONObject(g.visual);
                    assertTrue(MathDiagram.supports(v.getString("type")));illustrations++;
                }
            }
            for(String b:new String[]{"sgk","vbt","bonus"})for(JSONObject q:bank.section(l,b)){
                String before=q.toString();
                boolean scoped=in&&!b.equals("bonus");
                assertEquals(scoped,Part03Tutor.applies(q));
                String old=Part02Tutor.hint(q),hint=Part03Tutor.hint(q);
                if(scoped){total++;assertFalse(hint.trim().isEmpty());assertTrue(hint.startsWith(old));}
                else assertEquals(old,hint);
                assertEquals(before,q.toString());
            }
        }
        assertEquals(214,total);assertEquals(959,bank.questions.size());assertTrue(illustrations>=10);
    }
}