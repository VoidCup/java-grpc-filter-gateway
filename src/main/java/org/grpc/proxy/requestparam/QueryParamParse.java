package org.grpc.proxy.requestparam;

import com.alibaba.fastjson2.JSONObject;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-06-07 23:48
 * @description：
 * ?a=b&b=c&c=d&d=e的处理方式
 */

public class QueryParamParse {
    public static void parse(GrpcHttpRequestWrapper request, Message.Builder builder) throws Exception{
        String parameter = JSONObject.toJSONString(request.getParameterMap());
        JsonFormat.parser().ignoringUnknownFields().merge(parameter, builder);
    }
}
