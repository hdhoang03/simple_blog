package com.example.blog.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String username;
    String name;
    String email;
    Boolean enabled;
//    Set<RoleResponse> roles;
}
