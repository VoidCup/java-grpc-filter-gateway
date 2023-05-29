package org.grpc.proxy.grpc;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-27 14:00
 * @description：
 */
@Service
public class HttpGrpcClientMethodService implements Http2GrpcClientMethod{
    private static final Logger log = LoggerFactory.getLogger(HttpGrpcClientMethodService.class);
    @Autowired
    private GrpcServiceContext grpcServiceContext;

    @Override
    public void invoke(ServletRequest request, ServletResponse response, FilterChain filterChain) throws Throwable {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        String uri = httpServletRequest.getRequestURI();
        String method = httpServletRequest.getMethod();
        ProtoGrpcServiceClass.HttpMethodInfo httpMethodInfo = grpcServiceContext.getHttpMethodInfo(uri);
        //如果该restful-api无法转成grpcMethod那么直接放行
        if(httpMethodInfo == null){
            filterChain.doFilter(request,response);
        }
        //验证http方法是否一致
        else if (!httpMethodInfo.getHttpMethod().matches(method)){
            httpServletResponse.sendError(HttpStatus.METHOD_NOT_ALLOWED.value(),HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        }
        //开始将http请求转换成grpc请求
        else {
            log.info("start http transform grpc, uri = {}, grpc method = {}",
                    httpMethodInfo.getPath(),httpMethodInfo.getMethodName());
            doInvokeGrpc(httpServletRequest,httpServletResponse,httpMethodInfo);
        }
    }

    private void doInvokeGrpc(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           ProtoGrpcServiceClass.HttpMethodInfo httpMethodInfo) throws Throwable{
        HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(httpServletRequest);
        Map<String, String> head = requestWrapper.getHead();
        if (httpMethodInfo.getHttpMethod() == HttpMethod.GET) {
            // 使用GET方法和路径
        } else if (httpMethodInfo.getHttpMethod() == HttpMethod.PUT) {
            // 使用PUT方法和路径
        } else if (httpMethodInfo.getHttpMethod() == HttpMethod.POST) {
            // 使用POST方法和路径
        } else if (httpMethodInfo.getHttpMethod() == HttpMethod.DELETE) {
            // 使用DELETE方法和路径
        } else if (httpMethodInfo.getHttpMethod() == HttpMethod.PATCH) {
            // 使用PATCH方法和路径
        }
        Message.Builder builder = httpMethodInfo.newBuilder();
        JsonFormat.parser().merge(requestWrapper.getHttpRequestBody(), builder);
        Message responseMessage = httpMethodInfo
                .invokeRpc(grpcServiceContext.getChannel(), builder.build(),head);
        String res = JsonFormat.printer().includingDefaultValueFields().omittingInsignificantWhitespace().print(responseMessage);
        httpServletResponse.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        IOUtils.write(res,outputStream);
        outputStream.close();
    }
}
