package com.example.blog.mapper;

import com.example.blog.dto.request.CommentRequest;
import com.example.blog.dto.response.CommentResponse;
import com.example.blog.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toComment(CommentRequest request);

    @Mapping(source = "user.username", target = "username")
    CommentResponse toCommentResponse(Comment comment);
}
