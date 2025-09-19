package vn.nhuttan.controller.api;

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
import vn.nhuttan.model.Response;
import vn.nhuttan.service.CategoryService;
import vn.nhuttan.service.IStorageService;

@RestController
@RequestMapping("/api/category")
public class CategoryAPIController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private IStorageService storageService;

    @GetMapping
    public ResponseEntity<?> getAllCategories() {
        List<Category> list = categoryService.findAll();
        return new ResponseEntity<>(new Response(true, "Success", list), HttpStatus.OK);
    }

    @PostMapping(value = "/addCategory", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> addCategory(@RequestParam("name") String name,
                                         @RequestParam("icon") MultipartFile icon) {
        
        Optional<Category> optCategory = categoryService.findByNameContaining(name).stream().findFirst();
        if (optCategory.isPresent()) {
            return new ResponseEntity<>(new Response(false, "Category name already exists", null), HttpStatus.BAD_REQUEST);
        }

        Category category = new Category();
        category.setName(name);

        if (icon != null && !icon.isEmpty()) {
            UUID uuid = UUID.randomUUID();
            String storedFilename = storageService.getStorageFilename(icon, uuid.toString());
            storageService.store(icon, storedFilename);
            category.setIcon(storedFilename);
        } else {
            return new ResponseEntity<>(new Response(false, "Icon file is required", null), HttpStatus.BAD_REQUEST);
        }
        
        Category savedCategory = categoryService.save(category);
        return new ResponseEntity<>(new Response(true, "Category added successfully", savedCategory), HttpStatus.OK);
    }
    
    @PutMapping(value = "/updateCategory", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateCategory(@RequestParam("categoryId") Long categoryId,
                                            @RequestParam("name") String name,
                                            @RequestParam(value = "icon", required = false) MultipartFile icon) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Category not found", null), HttpStatus.NOT_FOUND);
        }

        Category category = optCategory.get();
        category.setName(name);

        // Nếu có file icon mới được upload
        if (icon != null && !icon.isEmpty()) {
            // 1. Lấy tên file icon cũ để xóa
            String oldIcon = category.getIcon();

            // 2. Tạo tên file mới và duy nhất cho icon mới
            UUID uuid = UUID.randomUUID();
            String storedFilename = storageService.getStorageFilename(icon, uuid.toString());
            
            // 3. Lưu icon mới
            storageService.store(icon, storedFilename);
            
            // 4. Cập nhật tên file mới vào entity
            category.setIcon(storedFilename);

            // 5. Xóa file icon cũ (nếu có)
            if (oldIcon != null && !oldIcon.isEmpty()) {
                try {
                    storageService.delete(oldIcon);
                } catch (Exception e) {
                    System.err.println("Could not delete old icon file: " + oldIcon);
                }
            }
        }
        
        Category updatedCategory = categoryService.save(category);
        return new ResponseEntity<>(new Response(true, "Category updated successfully", updatedCategory), HttpStatus.OK);
    }

    @DeleteMapping("/deleteCategory")
    public ResponseEntity<?> deleteCategory(@RequestParam("categoryId") Long categoryId) {
        Optional<Category> optCategory = categoryService.findById(categoryId);
        if (optCategory.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Category not found", null), HttpStatus.NOT_FOUND);
        }
        
        try {
            String iconToDelete = optCategory.get().getIcon();
            if (iconToDelete != null && !iconToDelete.isEmpty()) {
                storageService.delete(iconToDelete);
            }
            categoryService.deleteById(categoryId);
        } catch (Exception e) {
             return new ResponseEntity<>(new Response(false, "Error deleting category: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(new Response(true, "Category deleted successfully", null), HttpStatus.OK);
    }
}