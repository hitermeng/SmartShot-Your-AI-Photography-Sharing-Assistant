package cn.aicamera.backend.controller;

import cn.aicamera.backend.dto.GeneralResponse;
import cn.aicamera.backend.dto.LoginRequest;
import cn.aicamera.backend.dto.RegisterRequest;
import cn.aicamera.backend.dto.SuccessResponse;
import cn.aicamera.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok(new SuccessResponse(true, "注册成功"));
    }

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse<String>> login(@RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(new GeneralResponse<>(true, "登录成功", token));
    }

    @GetMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok(new SuccessResponse(true, "退出成功"));
    }

    @GetMapping("/validate-login")
    public ResponseEntity<GeneralResponse<String>> validateLogin(@RequestHeader("Authorization") String token) {
        String newToken= userService.validateLogin(token);
        return ResponseEntity.ok(new GeneralResponse<>(true, "Token 已刷新", newToken));
    }
}
