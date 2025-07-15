package cn.aicamera.backend.controller;

import cn.aicamera.backend.dto.GeneralResponse;
import cn.aicamera.backend.dto.SuccessResponse;
import cn.aicamera.backend.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

// TODO
@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<GeneralResponse<String>> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestParam String message) {
        System.out.println("Receive message:"+message);
        String response=chatService.sendMessage(token, message);
        System.out.println("Receive response:"+response);
        return ResponseEntity.ok(new GeneralResponse<>(true, "消息发送成功", response));
    }

//    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> listenMessages(@RequestHeader("Authorization") String token) {
//        return chatService.listenMessages(token);
//    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GeneralResponse<String>> uploadImage(
            @RequestHeader("Authorization") String token,
            @RequestPart("images") List<MultipartFile> images,
            @RequestPart("platform") String platform,
            @RequestPart("mood") String mood) throws IOException {
//        try {
//            chatService.uploadImage(token, image);
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
        System.out.println("Receive file: size "+images.size()+" platform "+ platform + " mood " + mood);
        String response= chatService.uploadImageRequest(token,images,platform,mood);
        return ResponseEntity.ok(new GeneralResponse<>(true, "图片上传成功",response));
    }
}
