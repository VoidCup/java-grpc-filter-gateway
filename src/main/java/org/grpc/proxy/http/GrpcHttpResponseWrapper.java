package org.grpc.proxy.http;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-31 23:19
 * @description：
 * 包装httpResponse以便于更好的操作response
 */

public class GrpcHttpResponseWrapper extends HttpServletResponseWrapper {

    public GrpcHttpResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse) getResponse();
    }
}
