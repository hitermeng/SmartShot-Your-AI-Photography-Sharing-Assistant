package cn.aicamera.backend.controller;

import cn.aicamera.backend.dto.GeneralResponse;
import cn.aicamera.backend.dto.ImageAnalysis;
import cn.aicamera.backend.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/camera")
public class CameraController {
    @Autowired
    private CameraService cameraService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<ImageAnalysis>> uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestPart("image") MultipartFile image) {
        System.out.println("Receive file: type " + image.getContentType() + " name " + image.getOriginalFilename());
//        ImageAnalysis ia=new ImageAnalysis(90,10,-5,10);
        try {
            ImageAnalysis ia = cameraService.analysisImage(token, image);
            return ResponseEntity.ok(new GeneralResponse<>(true, "处理成功", ia));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
