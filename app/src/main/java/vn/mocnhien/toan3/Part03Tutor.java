package vn.mocnhien.toan3;
import org.json.JSONObject;

/** Supplementary, unscored teaching aids for the existing measurement curriculum. */
final class Part03Tutor {
    private Part03Tutor() {}
    static boolean applies(int lesson){return lesson>=30&&lesson<=35;}
    static boolean applies(JSONObject q){
        return applies(q.optInt("lesson"))&&("SGK".equals(q.optString("book"))||"VBT".equals(q.optString("book")));
    }
    static final class Guide {
        final String name, rule, example, visual;
        final String[] steps;
        Guide(String name,String rule,String example,String visual,String...steps){
            this.name=name;this.rule=rule;this.example=example;this.visual=visual;this.steps=steps;
        }
    }
    static Guide[] guides(int lesson){
        switch(lesson){
            case 30:return new Guide[]{
                new Guide("Đọc thước mi-li-mét","Mỗi khoảng nhỏ là 1 mm. Độ dài bằng số đo ở đầu cuối trừ số đo ở đầu đầu.","Đo đoạn từ vạch 7 mm đến vạch 32 mm.",
                    "{\"type\":\"ruler\",\"maxMm\":50,\"segment\":{\"startMm\":7,\"endMm\":32}}",
                    "Đọc đầu trái: 7 mm; đầu phải: 32 mm.","32 − 7 = 25 (mm).","Đoạn thẳng dài 25 mm. Hình trên màn hình chỉ minh họa, không dùng làm thước thật."),
                new Guide("Đổi đơn vị độ dài","1 cm = 10 mm; 1 m = 1 000 mm.","3 cm = ? mm","",
                    "Mỗi xăng-ti-mét có 10 mi-li-mét.","3 × 10 = 30.","3 cm = 30 mm."),
                new Guide("So sánh cùng đơn vị","Đổi về cùng đơn vị trước khi so sánh.","4 cm và 35 mm","",
                    "Đổi 4 cm = 40 mm.","40 mm > 35 mm.","Vậy 4 cm dài hơn 35 mm.")};
            case 31:return new Guide[]{
                new Guide("Cân thăng bằng","Khi cân thăng bằng, khối lượng hai đĩa bằng nhau.","Một gói hạt cân bằng với quả cân 200 g và 50 g.",
                    "{\"type\":\"balance\",\"leftWeightsG\":[200,50],\"rightObjects\":[\"Gói hạt\"]}",
                    "Quan sát hai đĩa cân ngang nhau.","200 + 50 = 250 (g).","Gói hạt nặng 250 g."),
                new Guide("Đọc cân đồng hồ","Nhìn đầu kim và đọc đúng đơn vị trên mặt cân.","Kim chỉ vạch 750 g.",
                    "{\"type\":\"dial_scale\",\"object\":\"Túi hạt\",\"valueG\":750,\"maxG\":1000,\"majorTickG\":250,\"labelsG\":[0,250,500,750]}",
                    "Mặt cân ghi đơn vị g.","Đầu kim chỉ 750.","Túi hạt nặng 750 g."),
                new Guide("Đổi ki-lô-gam và gam","1 kg = 1 000 g.","2 kg = ? g","",
                    "Mỗi ki-lô-gam có 1 000 g.","2 × 1 000 = 2 000.","2 kg = 2 000 g.")};
            case 32:return new Guide[]{
                new Guide("Đọc vạch dung tích","Đặt ca thẳng, đọc mực nước theo vạch chia.","Ca 500 ml, mỗi vạch 100 ml.",
                    "{\"type\":\"cup\",\"capacityMl\":500,\"tickMl\":100,\"valueMl\":400}",
                    "Mỗi khoảng vạch là 100 ml.","Mực nước ở vạch thứ tư tính từ 0.","Ca chứa 400 ml."),
                new Guide("Đổi lít ra mi-li-lít","1 l = 1 000 ml.","Một bình có 1 l nước, rót đi 350 ml.","",
                    "Đổi 1 l = 1 000 ml.","1 000 − 350 = 650 (ml).","Còn 650 ml."),
                new Guide("Cộng lượng nước","Cộng các lượng nước khi cùng đơn vị.","Hai ca chứa 200 ml và 350 ml.",
                    "{\"type\":\"cups\",\"tickMl\":50,\"cups\":[{\"id\":\"A\",\"capacityMl\":500,\"valueMl\":200},{\"id\":\"B\",\"capacityMl\":500,\"valueMl\":350}]}",
                    "Ca A có 200 ml; ca B có 350 ml.","200 + 350 = 550 (ml).","Cả hai ca có 550 ml.")};
            case 33:return new Guide[]{
                new Guide("Đọc nhiệt kế không khí","Đọc vị trí đầu cột màu, theo vạch chia; đơn vị là độ C (°C).","Nhiệt kế có mỗi vạch nhỏ 1 °C.",
                    "{\"type\":\"thermometer\",\"rangeC\":[0,50],\"stepC\":1,\"majorTickC\":5,\"valueC\":24}",
                    "Tìm vạch 20 °C.","Đầu cột màu cao hơn 20 bốn vạch nhỏ.","Nhiệt độ là 24 °C, đọc là hai mươi tư độ xê."),
                new Guide("So sánh nhiệt độ","Số đo lớn hơn nghĩa là nhiệt độ cao hơn.","Sáng 18 °C, trưa 29 °C, tối 22 °C.","",
                    "So sánh ba số: 18, 29 và 22.","18 < 22 < 29.","Trưa nóng nhất, sáng có nhiệt độ thấp nhất."),
                new Guide("Đọc nhiệt kế cơ thể","Đọc số đo theo thang nhiệt kế; nhờ người lớn hỗ trợ khi thấy không khỏe.","Nhiệt kế minh họa 37 °C.",
                    "{\"type\":\"clinical_thermometer\",\"valueC\":37,\"rangeC\":[35,42]}",
                    "Nhìn đầu cột chỉ nhiệt độ.","Đầu cột dừng ở 37 °C.","Đọc: ba mươi bảy độ xê. Không dùng hình minh họa để đo nhiệt độ thật.")};
            case 34:return new Guide[]{
                new Guide("Đo đồ vật thật","Dùng thước thật, đặt vạch 0 ở đầu vật và đọc vạch ở đầu còn lại.","Ví dụ: một chiếc tẩy rộng 23 mm.",
                    "{\"type\":\"ruler\",\"maxMm\":50,\"segment\":{\"startMm\":0,\"endMm\":23}}",
                    "Đặt vạch 0 của thước thật ở đầu vật.","Đọc vạch cuối theo mm.","Ghi kết quả đo thật. Không lấy kích thước ảnh trên điện thoại làm số đo."),
                new Guide("Chọn quả cân","Tìm các quả cân có tổng bằng khối lượng cần lấy.","Cần lấy 350 g, có các quả cân 200 g, 100 g, 50 g.",
                    "{\"type\":\"balance\",\"leftWeightsG\":[200,100,50],\"rightObjects\":[\"Gạo\"]}",
                    "200 + 100 = 300 (g).","300 + 50 = 350 (g).","Khi cân thăng bằng, phần gạo có khối lượng 350 g."),
                new Guide("Chọn hai ca","Hai lượng nước cộng lại đúng lượng cần.","Có ba ca A: 150 ml, B: 200 ml, C: 350 ml. Cần 500 ml.",
                    "{\"type\":\"cups\",\"tickMl\":50,\"cups\":[{\"id\":\"A\",\"capacityMl\":500,\"valueMl\":150},{\"id\":\"B\",\"capacityMl\":500,\"valueMl\":200},{\"id\":\"C\",\"capacityMl\":500,\"valueMl\":350}]}",
                    "Thử ghép hai lượng nước.","150 + 350 = 500 (ml).","Chọn ca A và ca C.")};
            case 35:return new Guide[]{
                new Guide("Tính với cùng đơn vị","Giữ đơn vị sau mỗi phép tính; chỉ cộng trừ trực tiếp các lượng cùng đơn vị.","180 g + 60 g − 40 g","",
                    "180 + 60 = 240 (g).","240 − 40 = 200 (g).","Kết quả 200 g."),
                new Guide("Gấp rồi giảm số đo","Tính theo từng thao tác; giữ đúng đơn vị.","15 mm gấp 4 lần rồi giảm 3 lần.","",
                    "15 × 4 = 60 (mm).","60 : 3 = 20 (mm).","Kết quả 20 mm."),
                new Guide("Đong nước bằng hai cốc","Đổ đầy, rót cho đến khi cốc nhận đầy hoặc cốc cho hết nước; không ước lượng tùy ý.","Có cốc 300 ml và 100 ml. Lấy đúng 200 ml.",
                    "{\"type\":\"cups\",\"tickMl\":100,\"cups\":[{\"id\":\"Lớn\",\"capacityMl\":300,\"valueMl\":300},{\"id\":\"Nhỏ\",\"capacityMl\":100,\"valueMl\":0}]}",
                    "Đổ đầy cốc 300 ml.","Rót từ cốc lớn sang cốc 100 ml đang rỗng cho tới khi đầy.","Cốc lớn còn 300 − 100 = 200 (ml).")};
            default:return new Guide[0];
        }
    }
    static String hint(JSONObject q){
        String old=Part02Tutor.hint(q);
        if(!applies(q))return old;
        JSONObject v=q.optJSONObject("visual");if(v==null)return old;
        // Keep the already-reviewed problem-specific hint; add general method only.
        String extra="";
        switch(v.optString("type")){
            case "ruler":case "ruler_object":case "real_world_measure":
                extra="Đọc theo vạch chia; lấy số đo đầu cuối trừ số đo đầu đầu. Khi bài yêu cầu đo thật, dùng thước và vật hoặc sách in thật.";break;
            case "conversion":
                extra="Đổi về đơn vị đề yêu cầu. Nhớ 1 cm = 10 mm; 1 m = 1 000 mm; 1 kg = 1 000 g; 1 l = 1 000 ml.";break;
            case "balance":case "balance_select":case "balance_sequence":
                extra="Khi thăng bằng, tổng khối lượng bên trái bằng tổng khối lượng bên phải.";break;
            case "dial_scale":case "scales":
                extra="Nhìn đầu kim, đọc vạch và đơn vị trên mặt cân.";break;
            case "cup":case "cups":
                extra="Đọc lượng nước theo vạch chia, không nhầm dung tích tối đa với lượng nước đang có.";break;
            case "thermometer":case "clinical_thermometer":
                extra="Đọc đầu cột chỉ nhiệt độ theo vạch chia, đơn vị °C.";break;
            case "pour":
                extra="Chỉ rót đến khi cốc nhận đầy hoặc cốc cho hết. Theo dõi lượng nước của từng cốc.";break;
            default:break;
        }
        return extra.isEmpty()?old:(old.isEmpty()?extra:old+"\n"+extra);
    }
}