# BTWeb_Tuan6_AjaxCRUD (23110315_LeNgoNhutTan)
Project Quản lý Sản phẩm & Danh mục bằng Spring Boot
# Các tính năng chính
   * CRUD đầy đủ cho Danh mục (Category).
   * CRUD đầy đủ cho Sản phẩm (Product).
   * Giao diện quản lý sử dụng AJAX, mang lại trải nghiệm mượt mà không cần tải lại trang.
   * Chức năng upload hình ảnh cho cả Category và Product.
   * Tự động tạo tài liệu API với Swagger UI.
   * Bao gồm cả các trang quản lý theo kiểu MVC truyền thống (phân trang và tìm kiếm).
# Hướng dẫn Cài đặt và Chạy dự án
1. Yêu cầu môi trường
JDK 17 trở lên.
Maven 3.6 trở lên.
Microsoft SQL Server đã được cài đặt và đang chạy.
2. Cài đặt Cơ sở dữ liệu
Mở SQL Server Management Studio (SSMS).
Tạo một database mới với tên là Web_week5.
Mở một cửa sổ Query mới cho database Web_week5 và chạy đoạn script sau để tạo các bảng cần thiết:
code
```SQL
-- Tạo bảng Categories
CREATE TABLE dbo.categories (
    id BIGINT IDENTITY(1,1) NOT NULL,
    name NVARCHAR(200) NOT NULL,
    icon VARCHAR(255) NULL,
    PRIMARY KEY (id)
);
GO

-- Tạo bảng Products
CREATE TABLE dbo.products (
    id BIGINT IDENTITY(1,1) NOT NULL,
    name NVARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    unit_price FLOAT NOT NULL,
    discount FLOAT NOT NULL,
    images VARCHAR(255) NULL,
    description NVARCHAR(500) NULL,
    create_date DATETIME NOT NULL,
    status SMALLINT NOT NULL,
    category_id BIGINT NULL,
    PRIMARY KEY (id)
);
GO

-- Thêm ràng buộc khóa ngoại từ Products đến Categories
ALTER TABLE dbo.products
ADD CONSTRAINT FK_products_categories
FOREIGN KEY (category_id) REFERENCES dbo.categories(id);
GO
```
3. Cấu hình ứng dụng
* Mở file src/main/resources/application.properties.
* Tìm đến các dòng cấu hình database và chỉnh sửa cho phù hợp với môi trường của bạn, đặc biệt là mật khẩu.
```code
Properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=Web_week5;encrypt=true;trustServerCertificate=true;
spring.datasource.username=sa
spring.datasource.password=your_strong_password_here # <-- THAY MẬT KHẨU CỦA BẠN VÀO ĐÂY
```
4. Chạy ứng dụng
* Trang quản lý AJAX (chức năng chính):
```BASH
http://localhost:8088/admin/categories/crud-ajax
```
* Giao diện Swagger để kiểm tra API:
```BASH
http://localhost:8088/swagger-ui.html
```
* Trang quản lý MVC truyền thống:
```BASH
http://localhost:8088/admin/categories/searchpaginated
```
