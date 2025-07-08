package com.example.blog.mapper;

import com.example.blog.dto.request.PostRequest;
import com.example.blog.dto.response.CategoryPostResponse;
import com.example.blog.dto.response.PostAdminResponse;
import com.example.blog.dto.response.PostUserResponse;
import com.example.blog.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CommentMapper.class, CategoryMapper.class})
public interface PostMapper {
    @Mapping(source = "category", target = "category", qualifiedByName = "mapIdToCategory")
    Post toPost(PostRequest request);
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(expression = "java(post.getComments() != null ? post.getComments().size() : 0)", target = "commentCount")
    PostAdminResponse toPostAdminResponse(Post post);
    @Mapping(source = "user.username", target = "username")
    @Mapping(target = "categoryName", source = "category.name")
//    @Mapping(source = "comments", target = "comments")
    @Mapping(expression = "java(post.getVotes() != null ? (int) post.getVotes().size() : 0)", target = "upvotes")
    @Mapping(expression = "java(post.getVotes() != null ? (int) post.getVotes().size() : 0)", target = "downvotes")
    PostUserResponse toPostUserResponse(Post post);

    @Mapping(source = "user.username", target = "username")
    CategoryPostResponse toCategoryPostResponse(Post post);
}
