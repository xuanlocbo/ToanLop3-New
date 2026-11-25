# Mộc Nhiên · Toán 3 — 1.2.0-p02

Phần 2/15: nâng chất lượng học Bài 26–29, dựa trên phần 1 đã chốt (ffd5ecbe1099a686fb7b9c6908a0395995a252af).
- Giữ nguyên toàn bộ 959 mục dữ liệu, trong đó 83 SGK + 100 VBT thuộc Bài 26–29.
- Thêm 12 ví dụ tự biên soạn theo 4 bài, minh họa và mở từng bước giải. Đây là nội dung bổ trợ, không thay thế sách và không tính sao.
- Diễn giải từng lượt đặt tính chia (43 mục có mô hình long_division), gợi ý theo dạng bài, trình bày sơ đồ chia nhóm, máy số và chuỗi thao tác; không đọc đáp án ẩn vào hình.
- Không đổi ID, đáp án, chấm điểm, kho lưu tiến độ, phần thưởng, game hoặc các bài ngoài phạm vi.
- Phạm vi kiểm thử: giữ bộ kiểm thử phần 1; thêm kiểm thử trình bày, phép chia, phạm vi dữ liệu và UI mở từng hướng dẫn trên Android.
- PDF gốc chưa được mở lại ở lượt này; dựa trên dữ liệu nguồn đã đối chiếu trong docs/02 và docs/04. Không tuyên bố đối chiếu nguyên văn lần mới.
- APK debug 1.2.0-p02, versionCode 5; chỉ cài đè khi cùng chữ ký. Không gỡ app cũ có tiến độ.

# Mộc Nhiên · Toán 3 — 1.2.0-p01

Bản kiểm thử phần 1/15: kiến trúc và điều hướng bốn module Android native, phát triển từ commit 1.1.1 a61d71012af08c9b0cbf0e26593591675998bc16.

- Học bài: giữ toàn bộ bài 26–44, SGK, VBT và thử thách.
- Chơi game: giữ 19 màn và 19 trạm, 76 cổng toán.
- Phần thưởng: dùng sao, huy hiệu và kho báu từ ProgressStore hiện có.
- Tiến độ: tổng hợp từng nguồn, từng bài, màn đã hoàn thành và ôn câu sai.
- Màn hình chính có bốn nút. Cài đặt ở đầu màn hình. Giữ đường dẫn cũ để mở lại bài và game.
- Dùng chung Curriculum, ProgressStore và RewardLedger; không tạo vùng lưu mới.

Đây là APK debug cài thử, chưa phải bản cuối 15/15. Không thay dữ liệu, luật chơi, chấm điểm hoặc nội dung trong phần 1. Chỉ xác nhận nghiệm thu sau khi unit test, kiểm thử máy ảo và kiểm tra ảnh đều đạt. Bản cài mới vẫn có thể gặp xung đột khóa ký debug với bản cũ nếu cache không khôi phục. Không gỡ app đang có tiến độ trước khi sao lưu.

## Kiểm tra
Artifact MocNhien-Toan3-1.2.0-p01: APK cài thử đã xác minh cấu trúc và chữ ký. Artifact MocNhien-1.2.0-p01-QA: ảnh chụp thật của bốn module, log package và kết quả chạy hồi quy. Xem trạng thái CI trước khi cài thử.

## Căn cứ phiên bản trước
# Mộc Nhiên · Toán 3 (1.1.1)

Ứng dụng Android native ngoại tuyến, màn hình ngang, chủ đề 2D hồng pastel. Phạm vi Bài 26–44, 19 bài, 921 mục từ SGK/VBT được chuyển thể và 38 câu nâng cao tự biên soạn (959 mục tổng cộng). Các nội dung đã được kiểm tra bằng chương trình; không tuyên bố mọi hình và câu chữ khớp nguyên văn hai PDF nếu chưa đối chiếu từng mục.

- 19 màn phiêu lưu, 19 trạm thực hành, 76 cổng toán; hai câu kho báu mỗi bài có thể làm sau.
- Bài học, ôn câu sai, tiến độ, bản nháp và phần thưởng một lần được lưu cục bộ.
- Đồ họa Canvas/vector, 7 hiệu ứng âm thanh ngắn, không cần internet, không đăng nhập hay quảng cáo.

## Kiểm thử và cài đặt

Bản 1.1.1 sửa bố cục màn hình ngang nhỏ, cập nhật số mục đã hoàn thành ngay sau khi học, hiển thị dụng cụ cạnh nút thao tác, khôi phục vòng lặp game khi cửa sổ gắn trở lại. Bộ kiểm thử UI nhận diện nút hội thoại không phân biệt chữ hoa/thường.

GitHub Actions dùng Java 17, Gradle 8.9, Android SDK 35. Workflow xác minh ngân hàng, kiểm thử JVM, biên dịch và xác minh chữ ký/định danh APK. Sau đó chạy bộ kiểm thử giao diện trên máy ảo Android và lưu ảnh chụp thật trong artifact MocNhien-1.1.1-QA. Artifact MocNhien-Toan3-1.1.1 chứa APK cài thử đã xác minh cấu trúc và chữ ký; xem trạng thái bước máy ảo để biết kết quả kiểm tra tương tác.

APK debug có cùng applicationId vn.mocnhien.toan3, versionCode 3. Khóa debug cũ được khôi phục từ cache nếu có; nếu chữ ký khác với bản đang cài, Android không cho cập nhật đè. Không gỡ bản đang học khi chưa bảo đảm giữ được dữ liệu. Kiểm thử trên điện thoại thật và kiểm nghe âm thanh cần thực hiện riêng; máy ảo CI không phát âm thanh. Đây không phải APK production ký bằng khóa phát hành.
