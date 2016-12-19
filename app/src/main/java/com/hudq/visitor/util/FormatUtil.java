package com.hudq.visitor.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FormatUtil {

    /**
     * 格式化显示文件大小
     * <p>
     * eg:2G;2.3M（小数位不为0显示一位小数，小数位为0不显示小数位）
     *
     * @param size
     * @return
     */
    public static String formatSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        long _10gb = 10 * gb;

        if (size > _10gb) {
            return "大于10G";
        } else if (size >= gb) {
            return String.format(Locale.getDefault(), "%.1fG", (float) size
                    / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            return String.format(Locale.getDefault(), f > 100 ? "%.0fM"
                    : "%.1fM", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            return String.format(Locale.getDefault(), f > 100 ? "%.0fKB"
                    : "%.1fKB", f);
        } else
            return String.format(Locale.getDefault(), "%dB", size);
    }

    /**
     * 格式化时间
     * <p>
     * eg: 2016-12-26 23:59
     *
     * @param mills
     * @return
     */
    public static String formatMills(long mills) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(mills));
    }


}
