# Chuyển dần Java → Kotlin

Người dùng đã đồng ý chuyển dần sang Kotlin; giữ nguyên kế hoạch 15 phần. Nhánh thí điểm v1.2-kotlin-pilot tách từ mã p05 9981c9d40f2bb75810531c16be3b45ae204d4f41, không thay thế bản p05 đang CI. Chỉ dùng kết quả Kotlin làm nền sau khi bản p05 và nhánh thí điểm đều đạt, đồng thời xác minh không mất tiến độ.

- Giai đoạn thí điểm: chuyển thực tế AppModule và ModuleHome sang Kotlin, xóa hai file Java tương ứng trên nhánh thí điểm; giữ nguyên tên class, Java-facing ABI, màn hình, màu, route và hành vi. Toàn bộ app khác giữ nguyên.
- Kotlin 2.1.21, AGP 8.7.2, Gradle 8.9, JDK/JVM 17, SDK 35. AGP giảm đúng một patch trên nhánh thử để nằm trong ma trận Kotlin được kiểm chứng chính thức; không thay toolchain bản p05 Java. Nguồn: https://kotlinlang.org/docs/gradle-configure-project.html và https://developer.android.com/build/kotlin-support.
- Không thêm Compose, framework game hoặc thư viện nặng; chỉ Kotlin runtime tiêu chuẩn. Không đồng nhất Kotlin với hiệu năng; so sánh trên cùng thiết bị và cùng kịch bản. Không đổi kích thước giao diện hoặc công thức số học trong bước chuyển này.
- Kiểm thử lại toàn bộ unit tests, thêm kiểm thử tương đương route/metadata, kiểm tra class Kotlin đã biên dịch, khóa 959 mục theo SHA-256, chạy nguyên smoke test Android Bài 26–44 và bốn module. APK thử: 1.2.0-p05-k01 (versionCode 9), không phải bản p06.
- Sau khi đạt: chuyển giao diện học và ôn tập; tiếp đến giao diện phần thưởng/tiến độ; game UI và phần mô phỏng ở bước riêng; lưu tiến độ/chấm điểm chỉ chuyển cuối cùng nếu kiểm thử tương đương đầy đủ. Không chuyển nhiều lớp quan trọng đồng thời, không phá schema, ID hoặc quy tắc thưởng. Bất kỳ bản thử chưa đạt đều không được thay thế bản ổn định.
- Nếu p05 cần sửa, kế thừa bản sửa trước khi dùng nhánh thí điểm làm nền. Không tự gỡ app có tiến độ. Dùng công cụ migration đã xác minh cho APK được đăng ký chính xác khi cần, không khôi phục khóa cũ.

- Đã biên dịch thử hai lớp Kotlin và đạt 26/26 unit tests. Phần 5 Java đã đạt CI đầy đủ (24/24, 60 ảnh). Nhánh v1.2-part05-kotlin-migration đăng ký hai APK chính xác và kiểm thử p04/p05 ↔ p05-k01. Chưa thay thế nhánh ổn định khi CI Kotlin chưa đạt.

- Thí điểm Kotlin ĐẠT CI 45540144790 (26/26, 60 ảnh Android) và migration 45542760048 (15/15, hai cặp nâng cấp/phục hồi). Chọn làm nền phần 6. Chỉ bổ sung chuyển đổi lớp thuộc phạm vi mỗi phần, không chuyển đồng thời lớp lưu dữ liệu.
