package com.practice.media_processor.service;

public interface StorageService {
    /**
     * 儲存檔案
     * @param filePath 檔案路徑與名稱
     * @param data 檔案的 byte 內容
     */
    void saveFile(String filePath, byte[] data) throws Exception;

    /**
     * 讀取檔案
     * @param filePath 檔案路徑與名稱
     * @return 檔案的 byte 內容
     */
    byte[] getFileBytes(String filePath) throws Exception;

    /**
     * 取得檔案的公開存取網址
     * @param filePath 檔案名稱
     * @return URL 字串
     */
    String getFileUrl(String filePath);
}
