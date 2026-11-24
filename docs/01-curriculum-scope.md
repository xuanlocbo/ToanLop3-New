# Toán lớp 3 — Phần 1/10: Chốt nguồn và phạm vi

## Nguồn khóa theo bản PDF được cung cấp

- SGK Toán 3 tập một, Kết nối tri thức với cuộc sống, PDF 126 trang, SHA-256 `ca5720f69357ca5968fc8e96cc4961328a316ba680936548d2eeaae936a3fbcf`. Phạm vi trang in 75–121, PDF trang 76–122, 47 trang.
- VBT Toán 3 tập một, Kết nối tri thức với cuộc sống, PDF 118 trang, SHA-256 `cadef7165ad41318d6e93698b49d8f42a1ec909bfed4f22e22c3ad43cf3ce822`. Phạm vi trang in 66–115, PDF trang 67–116, 50 trang.

Đã kiểm tra trực quan bìa, mục lục, trang mở đầu 19 bài của từng sách và trang cuối bài 44. Hai tài liệu là bản ảnh, không có văn bản trích xuất đáng tin cậy. Số trang PDF (đếm từ 1) = số trang in + 1. Dùng đúng hai PDF, không thay bằng nguồn khác. Không đưa bài 25 vào ứng dụng mới. Không lưu PDF sách vào kho GitHub công khai.

## Chỉ mục bài học

| Bài | Tên bài | Chủ đề SGK | SGK (trang in) | VBT (trang in) | Số nhiệm vụ VBT ứng viên |
|---|---|---|---|---|---:|
| 26 | Chia số có hai chữ số cho số có một chữ số | 4 | 75–78 | 66–68 | 35 |
| 27 | Giảm một số đi một số lần | 4 | 79–80 | 69–70 | 23 |
| 28 | Bài toán giải bằng hai bước tính | 4 | 81–82 | 71–73 | 14 |
| 29 | Luyện tập chung | 4 | 83–84 | 74–75 | 26 |
| 30 | Mi-li-mét | 5 | 85–86 | 76–77 | 23 |
| 31 | Gam | 5 | 87–88 | 78–79 | 15 |
| 32 | Mi-li-lít | 5 | 89–90 | 80 | 12 |
| 33 | Nhiệt độ. Đơn vị đo nhiệt độ | 5 | 91–92 | 81 | 8 |
| 34 | Thực hành và trải nghiệm với các đơn vị mi-li-mét, gam, mi-li-lít, độ C | 5 | 93–94 | 82–84 | 17 |
| 35 | Luyện tập chung | 5 | 95–96 | 85–87 | 22 |
| 36 | Nhân số có ba chữ số với số có một chữ số | 6 | 97–98 | 88–89 | 23 |
| 37 | Chia số có ba chữ số cho số có một chữ số | 6 | 99–103 | 90–93 | 31 |
| 38 | Biểu thức số. Tính giá trị của biểu thức số | 6 | 104–108 | 94–97 | 39 |
| 39 | So sánh số lớn gấp mấy lần số bé | 6 | 109–110 | 98–99 | 21 |
| 40 | Luyện tập chung | 6 | 111–112 | 100–101 | 21 |
| 41 | Ôn tập phép nhân, phép chia trong phạm vi 100, 1 000 | 7 | 113–115 | 102–105 | 56 |
| 42 | Ôn tập biểu thức số | 7 | 116–117 | 106–108 | 26 |
| 43 | Ôn tập hình học và đo lường | 7 | 118–119 | 109–112 | 39 |
| 44 | Ôn tập chung | 7 | 120–121 | 113–115 | 25 |

## Kiểm tra dữ liệu đang có

- Nhánh `native-curriculum-1.1`: ngân hàng VBT ứng viên chứa 476 nhiệm vụ, 476 ID duy nhất; 0 bản ghi ngoài phạm vi trang/bài; trang in VBT không có bản ghi: không có. Đây chỉ là kiểm tra cấu trúc, không xác nhận độ đúng của từng bài tập.
- Mã app cũ vẫn chứa bài 25 và câu sinh tự động; cần thay thế ở phần 7. Chưa tích hợp ngân hàng VBT ứng viên; chưa có ngân hàng SGK hoàn chỉnh trên nhánh này.
- Mã nguồn Android native, không HTML/WebView. Giữ package riêng `vn.mocnhien.toan3`, không sửa dự án LittleEnglish hoặc nhánh main. Phần 1 không thay đổi mã app hay tạo APK.

## Quy tắc cho dữ liệu ở phần 2–6

Mỗi nhiệm vụ có mã ổn định theo nguồn, bài, trang in, tiết/hoạt động, câu và ý nhỏ; đáp án, lời giải có từng bước, gợi ý không tiết lộ đáp án ngay. Phân biệt SGK, VBT và câu tự soạn; đánh dấu bài thực hành không có đáp án cố định, kiểm tra bằng thao tác hoặc người lớn với dụng cụ thật. Giữ các dữ kiện và hình học đúng nguồn; không tự suy đoán phần ảnh không đọc rõ. Dùng mô hình tương tác vẽ lại bằng mã native thay vì sao chép ảnh nguyên trang. Với câu nâng cao, 2 câu mỗi bài (38 câu), giới hạn kiến thức đã học, có phần thưởng lớn riêng và chống cộng lặp điểm.

## 10 phần triển khai

1. **Hoàn thành:** Chốt nguồn, phạm vi, đối chiếu trang và tiêu chí nghiệm thu.
2. Chưa thực hiện trong phần 1: Trích SGK bài 26–35, từng hoạt động/luyện tập và hình, kiểm tra phép tính.
3. Chưa thực hiện trong phần 1: Trích SGK bài 36–44, từng hoạt động/luyện tập và hình, kiểm tra phép tính.
4. Chưa thực hiện trong phần 1: Đối chiếu và sửa VBT bài 26–35 với bản PDF, đủ câu và ý nhỏ.
5. Chưa thực hiện trong phần 1: Đối chiếu và sửa VBT bài 36–44 với bản PDF, đủ câu và ý nhỏ.
6. Chưa thực hiện trong phần 1: Rà soát độc lập ngân hàng SGK/VBT, đáp án, lời giải, hình; soạn và kiểm tra đúng 38 câu nâng cao.
7. Chưa thực hiện trong phần 1: Xây dựng giao diện Android native màu hồng, phân mục Học/Luyện tập/Thử thách, nhập số, đặt tính, chọn đáp án, sơ đồ, dụng cụ đo tương tác.
8. Chưa thực hiện trong phần 1: Xây dựng trò chơi native 2D theo chủ đề từng bài, câu toán trực tiếp điều khiển hành trình, thưởng thường và kho báu nâng cao.
9. Chưa thực hiện trong phần 1: Kiểm thử chức năng, lưu/khôi phục trạng thái, câu sai, điểm thưởng, thử giao diện màn hình thật/giả lập; sửa lỗi.
10. Chưa thực hiện trong phần 1: Build APK bằng GitHub Actions giống LittleEnglish: SDK 35, JDK 17, Gradle 8.9; kiểm tra gói, chữ ký, cài đặt, chạy thử, bàn giao artifact.

## Điều kiện chuyển phần

Phần 1 hoàn thành khi đối chiếu đủ 19 tiêu đề, trang đầu/cuối và khóa đúng phiên bản nguồn. Các phần tiếp theo chỉ đánh dấu hoàn thành khi kiểm tra được sản phẩm của chính phần đó; không coi kiểm tra cấu trúc, commit hoặc build thành công là bằng chứng đầy đủ về nội dung hay chất lượng chạy thực tế.
