package org.grpc.proxy.grpc;

import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.grpc.proxy.http.GrpcHttpResponseWrapper;

import javax.servlet.FilterChain;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-27 13:57
 * @description：
 */
public interface Http2GrpcClientMethod {
    /**
     * 将http请求转成对应的grpc服务进行调用
     * @param request       http request
     * @param response      http response
     * @param chain         过滤器链
     * @throws Throwable    异常信息
     */
    void invoke(GrpcHttpRequestWrapper request, GrpcHttpResponseWrapper response, FilterChain chain) throws Throwable;
}
