package com.robot.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 文件处理。
 *
 * @Author 张宝旭
 * @Date 2020/10/25
 */
public class FileUtils {

    public static void main(String[] args) throws Exception {
        String base64 = imageToBase64();
        System.out.println(base64);
//        System.out.println("https://super-omini.oss-cn-beijing.aliyuncs.com/test/robot.jpg?Expires=1604414532&OSSAccessKeyId=TMP.3Kg5GL73vS9s6zQaqCTX561tV9GrDbynV5vSbzS6aLLoE96wXNQ2QARtcdxWKXemMDTCPbizytSjTmbUwWB2PsJM7J9hRt&Signature=Zja2w8pM1XQgKc5uPv1BvH68LTs%3D".length());
//        String imageStr = imageToBase64();
//        uploadImage(imageStr);
//        System.out.println(imageStr);
//        byte[] bytes = base64ToByte(imageStr);
//        OSSUtils.uploadFile("test/", "byte" + getFileSuffix(bytes), bytes);
    }

    /**
     * 创建新的文件名。
     * 格式为：时间字符串-UUID（去掉连接符的UUID）
     *
     * @return 文件名
     */
    public static String makeFileName() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String uuid = String.valueOf(UUID.randomUUID()).replace("-", "").substring(10);
        return sdf.format(new Date()) + "-" + uuid;
    }

    /**
     * 创建上传到OSS的文件目录。
     * 一级目录为年月，二级目录为年月日。
     *
     * @return 存储目录路径
     */
    public static String makeSavePath() {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

        // 一级目录
        String path1 = sdf1.format(new Date());
        // 二级目录
        String path2 = sdf2.format(new Date());

        return path1 + "/" + path2;
    }

    /**
     * 获取文件的后缀。
     *
     * @param bytes 字节数组
     * @return 后缀名
     * @throws IOException IO异常
     */
    public static String getFileSuffix(byte[] bytes) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
        String[] tokens = mimeType.split("[/]");
        String fileExtension = tokens[1];
//        System.out.println(fileExtension);
        return fileExtension;
    }

    /**
     * Base64字符串转化成字节数组。
     *
     * @param base64 base64字符串
     * @return 字节数组
     * @throws IOException IO异常
     */
    public static byte[] base64ToByte(String base64) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(base64);
    }



    /**
     * 判断文件格式是否符合要求。
     *
     * @param fileSuffix 文件后缀名
     * @return true符合，false不符合
     */
    public static Boolean checkFileFormat(String fileSuffix) {
        List<String> suffix = new ArrayList<>();
        suffix.add("jpg");
        suffix.add("png");
        suffix.add("jpeg");
        suffix.add("mp4");
        return suffix.contains(fileSuffix);
    }


    /**
     * 图片转化成base64字符串，用于测试。
     *
     * @return base64字符串
     */
    public static String imageToBase64() {
        String imgFile = "E:\\高清壁纸\\bird.png"; // 待处理的图片
        InputStream in;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

}
