package com.robot.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * 基本的文件写入工具类。
 *
 * @Author 张宝旭
 * @Date 2021/3/15
 */
public class FileWriterUtil {

    private FileWriterUtil() {
    }

    public static Path write(String folderName, String fileName, String text) throws IOException {
        return writeBytes(folderName, fileName, text.getBytes(StandardCharsets.UTF_8));
    }

    public static Path write(String folderName, String fileName, String text, Charset charset) throws IOException {
        return writeBytes(folderName, fileName, text.getBytes(charset));
    }

    public static Path writeBytes(String folderName, String fileName, byte[] bytes) throws IOException {
        Path path = pathHandler(folderName, fileName);
        return Files.write(path, bytes);
    }

    public static Path writeLines(String folderName, String fileName, List<String> lines) throws IOException {
        return writeLines(folderName, fileName, lines, StandardCharsets.UTF_8);
    }

    public static Path writeLines(String folderName, String fileName, List<String> lines, Charset charset) throws IOException {
        Path path = pathHandler(folderName, fileName);
        return Files.write(path, lines, charset);
    }

    private static Path pathHandler(String folderName, String fileName) throws IOException {
        if (Objects.isNull(folderName) || folderName.isEmpty()) {
            throw new NullPointerException("文件路径不能为空！");
        }
        if (Objects.isNull(fileName) || fileName.isEmpty()) {
            throw new NullPointerException("文件名不能为空！");
        }
        Path folderPath = Paths.get(folderName);
        if (Files.notExists(folderPath)) {
            Files.createDirectories(folderPath);
        }
        Path filePath = folderPath.resolve(fileName);
        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
        return filePath;
    }
}
