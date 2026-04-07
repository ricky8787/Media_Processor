package com.practice.media_processor.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod") // 只有在生產環境 (-Dspring.profiles.active=prod) 才啟動
public class GcsStorageService implements StorageService {

    @Value("${gcp.bucket.name:my-default-bucket}")
    private String bucketName;

    // 初始化 Google Cloud Storage 客戶端
    // 預設會根據系統環境變數 GOOGLE_APPLICATION_CREDENTIALS 自動取得權限
    private final Storage storage = StorageOptions.getDefaultInstance().getService();

    @Override
    public void saveFile(String filePath, byte[] data) throws Exception {
        BlobId blobId = BlobId.of(bucketName, filePath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, data);
    }

    @Override
    public byte[] getFileBytes(String filePath) throws Exception {
        BlobId blobId = BlobId.of(bucketName, filePath);
        return storage.readAllBytes(blobId);
    }

    @Override
    public String getFileUrl(String filePath) {
        // 產生一個 10 分鐘後自動銷毀失效的加密限時網址 (Signed URL)
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, filePath)).build();
        URL url = storage.signUrl(blobInfo, 10, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
        return url.toString();
    }
}
