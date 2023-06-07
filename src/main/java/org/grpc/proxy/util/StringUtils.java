package org.grpc.proxy.util;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-27 13:45
 * @description：
 */

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    public static final String AND = "&";
    public static final String EQ = "=";
    public static final String COMMA = ",";

    private static final Splitter SPLITTER_AND = Splitter.on(AND)
            .omitEmptyStrings()
            .trimResults();
    private static final Splitter SPLITTER_EQ = Splitter.on(EQ)
            .omitEmptyStrings()
            .trimResults();
    private static final Splitter SPLITTER_COMMA = Splitter.on(COMMA)
            .omitEmptyStrings()
            .trimResults();

    public static List<String> splitSeparator(String text,String separator){
        if(AND.equals(separator)){
            return Lists.newArrayList(SPLITTER_AND.split(text));
        }
        else if (EQ.equals(separator)){
            return Lists.newArrayList(SPLITTER_EQ.split(text));
        }
        else if (COMMA.equals(separator)){
            return Lists.newArrayList(SPLITTER_COMMA.split(text));
        }
        else {
            Splitter splitter = Splitter.on(separator)
                    .omitEmptyStrings()
                    .trimResults();
            return Lists.newArrayList(splitter.split(text));
        }
    }

    /**
     * 分割 k1=vl&k2=v2&k3=v3 转成map
     */
    public static Map<String,String> splitAndEq(String text){
        return splitSeparator(text, AND)
                .stream()
                .map(t -> t.split(EQ))
                .collect(Collectors.toMap(t -> t[0], t -> t[1], (t1,t2) -> t2));
    }

    public static String toLowerCaseFirstOne(String str) {
        return Character.isLowerCase(str.charAt(0)) ? str : Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
