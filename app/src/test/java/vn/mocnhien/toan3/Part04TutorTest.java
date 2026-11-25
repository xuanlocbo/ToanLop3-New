package vn.mocnhien.toan3;
import org.junit.Test;
import org.json.*;
import java.util.List;
import static org.junit.Assert.*;

public final class Part04TutorTest {
    @Test public void sourceScopeAndIndependentArithmetic() throws Exception {
        Curriculum bank=new NativeCurriculumTest().load();
        int[] sgk={21,37,27,21,18},vbt={25,32,41,23,21};
        int count=0,muls=0,divs=0,expressions=0;
        for(int l=26;l<=44;l++){
            boolean in=l>=36&&l<=40;
            assertEquals(in?3:0,Part04Tutor.guides(l).length);
            if(in){assertEquals(sgk[l-36],bank.section(l,"sgk").size());assertEquals(vbt[l-36],bank.section(l,"vbt").size());}
            for(Part04Tutor.Guide g:Part04Tutor.guides(l)){
                assertEquals(3,g.steps.length);
                for(String step:g.steps)assertFalse(step.trim().isEmpty());
            }
            for(String b:new String[]{"sgk","vbt","bonus"})for(JSONObject q:bank.section(l,b)){
                boolean scoped=in&&!b.equals("bonus");String id=q.optString("id"),before=q.toString();
                assertEquals(id,scoped,Part04Tutor.applies(q));
                String hint=Part04Tutor.hint(q),solution=Part04Tutor.solution(q);
                String oldHint=Part03Tutor.hint(q),oldSolution=Part02Tutor.solution(q);
                if(scoped){
                    count++;assertFalse(id,hint.trim().isEmpty());assertTrue(id,hint.startsWith(oldHint));
                    assertTrue(id,solution.startsWith(oldSolution));
                    JSONObject v=Json.obj(q,"visual");JSONArray raw=Json.arr(v,"steps");
                    Object key="study".equals(q.optString("kind"))?q.opt("sampleAnswer"):q.opt("answer");
                    switch(v.optString("type")){
                        case "long_multiplication":{
                            muls++;int n=v.getInt("multiplicand"),d=v.getInt("multiplier");
                            assertTrue(id,Json.same(key,n*d));List<Part04Tutor.Mul> calc=Part04Tutor.multiply(n,d);
                            assertEquals(id,raw.length(),calc.size());
                            for(int i=0;i<calc.size();i++){
                                JSONObject r=raw.getJSONObject(i);Part04Tutor.Mul a=calc.get(i);
                                assertEquals(id,r.getInt("digit"),a.digit);assertEquals(id,r.getInt("carryIn"),a.carryIn);
                                assertEquals(id,r.getInt("product"),a.total);assertEquals(id,r.getInt("resultDigit"),a.written);
                                assertEquals(id,r.getInt("carryOut"),a.carryOut);
                            }
                            assertTrue(id,solution.contains("Cách tính từng bước"));break;
                        }
                        case "long_division":{
                            divs++;int n=v.getInt("dividend"),d=v.getInt("divisor");JSONObject k=(JSONObject)key;
                            assertEquals(id,n/d,k.getInt("quotient"));assertEquals(id,n%d,k.getInt("remainder"));
                            List<Part04Tutor.Div> calc=Part04Tutor.divide(n,d);assertEquals(id,raw.length(),calc.size());
                            for(int i=0;i<calc.size();i++){
                                JSONObject r=raw.getJSONObject(i);Part04Tutor.Div a=calc.get(i);
                                assertEquals(id,r.getInt("partialDividend"),a.partial);assertEquals(id,r.getInt("quotientDigit"),a.quotient);
                                assertEquals(id,r.getInt("product"),a.product);assertEquals(id,r.getInt("remainder"),a.remainder);
                            }
                            assertTrue(id,solution.contains("Cách tính từng bước"));break;
                        }
                        case "expression":{
                            expressions++;Part04Tutor.Expression e=Part04Tutor.expression(v.getString("expression"));
                            assertNotNull(id,e);assertTrue(id,Json.same(key,e.value));assertTrue(id,solution.contains("Cách tính từng bước"));break;
                        }
                    }
                }else{assertEquals(id,oldHint,hint);assertEquals(id,oldSolution,solution);}
                assertEquals(id,before,q.toString());
            }
        }
        assertEquals(266,count);assertEquals(22,muls);assertEquals(34,divs);assertEquals(13,expressions);
        assertEquals(959,bank.questions.size());
    }
    @Test public void exhaustiveThreeDigitArithmetic(){
        for(int n=100;n<=999;n++)for(int d=1;d<=9;d++){
            int value=0,power=1,carry=0;
            for(Part04Tutor.Mul s:Part04Tutor.multiply(n,d)){
                assertEquals(carry,s.carryIn);assertEquals(s.digit*d+carry,s.total);
                value+=s.written*power;power*=10;carry=s.carryOut;
            }
            assertEquals(n*d,value+carry*power);
            int quotient=0,remainder=0;
            for(Part04Tutor.Div s:Part04Tutor.divide(n,d)){
                quotient=quotient*10+s.quotient;remainder=s.remainder;
                assertEquals(s.partial,s.product+s.remainder);assertTrue(remainder>=0&&remainder<d);
            }
            assertEquals(n/d,quotient);assertEquals(n%d,remainder);
        }
        assertEquals(0,Part04Tutor.divide(437,4).get(1).quotient);
        assertEquals(0,Part04Tutor.divide(305,6).get(1).quotient);
    }
    @Test public void expressionPrecedenceAndRejectUnsupported(){
        String[] inputs={"58 − 18 + 12","25 + 6 × 4","48 : (9 − 3)","30 : 5 × 2","42 − (42 − 5)","126 + 38 + 62","8 × (11 − 6)","(2+3)×(4+5)"};
        int[] expected={52,49,8,12,5,226,40,45};
        for(int i=0;i<inputs.length;i++){
            Part04Tutor.Expression e=Part04Tutor.expression(inputs[i]);assertNotNull(inputs[i],e);
            assertEquals(inputs[i],expected[i],e.value);assertEquals(Integer.toString(e.value),e.lines.get(e.lines.size()-1));
        }
        for(String s:new String[]{"4:0","5:2","3−5","2(3+4)","(2+3","2++3","2 3+4","x+1","3+","3()","3.2+4"}){
            if(s.equals("2 3+4"))continue; // Whitespace is normalized for book-style grouped digits.
            assertNull(s,Part04Tutor.expression(s));
        }
    }
}
