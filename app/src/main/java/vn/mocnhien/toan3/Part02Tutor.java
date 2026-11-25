package vn.mocnhien.toan3;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

/** Part 02: pedagogical presentation only. Never writes progress or changes grading. */
final class Part02Tutor {
    private Part02Tutor() {}
    static boolean applies(int lesson) { return lesson >= 26 && lesson <= 29; }
    static boolean applies(JSONObject q) {
        return applies(q.optInt("lesson")) && ("SGK".equals(q.optString("book")) || "VBT".equals(q.optString("book")));
    }
    static final class Guide {
        final String name, rule, example, diagram;
        final String[] steps;
        Guide(String name,String rule,String example,String diagram,String... steps) {
            this.name=name;this.rule=rule;this.example=example;this.diagram=diagram;this.steps=steps;
        }
    }
    // Original supplementary examples, separate from source records and the reward ledger.
    static Guide[] guides(int lesson) {
        switch(lesson) {
            case 26:return new Guide[]{
                new Guide("Chia theo từng hàng","Chia từ trái sang phải: chia, nhân, trừ, hạ.","68 : 2","division",
                    "6 chục chia 2 được 3 chục. Viết 3; 3 × 2 = 6, trừ còn 0.",
                    "Hạ 8. 8 chia 2 được 4. Viết 4; 4 × 2 = 8, trừ còn 0.",
                    "68 : 2 = 34. Thử lại: 34 × 2 = 68."),
                new Guide("Chia có dư","Số dư luôn nhỏ hơn số chia.","83 : 4","division",
                    "8 chia 4 được 2. Viết 2; 2 × 4 = 8, trừ còn 0.",
                    "Hạ 3. 3 chia 4 được 0. Viết 0 ở hàng đơn vị; còn dư 3.",
                    "83 : 4 = 20 dư 3. Thử lại: 20 × 4 + 3 = 83."),
                new Guide("Chia theo nhóm","Nếu cần xếp hết đồ vật mà còn dư, phải có thêm một nhóm.","17 bạn, mỗi bàn 4 chỗ","groups",
                    "17 : 4 = 4 dư 1: xếp đủ 4 bàn, còn 1 bạn.",
                    "Bạn còn lại cần thêm 1 bàn.",
                    "Cần ít nhất 4 + 1 = 5 bàn. Chú ý: hỏi số bàn, không chỉ hỏi thương.")};
            case 27:return new Guide[]{
                new Guide("Giảm đi một số lần","Muốn giảm một số đi một số lần, lấy số đó chia cho số lần.","24 giảm 3 lần","reduce",
                    "Chia 24 thành 3 phần bằng nhau.",
                    "24 : 3 = 8. Một phần có 8.",
                    "Vậy 24 giảm 3 lần được 8."),
                new Guide("Phân biệt giảm và bớt","Giảm 3 lần là chia 3; bớt 3 đơn vị là trừ 3.","18 giảm 3 lần; 18 bớt 3","compare",
                    "18 : 3 = 6. Đây là giảm theo số lần.",
                    "18 − 3 = 15. Đây là bớt theo số đơn vị.",
                    "Đọc kỹ chữ “lần” hay “đơn vị” trước khi chọn phép tính."),
                new Guide("Hai thao tác liên tiếp","Làm thao tác thứ nhất rồi dùng kết quả để làm thao tác thứ hai.","32 giảm 4 lần rồi gấp 3 lần","chain",
                    "32 : 4 = 8.",
                    "Lấy 8 × 3 = 24.",
                    "Kết quả cuối cùng là 24.")};
            case 28:return new Guide[]{
                new Guide("Nhiều hơn và tìm tổng","Tìm số lượng còn thiếu trước, sau đó tìm tổng.","An có 6 nhãn vở, Bình có nhiều hơn 3 nhãn","more",
                    "Bình có: 6 + 3 = 9 (nhãn vở).",
                    "Cả hai có: 6 + 9 = 15 (nhãn vở).",
                    "Đáp số: 15 nhãn vở."),
                new Guide("Gấp một số lần và tìm tổng","Gấp dùng phép nhân. Muốn biết tất cả, cộng hai số lượng.","Giỏ A có 4 quả, giỏ B gấp 3 lần","multiple",
                    "Giỏ B có: 4 × 3 = 12 (quả).",
                    "Hai giỏ có: 4 + 12 = 16 (quả).",
                    "Đáp số: 16 quả."),
                new Guide("Ít hơn và tìm tổng","Ít hơn một số đơn vị dùng phép trừ, rồi cộng để tìm tổng.","Lan có 12 bút, Mai ít hơn 5 bút","less",
                    "Mai có: 12 − 5 = 7 (bút).",
                    "Cả hai có: 12 + 7 = 19 (bút).",
                    "Đáp số: 19 bút.")};
            case 29:return new Guide[]{
                new Guide("Nhẩm theo hàng chục","Nhẩm bằng cách tính số chục, rồi đổi về số đơn vị.","70 : 5","tens",
                    "70 = 50 + 20. Cả hai số đều chia hết cho 5.",
                    "50 : 5 = 10; 20 : 5 = 4.",
                    "70 : 5 = 10 + 4 = 14."),
                new Guide("Tính đúng thứ tự theo sơ đồ","Mỗi mũi tên là một thao tác; không bỏ qua bước.","12 gấp 4 lần rồi giảm 6 lần","chain29",
                    "12 × 4 = 48.",
                    "48 : 6 = 8.",
                    "Kết quả cuối cùng là 8."),
                new Guide("Chia có dư trong bài toán","Hỏi được nhiều nhất bao nhiêu nhóm đủ: dùng thương; phần thừa là số dư.","26 m dây, mỗi đoạn dài 4 m","cut",
                    "26 : 4 = 6 dư 2.",
                    "Cắt được 6 đoạn đủ 4 m, dùng 24 m.",
                    "Còn 2 m dây; không đủ cắt thêm một đoạn 4 m.")};
            default:return new Guide[0];
        }
    }
    static String hint(JSONObject q) {
        String old=q.optString("hint");
        if(!applies(q))return old;
        JSONObject v=q.optJSONObject("visual");String t=v==null?"":v.optString("type");
        String kind=q.optString("kind");
        switch(t){
            case "long_division":return "Chia → nhân → trừ → hạ. Nếu số đầu nhỏ hơn số chia, lấy thêm chữ số kế tiếp. Sau khi đã viết chữ số đầu của thương, nếu số đang chia nhỏ hơn số chia, viết 0 vào thương. Số dư phải nhỏ hơn số chia.";
            case "worked_division":return "Thử lại: lấy thương nhân số chia, cộng số dư. Kết quả phải bằng số bị chia và số dư nhỏ hơn số chia.";
            case "missing_factor":return "Muốn tìm thừa số chưa biết, lấy tích chia cho thừa số đã biết.";
            case "missing_dividend":return "Muốn tìm số bị chia trong phép chia hết, lấy thương nhân với số chia.";
            case "missing_divisor":return "Muốn tìm số chia trong phép chia hết, lấy số bị chia chia cho thương.";
            case "number_machine":case "operation_chain":case "route":case "bridges":
                return "Gấp một số lần: nhân. Giảm một số lần: chia. Thêm đơn vị: cộng. Bớt đơn vị: trừ. Theo từng mũi tên, dùng kết quả trước cho bước tiếp theo.";
            case "bar_compare":case "bar_multiple":case "polyline":
                return "Đọc sơ đồ: tìm phần đã biết và phần cần tìm. Nhiều hơn dùng cộng, ít hơn dùng trừ, gấp dùng nhân. Nếu hỏi tất cả hoặc cả đường, cộng các phần sau khi tìm được phần còn thiếu.";
            case "seating":return "Chia số bạn cho số chỗ mỗi bàn. Nếu còn bạn chưa có chỗ, cần thêm một bàn.";
            case "equal_groups":case "group_scale":case "division_context":case "capacity_grouping":case "cut_segments":
                return "Xác định tổng số và số nhóm hoặc số vật mỗi nhóm. Chia để tìm phần bằng nhau hoặc số nhóm đủ; đọc kỹ câu hỏi về phần dư.";
            default: if("steps".equals(kind)||"compose_story".equals(kind))
                return "Tìm dữ kiện còn thiếu ở bước 1. Dùng kết quả đó ở bước 2 để trả lời đúng điều đề hỏi. Nhớ ghi đơn vị.";
                return old;
        }
    }
    static List<String> divisionSteps(int n,int divisor) {
        if(n<0||divisor<1||divisor>9)throw new IllegalArgumentException("division");
        List<String> result=new ArrayList<>();
        String digits=Integer.toString(n);int partial=0,quotient=0;boolean started=false;
        for(int i=0;i<digits.length();i++){
            int digit=digits.charAt(i)-'0';partial=partial*10+digit;
            if(!started && partial<divisor && i+1<digits.length())continue;
            int q=partial/divisor,p=q*divisor,r=partial-p;
            String prefix=started?"Hạ "+digit+", được "+partial+". ":"";
            result.add(prefix+partial+" : "+divisor+" được "+q+"; viết "+q+". "+q+" × "+divisor+" = "+p+"; "+partial+" − "+p+" = "+r+".");
            started=true;quotient=quotient*10+q;partial=r;
        }
        result.add(n+" : "+divisor+" = "+quotient+(partial==0?"":" dư "+partial)+". Thử lại: "+quotient+" × "+divisor+(partial==0?"":" + "+partial)+" = "+n+".");
        return result;
    }
    static String solution(JSONObject q) {
        String old=q.optString("solution");
        if(!applies(q))return old;
        JSONObject v=q.optJSONObject("visual");if(v==null)return old;
        if("long_division".equals(v.optString("type"))){
            int n=v.optInt("dividend",-1),d=v.optInt("divisor",-1);
            if(n>=0&&d>0&&d<10){
                StringBuilder b=new StringBuilder();List<String> steps=divisionSteps(n,d);
                for(int i=0;i<steps.size();i++){if(i>0)b.append("\n");if(i<steps.size()-1)b.append("Lượt ").append(i+1).append(": ");b.append(steps.get(i));}
                return b.toString();
            }
        }
        // Keep the authoritative worked solution, and make the two-step structure legible.
        if("steps".equals(q.optString("kind"))||"compose_story".equals(q.optString("kind"))){
            JSONObject a=q.optJSONObject("answer");
            if(a!=null && a.has("intermediate") && a.has("final")){
                return "Bước 1: tìm số lượng còn thiếu = "+a.optInt("intermediate")+".\nBước 2: dùng kết quả bước 1 để tính kết quả cuối = "+a.optInt("final")+" "+a.optString("unit")+".\n"+old;
            }
        }
        return old;
    }
}