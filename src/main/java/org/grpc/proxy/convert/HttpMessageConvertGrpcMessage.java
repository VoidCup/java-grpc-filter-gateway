package org.grpc.proxy.convert;

import com.google.protobuf.Message;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.grpc.proxy.http.GrpcHttpResponseWrapper;

/**
 * http请求转成grpc请求
 * grpc响应转成http响应
 */
public interface HttpMessageConvertGrpcMessage {

    /**
     * 是否匹配到使用该转换器
     * @param requestWrapper http 请求
     */
    boolean match(GrpcHttpRequestWrapper requestWrapper);

    /**
     * http请求转成grpc请求
     * @param request http请求
     * @param builder grpc请求消息
     */
    void httpRequest2GrpcRequest(GrpcHttpRequestWrapper request, Message.Builder builder) throws Exception;

    /**
     * 将Grpc消息写出到http response
     * @param response http response
     * @param grpcResponse grpc response
     */
    void grpcResponse2HttpResponse(GrpcHttpResponseWrapper response, Message grpcResponse) throws Exception;
}
