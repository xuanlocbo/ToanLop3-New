# Mộc Nhiên – Toán lớp 3

Dự án Android Native độc lập, giao diện 2D màu hồng, màn hình ngang, hoạt động ngoại tuyến. 20 chủ đề bài 25–44 Kết nối tri thức, 24 vị trí nhiệm vụ và 2 thử thách mỗi bài, bài tập tự soạn, trò chơi chia giỏ táo, thu hoạch, đong nước, dụng cụ đo, ôn tập câu sai, thành tích, lưu từng trạng thái và tiến độ trên máy. Chưa xác nhận đối chiếu từng câu với hai bản PDF SGK/VBT của người dùng; chưa phải bản cuối về nội dung hoặc chất lượng giao diện.

## APK thử nghiệm

GitHub Actions dùng Java 17, Gradle 8.9, Android SDK 35 và assembleDebug như LittleEnglish. Kiểm tra ngân hàng, đóng gói, chữ ký và mã ứng dụng, sau đó tải APK trong Actions > Build Android APK > Artifacts. Không cần tạo secret để build thử nghiệm. Khóa debug được cache để hỗ trợ cập nhật khi cache còn tồn tại; không bảo đảm tương thích chữ ký nếu cache mất. Không coi APK debug là bản phát hành production. Cần kiểm tra giao diện, thao tác và lưu tiến độ trên thiết bị thực.
