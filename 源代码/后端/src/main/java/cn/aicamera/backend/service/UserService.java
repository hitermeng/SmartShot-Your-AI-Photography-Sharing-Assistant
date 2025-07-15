package cn.aicamera.backend.service;

import cn.aicamera.backend.dto.*;
import cn.aicamera.backend.exception.CustomException;
import cn.aicamera.backend.mapper.UserMapper;
import cn.aicamera.backend.model.User;
import cn.aicamera.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private MinioService minioService;

    public void register(RegisterRequest request) {
        if (userMapper.findByEmail(request.getEmail()) != null) {
            throw new CustomException(200, "邮箱已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        userMapper.insert(user);
    }

    public String login(LoginRequest request) {
        User user = userMapper.findByEmail(request.getEmail());
//        if (user == null || !user.getPassword().equals(request.getPassword())) {
//            throw new CustomException(200, "用户名或密码错误");
//        }
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(200, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        tokenService.storeToken(user.getEmail(), token);
        return token;
    }

    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.getUsernameFromToken(token);
        tokenService.removeToken(username);
    }

    public User selectUser(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByEmail(email);
        return user;
    }
    public UserProfile updateUserProfile(String token, UserProfile request) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String email = jwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByEmail(email);
        user.setAge(request.getAge());
        user.setUsername(request.getUsername());
        user.setGender(request.getGender());
        user.setPreference(request.getPreference());
        userMapper.update(user);
        return request;
    }

    public String uploadAvatar(String token, MultipartFile avatar) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtUtil.getUsernameFromToken(token);
        User user = userMapper.findByEmail(email);

        if(!user.getAvatarUrl().isEmpty()) minioService.deleteFile(user.getAvatarUrl());
        String avatarUrl = minioService.uploadFile(avatar);

        user.setAvatarUrl(avatarUrl);
        userMapper.update(user);
        return avatarUrl;
    }

    public String validateLogin(String token) {
        // 去掉前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            throw new CustomException(200,"Token 无效或已过期");
        }

        // 更新 Token 的过期时间
        String username = jwtUtil.getUsernameFromToken(token);
        String newToken = jwtUtil.generateToken(username);
        tokenService.storeToken(username, newToken);
        return newToken;
    }
}