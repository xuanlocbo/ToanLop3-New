package vn.mocnhien.toan3;
import org.json.JSONObject;
import java.util.*;

/** Explicit, stable source IDs. Every stage contains an SGK task, a VBT task and both bonus tasks. */
public final class AdventurePlan {
    public static final int FIRST=26,LAST=44,GATES=4;
    private static final String[][] SOURCE={
        {"SGK.26.77.HD2.3.a","VBT.26.2.67.3.1"},
        {"SGK.27.79.HD.2.a","VBT.27.1.69.3.1"},
        {"SGK.28.82.HD.2.a","VBT.28.2.73.4.1"},
        {"SGK.29.84.LT2.3.a","VBT.29.2.75.3.1"},
        {"SGK.30.85.HD.1.b","VBT.30.1.76.3.1"},
        {"SGK.31.87.HD.1.a","VBT.31.1.79.2.3"},
        {"SGK.32.89.HD.1.b","VBT.32.1.80.1.1"},
        {"SGK.33.92.HD.2.b1","VBT.33.1.81.4.1"},
        {"SGK.34.93.HD1.3.a","VBT.34.2.84.4.1"},
        {"SGK.35.95.LT1.4.a","VBT.35.1.85.3.1"},
        {"SGK.36.98.HD.1.c","VBT.36.2.89.3.1"},
        {"SGK.37.100.HD1.1.b1","VBT.37.3.93.4.1"},
        {"SGK.38.105.HD1.2.a","VBT.38.2.95.4.1"},
        {"SGK.39.110.HD.2.a","VBT.39.1.98.3.1"},
        {"SGK.40.111.LT1.3.a","VBT.40.1.100.1.3"},
        {"SGK.41.114.LT2.5.a","VBT.41.3.105.3.1"},
        {"SGK.42.116.LT1.4.a","VBT.42.1.107.5.1"},
        {"SGK.43.118.LT1.3.a1","VBT.43.1.109.2.1"},
        {"SGK.44.120.LT1.3.b2","VBT.44.1.114.5.1"}
    };
    private static final String[] NAMES={"Bến thuyền ngọc trai","Rừng phép thuật","Cầu cầu vồng","Hang pha lê","Xưởng cầu tí hon","Tiệm bánh thần kỳ","Suối nước màu","Đỉnh núi tuyết","Phòng thí nghiệm","Đảo đo lường","Xưởng đèn sao","Mỏ kim cương","Cổng biểu thức","Vườn khổng lồ","Thành phố trên mây","Đấu trường con số","Mê cung bí mật","Lâu đài hình khối","Kho báu học kỳ"};
    public static boolean validLesson(int lesson){return lesson>=FIRST&&lesson<=LAST;}
    static void require(int lesson){if(!validLesson(lesson))throw new IllegalArgumentException("Unknown adventure lesson: "+lesson);}
    public static String name(int lesson){require(lesson);return NAMES[lesson-FIRST];}
    public static int theme(int lesson){require(lesson);return lesson<=29?0:lesson<=35?1:lesson<=40?2:3;}
    public static String questionId(int lesson,int gate){
        require(lesson);if(gate<0||gate>=GATES)throw new IllegalArgumentException("Unknown gate");
        return gate<2?SOURCE[lesson-FIRST][gate]:"BONUS."+lesson+"."+(gate-1);
    }
    public static void verify(Curriculum c){
        Set<String> ids=new HashSet<>();
        for(int l=FIRST;l<=LAST;l++)for(int g=0;g<GATES;g++){
            String id=questionId(l,g);JSONObject q=c.questions.get(id);
            String book=g==0?"SGK":g==1?"VBT":"BONUS";
            if(q==null||!ids.add(id)||q.optInt("lesson")!=l||!book.equals(q.optString("book"))
                ||!Json.obj(q,"grading").optBoolean("scoreEligible")||Json.arr(q,"prerequisites").length()!=0
                ||!NativeGrader.supports(q.optString("kind")))throw new IllegalStateException("Invalid adventure gate: "+id);
        }
        if(ids.size()!=76)throw new IllegalStateException("Incomplete adventure map");
    }
}
