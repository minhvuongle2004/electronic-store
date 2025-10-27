# Electronic Store - Hệ thống Quản lý Cửa hàng Phụ kiện Máy tính

Hệ thống quản lý cửa hàng bán phụ kiện máy tính trực tuyến, được xây dựng bằng Spring Boot và MySQL.

## Mục lục
- [Giới thiệu](#giới-thiệu)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Tính năng](#tính-năng)
- [Cài đặt](#cài-đặt)
- [Cấu hình](#cấu-hình)
- [Cấu trúc Database](#cấu-trúc-database)
- [API Endpoints](#api-endpoints)
- [Hướng dẫn sử dụng](#hướng-dẫn-sử-dụng)
- [Demo Account](#demo-account)

## Giới thiệu

Electronic Store là một hệ thống thương mại điện tử chuyên về phụ kiện máy tính, bao gồm:
- Tai nghe (Bluetooth, gaming, có dây)
- Chuột máy tính (gaming, văn phòng, không dây)
- Bàn phím (cơ, giả cơ, văn phòng)
- Loa (Bluetooth, vi tính)
- Phụ kiện khác (cáp sạc, USB, thẻ nhớ, hub)

Hệ thống hỗ trợ hai loại người dùng:
- **Admin**: Quản lý toàn bộ hệ thống
- **User**: Mua sắm và quản lý đơn hàng cá nhân

## Công nghệ sử dụng

### Backend
- **Java 21**
- **Spring Boot 3.2.0**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
  - Spring Mail
- **JWT (JSON Web Token)** - Xác thực và phân quyền
- **MySQL 8.0** - Database
- **Maven** - Build tool

### Frontend
- **Thymeleaf** - Template engine
- **HTML/CSS/JavaScript**
- **Bootstrap** - UI framework

### Payment Integration
- **MoMo Payment Gateway** - Thanh toán trực tuyến
- **COD (Cash on Delivery)** - Thanh toán khi nhận hàng

### Email Service
- **Gmail SMTP** - Gửi email xác thực và thông báo

## Tính năng

### Chức năng dành cho Admin

#### 1. Quản lý người dùng
- Xem danh sách người dùng (có phân trang và tìm kiếm)
- Xem chi tiết thông tin người dùng
- Chặn/mở chặn tài khoản người dùng
- Xóa người dùng (có hỗ trợ xóa hàng loạt)
- Xem thống kê người dùng theo thời gian

#### 2. Quản lý sản phẩm
- Thêm sản phẩm mới (có upload hình ảnh)
- Sửa thông tin sản phẩm
- Xóa sản phẩm
- Quản lý tồn kho
- Phân loại sản phẩm theo danh mục

#### 3. Quản lý danh mục
- Thêm/sửa/xóa danh mục sản phẩm
- Quản lý mô tả danh mục

#### 4. Quản lý đơn hàng
- Xem danh sách đơn hàng
- Xem chi tiết đơn hàng
- Cập nhật trạng thái đơn hàng:
  - PENDING (Chờ xử lý)
  - SHIPPED (Đang giao hàng)
  - COMPLETED (Hoàn thành)
  - CANCELED (Đã hủy)
- Theo dõi trạng thái thanh toán

#### 5. Quản lý khuyến mãi
- Tạo mã khuyến mãi mới
- Sửa thông tin khuyến mãi
- Xóa khuyến mãi
- Quản lý loại giảm giá:
  - PERCENT (Theo phần trăm)
  - FIXED (Số tiền cố định)
- Giới hạn số lần sử dụng
- Đặt ngày bắt đầu và kết thúc
- Đặt điều kiện đơn hàng tối thiểu

#### 6. Quản lý FAQs (Câu hỏi thường gặp)
- Thêm/sửa/xóa câu hỏi
- Phân loại theo danh mục
- Sắp xếp thứ tự hiển thị
- Bật/tắt câu hỏi

#### 7. Dashboard
- Thống kê tổng quan:
  - Doanh thu
  - Số đơn hàng
  - Số lượng người dùng
  - Số sản phẩm
- Biểu đồ và báo cáo

### Chức năng dành cho User

#### 1. Xác thực và tài khoản
- Đăng ký tài khoản mới
- Xác thực email (gửi link xác nhận qua email)
- Đăng nhập/Đăng xuất
- Quên mật khẩu (gửi link reset qua email)
- Quản lý thông tin cá nhân

#### 2. Quản lý sản phẩm
- Xem danh sách sản phẩm (có phân trang)
- Tìm kiếm sản phẩm
- Lọc sản phẩm theo danh mục
- Xem chi tiết sản phẩm

#### 3. Giỏ hàng
- Thêm sản phẩm vào giỏ hàng
- Cập nhật số lượng
- Xóa sản phẩm khỏi giỏ hàng
- Xem tổng tiền

#### 4. Đặt hàng
- Điền thông tin giao hàng:
  - Tên người nhận
  - Số điện thoại
  - Địa chỉ giao hàng
- Chọn phương thức thanh toán:
  - **MoMo** (thanh toán online)
  - **COD** (thanh toán khi nhận hàng)
- Áp dụng mã khuyến mãi
- Xác nhận đơn hàng

#### 5. Thanh toán
- Tích hợp MoMo Payment Gateway
- Xử lý callback từ MoMo
- Cập nhật trạng thái thanh toán tự động
- Hiển thị kết quả thanh toán

#### 6. Quản lý đơn hàng
- Xem lịch sử đơn hàng
- Theo dõi trạng thái đơn hàng
- Xem chi tiết đơn hàng

#### 7. Danh sách yêu thích (Wishlist)
- Thêm sản phẩm vào danh sách yêu thích
- Xóa khỏi danh sách yêu thích
- Xem danh sách sản phẩm yêu thích

#### 8. Khuyến mãi
- Xem danh sách mã khuyến mãi khả dụng
- Áp dụng mã khuyến mãi khi đặt hàng
- Theo dõi lịch sử sử dụng khuyến mãi

#### 9. FAQs
- Xem câu hỏi thường gặp
- Phân loại theo danh mục:
  - Sản phẩm & Đặt hàng
  - Thanh toán
  - Giao hàng
  - Đổi trả & Bảo hành
  - Tài khoản

## Cài đặt

### Yêu cầu hệ thống
- Java 21 hoặc cao hơn
- Maven 3.6+
- MySQL 8.0+
- Git

### Các bước cài đặt

#### 1. Clone repository
```bash
git clone https://github.com/minhvuongle2004/electronic-store.git
cd electronic-store
```

#### 2. Tạo database
```bash
mysql -u root -p
```

Trong MySQL shell:
```sql
CREATE DATABASE electronic_store;
USE electronic_store;
SOURCE electronic_store.sql;
```

Hoặc import file `electronic_store.sql` bằng các công cụ như MySQL Workbench, phpMyAdmin, HeidiSQL.

#### 3. Cấu hình application.properties

Mở file `src/main/resources/application.properties` và cập nhật các thông tin sau:

**Database Configuration:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/electronic_store
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

**Email Configuration (Gmail):**
```properties
spring.mail.username=YOUR_GMAIL_ADDRESS
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
```

Lưu ý: Để lấy Gmail App Password:
1. Truy cập: https://myaccount.google.com/apppasswords
2. Tạo App Password mới
3. Sao chép và dán vào file config

**MoMo Payment Configuration:**

Để test thanh toán MoMo, bạn có thể sử dụng thông tin test có sẵn trong file config hoặc đăng ký tài khoản test tại: https://developers.momo.vn/

#### 4. Build project
```bash
mvn clean install
```

#### 5. Chạy ứng dụng
```bash
mvn spring-boot:run
```

Hoặc:
```bash
java -jar target/store-0.0.1-SNAPSHOT.jar
```

#### 6. Truy cập ứng dụng

- **Homepage**: http://localhost:8080
- **Admin Panel**: http://localhost:8080/admin/dashboard
- **User Login**: http://localhost:8080/auth/login
- **Register**: http://localhost:8080/auth/register

## Cấu hình

### Port Configuration
Mặc định ứng dụng chạy trên port 8080. Để thay đổi, sửa trong `application.properties`:
```properties
server.port=8080
```

### File Upload Configuration
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
app.upload.path=uploads/products
```

### JWT Configuration
```properties
app.jwt.secret=YOUR_SECRET_KEY
app.jwt.expiration=86400000
```

### Session Configuration
```properties
server.servlet.session.timeout=30m
server.servlet.session.cookie.max-age=1800
```

## Cấu trúc Database

### Các bảng chính

#### 1. users
- Lưu thông tin người dùng
- Phân quyền: USER, ADMIN
- Trạng thái: ACTIVE, BLOCKED

#### 2. products
- Thông tin sản phẩm
- Liên kết với categories
- Quản lý tồn kho và giá

#### 3. categories
- Danh mục sản phẩm
- Mô tả danh mục

#### 4. cart_items
- Giỏ hàng của từng user
- Lưu số lượng sản phẩm

#### 5. orders
- Đơn hàng
- Trạng thái: PENDING, SHIPPED, COMPLETED, CANCELED
- Phương thức thanh toán: MOMO, COD
- Trạng thái thanh toán: UNPAID, PAID, FAILED

#### 6. order_items
- Chi tiết sản phẩm trong đơn hàng
- Số lượng và giá tại thời điểm đặt hàng

#### 7. payments
- Thông tin thanh toán
- Transaction ID từ MoMo
- Trạng thái: SUCCESS, FAILED, PENDING

#### 8. promotions
- Mã khuyến mãi
- Loại giảm giá: PERCENT, FIXED
- Điều kiện và giới hạn sử dụng

#### 9. user_promotions
- Lịch sử sử dụng khuyến mãi của user
- Liên kết với order

#### 10. wishlist
- Danh sách sản phẩm yêu thích của user

#### 11. faqs & faq_categories
- Câu hỏi thường gặp
- Phân loại theo danh mục

### Sơ đồ quan hệ
```
users (1) ---> (n) cart_items (n) ---> (1) products
users (1) ---> (n) orders
users (1) ---> (n) wishlist (n) ---> (1) products
users (1) ---> (n) user_promotions (n) ---> (1) promotions

orders (1) ---> (n) order_items (n) ---> (1) products
orders (1) ---> (1) payments
orders (n) ---> (1) promotions (via promotion_code)

products (n) ---> (1) categories

faq_categories (1) ---> (n) faqs
```

## API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập
- `GET /api/auth/verify-email` - Xác thực email
- `POST /api/auth/resend-verification` - Gửi lại email xác thực

### Admin APIs
- `GET /admin/dashboard` - Dashboard
- `GET /admin/users` - Quản lý người dùng
- `GET /admin/products` - Quản lý sản phẩm
- `GET /admin/categories` - Quản lý danh mục
- `GET /admin/orders` - Quản lý đơn hàng
- `GET /admin/promotions` - Quản lý khuyến mãi
- `GET /admin/faqs` - Quản lý FAQs

### User APIs
- `GET /user/products` - Danh sách sản phẩm
- `GET /user/product/{id}` - Chi tiết sản phẩm
- `GET /user/cart` - Giỏ hàng
- `POST /user/cart/add` - Thêm vào giỏ hàng
- `GET /user/orders` - Lịch sử đơn hàng
- `POST /user/order/create` - Tạo đơn hàng
- `GET /user/wishlist` - Danh sách yêu thích
- `POST /user/wishlist/add` - Thêm vào wishlist
- `GET /user/promotions` - Danh sách khuyến mãi
- `GET /user/faq` - Câu hỏi thường gặp
- `GET /user/profile` - Thông tin cá nhân

### Payment APIs
- `POST /payment/create` - Tạo thanh toán
- `POST /payment/momo/notify` - MoMo callback (webhook)
- `GET /payment/momo/return` - Redirect sau thanh toán

## Hướng dẫn sử dụng

### Đối với Admin

1. **Đăng nhập Admin**
   - Truy cập: http://localhost:8080/auth/login
   - Sử dụng tài khoản admin (xem phần Demo Account)

2. **Quản lý sản phẩm**
   - Vào "Sản phẩm" > "Thêm mới"
   - Điền thông tin: tên, mô tả, giá, tồn kho
   - Upload hình ảnh
   - Chọn danh mục
   - Lưu sản phẩm

3. **Quản lý khuyến mãi**
   - Vào "Khuyến mãi" > "Tạo mới"
   - Đặt mã khuyến mãi (CODE)
   - Chọn loại giảm giá
   - Đặt điều kiện và thời hạn
   - Kích hoạt khuyến mãi

4. **Quản lý đơn hàng**
   - Xem danh sách đơn hàng
   - Click vào đơn hàng để xem chi tiết
   - Cập nhật trạng thái giao hàng

### Đối với User

1. **Đăng ký tài khoản**
   - Truy cập: http://localhost:8080/auth/register
   - Điền thông tin đăng ký
   - Kiểm tra email để xác thực

2. **Mua sắm**
   - Duyệt sản phẩm trên homepage
   - Click vào sản phẩm để xem chi tiết
   - Chọn "Thêm vào giỏ hàng"

3. **Đặt hàng**
   - Vào giỏ hàng
   - Kiểm tra sản phẩm và số lượng
   - Click "Thanh toán"
   - Điền thông tin giao hàng
   - Nhập mã khuyến mãi (nếu có)
   - Chọn phương thức thanh toán
   - Xác nhận đơn hàng

4. **Thanh toán MoMo**
   - Sau khi tạo đơn, hệ thống redirect sang MoMo
   - Đăng nhập MoMo và xác nhận thanh toán
   - Sau khi thanh toán, hệ thống tự động cập nhật trạng thái

5. **Theo dõi đơn hàng**
   - Vào "Đơn hàng của tôi"
   - Xem trạng thái và chi tiết đơn hàng

## Demo Account

### Admin Account
```
Username: admin
Password: admin123
Email: admin@gmail.com
```

### User Account
```
Username: vuongdh12c1
Password: [xem trong database]
Email: vuong8aqhqlna@gmail.com
```

Hoặc bạn có thể đăng ký tài khoản mới.

## Lưu ý bảo mật

1. **Đổi JWT Secret**: Thay đổi `app.jwt.secret` trong production
2. **Đổi mật khẩu mặc định**: Đổi password của tài khoản admin
3. **Cấu hình HTTPS**: Sử dụng HTTPS trong production
4. **Database Password**: Không commit password thật lên Git
5. **Email Credentials**: Sử dụng App Password, không dùng mật khẩu chính
6. **MoMo Credentials**: Sử dụng credentials production khi deploy

## Cấu trúc thư mục

```
electronic-store/
├── src/
│   ├── main/
│   │   ├── java/com/electronic/store/
│   │   │   ├── config/          # Cấu hình Spring
│   │   │   ├── controller/      # REST Controllers
│   │   │   │   ├── admin/       # Admin Controllers
│   │   │   │   └── user/        # User Controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA Entities
│   │   │   ├── repository/      # Spring Data Repositories
│   │   │   ├── security/        # Security & JWT
│   │   │   ├── service/         # Business Logic
│   │   │   └── util/            # Utilities
│   │   └── resources/
│   │       ├── static/          # CSS, JS, Images
│   │       ├── templates/       # Thymeleaf Templates
│   │       └── application.properties
│   └── test/                    # Unit Tests
├── uploads/                     # Uploaded Files
├── electronic_store.sql         # Database Schema
├── pom.xml                      # Maven Configuration
└── README.md                    # This file
```

## Troubleshooting

### Lỗi kết nối database
```
Error: Access denied for user 'root'@'localhost'
```
**Giải pháp**: Kiểm tra username và password trong `application.properties`

### Lỗi gửi email
```
Error: Authentication failed
```
**Giải pháp**:
- Bật 2-Step Verification cho Gmail
- Tạo App Password tại: https://myaccount.google.com/apppasswords

### Lỗi upload file
```
Error: File size exceeds limit
```
**Giải pháp**: Tăng giới hạn trong `application.properties`:
```properties
spring.servlet.multipart.max-file-size=10MB
```

### Port đã được sử dụng
```
Error: Port 8080 is already in use
```
**Giải pháp**:
- Đổi port khác trong `application.properties`
- Hoặc kill process đang dùng port 8080

## Tính năng sắp tới

- [ ] Đánh giá và nhận xét sản phẩm
- [ ] Chat support realtime
- [ ] Thống báo realtime
- [ ] Export đơn hàng ra Excel/PDF
- [ ] Quản lý kho hàng nâng cao
- [ ] Tích hợp thêm cổng thanh toán (VNPay, ZaloPay)
- [ ] Mobile responsive tốt hơn
- [ ] API documentation với Swagger

## Liên hệ

- **Author**: Minh Vương
- **Email**: vuongleminh380@gmail.com
- **GitHub**: https://github.com/minhvuongle2004
- **Repository**: https://github.com/minhvuongle2004/electronic-store

## License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.
