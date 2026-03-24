package work.soho.ai.biz.service.impl;

import lombok.extern.log4j.Log4j2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import work.soho.ai.biz.service.AiFileService;
import work.soho.common.core.util.StringUtils;
import work.soho.common.data.upload.utils.UploadUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Log4j2
@Service
public class AiFileServiceImpl implements AiFileService {
    private static final int CONNECT_TIMEOUT_MS = 10000;
    private static final int READ_TIMEOUT_MS = 20000;
    private static final int MAX_DOWNLOAD_BYTES = 4 * 1024 * 1024;
    private static final int MAX_EXTRACTED_CHARS = 20000;

    @Override
    public String uploadUserFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }
        String url = UploadUtils.upload("ai/file", file);
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("file upload failed");
        }
        return url;
    }

    @Override
    public String extractTextFromUrl(String fileUrl) {
        if (StringUtils.isBlank(fileUrl)) {
            return "";
        }
        try {
            RemoteFile remoteFile = download(fileUrl);
            String extracted = extractText(remoteFile, fileUrl);
            return limitText(extracted);
        } catch (Exception e) {
            log.warn("extract file text failed: {}", fileUrl, e);
            return "";
        }
    }

    private RemoteFile download(String fileUrl) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl).openConnection();
        connection.setInstanceFollowRedirects(true);
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setRequestProperty("User-Agent", "soho-ai-file-parser/1.0");
        connection.connect();

        int status = connection.getResponseCode();
        if (status >= 400) {
            throw new IOException("download file failed, status=" + status);
        }

        String contentType = connection.getContentType();
        try (InputStream inputStream = connection.getInputStream()) {
            byte[] bytes = readAll(inputStream, MAX_DOWNLOAD_BYTES);
            return new RemoteFile(bytes, contentType);
        } finally {
            connection.disconnect();
        }
    }

    private String extractText(RemoteFile remoteFile, String fileUrl) throws IOException {
        String lowerUrl = fileUrl.toLowerCase(Locale.ROOT);
        String contentType = remoteFile.contentType == null ? "" : remoteFile.contentType.toLowerCase(Locale.ROOT);

        if (lowerUrl.endsWith(".pdf") || contentType.contains("application/pdf")) {
            return extractPdf(remoteFile.bytes);
        }
        if (isPlainText(lowerUrl, contentType)) {
            return new String(remoteFile.bytes, StandardCharsets.UTF_8);
        }
        throw new IllegalArgumentException("unsupported file type");
    }

    private boolean isPlainText(String lowerUrl, String contentType) {
        if (contentType.startsWith("text/")) {
            return true;
        }
        return lowerUrl.endsWith(".txt")
                || lowerUrl.endsWith(".md")
                || lowerUrl.endsWith(".markdown")
                || lowerUrl.endsWith(".csv")
                || lowerUrl.endsWith(".json")
                || lowerUrl.endsWith(".xml")
                || lowerUrl.endsWith(".yaml")
                || lowerUrl.endsWith(".yml")
                || lowerUrl.endsWith(".log")
                || lowerUrl.endsWith(".java")
                || lowerUrl.endsWith(".js")
                || lowerUrl.endsWith(".ts")
                || lowerUrl.endsWith(".py")
                || lowerUrl.endsWith(".sql")
                || lowerUrl.endsWith(".html")
                || lowerUrl.endsWith(".htm");
    }

    private String extractPdf(byte[] bytes) throws IOException {
        try (PDDocument document = PDDocument.load(bytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private byte[] readAll(InputStream inputStream, int maxBytes) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int total = 0;
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            total += len;
            if (total > maxBytes) {
                throw new IOException("file too large");
            }
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }

    private String limitText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        String normalized = text.replace("\r\n", "\n").trim();
        if (normalized.length() <= MAX_EXTRACTED_CHARS) {
            return normalized;
        }
        return normalized.substring(0, MAX_EXTRACTED_CHARS);
    }

    private static class RemoteFile {
        private final byte[] bytes;
        private final String contentType;

        private RemoteFile(byte[] bytes, String contentType) {
            this.bytes = bytes;
            this.contentType = contentType;
        }
    }
}
