 🏥 Health Tracker — Hướng dẫn sử dụng

> Ứng dụng theo dõi sức khỏe cá nhân viết bằng  Java Swing + MySQL (XAMPP) .

---

 📋 Mục lục

1. [Yêu cầu hệ thống](1-yêu-cầu-hệ-thống)
2. [Cài đặt & Khởi động](2-cài-đặt--khởi-động)
3. [Đăng ký & Đăng nhập](3-đăng-ký--đăng-nhập)
4. [Tổng quan Dashboard](4-tổng-quan-dashboard)
5. [Theo dõi Cân nặng & BMI](5-theo-dõi-cân-nặng--bmi)
6. [Theo dõi Uống nước](6-theo-dõi-uống-nước)
7. [Theo dõi Giấc ngủ](7-theo-dõi-giấc-ngủ)
8. [Theo dõi Chiều cao](8-theo-dõi-chiều-cao)
9. [Health Score](9-health-score)
10. [Cấu trúc dự án](10-cấu-trúc-dự-án)
11. [Database Schema](11-database-schema)

---

 1. Yêu cầu hệ thống

| Thành phần | Phiên bản tối thiểu |
|---|---|
| Java JDK | 17+ |
| XAMPP (MySQL) | 8.0+ |
| MySQL Connector/J | 9.x (có sẵn trong thư mục `lib/`) |

---

 2. Cài đặt & Khởi động

 Bước 1 — Khởi động MySQL (XAMPP)
1. Mở  XAMPP Control Panel 
2. Click  Start  ở dòng  MySQL 
3. Đảm bảo cột  Port  hiển thị `3306`

 Bước 2 — Tạo Database
1. Mở trình duyệt → vào `http:localhost/phpmyadmin`
2. Click  New  → nhập tên `health_tracker` →  Create 
3. Chọn database `health_tracker` → tab  SQL 
4. Copy toàn bộ nội dung file `database.sql` dán vào → nhấn  Go 

 Bước 3 — Chạy ứng dụng
-  Trong Eclipse / IntelliJ : Run file `Main.java`
-  Dòng lệnh :
  ```bash
  javac -cp "lib/*" -d bin src/ /*.java
  java -cp "bin;lib/*" Main
  ```

> [!IMPORTANT]
> Phải khởi động MySQL trong XAMPP  trước  khi chạy ứng dụng, nếu không sẽ báo lỗi kết nối.

---

 3. Đăng ký & Đăng nhập

 Đăng ký tài khoản mới
1. Tại màn hình đăng nhập, click  Đăng ký 
2. Điền đầy đủ các trường:

| Trường | Yêu cầu |
|---|---|
| Tên đăng nhập | Không dấu, không khoảng trắng |
| Mật khẩu | Tối thiểu 6 ký tự |
| Họ và tên | Tên đầy đủ |
| Ngày sinh | Định dạng `yyyy-MM-dd` (VD: `2000-05-15`) |
| Giới tính | Nam / Nữ |
| Chiều cao (cm) | Từ 50 đến 250 cm |

3. Click  Đăng ký 

 Đăng nhập
- Nhập tên đăng nhập + mật khẩu → Click  Đăng nhập 

---

 4. Tổng quan Dashboard

Sau khi đăng nhập, màn hình  Dashboard  hiển thị 4 thẻ tóm tắt:

| Thẻ | Thông tin hiển thị |
|---|---|
|  [=] Cân nặng  | Cân nặng gần nhất + chỉ số BMI + phân loại |
|  [~] Nước hôm nay  | Tổng ml đã uống / mục tiêu 2000 ml |
|  [z] Giấc ngủ  | Số giờ ngủ gần nhất + chất lượng |
|  [↕] Chiều cao  | Chiều cao gần nhất + phân loại |

Phần  Thao tác nhanh  ở dưới cho phép chuyển nhanh đến từng chức năng bằng 1 click.

---

 5. Theo dõi Cân nặng & BMI

 Menu sidebar:  `[=] Cân nặng`

 Thêm bản ghi
1. Nhập  Cân nặng (kg)  — hợp lệ từ 20 đến 300 kg
2. BMI được  tính toán tự động  ngay khi bạn gõ (cần đã có chiều cao)
3. Kiểm tra  Ngày  (mặc định là hôm nay, định dạng `yyyy-MM-dd`)
4. Thêm  Ghi chú  nếu muốn
5. Click  Lưu cân nặng 

 Bảng phân loại BMI

| Chỉ số BMI | Phân loại | Màu |
|---|---|---|
| < 18.5 | Gầy | Cyan |
| 18.5 – 24.9 | Bình thường | Xanh lá |
| 25 – 29.9 | Thừa cân | Cam |
| ≥ 30 | Béo phì | Đỏ |

 Xóa bản ghi
