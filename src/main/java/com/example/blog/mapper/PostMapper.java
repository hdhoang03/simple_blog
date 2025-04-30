package com.example.blog.mapper;

import com.example.blog.dto.request.PostRequest;
import com.example.blog.dto.response.PostAdminResponse;
import com.example.blog.dto.response.PostUserResponse;
import com.example.blog.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {CommentMapper.class})
public interface PostMapper {
    Post toPost(PostRequest request);
    PostAdminResponse toPostAdminResponse(Post post);
    @Mappings({
            @Mapping(source = "user.username", target = "username"),
//            @Mapping(source = "comments", target = "comments")
    })
    PostUserResponse toPostUserResponse(Post post);
}
