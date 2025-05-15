package com.example.blog.controller;

import com.example.blog.dto.ApiResponse;
import com.example.blog.dto.request.CategoryRequest;
import com.example.blog.dto.response.CategoryNameResponse;
import com.example.blog.dto.response.CategoryResponse;
import com.example.blog.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping("/admin/add")
    ApiResponse<CategoryResponse> createCategory(@RequestBody CategoryRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .code(1000)
                .result(categoryService.createCategory(request))
                .build();
    }

    @GetMapping("/get-name")
    ApiResponse<List<CategoryNameResponse>> getAllCategoriesName(){
        return ApiResponse.<List<CategoryNameResponse>>builder()
                .code(1000)
                .result(categoryService.getAllCategoriesName())
                .build();
    }

    @GetMapping("/{categoryId}")
    ApiResponse<List<CategoryResponse>> getAllPostsFromCategory(@PathVariable String categoryId){
        return ApiResponse.<List<CategoryResponse>>builder()
                .code(1000)
                .result(categoryService.getAllPostsFromCategory(categoryId))
                .build();
    }

    @DeleteMapping("/admin/delete/{categoryId}")
    ApiResponse<Void> deleteCategory(@PathVariable String categoryId){
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Category has been deleted!")
                .build();
    }

    @PutMapping("/admin/edit/{categoryId}")
    ApiResponse<CategoryResponse> updateCategory(@PathVariable String categoryId, @RequestBody CategoryRequest request){
        return ApiResponse.<CategoryResponse>builder()
                .code(1000)
                .result(categoryService.updateCategory(categoryId, request))
                .build();
    }
}
