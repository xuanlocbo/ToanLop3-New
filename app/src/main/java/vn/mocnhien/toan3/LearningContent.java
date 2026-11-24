package vn.mocnhien.toan3;
import java.util.*;
/** Data layer only; old activity and progress keys remain untouched until integration. */
public final class LearningContent {
 public static final int FIRST=26,LAST=44, BONUS_STARS=100, NORMAL_STARS=10;
 public static final int[] SGK_PAGES={75,79,81,83,85,87,89,91,93,95,97,99,104,109,111,113,116,118,120,122};
 public static final int[] VBT_PAGES={66,69,71,74,76,78,80,81,82,85,88,90,94,98,100,102,106,109,113,116};
 public enum Mode {SGK,VBT,GAME,CHALLENGE}
 public static final String[][] LESSONS={{"Chia số có hai chữ số cho số có một chữ số","Chia lần lượt từ hàng chục rồi đến hàng đơn vị. Số dư nhỏ hơn số chia.","74 : 3 = 24 dư 2; 24 × 3 + 2 = 74.","Bến thuyền chia đều","Chia hàng hóa vào thuyền; cổng mở khi chọn thương và số dư đúng."},
{"Giảm một số đi một số lần","Muốn giảm một số đi một số lần, chia số đó cho số lần. Gấp dùng nhân, bớt dùng trừ.","36 giảm 4 lần: 36 : 4 = 9.","Rừng thu nhỏ","Đi qua máy thu nhỏ đúng số lần, tránh nhầm giảm số đơn vị."},
{"Bài toán giải bằng hai bước tính","Tìm dữ kiện còn thiếu ở bước một; dùng kết quả đó để trả lời câu hỏi ở bước hai.","8 hoa đỏ; hoa vàng nhiều hơn 4: 8 + 4 = 12; cả hai: 8 + 12 = 20.","Cầu hai nhịp","Mỗi phép tính đúng dựng một nhịp cầu."},
{"Luyện tập chung","Ôn nhân, chia, gấp, giảm và giải toán hai bước.","73 : 4 = 18 dư 1; 18 × 4 + 1 = 73.","Đường hầm phép tính","Chọn đường đi và kiểm tra số hàng hóa còn lại."},
{"Mi-li-mét","1 cm = 10 mm; 1 m = 1000 mm. Đặt vạch 0 trùng đầu vật cần đo.","6 cm = 60 mm; 60 − 17 = 43 mm.","Cầu thước thần kỳ","Đo, chọn đúng tấm ván theo chiều dài."},
{"Gam","Gam là đơn vị đo khối lượng. 1 kg = 1000 g. Cân thăng bằng khi hai bên bằng nhau.","200 g + 50 g = 250 g.","Chợ cân hoa quả","Đặt quả cân để cân bằng các giỏ hàng."},
{"Mi-li-lít","Mi-li-lít là đơn vị đo dung tích. 1 l = 1000 ml. Đọc mực chất lỏng theo vạch ca.","1 l − 300 ml = 700 ml.","Xưởng nước cầu vồng","Đổ đầy, rót và đong đúng lượng nước."},
{"Nhiệt độ. Đơn vị đo nhiệt độ","Đơn vị thường dùng là độ C (°C). Đọc vạch tại đỉnh cột màu của nhiệt kế.","30°C cao hơn 18°C; chênh 12°C.","Đỉnh núi nhiệt kế","Ghép nhiệt kế với vùng nóng hoặc lạnh."},
{"Thực hành và trải nghiệm với các đơn vị mi-li-mét, gam, mi-li-lít, độ C","Chọn đúng dụng cụ; thực hành với vật thật khi đề yêu cầu. Đổi về cùng đơn vị khi tính.","500 g + 200 g + 200 g + 100 g = 1000 g.","Phòng thí nghiệm nhỏ","Chọn thước, cân, ca đong hoặc nhiệt kế và thực hiện nhiệm vụ."},
{"Luyện tập chung","Ôn mm, g, ml và °C; tính cùng đơn vị.","Ca 400 ml đầy, rót đầy ca 150 ml, còn 250 ml.","Đảo đo lường","Vượt các trạm đo lường khác nhau."},
{"Nhân số có ba chữ số với số có một chữ số","Nhân từ hàng đơn vị, hàng chục, hàng trăm; nhớ nếu cần.","215 × 4: 4 × 5 = 20 (viết 0 nhớ 2); 4 × 1 + 2 = 6; 4 × 2 = 8; kết quả 860.","Xưởng nhân sao","Ghép các nhóm vật phẩm, mở khóa bằng tích."},
{"Chia số có ba chữ số cho số có một chữ số","Chia từ hàng cao nhất rồi hạ từng chữ số. Không bỏ chữ số 0 ở giữa hoặc cuối thương.","714 : 7 = 102; 102 × 7 = 714.","Mỏ pha lê chia đều","Chia pha lê vào các rương, xác định phần dư."},
{"Biểu thức số. Tính giá trị của biểu thức số","Trong ngoặc trước; nhân chia trước cộng trừ; cùng mức ưu tiên làm từ trái sang phải.","24 + 8 : 2 = 28; (24 + 8) : 2 = 16.","Cổng biểu thức","Chọn thứ tự phép tính để mở từng cổng."},
{"So sánh số lớn gấp mấy lần số bé","Muốn biết số lớn gấp mấy lần số bé, lấy số lớn chia số bé. Hơn bao nhiêu dùng trừ.","24 : 6 = 4 lần; 24 − 6 = 18 đơn vị.","Vườn khổng lồ","So sánh độ dài và số lượng của các vật."},
{"Luyện tập chung","Ôn nhân chia trong 1000, biểu thức và so sánh số lần.","6 × 2 × 5 = 6 × 10 = 60.","Đường mây thử tài","Ghép đường đi bằng nhiều loại phép tính."},
{"Ôn tập phép nhân, phép chia trong phạm vi 100, 1 000","Ôn đặt tính nhân chia, thành phần chưa biết, phép chia có dư và một phần mấy.","Một phần năm của 15 là 15 : 5 = 3.","Đấu trường nhân chia","Tìm số còn thiếu, phân chia và thu thập vật phẩm."},
{"Ôn tập biểu thức số","Trong ngoặc trước, nhân chia trước; nhóm số để tính thuận tiện.","27 + 34 + 66 = 27 + 100 = 127.","Mê cung biểu thức","Tính theo đúng thứ tự để chọn đường."},
{"Ôn tập hình học và đo lường","Trung điểm nằm giữa và cách đều hai đầu. Đường kính gấp đôi bán kính. Hình vuông có 4 cạnh bằng nhau và 4 góc vuông.","Bán kính 4 cm thì đường kính 8 cm.","Lâu đài hình khối","Vẽ trên lưới, nhận biết góc, hình và khối."},
{"Ôn tập chung","Ôn toàn bộ nhân chia, biểu thức, hình học, đo lường và toán có lời văn.","100 l + 5 × 10 l = 150 l.","Kho báu học kỳ","Vượt nhiệm vụ tổng hợp, mở kho báu cuối."}};
 public static int zone(int l){validate(l);return l<=29?0:l<=35?1:l<=40?2:3;}
 private static void validate(int l){if(l<FIRST||l>LAST)throw new IllegalArgumentException("lesson "+l);}
 public static String title(int l){validate(l);return LESSONS[l-FIRST][0];}
 public static String rule(int l){validate(l);return LESSONS[l-FIRST][1];}
 public static String example(int l){validate(l);return LESSONS[l-FIRST][2];}
 public static String gameTitle(int l){validate(l);return LESSONS[l-FIRST][3];}
 public static String gameObjective(int l){validate(l);return LESSONS[l-FIRST][4];}
 public static List<BookBank.Q> questions(int l, Mode mode){
  validate(l);
  switch(mode){
   case SGK:return Textbook.lesson(l);
   case VBT:return BookBank.lesson(l);
   case CHALLENGE:return ChallengeBank.lesson(l);
   default:return gameQuestions(l);
  }
 }
 private static List<BookBank.Q> gameQuestions(int l){
  ArrayList<BookBank.Q> candidates=new ArrayList<>();
  candidates.addAll(Textbook.lesson(l));candidates.addAll(BookBank.lesson(l));
  ArrayList<BookBank.Q> interactive=new ArrayList<>(),regular=new ArrayList<>();
  for(BookBank.Q q:candidates)switch(q.kind){
   case PRACTICE:break;
   case RULER:case SCALE:case BALANCE:case CUP:case THERMO:case CUPS:case CLINICAL:case GEOMETRY:case POUR:case DRAW:interactive.add(q);break;
   default:regular.add(q);
  }
  Collections.shuffle(interactive,new Random(9029L+l));Collections.shuffle(regular,new Random(4261L+l));
  ArrayList<BookBank.Q> selected=new ArrayList<>(interactive.subList(0,Math.min(3,interactive.size())));
  for(BookBank.Q q:regular){if(selected.size()==8)break;selected.add(q);}
  return Collections.unmodifiableList(selected);
 }
 /** Numeric answers accept leading zeroes and surrounding whitespace.
  * Multiple selections are set-based. Physical tasks and drawing require a separate validator. */
 public static boolean check(BookBank.Q q,String input){
  if(input==null)return false;String s=input.trim();
  switch(q.kind){
   case DRAW:case PRACTICE: return false;
   case CHOICE:case GEOMETRY: return q.answer.equalsIgnoreCase(s);
   case MULTI:try{
    Set<Integer> got=new HashSet<>(),want=new HashSet<>();
    for(String v:s.split(",",-1)){int i=Integer.parseInt(v.trim());if(i<0||i>=q.options.length||!got.add(i))return false;}
    for(String v:q.answer.split(","))want.add(Integer.parseInt(v.trim()));
    return got.equals(want);
   }catch(NumberFormatException e){return false;}
   default:try{
    String[] a=s.split(",",-1),b=q.answer.split(",",-1);if(a.length!=b.length)return false;
    for(int i=0;i<a.length;i++)if(Integer.parseInt(a[i].trim())!=Integer.parseInt(b[i].trim()))return false;
    return true;
   }catch(NumberFormatException e){return false;}
  }
 }
}
