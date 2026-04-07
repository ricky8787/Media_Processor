# Media Processor Prototype

一個基於 Spring Boot 的分散式圖片處理系統原型。此專案展示了如何使用 RabbitMQ 進行任務分發、Redis 追蹤任務狀態，以及 Docker Compose 部署多個服務（API、Worker、RabbitMQ、Redis）。

## 🚀 功能概述

- **圖片上傳**：透過 REST API 上傳圖片。
- **任務佇列**：使用 RabbitMQ 進行非同步任務分發。
- **背景處理**：獨立的 `ImageWorker` 服務負責處理圖片（目前模擬 5 秒處理時間並加上浮水印）。
- **狀態追蹤**：Redis 儲存任務狀態（processing, completed, failed）。
- **WebSocket 推播**：處理完成後主動推播結果給前端。
- **多環境部署**：透過 Docker Compose 快速啟動所有依賴服務。

## 🛠️ 技術架構

- **Backend**：Spring Boot 3.x
- **Message Queue**：RabbitMQ
- **Cache & State**：Redis
- **Storage**：本地檔案系統（可擴展至 GCP）
- **Deployment**：Docker & Docker Compose
- **Frontend**：Vue 3 (獨立專案，需另外啟動)

## 📦 部署與執行

### 前置需求
- Docker
- Docker Compose

### 快速啟動

1. **啟動所有服務**：
   在專案根目錄下執行：
   ```bash
   docker-compose up -d
   ```

2. **檢查服務狀態**：
   ```bash
   docker-compose ps
   ```

3. **查看日誌**：
   ```bash
   docker-compose logs -f
   ```

### 停止服務

```bash
docker-compose down
```

## 🔌 API 說明

### 上傳圖片
- **URL**: `http://localhost:8080/api/images/upload`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Body**:
  - `file`: 圖片檔案
- **Response**:
  ```json
  {
    "taskId": "generated-uuid"
  }
  ```

### 查詢任務狀態
- **URL**: `http://localhost:8080/api/tasks/{taskId}/status`
- **Method**: `GET`
- **Response**:
  ```json
  {
    "status": "processing" // 或 "completed", "failed"
  }
  ```

## 🧪 測試流程

1. **上傳圖片**：
   使用 Postman 或 curl 上傳一張圖片到 `http://localhost:8080/api/images/upload`。

2. **取得 Task ID**：
   記錄回傳的 `taskId`。

3. **監控狀態**：
   - **WebSocket**：打開 `http://localhost:8080` 頁面，監聽該 Task ID 的 WebSocket 頻道。
   - **Polling**：定時呼叫 `http://localhost:8080/api/tasks/{taskId}/status`。

4. **查看結果**：
   - 處理完成後，WebSocket 會推播圖片連結。
   - 也可以直接訪問 `http://localhost/outputs/{taskId}.png` 查看處理後的圖片（需 Nginx 已設定）。

## 📂 專案結構

```
media-processor-prototype/
├── src/main/java/com/practice/media_processor/
│   ├── controller/      # REST API 控制器
│   ├── service/         # 業務邏輯與 Worker
│   ├── conf/            # Spring 配置 (RabbitMQ, etc.)
│   └── model/           # 數據模型
├── src/main/resources/  # 配置文件 (application.properties)
├── docker-compose.yml   # 服務部署配置
├── Dockerfile           # 應用程式 Dockerfile
└── README.md            # 本文件
```

## 🔧 開發指南

### 修改 RabbitMQ 配置
編輯 `src/main/resources/application.properties` 中的 `app.queue.name` 即可修改佇列名稱。

### 修改圖片處理邏輯
編輯 `ImageWorker.java` 中的 `processTask` 方法，修改圖片處理邏輯（目前為打浮水印）。

### 修改儲存位置
編輯 `LocalStorageService.java` 中的 `BASE_DIR` 即可修改本地儲存路徑。

## ⚠️ 注意事項

- 預設使用本地儲存，如需切換至 GCP，請修改 `StorageService` 實作。

