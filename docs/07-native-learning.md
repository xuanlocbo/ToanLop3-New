# Phần 7/10 — Ứng dụng học Toán Android native

## Mã nguồn đã triển khai
- Nhánh: native-curriculum-1.1. Không thay đổi main hay ứng dụng tiếng Anh.
- Java Android native; MainActivity, QuestionPanel, NativeUi, MathDiagram, GridCanvas. Không sử dụng WebView.
- Curriculum đọc runtime_v2.json, sinh từ ngân hàng đã rà soát bằng Gradle trước khi biên dịch; 959 mục, gồm 437 SGK, 484 VBT và 38 nâng cao thuộc 19 bài (26–44).
- Luồng chọn bài, chọn SGK/VBT/nâng cao, hiển thị nguồn, tiếp tục, ôn câu sai, thành tích, cài đặt âm thanh.
- NativeGrader xử lý 29 dạng nội dung; kiểm tra các ô đặt tính trung gian, thao tác cân/đong, ghép, vẽ hình, góc và kết quả. Nhiệm vụ thực hành mở cần ghi nhận/xác nhận, không tự suy diễn đáp án.
- MathDiagram vẽ các mô hình trọng yếu bằng Android Canvas; một số dạng mô hình được trình bày bằng văn bản và điều khiển native. Chưa đối chiếu giao diện thực tế trên nhiều màn hình.
- BoardState mô phỏng lượt chơi bàn 28 ô của SGK, gồm xúc xắc, quay lại khi trả lời sai, yêu cầu về đích chính xác. Game phiêu lưu 2D mở rộng thuộc phần 8.
- RewardLedger: thưởng một lần theo ID; 80/120 sao cho hai thử thách, 100 sao kho báu; 10 sao cho bài thường đủ điều kiện.
- ProgressStore lưu snapshot JSON bằng SharedPreferences commit: câu đang làm, nội dung trả lời, hoàn thành, ôn lại, thưởng, điều hướng và âm thanh. Sao chép draft/nav để tránh UI sửa trực tiếp snapshot đã lưu.
- Giữ nguyên vùng dữ liệu cũ; không tự gán tiến độ theo chỉ số của phiên bản cũ sang các ID mới vì nguy cơ sai bài. Tiến độ mới được lưu riêng.

## Kiểm tra
- Đã chạy lại kiểm tra dữ liệu và đối chiếu hợp đồng dữ liệu/giao diện/chấm: 959 mục, đủ 29 dạng, không phát hiện lỗi trong các kiểm tra đó.
- Run 45435988089: dữ liệu kiểm tra thành công, mã Java chính biên dịch thành công; test chưa biên dịch do Files.readString không có trong bộ API biên dịch. Đã thay bằng Files.readAllBytes, sử dụng UTF-8.
- Workflow verify-native.yml đã được kích hoạt lại trên nhánh phát triển: https://github.com/xuanlocbo/ToanLop3-New/actions/runs/45436327065
- NativeCurriculumTest gồm kiểm tra đáp án/nháp trống/nháp khôi phục của 959 mục, bài làm thiếu bước/thao tác không hợp lệ, chống cộng thưởng trùng.
- Kết quả chạy lần hai cần được xác nhận từ GitHub Actions; không coi việc viết test là test đã đạt.
- Chưa kiểm thử trên thiết bị Android thật, chưa xác nhận vòng đời ứng dụng và bố cục bằng ảnh chụp; chưa phát hành APK hoàn chỉnh. Workflow đóng gói APK cuối sẽ cần đồng bộ version ở phần 10.

## Tiếp theo
Xác nhận CI và sửa lỗi còn lại trước khi chuyển sang game phiêu lưu native phần 8; phần 9 kiểm tra giao diện, thao tác, lưu/khôi phục tiến độ trên thiết bị hoặc giả lập; phần 10 đóng gói APK.
