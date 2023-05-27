package org.grpc.proxy.grpc;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

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
    void invoke(ServletRequest request, ServletResponse response, FilterChain chain) throws Throwable;
}
