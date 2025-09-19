$(document).ready(function() {

	const apiCategoryUrl = "/api/category";
	const apiProductUrl = "/api/product";
	const imageDisplayPath = "/uploads/images/";
	const defaultImage = "/images/myname.jpg";


	function loadCategories() {
		$.ajax({
			url: apiCategoryUrl,
			type: 'GET',
			success: function(response) {
				let html = '';
				let categoryOptions = '<option value="">-- Select Category --</option>';

				if (response.status && Array.isArray(response.body)) {
					let categories = response.body;
					categories.forEach(function(category) {
						// Thêm tham số timestamp để chống cache trình duyệt
						let timestamp = new Date().getTime();
						let iconUrl = category.icon ? `${imageDisplayPath}${category.icon}?v=${timestamp}` : defaultImage;

						html += `
                            <tr data-id="${category.categoryId}" data-name="${category.name}" data-icon="${category.icon || ''}">
                                <td>${category.categoryId}</td>
                                <td><img src="${iconUrl}" alt="icon" style="width: 70px; height: auto;" onerror="this.onerror=null;this.src='${defaultImage}';"></td>
                                <td>${category.name}</td>
                                <td>
                                    <button class="btn btn-warning btn-sm btn-edit-category"><i class="fas fa-edit"></i></button>
                                    <button class="btn btn-danger btn-sm btn-delete-category"><i class="fas fa-trash"></i></button>
                                </td>
                            </tr>
                        `;
						categoryOptions += `<option value="${category.categoryId}">${category.name}</option>`;
					});
				}
				$('#categoryTableBody').html(html);
				$('#productCategory').html(categoryOptions);
			},
			error: function(xhr) {
				console.error("Error loading categories: ", xhr.responseText);
				$('#categoryTableBody').html('<tr><td colspan="4" class="text-center text-danger">Error loading data. Check console (F12) for details.</td></tr>');
			}
		});
	}

	$("#addCategoryForm").submit(function(e) {
		e.preventDefault();
		var formData = new FormData(this);
		$.ajax({
			url: apiCategoryUrl + "/addCategory",
			type: 'POST',
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				if (response.status) {
					alert('Category added successfully!');
					$('#addCategoryModal').modal('hide');
					$("#addCategoryForm")[0].reset();
					loadCategories();
				} else {
					alert('Error: ' + response.message);
				}
			},
			error: function(xhr) {
				alert('An error occurred while adding category.');
				console.error(xhr.responseText);
			}
		});
	});

	$('#categoryTableBody').on('click', '.btn-edit-category', function() {
		let row = $(this).closest('tr');
		$('#editCategoryId').val(row.data('id'));
		$('#editCategoryName').val(row.data('name'));

		// Thêm tham số timestamp để chống cache trình duyệt
		let timestamp = new Date().getTime();
		let iconUrl = row.data('icon') ? `${imageDisplayPath}${row.data('icon')}?v=${timestamp}` : defaultImage;

		$('#currentIcon').attr('src', iconUrl);
		$('#editCategoryModal').modal('show');
	});

	$("#editCategoryForm").submit(function(e) {
		e.preventDefault();
		var formData = new FormData(this);
		$.ajax({
			url: apiCategoryUrl + "/updateCategory",
			type: 'PUT',
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				if (response.status) {
					alert('Category updated successfully!');
					$('#editCategoryModal').modal('hide');
					loadCategories();
				} else {
					alert('Error: ' + response.message);
				}
			},
			error: function(xhr) {
				alert('An error occurred while updating category.');
				console.error(xhr.responseText);
			}
		});
	});

	$('#categoryTableBody').on('click', '.btn-delete-category', function() {
		let categoryId = $(this).closest('tr').data('id');
		if (confirm('Are you sure you want to delete this category?')) {
			$.ajax({
				url: `${apiCategoryUrl}/deleteCategory?categoryId=${categoryId}`,
				type: 'DELETE',
				success: function(response) {
					if (response.status) {
						alert('Category deleted successfully!');
						loadCategories();
					} else {
						alert('Error: ' + response.message);
					}
				},
				error: function(xhr) {
					alert('An error occurred while deleting category.');
					console.error(xhr.responseText);
				}
			});
		}
	});


	function loadProducts() {
		$.ajax({
			url: apiProductUrl,
			type: 'GET',
			success: function(response) {
				let html = '';
				if (response.status && Array.isArray(response.body)) {
					let products = response.body;
					products.forEach(function(product) {
						// Thêm tham số timestamp để chống cache trình duyệt
						let timestamp = new Date().getTime();
						let imageUrl = product.images ? `${imageDisplayPath}${product.images}?v=${timestamp}` : defaultImage;

						html += `
                            <tr data-product='${JSON.stringify(product)}'>
                                <td>${product.productId}</td>
                                <td><img src="${imageUrl}" alt="image" style="width: 70px; height: auto;" onerror="this.onerror=null;this.src='${defaultImage}';"></td>
                                <td>${product.name}</td>
                                <td>${product.unitPrice}</td>
                                <td>${product.category ? product.category.name : 'N/A'}</td>
                                <td>
                                    <button class="btn btn-warning btn-sm btn-edit-product"><i class="fas fa-edit"></i></button>
                                    <button class="btn btn-danger btn-sm btn-delete-product"><i class="fas fa-trash"></i></button>
                                </td>
                            </tr>
                        `;
					});
				}
				$('#productTableBody').html(html);
			},
			error: function(xhr) {
				console.error("Error loading products: ", xhr.responseText);
				$('#productTableBody').html('<tr><td colspan="6" class="text-center text-danger">Error loading data. Check console (F12) for details.</td></tr>');
			}
		});
	}

	$('#btnAddNewProduct').on('click', function() {
		$("#productForm")[0].reset();
		$('#productId').val('');
		$('#productModalTitle').text('Add New Product');
		$('#btnSaveProduct').text('Save');
		$('#productModal').modal('show');
	});

	$("#productForm").submit(function(e) {
		e.preventDefault();
		var formData = new FormData(this);

		let productId = $('#productId').val();
		let url = productId ? apiProductUrl + "/updateProduct" : apiProductUrl + "/addProduct";
		let type = productId ? "PUT" : "POST";

		$.ajax({
			url: url,
			type: type,
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				if (response.status) {
					alert('Product saved successfully!');
					$('#productModal').modal('hide');
					loadProducts();
				} else {
					alert('Error: ' + response.message);
				}
			},
			error: function(xhr) {
				alert('An error occurred while saving product.');
				console.error(xhr.responseText);
			}
		});
	});

	$('#productTableBody').on('click', '.btn-edit-product', function() {
		let productData = $(this).closest('tr').data('product');

		$('#productId').val(productData.productId);
		$('#productName').val(productData.name);
		$('#productQuantity').val(productData.quantity);
		$('#productPrice').val(productData.unitPrice);
		$('#productDescription').val(productData.description);
		if (productData.category) {
			$('#productCategory').val(productData.category.categoryId);
		}

		$('#productModalTitle').text('Edit Product');
		$('#btnSaveProduct').text('Update');
		$('#productModal').modal('show');
	});

	$('#productTableBody').on('click', '.btn-delete-product', function() {
		let productId = $(this).closest('tr').data('product').productId;
		if (confirm('Are you sure you want to delete this product?')) {
			$.ajax({
				url: `${apiProductUrl}/deleteProduct?productId=${productId}`,
				type: 'DELETE',
				success: function(response) {
					if (response.status) {
						alert('Product deleted successfully!');
						loadProducts();
					} else {
						alert('Error: ' + response.message);
					}
				},
				error: function(xhr) {
					alert('An error occurred while deleting product.');
					console.error(xhr.responseText);
				}
			});
		}
	});

	// Tải dữ liệu ban đầu khi trang được mở
	loadCategories();
	loadProducts();
});