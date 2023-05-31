package org.grpc.proxy.http;

import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-27 14:52
 * @description：
 * 包装httpServletRequest，以便于更好的操作request
 */

public class GrpcHttpRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request the {@link HttpServletRequest} to be wrapped.
     * @throws IllegalArgumentException if the request is null
     */
    public GrpcHttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public String getHttpRequestBody() throws IOException {
        return FileCopyUtils.copyToString(getRequest().getReader());
    }

    public HttpServletRequest getHttpServletRequest(){
        return (HttpServletRequest) getRequest();
    }
}
