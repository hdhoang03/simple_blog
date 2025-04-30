package com.example.blog.mapper;

import com.example.blog.dto.request.RoleRequest;
import com.example.blog.dto.response.RoleResponse;
import com.example.blog.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);//dto -> entity
    RoleResponse toRoleResponse(Role role);//entity -> dto
}
