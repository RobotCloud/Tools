package com.robot.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * 基本的文件读取工具类。
 *
 * @Author 张宝旭
 * @Date 2021/3/15
 */
public class FileReaderUtil {

    private FileReaderUtil() {
    }

    // 读取文件
    public static String read(String filePath) throws IOException {
        return read(filePath, StandardCharsets.UTF_8);
    }

    public static String read(String filePath, Charset charset) throws IOException {
        return new String(readBytes(filePath), charset);
    }

    public static byte[] readBytes(String filePath) throws IOException {
        Path path = pathHandler(filePath);
        return Files.readAllBytes(path);
    }

    // 读取一行
    public static List<String> readLines(String filePath) throws IOException {
        return readLines(filePath, StandardCharsets.UTF_8);
    }

    public static List<String> readLines(String filePath, Charset charset) throws IOException {
        Path path = pathHandler(filePath);
        return Files.readAllLines(path, charset);
    }

    // 解析路径
    private static Path pathHandler(String filePath) {
        Path path = Optional.ofNullable(filePath)
                .map(str -> Paths.get(str))
                .orElseThrow(() -> new NullPointerException("文件路径不能为空！"));
        if (Files.exists(path)) {
            return path;
        }
        throw new RuntimeException("文件异常！");
    }
}
