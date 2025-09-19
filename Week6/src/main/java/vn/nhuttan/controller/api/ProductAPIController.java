package vn.nhuttan.controller.api;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.nhuttan.entity.Category;
import vn.nhuttan.entity.Product;
import vn.nhuttan.model.Response;
import vn.nhuttan.service.CategoryService;
import vn.nhuttan.service.IProductService;
import vn.nhuttan.service.IStorageService;

@RestController
@RequestMapping("/api/product")
public class ProductAPIController {
    
    @Autowired
    private IProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private IStorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        List<Product> products = productService.findAll();
        return new ResponseEntity<>(new Response(true, "Successfully retrieved all products", products), HttpStatus.OK);
    }
    
    @PostMapping(value = "/addProduct", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> addProduct(
        @RequestParam("name") String name,
        @RequestParam("quantity") int quantity,
        @RequestParam("unitPrice") double unitPrice,
        @RequestParam(value = "discount", defaultValue = "0") double discount,
        @RequestParam("description") String description,
        @RequestParam(value = "status", defaultValue = "1") short status,
        @RequestParam("categoryId") Long categoryId,
        @RequestParam(value = "images", required = false) MultipartFile imageFile) {
            
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Category not found with id: " + categoryId, null), HttpStatus.BAD_REQUEST);
        }

        Product product = new Product();
        product.setName(name);
        product.setQuantity(quantity);
        product.setUnitPrice(unitPrice);
        product.setDiscount(discount);
        product.setDescription(description);
        product.setStatus(status);
        product.setCategory(optCategory.get());
        product.setCreateDate(new Date());

        if (imageFile != null && !imageFile.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String storedFilename = storageService.getStorageFilename(imageFile, uuid.toString());
            storageService.store(imageFile, storedFilename);
            product.setImages(storedFilename);
        }
        
        Product savedProduct = productService.save(product);
        return new ResponseEntity<>(new Response(true, "Product added successfully", savedProduct), HttpStatus.CREATED);
    }

    @PutMapping(value = "/updateProduct", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateProduct(
        @RequestParam("productId") Long productId,
        @RequestParam("name") String name,
        @RequestParam("quantity") int quantity,
        @RequestParam("unitPrice") double unitPrice,
        @RequestParam(value = "discount", defaultValue = "0") double discount,
        @RequestParam("description") String description,
        @RequestParam(value = "status", defaultValue = "1") short status,
        @RequestParam("categoryId") Long categoryId,
        @RequestParam(value = "images", required = false) MultipartFile imageFile) {

        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Product not found with id: " + productId, null), HttpStatus.NOT_FOUND);
        }

        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Category not found with id: " + categoryId, null), HttpStatus.BAD_REQUEST);
        }

        Product existingProduct = optProduct.get();
        existingProduct.setName(name);
        existingProduct.setQuantity(quantity);
        existingProduct.setUnitPrice(unitPrice);
        existingProduct.setDiscount(discount);
        existingProduct.setDescription(description);
        existingProduct.setStatus(status);
        existingProduct.setCategory(optCategory.get());

        if (imageFile != null && !imageFile.isEmpty()) {
            // 1. Lấy tên file ảnh cũ
            String oldImage = existingProduct.getImages();

            // 2. Tạo tên file mới và duy nhất
            UUID uuid = UUID.randomUUID();
            String storedFilename = storageService.getStorageFilename(imageFile, uuid.toString());
            
            // 3. Lưu ảnh mới
            storageService.store(imageFile, storedFilename);
            
            // 4. Cập nhật tên file mới vào entity
            existingProduct.setImages(storedFilename);

            // 5. Xóa file ảnh cũ (nếu có)
            if (oldImage != null && !oldImage.isEmpty()) {
                try {
                    storageService.delete(oldImage);
                } catch (Exception e) {
                    System.err.println("Could not delete old image file: " + oldImage);
                }
            }
        }

        Product updatedProduct = productService.save(existingProduct);
        return new ResponseEntity<>(new Response(true, "Product updated successfully", updatedProduct), HttpStatus.OK);
    }
    
    @DeleteMapping("/deleteProduct")
    public ResponseEntity<?> deleteProduct(@RequestParam("productId") Long productId) {
        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Product not found with id: " + productId, null), HttpStatus.NOT_FOUND);
        }

        Product productToDelete = optProduct.get();
        try {
            String imageToDelete = productToDelete.getImages();
            if (imageToDelete != null && !imageToDelete.isEmpty()) {
                storageService.delete(imageToDelete);
            }
            productService.deleteById(productId);
        } catch (Exception e) {
             return new ResponseEntity<>(new Response(false, "Error deleting product: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new Response(true, "Product deleted successfully", null), HttpStatus.OK);
    }
}