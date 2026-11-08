package ng.expensetracker.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ng.expensetracker.data.model.Category;
import ng.expensetracker.dto.request.CreateCategoryRequest;
import ng.expensetracker.dto.response.CategoryResponse;
import ng.expensetracker.mapper.CategoryMapper;
import ng.expensetracker.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // CREATE CATEGORY
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        Category category = new Category(
                request.name(),
                request.type()
        );

        Category savedCategory = categoryService.createCategory(category);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryMapper.toResponse(savedCategory));
    }

    // GET CATEGORY BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable Long id
    ) {
        Category category = categoryService.getCategory(id);

        return ResponseEntity.ok(
                CategoryMapper.toResponse(category)
        );
    }

    // GET ALL CATEGORIES
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> categories =
                categoryService.getAllCategories()
                        .stream()
                        .map(CategoryMapper::toResponse)
                        .toList();

        return ResponseEntity.ok(categories);
    }

    // DELETE CATEGORY
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id
    ) {
        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}
