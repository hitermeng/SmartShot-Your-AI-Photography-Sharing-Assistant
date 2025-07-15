package cn.aicamera.backend.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioService {
    @Autowired
    private MinioClient minioClient;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.bucket-name}")
    private String bucketName;
    public String uploadFile(MultipartFile file) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(file.getOriginalFilename())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .build());
            return String.format("%s/%s/%s",endpoint,bucketName,file.getOriginalFilename());
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败：", e);
        }
    }
    public void deleteFile(String filename) {
        String originFileName=filename.split(String.format("%s/%s/",endpoint,bucketName))[1];
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(originFileName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败: " + filename, e);
        }
    }

    public InputStream getFile(String filename){
        try {
            String originFilename=filename.split(String.format("%s/%s/",endpoint,bucketName))[1];
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(originFilename)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("获取文件失败", e);
        }
    }
}
