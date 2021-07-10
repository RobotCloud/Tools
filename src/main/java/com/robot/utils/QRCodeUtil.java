package com.robot.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类。
 *
 * @Author 张宝旭
 * @Date 2020/12/15
 */
public class QRCodeUtil {

    public static void main(String[] args) {
        generateQRCodePic("www.baidu.com", QRCODE_SIZE, QRCODE_SIZE, "png");
    }



    /**
     * 定义日期格式
     */
    public static DateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * 二维码尺寸
     */
    public static final int QRCODE_SIZE = 300;

    /**
     * 生成二维码。
     *
     * @param content 二维码携带内容
     * @param width 二维码宽
     * @param height 二维码高
     * @param picFormat 图片后缀
     */
    public static void generateQRCodePic(String content, int width, int height, String picFormat) {

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            // 构造二维字节矩阵
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 构造文件目录，若目录不存在，则创建目录
            String fileDir = "D:\\DEV_QR_CODE" + File.separator + "image" + File.separator + sf.format(new Date());
            if (!new File(fileDir).exists()) {
                new File(fileDir).mkdirs();
            }
            Path file = new File(fileDir + File.separator + "qrcode." + picFormat).toPath();

            // 信息块颜色，背景颜色
            MatrixToImageConfig config = new MatrixToImageConfig(0xFF000000, 0xFFFFFFFF);
            // 将二位字节矩阵按照指定图片格式，写入指定文件目录，生成二维码图片
            MatrixToImageWriter.writeToPath(bitMatrix, picFormat, file, config);
        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

}
