package com.example.blog.mapper;

import com.example.blog.dto.request.CategoryRequest;
import com.example.blog.dto.response.CategoryNameResponse;
import com.example.blog.dto.response.CategoryPostResponse;
import com.example.blog.dto.response.CategoryResponse;
import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "category.id", ignore = true)
    Category toCategory (CategoryRequest request);
    @Mapping(target = "posts", source = "posts") //dùng uses cũng được
    CategoryResponse toCategoryResponse(Category category);
    void updateCategory(@MappingTarget Category category, CategoryRequest request);

    @Mapping(source = "description", target = "description")
    CategoryNameResponse toCategoryNameResponse(Category category);

    @Named("mapIdToCategory")
    default Category mapIdToCategory(String id){
        if (id == null) return null;
        Category category = new Category();
        category.setId(id);
        return category;
    }

    @Mapping(source = "user.username", target = "username")//Vì các class lồng nhau nên phải chỉ định rõ, ở đây Post chỉ gọi class User chứ không có trường username
    CategoryPostResponse toCategoryPostResponse(Post post);
}
