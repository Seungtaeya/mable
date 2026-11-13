package demo.mable.admin.service;

import demo.mable.admin.enums.AssetType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path upload;
    private static final long MAX_BYTES = 5 * 1024 * 1024;

    public ImageStorageService(@Value("${app.upload-directory}") String directory) {
        this.upload = Paths.get(directory).toAbsolutePath().normalize();
    }

    public String save(MultipartFile image, AssetType assetType) throws IOException {
        validate(image);
        Path typeDirectory = upload.resolve(assetType.getDirectory()).normalize();

        String  contentType = extensionFor(image.getContentType());
        String storedName = UUID.randomUUID() + contentType;

        Path destination = typeDirectory.resolve(storedName).normalize();

        if(!destination.startsWith(upload)) {
            throw new IllegalArgumentException("Invalid upload path");
        }

        Files.createDirectories(typeDirectory);
        image.transferTo(destination);

        return "/uploads/" + assetType.getDirectory() + "/" + storedName;
    }

    private void validate(MultipartFile image) {
        if(image == null || image.isEmpty()) {
            throw new IllegalArgumentException("이미지는 필수 입니다.");
        }
        if(MAX_BYTES < image.getSize()) {
            throw new IllegalArgumentException("이미지의 최대 크기는 5MB 입니다.");
        }
    }

    private String extensionFor(String contentType) {
        if(contentType == null) {
            throw new IllegalArgumentException("확장자가 이미지파일 확장자가 아닙니다.");
        }

        return switch(contentType) {
            case "image/png" -> ".png";
            case "image/jpeg" -> ".jpg";
            default -> throw new IllegalArgumentException("확장자는 png, jpeg만 가능합니다.");
        };
    }
}
