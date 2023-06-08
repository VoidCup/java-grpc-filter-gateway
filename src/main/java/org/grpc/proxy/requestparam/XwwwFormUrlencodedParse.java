package org.grpc.proxy.requestparam;

import com.alibaba.fastjson2.JSONObject;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.grpc.proxy.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-06-07 23:46
 * @description：
 * Content-Type: application/x-www-form-urlencoded
 * 上述请求内容处理方式
 */
public class XwwwFormUrlencodedParse {
    private static final Logger log = LoggerFactory.getLogger(XwwwFormUrlencodedParse.class);
    public static void parse(GrpcHttpRequestWrapper request, Message.Builder builder) throws Exception{
        String contentType = request.getContentType();
        if(MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType)){
            String requestBody = request.getHttpRequestBody();
            Map<String, String> stringMap = StringUtils.splitAndEq(requestBody);
            JsonFormat.parser().ignoringUnknownFields().merge(JSONObject.toJSONString(stringMap), builder);
        }
    }
}
