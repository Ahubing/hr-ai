package com.open.ai.eros.common.util;

/**
 * @项目名：blue-cat-api
 * @创建人：Administrator
 * @创建时间：2023/10/7 13:18
 */
public class TimeUtil {

    /**
     * 将毫秒数转换为秒，保留3位小数
     *
     * @param milliseconds 毫秒数，需输入非负数
     * @return 转换后的秒数
     */
    public static String formatDuration(long milliseconds) {
        // 判断参数有效性
        if (milliseconds < 0) {
            throw new IllegalArgumentException("毫秒数不能为负数: " + milliseconds);
        }

        double seconds = (double) milliseconds / 1000;

        // 生成保留三位小数的字符串表示
        return String.format("%.3f", seconds) + "s";
    }
}
