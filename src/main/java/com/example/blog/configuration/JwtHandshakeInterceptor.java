package com.example.blog.configuration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final CustomJwtDecoder jwtDecoder;

    public JwtHandshakeInterceptor(CustomJwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest req = servletRequest.getServletRequest();
            // Lấy token từ query param
            String token = req.getParameter("token");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Xử lý token nếu có "Bearer"
            }

            if (token != null) {
                try {
                    // Giải mã token và kiểm tra tính hợp lệ
                    Jwt jwt = jwtDecoder.decode(token);
                    String username = jwt.getSubject(); // Lấy username từ claim "sub"
                    // Gán principal vào WebSocket attributes
                    attributes.put("user", new UsernamePasswordAuthenticationToken(username, null, List.of()));
                    return true;  // Cho phép kết nối
                } catch (Exception e) {
                    // Xử lý lỗi nếu token không hợp lệ
                    System.out.println("Invalid JWT token: " + e.getMessage());
                    return false;
                }
            }
        }
        return false; // Nếu không có token hoặc token không hợp lệ
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        // Không cần xử lý gì sau handshake
    }
}