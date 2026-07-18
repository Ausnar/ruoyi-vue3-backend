package com.ruoyi.manage.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.uuid.IdUtils;

/**
 * 设备预警处置附件私有存储。
 */
@Component
public class DeviceWarningAttachmentStorage
{
    public static final int MAX_FILE_COUNT = 5;
    public static final long MAX_FILE_SIZE = 10L * 1024L * 1024L;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<String>(
        Arrays.asList("jpg", "jpeg", "png", "pdf"));

    public StoredFile store(MultipartFile file, Long taskId)
    {
        validate(file);
        String originalName = FilenameUtils.getName(file.getOriginalFilename());
        String extension = FilenameUtils.getExtension(originalName).toLowerCase();
        String relativePath = DateUtils.datePath() + "/task-" + taskId + "/"
            + IdUtils.fastSimpleUUID() + "." + extension;
        Path target = resolveTarget(relativePath);
        try
        {
            Files.createDirectories(target.getParent());
            file.transferTo(target.toFile());
            return new StoredFile(originalName, relativePath, contentType(extension), file.getSize());
        }
        catch (IOException | IllegalStateException ex)
        {
            delete(relativePath);
            throw new ServiceException("处置附件保存失败，请稍后重试");
        }
    }

    public File resolve(String storedPath)
    {
        Path target = resolveTarget(storedPath);
        if (!Files.isRegularFile(target))
        {
            throw new ServiceException("处置附件文件不存在");
        }
        return target.toFile();
    }

    public void delete(String storedPath)
    {
        if (StringUtils.isBlank(storedPath))
        {
            return;
        }
        try
        {
            Files.deleteIfExists(resolveTarget(storedPath));
        }
        catch (IOException ignored)
        {
            // 数据库事务回滚优先，遗留文件可由后续巡检清理。
        }
    }

    private void validate(MultipartFile file)
    {
        if (file == null || file.isEmpty())
        {
            throw new ServiceException("不能上传空附件");
        }
        String originalName = FilenameUtils.getName(file.getOriginalFilename());
        if (StringUtils.isBlank(originalName) || originalName.length() > 255)
        {
            throw new ServiceException("附件名称不能为空且不能超过255个字符");
        }
        if (file.getSize() > MAX_FILE_SIZE)
        {
            throw new ServiceException("单个处置附件不能超过10MB");
        }
        String extension = FilenameUtils.getExtension(originalName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension))
        {
            throw new ServiceException("处置附件仅支持JPG、PNG或PDF格式");
        }
        verifySignature(file, extension);
    }

    private void verifySignature(MultipartFile file, String extension)
    {
        byte[] header = new byte[8];
        int length;
        try (InputStream input = file.getInputStream())
        {
            length = input.read(header);
        }
        catch (IOException ex)
        {
            throw new ServiceException("无法读取处置附件");
        }
        boolean valid;
        if ("jpg".equals(extension) || "jpeg".equals(extension))
        {
            valid = length >= 3 && (header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF;
        }
        else if ("png".equals(extension))
        {
            byte[] signature = new byte[] {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
            valid = length >= signature.length && Arrays.equals(header, signature);
        }
        else
        {
            valid = length >= 5 && header[0] == '%' && header[1] == 'P' && header[2] == 'D'
                && header[3] == 'F' && header[4] == '-';
        }
        if (!valid)
        {
            throw new ServiceException("附件内容与文件扩展名不一致");
        }
    }

    private Path resolveTarget(String relativePath)
    {
        if (StringUtils.isBlank(relativePath))
        {
            throw new ServiceException("处置附件路径无效");
        }
        Path root = storageRoot();
        Path target = root.resolve(relativePath.replace('/', File.separatorChar)).normalize();
        if (!target.startsWith(root))
        {
            throw new ServiceException("处置附件路径无效");
        }
        return target;
    }

    private Path storageRoot()
    {
        Path profile = Paths.get(RuoYiConfig.getProfile()).toAbsolutePath().normalize();
        Path parent = profile.getParent();
        return (parent == null ? profile : parent).resolve("warning-attachments").normalize();
    }

    private String contentType(String extension)
    {
        if ("jpg".equals(extension) || "jpeg".equals(extension))
        {
            return "image/jpeg";
        }
        if ("png".equals(extension))
        {
            return "image/png";
        }
        return "application/pdf";
    }

    public static class StoredFile
    {
        private final String originalName;
        private final String storedPath;
        private final String fileType;
        private final long fileSize;

        StoredFile(String originalName, String storedPath, String fileType, long fileSize)
        {
            this.originalName = originalName;
            this.storedPath = storedPath;
            this.fileType = fileType;
            this.fileSize = fileSize;
        }

        public String getOriginalName() { return originalName; }
        public String getStoredPath() { return storedPath; }
        public String getFileType() { return fileType; }
        public long getFileSize() { return fileSize; }
    }
}
