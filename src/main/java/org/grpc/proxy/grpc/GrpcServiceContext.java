package org.grpc.proxy.grpc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.grpc.Channel;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.grpc.proxy.convert.HttpGetRequestConvertGrpcMessage;
import org.grpc.proxy.convert.HttpMessageConvertGrpcMessage;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.grpc.proxy.util.ClassPathScanningUtils;
import org.grpc.proxy.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-26 23:31
 * @description：
 */
@Component
@Configuration
@ConfigurationProperties(prefix = "grpc.server")
public class GrpcServiceContext implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(GrpcServiceContext.class);
    //grpc.server.port属性
    private int port;
    // base package
    private List<String> basePackages = Lists.newArrayList();
    // exclude url
    private List<String> excludeUrls = Lists.newArrayList();
    //convert packages
    private List<String> convertPackages = Lists.newArrayList();
    @GrpcClient("localhost")
    private Channel channel;

    //key: option (google.api.http){post = "uri"} 中的uri
    //value: GrpcMethodInfo
    private final Map<String, ProtoGrpcServiceClass.HttpMethodInfo> grpcMethodApi = Maps.newHashMap();

    private final List<HttpGetRequestConvertGrpcMessage> httpConvertGrpc = Lists.newArrayList();

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!CollectionUtils.isEmpty(basePackages)) {
            List<ProtoGrpcServiceClass> pbGrpcClassList
                    = ClassPathScanningUtils.getAllServiceProtoClasses(basePackages.toArray(new String[0]));
            log.info("start parse protobuf Class size: {}", pbGrpcClassList.size());
            for (ProtoGrpcServiceClass pbGrpcClass : pbGrpcClassList) {
                List<ProtoGrpcServiceClass.HttpMethodInfo> httpMethodInfoList = pbGrpcClass.parseGrpcHttpMethod();
                for (ProtoGrpcServiceClass.HttpMethodInfo httpMethodInfo : httpMethodInfoList) {
                    if (!excludeUrls.contains(httpMethodInfo.getPath())) {
                        log.info("register grpc service api, grpc service = {}, grpc api = {}, http-url = {}",
                                pbGrpcClass.getServiceName(), httpMethodInfo.getMethodName(), httpMethodInfo.getPath());
                        grpcMethodApi.put(httpMethodInfo.getPath(), httpMethodInfo);
                    }
                }
            }
        }
        if(!CollectionUtils.isEmpty(getConvertPackages())){
            httpConvertGrpc.addAll(ClassPathScanningUtils.getHttpConvertGrpcMsg(convertPackages.toArray(new String[0])));
        }
    }

    public HttpGetRequestConvertGrpcMessage matchConvert(GrpcHttpRequestWrapper requestWrapper){
        for (HttpGetRequestConvertGrpcMessage convertGrpcMessage : httpConvertGrpc) {
            if(convertGrpcMessage.match(requestWrapper)){
                return convertGrpcMessage;
            }
        }
        return null;
    }

    public ProtoGrpcServiceClass.HttpMethodInfo getHttpMethodInfo(String path){
        return grpcMethodApi.get(path);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public List<String> getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public List<String> getConvertPackages() {
        return convertPackages;
    }

    public void setConvertPackages(List<String> convertPackages) {
        this.convertPackages = convertPackages;
    }
}
