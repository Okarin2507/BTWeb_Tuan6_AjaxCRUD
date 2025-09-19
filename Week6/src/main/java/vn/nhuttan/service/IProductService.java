package vn.nhuttan.service;

import java.util.List;
import java.util.Optional;

import vn.nhuttan.entity.Product;

public interface IProductService {
	<S extends Product> S save(S entity);

	List<Product> findAll();

	Optional<Product> findById(Long id);

	void deleteById(Long id);
}