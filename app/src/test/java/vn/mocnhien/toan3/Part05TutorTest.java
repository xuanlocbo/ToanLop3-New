package vn.mocnhien.toan3;
import org.junit.Test;
import org.json.*;
import java.util.*;
import static org.junit.Assert.*;

public final class Part05TutorTest {
    @Test public void preserveScopeAndReviewArithmetic()throws Exception{
        Curriculum bank=new NativeCurriculumTest().load();
        int[] sgk={47,14,28,26},vbt={53,26,39,25};int total=0,muls=0,divs=0,exps=0,guides=0;
        for(int l=26;l<=44;l++){
            boolean in=l>=41&&l<=44;
            if(in){assertEquals(sgk[l-41],bank.section(l,"sgk").size());assertEquals(vbt[l-41],bank.section(l,"vbt").size());}
            Part05Tutor.Guide[] gg=Part05Tutor.guides(l);assertEquals(in?(l==43?6:3):0,gg.length);
            for(Part05Tutor.Guide g:gg){guides++;assertEquals(3,g.steps.length);for(String s:g.steps)assertFalse(s.trim().isEmpty());if(!g.visual.isEmpty())assertTrue(MathDiagram.supports(new JSONObject(g.visual).getString("type")));}
            for(String b:new String[]{"sgk","vbt","bonus"})for(JSONObject q:bank.section(l,b)){
                String id=q.optString("id"),before=q.toString(),oh=Part04Tutor.hint(q),os=Part04Tutor.solution(q),h=Part05Tutor.hint(q),s=Part05Tutor.solution(q);
                boolean scoped=in&&!b.equals("bonus");assertEquals(id,scoped,Part05Tutor.applies(q));
                if(scoped){total++;assertTrue(id,h.startsWith(oh));assertTrue(id,s.startsWith(os));assertFalse(id,h.trim().isEmpty());
                    JSONObject v=Json.obj(q,"visual");JSONArray raw=Json.arr(v,"steps");
                    switch(v.optString("type")){
                        case "long_multiplication":{
                            muls++;int n=v.getInt("multiplicand"),d=v.getInt("multiplier");assertTrue(id,Json.same(q.opt("answer"),n*d));
                            List<Part04Tutor.Mul> a=Part04Tutor.multiply(n,d);assertEquals(id,raw.length(),a.size());
                            for(int i=0;i<a.size();i++){JSONObject x=raw.getJSONObject(i);Part04Tutor.Mul y=a.get(i);
                                assertEquals(id,x.getInt("digit"),y.digit);assertEquals(id,x.getInt("carryIn"),y.carryIn);assertEquals(id,x.getInt("product"),y.total);assertEquals(id,x.getInt("resultDigit"),y.written);assertEquals(id,x.getInt("carryOut"),y.carryOut);}
                            assertTrue(id,s.contains("Ôn lại cách giải"));break;
                        }
                        case "long_division":{
                            divs++;int n=v.getInt("dividend"),d=v.getInt("divisor");JSONObject k=q.getJSONObject("answer");
                            assertEquals(id,n/d,k.getInt("quotient"));assertEquals(id,n%d,k.getInt("remainder"));
                            List<Part04Tutor.Div> a=Part04Tutor.divide(n,d);assertEquals(id,raw.length(),a.size());
                            for(int i=0;i<a.size();i++){JSONObject x=raw.getJSONObject(i);Part04Tutor.Div y=a.get(i);
                                assertEquals(id,x.getInt("partialDividend"),y.partial);assertEquals(id,x.getInt("quotientDigit"),y.quotient);assertEquals(id,x.getInt("product"),y.product);assertEquals(id,x.getInt("remainder"),y.remainder);}
                            assertTrue(id,s.contains("Ôn lại cách giải"));break;
                        }
                        case "expression":{
                            exps++;Part04Tutor.Expression e=Part04Tutor.expression(v.getString("expression"));assertNotNull(id,e);assertTrue(id,Json.same(q.opt("answer"),e.value));break;
                        }
                    }
                }else{assertEquals(id,oh,h);assertEquals(id,os,s);}
                assertEquals(id,before,q.toString());
            }
        }
        assertEquals(258,total);assertEquals(24,muls);assertEquals(24,divs);assertEquals(21,exps);assertEquals(15,guides);assertEquals(959,bank.questions.size());
    }
    @Test public void verifyGeometryFromCoordinatesAndModels()throws Exception{
        Curriculum bank=new NativeCurriculumTest().load();int mid=0,right=0,notRight=0,circle=0,paint=0,stack=0,poly=0;
        for(int l=43;l<=44;l++)for(String b:new String[]{"sgk","vbt"})for(JSONObject q:bank.section(l,b)){
            JSONObject v=Json.obj(q,"visual");String type=v.optString("type"),id=q.optString("id"),p=q.optString("prompt");
            if("geometry_grid".equals(type)){
                if("point_select".equals(q.optString("kind"))){
                    mid++;JSONArray seg=v.getJSONArray("highlightSegment");assertTrue(id,Part05Geometry.midpoint(v,seg.getString(0),seg.getString(1),q.getString("answer")));
                }else if("number".equals(q.optString("kind"))&&p.contains("góc không vuông")){
                    notRight++;String vertex=p.contains("đỉnh B")?"B":"A";assertEquals(id,q.getInt("answer"),Part05Geometry.angles(v,vertex,false));
                }else if("number".equals(q.optString("kind"))&&p.contains("góc vuông")){
                    right++;assertEquals(id,q.getInt("answer"),Part05Geometry.allRight(v));
                }
            }else if("circle_diagram".equals(type)&&"segment_multi".equals(q.optString("kind"))){
                circle++;Set<String> computed=new HashSet<>(),expected=new HashSet<>();
                boolean diameter=p.contains("đường kính");
                for(Object x:Json.list(v.getJSONArray("options"))){String s=Json.str(x);if(diameter?Part05Geometry.drawnDiameter(v,s):Part05Geometry.drawnRadius(v,s))computed.add(s);}
                for(Object x:Json.list(q.getJSONArray("answer")))expected.add(Json.str(x));assertEquals(id,expected,computed);
            }else if("geometry_circle_grid".equals(type)){
                double[] bounds=Part05Geometry.bounds(v),center=Part05Geometry.point(v,"O"),a=Part05Geometry.point(v,"A");
                double r=Math.hypot(a[0]-center[0],a[1]-center[1]);
                assertTrue(bounds[0]<=center[0]-r&&bounds[1]<=center[1]-r&&bounds[2]>=center[0]+r&&bounds[3]>=center[1]+r);
                assertTrue(Part05Geometry.midpoint(v,"A","C","O"));assertTrue(Part05Geometry.midpoint(v,"B","D","O"));
                assertEquals(4,Part05Geometry.allRight(v));assertEquals(4,Part05Geometry.angles(v,"O",false));
            }else if("painted_cuboid".equals(type)){
                paint++;assertEquals(id,q.getInt("answer"),Part05Geometry.paintedCount(v.getJSONArray("dimensions"),p.contains("2 mặt")?2:3));
            }else if("solid_stack".equals(type)){
                stack++;JSONArray d=Json.obj(v,"base").getJSONArray("dimensions");int n=0;
                if(p.contains("lập phương"))n=d.getInt(0)*d.getInt(1)*d.getInt(2)+Json.arr(Json.obj(v,"extraCubes"),"positions").length();
                else for(Object raw:Json.list(v.getJSONArray("above"))){String shape=((JSONObject)raw).optString("shape");if((p.contains("trụ")&&shape.equals("cylinder"))||(p.contains("cầu")&&shape.equals("sphere")))n++;}
                assertEquals(id,q.getInt("answer"),n);
            }else if("polyline".equals(type)){
                poly++;int sum=0;for(Object raw:Json.list(v.getJSONArray("segments")))sum+=((JSONObject)raw).getInt("length");assertEquals(id,q.getInt("answer"),sum);
            }
        }
        assertEquals(11,mid);assertEquals(3,right);assertEquals(3,notRight);assertEquals(4,circle);assertEquals(3,paint);assertEquals(5,stack);assertEquals(4,poly);
    }
    @Test public void checkIndependentCornerCounts(){
        for(int a=2;a<=6;a++)for(int b=2;b<=6;b++)for(int c=2;c<=6;c++){
            JSONArray d=new JSONArray(Arrays.asList(a,b,c));
            assertEquals(8,Part05Geometry.paintedCount(d,3));
            assertEquals(4*((a-2)+(b-2)+(c-2)),Part05Geometry.paintedCount(d,2));
            assertEquals(a*b*c,java.util.stream.IntStream.rangeClosed(0,6).map(i->Part05Geometry.paintedCount(d,i)).sum());
        }
    }
}
