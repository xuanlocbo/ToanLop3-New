# CI liên tục — không Codex, không API AI trả phí

Một PR kích hoạt một workflow gồm verify → migration → acceptance. Mỗi job chỉ chạy khi job trước thành công. Verify giữ nguyên kiểm thử hồi quy, biên dịch, APK, dữ liệu và Android. Migration tải đúng APK đã nghiệm thu và APK mới từ cùng workflow, kiểm thử các biện pháp bảo vệ và nâng cấp/phục hồi trên emulator. ci_candidate.py chỉ đăng ký ứng viên trong bộ nhớ CI, không sửa registry trên đĩa. Acceptance đối chiếu SHA, XML kiểm thử, ảnh và bằng chứng migration, tạo acceptance-p07.json và summary. Runner chỉ có quyền đọc. Không tự merge main, không cài lên điện thoại thật.

ChatGPT rà soát biên bản, hình ảnh, log; chỉ khi đạt mới đăng ký hash thật trong registry, lưu mốc và triển khai phần tiếp theo. Không tự động sửa mã bằng GitHub; khi lỗi, sửa từ cuộc trò chuyện và kiểm thử lại. Tác vụ ChatGPT định kỳ có thể nối lại nếu được truy cập GitHub. Không cần người dùng phê duyệt từng phần. Không tuyên bố bản debug là phát hành chính thức.

Các phần sau cập nhật baseline SHA/run-id, tên APK, phiên bản, phạm vi và số kiểm thử tối thiểu; giữ nguyên kiểm thử bảo vệ. Không giảm chất lượng để tăng tốc.

- Khắc phục CI bàn giao p07: run 45554825123 fail vì allowlist chưa cho phép hai file đăng ký p07 (registry và README). Kiểm tra lại trên chính artifact p07 run 45548847319 với SHA đã đăng ký; vẫn biên dịch, chạy đủ unit/smoke, migration và acceptance. Giới hạn thay đổi sau commit ứng dụng; registry chỉ được thêm đúng hash p07. Bridge có chế độ --registered-recheck chỉ cho cặp cuối đã đăng ký, không ghi đè hash. Phần sau dùng lại chế độ candidate mặc định và bỏ pin recheck p07 khi thiết lập workflow cho ứng viên mới; chưa chuyển phần nếu recheck chưa đạt.

- Recheck 45555229950 fail tại selector Bài 40 dù APK và guard đạt. Sửa tìm kiếm UI theo viewport trong .github/scripts/ui_scan.py, kiểm thử riêng bằng test_ui_scan.py; giữ nguyên bài kiểm thử chức năng. Guard cho phép đúng ba tệp hạ tầng kiểm thử, không đổi mã app/registry/APK. Phần 8 phải kế thừa commit đã sửa sau khi CI đạt và gỡ pin recheck p07 khi build ứng viên mới.

- Bản hợp nhất p15: CI biên dịch/đóng gói chính xác, sau đó Android regression và migration chạy độc lập từ cùng APK, acceptance chỉ chạy nếu cả hai đạt. Chuyển p07 → p15 chỉ được phép trong CI qua --consolidated-final đúng cặp và hash; registry trên đĩa không đổi trước nghiệm thu. Không có p08–p14 giả. Giữ bộ test đã có và bổ sung test chức năng.

- Final p15 dùng PR riêng trên base v1.2-part07-rainbow. Một job đóng gói và hai job Android/migration độc lập chạy từ đúng APK hash; acceptance yêu cầu cả hai. Bảo toàn registry p07 đến khi xác nhận bản p15. Không giảm bộ hồi quy cũ; không tự merge hoặc ký phát hành.
