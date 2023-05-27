package org.grpc.proxy.util;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-27 13:45
 * @description：
 */

public class StringUtils extends org.apache.commons.lang3.StringUtils {
    public static String toLowerCaseFirstOne(String str) {
        return Character.isLowerCase(str.charAt(0)) ? str : Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
