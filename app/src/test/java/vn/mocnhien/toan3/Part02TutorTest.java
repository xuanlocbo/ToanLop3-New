package vn.mocnhien.toan3;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import org.json.*;

public final class Part02TutorTest {
    @Test public void allSourceItemsRemainAndGuidesAreScoped() throws Exception {
        Curriculum b=new NativeCurriculumTest().load();
        int[] sgk={33,17,7,26},vbt={36,23,15,26};int total=0,divisions=0;
        for(int l=26;l<=44;l++){
            if(l<=29){
                assertEquals(sgk[l-26],b.section(l,"sgk").size());
                assertEquals(vbt[l-26],b.section(l,"vbt").size());
                assertEquals(3,Part02Tutor.guides(l).length);
            }else assertEquals(0,Part02Tutor.guides(l).length);
            for(String book:new String[]{"sgk","vbt","bonus"})for(JSONObject q:b.section(l,book)){
                String before=q.toString();boolean scoped=l<=29&&!book.equals("bonus");
                assertEquals(scoped,Part02Tutor.applies(q));
                if(scoped){
                    total++;assertFalse(q.optString("id"),Part02Tutor.hint(q).trim().isEmpty());
                    assertFalse(q.optString("id"),Part02Tutor.solution(q).trim().isEmpty());
                    JSONObject v=q.optJSONObject("visual");
                    if(v!=null&&"long_division".equals(v.optString("type"))){
                        divisions++;int n=v.getInt("dividend"),d=v.getInt("divisor");
                        JSONObject a=q.optJSONObject("answer");
                        if(a==null)a=q.optJSONObject("sampleAnswer");
                        assertNotNull(q.optString("id"),a);
                        assertEquals(q.optString("id"),n/d,a.getInt("quotient"));
                        assertEquals(q.optString("id"),n%d,a.getInt("remainder"));
                        List<String> steps=Part02Tutor.divisionSteps(n,d);
                        assertTrue(steps.get(steps.size()-1).contains(n+" : "+d+" = "+(n/d)));
                    }
                }else{
                    assertEquals(q.optString("hint"),Part02Tutor.hint(q));
                    assertEquals(q.optString("solution"),Part02Tutor.solution(q));
                }
                assertEquals("Presentation must not mutate "+q.optString("id"),before,q.toString());
            }
        }
        assertEquals(183,total);assertEquals(43,divisions);
        assertEquals(959,b.questions.size());
    }
    @Test public void divisionShowsLeadingAndInteriorZeroCorrectly(){
        assertEquals(2,Part02Tutor.divisionSteps(53,6).size());
        List<String> zero=Part02Tutor.divisionSteps(83,4);
        assertTrue(zero.get(1).contains("viết 0"));
        assertTrue(zero.get(2).contains("83 : 4 = 20 dư 3"));
        assertTrue(Part02Tutor.divisionSteps(60,2).get(2).contains("60 : 2 = 30"));
        for(int n=10;n<=99;n++)for(int d=1;d<=9;d++){
            List<String> steps=Part02Tutor.divisionSteps(n,d);
            String expected=n+" : "+d+" = "+n/d+(n%d==0?"":" dư "+n%d)+".";
            assertTrue(expected,steps.get(steps.size()-1).startsWith(expected));
        }
    }
}