# Mộc Nhiên · Toán 3 (1.1.0)

Ứng dụng Android native ngoại tuyến, màn hình ngang, chủ đề 2D hồng pastel. Phạm vi Bài 26–44, 19 bài, 921 mục từ SGK/VBT được chuyển thể và 38 câu nâng cao tự biên soạn (959 mục tổng cộng). Các nội dung đã được kiểm tra bằng chương trình; không tuyên bố mọi hình và câu chữ khớp nguyên văn hai PDF nếu chưa đối chiếu từng mục.

- 19 màn phiêu lưu, 19 trạm thực hành, 76 cổng toán; hai câu kho báu mỗi bài có thể làm sau.
- Bài học, ôn câu sai, tiến độ, bản nháp và phần thưởng một lần được lưu cục bộ.
- Đồ họa Canvas/vector, 7 hiệu ứng âm thanh ngắn, không cần internet, không đăng nhập hay quảng cáo.

## Kiểm thử và cài đặt

GitHub Actions dùng Java 17, Gradle 8.9, Android SDK 35. Workflow xác minh ngân hàng, kiểm thử JVM, biên dịch và xác minh chữ ký/định danh APK. Sau đó chạy bộ kiểm thử giao diện trên máy ảo Android và lưu ảnh chụp thật trong artifact MocNhien-1.1.0-QA. Artifact MocNhien-Toan3-1.1.0 chứa APK cài thử đã xác minh cấu trúc và chữ ký; xem trạng thái bước máy ảo để biết kết quả kiểm tra tương tác.

APK debug có cùng applicationId vn.mocnhien.toan3, versionCode 2. Khóa debug cũ được khôi phục từ cache nếu có; nếu chữ ký khác với bản đang cài, Android không cho cập nhật đè. Không gỡ bản đang học khi chưa bảo đảm giữ được dữ liệu. Kiểm thử trên điện thoại thật và kiểm nghe âm thanh cần thực hiện riêng; máy ảo CI không phát âm thanh. Đây không phải APK production ký bằng khóa phát hành.
