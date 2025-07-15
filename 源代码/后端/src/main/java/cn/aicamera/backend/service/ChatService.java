package cn.aicamera.backend.service;

import cn.aicamera.backend.dto.CopywritingResponse;
import cn.aicamera.backend.dto.ImageAnalysis;
import cn.aicamera.backend.exception.ChatModelException;
import cn.aicamera.backend.exception.CustomException;
import cn.aicamera.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO:对接大模型
@Service
public class ChatService {
//    @Autowired
//    private ChatMapper chatMapper;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${model.endpoint}")
    private String serverUrl;

    public String sendMessage(String token, String message) {
        String sessionId=redisTemplate.opsForValue().get("Chat_"+token);
        if(sessionId==null||sessionId.isEmpty()) throw new CustomException(500,"获取对话信息失败");
        // 构建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        // 构建请求体
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("message", message);
        body.add("sessionId",sessionId);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                serverUrl + "/caption/conversation_update",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new CustomException(response.getStatusCode().value(),"更新文案失败：" + response.getBody());
        }
        return response.getBody();
    }
//
//    public Flux<String> listenMessages(String token) {
//        String email = jwtUtil.getUsernameFromToken(token);
//        return Flux.fromStream(chatMapper.findMessagesBySender(username).map(Message::getContent);
//    }
//
    public void uploadImage(String token, MultipartFile image) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new InputStreamResource(image.getInputStream()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                serverUrl + "/chat/upload",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        if(!response.getStatusCode().is2xxSuccessful()){
            throw new ChatModelException(response.getBody());
        }
    }

    public String uploadImageRequest(String token, List<MultipartFile> images, String platform,String mood) throws IOException{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        for (MultipartFile image : images) {
            Resource resource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename(); // 设置文件名
                }
            };
            byte[] imageBytes = image.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String dataUrl = "data:image/jpeg;base64," + base64Image;
            body.add("images", dataUrl);
        }
        body.add("platform", platform);
        body.add("mood",mood);
        System.out.println("platform:"+body.get("platform"));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<CopywritingResponse> response = restTemplate.exchange(
                serverUrl + "/caption/generate",
                HttpMethod.POST,
                requestEntity,
                CopywritingResponse.class
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            String redisToken="Chat_"+token;
            if(redisTemplate.opsForValue().get(redisToken)!=null&&!redisTemplate.opsForValue().get(redisToken).isEmpty()) redisTemplate.delete(redisToken);
            redisTemplate.opsForValue().set(redisToken,response.getBody().getSessionId(),20, TimeUnit.MINUTES);// 保存上下文id 10分钟
            return response.getBody().getCaption();
        } else {
            throw new CustomException(response.getStatusCode().value(),"生成文案失败：" + response.getBody());
        }
    }
}
