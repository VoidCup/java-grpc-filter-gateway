package org.grpc.proxy.filter;

import org.grpc.proxy.grpc.Http2GrpcClientMethod;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.grpc.proxy.http.GrpcHttpResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-25 21:36
 * @description：
 * http restful-api 转成 grpc 服务
 */
@Component
public class GrpcGatewayFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(GrpcGatewayFilter.class);

    @Autowired
    private Http2GrpcClientMethod http2GrpcClientMethod;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {
        try {
            GrpcHttpRequestWrapper requestWrapper = new GrpcHttpRequestWrapper((HttpServletRequest) request);
            GrpcHttpResponseWrapper responseWrapper = new GrpcHttpResponseWrapper((HttpServletResponse) response);
            http2GrpcClientMethod.invoke(requestWrapper,responseWrapper,chain);

        }catch (Throwable throwable){
            log.error("http2GrpcClientMethod invoke error",throwable);
            throw new ServletException(throwable);
        }
    }
}
