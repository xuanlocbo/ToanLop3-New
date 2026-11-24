package vn.mocnhien.toan3;
import java.util.*;
public final class BookBank {
 public enum Kind {NUMBER,CHOICE,MULTI,DIVISION,MULTIPLY,RULER,SCALE,BALANCE,CUP,THERMO,CUPS,CLINICAL,GEOMETRY,POUR,DRAW,PRACTICE}
 public static final class Q {
   public final int lesson,period,page,exercise,part,a,b;
   public final String book;
   public final Kind kind;
   public final String prompt,answer,hint,explain;
   public final String[] options;
   Q(int l,int t,int p,int e,int part,Kind k,String q,String ans,String h,String sol,String[] opts,int a,int b){
     this("VBT",l,t,p,e,part,k,q,ans,h,sol,opts,a,b);
   }
   Q(String book,int l,int t,int p,int e,int part,Kind k,String q,String ans,String h,String sol,String[] opts,int a,int b){
     this.book=book;lesson=l;period=t;page=p;exercise=e;this.part=part;kind=k;prompt=q;answer=ans;hint=h;explain=sol;options=opts;this.a=a;this.b=b;
   }
   public String id(){return book+"."+lesson+"."+period+"."+page+"."+exercise+"."+part;}
   public String source(){return book+(page>0?" · tr. "+page:"")+(period>0?" · Tiết "+period:"")+(exercise>0?" · Câu "+exercise:"");}
   public boolean check(String v){return answer.equals(v==null?"":v.trim());}
 }
 private static final List<List<Q>> DATA=new ArrayList<>();
 static { for(int i=0;i<19;i++) DATA.add(new ArrayList<>()); l26(); l27(); l28(); l29(); l30(); l31(); l32(); l33(); l34(); l35(); l36(); l37(); l38(); l39(); l40(); l41(); l42(); l43(); l44(); }
 private static void add(int l,int t,int p,int e,int part,Kind k,String q,String ans,String h,String sol,String[] opts,int a,int b){DATA.get(l-26).add(new Q(l,t,p,e,part,k,q,ans,h,sol,opts,a,b));}
 public static int count(int l){return DATA.get(l-26).size();}
 public static Q get(int l,int i){return DATA.get(l-26).get(i);}
 public static List<Q> lesson(int l){return Collections.unmodifiableList(DATA.get(l-26));}
private static void l26(){
 add(26,1,66,1,1,Kind.DIVISION,"46 : 2","23,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","46 = 2 × 23; thương 23, dư 0.",new String[]{},46,2);
 add(26,1,66,1,2,Kind.DIVISION,"55 : 5","11,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","55 = 5 × 11; thương 11, dư 0.",new String[]{},55,5);
 add(26,1,66,1,3,Kind.DIVISION,"96 : 3","32,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","96 = 3 × 32; thương 32, dư 0.",new String[]{},96,3);
 add(26,1,66,1,4,Kind.DIVISION,"84 : 4","21,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","84 = 4 × 21; thương 21, dư 0.",new String[]{},84,4);
 add(26,1,66,2,1,Kind.DIVISION,"39 : 3","13,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","39 = 3 × 13; thương 13, dư 0.",new String[]{},39,3);
 add(26,1,66,2,2,Kind.DIVISION,"64 : 2","32,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","64 = 2 × 32; thương 32, dư 0.",new String[]{},64,2);
 add(26,1,66,2,3,Kind.DIVISION,"63 : 3","21,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","63 = 3 × 21; thương 21, dư 0.",new String[]{},63,3);
 add(26,1,66,2,4,Kind.DIVISION,"88 : 2","44,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","88 = 2 × 44; thương 44, dư 0.",new String[]{},88,2);
 add(26,1,66,3,1,Kind.NUMBER,"80 : 2 = ?","40","Tính theo đúng thứ tự các phép tính.","80 : 2 = 40",new String[]{},0,0);
 add(26,1,66,3,2,Kind.NUMBER,"30 : 3 = ?","10","Tính theo đúng thứ tự các phép tính.","30 : 3 = 10",new String[]{},0,0);
 add(26,1,66,3,3,Kind.NUMBER,"90 : 3 = ?","30","Tính theo đúng thứ tự các phép tính.","90 : 3 = 30",new String[]{},0,0);
 add(26,1,66,3,4,Kind.NUMBER,"40 : 2 = ?","20","Tính theo đúng thứ tự các phép tính.","40 : 2 = 20",new String[]{},0,0);
 add(26,1,66,4,1,Kind.NUMBER,"? × 4 = 48","12","Lấy tích chia thừa số đã biết.","Đáp án: 12",new String[]{},0,0);
 add(26,1,66,4,2,Kind.NUMBER,"5 × ? = 55","11","Lấy tích chia thừa số đã biết.","Đáp án: 11",new String[]{},0,0);
 add(26,1,66,4,3,Kind.NUMBER,"? × 3 = 93","31","Lấy tích chia thừa số đã biết.","Đáp án: 31",new String[]{},0,0);
 add(26,2,67,1,1,Kind.DIVISION,"57 : 3","19,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","57 = 3 × 19; thương 19, dư 0.",new String[]{},57,3);
 add(26,2,67,1,2,Kind.DIVISION,"58 : 2","29,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","58 = 2 × 29; thương 29, dư 0.",new String[]{},58,2);
 add(26,2,67,1,3,Kind.DIVISION,"86 : 7","12,2","Chia từ trái sang phải; số dư nhỏ hơn số chia.","86 = 7 × 12 + 2; thương 12, dư 2.",new String[]{},86,7);
 add(26,2,67,1,4,Kind.DIVISION,"65 : 4","16,1","Chia từ trái sang phải; số dư nhỏ hơn số chia.","65 = 4 × 16 + 1; thương 16, dư 1.",new String[]{},65,4);
 add(26,2,67,2,1,Kind.CHOICE,"39 : 3 = 13","Đúng","Tính lại để kiểm tra.","39 = 3 × 13",new String[]{"Đúng","Sai"},0,0);
 add(26,2,67,2,2,Kind.CHOICE,"35 : 2 = 12 (dư 1)","Sai","Tính lại để kiểm tra.","35 : 2 = 17 (dư 1).",new String[]{"Đúng","Sai"},0,0);
 add(26,2,67,2,3,Kind.CHOICE,"89 : 5 = 17 (dư 4)","Đúng","Tính lại để kiểm tra.","89 = 5 × 17 + 4",new String[]{"Đúng","Sai"},0,0);
 add(26,2,67,2,4,Kind.CHOICE,"53 : 4 = 13 (dư 1)","Đúng","Tính lại để kiểm tra.","53 = 4 × 13 + 1",new String[]{"Đúng","Sai"},0,0);
 add(26,2,67,3,1,Kind.NUMBER,"60 quyển truyện chia đều vào 4 ngăn. Mỗi ngăn có bao nhiêu quyển?","15","60 : 4","Đáp án: 15",new String[]{},0,0);
 add(26,2,67,4,1,Kind.NUMBER,"40 cái kẹo chia đều cho 3 bạn. Còn dư bao nhiêu cái?","1","Tìm số dư.","40 : 3 = 13 (dư 1)",new String[]{},0,0);
 add(26,3,68,1,1,Kind.DIVISION,"53 : 3","17,2","Chia từ trái sang phải; số dư nhỏ hơn số chia.","53 = 3 × 17 + 2; thương 17, dư 2.",new String[]{},53,3);
 add(26,3,68,1,2,Kind.DIVISION,"67 : 4","16,3","Chia từ trái sang phải; số dư nhỏ hơn số chia.","67 = 4 × 16 + 3; thương 16, dư 3.",new String[]{},67,4);
 add(26,3,68,1,3,Kind.DIVISION,"35 : 3","11,2","Chia từ trái sang phải; số dư nhỏ hơn số chia.","35 = 3 × 11 + 2; thương 11, dư 2.",new String[]{},35,3);
 add(26,3,68,1,4,Kind.DIVISION,"99 : 8","12,3","Chia từ trái sang phải; số dư nhỏ hơn số chia.","99 = 8 × 12 + 3; thương 12, dư 3.",new String[]{},99,8);
 add(26,3,68,2,1,Kind.NUMBER,"87 quả cam, mỗi suất quà 3 quả. Có bao nhiêu suất?","29","87 : 3","Đáp án: 29",new String[]{},0,0);
 add(26,3,68,3,1,Kind.NUMBER,"32 khách, mỗi thuyền chở tối đa 5 khách. Cần ít nhất bao nhiêu thuyền?","7","Nếu còn khách thừa cần thêm một thuyền.","32 : 5 = 6 (dư 2); cần 7 thuyền.",new String[]{},0,0);
 add(26,3,68,4,1,Kind.NUMBER,"? : 8 = 12","96","Lấy thương nhân số chia.","Đáp án: 96",new String[]{},0,0);
 add(26,3,68,4,2,Kind.NUMBER,"? : 4 = 23","92","Lấy thương nhân số chia.","Đáp án: 92",new String[]{},0,0);
 add(26,3,68,4,3,Kind.NUMBER,"? : 3 = 26","78","Lấy thương nhân số chia.","Đáp án: 78",new String[]{},0,0);
 add(26,3,68,4,4,Kind.NUMBER,"? : 2 = 47","94","Lấy thương nhân số chia.","Đáp án: 94",new String[]{},0,0);
}
private static void l27(){
 add(27,1,69,1,1,Kind.NUMBER,"36 giảm 2 lần","18","Chia cho số lần.","Đáp án: 18",new String[]{},0,0);
 add(27,1,69,1,2,Kind.NUMBER,"48 giảm 6 lần","8","Chia cho số lần.","Đáp án: 8",new String[]{},0,0);
 add(27,1,69,1,3,Kind.NUMBER,"45 giảm 3 lần","15","Chia cho số lần.","Đáp án: 15",new String[]{},0,0);
 add(27,1,69,2,1,Kind.CHOICE,"24 → ? 4 lần → 6","Giảm","So sánh số đầu với số cuối.","Đáp án: Giảm",new String[]{"Gấp","Giảm"},0,0);
 add(27,1,69,2,2,Kind.CHOICE,"72 → ? 6 lần → 12","Giảm","So sánh số đầu với số cuối.","Đáp án: Giảm",new String[]{"Gấp","Giảm"},0,0);
 add(27,1,69,2,3,Kind.CHOICE,"48 → ? 2 lần → 96","Gấp","So sánh số đầu với số cuối.","Đáp án: Gấp",new String[]{"Gấp","Giảm"},0,0);
 add(27,1,69,2,4,Kind.CHOICE,"56 → ? 4 lần → 14","Giảm","So sánh số đầu với số cuối.","Đáp án: Giảm",new String[]{"Gấp","Giảm"},0,0);
 add(27,1,69,3,1,Kind.NUMBER,"78 kg cam, sau khi hỏng một phần, lượng còn lại giảm 3 lần. Còn bao nhiêu kg?","26","78 : 3","Đáp án: 26",new String[]{},0,0);
 add(27,1,69,4,1,Kind.CHOICE,"18 → 6: chọn thao tác.","Giảm 3 lần","Thử nhân hoặc chia.","Đáp án: Giảm 3 lần",new String[]{"Gấp 2 lần","Giảm 3 lần"},0,0);
 add(27,1,69,4,2,Kind.CHOICE,"6 → 12: chọn thao tác.","Gấp 2 lần","Thử nhân hoặc chia.","Đáp án: Gấp 2 lần",new String[]{"Gấp 2 lần","Giảm 3 lần"},0,0);
 add(27,1,69,4,3,Kind.CHOICE,"18 → 36: chọn thao tác.","Gấp 2 lần","Thử nhân hoặc chia.","Đáp án: Gấp 2 lần",new String[]{"Gấp 2 lần","Giảm 3 lần"},0,0);
 add(27,1,69,4,4,Kind.CHOICE,"36 → 12: chọn thao tác.","Giảm 3 lần","Thử nhân hoặc chia.","Đáp án: Giảm 3 lần",new String[]{"Gấp 2 lần","Giảm 3 lần"},0,0);
 add(27,2,70,1,1,Kind.NUMBER,"52 giảm 4 lần","13","Gấp dùng nhân; giảm dùng chia.","Đáp án: 13",new String[]{},0,0);
 add(27,2,70,1,2,Kind.NUMBER,"13 gấp 6 lần","78","Gấp dùng nhân; giảm dùng chia.","Đáp án: 78",new String[]{},0,0);
 add(27,2,70,1,3,Kind.NUMBER,"26 gấp 3 lần","78","Gấp dùng nhân; giảm dùng chia.","Đáp án: 78",new String[]{},0,0);
 add(27,2,70,1,4,Kind.NUMBER,"78 giảm 2 lần","39","Gấp dùng nhân; giảm dùng chia.","Đáp án: 39",new String[]{},0,0);
 add(27,2,70,2,1,Kind.NUMBER,"Mũi chú người gỗ dài 52 cm. Nói thật một lần, mũi giảm 2 lần. Còn bao nhiêu cm?","26","52 : 2","Đáp án: 26",new String[]{},0,0);
 add(27,2,70,2,2,Kind.NUMBER,"Mũi dài 9 cm. Mỗi lần nói dối dài gấp 3 lần. Sau hai lần, dài bao nhiêu cm?","81","Nhân 3 hai lần.","9 × 3 × 3 = 81 cm",new String[]{},0,0);
 add(27,2,70,3,1,Kind.NUMBER,"42 tờ giấy màu, số còn lại giảm 6 lần. Còn bao nhiêu tờ?","7","42 : 6","Đáp án: 7",new String[]{},0,0);
 add(27,2,70,4,1,Kind.NUMBER,"72 : ? = 8","9","Lấy số bị chia chia cho thương.","Đáp án: 9",new String[]{},0,0);
 add(27,2,70,4,2,Kind.NUMBER,"48 : ? = 6","8","Lấy số bị chia chia cho thương.","Đáp án: 8",new String[]{},0,0);
 add(27,2,70,4,3,Kind.NUMBER,"81 : ? = 9","9","Lấy số bị chia chia cho thương.","Đáp án: 9",new String[]{},0,0);
 add(27,2,70,4,4,Kind.NUMBER,"45 : ? = 5","9","Lấy số bị chia chia cho thương.","Đáp án: 9",new String[]{},0,0);
}
private static void l28(){
 add(28,1,71,1,1,Kind.NUMBER,"Trong chuồng 3 con thỏ, ngoài sân gấp 4 lần. Ngoài sân có bao nhiêu con?","12","3 × 4","Đáp án: 12",new String[]{},0,0);
 add(28,1,71,1,2,Kind.NUMBER,"Trong chuồng 3 con, ngoài sân 12 con. Có tất cả bao nhiêu con?","15","3 + 12","Đáp án: 15",new String[]{},0,0);
 add(28,1,71,1,3,Kind.NUMBER,"Ngoài sân 12 con, trong chuồng 3 con. Ngoài sân nhiều hơn bao nhiêu con?","9","12 − 3","Đáp án: 9",new String[]{},0,0);
 add(28,1,71,2,1,Kind.NUMBER,"AB dài 15 cm; BC ngắn hơn AB 5 cm. BC dài bao nhiêu cm?","10","15 − 5","Đáp án: 10",new String[]{},0,0);
 add(28,1,71,2,2,Kind.NUMBER,"AB dài 15 cm, BC dài 10 cm. Đường gấp khúc ABC dài bao nhiêu cm?","25","15 + 10","Đáp án: 25",new String[]{},0,0);
 add(28,1,72,3,1,Kind.NUMBER,"Lớp có 15 bạn nam, nữ nhiều hơn 2 bạn. Có bao nhiêu bạn nữ?","17","15 + 2","Đáp án: 17",new String[]{},0,0);
 add(28,1,72,3,2,Kind.NUMBER,"15 nam và 17 nữ. Có tất cả bao nhiêu bạn?","32","15 + 17","Đáp án: 32",new String[]{},0,0);
 add(28,2,72,1,1,Kind.NUMBER,"Lợn đen 65 kg, lợn trắng nặng hơn 8 kg. Lợn trắng nặng bao nhiêu kg?","73","65 + 8","Đáp án: 73",new String[]{},0,0);
 add(28,2,72,1,2,Kind.NUMBER,"Hai con lợn nặng 65 kg và 73 kg. Cả hai nặng bao nhiêu kg?","138","65 + 73","Đáp án: 138",new String[]{},0,0);
 add(28,2,73,2,1,Kind.NUMBER,"Thùng có 15 l nước mắm. Đổ thêm 6 can, mỗi can 3 l. Đổ thêm bao nhiêu lít?","18","6 × 3","Đáp án: 18",new String[]{},0,0);
 add(28,2,73,2,2,Kind.NUMBER,"Thùng có 15 l, thêm 18 l. Tổng cộng bao nhiêu lít?","33","15 + 18","Đáp án: 33",new String[]{},0,0);
 add(28,2,73,3,1,Kind.NUMBER,"Theo sơ đồ, gà nặng 2 kg; ngỗng nặng gấp 4 lần gà. Ngỗng nặng bao nhiêu kg?","8","2 × 4","Đáp án: 8",new String[]{},0,0);
 add(28,2,73,3,2,Kind.NUMBER,"Gà 2 kg, ngỗng 8 kg. Cả hai nặng bao nhiêu kg?","10","2 + 8","Đáp án: 10",new String[]{},0,0);
 add(28,2,73,4,1,Kind.NUMBER,"Mi 5 tuổi, chị Mai hơn Mi 3 tuổi. Sau 2 năm nữa Mai bao nhiêu tuổi?","10","5 + 3 + 2","Đáp án: 10",new String[]{},0,0);
}
private static void l29(){
 add(29,1,74,1,1,Kind.NUMBER,"50 × 2 = ?","100","Tính theo đúng thứ tự các phép tính.","50 × 2 = 100",new String[]{},0,0);
 add(29,1,74,1,2,Kind.NUMBER,"30 × 2 = ?","60","Tính theo đúng thứ tự các phép tính.","30 × 2 = 60",new String[]{},0,0);
 add(29,1,74,1,3,Kind.NUMBER,"10 × 5 = ?","50","Tính theo đúng thứ tự các phép tính.","10 × 5 = 50",new String[]{},0,0);
 add(29,1,74,1,4,Kind.NUMBER,"60 : 3 = ?","20","Tính theo đúng thứ tự các phép tính.","60 : 3 = 20",new String[]{},0,0);
 add(29,1,74,1,5,Kind.NUMBER,"80 : 2 = ?","40","Tính theo đúng thứ tự các phép tính.","80 : 2 = 40",new String[]{},0,0);
 add(29,1,74,1,6,Kind.NUMBER,"90 : 9 = ?","10","Tính theo đúng thứ tự các phép tính.","90 : 9 = 10",new String[]{},0,0);
 add(29,1,74,2,1,Kind.DIVISION,"32 : 2","16,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","32 = 2 × 16; thương 16, dư 0.",new String[]{},32,2);
 add(29,1,74,2,2,Kind.DIVISION,"54 : 3","18,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","54 = 3 × 18; thương 18, dư 0.",new String[]{},54,3);
 add(29,1,74,2,3,Kind.DIVISION,"84 : 7","12,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","84 = 7 × 12; thương 12, dư 0.",new String[]{},84,7);
 add(29,1,74,2,4,Kind.DIVISION,"44 : 4","11,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","44 = 4 × 11; thương 11, dư 0.",new String[]{},44,4);
 add(29,1,74,3,1,Kind.NUMBER,"72 giảm 6 lần","12","Gấp thì nhân, giảm thì chia.","Đáp án: 12",new String[]{},0,0);
 add(29,1,74,3,2,Kind.NUMBER,"14 gấp 7 lần","98","Gấp thì nhân, giảm thì chia.","Đáp án: 98",new String[]{},0,0);
 add(29,1,74,3,3,Kind.NUMBER,"81 giảm 3 lần","27","Gấp thì nhân, giảm thì chia.","Đáp án: 27",new String[]{},0,0);
 add(29,1,74,3,4,Kind.NUMBER,"29 gấp 3 lần","87","Gấp thì nhân, giảm thì chia.","Đáp án: 87",new String[]{},0,0);
 add(29,1,74,4,1,Kind.NUMBER,"Năm ngoái cây sầu riêng có 27 quả, năm nay gấp 3 lần. Năm nay có bao nhiêu quả?","81","27 × 3","Đáp án: 81",new String[]{},0,0);
 add(29,1,74,4,2,Kind.NUMBER,"81 quả, sau khi bán số còn lại giảm 3 lần. Còn bao nhiêu quả?","27","81 : 3","Đáp án: 27",new String[]{},0,0);
 add(29,2,75,1,1,Kind.DIVISION,"43 : 2","21,1","Chia từ trái sang phải; số dư nhỏ hơn số chia.","43 = 2 × 21 + 1; thương 21, dư 1.",new String[]{},43,2);
 add(29,2,75,1,2,Kind.DIVISION,"73 : 3","24,1","Chia từ trái sang phải; số dư nhỏ hơn số chia.","73 = 3 × 24 + 1; thương 24, dư 1.",new String[]{},73,3);
 add(29,2,75,1,3,Kind.DIVISION,"64 : 4","16,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","64 = 4 × 16; thương 16, dư 0.",new String[]{},64,4);
 add(29,2,75,1,4,Kind.DIVISION,"84 : 5","16,4","Chia từ trái sang phải; số dư nhỏ hơn số chia.","84 = 5 × 16 + 4; thương 16, dư 4.",new String[]{},84,5);
 add(29,2,75,2,3,Kind.NUMBER,"30 giảm 6 lần được bao nhiêu?","5","30 : 6","30 : 6 = 5.",new String[]{},0,0);
 add(29,2,75,2,1,Kind.NUMBER,"30 giảm 6 lần rồi thêm 7 đơn vị. Kết quả?","12","30 : 6 + 7","30 : 6 = 5; 5 + 7 = 12.",new String[]{},0,0);
 add(29,2,75,2,4,Kind.NUMBER,"21 gấp 2 lần được bao nhiêu?","42","21 × 2","21 × 2 = 42.",new String[]{},0,0);
 add(29,2,75,2,2,Kind.NUMBER,"21 gấp 2 lần rồi giảm mấy lần để còn 7?","6","21 × 2 = 42; lấy 42 chia 7.","42 : 7 = 6.",new String[]{},0,0);
 add(29,2,75,3,1,Kind.NUMBER,"Cây tre 19 m. Cắt đoạn 3 m. Cắt được nhiều nhất bao nhiêu đoạn?","6","19 : 3 = 6 dư 1","Đáp án: 6",new String[]{},0,0);
 add(29,2,75,4,1,Kind.NUMBER,"Hôm qua bán 47 kg chà bông, hôm nay gấp 2 lần. Hôm nay bán bao nhiêu kg?","94","47 × 2","Đáp án: 94",new String[]{},0,0);
}
private static void l30(){
 add(30,1,76,1,1,Kind.NUMBER,"3 cm = ? mm","30","1 cm = 10 mm; 1 m = 100 cm = 1 000 mm.","Đáp án: 30",new String[]{},0,0);
 add(30,1,76,1,2,Kind.NUMBER,"20 mm = ? cm","2","1 cm = 10 mm; 1 m = 100 cm = 1 000 mm.","Đáp án: 2",new String[]{},0,0);
 add(30,1,76,1,3,Kind.NUMBER,"5 cm = ? mm","50","1 cm = 10 mm; 1 m = 100 cm = 1 000 mm.","Đáp án: 50",new String[]{},0,0);
 add(30,1,76,1,4,Kind.NUMBER,"10 cm = ? mm","100","1 cm = 10 mm; 1 m = 100 cm = 1 000 mm.","Đáp án: 100",new String[]{},0,0);
 add(30,1,76,1,5,Kind.NUMBER,"1 m = ? cm","100","1 cm = 10 mm; 1 m = 100 cm = 1 000 mm.","Đáp án: 100",new String[]{},0,0);
 add(30,1,76,1,6,Kind.NUMBER,"1 m = ? mm","1000","1 cm = 10 mm; 1 m = 100 cm = 1 000 mm.","Đáp án: 1000",new String[]{},0,0);
 add(30,1,76,2,1,Kind.RULER,"Đo đoạn AB. Đoạn dài bao nhiêu mm?","10","Đọc vạch từ 0; mỗi vạch nhỏ là 1 mm.","AB dài 10 mm.",new String[]{},10,0);
 add(30,1,76,2,2,Kind.RULER,"Đo đoạn CD. Đoạn dài bao nhiêu mm?","40","Đọc vạch từ 0.","CD dài 40 mm.",new String[]{},40,0);
 add(30,1,76,3,1,Kind.CHOICE,"Cào cào nhảy 3 cm, bọ ngựa 15 mm, châu chấu 20 mm. Ai xa nhất?","Cào cào","3 cm = 30 mm.","Đáp án: Cào cào",new String[]{"Cào cào","Bọ ngựa","Châu chấu"},0,0);
 add(30,1,76,3,2,Kind.CHOICE,"Trong ba bạn trên, ai nhảy ngắn nhất? (30 mm, 15 mm, 20 mm)","Bọ ngựa","15 mm nhỏ nhất.","Đáp án: Bọ ngựa",new String[]{"Cào cào","Bọ ngựa","Châu chấu"},0,0);
 add(30,1,76,4,1,Kind.NUMBER,"Chồng 7 tấm gỗ dày 2 mm mỗi tấm. Tổng độ dày là bao nhiêu mm?","14","2 × 7","Đáp án: 14",new String[]{},0,0);
 add(30,2,77,1,1,Kind.NUMBER,"150 + 400 = ? mm","550","Tính theo đúng thứ tự các phép tính.","150 + 400 = 550 mm",new String[]{},0,0);
 add(30,2,77,1,2,Kind.NUMBER,"45 + 2 = ? mm","47","Tính theo đúng thứ tự các phép tính.","45 + 2 = 47 mm",new String[]{},0,0);
 add(30,2,77,1,3,Kind.NUMBER,"450 − 180 = ? mm","270","Tính theo đúng thứ tự các phép tính.","450 − 180 = 270 mm",new String[]{},0,0);
 add(30,2,77,1,4,Kind.NUMBER,"72 − 34 = ? mm","38","Tính theo đúng thứ tự các phép tính.","72 − 34 = 38 mm",new String[]{},0,0);
 add(30,2,77,1,5,Kind.NUMBER,"37 × 2 = ? mm","74","Tính theo đúng thứ tự các phép tính.","37 × 2 = 74 mm",new String[]{},0,0);
 add(30,2,77,1,6,Kind.NUMBER,"72 : 3 = ? mm","24","Tính theo đúng thứ tự các phép tính.","72 : 3 = 24 mm",new String[]{},0,0);
 add(30,2,77,2,1,Kind.NUMBER,"24 : 8 = ? mm","3","Tính theo đúng thứ tự các phép tính.","24 : 8 = 3 mm",new String[]{},0,0);
 add(30,2,77,2,2,Kind.NUMBER,"49 × 2 = ? mm","98","Tính theo đúng thứ tự các phép tính.","49 × 2 = 98 mm",new String[]{},0,0);
 add(30,2,77,2,3,Kind.NUMBER,"96 : 4 = ? mm","24","Tính theo đúng thứ tự các phép tính.","96 : 4 = 24 mm",new String[]{},0,0);
 add(30,2,77,2,4,Kind.NUMBER,"18 × 5 = ? mm","90","Tính theo đúng thứ tự các phép tính.","18 × 5 = 90 mm",new String[]{},0,0);
 add(30,2,77,3,1,Kind.NUMBER,"Măng hôm qua cao 173 mm, hôm nay 292 mm. Cao thêm bao nhiêu mm?","119","292 − 173","Đáp án: 119",new String[]{},0,0);
 add(30,2,77,4,1,Kind.NUMBER,"Thứ Ba mưa 17 mm, thứ Tư gấp 4 lần. Thứ Tư mưa bao nhiêu mm?","68","17 × 4","Đáp án: 68",new String[]{},0,0);
}
private static void l31(){
 add(31,1,78,1,1,Kind.BALANCE,"Ba quả cam nặng bao nhiêu gam?","550","Cộng hai quả cân.","50 + 500 = 550 g",new String[]{},50,500);
 add(31,1,78,1,2,Kind.BALANCE,"Hộp sữa nặng bao nhiêu gam?","600","Cộng hai quả cân.","100 + 500 = 600 g",new String[]{},100,500);
 add(31,1,78,1,3,Kind.BALANCE,"Gói mì chính nặng bao nhiêu gam?","120","Cộng hai quả cân.","100 + 20 = 120 g",new String[]{},100,20);
 add(31,1,78,1,4,Kind.BALANCE,"Gói bột canh nặng bao nhiêu gam?","300","Cộng hai quả cân.","100 + 200 = 300 g",new String[]{},100,200);
 add(31,1,79,2,1,Kind.SCALE,"Đọc cân túi táo (g).","750","Đọc vị trí kim trên mặt cân.","750 g",new String[]{},750,0);
 add(31,1,79,2,2,Kind.SCALE,"Đọc cân gói bột mì (g).","500","Đọc vị trí kim.","500 g",new String[]{},500,0);
 add(31,1,79,2,3,Kind.NUMBER,"Táo 750 g, bột mì 500 g. Bột mì nhẹ hơn bao nhiêu gam?","250","750 − 500","Đáp án: 250",new String[]{},0,0);
 add(31,1,79,3,1,Kind.NUMBER,"250 + 180 = ? g","430","Tính theo đúng thứ tự các phép tính.","250 + 180 = 430 g",new String[]{},0,0);
 add(31,1,79,3,2,Kind.NUMBER,"430 − 250 = ? g","180","Tính theo đúng thứ tự các phép tính.","430 − 250 = 180 g",new String[]{},0,0);
 add(31,1,79,3,3,Kind.NUMBER,"430 − 180 = ? g","250","Tính theo đúng thứ tự các phép tính.","430 − 180 = 250 g",new String[]{},0,0);
 add(31,1,79,3,4,Kind.NUMBER,"8 × 9 = ? g","72","Tính theo đúng thứ tự các phép tính.","8 × 9 = 72 g",new String[]{},0,0);
 add(31,1,79,3,5,Kind.NUMBER,"72 : 9 = ? g","8","Tính theo đúng thứ tự các phép tính.","72 : 9 = 8 g",new String[]{},0,0);
 add(31,1,79,3,6,Kind.NUMBER,"72 : 8 = ? g","9","Tính theo đúng thứ tự các phép tính.","72 : 8 = 9 g",new String[]{},0,0);
 add(31,1,79,4,1,Kind.NUMBER,"1 kg = ? g","1000","Đổi kg sang g.","Đáp án: 1000",new String[]{},0,0);
 add(31,1,79,4,2,Kind.NUMBER,"Có 1 kg đường, lấy 150 g rồi 200 g. Còn bao nhiêu gam?","650","1 000 − 150 − 200","Đáp án: 650",new String[]{},0,0);
}
private static void l32(){
 add(32,1,80,1,1,Kind.CUP,"Đọc ca B (ml).","400","Mỗi vạch 100 ml.","400 ml",new String[]{},400,500);
 add(32,1,80,1,2,Kind.CUP,"Đọc ca C (ml).","100","Mỗi vạch 100 ml.","100 ml",new String[]{},100,500);
 add(32,1,80,1,3,Kind.NUMBER,"Ba ca có 500 ml, 400 ml và 100 ml. Lượng nước ban đầu là bao nhiêu ml?","1000","Cộng ba lượng nước.","Đáp án: 1000",new String[]{},0,0);
 add(32,1,80,2,1,Kind.NUMBER,"1 l = ? ml","1000","Một lít bằng một nghìn mi-li-lít.","Đáp án: 1000",new String[]{},0,0);
 add(32,1,80,2,2,Kind.NUMBER,"Phích 1 l, rót 400 ml, 300 ml, 100 ml. Còn bao nhiêu ml?","200","1 000 − 400 − 300 − 100","Đáp án: 200",new String[]{},0,0);
 add(32,1,80,3,1,Kind.NUMBER,"250 + 100 = ? ml","350","Tính theo đúng thứ tự các phép tính.","250 + 100 = 350 ml",new String[]{},0,0);
 add(32,1,80,3,2,Kind.NUMBER,"350 − 250 = ? ml","100","Tính theo đúng thứ tự các phép tính.","350 − 250 = 100 ml",new String[]{},0,0);
 add(32,1,80,3,3,Kind.NUMBER,"350 − 100 = ? ml","250","Tính theo đúng thứ tự các phép tính.","350 − 100 = 250 ml",new String[]{},0,0);
 add(32,1,80,3,4,Kind.NUMBER,"9 × 3 = ? ml","27","Tính theo đúng thứ tự các phép tính.","9 × 3 = 27 ml",new String[]{},0,0);
 add(32,1,80,3,5,Kind.NUMBER,"27 : 3 = ? ml","9","Tính theo đúng thứ tự các phép tính.","27 : 3 = 9 ml",new String[]{},0,0);
 add(32,1,80,3,6,Kind.NUMBER,"27 : 9 = ? ml","3","Tính theo đúng thứ tự các phép tính.","27 : 9 = 3 ml",new String[]{},0,0);
 add(32,1,80,4,1,Kind.NUMBER,"Bình 1 l nước, rót đầy ca 500 ml và 300 ml. Còn bao nhiêu ml?","200","1 000 − 500 − 300","Đáp án: 200",new String[]{},0,0);
}
private static void l33(){
 add(33,1,81,1,1,Kind.CHOICE,"Nha Trang 32 °C, Sa Pa 15 °C, Hạ Long 26 °C. Hạ Long nóng hơn nơi nào?","Sa Pa","So sánh các số đo.","Đáp án: Sa Pa",new String[]{"Sa Pa","Nha Trang"},0,0);
 add(33,1,81,1,2,Kind.CHOICE,"Nơi nóng nhất? Nha Trang 32 °C, Sa Pa 15 °C, Hạ Long 26 °C.","Nha Trang","Tìm số lớn nhất.","Đáp án: Nha Trang",new String[]{"Sa Pa","Nha Trang","Hạ Long"},0,0);
 add(33,1,81,1,3,Kind.CHOICE,"Nơi lạnh nhất trong ba nơi?","Sa Pa","15 °C là thấp nhất.","Đáp án: Sa Pa",new String[]{"Nha Trang","Hạ Long","Sa Pa"},0,0);
 add(33,1,81,2,1,Kind.NUMBER,"Ba mươi lăm độ xê viết là ? °C","35","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 35",new String[]{},0,0);
 add(33,1,81,2,2,Kind.NUMBER,"Ba mươi mốt độ xê viết là ? °C","31","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 31",new String[]{},0,0);
 add(33,1,81,2,3,Kind.CHOICE,"15 °C đọc thế nào?","Mười lăm độ xê","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: Mười lăm độ xê",new String[]{"Mười lăm độ xê","Năm mươi độ xê","Mười lăm gam"},0,0);
 add(33,1,81,3,1,Kind.NUMBER,"Ba người có nhiệt độ 38 °C, 36 °C, 37 °C. Người thứ nhất cao nhất, thứ hai thấp nhất. Người thứ ba bao nhiêu °C?","37","Loại số lớn nhất và nhỏ nhất.","Đáp án: 37",new String[]{},0,0);
 add(33,1,81,4,1,Kind.CHOICE,"Sáng 18 °C, trưa 30 °C, chiều 24 °C, đêm 12 °C. Chọn thứ tự từ cao xuống thấp.","30, 24, 18, 12","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 30, 24, 18, 12",new String[]{"30, 24, 18, 12","12, 18, 24, 30","30, 18, 24, 12"},0,0);
}
private static void l34(){
 add(34,1,82,1,1,Kind.PRACTICE,"Dùng thước thật đo chiều cao ba quân cờ trong VBT tr.82, ghi số mm.","done","Thực hành với vở bài tập hoặc đồ dùng thật; nhờ người lớn kiểm tra.","Đặt thước từ chân đến đỉnh từng quân cờ. Không dùng kích thước trên màn hình làm số đo thật.",new String[]{},0,0);
 add(34,1,82,2,1,Kind.CHOICE,"Quân xe cờ vua nặng khoảng bao nhiêu?","10 g","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 10 g",new String[]{"10 g","1 kg"},0,0);
 add(34,1,82,2,2,Kind.CHOICE,"Gói đường nặng khoảng bao nhiêu?","1 kg","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 1 kg",new String[]{"10 g","1 kg"},0,0);
 add(34,1,82,2,3,Kind.CHOICE,"Quả tạ tay nặng khoảng bao nhiêu?","5 kg","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 5 kg",new String[]{"500 g","5 kg"},0,0);
 add(34,1,82,3,1,Kind.PRACTICE,"Đo nhiệt độ không khí vào thứ Năm, thứ Sáu, thứ Bảy, Chủ nhật và ghi bảng.","done","Thực hành với vở bài tập hoặc đồ dùng thật; nhờ người lớn kiểm tra.","Ghi số đo thực tế từng ngày; không có đáp án cố định.",new String[]{},0,0);
 add(34,1,83,4,1,Kind.CHOICE,"Cân hai đĩa, quả cân 100 g và 200 g. Một lần cân để lấy 300 g gạo: làm sao?","Cân gạo bằng cả hai quả cân","100 + 200 = 300 g.","Đáp án: Cân gạo bằng cả hai quả cân",new String[]{"Cân gạo bằng cả hai quả cân","Chỉ dùng quả 100 g","Chỉ dùng quả 200 g"},0,0);
 add(34,1,83,4,2,Kind.CHOICE,"Từ túi 1 kg, lấy 700 g gạo chỉ cân một lần: làm sao?","Cân 300 g, giữ phần còn lại","1 000 − 300 = 700 g.","Đáp án: Cân 300 g, giữ phần còn lại",new String[]{"Cân 300 g, giữ phần còn lại","Cân 200 g, giữ phần còn lại","Cân 100 g, giữ phần còn lại"},0,0);
 add(34,2,83,1,1,Kind.THERMO,"Đọc nhiệt kế thứ nhất (°C).","40","Đọc ngang đỉnh cột màu.","40 °C",new String[]{},40,0);
 add(34,2,83,1,2,Kind.THERMO,"Đọc nhiệt kế thứ hai (°C).","0","Đọc ngang đỉnh cột màu.","0 °C",new String[]{},0,0);
 add(34,2,83,1,3,Kind.THERMO,"Đọc nhiệt kế thứ ba (°C).","38","Đọc ngang đỉnh cột màu: mỗi vạch nhỏ là 1 °C.","38 °C.",new String[]{},38,0);
 add(34,2,84,2,1,Kind.CHOICE,"Xe đạp trẻ em nặng khoảng bao nhiêu?","20 kg","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 20 kg",new String[]{"20 g","20 kg","2 kg"},0,0);
 add(34,2,84,2,2,Kind.CHOICE,"Máy tính xách tay nặng khoảng bao nhiêu?","2 kg","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 2 kg",new String[]{"20 g","20 kg","2 kg"},0,0);
 add(34,2,84,2,3,Kind.CHOICE,"Bút máy nặng khoảng bao nhiêu?","20 g","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 20 g",new String[]{"20 g","20 kg","2 kg"},0,0);
 add(34,2,84,3,1,Kind.CHOICE,"A 500 ml, B 200 ml, C 300 ml, D 150 ml. Ca nào nhiều nước nhất?","A","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: A",new String[]{"A","B","C","D"},0,0);
 add(34,2,84,3,2,Kind.MULTI,"A 500 ml, B 200 ml, C 300 ml, D 150 ml. Chọn hai ca tổng 500 ml.","1,2","Tìm hai số cộng bằng 500.","Chọn: B, C",new String[]{"A","B","C","D"},0,0);
 add(34,2,84,3,3,Kind.NUMBER,"Ca A 500 ml, ca D 150 ml. A nhiều hơn D bao nhiêu ml?","350","500 − 150","Đáp án: 350",new String[]{},0,0);
 add(34,2,84,4,1,Kind.POUR,"Dùng hai ca 250 ml và 100 ml để lấy đúng 50 ml nước.","50","Đổ đầy ca 250 ml, rót sang ca 100 ml; đổ hết ca nhỏ rồi rót tiếp.","250 − 100 − 100 = 50 ml.",new String[]{},250,100);
}
private static void l35(){
 add(35,1,85,1,1,Kind.NUMBER,"832 − 228 = ? mm","604","Tính theo đúng thứ tự các phép tính.","832 − 228 = 604 mm",new String[]{},0,0);
 add(35,1,85,1,2,Kind.NUMBER,"127 + 328 = ? mm","455","Tính theo đúng thứ tự các phép tính.","127 + 328 = 455 mm",new String[]{},0,0);
 add(35,1,85,1,3,Kind.NUMBER,"37 + 182 = ? g","219","Tính theo đúng thứ tự các phép tính.","37 + 182 = 219 g",new String[]{},0,0);
 add(35,1,85,1,4,Kind.NUMBER,"57 − 37 + 50 = ? g","70","Tính theo đúng thứ tự các phép tính.","57 − 37 + 50 = 70 g",new String[]{},0,0);
 add(35,1,85,1,5,Kind.NUMBER,"215 + 37 = ? ml","252","Tính theo đúng thứ tự các phép tính.","215 + 37 = 252 ml",new String[]{},0,0);
 add(35,1,85,1,6,Kind.NUMBER,"32 − 15 + 80 = ? ml","97","Tính theo đúng thứ tự các phép tính.","32 − 15 + 80 = 97 ml",new String[]{},0,0);
 add(35,1,85,2,1,Kind.NUMBER,"Hai kiện hàng giống nhau cân bằng với hai quả cân 500 g. Mỗi kiện nặng bao nhiêu gam?","500","Cộng hai quả cân rồi chia cho 2.","(500 + 500) : 2 = 500 g.",new String[]{},0,0);
 add(35,1,85,2,2,Kind.NUMBER,"Cốc và 50 g cân bằng 200 g và 100 g. Cốc nặng bao nhiêu gam?","250","200 + 100 − 50","Đáp án: 250",new String[]{},0,0);
 add(35,1,85,3,1,Kind.NUMBER,"42 : 2 = ? mm","21","Tính theo đúng thứ tự các phép tính.","42 : 2 = 21 mm",new String[]{},0,0);
 add(35,1,85,3,2,Kind.NUMBER,"42 × 2 = ? mm","84","Tính theo đúng thứ tự các phép tính.","42 × 2 = 84 mm",new String[]{},0,0);
 add(35,1,85,3,3,Kind.NUMBER,"72 : 3 = ? mm","24","Tính theo đúng thứ tự các phép tính.","72 : 3 = 24 mm",new String[]{},0,0);
 add(35,1,85,3,4,Kind.NUMBER,"38 × 2 = ? mm","76","Tính theo đúng thứ tự các phép tính.","38 × 2 = 76 mm",new String[]{},0,0);
 add(35,1,86,4,1,Kind.POUR,"Dùng hai cốc 250 ml và 400 ml để lấy đúng 100 ml nước.","100","Rót hai cốc 250 ml vào cốc 400 ml. Giữ phần còn lại ở cốc nhỏ.","250 + 250 − 400 = 100 ml.",new String[]{},250,400);
 add(35,2,86,1,1,Kind.NUMBER,"24 × 4 = ? mm","96","Tính theo đúng thứ tự các phép tính.","24 × 4 = 96 mm",new String[]{},0,0);
 add(35,2,86,1,2,Kind.NUMBER,"96 : 3 = ? mm","32","Tính theo đúng thứ tự các phép tính.","96 : 3 = 32 mm",new String[]{},0,0);
 add(35,2,86,1,3,Kind.NUMBER,"60 : 4 = ? ml","15","Tính theo đúng thứ tự các phép tính.","60 : 4 = 15 ml",new String[]{},0,0);
 add(35,2,86,1,4,Kind.NUMBER,"15 × 2 = ? ml","30","Tính theo đúng thứ tự các phép tính.","15 × 2 = 30 ml",new String[]{},0,0);
 add(35,2,86,2,1,Kind.CLINICAL,"Nhiệt kế trong hình chỉ bao nhiêu độ C?","38","Đọc vạch ngang với đầu cột màu.","Nhiệt độ trên hình: 38 °C.",new String[]{},38,0);
 add(35,2,86,2,2,Kind.CHOICE,"Trong bài toán, Nam đo được 38 °C. Theo em, Nam có bị sốt không?","Có, nhiệt độ cao hơn bình thường","So sánh số đo với nhiệt độ cơ thể thường gặp.","Trong tình huống bài toán, 38 °C là nhiệt độ sốt.",new String[]{"Có, nhiệt độ cao hơn bình thường","Không, nhiệt độ rất thấp","Không, 38 °C là nhiệt độ môi trường"},0,0);
 add(35,2,87,3,1,Kind.NUMBER,"Hộp sữa 250 ml, dùng 80 ml làm bánh. Còn bao nhiêu ml?","170","250 − 80","Đáp án: 170",new String[]{},0,0);
 add(35,2,87,4,1,Kind.NUMBER,"Hộp B và 100 g cân bằng 500 g. B nặng bao nhiêu gam?","400","500 − 100","Đáp án: 400",new String[]{},0,0);
 add(35,2,87,4,2,Kind.NUMBER,"Hộp A cân bằng hai hộp B 400 g. A nặng bao nhiêu gam?","800","400 × 2","Đáp án: 800",new String[]{},0,0);
}
private static void l36(){
 add(36,1,88,1,1,Kind.MULTIPLY,"314 × 2","628","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","314 × 2 = 628",new String[]{},314,2);
 add(36,1,88,1,2,Kind.MULTIPLY,"150 × 5","750","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","150 × 5 = 750",new String[]{},150,5);
 add(36,1,88,1,3,Kind.MULTIPLY,"251 × 3","753","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","251 × 3 = 753",new String[]{},251,3);
 add(36,1,88,1,4,Kind.MULTIPLY,"224 × 4","896","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","224 × 4 = 896",new String[]{},224,4);
 add(36,1,88,2,1,Kind.MULTIPLY,"230 × 3","690","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","230 × 3 = 690",new String[]{},230,3);
 add(36,1,88,2,2,Kind.MULTIPLY,"123 × 2","246","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","123 × 2 = 246",new String[]{},123,2);
 add(36,1,88,2,3,Kind.MULTIPLY,"237 × 2","474","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","237 × 2 = 474",new String[]{},237,2);
 add(36,1,88,2,4,Kind.MULTIPLY,"205 × 4","820","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","205 × 4 = 820",new String[]{},205,4);
 add(36,1,88,3,1,Kind.NUMBER,"Mai chạy 250 m, Việt chạy gấp 3 lần. Việt chạy bao nhiêu m?","750","250 × 3","Đáp án: 750",new String[]{},0,0);
 add(36,1,88,4,1,Kind.CHOICE,"Dùng 4 số trong 2, 3, 5, 100, 250 để lập hai tích bằng nhau. Số nào không dùng?","3","2 × 250 = 5 × 100","Đáp án: 3",new String[]{"2","3","5","250"},0,0);
 add(36,2,89,1,1,Kind.NUMBER,"105 × 2 = ?","210","Tính theo đúng thứ tự các phép tính.","105 × 2 = 210",new String[]{},0,0);
 add(36,2,89,1,2,Kind.NUMBER,"120 × 5 = ?","600","Tính theo đúng thứ tự các phép tính.","120 × 5 = 600",new String[]{},0,0);
 add(36,2,89,1,3,Kind.NUMBER,"126 × 4 = ?","504","Tính theo đúng thứ tự các phép tính.","126 × 4 = 504",new String[]{},0,0);
 add(36,2,89,1,4,Kind.NUMBER,"141 × 6 = ?","846","Tính theo đúng thứ tự các phép tính.","141 × 6 = 846",new String[]{},0,0);
 add(36,2,89,2,1,Kind.NUMBER,"200 × 3 = ?","600","Tính theo đúng thứ tự các phép tính.","200 × 3 = 600",new String[]{},0,0);
 add(36,2,89,2,2,Kind.NUMBER,"500 × 2 = ?","1000","Tính theo đúng thứ tự các phép tính.","500 × 2 = 1000",new String[]{},0,0);
 add(36,2,89,2,3,Kind.NUMBER,"300 × 2 = ?","600","Tính theo đúng thứ tự các phép tính.","300 × 2 = 600",new String[]{},0,0);
 add(36,2,89,2,4,Kind.NUMBER,"600 × 1 = ?","600","Tính theo đúng thứ tự các phép tính.","600 × 1 = 600",new String[]{},0,0);
 add(36,2,89,3,1,Kind.NUMBER,"Đảo có 420 con cừu, mỗi năm tăng 180 con. Sau 2 năm có bao nhiêu con?","780","420 + 180 × 2","Đáp án: 780",new String[]{},0,0);
 add(36,2,89,4,1,Kind.NUMBER,"1 kg bột mì, dùng làm 3 bánh, mỗi bánh 200 g. Còn bao nhiêu gam?","400","1 000 − 3 × 200","Đáp án: 400",new String[]{},0,0);
 add(36,2,89,5,1,Kind.NUMBER,"210 × 3 = ?","630","Tính theo đúng thứ tự các phép tính.","210 × 3 = 630",new String[]{},0,0);
 add(36,2,89,5,2,Kind.NUMBER,"340 × 2 = ?","680","Tính theo đúng thứ tự các phép tính.","340 × 2 = 680",new String[]{},0,0);
 add(36,2,89,5,3,Kind.NUMBER,"110 × 4 = ?","440","Tính theo đúng thứ tự các phép tính.","110 × 4 = 440",new String[]{},0,0);
}
private static void l37(){
 add(37,1,90,1,1,Kind.DIVISION,"375 : 3","125,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","375 = 3 × 125; thương 125, dư 0.",new String[]{},375,3);
 add(37,1,90,1,2,Kind.DIVISION,"448 : 2","224,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","448 = 2 × 224; thương 224, dư 0.",new String[]{},448,2);
 add(37,1,90,1,3,Kind.DIVISION,"627 : 4","156,3","Chia từ trái sang phải; số dư nhỏ hơn số chia.","627 = 4 × 156 + 3; thương 156, dư 3.",new String[]{},627,4);
 add(37,1,90,2,1,Kind.NUMBER,"Thuyền phó có 548 đồng vàng, gấp 2 lần thuyền trưởng. Thuyền trưởng có bao nhiêu đồng?","274","548 : 2","Đáp án: 274",new String[]{},0,0);
 add(37,1,90,3,1,Kind.NUMBER,"124 : 4 = ? giờ","31","Tính theo đúng thứ tự các phép tính.","124 : 4 = 31 giờ",new String[]{},0,0);
 add(37,1,90,3,2,Kind.NUMBER,"702 : 9 = ? dm","78","Tính theo đúng thứ tự các phép tính.","702 : 9 = 78 dm",new String[]{},0,0);
 add(37,1,90,3,3,Kind.NUMBER,"384 : 6 = ? kg","64","Tính theo đúng thứ tự các phép tính.","384 : 6 = 64 kg",new String[]{},0,0);
 add(37,1,90,3,4,Kind.NUMBER,"343 : 7 = ? l","49","Tính theo đúng thứ tự các phép tính.","343 : 7 = 49 l",new String[]{},0,0);
 add(37,1,91,4,1,Kind.CHOICE,"Dùng 4 số khác nhau trong 2, 4, 5, 124, 155 để lập hai phép chia bằng nhau. Số chia cuối có thể là những số nào?","4 hoặc 5","124 : 4 = 155 : 5 = 31.","Đáp án: 4 hoặc 5",new String[]{"2 hoặc 4","4 hoặc 5","2 hoặc 5","124 hoặc 155"},0,0);
 add(37,2,91,1,1,Kind.DIVISION,"250 : 2","125,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","250 = 2 × 125; thương 125, dư 0.",new String[]{},250,2);
 add(37,2,91,1,2,Kind.DIVISION,"407 : 4","101,3","Chia từ trái sang phải; số dư nhỏ hơn số chia.","407 = 4 × 101 + 3; thương 101, dư 3.",new String[]{},407,4);
 add(37,2,91,1,3,Kind.DIVISION,"210 : 7","30,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","210 = 7 × 30; thương 30, dư 0.",new String[]{},210,7);
 add(37,2,91,2,1,Kind.NUMBER,"Hôm qua bán 450 bánh, hôm nay giảm 5 lần. Hôm nay bán bao nhiêu bánh?","90","450 : 5","Đáp án: 90",new String[]{},0,0);
 add(37,2,91,3,1,Kind.CHOICE,"240 : 4 = 6 (dư 0)","Sai","Tính lại để kiểm tra.","240 : 4 = 60.",new String[]{"Đúng","Sai"},0,0);
 add(37,2,91,3,2,Kind.CHOICE,"425 : 6 = 70 (dư 5)","Đúng","Tính lại để kiểm tra.","425 = 6 × 70 + 5",new String[]{"Đúng","Sai"},0,0);
 add(37,2,91,3,3,Kind.CHOICE,"812 : 8 = 104","Sai","Tính lại để kiểm tra.","812 : 8 = 101 (dư 4).",new String[]{"Đúng","Sai"},0,0);
 add(37,2,91,3,4,Kind.CHOICE,"354 : 5 = 7 (dư 4)","Sai","Tính lại để kiểm tra.","354 : 5 = 70 (dư 4).",new String[]{"Đúng","Sai"},0,0);
 add(37,2,92,4,1,Kind.NUMBER,"4 ô vuông có tổng 40; 6 hình tròn có tổng 600. Tính tổng 4 ô vuông và 4 hình tròn.","440","Một vuông = 10; một tròn = 100.","4 × 10 + 4 × 100 = 440",new String[]{},0,0);
 add(37,3,92,1,1,Kind.DIVISION,"317 : 3","105,2","Chia từ trái sang phải; số dư nhỏ hơn số chia.","317 = 3 × 105 + 2; thương 105, dư 2.",new String[]{},317,3);
 add(37,3,92,1,2,Kind.DIVISION,"625 : 5","125,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","625 = 5 × 125; thương 125, dư 0.",new String[]{},625,5);
 add(37,3,92,1,3,Kind.DIVISION,"125 : 6","20,5","Chia từ trái sang phải; số dư nhỏ hơn số chia.","125 = 6 × 20 + 5; thương 20, dư 5.",new String[]{},125,6);
 add(37,3,92,2,1,Kind.NUMBER,"270 : 3 = ?","90","Tính theo đúng thứ tự các phép tính.","270 : 3 = 90",new String[]{},0,0);
 add(37,3,92,2,2,Kind.NUMBER,"450 : 9 = ?","50","Tính theo đúng thứ tự các phép tính.","450 : 9 = 50",new String[]{},0,0);
 add(37,3,92,2,3,Kind.NUMBER,"360 : 6 = ?","60","Tính theo đúng thứ tự các phép tính.","360 : 6 = 60",new String[]{},0,0);
 add(37,3,93,3,1,Kind.NUMBER,"930 ml mật ong chia đều vào 3 hũ. Mỗi hũ có bao nhiêu ml?","310","930 : 3","Đáp án: 310",new String[]{},0,0);
 add(37,3,93,4,1,Kind.NUMBER,"3 mũi tên vòng ngoài được 300 điểm; 2 vòng ngoài + 1 vòng trong được 350. 2 vòng trong + 1 vòng ngoài được bao nhiêu?","400","Vòng ngoài 100; vòng trong 150.","150 × 2 + 100 = 400",new String[]{},0,0);
 add(37,3,93,5,1,Kind.NUMBER,"480 : 3 = ?","160","Tính theo đúng thứ tự các phép tính.","480 : 3 = 160",new String[]{},0,0);
 add(37,3,93,5,2,Kind.NUMBER,"160 : 4 = ?","40","Tính theo đúng thứ tự các phép tính.","160 : 4 = 40",new String[]{},0,0);
 add(37,3,93,5,3,Kind.NUMBER,"40 × 5 = ?","200","Tính theo đúng thứ tự các phép tính.","40 × 5 = 200",new String[]{},0,0);
 add(37,3,93,5,4,Kind.NUMBER,"200 : 2 = ?","100","Tính theo đúng thứ tự các phép tính.","200 : 2 = 100",new String[]{},0,0);
 add(37,3,93,5,5,Kind.NUMBER,"100 × 8 = ?","800","Tính theo đúng thứ tự các phép tính.","100 × 8 = 800",new String[]{},0,0);
}
private static void l38(){
 add(38,1,94,1,1,Kind.NUMBER,"162 + 29 − 18 = ?","173","Tính theo đúng thứ tự các phép tính.","162 + 29 − 18 = 173",new String[]{},0,0);
 add(38,1,94,1,2,Kind.NUMBER,"18 × 7 = ?","126","Tính theo đúng thứ tự các phép tính.","18 × 7 = 126",new String[]{},0,0);
 add(38,1,94,1,3,Kind.NUMBER,"84 : 6 = ?","14","Tính theo đúng thứ tự các phép tính.","84 : 6 = 14",new String[]{},0,0);
 add(38,1,94,2,1,Kind.NUMBER,"81 : 9 = ?","9","Tính theo đúng thứ tự các phép tính.","81 : 9 = 9",new String[]{},0,0);
 add(38,1,94,2,2,Kind.NUMBER,"36 + 17 − 45 = ?","8","Tính theo đúng thứ tự các phép tính.","36 + 17 − 45 = 8",new String[]{},0,0);
 add(38,1,94,2,3,Kind.NUMBER,"62 − 45 + 28 = ?","45","Tính theo đúng thứ tự các phép tính.","62 − 45 + 28 = 45",new String[]{},0,0);
 add(38,1,94,2,4,Kind.NUMBER,"17 × 3 = ?","51","Tính theo đúng thứ tự các phép tính.","17 × 3 = 51",new String[]{},0,0);
 add(38,1,94,3,1,Kind.CHOICE,"Biểu thức nào lớn nhất?","18 × 4","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 18 × 4",new String[]{"49 + 27 − 58","18 × 4","56 − 18 + 23","93 : 3"},0,0);
 add(38,1,94,3,2,Kind.CHOICE,"Biểu thức nào bé nhất?","49 + 27 − 58","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 49 + 27 − 58",new String[]{"49 + 27 − 58","18 × 4","56 − 18 + 23","93 : 3"},0,0);
 add(38,2,95,1,1,Kind.NUMBER,"64 − 25 : 5 = ?","59","Tính theo đúng thứ tự các phép tính.","64 − 25 : 5 = 59",new String[]{},0,0);
 add(38,2,95,1,2,Kind.NUMBER,"15 + 4 × 9 = ?","51","Tính theo đúng thứ tự các phép tính.","15 + 4 × 9 = 51",new String[]{},0,0);
 add(38,2,95,1,3,Kind.NUMBER,"90 − 7 × 3 = ?","69","Tính theo đúng thứ tự các phép tính.","90 − 7 × 3 = 69",new String[]{},0,0);
 add(38,2,95,1,4,Kind.NUMBER,"98 + 42 : 6 = ?","105","Tính theo đúng thứ tự các phép tính.","98 + 42 : 6 = 105",new String[]{},0,0);
 add(38,2,95,2,1,Kind.NUMBER,"25 + 17 × 2 = ?","59","Tính theo đúng thứ tự các phép tính.","25 + 17 × 2 = 59",new String[]{},0,0);
 add(38,2,95,2,2,Kind.NUMBER,"92 − 26 × 3 = ?","14","Tính theo đúng thứ tự các phép tính.","92 − 26 × 3 = 14",new String[]{},0,0);
 add(38,2,95,2,3,Kind.NUMBER,"40 − 36 : 2 = ?","22","Tính theo đúng thứ tự các phép tính.","40 − 36 : 2 = 22",new String[]{},0,0);
 add(38,2,95,2,4,Kind.NUMBER,"87 + 48 : 6 = ?","95","Tính theo đúng thứ tự các phép tính.","87 + 48 : 6 = 95",new String[]{},0,0);
 add(38,2,95,3,1,Kind.CHOICE,"40 + 60 : 2 = 50","Sai","Tính lại để kiểm tra.","40 + 30 = 70",new String[]{"Đúng","Sai"},0,0);
 add(38,2,95,3,2,Kind.CHOICE,"40 + 60 : 2 = 70","Đúng","Tính lại để kiểm tra.","Chia trước cộng sau.",new String[]{"Đúng","Sai"},0,0);
 add(38,2,95,3,3,Kind.CHOICE,"70 − 30 : 5 = 64","Đúng","Tính lại để kiểm tra.","70 − 6 = 64",new String[]{"Đúng","Sai"},0,0);
 add(38,2,95,3,4,Kind.CHOICE,"70 − 30 : 5 = 8","Sai","Tính lại để kiểm tra.","70 − 6 = 64",new String[]{"Đúng","Sai"},0,0);
 add(38,2,95,4,1,Kind.NUMBER,"Thùng 50 l dầu, lấy 3 lần, mỗi lần 10 l. Còn bao nhiêu lít?","20","50 − 3 × 10","Đáp án: 20",new String[]{},0,0);
 add(38,3,96,1,1,Kind.NUMBER,"64 : (25 − 17) = ?","8","Tính theo đúng thứ tự các phép tính.","64 : (25 − 17) = 8",new String[]{},0,0);
 add(38,3,96,1,2,Kind.NUMBER,"(70 − 15) : 5 = ?","11","Tính theo đúng thứ tự các phép tính.","(70 − 15) : 5 = 11",new String[]{},0,0);
 add(38,3,96,1,3,Kind.NUMBER,"26 × (71 − 68) = ?","78","Tính theo đúng thứ tự các phép tính.","26 × (71 − 68) = 78",new String[]{},0,0);
 add(38,3,96,1,4,Kind.NUMBER,"50 − (50 − 10) = ?","10","Tính theo đúng thứ tự các phép tính.","50 − (50 − 10) = 10",new String[]{},0,0);
 add(38,3,96,2,1,Kind.NUMBER,"23 × (42 − 38) = ?","92","Tính theo đúng thứ tự các phép tính.","23 × (42 − 38) = 92",new String[]{},0,0);
 add(38,3,96,2,2,Kind.NUMBER,"75 : (18 − 13) = ?","15","Tính theo đúng thứ tự các phép tính.","75 : (18 − 13) = 15",new String[]{},0,0);
 add(38,3,96,2,3,Kind.NUMBER,"(30 + 10) : 8 = ?","5","Tính theo đúng thứ tự các phép tính.","(30 + 10) : 8 = 5",new String[]{},0,0);
 add(38,3,96,2,4,Kind.NUMBER,"(48 − 21) × 3 = ?","81","Tính theo đúng thứ tự các phép tính.","(48 − 21) × 3 = 81",new String[]{},0,0);
 add(38,3,96,3,1,Kind.CHOICE,"Biểu thức nào lớn nhất?","80 : (3 + 5)","Các giá trị là 9, 8, 10.","Đáp án: 80 : (3 + 5)",new String[]{"72 : (16 − 8)","2 × (35 − 31)","80 : (3 + 5)"},0,0);
 add(38,3,96,4,1,Kind.NUMBER,"Một thỏ 4 chân và một gà 2 chân. Một cặp có bao nhiêu chân?","6","4 + 2","Đáp án: 6",new String[]{},0,0);
 add(38,3,96,4,2,Kind.NUMBER,"8 cặp như vậy có tổng bao nhiêu chân?","48","6 × 8","Đáp án: 48",new String[]{},0,0);
 add(38,4,97,1,1,Kind.CHOICE,"Biểu thức nào lớn nhất?","80 + 40 : 4","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 80 + 40 : 4",new String[]{"12 × (7 − 4)","12 × 7 − 4","(80 + 40) : 4","80 + 40 : 4"},0,0);
 add(38,4,97,1,2,Kind.CHOICE,"Biểu thức nào bé nhất?","(80 + 40) : 4","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: (80 + 40) : 4",new String[]{"12 × (7 − 4)","12 × 7 − 4","(80 + 40) : 4","80 + 40 : 4"},0,0);
 add(38,4,97,2,1,Kind.NUMBER,"Mai cắm 5 lọ rồi thêm 3 lọ, mỗi lọ 10 bông. Có tất cả bao nhiêu bông?","80","(5 + 3) × 10","Đáp án: 80",new String[]{},0,0);
 add(38,4,97,3,1,Kind.NUMBER,"476 + 70 + 30 = ?","576","Tính theo đúng thứ tự các phép tính.","476 + 70 + 30 = 576",new String[]{},0,0);
 add(38,4,97,3,2,Kind.NUMBER,"67 + 125 + 75 = ?","267","Tính theo đúng thứ tự các phép tính.","67 + 125 + 75 = 267",new String[]{},0,0);
 add(38,4,97,4,1,Kind.CHOICE,"Giá trị 15 × (7 − 4), 74 : (6 − 4), (24 + 60) : 4 theo thứ tự bé đến lớn?","21, 37, 45","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 21, 37, 45",new String[]{"21, 37, 45","45, 37, 21","37, 21, 45"},0,0);
}
private static void l39(){
 add(39,1,98,1,1,Kind.NUMBER,"8 : 2 = ? lần","4","Tính theo đúng thứ tự các phép tính.","8 : 2 = 4 lần",new String[]{},0,0);
 add(39,1,98,1,2,Kind.NUMBER,"20 : 5 = ? lần","4","Tính theo đúng thứ tự các phép tính.","20 : 5 = 4 lần",new String[]{},0,0);
 add(39,1,98,1,3,Kind.NUMBER,"70 : 7 = ? lần","10","Tính theo đúng thứ tự các phép tính.","70 : 7 = 10 lần",new String[]{},0,0);
 add(39,1,98,2,1,Kind.NUMBER,"AB dài 18 cm, CD dài 6 cm. AB hơn CD bao nhiêu cm?","12","18 − 6","Đáp án: 12",new String[]{},0,0);
 add(39,1,98,2,2,Kind.NUMBER,"AB 18 cm gấp CD 6 cm mấy lần?","3","18 : 6","Đáp án: 3",new String[]{},0,0);
 add(39,1,98,3,1,Kind.NUMBER,"27 bạn học cờ vua, 9 bạn đá cầu. Cờ vua gấp đá cầu mấy lần?","3","27 : 9","Đáp án: 3",new String[]{},0,0);
 add(39,2,99,1,1,Kind.NUMBER,"18 hơn 6 bao nhiêu đơn vị?","12","Trừ hai số.","Đáp án: 12",new String[]{},0,0);
 add(39,2,99,1,2,Kind.NUMBER,"18 gấp 6 mấy lần?","3","Chia số lớn cho số bé.","Đáp án: 3",new String[]{},0,0);
 add(39,2,99,1,3,Kind.NUMBER,"35 hơn 7 bao nhiêu đơn vị?","28","Trừ hai số.","Đáp án: 28",new String[]{},0,0);
 add(39,2,99,1,4,Kind.NUMBER,"35 gấp 7 mấy lần?","5","Chia số lớn cho số bé.","Đáp án: 5",new String[]{},0,0);
 add(39,2,99,1,5,Kind.NUMBER,"48 hơn 8 bao nhiêu đơn vị?","40","Trừ hai số.","Đáp án: 40",new String[]{},0,0);
 add(39,2,99,1,6,Kind.NUMBER,"48 gấp 8 mấy lần?","6","Chia số lớn cho số bé.","Đáp án: 6",new String[]{},0,0);
 add(39,2,99,2,1,Kind.NUMBER,"21 : 3 = ?","7","Tính theo đúng thứ tự các phép tính.","21 : 3 = 7",new String[]{},0,0);
 add(39,2,99,2,2,Kind.NUMBER,"7 × 7 = ?","49","Tính theo đúng thứ tự các phép tính.","7 × 7 = 49",new String[]{},0,0);
 add(39,2,99,3,1,Kind.NUMBER,"Chó 18 kg, ngỗng 6 kg. Chó nặng gấp mấy lần ngỗng?","3","18 : 6","Đáp án: 3",new String[]{},0,0);
 add(39,2,99,3,2,Kind.NUMBER,"Ngỗng 6 kg, gà 2 kg. Ngỗng gấp gà mấy lần?","3","6 : 2","Đáp án: 3",new String[]{},0,0);
 add(39,2,99,3,3,Kind.NUMBER,"Chó 18 kg, gà 2 kg. Chó gấp gà mấy lần?","9","18 : 2","Đáp án: 9",new String[]{},0,0);
 add(39,2,99,4,1,Kind.NUMBER,"24 : 6 = ? lần","4","Tính theo đúng thứ tự các phép tính.","24 : 6 = 4 lần",new String[]{},0,0);
 add(39,2,99,4,2,Kind.NUMBER,"24 : 2 = ? lần","12","Tính theo đúng thứ tự các phép tính.","24 : 2 = 12 lần",new String[]{},0,0);
 add(39,2,99,4,3,Kind.NUMBER,"24 : 8 = ? lần","3","Tính theo đúng thứ tự các phép tính.","24 : 8 = 3 lần",new String[]{},0,0);
 add(39,2,99,4,4,Kind.NUMBER,"24 : 4 = ? lần","6","Tính theo đúng thứ tự các phép tính.","24 : 4 = 6 lần",new String[]{},0,0);
}
private static void l40(){
 add(40,1,100,1,1,Kind.MULTIPLY,"116 × 5","580","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","116 × 5 = 580",new String[]{},116,5);
 add(40,1,100,1,2,Kind.MULTIPLY,"308 × 3","924","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","308 × 3 = 924",new String[]{},308,3);
 add(40,1,100,1,3,Kind.DIVISION,"815 : 5","163,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","815 = 5 × 163; thương 163, dư 0.",new String[]{},815,5);
 add(40,1,100,1,4,Kind.DIVISION,"642 : 3","214,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","642 = 3 × 214; thương 214, dư 0.",new String[]{},642,3);
 add(40,1,100,2,1,Kind.NUMBER,"12 gấp 3 lần","36","Nhân hoặc chia theo yêu cầu.","Đáp án: 36",new String[]{},0,0);
 add(40,1,100,2,2,Kind.NUMBER,"12 giảm 2 lần","6","Nhân hoặc chia theo yêu cầu.","Đáp án: 6",new String[]{},0,0);
 add(40,1,100,2,3,Kind.NUMBER,"36 gấp 6 mấy lần?","6","Nhân hoặc chia theo yêu cầu.","Đáp án: 6",new String[]{},0,0);
 add(40,1,100,3,1,Kind.NUMBER,"Can thứ nhất 2 l, can thứ hai gấp 5 lần. Can thứ hai có bao nhiêu lít?","10","2 × 5","Đáp án: 10",new String[]{},0,0);
 add(40,1,100,3,2,Kind.NUMBER,"Hai can 2 l và 10 l. Cả hai có bao nhiêu lít?","12","2 + 10","Đáp án: 12",new String[]{},0,0);
 add(40,1,100,3,3,Kind.NUMBER,"Can 10 l hơn can 2 l bao nhiêu lít?","8","10 − 2","Đáp án: 8",new String[]{},0,0);
 add(40,2,101,1,1,Kind.NUMBER,"473 + 18 − 215 = ?","276","Tính theo đúng thứ tự các phép tính.","473 + 18 − 215 = 276",new String[]{},0,0);
 add(40,2,101,1,2,Kind.NUMBER,"370 − (319 − 270) = ?","321","Tính theo đúng thứ tự các phép tính.","370 − (319 − 270) = 321",new String[]{},0,0);
 add(40,2,101,1,3,Kind.NUMBER,"185 + 71 × 2 = ?","327","Tính theo đúng thứ tự các phép tính.","185 + 71 × 2 = 327",new String[]{},0,0);
 add(40,2,101,1,4,Kind.NUMBER,"38 + 72 × 3 = ?","254","Tính theo đúng thứ tự các phép tính.","38 + 72 × 3 = 254",new String[]{},0,0);
 add(40,2,101,1,5,Kind.CHOICE,"Chọn biểu thức lớn nhất.","185 + 71 × 2","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 185 + 71 × 2",new String[]{"473 + 18 − 215","370 − (319 − 270)","185 + 71 × 2","38 + 72 × 3"},0,0);
 add(40,2,101,1,6,Kind.CHOICE,"Chọn biểu thức bé nhất.","38 + 72 × 3","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 38 + 72 × 3",new String[]{"473 + 18 − 215","370 − (319 − 270)","185 + 71 × 2","38 + 72 × 3"},0,0);
 add(40,2,101,2,1,Kind.NUMBER,"Lợn lúc mua 9 kg, nay 36 kg. Nay gấp mấy lần lúc mua?","4","36 : 9","Đáp án: 4",new String[]{},0,0);
 add(40,2,101,2,2,Kind.NUMBER,"Lợn từ 9 kg lên 36 kg. Tăng bao nhiêu kg?","27","36 − 9","Đáp án: 27",new String[]{},0,0);
 add(40,2,101,3,1,Kind.NUMBER,"9 × 2 × 5 = ?","90","Tính theo đúng thứ tự các phép tính.","9 × 2 × 5 = 90",new String[]{},0,0);
 add(40,2,101,3,2,Kind.NUMBER,"5 × 7 × 2 = ?","70","Tính theo đúng thứ tự các phép tính.","5 × 7 × 2 = 70",new String[]{},0,0);
 add(40,2,101,4,1,Kind.NUMBER,"40 giảm 5 lần được 8. Vậy 40 gấp 8 mấy lần?","5","40 : 8","Đáp án: 5",new String[]{},0,0);
}
private static void l41(){
 add(41,1,102,1,1,Kind.NUMBER,"30 × 2 = ?","60","Tính theo đúng thứ tự các phép tính.","30 × 2 = 60",new String[]{},0,0);
 add(41,1,102,1,2,Kind.NUMBER,"20 × 4 = ?","80","Tính theo đúng thứ tự các phép tính.","20 × 4 = 80",new String[]{},0,0);
 add(41,1,102,1,3,Kind.NUMBER,"50 × 2 = ?","100","Tính theo đúng thứ tự các phép tính.","50 × 2 = 100",new String[]{},0,0);
 add(41,1,102,1,4,Kind.NUMBER,"20 × 2 = ?","40","Tính theo đúng thứ tự các phép tính.","20 × 2 = 40",new String[]{},0,0);
 add(41,1,102,1,5,Kind.NUMBER,"60 : 3 = ?","20","Tính theo đúng thứ tự các phép tính.","60 : 3 = 20",new String[]{},0,0);
 add(41,1,102,1,6,Kind.NUMBER,"100 : 2 = ?","50","Tính theo đúng thứ tự các phép tính.","100 : 2 = 50",new String[]{},0,0);
 add(41,1,102,1,7,Kind.NUMBER,"40 : 2 = ?","20","Tính theo đúng thứ tự các phép tính.","40 : 2 = 20",new String[]{},0,0);
 add(41,1,102,1,8,Kind.NUMBER,"90 : 3 = ?","30","Tính theo đúng thứ tự các phép tính.","90 : 3 = 30",new String[]{},0,0);
 add(41,1,102,2,1,Kind.MULTIPLY,"46 × 2","92","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","46 × 2 = 92",new String[]{},46,2);
 add(41,1,102,2,2,Kind.MULTIPLY,"13 × 7","91","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","13 × 7 = 91",new String[]{},13,7);
 add(41,1,102,2,3,Kind.MULTIPLY,"29 × 3","87","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","29 × 3 = 87",new String[]{},29,3);
 add(41,1,102,2,4,Kind.DIVISION,"82 : 2","41,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","82 = 2 × 41; thương 41, dư 0.",new String[]{},82,2);
 add(41,1,102,2,5,Kind.DIVISION,"72 : 6","12,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","72 = 6 × 12; thương 12, dư 0.",new String[]{},72,6);
 add(41,1,102,2,6,Kind.DIVISION,"97 : 9","10,7","Chia từ trái sang phải; số dư nhỏ hơn số chia.","97 = 9 × 10 + 7; thương 10, dư 7.",new String[]{},97,9);
 add(41,1,102,3,1,Kind.CHOICE,"23 × 4 = 62","Sai","Tính lại để kiểm tra.","23 × 4 = 92",new String[]{"Đúng","Sai"},0,0);
 add(41,1,102,3,2,Kind.CHOICE,"18 × 5 = 90","Đúng","Tính lại để kiểm tra.","18 × 5 = 90",new String[]{"Đúng","Sai"},0,0);
 add(41,1,102,3,3,Kind.CHOICE,"92 : 7 = 12 (dư 8)","Sai","Tính lại để kiểm tra.","92 : 7 = 13 (dư 1)",new String[]{"Đúng","Sai"},0,0);
 add(41,1,102,3,4,Kind.CHOICE,"74 : 4 = 18 (dư 2)","Đúng","Tính lại để kiểm tra.","74 = 4 × 18 + 2",new String[]{"Đúng","Sai"},0,0);
 add(41,1,102,4,1,Kind.NUMBER,"6 xe, mỗi xe chở 32 học sinh. Có bao nhiêu học sinh?","192","32 × 6","Đáp án: 192",new String[]{},0,0);
 add(41,1,103,5,1,Kind.NUMBER,"46 l nước mắm, mỗi can chứa 7 l. Cần ít nhất bao nhiêu can?","7","46 : 7 = 6 dư 4; thêm một can.","Đáp án: 7",new String[]{},0,0);
 add(41,2,103,1,1,Kind.NUMBER,"200 × 3 = ?","600","Tính theo đúng thứ tự các phép tính.","200 × 3 = 600",new String[]{},0,0);
 add(41,2,103,1,2,Kind.NUMBER,"300 × 2 = ?","600","Tính theo đúng thứ tự các phép tính.","300 × 2 = 600",new String[]{},0,0);
 add(41,2,103,1,3,Kind.NUMBER,"200 × 2 = ?","400","Tính theo đúng thứ tự các phép tính.","200 × 2 = 400",new String[]{},0,0);
 add(41,2,103,1,4,Kind.NUMBER,"100 × 6 = ?","600","Tính theo đúng thứ tự các phép tính.","100 × 6 = 600",new String[]{},0,0);
 add(41,2,103,1,5,Kind.NUMBER,"900 : 3 = ?","300","Tính theo đúng thứ tự các phép tính.","900 : 3 = 300",new String[]{},0,0);
 add(41,2,103,1,6,Kind.NUMBER,"600 : 2 = ?","300","Tính theo đúng thứ tự các phép tính.","600 : 2 = 300",new String[]{},0,0);
 add(41,2,103,1,7,Kind.NUMBER,"800 : 8 = ?","100","Tính theo đúng thứ tự các phép tính.","800 : 8 = 100",new String[]{},0,0);
 add(41,2,103,1,8,Kind.NUMBER,"1000 : 5 = ?","200","Tính theo đúng thứ tự các phép tính.","1000 : 5 = 200",new String[]{},0,0);
 add(41,2,103,2,1,Kind.MULTIPLY,"312 × 3","936","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","312 × 3 = 936",new String[]{},312,3);
 add(41,2,103,2,2,Kind.MULTIPLY,"105 × 7","735","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","105 × 7 = 735",new String[]{},105,7);
 add(41,2,103,2,3,Kind.MULTIPLY,"81 × 9","729","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","81 × 9 = 729",new String[]{},81,9);
 add(41,2,103,2,4,Kind.DIVISION,"936 : 3","312,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","936 = 3 × 312; thương 312, dư 0.",new String[]{},936,3);
 add(41,2,103,2,5,Kind.DIVISION,"852 : 6","142,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","852 = 6 × 142; thương 142, dư 0.",new String[]{},852,6);
 add(41,2,103,2,6,Kind.DIVISION,"690 : 8","86,2","Chia từ trái sang phải; số dư nhỏ hơn số chia.","690 = 8 × 86 + 2; thương 86, dư 2.",new String[]{},690,8);
 add(41,2,104,3,1,Kind.CHOICE,"96 × 8 = 728","Sai","Tính lại để kiểm tra.","96 × 8 = 768",new String[]{"Đúng","Sai"},0,0);
 add(41,2,104,3,2,Kind.CHOICE,"108 × 5 = 540","Đúng","Tính lại để kiểm tra.","108 × 5 = 540",new String[]{"Đúng","Sai"},0,0);
 add(41,2,104,3,3,Kind.CHOICE,"839 : 4 = 29 (dư 3)","Sai","Tính lại để kiểm tra.","839 : 4 = 209 (dư 3)",new String[]{"Đúng","Sai"},0,0);
 add(41,2,104,3,4,Kind.CHOICE,"740 : 8 = 92 (dư 4)","Đúng","Tính lại để kiểm tra.","740 = 8 × 92 + 4",new String[]{"Đúng","Sai"},0,0);
 add(41,2,104,4,1,Kind.NUMBER,"132 quả táo, mỗi khay 6 quả. Xếp được bao nhiêu khay?","22","132 : 6","Đáp án: 22",new String[]{},0,0);
 add(41,2,104,5,1,Kind.NUMBER,"1□3 × 6 = 61□. Tìm số bị nhân.","103","Thử chữ số, kiểm tra tích.","103 × 6 = 618.",new String[]{},0,0);
 add(41,2,104,5,4,Kind.NUMBER,"103 × 6 = 61□. Điền chữ số cuối của tích.","8","Nhân 103 với 6.","103 × 6 = 618.",new String[]{},0,0);
 add(41,2,104,5,2,Kind.NUMBER,"4□ × 7 = □□8. Tìm thừa số hai chữ số.","44","Chữ số hàng đơn vị nhân 7 phải tận cùng bằng 8.","44 × 7 = 308.",new String[]{},0,0);
 add(41,2,104,5,5,Kind.NUMBER,"44 × 7 = ? (điền cả tích).","308","Nhân hàng đơn vị, nhớ sang hàng chục.","44 × 7 = 308.",new String[]{},0,0);
 add(41,2,104,5,3,Kind.NUMBER,"□□5 × 3 = 64□. Tìm thừa số ba chữ số.","215","Thử chữ số, kiểm tra tích.","215 × 3 = 645.",new String[]{},0,0);
 add(41,2,104,5,6,Kind.NUMBER,"215 × 3 = 64□. Điền chữ số cuối của tích.","5","5 × 3 = 15.","215 × 3 = 645.",new String[]{},0,0);
 add(41,3,104,1,1,Kind.NUMBER,"181 × 4 = ?","724","Tính theo đúng thứ tự các phép tính.","181 × 4 = 724",new String[]{},0,0);
 add(41,3,104,1,2,Kind.NUMBER,"806 : 2 = ?","403","Tính theo đúng thứ tự các phép tính.","806 : 2 = 403",new String[]{},0,0);
 add(41,3,104,1,3,Kind.NUMBER,"465 : 7 dư bao nhiêu?","3","465 = 7 × 66 + 3","Đáp án: 3",new String[]{},0,0);
 add(41,3,105,2,1,Kind.NUMBER,"? × 8 = 280","35","Dùng phép tính ngược.","Đáp án: 35",new String[]{},0,0);
 add(41,3,105,2,2,Kind.NUMBER,"? : 9 = 108","972","Dùng phép tính ngược.","Đáp án: 972",new String[]{},0,0);
 add(41,3,105,2,3,Kind.NUMBER,"84 : ? = 6","14","Dùng phép tính ngược.","Đáp án: 14",new String[]{},0,0);
 add(41,3,105,2,4,Kind.NUMBER,"? × 7 = 161","23","Dùng phép tính ngược.","Đáp án: 23",new String[]{},0,0);
 add(41,3,105,3,1,Kind.NUMBER,"19 cây chanh, cam gấp 4 lần. Có bao nhiêu cây cả hai loại?","95","19 × 4 + 19","Đáp án: 95",new String[]{},0,0);
 add(41,3,105,4,1,Kind.NUMBER,"Có 24 quả dâu. Một phần sáu là bao nhiêu quả?","4","24 : 6","Đáp án: 4",new String[]{},0,0);
 add(41,3,105,4,2,Kind.NUMBER,"Một phần tám của 24 quả dâu là bao nhiêu?","3","24 : 8","Đáp án: 3",new String[]{},0,0);
 add(41,3,105,5,1,Kind.CHOICE,"Dùng mỗi chữ số 0, 1, 2, 3 đúng một lần để lập phép nhân có tích 306.","102 × 3","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 102 × 3",new String[]{"102 × 3","103 × 2","120 × 3","301 × 2"},0,0);
}
private static void l42(){
 add(42,1,106,1,1,Kind.NUMBER,"527 − 346 + 74 = ?","255","Tính theo đúng thứ tự các phép tính.","527 − 346 + 74 = 255",new String[]{},0,0);
 add(42,1,106,1,2,Kind.NUMBER,"72 × 3 : 9 = ?","24","Tính theo đúng thứ tự các phép tính.","72 × 3 : 9 = 24",new String[]{},0,0);
 add(42,1,106,1,3,Kind.NUMBER,"28 + 45 − 60 = ?","13","Tính theo đúng thứ tự các phép tính.","28 + 45 − 60 = 13",new String[]{},0,0);
 add(42,1,106,1,4,Kind.NUMBER,"96 : 6 × 8 = ?","128","Tính theo đúng thứ tự các phép tính.","96 : 6 × 8 = 128",new String[]{},0,0);
 add(42,1,106,2,1,Kind.NUMBER,"24 × 3 − 52 = ?","20","Tính theo đúng thứ tự các phép tính.","24 × 3 − 52 = 20",new String[]{},0,0);
 add(42,1,106,2,2,Kind.NUMBER,"518 + 70 : 5 = ?","532","Tính theo đúng thứ tự các phép tính.","518 + 70 : 5 = 532",new String[]{},0,0);
 add(42,1,106,2,3,Kind.NUMBER,"91 : 7 + 69 = ?","82","Tính theo đúng thứ tự các phép tính.","91 : 7 + 69 = 82",new String[]{},0,0);
 add(42,1,106,2,4,Kind.NUMBER,"200 − 18 × 5 = ?","110","Tính theo đúng thứ tự các phép tính.","200 − 18 × 5 = 110",new String[]{},0,0);
 add(42,1,106,3,1,Kind.NUMBER,"7 lớp, mỗi lớp 31 học sinh, thêm 1 lớp 34 học sinh. Khối Ba có bao nhiêu học sinh?","251","31 × 7 + 34","Đáp án: 251",new String[]{},0,0);
 add(42,1,107,4,1,Kind.MULTI,"Chọn biểu thức lớn hơn 90.","1,2,4","Tính nhân chia trước.","Chọn: 70 + 80 : 2, 100 : 5 + 80, 30 + 40 × 2",new String[]{"20 × 3 + 30","70 + 80 : 2","100 : 5 + 80","20 × 5 − 20","30 + 40 × 2"},0,0);
 add(42,1,107,4,2,Kind.CHOICE,"Chọn biểu thức nhỏ nhất.","20 × 5 − 20","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 20 × 5 − 20",new String[]{"20 × 3 + 30","70 + 80 : 2","100 : 5 + 80","20 × 5 − 20","30 + 40 × 2"},0,0);
 add(42,1,107,5,1,Kind.MULTI,"Chọn hai cách điền dấu để 4 □ 4 □ 4 = 20.","0,1","Nhân trước cộng sau.","Chọn: 4 + 4 × 4, 4 × 4 + 4",new String[]{"4 + 4 × 4","4 × 4 + 4","4 + 4 + 4","4 × 4 × 4"},0,0);
 add(42,2,107,1,1,Kind.NUMBER,"75 − (68 − 23) = ?","30","Tính theo đúng thứ tự các phép tính.","75 − (68 − 23) = 30",new String[]{},0,0);
 add(42,2,107,1,2,Kind.NUMBER,"8 × (63 : 7) = ?","72","Tính theo đúng thứ tự các phép tính.","8 × (63 : 7) = 72",new String[]{},0,0);
 add(42,2,107,1,3,Kind.NUMBER,"67 + (56 + 44) = ?","167","Tính theo đúng thứ tự các phép tính.","67 + (56 + 44) = 167",new String[]{},0,0);
 add(42,2,107,1,4,Kind.NUMBER,"42 : (2 × 3) = ?","7","Tính theo đúng thứ tự các phép tính.","42 : (2 × 3) = 7",new String[]{},0,0);
 add(42,2,108,2,1,Kind.NUMBER,"79 + 57 + 43 = ?","179","Tính theo đúng thứ tự các phép tính.","79 + 57 + 43 = 179",new String[]{},0,0);
 add(42,2,108,2,2,Kind.NUMBER,"50 × 4 × 2 = ?","400","Tính theo đúng thứ tự các phép tính.","50 × 4 × 2 = 400",new String[]{},0,0);
 add(42,2,108,3,1,Kind.NUMBER,"5 × (35 − 25) = ?","50","Tính theo đúng thứ tự các phép tính.","5 × (35 − 25) = 50",new String[]{},0,0);
 add(42,2,108,3,2,Kind.NUMBER,"(54 + 36) : 3 = ?","30","Tính theo đúng thứ tự các phép tính.","(54 + 36) : 3 = 30",new String[]{},0,0);
 add(42,2,108,3,3,Kind.NUMBER,"(12 + 18) × 4 = ?","120","Tính theo đúng thứ tự các phép tính.","(12 + 18) × 4 = 120",new String[]{},0,0);
 add(42,2,108,3,4,Kind.NUMBER,"132 − 84 : 2 = ?","90","Tính theo đúng thứ tự các phép tính.","132 − 84 : 2 = 90",new String[]{},0,0);
 add(42,2,108,4,1,Kind.NUMBER,"280 bánh, mỗi hộp 8 bánh. Có bao nhiêu hộp?","35","280 : 8","Đáp án: 35",new String[]{},0,0);
 add(42,2,108,4,2,Kind.NUMBER,"35 hộp, mỗi thùng 5 hộp. Có bao nhiêu thùng?","7","35 : 5","Đáp án: 7",new String[]{},0,0);
 add(42,2,108,5,1,Kind.CHOICE,"Chọn dấu để 3 × (3 □ 3) lớn nhất.","×","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: ×",new String[]{"+","×",":"},0,0);
 add(42,2,108,5,2,Kind.CHOICE,"Chọn dấu để 3 × (3 □ 3) bé nhất.",":","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: :",new String[]{"+","×",":"},0,0);
}
private static void l43(){
 add(43,1,109,1,1,Kind.GEOMETRY,"Trong hình có bao nhiêu góc không vuông đỉnh B?","2","Đếm hai góc nhỏ tạo bởi BK và hai cạnh BA, BC.","Góc ABK và KBC: 2 góc.",new String[]{"1","2","3"},1,0);
 add(43,1,109,1,7,Kind.GEOMETRY,"Trong hình có bao nhiêu góc không vuông đỉnh A?","3","Xem từng cặp tia AB, AK, AE.","BAK, KAE và BAE: 3 góc.",new String[]{"1","2","3"},1,0);
 add(43,1,109,1,8,Kind.GEOMETRY,"Trong cả hình có bao nhiêu góc vuông?","11","Đếm tại B, K, I, H.","1 + 4 + 4 + 2 = 11 góc.",new String[]{"9","10","11","12"},1,0);
 add(43,1,109,1,2,Kind.GEOMETRY,"Trung điểm đoạn AC trong hình là điểm nào?","K","Trung điểm nằm giữa và cách đều hai đầu mút.","Trung điểm: K",new String[]{"K","I","H"},1,0);
 add(43,1,109,1,3,Kind.GEOMETRY,"Trung điểm đoạn ED trong hình là điểm nào?","I","Trung điểm nằm giữa và cách đều hai đầu mút.","Trung điểm: I",new String[]{"K","I","H"},1,0);
 add(43,1,109,1,4,Kind.GEOMETRY,"Trung điểm đoạn KH trong hình là điểm nào?","I","Trung điểm nằm giữa và cách đều hai đầu mút.","Trung điểm: I",new String[]{"K","I","H"},1,0);
 add(43,1,109,1,5,Kind.GEOMETRY,"Trung điểm đoạn BH trong hình là điểm nào?","K","Trung điểm nằm giữa và cách đều hai đầu mút.","Trung điểm: K",new String[]{"K","I","H"},1,0);
 add(43,1,109,1,6,Kind.GEOMETRY,"Trung điểm đoạn MN trong hình là điểm nào?","H","Trung điểm nằm giữa và cách đều hai đầu mút.","Trung điểm: H",new String[]{"K","I","H"},1,0);
 add(43,1,109,2,1,Kind.DRAW,"Vẽ lại hình tam giác theo mẫu trên lưới.","done","Chạm lần lượt ba đỉnh, rồi chạm lại đỉnh đầu để khép kín.","Tam giác có đáy 6 ô, cao 4 ô; đỉnh trên cách đầu trái 2 ô.",new String[]{},0,0);
 add(43,1,109,2,2,Kind.DRAW,"Vẽ lại hình vuông theo mẫu trên lưới.","done","Đếm bốn ô cho mỗi cạnh, vẽ bốn đỉnh và khép kín.","Hình vuông có mỗi cạnh dài 4 ô.",new String[]{},1,0);
 add(43,1,109,2,3,Kind.DRAW,"Vẽ lại hình chữ nhật theo mẫu trên lưới.","done","Đếm 3 ô ngang và 4 ô dọc.","Hình chữ nhật rộng 3 ô và cao 4 ô.",new String[]{},2,0);
 add(43,1,110,3,1,Kind.MULTI,"Quan sát hình tròn tâm O. Chọn các đường kính.","0,1","Đường kính đi qua tâm, hai đầu nằm trên đường tròn.","AB và CD.",new String[]{"AB","CD","AC","OE"},0,0);
 add(43,1,110,3,2,Kind.MULTI,"Quan sát hình tròn tâm O. Chọn các bán kính.","0,1,2,3,4","Bán kính nối tâm với một điểm trên đường tròn.","OA, OB, OC, OD và OE.",new String[]{"OA","OB","OC","OD","OE","AB"},0,0);
 add(43,1,110,3,3,Kind.NUMBER,"Đường kính AB gấp bán kính OB mấy lần?","2","Đường kính bằng hai bán kính.","Đáp án: 2",new String[]{},0,0);
 add(43,1,110,4,1,Kind.NUMBER,"Quan sát công trình. Có bao nhiêu khối lập phương nhỏ?","20","Đếm lớp đế 4 × 4 và bốn khối ở góc.","16 + 4 = 20 khối lập phương nhỏ.",new String[]{},0,0);
 add(43,1,110,4,2,Kind.NUMBER,"Quan sát công trình. Có bao nhiêu khối trụ?","2","Đếm khối trụ to và khối trụ nhỏ phía trên.","2 khối trụ.",new String[]{},0,0);
 add(43,1,110,4,3,Kind.NUMBER,"Quan sát công trình. Có bao nhiêu khối cầu?","1","Nhìn ở đỉnh công trình.","1 khối cầu.",new String[]{},0,0);
 add(43,1,110,5,1,Kind.NUMBER,"Khối hộp ghép 4 × 2 × 2 khối lập phương, sơn mọi mặt ngoài. Có bao nhiêu khối được sơn 3 mặt?","8","Các khối góc được sơn 3 mặt.","Đáp án: 8",new String[]{},0,0);
 add(43,1,110,5,2,Kind.NUMBER,"Có bao nhiêu khối được sơn đúng 2 mặt?","8","16 − 8 = 8","Đáp án: 8",new String[]{},0,0);
 add(43,2,111,1,1,Kind.NUMBER,"Đường gấp khúc gồm 3 đoạn, mỗi đoạn 35 mm. Tổng chiều dài?","105","35 × 3","Đáp án: 105",new String[]{},0,0);
 add(43,2,111,1,2,Kind.NUMBER,"Hai quả cân 500 g cân bằng 3 quả xoài và quả cân 200 g. Ba quả xoài nặng bao nhiêu gam?","800","500 + 500 − 200","Đáp án: 800",new String[]{},0,0);
 add(43,2,111,2,1,Kind.CHOICE,"Hộp bút dày khoảng bao nhiêu?","15 mm","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 15 mm",new String[]{"15 cm","15 mm","15 dm"},0,0);
 add(43,2,111,2,2,Kind.CHOICE,"Bút bi nặng khoảng bao nhiêu?","8 g","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 8 g",new String[]{"8 g","80 g","8 kg"},0,0);
 add(43,2,111,2,3,Kind.CHOICE,"Một bát nước đầy khoảng bao nhiêu?","200 ml","Đọc kỹ đề, tìm phép tính hoặc quan hệ phù hợp.","Đáp án: 200 ml",new String[]{"2 l","20 ml","200 ml"},0,0);
 add(43,2,111,2,4,Kind.CHOICE,"Trong các mức nhiệt sau, mức nào thích hợp để uống nước?","25 °C","Không uống nước quá nóng.","Đáp án: 25 °C",new String[]{"25 °C","70 °C","100 °C"},0,0);
 add(43,2,112,3,1,Kind.NUMBER,"540 + 260 = ? mm","800","Tính theo đúng thứ tự các phép tính.","540 + 260 = 800 mm",new String[]{},0,0);
 add(43,2,112,3,2,Kind.NUMBER,"732 − 32 = ? mm","700","Tính theo đúng thứ tự các phép tính.","732 − 32 = 700 mm",new String[]{},0,0);
 add(43,2,112,3,3,Kind.NUMBER,"180 × 4 = ? mm","720","Tính theo đúng thứ tự các phép tính.","180 × 4 = 720 mm",new String[]{},0,0);
 add(43,2,112,3,4,Kind.NUMBER,"720 : 8 = ? mm","90","Tính theo đúng thứ tự các phép tính.","720 : 8 = 90 mm",new String[]{},0,0);
 add(43,2,112,3,5,Kind.NUMBER,"327 + 400 = ? ml","727","Tính theo đúng thứ tự các phép tính.","327 + 400 = 727 ml",new String[]{},0,0);
 add(43,2,112,3,6,Kind.NUMBER,"1000 − 300 = ? ml","700","Tính theo đúng thứ tự các phép tính.","1000 − 300 = 700 ml",new String[]{},0,0);
 add(43,2,112,3,7,Kind.NUMBER,"150 × 6 = ? ml","900","Tính theo đúng thứ tự các phép tính.","150 × 6 = 900 ml",new String[]{},0,0);
 add(43,2,112,3,8,Kind.NUMBER,"700 : 7 = ? ml","100","Tính theo đúng thứ tự các phép tính.","700 : 7 = 100 ml",new String[]{},0,0);
 add(43,2,112,3,9,Kind.NUMBER,"270 + 538 = ? g","808","Tính theo đúng thứ tự các phép tính.","270 + 538 = 808 g",new String[]{},0,0);
 add(43,2,112,3,10,Kind.NUMBER,"730 − 430 = ? g","300","Tính theo đúng thứ tự các phép tính.","730 − 430 = 300 g",new String[]{},0,0);
 add(43,2,112,3,11,Kind.NUMBER,"240 × 3 = ? g","720","Tính theo đúng thứ tự các phép tính.","240 × 3 = 720 g",new String[]{},0,0);
 add(43,2,112,3,12,Kind.NUMBER,"960 : 6 = ? g","160","Tính theo đúng thứ tự các phép tính.","960 : 6 = 160 g",new String[]{},0,0);
 add(43,2,112,4,1,Kind.NUMBER,"5 gói mì 75 g mỗi gói, 1 hộp ngũ cốc 500 g. Tổng nặng bao nhiêu gam?","875","75 × 5 + 500","Đáp án: 875",new String[]{},0,0);
 add(43,2,112,5,1,Kind.CHOICE,"Cân hai đĩa, có quả cân 5 kg và 2 kg. Lấy 3 kg gạo thế nào?","5 kg một đĩa; 2 kg và gạo ở đĩa kia","5 − 2 = 3 kg.","Đáp án: 5 kg một đĩa; 2 kg và gạo ở đĩa kia",new String[]{"5 kg một đĩa; 2 kg và gạo ở đĩa kia","Hai quả cân cùng đĩa","Chỉ dùng quả cân 2 kg"},0,0);
}
private static void l44(){
 add(44,1,113,1,1,Kind.MULTIPLY,"132 × 4","528","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","132 × 4 = 528",new String[]{},132,4);
 add(44,1,113,1,2,Kind.MULTIPLY,"209 × 4","836","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","209 × 4 = 836",new String[]{},209,4);
 add(44,1,113,1,3,Kind.MULTIPLY,"113 × 6","678","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","113 × 6 = 678",new String[]{},113,6);
 add(44,1,113,2,1,Kind.DIVISION,"75 : 5","15,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","75 = 5 × 15; thương 15, dư 0.",new String[]{},75,5);
 add(44,1,113,2,2,Kind.DIVISION,"68 : 4","17,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","68 = 4 × 17; thương 17, dư 0.",new String[]{},68,4);
 add(44,1,113,2,3,Kind.DIVISION,"96 : 6","16,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","96 = 6 × 16; thương 16, dư 0.",new String[]{},96,6);
 add(44,1,113,3,1,Kind.MULTI,"Quan sát hình chữ nhật ABCD và hình tròn tâm O. Chọn đoạn có trung điểm O.","1,2","Hai đường chéo cắt nhau tại trung điểm.","AC và BD.",new String[]{"AB","AC","BD","CD"},0,0);
 add(44,1,113,3,2,Kind.MULTI,"Quan sát hình. Chọn các góc vuông.","0,1,2,3","Bốn góc ở bốn đỉnh hình chữ nhật.","DAB, ABC, BCD, CDA.",new String[]{"DAB","ABC","BCD","CDA","AOB","BOC"},0,0);
 add(44,1,113,3,3,Kind.MULTI,"Quan sát hình. Chọn các góc không vuông chung đỉnh O.","0,1,2,3","Hai đường chéo không vuông góc; xét bốn góc tạo thành quanh O.","AOB, BOC, COD, DOA.",new String[]{"AOB","BOC","COD","DOA","ABC","BCD"},0,0);
 add(44,1,114,4,1,Kind.NUMBER,"216 + 148 − 144 = ?","220","Tính theo đúng thứ tự các phép tính.","216 + 148 − 144 = 220",new String[]{},0,0);
 add(44,1,114,4,2,Kind.NUMBER,"216 × (148 − 144) = ?","864","Tính theo đúng thứ tự các phép tính.","216 × (148 − 144) = 864",new String[]{},0,0);
 add(44,1,114,5,1,Kind.NUMBER,"Thùng 120 l nước mắm, lấy 7 can, mỗi can 10 l. Còn bao nhiêu lít?","50","120 − 7 × 10","Đáp án: 50",new String[]{},0,0);
 add(44,2,114,1,1,Kind.MULTIPLY,"217 × 4","868","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","217 × 4 = 868",new String[]{},217,4);
 add(44,2,114,1,2,Kind.MULTIPLY,"309 × 3","927","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","309 × 3 = 927",new String[]{},309,3);
 add(44,2,114,1,3,Kind.MULTIPLY,"160 × 5","800","Nhân từ hàng đơn vị, nhớ sang hàng tiếp theo.","160 × 5 = 800",new String[]{},160,5);
 add(44,2,114,2,1,Kind.DIVISION,"576 : 3","192,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","576 = 3 × 192; thương 192, dư 0.",new String[]{},576,3);
 add(44,2,114,2,2,Kind.DIVISION,"645 : 5","129,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","645 = 5 × 129; thương 129, dư 0.",new String[]{},645,5);
 add(44,2,114,2,3,Kind.DIVISION,"847 : 7","121,0","Chia từ trái sang phải; số dư nhỏ hơn số chia.","847 = 7 × 121; thương 121, dư 0.",new String[]{},847,7);
 add(44,2,115,3,1,Kind.NUMBER,"Đường gấp khúc gồm 34 mm, 18 mm và 45 mm. Tổng độ dài?","97","34 + 18 + 45","Đáp án: 97",new String[]{},0,0);
 add(44,2,115,3,2,Kind.NUMBER,"Túi đường cân bằng 100 g, 200 g và 500 g. Túi nặng bao nhiêu gam?","800","100 + 200 + 500","Đáp án: 800",new String[]{},0,0);
 add(44,2,115,3,3,Kind.CUPS,"Rót hết nước trong bình được ba ca như hình. Ban đầu bình có bao nhiêu ml?","600","Đọc mỗi ca rồi cộng ba lượng nước.","200 + 200 + 200 = 600 ml.",new String[]{},200,200);
 add(44,2,115,4,1,Kind.NUMBER,"32 × 6 : 3 = ?","64","Tính theo đúng thứ tự các phép tính.","32 × 6 : 3 = 64",new String[]{},0,0);
 add(44,2,115,4,2,Kind.NUMBER,"32 × (6 − 3) = ?","96","Tính theo đúng thứ tự các phép tính.","32 × (6 − 3) = 96",new String[]{},0,0);
 add(44,2,115,5,1,Kind.NUMBER,"Ngày đầu trồng 235 cây; ngày sau nhiều hơn 80 cây. Ngày sau trồng bao nhiêu cây?","315","235 + 80","Đáp án: 315",new String[]{},0,0);
 add(44,2,115,5,2,Kind.NUMBER,"Ngày đầu 235 cây, ngày sau 315 cây. Cả hai ngày trồng bao nhiêu cây?","550","235 + 315","Đáp án: 550",new String[]{},0,0);
}
}
