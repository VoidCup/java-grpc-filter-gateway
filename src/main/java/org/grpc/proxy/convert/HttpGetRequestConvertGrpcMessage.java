package org.grpc.proxy.convert;

import com.alibaba.fastjson2.JSONObject;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.apache.commons.io.IOUtils;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.grpc.proxy.http.GrpcHttpResponseWrapper;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import javax.servlet.ServletOutputStream;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-31 23:45
 * @description：
 * http get 请求方式转换消息
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class HttpGetRequestConvertGrpcMessage implements HttpMessageConvertGrpcMessage {

    @Override
    public boolean match(GrpcHttpRequestWrapper requestWrapper) {
        return HttpMethod.GET.matches(requestWrapper.getMethod());
    }

    @Override
    public void httpRequest2GrpcRequest(GrpcHttpRequestWrapper request,Message.Builder builder) throws Exception{
        String parameter = JSONObject.toJSONString(request.getParameterMap());
        JsonFormat.parser().ignoringUnknownFields().merge(parameter, builder);
    }

    @Override
    public void grpcResponse2HttpResponse(GrpcHttpResponseWrapper response, Message grpcResponse) throws Exception{
        String res = JsonFormat.printer()
                .includingDefaultValueFields()
                .omittingInsignificantWhitespace()
                .print(grpcResponse);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ServletOutputStream outputStream = response.getOutputStream();
        IOUtils.write(res,outputStream);
        outputStream.close();
    }
}