1. Click vào dòng muốn xóa trong bảng lịch sử
2. Click  Xóa dòng đã chọn  → Xác nhận

---

 6. Theo dõi Uống nước

 Menu sidebar:  `[~] Uống nước`

- Click các nút nhanh:  +100 ml ,  +200 ml ,  +250 ml ,  +500 ml 
- Hoặc nhập số ml tùy chỉnh vào ô rồi click  + Thêm 
- Vòng tròn tiến độ hiển thị tổng uống so với mục tiêu  2000 ml/ngày 
- Bảng bên phải liệt kê từng lần uống trong ngày (có thể xóa từng dòng)

> [!TIP]
> 2000 ml ≈ 8 ly nước — đây là mức khuyến nghị cơ bản mỗi ngày.

---

 7. Theo dõi Giấc ngủ

 Menu sidebar:  `[z] Giấc ngủ`

1. Nhập  Giờ ngủ  và  Giờ thức  theo định dạng `yyyy-MM-dd HH:mm`
   - Ví dụ: `2025-05-03 22:30` và `2025-05-04 06:15`
2. Chọn  Chất lượng giấc ngủ : Tệ / Bình thường / Tốt / Rất tốt
3. Thêm  Ghi chú  (không bắt buộc)
4. Click  Lưu giấc ngủ 

Ứng dụng tự động tính  số giờ ngủ  và hiển thị trong bảng lịch sử 14 ngày gần nhất.

---

 8. Theo dõi Chiều cao

 Menu sidebar:  `[↕] Chiều cao`

- Ghi lại chiều cao định kỳ theo thời gian
- Bản ghi mới nhất sẽ được dùng để tính BMI ở mục Cân nặng
- Xem bảng phân loại chiều cao theo độ tuổi & giới tính

> [!NOTE]
> Chiều cao mặc định khi đăng ký cũng được lưu và sẽ được dùng để tính BMI nếu chưa có bản ghi trong `height_log`.

---

 9. Health Score

 Menu sidebar:  `[★] Health Score`

Điểm sức khỏe tổng hợp tính từ  4 tiêu chí , mỗi tiêu chí tối đa  25 điểm :

| Tiêu chí | Điều kiện đạt điểm tối đa |
|---|---|
| BMI  | Nằm trong khoảng 18.5 – 24.9 |
| Nước  | Uống ≥ 2000 ml hôm nay |
| Giấc ngủ  | Ngủ 7–9 giờ, chất lượng Tốt/Rất tốt |
| Chiều cao  | Đã có bản ghi chiều cao gần đây |

 Thang điểm

| Điểm | Đánh giá | Màu |
|---|---|---|
| < 40 | Cần cải thiện nhiều | Đỏ |
| 40 – 59 | Trung bình | Cam |
| 60 – 79 | Khá tốt | Vàng |
| ≥ 80 | Rất tốt | Xanh |

Điểm được hiển thị dưới dạng  vòng tròn động  kèm phân tích từng tiêu chí và lời khuyên cải thiện.

---

 10. Cấu trúc dự án

```
HealthTracker/
├── src/
│   ├── Main.java                   Điểm khởi chạy ứng dụng
│   ├── db/
│   │   ├── DatabaseConnection.java   Kết nối MySQL
│   │   └── DAO.java                  Tất cả các truy vấn SQL
│   ├── model/
│   │   ├── User.java
│   │   ├── WeightLog.java
│   │   ├── WaterLog.java
│   │   ├── SleepLog.java
│   │   └── HeightLog.java
│   ├── ui/
│   │   ├── LoginForm.java
│   │   ├── RegisterForm.java
│   │   ├── DashboardForm.java        Khung chính + sidebar
│   │   ├── WeightPanel.java
│   │   ├── WaterPanel.java
│   │   ├── SleepPanel.java
│   │   ├── HeightPanel.java
│   │   └── HealthScorePanel.java
│   └── util/
│       └── Theme.java                Màu sắc, font, component dùng chung
├── lib/
│   └── mysql-connector-j-*.jar       Driver kết nối MySQL
├── database.sql                      Script tạo toàn bộ database
└── README.md                         File này
```

---

 11. Database Schema

```sql
users        -- Tài khoản & thông tin người dùng
weight_log   -- Lịch sử cân nặng & chỉ số BMI
water_log    -- Lịch sử từng lần uống nước
sleep_log    -- Lịch sử giấc ngủ (giờ ngủ, giờ thức, chất lượng)
height_log   -- Lịch sử chiều cao
goals        -- Mục tiêu sức khỏe (dự phòng cho tính năng tương lai)
```

> [!NOTE]
> Tất cả bảng log đều có cột `user_id` liên kết với bảng `users`, đảm bảo dữ liệu hoàn toàn tách biệt giữa các tài khoản.

---

