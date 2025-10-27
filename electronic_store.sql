-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               8.0.41 - MySQL Community Server - GPL
-- Server OS:                    Win64
-- HeidiSQL Version:             12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for electronic_store
CREATE DATABASE IF NOT EXISTS `electronic_store` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `electronic_store`;

-- Dumping structure for table electronic_store.cart_items
CREATE TABLE IF NOT EXISTS `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `quantity` int DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.cart_items: ~0 rows (approximately)

-- Dumping structure for table electronic_store.categories
CREATE TABLE IF NOT EXISTS `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.categories: ~5 rows (approximately)
INSERT INTO `categories` (`id`, `name`, `description`) VALUES
	(1, 'Tai nghe', 'Các loại tai nghe có dây và không dây'),
	(2, 'Chuột máy tính', 'Chuột gaming, văn phòng, không dây'),
	(3, 'Bàn phím', 'Bàn phím cơ, giả cơ, văn phòng'),
	(4, 'Loa', 'Loa Bluetooth, loa vi tính'),
	(5, 'Phụ kiện khác', 'Cáp sạc, USB, thẻ nhớ, phụ kiện khác');

-- Dumping structure for table electronic_store.faqs
CREATE TABLE IF NOT EXISTS `faqs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint DEFAULT NULL,
  `question` text NOT NULL,
  `answer` text NOT NULL,
  `display_order` int DEFAULT '0',
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `faqs_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `faq_categories` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.faqs: ~15 rows (approximately)
INSERT INTO `faqs` (`id`, `category_id`, `question`, `answer`, `display_order`, `status`, `created_at`, `updated_at`) VALUES
	(1, 1, 'Làm thế nào để đặt hàng trên website?', 'Bạn có thể đặt hàng bằng cách: 1) Chọn sản phẩm → 2) Thêm vào giỏ hàng → 3) Vào giỏ hàng → 4) Điền thông tin giao hàng → 5) Chọn hình thức thanh toán → 6)\r\n  Xác nhận đơn hàng.', 1, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(2, 1, 'Có thể hủy đơn hàng sau khi đặt không?', 'Bạn có thể hủy đơn hàng trong vòng 30 phút sau khi đặt hàng. Sau thời gian này, vui lòng liên hệ hotline 0123456789 để được hỗ trợ.', 2, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(3, 1, 'Sản phẩm có chính hãng không?', 'Tất cả sản phẩm tại Electronic Store đều là hàng chính hãng 100%, có tem bảo hành và hóa đơn VAT đầy đủ.', 3, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(4, 2, 'Website có những hình thức thanh toán nào?', 'Chúng tôi hỗ trợ 2 hình thức thanh toán: 1) Thanh toán online qua MoMo, 2) Thanh toán khi nhận hàng (COD).', 1, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(5, 2, 'Thanh toán MoMo có an toàn không?', 'Thanh toán qua MoMo hoàn toàn an toàn với công nghệ mã hóa SSL 256-bit. Thông tin thẻ của bạn được bảo mật tuyệt đối.', 2, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(6, 2, 'Có thể thanh toán bằng thẻ tín dụng không?', 'Hiện tại chúng tôi chưa hỗ trợ thanh toán trực tiếp bằng thẻ tín dụng. Bạn có thể sử dụng MoMo hoặc COD.', 3, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(7, 3, 'Thời gian giao hàng bao lâu?', 'Thời gian giao hàng: Nội thành TP.HCM: 1-2 ngày, Các tỉnh thành khác: 2-5 ngày làm việc (không tính thứ 7, CN).', 1, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(8, 3, 'Phí giao hàng như thế nào?', 'Miễn phí giao hàng cho đơn hàng từ 500.000đ trở lên. Đơn hàng dưới 500.000đ phí ship 30.000đ.', 2, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(9, 3, 'Có giao hàng toàn quốc không?', 'Có, chúng tôi giao hàng toàn quốc qua đối tác vận chuyển uy tín như Giao Hàng Nhanh, Viettel Post.', 3, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(10, 4, 'Chính sách đổi trả như thế nào?', 'Đổi trả trong 7 ngày kể từ ngày nhận hàng nếu sản phẩm lỗi do nhà sản xuất. Sản phẩm phải còn nguyên vẹn, chưa qua sử dụng.', 1, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(11, 4, 'Sản phẩm được bảo hành bao lâu?', 'Tùy theo từng sản phẩm: Phụ kiện máy tính: 6-12 tháng, Thiết bị điện tử: 12-24 tháng. Thông tin cụ thể có trong mô tả sản phẩm.', 2, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(12, 4, 'Làm thế nào để bảo hành sản phẩm?', 'Mang sản phẩm và hóa đơn đến cửa hàng hoặc gửi về địa chỉ: 123 Đường ABC, Quận XYZ, TP.HCM. Hotline: 0123456789.', 3, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(13, 5, 'Làm thế nào để đăng ký tài khoản?', 'Click vào "Đăng ký" ở góc trên bên phải → Điền thông tin: họ tên, email, mật khẩu → Click "Đăng ký". Hệ thống sẽ gửi email xác nhận.', 1, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(14, 5, 'Quên mật khẩu phải làm sao?', 'Click "Quên mật khẩu" tại trang đăng nhập → Nhập email → Kiểm tra email để nhận link đặt lại mật khẩu.', 2, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(15, 5, 'Có thể thay đổi thông tin cá nhân không?', 'Có, bạn đăng nhập → Vào "Thông tin cá nhân" → Chỉnh sửa thông tin → Lưu thay đổi.', 3, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34');

-- Dumping structure for table electronic_store.faq_categories
CREATE TABLE IF NOT EXISTS `faq_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `icon` varchar(50) DEFAULT NULL,
  `display_order` int DEFAULT '0',
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.faq_categories: ~2 rows (approximately)
INSERT INTO `faq_categories` (`id`, `name`, `icon`, `display_order`, `status`, `created_at`, `updated_at`) VALUES
	(1, 'Sản phẩm & Đặt hàng', 'fas fa-box', 1, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(2, 'Thanh toán', 'fas fa-credit-card', 2, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(3, 'Giao hàng', 'fas fa-shipping-fast', 3, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(4, 'Đổi trả & Bảo hành', 'fas fa-undo', 4, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34'),
	(5, 'Tài khoản', 'fas fa-user', 5, 'ACTIVE', '2025-09-24 06:05:34', '2025-09-24 06:05:34');

-- Dumping structure for table electronic_store.orders
CREATE TABLE IF NOT EXISTS `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `total_price` double DEFAULT NULL,
  `status` enum('PENDING','SHIPPED','COMPLETED','CANCELED') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT 'PENDING',
  `payment_method` enum('MOMO','COD') DEFAULT 'MOMO',
  `payment_status` enum('UNPAID','PAID','FAILED') DEFAULT 'UNPAID',
  `promotion_code` varchar(50) DEFAULT NULL,
  `discount_amount` double DEFAULT '0',
  `shipping_name` varchar(255) DEFAULT NULL COMMENT 'Tên người nhận',
  `shipping_phone` varchar(20) DEFAULT NULL COMMENT 'Số điện thoại người nhận',
  `shipping_address` text COMMENT 'Địa chỉ giao hàng',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `FK_orders_order_items` (`order_id`),
  KEY `idx_orders_promotion_code` (`promotion_code`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.orders: ~17 rows (approximately)
INSERT INTO `orders` (`id`, `user_id`, `order_id`, `order_date`, `total_price`, `status`, `payment_method`, `payment_status`, `promotion_code`, `discount_amount`, `shipping_name`, `shipping_phone`, `shipping_address`) VALUES
	(1, 4, 'ORDER_1758436641847_4', '2025-09-21 06:37:22', 28990000, 'SHIPPED', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(2, 4, 'ORDER_1758437173435_4', '2025-09-21 06:46:13', 28990000, 'PENDING', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(3, 4, 'ORDER_1758438125594_4', '2025-09-21 07:02:06', 28990000, 'SHIPPED', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(4, 4, 'ORDER_1758438287577_4', '2025-09-21 07:04:48', 28990000, 'PENDING', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(5, 4, 'ORDER_1758438328172_4', '2025-09-21 07:05:28', 1290000, 'PENDING', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(6, 4, 'ORDER_1758438675575_4', '2025-09-21 07:11:16', 5990000, 'PENDING', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(7, 4, 'ORDER_1758438939086_4', '2025-09-21 07:15:39', 1890000, 'PENDING', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(8, 4, 'ORDER_1758439492055_4', '2025-09-21 07:24:52', 1890000, 'CANCELED', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(9, 4, 'ORDER_1758439658558_4', '2025-09-21 07:27:39', 5990000, 'PENDING', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(10, 4, 'ORDER_1758508080784_4', '2025-09-22 02:28:01', 1290000, 'PENDING', 'MOMO', 'UNPAID', NULL, 0, NULL, NULL, NULL),
	(11, 4, 'ORDER_1758512184212_4', '2025-09-22 03:36:24', 5990000, 'SHIPPED', 'MOMO', 'PAID', NULL, 0, NULL, NULL, NULL),
	(12, 4, 'ORDER_1758513287410_4', '2025-09-22 03:54:47', 1290000, 'PENDING', 'MOMO', 'PAID', NULL, 0, NULL, NULL, NULL),
	(13, 4, 'ORDER_1758513823311_4', '2025-09-22 04:03:43', 480000, 'SHIPPED', 'MOMO', 'PAID', NULL, 0, 'Vương Lê', '0353234113', 'Bắc Từ Liêm'),
	(14, 4, 'ORDER_1758544981661_4', '2025-09-22 12:43:02', 1290000, 'SHIPPED', 'MOMO', 'PAID', NULL, 0, 'Vương Lê', '0353234113', 'Bắc Từ Liêm'),
	(15, 4, 'ORDER_1758596772726_4', '2025-09-23 03:06:13', 5740000, 'SHIPPED', 'MOMO', 'PAID', 'VIP15', 250000, 'Vương Lê', '0353234113', 'Bắc Từ Liêm'),
	(16, 4, 'ORDER_1758605979096_4', '2025-09-23 05:39:39', 28740000, 'PENDING', 'MOMO', 'PAID', 'VIP15', 250000, 'Vương Lê', '0353234113', 'Bắc Từ Liêm'),
	(17, 4, 'ORDER_1758608153199_4', '2025-09-23 06:15:53', 28890000, 'PENDING', 'MOMO', 'PAID', 'STUDENT10', 100000, 'Vương Lê', '0353234113', 'Bắc Từ Liêm'),
	(18, 4, 'ORDER_1758609442460_4', '2025-09-23 06:37:22', 28740000, 'PENDING', 'MOMO', 'FAILED', 'VIP15', 250000, 'Vương Lê', '0353234113', 'Bắc Từ Liêm'),
	(19, 4, 'ORDER_1758609508876_4', '2025-09-23 06:38:29', 28990000, 'PENDING', 'MOMO', 'PAID', NULL, 0, 'Vương Lê', '0353234113', 'Bắc Từ Liêm'),
	(20, 4, 'ORDER_1758609900106_4', '2025-09-23 06:45:00', 28990000, 'PENDING', 'MOMO', 'FAILED', NULL, 0, 'Vương Lê', '0353234113', 'Bắc Từ Liêm'),
	(21, 4, 'ORDER_1758623971938_4', '2025-09-23 10:39:32', 28740000, 'PENDING', 'MOMO', 'PAID', 'VIP15', 250000, 'Vương Lê', '0353234113', 'Bắc Từ Liêm');

-- Dumping structure for table electronic_store.order_items
CREATE TABLE IF NOT EXISTS `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `quantity` int NOT NULL,
  `price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.order_items: ~17 rows (approximately)
INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `quantity`, `price`) VALUES
	(1, 1, 26, 1, 28990000),
	(2, 2, 26, 1, 28990000),
	(3, 3, 26, 1, 28990000),
	(4, 4, 26, 1, 28990000),
	(5, 5, 1, 1, 1290000),
	(6, 6, 2, 1, 5990000),
	(7, 7, 3, 1, 1890000),
	(8, 8, 11, 1, 1890000),
	(9, 9, 2, 1, 5990000),
	(10, 10, 1, 1, 1290000),
	(11, 11, 2, 1, 5990000),
	(12, 12, 1, 1, 1290000),
	(13, 13, 10, 1, 450000),
	(14, 14, 1, 1, 1290000),
	(15, 15, 2, 1, 5990000),
	(16, 16, 26, 1, 28990000),
	(17, 17, 26, 1, 28990000),
	(18, 18, 26, 1, 28990000),
	(19, 19, 26, 1, 28990000),
	(20, 20, 26, 1, 28990000),
	(21, 21, 26, 1, 28990000);

-- Dumping structure for table electronic_store.payments
CREATE TABLE IF NOT EXISTS `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `status` enum('SUCCESS','FAILED','PENDING') DEFAULT 'PENDING',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `transaction_id` (`transaction_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.payments: ~19 rows (approximately)
INSERT INTO `payments` (`id`, `order_id`, `transaction_id`, `amount`, `status`, `created_at`) VALUES
	(1, 1, 'REQ_1758436641899', 28990000, 'PENDING', '2025-09-21 06:37:23'),
	(2, 2, 'REQ_1758437173480', 28990000, 'PENDING', '2025-09-21 06:46:13'),
	(3, 3, 'REQ_1758438125642', 28990000, 'PENDING', '2025-09-21 07:02:06'),
	(4, 4, 'REQ_1758438287591', 28990000, 'PENDING', '2025-09-21 07:04:48'),
	(5, 5, 'REQ_1758438328219', 1290000, 'PENDING', '2025-09-21 07:05:29'),
	(6, 6, 'REQ_1758438675620', 5990000, 'PENDING', '2025-09-21 07:11:16'),
	(7, 7, 'REQ_1758438939138', 1890000, 'PENDING', '2025-09-21 07:15:39'),
	(8, 8, 'REQ_1758439492106', 1890000, 'PENDING', '2025-09-21 07:24:52'),
	(9, 9, 'REQ_1758439658615', 5990000, 'PENDING', '2025-09-21 07:27:39'),
	(10, 10, 'REQ_1758508080843', 1290000, 'PENDING', '2025-09-22 02:28:03'),
	(11, 11, '4579291641', 5990000, 'SUCCESS', '2025-09-22 03:36:25'),
	(12, 12, '4580872862', 1290000, 'SUCCESS', '2025-09-22 03:54:49'),
	(13, 13, '4580864311', 480000, 'SUCCESS', '2025-09-22 04:03:45'),
	(14, 14, '4581118494', 1290000, 'SUCCESS', '2025-09-22 12:43:02'),
	(15, 15, '4581119373', 5740000, 'SUCCESS', '2025-09-23 03:06:14'),
	(16, 16, '4581152527', 28740000, 'SUCCESS', '2025-09-23 05:39:40'),
	(17, 17, '4581100038', 28890000, 'SUCCESS', '2025-09-23 06:15:54'),
	(18, 18, '4581100099', 28740000, 'FAILED', '2025-09-23 06:37:24'),
	(19, 19, '4581100110', 28990000, 'SUCCESS', '2025-09-23 06:38:31'),
	(20, 20, '1758609933116', 28990000, 'FAILED', '2025-09-23 06:45:01'),
	(21, 21, '4581242031', 28740000, 'SUCCESS', '2025-09-23 10:39:33');

-- Dumping structure for table electronic_store.products
CREATE TABLE IF NOT EXISTS `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `description` text,
  `price` double DEFAULT NULL,
  `stock` int DEFAULT '0',
  `image_url` varchar(255) DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.products: ~26 rows (approximately)
INSERT INTO `products` (`id`, `name`, `description`, `price`, `stock`, `image_url`, `category_id`, `created_at`, `updated_at`) VALUES
	(1, 'Tai nghe Bluetooth Sony WH-CH510', 'Tai nghe không dây, pin 35h', 1290000, 50, '/uploads/products/5e236a07-c065-4835-bd45-754647f7a2fb.png', 1, '2025-09-19 15:28:15', '2025-09-24 04:39:06'),
	(2, 'Tai nghe AirPods Pro 2', 'Tai nghe Apple, chống ồn chủ động', 5990000, 30, '/uploads/products/d9d2ca33-4398-489d-aed1-32d2c402760a.jpg', 1, '2025-09-19 15:28:15', '2025-09-24 04:40:18'),
	(3, 'Tai nghe Gaming Razer Kraken', 'Tai nghe gaming, âm thanh vòm 7.1', 1890000, 40, '/uploads/products/9b993c34-9c45-415d-b229-d78c36b8e928.jpg', 1, '2025-09-19 15:28:15', '2025-09-24 04:40:45'),
	(4, 'Tai nghe Samsung Galaxy Buds2', 'Tai nghe TWS chống ồn', 2990000, 35, '/uploads/products/bde2fec8-2efc-4887-b12c-056770c13663.png', 1, '2025-09-19 15:28:15', '2025-09-24 04:41:08'),
	(5, 'Tai nghe có dây JBL C100', 'Tai nghe nhét tai giá rẻ', 299000, 100, '/uploads/products/cd096854-02dc-4ae0-8a92-78131d95f026.jpg', 1, '2025-09-19 15:28:15', '2025-09-24 04:41:25'),
	(6, 'Chuột Logitech M185', 'Chuột không dây văn phòng', 299000, 80, '/uploads/products/257542fa-6b47-4dc9-b291-d2631047745b.jpg', 2, '2025-09-19 15:28:15', '2025-09-24 04:41:48'),
	(7, 'Chuột Gaming Logitech G102', 'Chuột gaming LED RGB', 499000, 60, '/uploads/products/607db290-45a1-454e-b3ed-6674bfb0b50e.jpg', 2, '2025-09-19 15:28:15', '2025-09-24 04:42:14'),
	(8, 'Chuột Razer DeathAdder Essential', 'Chuột gaming huyền thoại', 690000, 45, '/uploads/products/6f2678f3-6fe4-44b0-9d3c-9bb285d1826c.png', 2, '2025-09-19 15:28:15', '2025-09-24 04:42:37'),
	(9, 'Chuột Apple Magic Mouse 2', 'Chuột Apple cảm ứng', 2190000, 25, '/uploads/products/3363c1ba-c2e1-4fc8-9126-b1b95741ed41.png', 2, '2025-09-19 15:28:15', '2025-09-24 04:43:03'),
	(10, 'Chuột Microsoft Bluetooth Mouse', 'Chuột nhỏ gọn, kết nối Bluetooth', 450000, 55, '/uploads/products/0f39de20-db23-452c-a857-da9d5111cd10.jpg', 2, '2025-09-19 15:28:15', '2025-09-24 04:43:27'),
	(11, 'Bàn phím cơ Akko 3068B Plus', 'Bàn phím cơ không dây, hot-swap', 1890000, 35, '/uploads/products/cab9655c-be3b-479e-9f03-2bb6fe0a6f0e.jpg', 3, '2025-09-19 15:28:15', '2025-09-24 04:43:47'),
	(12, 'Bàn phím Logitech K120', 'Bàn phím văn phòng, giá rẻ', 159000, 100, '/uploads/products/a1bfa1ce-9414-4107-ac4b-780f046d0419.jpg', 3, '2025-09-19 15:28:15', '2025-09-24 04:44:06'),
	(13, 'Bàn phím cơ Keychron K2', 'Bàn phím cơ Bluetooth, hot-swap', 2390000, 20, '/uploads/products/0452dab5-5528-4eda-8015-5d64e72c7e78.jpg', 3, '2025-09-19 15:28:15', '2025-09-24 04:44:26'),
	(14, 'Bàn phím cơ Ducky One 2 Mini', 'Bàn phím 60%, switch Cherry MX', 2490000, 15, '/uploads/products/467f832b-e1bc-4172-ac1b-2a54342b1c2e.png', 3, '2025-09-19 15:28:15', '2025-09-24 04:44:45'),
	(15, 'Bàn phím giả cơ DareU LK185', 'Bàn phím gaming giá rẻ', 399000, 70, '/uploads/products/5296a805-75a9-4f85-b548-5ab4ee0c5181.jpg', 3, '2025-09-19 15:28:15', '2025-09-24 04:45:06'),
	(16, 'Loa Bluetooth JBL Flip 6', 'Loa di động, chống nước IPX7', 2490000, 40, '/uploads/products/ca3a3061-b7f3-409a-8841-dd2966a00623.jpg', 4, '2025-09-19 15:28:15', '2025-09-24 04:45:26'),
	(17, 'Loa Bluetooth Sony SRS-XB23', 'Loa Extra Bass, chống nước', 1990000, 35, '/uploads/products/8c9b0b34-7aa7-4f4a-87cc-1ee03f255f4c.jpg', 4, '2025-09-19 15:28:15', '2025-09-24 04:45:46'),
	(18, 'Loa Bluetooth Anker Soundcore 2', 'Loa giá rẻ, pin 24h', 890000, 50, '/uploads/products/2da032e1-f4e7-47e9-941c-006cb3ee9559.jpg', 4, '2025-09-19 15:28:15', '2025-09-24 04:46:04'),
	(19, 'Loa Bluetooth Bose SoundLink Flex', 'Âm thanh cao cấp, chống nước', 3990000, 20, '/uploads/products/2e410caa-21f1-4d52-b8ba-429f845e5111.jpg', 4, '2025-09-19 15:28:15', '2025-09-24 04:46:30'),
	(20, 'Loa vi tính Logitech Z313', 'Loa 2.1 công suất 25W', 990000, 45, '/uploads/products/d22d96e8-6f83-4c17-b18f-fb633482d511.png', 4, '2025-09-19 15:28:15', '2025-09-24 04:46:57'),
	(21, 'Cáp sạc Anker PowerLine III', 'Cáp USB-C to Lightning 1m', 390000, 120, '/uploads/products/bdf9e71c-6a6e-41ff-8164-9b8bc72c6301.png', 5, '2025-09-19 15:28:15', '2025-09-24 04:47:14'),
	(22, 'Sạc dự phòng Xiaomi 20000mAh', 'Sạc nhanh 18W QC3.0', 590000, 75, '/uploads/products/7dddd9dc-4afb-4e7d-bbd6-077f2ddfadcf.jpg', 5, '2025-09-19 15:28:15', '2025-09-24 04:47:30'),
	(23, 'Thẻ nhớ SanDisk 64GB', 'Thẻ microSDXC tốc độ cao', 290000, 150, '/uploads/products/f34490dc-d131-4c22-b67f-76698424abd6.jpg', 5, '2025-09-19 15:28:15', '2025-09-24 04:47:46'),
	(24, 'USB Kingston 32GB', 'USB 3.0 tốc độ cao', 190000, 90, '/uploads/products/a45ad615-5e26-423c-afc4-c960ac9d2b47.jpeg', 5, '2025-09-19 15:28:15', '2025-09-24 04:48:23'),
	(25, 'Hub USB-C Baseus 6in1', 'Hub chuyển đổi, hỗ trợ HDMI', 690000, 40, '/uploads/products/1127217f-06d6-44b4-8a8e-23d5576f65f7.jpg', 5, '2025-09-19 15:28:15', '2025-09-24 04:48:44'),
	(26, 'iPhone 15 Pro Max', 'Điện thoại thông minh cao cấp với chip A17 Pro, camera 48MP và màn hình Super Retina XDR 6.7 inch - Phiên bản cập nhật', 28990000, 44, '/uploads/products/079ed6df-7151-4c98-ad47-bfaa5da8b7c6.jpg', 2, '2025-09-20 15:27:24', '2025-09-24 04:38:44');

-- Dumping structure for table electronic_store.product_promotions
CREATE TABLE IF NOT EXISTS `product_promotions` (
  `product_id` bigint NOT NULL,
  `promotion_id` bigint NOT NULL,
  PRIMARY KEY (`product_id`,`promotion_id`),
  KEY `promotion_id` (`promotion_id`),
  CONSTRAINT `product_promotions_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `product_promotions_ibfk_2` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.product_promotions: ~0 rows (approximately)

-- Dumping structure for table electronic_store.promotions
CREATE TABLE IF NOT EXISTS `promotions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(50) DEFAULT NULL,
  `description` text,
  `discount_type` enum('PERCENT','FIXED') NOT NULL,
  `discount_value` double DEFAULT NULL,
  `max_discount_amount` double DEFAULT NULL,
  `min_order_amount` double DEFAULT '0',
  `usage_limit` int DEFAULT NULL,
  `used_count` int DEFAULT '0',
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  KEY `idx_promotions_code` (`code`),
  KEY `idx_promotions_status_dates` (`status`,`start_date`,`end_date`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.promotions: ~10 rows (approximately)
INSERT INTO `promotions` (`id`, `name`, `code`, `description`, `discount_type`, `discount_value`, `max_discount_amount`, `min_order_amount`, `usage_limit`, `used_count`, `start_date`, `end_date`, `status`, `created_at`, `updated_at`) VALUES
	(1, 'Khuyến mãi Tết Nguyên Đán 2025', 'TET2025', 'Giảm giá đặc biệt nhân dịp Tết Nguyên Đán', 'PERCENT', 15, 200000, 500000, 1000, 0, '2025-01-20', '2025-02-15', 'INACTIVE', '2025-09-23 02:32:19', '2025-09-23 02:32:48'),
	(2, 'Valentine Sale 2025', 'VALENTINE25', 'Ưu đãi dành cho ngày Valentine', 'PERCENT', 20, 150000, 300000, 500, 0, '2025-02-10', '2025-02-16', 'ACTIVE', '2025-09-23 02:32:19', '2025-09-23 02:32:19'),
	(3, 'Ưu đãi sinh viên', 'STUDENT10', 'Giảm giá đặc biệt cho sinh viên', 'PERCENT', 10, 100000, 200000, NULL, 0, '2025-01-01', '2025-12-31', 'ACTIVE', '2025-09-23 02:32:19', '2025-09-23 02:32:19'),
	(4, 'Giảm 50K cho đơn đầu tiên', 'FIRST50K', 'Giảm 50,000 VND cho khách hàng mới', 'FIXED', 50000, NULL, 500000, 200, 0, '2025-01-15', '2025-03-15', 'ACTIVE', '2025-09-23 02:32:19', '2025-09-23 02:32:19'),
	(5, 'Flash Sale Weekend', 'WEEKEND30', 'Giảm giá sốc cuối tuần', 'PERCENT', 30, 300000, 1000000, 100, 0, '2025-01-25', '2025-01-26', 'ACTIVE', '2025-09-23 02:32:19', '2025-09-23 02:32:19'),
	(6, 'Summer Sale 2025', 'SUMMER25', 'Ưu đãi mùa hè cực hot', 'PERCENT', 25, 500000, 800000, 300, 0, '2025-06-01', '2025-08-31', 'INACTIVE', '2025-09-23 02:32:19', '2025-09-23 02:32:19'),
	(7, 'Black Friday 2025', 'BLACK50', 'Siêu sale Black Friday', 'PERCENT', 50, 1000000, 2000000, 50, 0, '2025-11-29', '2025-11-29', 'INACTIVE', '2025-09-23 02:32:19', '2025-09-23 02:32:19'),
	(8, 'VIP Member Discount', 'VIP15', 'Ưu đãi dành cho thành viên VIP', 'PERCENT', 15, 250000, 1000000, NULL, 1, '2025-01-01', '2025-12-31', 'ACTIVE', '2025-09-23 02:32:19', '2025-09-23 10:40:25'),
	(9, 'Mega Sale', 'MEGA100K', 'Giảm 100K cho đơn hàng lớn', 'FIXED', 100000, NULL, 1500000, 150, 0, '2025-02-01', '2025-02-28', 'ACTIVE', '2025-09-23 02:32:19', '2025-09-23 02:32:19'),
	(10, 'Ngày Quốc tế Phụ nữ', 'WOMEN83', 'Tri ân ngày 8/3', 'PERCENT', 18, 300000, 600000, 800, 0, '2025-03-01', '2025-03-10', 'INACTIVE', '2025-09-23 02:32:19', '2025-09-23 02:32:19');

-- Dumping structure for table electronic_store.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `role` enum('USER','ADMIN') DEFAULT 'USER',
  `status` enum('ACTIVE','BLOCKED') DEFAULT 'ACTIVE',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.users: ~3 rows (approximately)
INSERT INTO `users` (`id`, `username`, `password`, `email`, `full_name`, `phone`, `address`, `role`, `status`, `created_at`, `updated_at`) VALUES
	(1, 'user1', '$2a$10$5w02eBq2TPJQzO40vwALieMlfmKQ6gEL7eVmmw2Q4IQzFkmo0nCOO', 'user1@gmail.com', 'Nguyen Van A', NULL, NULL, 'USER', 'ACTIVE', '2025-09-19 08:46:24', '2025-09-22 22:45:27'),
	(2, 'admin', '$2a$10$LLDYJCB3X5X4Wcc58FPdf.tohPzj6sUDwU0TySfhe6a9ftWk7kMJ.', 'admin@gmail.com', 'Nguyen Van A', NULL, NULL, 'ADMIN', 'ACTIVE', NULL, '2025-09-22 22:45:27'),
	(4, 'vuongdh12c1', '$2a$10$0lGn/f4YMFJC5/0Qu4ASvey5zoCWoOB5oZZSlJzlf6Ns8n/XKAXei', 'vuong8aqhqlna@gmail.com', 'Vương Lê', '0977965516', 'Bắc Từ Liêm', 'USER', 'ACTIVE', '2025-09-20 05:29:27', '2025-09-22 22:45:27');

-- Dumping structure for table electronic_store.user_promotions
CREATE TABLE IF NOT EXISTS `user_promotions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `promotion_id` bigint NOT NULL,
  `order_id` bigint DEFAULT NULL,
  `used_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_promotion` (`user_id`,`promotion_id`),
  KEY `user_id` (`user_id`),
  KEY `promotion_id` (`promotion_id`),
  KEY `order_id` (`order_id`),
  KEY `idx_user_promotions_user_id` (`user_id`),
  KEY `idx_user_promotions_promotion_id` (`promotion_id`),
  CONSTRAINT `user_promotions_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_promotions_ibfk_2` FOREIGN KEY (`promotion_id`) REFERENCES `promotions` (`id`) ON DELETE CASCADE,
  CONSTRAINT `user_promotions_ibfk_3` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.user_promotions: ~0 rows (approximately)
INSERT INTO `user_promotions` (`id`, `user_id`, `promotion_id`, `order_id`, `used_at`) VALUES
	(1, 4, 8, 21, '2025-09-23 10:40:25');

-- Dumping structure for table electronic_store.wishlist
CREATE TABLE IF NOT EXISTS `wishlist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `wishlist_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `wishlist_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Dumping data for table electronic_store.wishlist: ~0 rows (approximately)
INSERT INTO `wishlist` (`id`, `user_id`, `product_id`) VALUES
	(2, 4, 1);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
