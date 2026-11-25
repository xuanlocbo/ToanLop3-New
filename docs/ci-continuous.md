# CI liên tục — không Codex, không API AI trả phí

Một PR kích hoạt một workflow gồm verify → migration → acceptance. Mỗi job chỉ chạy khi job trước thành công. Verify giữ nguyên kiểm thử hồi quy, biên dịch, APK, dữ liệu và Android. Migration tải đúng APK đã nghiệm thu và APK mới từ cùng workflow, kiểm thử các biện pháp bảo vệ và nâng cấp/phục hồi trên emulator. ci_candidate.py chỉ đăng ký ứng viên trong bộ nhớ CI, không sửa registry trên đĩa. Acceptance đối chiếu SHA, XML kiểm thử, ảnh và bằng chứng migration, tạo acceptance-p07.json và summary. Runner chỉ có quyền đọc. Không tự merge main, không cài lên điện thoại thật.

ChatGPT rà soát biên bản, hình ảnh, log; chỉ khi đạt mới đăng ký hash thật trong registry, lưu mốc và triển khai phần tiếp theo. Không tự động sửa mã bằng GitHub; khi lỗi, sửa từ cuộc trò chuyện và kiểm thử lại. Tác vụ ChatGPT định kỳ có thể nối lại nếu được truy cập GitHub. Không cần người dùng phê duyệt từng phần. Không tuyên bố bản debug là phát hành chính thức.

Các phần sau cập nhật baseline SHA/run-id, tên APK, phiên bản, phạm vi và số kiểm thử tối thiểu; giữ nguyên kiểm thử bảo vệ. Không giảm chất lượng để tăng tốc.
