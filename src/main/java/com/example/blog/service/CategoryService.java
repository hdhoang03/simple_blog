package com.example.blog.service;

import com.example.blog.dto.request.CategoryRequest;
import com.example.blog.dto.response.CategoryNameResponse;
import com.example.blog.dto.response.CategoryResponse;
import com.example.blog.entity.Category;
import com.example.blog.exception.AppException;
import com.example.blog.exception.ErrorCode;
import com.example.blog.mapper.CategoryMapper;
import com.example.blog.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryRequest request){
        if(categoryRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryMapper.toCategory(request);
        category = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(category);
    }

    public List<CategoryNameResponse> getAllCategoriesName(){
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryNameResponse)
                .toList();
    }

    public List<CategoryResponse> getAllCategories(){
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toCategoryResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(String id){
        categoryRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(String id, CategoryRequest request){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new AppException(ErrorCode.CATEGORY_EXISTED));

        categoryMapper.updateCategory(category, request);
        category = categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }
}
