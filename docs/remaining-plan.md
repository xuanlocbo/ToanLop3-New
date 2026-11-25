# Kế hoạch phần 5–15 và quy tắc tự nghiệm thu

Phạm vi do người dùng phê duyệt: tự triển khai tuần tự đến APK chính thức phần 15; không xin chốt từng phần. Không viết lại dự án; dùng Kotlin cho các lớp được chuyển dần, tiếp tục giữ lớp Java đã kiểm chứng khi chuyển chưa an toàn. Không nới lỏng kiểm thử để đạt kết quả.

| Phần | Phạm vi |
|---|---|
| 5 | Ôn tập 41–44, hoàn tất CI Kotlin thí điểm và chuyển tiến độ |
| 6 | Game Vườn kẹo ngọt 26–29; chuyển giao diện game thuộc phạm vi, bảo toàn save/game state |
| 7 | Game Hồ cầu vồng 30–35 |
| 8 | Game Thung lũng sao 36–40 |
| 9 | Game Đồi mây hồng 41–44 |
| 10 | Đồ họa, nhân vật, cảnh vật, hiệu ứng; cải thiện viewport hình học landscape |
| 11 | Âm thanh, nhạc nền và kiểm tra âm lượng, không làm gián đoạn |
| 12 | Phần thưởng, kho báu, vật phẩm, thành tích |
| 13 | Thống kê, lưu trữ, khôi phục; chuyển lớp dữ liệu/chấm điểm nếu có kiểm thử tương đương đầy đủ |
| 14 | Tích hợp 4 module, tối ưu hiệu năng; đánh giá độ mượt và kích thước thực tế |
| 15 | Tổng kiểm thử, đối chiếu phạm vi Bài 25 theo yêu cầu ban đầu (không tự thêm nội dung sách chưa có căn cứ), kiểm thử điện thoại, APK chính thức |

Mỗi phần: xác định vấn đề → sửa đúng phạm vi → test độc lập + hồi quy → CI thành công, kiểm tra APK, ảnh và dữ liệu → ghi mốc bàn giao (SHA, run, APK, kiểm thử, giới hạn) → tự chuyển bước. Khi CI đang chạy, có thể chuẩn bị kỹ thuật tách riêng nhưng không được gọi là đã nghiệm thu hay thay thế mốc đạt. Không làm lại các phần đã chốt; chỉ kiểm tra hồi quy và lỗi cụ thể. Ưu tiên test các tính năng mới rồi sử dụng lại bộ kiểm thử cũ. Không công bố dữ liệu của bé, không gỡ app thật, không công khai khóa riêng. Không merge main, phát hành chính thức, xóa nhánh đã đạt hoặc force-push khi chưa được phép. Dùng PR và nhánh nối tiếp rõ ràng. Khi hoàn tất phần 15, trình APK chính thức và kết quả để người dùng nghiệm thu; cần chủ dự án quản lý khóa phát hành an toàn và kiểm thử trên thiết bị thật. Không gọi một bản debug là APK phát hành.

Dự án hiện tại: xuanlocbo/ToanLop3-New. Mốc ổn định trước part05: b1174f3aa0cc34a9db52eb39dee10dcb30e04135. CI Java part05 đã đạt. Mốc thử Kotlin: 5306da67bcdc01de9dded6cfd641a154e5f0deb2. Đọc docs/15-part-handoff.md và docs/kotlin-migration.md trước khi tiếp tục; đối chiếu HEAD và CI thực tế, không dùng số liệu cũ khi nhánh đã thay đổi.

Cập nhật: phần 5 đã tự nghiệm thu theo kết quả CI 45539283903, 45540144790, 45542760048. Nền mới là Kotlin p05-k01 và công cụ migration đã đạt. Chuyển sang phần 6, nhánh v1.2-part06-candy. Giữ nguyên các điều kiện phần 15.

Phần 6: đã triển khai ứng viên 1.2.0-p06, cần kiểm thử CI đầy đủ và đăng ký APK chính xác vào công cụ chuyển tiến độ trước tự nghiệm thu. Không tự đánh dấu đạt khi chưa có kết quả.

Phần 6: CI ứng dụng 45544219876 đạt; đang kiểm thử cặp APK p05-k01 ↔ p06 trên v1.2-part06-migration. Giữ đủ bước bắt buộc. Chỉ nghiệm thu sau khi đạt; cung cấp liên kết GitHub Actions ở từng báo cáo tiến độ.
