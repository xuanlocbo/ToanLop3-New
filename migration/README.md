# Chuyển tiến độ giữa các bản APK kiểm thử

Công cụ dùng quy trình đã đạt ở phần 2, hỗ trợ các cặp APK có SHA-256 đăng ký (p01, p02, p03, p04, p05, p05-k01, p06). Mỗi bản sao lưu gồm đúng hai APK và snapshot, cùng schema và ngân hàng dữ liệu. Các bản sau chỉ thêm SHA-256 đã kiểm chứng vào HASHES và chạy kiểm thử cặp APK. Không tin cậy hash từ nguồn không rõ.

Công cụ cục bộ, chỉ dành cho đúng APK debug có hash đăng ký, thiết bị Android một người dùng có run-as. Không thay đổi mã ứng dụng, ngân hàng câu hỏi, schema hay game. Không có dữ liệu thật của bé trong GitHub.

1. Cài Python 3.10+ và Android platform-tools trên máy tính, bật USB debugging, kết nối đúng thiết bị. Thoát ứng dụng về màn hình chính Android để lưu trạng thái trước khi sao lưu.
2. Chạy adb devices để lấy đúng serial. Thay SERIAL trong lệnh dưới đây.
3. Sao lưu vào thư mục mới: python migrate_progress.py --serial SERIAL prepare --directory MocNhien-backup --apk MocNhien-Toan3-1.2.0-p06.apk
4. Xác minh: python migrate_progress.py --serial SERIAL verify --directory MocNhien-backup
5. Chuyển: python migrate_progress.py --serial SERIAL switch --directory MocNhien-backup --target p06
6. Chỉ sau khi gõ SWITCH p06, công cụ thử cài đè. Nếu Android báo khác chữ ký, công cụ xác minh APK cũ và tiến độ chưa đổi, xác minh backup rồi thay thế và khôi phục từng byte. Không xóa app vì lỗi cài khác. Không chạy trên nhiều hồ sơ Android.
7. Giữ thư mục backup riêng trên PC. Nếu gián đoạn chạy lại switch. Chỉ khôi phục vào app trống hoặc cùng snapshot; không ghi đè tiến độ khác. Nếu muốn về bản nguồn, dùng --target p05-k01 (nếu đúng phiên bản nguồn); nếu đã học tiếp, tạo bản backup mới với APK bản nguồn làm đích trước.
8. Không tự gỡ/xóa dữ liệu. Không đưa backup/keystore lên GitHub công khai.

Khóa riêng ký p01 không có trong cache CI; không thể tái tạo từ APK. Cơ chế này là chuyển dữ liệu an toàn, không phải cài đè khác chữ ký. Muốn cập nhật tại chỗ các bản sau cần keystore ổn định do chủ dự án kiểm soát, ngoài repo (ký cục bộ hoặc secrets được chủ repo cấu hình). Không coi debug cache là kho khóa.

CI dùng dữ liệu giả lập và đúng APK gốc. Nếu artifact hết hạn, dừng kiểm thử, không build lại để giả làm bản gốc.

