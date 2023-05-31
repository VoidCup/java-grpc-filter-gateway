package org.grpc.proxy.grpc;

import com.google.protobuf.Message;
import org.grpc.proxy.convert.HttpGetRequestConvertGrpcMessage;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.grpc.proxy.http.GrpcHttpResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;

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
    public void invoke(GrpcHttpRequestWrapper request, GrpcHttpResponseWrapper response, FilterChain filterChain) throws Throwable {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        ProtoGrpcServiceClass.HttpMethodInfo httpMethodInfo = grpcServiceContext.getHttpMethodInfo(uri);
        //如果该restful-api无法转成grpcMethod那么直接放行
        if(httpMethodInfo == null){
            filterChain.doFilter(request,response);
        }
        //验证http方法是否一致
        else if (!httpMethodInfo.getHttpMethod().matches(method)){
            response.sendError(HttpStatus.METHOD_NOT_ALLOWED.value(),HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        }
        //开始将http请求转换成grpc请求
        else {
            log.info("start http transform grpc, uri = {}, grpc method = {}",
                    httpMethodInfo.getPath(),httpMethodInfo.getMethodName());
            HttpGetRequestConvertGrpcMessage convertGrpcMessage = grpcServiceContext.matchConvert(request);
            if(convertGrpcMessage == null){
                throw new NullPointerException("not found HttpGetRequestConvertGrpcMessage");
            }
            //开始调用grpc-api Method
            Message.Builder builder = httpMethodInfo.newBuilder();
            convertGrpcMessage.httpRequest2GrpcRequest(request,builder);
            Message responseMessage = httpMethodInfo.invokeRpc(grpcServiceContext.getChannel(), builder.build());
            convertGrpcMessage.grpcResponse2HttpResponse(response,responseMessage);
        }
    }
}
