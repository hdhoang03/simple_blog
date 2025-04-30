package com.example.blog.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TemporaryUser implements Serializable {
    String username;
    String password;
    String name;
    String email;
    String verificationCode;
}
