package cn.aicamera.backend.controller;

import cn.aicamera.backend.dto.GeneralResponse;
import cn.aicamera.backend.dto.UserProfile;
import cn.aicamera.backend.model.User;
import cn.aicamera.backend.service.MinioService;
import cn.aicamera.backend.service.UserService;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private MinioService minioService;

    @GetMapping("/info")
    public ResponseEntity<GeneralResponse<User>> getUserProfile(
            @RequestHeader("Authorization") String token
    ){
        User profile = userService.selectUser(token);
        return ResponseEntity.ok(new GeneralResponse<>(true,"获取个人信息成功",profile));
    }

    @GetMapping(value = "/avatar", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getAvatar(@RequestParam String avatarUrl) {
        InputStream imageStream = minioService.getFile(avatarUrl);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(new InputStreamResource(imageStream));
    }

    @PutMapping("/update")
    public ResponseEntity<GeneralResponse<UserProfile>> updateUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody UserProfile request) {
        UserProfile updatedProfile = userService.updateUserProfile(token, request);
        return ResponseEntity.ok(new GeneralResponse<>(true, "更新成功", updatedProfile));
    }

    @PostMapping(value = "/upload-avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<String>> uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestPart("avatar") MultipartFile avatar) {
        String avatarUrl = userService.uploadAvatar(token, avatar);
        return ResponseEntity.ok(new GeneralResponse<>(true, "上传成功", avatarUrl));
    }
}
