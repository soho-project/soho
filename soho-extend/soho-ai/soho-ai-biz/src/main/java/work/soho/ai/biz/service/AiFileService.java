package work.soho.ai.biz.service;

import org.springframework.web.multipart.MultipartFile;

public interface AiFileService {
    String uploadUserFile(MultipartFile file);

    String extractTextFromUrl(String fileUrl);
}
