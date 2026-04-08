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

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        try {
            log.info("嘗試為路徑簽署網址: {}", filePath);

            BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, filePath)).build();

            // 生成 V4 簽署網址
            URL url = storage.signUrl(
                    blobInfo,
                    10,
                    TimeUnit.MINUTES,
                    Storage.SignUrlOption.withV4Signature());

            if (url == null) {
                log.error("Storage signUrl 回傳為 null！");
                return "https://storage.googleapis.com/" + bucketName + "/" + filePath;
            }

            String signedUrl = url.toString();
            log.info("簽署成功！網址長度: {}", signedUrl.length());
            return signedUrl;

        } catch (Exception e) {
            // 這是最關鍵的一行，如果權限不足，這裡會印出原因
            log.error("!!! 簽署網址發生異常 !!! 錯誤訊息: {}", e.getMessage(), e);
            // 暫時回傳短網址（這是導致 Access Denied 的原因）
            return "https://storage.googleapis.com/" + bucketName + "/" + filePath;
        }
    }
}
