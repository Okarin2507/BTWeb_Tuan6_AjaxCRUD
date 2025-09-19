package vn.nhuttan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.nhuttan.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
	// Có thể thêm các phương thức tìm kiếm tùy chỉnh ở đây
}