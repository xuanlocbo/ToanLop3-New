package vn.mocnhien.toan3;
import org.json.*;
import java.util.*;
import java.util.regex.*;

/** End-of-term review; supplementary guidance keeps all approved question IDs and solutions. */
final class Part05Tutor {
    private Part05Tutor(){}
    static boolean applies(int l){return l>=41&&l<=44;}
    static boolean applies(JSONObject q){return applies(q.optInt("lesson"))&&("SGK".equals(q.optString("book"))||"VBT".equals(q.optString("book")));}
    static String hint(JSONObject q){
        String old=Part04Tutor.hint(q);if(!applies(q))return old;
        String type=Json.obj(q,"visual").optString("type"),kind=q.optString("kind"),extra="";
        switch(type){
            case "long_multiplication":case "worked_multiplication":case "vertical_digit_fill":extra="Xếp đúng hàng. Nhân từ hàng đơn vị, cộng số nhớ của lượt trước vào lượt sau. Thử lại toàn bộ phép tính.";break;
            case "long_division":case "worked_division":extra="Chia → nhân → trừ → hạ. Giữ chữ số 0 sau khi đã bắt đầu viết thương; thử lại bằng thương × số chia + số dư.";break;
            case "capacity_grouping":extra="Chia tổng lượng cho sức chứa mỗi can. Nếu còn dư, cần thêm một can để chứa hết.";break;
            case "missing_factor":extra="Lấy tích chia cho thừa số đã biết.";break;
            case "missing_dividend":extra="Lấy thương nhân với số chia nếu phép chia hết.";break;
            case "missing_divisor":extra="Lấy số bị chia chia cho thương nếu phép chia hết.";break;
            case "expression":case "expression_match":case "expression_compare":case "operator_fill":case "nested_groups":extra="Tính trong ngoặc trước; nhân, chia trước cộng, trừ; cùng mức tính từ trái sang phải. Nếu đề yêu cầu nhiều cách, kiểm tra đủ các cách.";break;
            case "geometry_grid":case "geometry_circle_grid":
                extra="point_select".equals(kind)?"Trung điểm phải nằm trên đoạn thẳng và cách đều hai đầu. Đếm khoảng ô lưới, không đếm số chấm.":q.optString("prompt").contains("trung điểm")?"Chọn đoạn thẳng đi qua điểm đã cho; hai phần của đoạn phải bằng nhau.":"Xét từng đỉnh. Dùng góc vuông của ê ke để đối chiếu; mỗi cặp hai tia khác hướng tạo một góc cần xét. Không đếm hai tia đối nhau thành góc không vuông trong bài này.";break;
            case "grid_draw":extra="Đếm ô theo chiều ngang và chiều dọc từ một đỉnh. Đặt các đỉnh đúng vị trí tương đối, nối theo đường bao rồi khép kín.";break;
            case "circle_diagram":extra="Bán kính nối tâm với một điểm trên đường tròn; đường kính đi qua tâm và có hai đầu trên đường tròn. Chỉ chọn đoạn đã vẽ.";break;
            case "solid_stack":extra="Đếm theo từng hàng, từng lớp và các khối đặt thêm. Không bỏ sót khối bị che; phân biệt lập phương, trụ và cầu.";break;
            case "painted_cuboid":extra="Xét từng vị trí: khối ở góc có ba mặt ngoài; khối nằm giữa một cạnh có hai mặt ngoài (với các kích thước đều từ 2 khối trở lên).";break;
            case "polyline":extra="Cộng độ dài của tất cả các đoạn, cùng một đơn vị.";break;
            case "balance":case "balance_select":extra="Khi cân thăng bằng, tổng khối lượng hai bên bằng nhau. Đổi về cùng đơn vị trước khi tính.";break;
            case "cups":extra="Đọc đúng vạch chia, rồi cộng hoặc trừ các số đo cùng đơn vị.";break;
            case "bar_compare":case "bar_multiple":extra="Phân biệt hơn bao nhiêu (trừ) và gấp mấy lần (chia). Đọc đúng câu hỏi.";break;
            default:
                if(q.optInt("lesson")==42&&"number".equals(kind))extra="Xác định thứ tự phép tính, viết kết quả trung gian rồi tính tiếp.";
        }
        return extra.isEmpty()?old:(old.isEmpty()?extra:old+"\n"+extra);
    }
    static String solution(JSONObject q){
        String old=Part04Tutor.solution(q);if(!applies(q))return old;
        JSONObject v=Json.obj(q,"visual");String type=v.optString("type");List<String> lines=new ArrayList<>();
        Object expected=q.opt("answer");
        if("long_multiplication".equals(type)){
            int n=v.optInt("multiplicand",-1),b=v.optInt("multiplier",-1);
            if(n>=0&&n<=999&&b>=1&&b<=9&&Json.same(expected,n*b)){
                List<Part04Tutor.Mul> rows=Part04Tutor.multiply(n,b);
                for(int i=0;i<rows.size();i++)lines.add(Part04Tutor.mulLine(rows.get(i),b,i,rows.size()));
                lines.add(n+" × "+b+" = "+n*b+".");
            }
        }else if("long_division".equals(type)){
            int n=v.optInt("dividend",-1),b=v.optInt("divisor",-1);
            if(n>=0&&n<=999&&b>=1&&b<=9&&expected instanceof JSONObject){
                JSONObject key=(JSONObject)expected;
                if(key.optInt("quotient",-1)==n/b&&key.optInt("remainder",-1)==n%b){
                    List<Part04Tutor.Div> rows=Part04Tutor.divide(n,b);
                    for(int i=0;i<rows.size();i++)lines.add(Part04Tutor.divLine(rows.get(i),b,i));
                    lines.add(n+" : "+b+" = "+n/b+(n%b==0?"":" dư "+n%b)+". Thử lại: "+n/b+" × "+b+(n%b==0?"":" + "+n%b)+" = "+n+".");
                }
            }
        }else if("expression".equals(type)||("number".equals(q.optString("kind"))&&q.optInt("lesson")==42)){
            String expression=v.optString("expression");
            if(expression.isEmpty()){
                Matcher m=Pattern.compile("^\\s*([0-9() +−×:*/-]+)\\s*=\\s*\\?\\s*$").matcher(q.optString("prompt"));
                if(m.matches())expression=m.group(1);
            }
            Part04Tutor.Expression trace=Part04Tutor.expression(expression);
            if(trace!=null&&Json.same(expected,trace.value)&&trace.lines.size()>1)lines.add(String.join(" = ",trace.lines));
        }else if("capacity_grouping".equals(type)){
            int n=v.optInt("total"),b=v.optInt("capacity");
            if(n>0&&b>0&&Json.same(expected,(n+b-1)/b)){
                lines.add(n+" : "+b+" = "+n/b+(n%b==0?"":" dư "+n%b)+".");
                lines.add(n%b==0?"Các can vừa đủ, không cần can thêm.":"Còn dư "+n%b+" "+v.optString("unit")+", nên cần thêm một can.");
            }
        }else if("geometry_grid".equals(type)&&"point_select".equals(q.optString("kind"))){
            JSONArray s=Json.arr(v,"highlightSegment");String a=s.optString(0),b=s.optString(1),p=Json.str(expected);
            if(Part05Geometry.midpoint(v,a,b,p))lines.add(p+" nằm giữa "+a+" và "+b+"; "+a+p+" = "+p+b+". Vì vậy "+p+" là trung điểm của "+a+b+".");
        }else if("painted_cuboid".equals(type)){
            int faces=q.optString("prompt").contains("2 mặt")?2:3;
            int calculated=Part05Geometry.paintedCount(Json.arr(v,"dimensions"),faces);
            if(Json.same(expected,calculated))lines.add("Đếm các khối có đúng "+faces+" mặt ngoài: "+calculated+" khối.");
        }else if("polyline".equals(type)){
            List<String> values=new ArrayList<>();int sum=0;boolean valid=true;
            for(Object raw:Json.list(Json.arr(v,"segments"))){JSONObject seg=(JSONObject)raw;if(!seg.has("length")){valid=false;break;}int n=seg.optInt("length");values.add(""+n);sum+=n;}
            if(valid&&!values.isEmpty()&&Json.same(expected,sum))lines.add(String.join(" + ",values)+" = "+sum+" ("+v.optString("unit")+").");
        }
        return lines.isEmpty()?old:old+"\n\nÔn lại cách giải:\n"+String.join("\n",lines);
    }
    static final class Guide {
        final String name,rule,example,visual;final String[] steps;
        Guide(String n,String r,String e,String v,String...s){name=n;rule=r;example=e;visual=v;steps=s;}
    }
    private static String graph(String points,String segments){return "{\"type\":\"geometry_grid\",\"points\":"+points+",\"segments\":"+segments+"}";}
    static Guide[] guides(int l){
        switch(l){
            case 41:return new Guide[]{
                new Guide("Nhẩm theo hàng chục","Nhân, chia số tròn chục theo bảng đã học.","30 × 3 và 90 : 3","",
                    "3 chục × 3 = 9 chục.","9 chục là 90.","Kiểm tra ngược: 90 : 3 = 30."),
                new Guide("Kiểm tra phép chia có dư","Thương × số chia + số dư = số bị chia.","527 : 5","",
                    "527 : 5 = 105 dư 2; giữ chữ số 0 ở thương.","105 × 5 + 2 = 525 + 2 = 527.","Số dư 2 nhỏ hơn 5; phép chia đúng."),
                new Guide("Điền chữ số còn thiếu","Đặt tính theo hàng, thử chữ số rồi kiểm tra tích.","2□3 × 3 = 6□9","",
                    "3 × 3 = 9 ở hàng đơn vị, không nhớ.","Ở hàng chục, thử từng chữ số sao cho hàng trăm vẫn là 6; đề này có nhiều nghiệm.","Ví dụ: 213 × 3 = 639. Không khẳng định chỉ có một cách.")};
            case 42:return new Guide[]{
                new Guide("Ôn thứ tự tính","Ngoặc trước; nhân chia trước cộng trừ.","160 − 8 × 7","",
                    "Tính 8 × 7 = 56.","Thay vào biểu thức: 160 − 56.","Kết quả 104."),
                new Guide("Tính thuận tiện","Nhóm số hạng để được số tròn trăm.","143 + 36 + 64","",
                    "36 + 64 = 100.","143 + (36 + 64) = 143 + 100.","Kết quả 243."),
                new Guide("Tìm đủ cách điền dấu","Thử các tổ hợp dấu được phép; tính đúng thứ tự.","3 □ 3 □ 3 = 12; dùng + hoặc ×","",
                    "3 + 3 × 3 = 3 + 9 = 12.","3 × 3 + 3 = 9 + 3 = 12.","Cả hai cách đúng. Hai dấu + cho 9; hai dấu × cho 27.")};
            case 43:return new Guide[]{
                new Guide("Nhận ra góc vuông","Góc vuông giống góc của ê ke.","Xét hai cạnh OA và OB.",graph("{\"O\":[0,3],\"A\":[0,0],\"B\":[4,3]}","[[\"O\",\"A\"],[\"O\",\"B\"]]"),
                    "Tìm đỉnh chung O.","Cạnh OA thẳng đứng, OB nằm ngang.","Hai cạnh vuông góc; AOB là góc vuông."),
                new Guide("Tìm trung điểm","Điểm phải nằm giữa hai đầu và cách đều hai đầu.","Đoạn PQ dài 6 ô.",graph("{\"P\":[0,2],\"M\":[3,2],\"Q\":[6,2]}","[[\"P\",\"M\"],[\"M\",\"Q\"]]"),
                    "Quan sát M nằm trên đoạn PQ.","PM dài 3 ô; MQ dài 3 ô.","M nằm giữa và PM = MQ; M là trung điểm."),
                new Guide("Bán kính và đường kính","Đường kính đi qua tâm; bán kính nối tâm với đường tròn.","Hình tròn tâm O.","{\"type\":\"circle_diagram\",\"center\":\"O\",\"pointAnglesDeg\":{\"A\":180,\"B\":0,\"C\":-90},\"segments\":[[\"A\",\"O\"],[\"O\",\"B\"],[\"O\",\"C\"]]}",
                    "OA, OB và OC nối tâm O tới đường tròn.","AB đi qua O, hai đầu nằm trên đường tròn.","AB là đường kính; AB = 2 × OA."),
                new Guide("Vẽ trên lưới","Giữ đúng kích thước theo số ô, không theo kích thước ảnh.","Hình chữ nhật rộng 5 ô, cao 2 ô.",graph("{\"A\":[0,0],\"B\":[5,0],\"C\":[5,2],\"D\":[0,2]}","[[\"A\",\"B\"],[\"B\",\"C\"],[\"C\",\"D\"],[\"D\",\"A\"]]"),
                    "Từ đỉnh A, sang phải 5 ô đến B.","Từ B xuống 2 ô đến C; sang trái 5 ô đến D.","Nối D về A; hình khép kín, đúng 5 × 2 ô."),
                new Guide("Đếm khối theo lớp","Đếm số khối một lớp, rồi nhân với số lớp.","Đế 3 × 3 khối, thêm một khối trụ.","{\"type\":\"solid_stack\",\"base\":{\"shape\":\"cube\",\"dimensions\":[3,3,1]},\"above\":[{\"shape\":\"cylinder\",\"size\":\"wide\"}]}",
                    "Đế có 3 hàng, mỗi hàng 3 khối.","3 × 3 = 9 khối lập phương.","Có thêm 1 khối trụ; không cộng trụ vào số khối lập phương."),
                new Guide("Khối nhỏ được sơn mặt ngoài","Với khối hộp mỗi chiều ít nhất 2, các khối góc có đúng 3 mặt ngoài.","Khối hộp 3 × 3 × 2.","{\"type\":\"painted_cuboid\",\"dimensions\":[3,3,2],\"paintOuterFaces\":true}",
                    "Đếm 4 góc ở lớp trên.","Đếm 4 góc ở lớp dưới.","4 + 4 = 8 khối nhỏ có đúng 3 mặt ngoài.")};
            case 44:return new Guide[]{
                new Guide("Ôn bài toán hai bước","Tìm số chưa biết trước, rồi trả lời câu hỏi cuối.","An có 8 nhãn, Bình có gấp 3 lần. Hỏi cả hai có bao nhiêu?","",
                    "Số nhãn của Bình: 8 × 3 = 24.","Tổng: 8 + 24 = 32.","Đáp số: 32 nhãn."),
                new Guide("Độ dài đường gấp khúc","Cộng tất cả các đoạn đã cùng đơn vị.","PQ = 25 mm, QR = 30 mm, RS = 45 mm.","{\"type\":\"polyline\",\"segments\":[{\"name\":\"PQ\",\"length\":25},{\"name\":\"QR\",\"length\":30},{\"name\":\"RS\",\"length\":45}],\"unit\":\"mm\"}",
                    "Đọc đủ ba đoạn.","25 + 30 + 45 = 100 (mm).","100 mm = 10 cm."),
                new Guide("Ôn hình chữ nhật và hình tròn","Đường chéo hình chữ nhật cắt nhau tại trung điểm của mỗi đường.","Hình chữ nhật ABCD, hình tròn tâm O qua bốn đỉnh.","{\"type\":\"geometry_circle_grid\",\"points\":{\"A\":[0,0],\"B\":[6,0],\"C\":[6,3],\"D\":[0,3],\"O\":[3,1.5]},\"segments\":[[\"A\",\"B\"],[\"B\",\"C\"],[\"C\",\"D\"],[\"D\",\"A\"],[\"A\",\"O\"],[\"O\",\"C\"],[\"B\",\"O\"],[\"O\",\"D\"]],\"circleCenter\":\"O\",\"circleThrough\":[\"A\",\"B\",\"C\",\"D\"]}",
                    "Xác định đường chéo AC và BD.","Hai đường gặp nhau ở O; OA = OC, OB = OD.","O là trung điểm của AC và BD; AC, BD cũng là đường kính.")};
            default:return new Guide[0];
        }
    }
}
