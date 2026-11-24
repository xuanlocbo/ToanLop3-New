# Phần 6/10 — Kiểm tra chéo, chuẩn hóa và thử thách kho báu

**Trạng thái:** hoàn thành dữ liệu, quy tắc chấm và lớp tính thưởng Java; chưa nối vào màn hình Android, lưu trạng thái thực tế hoặc tạo APK.

## Phạm vi

Đọc lại 11 tệp JSON nguồn đã lưu từ phần 2–5, gồm 437 mục SGK và 484 mục VBT. Xác nhận lại hai mã SHA-256 PDF đã khóa ở phần 1. Kiểm tra độc lập dữ liệu, mở lại các trang PDF trọng điểm về hình học, phép tính, bài chọn số đo, nhiệt kế, cân, ca nước, chuỗi phép tính và bàn chơi. Không khẳng định đã đọc thủ công lại toàn bộ 97 trang lần thứ hai; việc đối chiếu theo từng trang đã thực hiện trong phần 2–5. Nội dung mới gồm 38 câu tự soạn, không gán là nội dung SGK/VBT.

| Thành phần | Số mục |
|---|---:|
| SGK | 437 |
| VBT | 484 |
| Nâng cao (2 mỗi bài, đủ 19 bài) | 38 |
| Tổng | 959 |

## Kiểm tra chéo

- Bộ kiểm tra chạy trên dữ liệu đọc lại từ GitHub sau chuẩn hóa: 959 ID, đầy đủ nguồn, bài 26–44, 47 trang SGK, 50 trang VBT và 38 câu nâng cao; kiểm tra thứ tự các bước, dạng đáp án, tùy chọn và thưởng.
- 846 mục có phép kiểm tra khai báo trong dữ liệu, 35 mục được kiểm tra bằng quy tắc bổ sung độc lập. Tổng 881 mục có kiểm tra định lượng/tương tác (gồm 16 ví dụ).
- 21 mục chọn số đo và ngữ cảnh được đối chiếu bằng lựa chọn tương ứng của nguồn; 8 mục thực hành mở không áp đáp án cố định; 49 ví dụ còn lại được đọc lại lời giải/quy tắc, không chấm điểm. Tổng 881 + 21 + 8 + 49 = 959; tổng 65 ví dụ học.
- Kiểm tra riêng 101 mô hình đặt tính chia, 46 mô hình đặt tính nhân, nghiệm điền số, dấu, hình học, bài cân và chuỗi rót nước. Kiểm tra cả 38 câu nâng cao. Kết quả thực thi hàm kiểm tra độc lập: không phát hiện lỗi (errors=[]).
- Sửa 44 điểm chuẩn hóa được ghi nhận: đồng nhất cấu trúc bài hai bước, liên kết các bước phép tính, tách đáp án 16 ví dụ khỏi chấm điểm, bỏ số đo ước lượng trong 3 nhiệm vụ thực hành, chuyển 2 bài cân gạo từ chọn câu văn sang thao tác và tách luật nguồn khỏi quy ước bàn chơi ứng dụng. Giữ nguyên ID nguồn. Bộ chuẩn hóa sinh dữ liệu v2, không ghi đè JSON gốc.

## Thử thách và thưởng

Mỗi bài có 2 câu nâng cao, đúng nội dung/kiến thức từng bài: suy luận ngược, xử lý số dư, biểu thức, điền số, đo lường, hình học. Không yêu cầu kiến thức lớp trên. Giải thích từng câu và gợi ý khi cần.

| Hành động | Sao hồng |
|---|---:|
| Hoàn thành đúng một mục thường có chấm | 10 |
| Thử thách thứ nhất | 80 |
| Thử thách thứ hai | 120 |
| Hoàn thành cả hai, mở kho báu của bài | 100 |
| Tổng tối đa từ nâng cao mỗi bài | 300 |
| Tổng từ 19 kho báu và 38 câu nâng cao | 5700 |

Sai không trừ sao; chơi lại và sử dụng gợi ý được phép; thưởng chỉ một lần theo ID. Ví dụ và thực hành mở không tự phát sao. Lớp Java RewardLedger tính tổng từ tập ID đã nhận, không tin bộ đếm rời; kho báu chỉ phát sinh nếu có đủ hai ID. Cần lưu snapshot dưới một giá trị, theo luồng cập nhật tuần tự và commit thành công trước khi hiển thị. Chưa triển khai lớp lưu tiến độ Android ở phần này.

Kiểm thử Java: sao chép chính xác hai blob đã lưu (RewardLedger.java aa42a582edbf02d6c7f81cb2b2a48c51a0dea295 và RewardLedgerTest.java fa3f247a2a0fa23a3bc9dcf5567ba6b8e5285b1e), kiểm tra git hash-object trùng khớp, biên dịch bằng javac và thực thi RewardLedgerTest. Kết quả REWARD_LEDGER_PASS: đủ 19 kho báu, chơi lại, khôi phục snapshot, trả lời sai và mã thưởng không hợp lệ. Đây là kiểm thử logic Java, chưa phải thử đóng/mở app trên điện thoại.

## Tệp và cách kiểm tra

- app/src/main/assets/curriculum/bonus_26_44.json
- tools/prepare_curriculum.mjs — chạy node tools/prepare_curriculum.mjs để sinh app/build/generated/curriculum/runtime_v2.json.
- tests/verify_curriculum_v2.mjs — chạy node tests/verify_curriculum_v2.mjs từ thư mục gốc repository; kiểm tra 959 bản ghi.
- app/src/main/java/vn/mocnhien/toan3/RewardLedger.java
- tests/java/vn/mocnhien/toan3/RewardLedgerTest.java

Các phép kiểm tra dữ liệu không thay thế kiểm thử giao diện, thao tác chấm và lưu tiến độ Android. Chưa nối bước sinh dữ liệu v2 và kiểm tra vào Gradle/GitHub Actions, chưa có APK. Không sửa LittleEnglish hay nhánh main.

## Phần tiếp theo

Phần 7/10: triển khai Android native, sử dụng đúng ngân hàng v2, các loại câu hỏi và quy tắc chấm; giao diện đã chốt, chữ lớn, tông hồng, đơn giản, không HTML/WebView; lưu tự động tiến độ học và trạng thái đang làm. Kiểm tra mỗi dạng tương tác, không lộ đáp án trước khi trẻ trả lời. Tích hợp lớp thưởng Java và lưu snapshot nguyên tử. Phần 8 xây dựng game phiêu lưu 2D; phần 9 kiểm thử; phần 10 build và bàn giao APK.
