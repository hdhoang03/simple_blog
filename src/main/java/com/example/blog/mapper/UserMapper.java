package com.example.blog.mapper;

import com.example.blog.dto.request.UserCreationRequest;
import com.example.blog.dto.request.UserUpdateRequest;
import com.example.blog.dto.response.UserResponse;
import com.example.blog.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    @Mapping(target = "email", source = "email")
    User toUser(UserCreationRequest request);
    @Mapping(target = "dob", source = "dob")
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
