package com.example.blog.mapper;

import com.example.blog.dto.request.PermissionRequest;
import com.example.blog.dto.response.PermissionResponse;
import com.example.blog.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
