package com.robot.utils;

import java.math.BigDecimal;

/**
 * 小数处理。
 *
 * @Author 张宝旭
 * @Date 2021/2/8
 */
public class DecimalUtils {

    /**
     * 删除BigDecimal中小数点后的最后面连续的0。
     * @param bigDecimal 原数据
     * @return 处理之后的数据
     */
    public static BigDecimal removeLastZero(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return null;
        }
        String source = String.valueOf(bigDecimal);
        if (!source.contains(".")) {
            // 说明不是小数，不处理
            return bigDecimal;
        }
        int count = 0;
        for (int i = source.length() - 1 ; i >= 0 ; i--) {
            char charAt = source.charAt(i);
            if (charAt == '0') {
                count++;
            } else {
                source = source.substring(0, source.length() - count);
                break;
            }
        }
        return new BigDecimal(source);
    }
}
