package org.grpc.proxy.grpc;

import com.google.api.AnnotationsProto;
import com.google.api.HttpRule;
import com.google.common.collect.Lists;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import io.grpc.Channel;
import io.grpc.stub.AbstractStub;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.grpc.proxy.util.StringUtils;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-25 23:23
 * @description： protobuf生成的服务文件的解析
 */
public class ProtoGrpcServiceClass implements Serializable {


    //java_outer_className 输出的类文件后缀必须
    public static final String PROTO_SERVICE_SUFFIX = "ServiceProto";
    public static final String PROTO_SUFFIX = "Proto";
    public static final String GRPC_SUFFIX = "Grpc";
    public static final String STUB_SUFFIX = "Stub";
    public static final String BLOCKING_STUB_SUFFIX = "BlockingStub";
    public static final String FUTURE_STUB = "FutureStub";
    public static final String GET_DESCRIPTOR_METHOD = "getDescriptor";
    public static final String NEW_BLOCKING_STUB_METHOD = "newBlockingStub";
    public static final String NEW_BUILDER_METHOD = "newBuilder";
    public static final String DOT = ".";
    public static final String DOLLAR = "$";

    //option java_outer_classname = "HelloWorldServiceProto";
    private Class<?> serviceProtoClass;
    // serviceName + GRPC_SUFFIX
    private Class<?> grpcClass;
    // serviceName + GRPC_SUFFIX + "$" + serviceName + STUB_SUFFIX
    private Class<?> stubClass;
    // serviceName + GRPC_SUFFIX + "$" + serviceName + BLOCKING_STUB_SUFFIX
    private Class<?> blockingStubClass;
    // serviceName + GRPC_SUFFIX + "$" + serviceName + FUTURE_STUB
    private Class<?> futureStubClass;
    //serviceName - PROTO_SUFFIX
    private String serviceName;
    private String packageName;

    public static ProtoGrpcServiceClass parseServiceProtoClass(Class<?>  serviceProtoClass) throws ClassNotFoundException {
        ProtoGrpcServiceClass pbClass = new ProtoGrpcServiceClass();
        pbClass.setPackageName(serviceProtoClass.getPackage().getName());
        pbClass.setServiceProtoClass(serviceProtoClass);
        String simpleServiceName = serviceProtoClass.getSimpleName().replace(PROTO_SUFFIX,"");
        pbClass.setServiceName(simpleServiceName);
        String grpcClassName = pbClass.getPackageName() + DOT + simpleServiceName + GRPC_SUFFIX;
        pbClass.setGrpcClass(Class.forName(grpcClassName));
        pbClass.setStubClass(Class.forName(grpcClassName + DOLLAR + simpleServiceName + STUB_SUFFIX));
        pbClass.setBlockingStubClass(Class.forName(grpcClassName + DOLLAR + simpleServiceName + BLOCKING_STUB_SUFFIX));
        pbClass.setFutureStubClass(Class.forName(grpcClassName + DOLLAR + simpleServiceName + FUTURE_STUB));
        return pbClass;
    }

    /**
     * 从pbGrpcClass中解析出 api method
     */
    public List<HttpMethodInfo> parseGrpcHttpMethod() throws Exception {
        List<HttpMethodInfo> httpMethodInfoList = Lists.newArrayList();
        Descriptors.ServiceDescriptor serviceDescriptor = getServiceDescriptor();
        for (Descriptors.MethodDescriptor methodDescriptor : serviceDescriptor.getMethods()) {
            if(methodDescriptor.getOptions().hasExtension(AnnotationsProto.http)){
                HttpMethodInfo httpMethodInfo = new HttpMethodInfo();
                HttpRule httpRule = methodDescriptor.getOptions().getExtension(AnnotationsProto.http);
                //判断是否存在自定义的http method方法
                String httpPath = "";
                HttpMethod httpMethod = null;
                if (httpRule.getPatternCase() == HttpRule.PatternCase.CUSTOM) {
                    // 使用自定义HTTP路径,暂不支持直接跳过
                    continue;
                } else if (httpRule.getPatternCase() == HttpRule.PatternCase.GET) {
                    // 使用GET方法和路径
                    httpPath = httpRule.getGet();
                    httpMethod = HttpMethod.GET;
                } else if (httpRule.getPatternCase() == HttpRule.PatternCase.PUT) {
                    // 使用PUT方法和路径
                    httpPath = httpRule.getPut();
                    httpMethod = HttpMethod.PUT;
                } else if (httpRule.getPatternCase() == HttpRule.PatternCase.POST) {
                    // 使用POST方法和路径
                    httpPath = httpRule.getPost();
                    httpMethod = HttpMethod.POST;
                } else if (httpRule.getPatternCase() == HttpRule.PatternCase.DELETE) {
                    // 使用DELETE方法和路径
                    httpPath = httpRule.getDelete();
                    httpMethod = HttpMethod.DELETE;
                } else if (httpRule.getPatternCase() == HttpRule.PatternCase.PATCH) {
                    // 使用PATCH方法和路径
                    httpPath = httpRule.getPatch();
                    httpMethod = HttpMethod.PATCH;
                }
                String methodName = StringUtils.toLowerCaseFirstOne(methodDescriptor.getName());
                httpMethodInfo.setMethodName(methodName);
                httpMethodInfo.setPath(httpPath);
                httpMethodInfo.setHttpMethod(httpMethod);
                httpMethodInfo.setNewBlockingStub(getNewBlockingStubMethod());
                httpMethodInfo.setRequestMessageBuilder(getRequestNewBuilder(methodDescriptor));
                httpMethodInfo.setRpcMethod(getGrpcInvokeMethod(methodDescriptor));
                httpMethodInfoList.add(httpMethodInfo);
            }
        }
        return httpMethodInfoList;
    }

    /**
     * 从protoCLass中获取
     * @return {@link Descriptors.FileDescriptor}
     * @throws Exception exception异常
     */
    public Descriptors.FileDescriptor getFileDescriptor() throws Exception{
        return (Descriptors.FileDescriptor)getServiceProtoClass()
                .getMethod(ProtoGrpcServiceClass.GET_DESCRIPTOR_METHOD).invoke((Object)null, (Object[])null);
    }

    /**
     * 从protoClass中获取
     * @return {@link Descriptors.ServiceDescriptor}
     * @throws Exception exception异常
     */
    public Descriptors.ServiceDescriptor getServiceDescriptor() throws Exception{
        return getFileDescriptor().findServiceByName(getServiceName());
    }

    /**
     * 获取newBlockingStub方法
     HelloWorldServiceGrpc.HelloWorldServiceBlockingStub blockingStub
     = HelloWorldServiceGrpc.newBlockingStub(channel);
     * @throws Exception
     */
    public Method getNewBlockingStubMethod() throws Exception{
        return getGrpcClass().getMethod(ProtoGrpcServiceClass.NEW_BLOCKING_STUB_METHOD, Channel.class);
    }

    /**
     * @param methodDescriptor 获取该方法的输入请求的protobuf构建方法
     * @return HelloRequest.newBuilder()
     * @throws Exception
     */
    public Method getRequestNewBuilder(Descriptors.MethodDescriptor methodDescriptor) throws Exception{
        String inputTypeName = methodDescriptor.getInputType().getName();
        return Class.forName(getPackageName()+DOT+inputTypeName).getMethod(NEW_BUILDER_METHOD);
    }

    /**
     * public void sayHello(org.grpc.proxy.HelloRequest request,
     *     io.grpc.stub.StreamObserver<org.grpc.proxy.HelloReply> responseObserver) {
     *   asyncUnimplementedUnaryCall(getSayHelloMethod(), responseObserver);
     * }
     * @return public void sayHello(org.grpc.proxy.HelloRequest request,
     *              io.grpc.stub.StreamObserver<org.grpc.proxy.HelloReply> responseObserver)
     */
    public Method getGrpcInvokeMethod(Descriptors.MethodDescriptor methodDescriptor) throws Exception{
        //获取request Message类名
        Message.Builder requestMessageBuilder = (Message.Builder)getRequestNewBuilder(methodDescriptor)
                .invoke((Object)null);
        String methodName = StringUtils.toLowerCaseFirstOne(methodDescriptor.getName());
        return getBlockingStubClass().getMethod(methodName, requestMessageBuilder.build().getClass());
    }

    public Class<?> getServiceProtoClass() {
        return serviceProtoClass;
    }

    public void setServiceProtoClass(Class<?> serviceProtoClass) {
        this.serviceProtoClass = serviceProtoClass;
    }

    public Class<?> getGrpcClass() {
        return grpcClass;
    }

    public void setGrpcClass(Class<?> grpcClass) {
        this.grpcClass = grpcClass;
    }

    public Class<?> getStubClass() {
        return stubClass;
    }

    public void setStubClass(Class<?> stubClass) {
        this.stubClass = stubClass;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Class<?> getBlockingStubClass() {
        return blockingStubClass;
    }

    public void setBlockingStubClass(Class<?> blockingStubClass) {
        this.blockingStubClass = blockingStubClass;
    }

    public Class<?> getFutureStubClass() {
        return futureStubClass;
    }

    public void setFutureStubClass(Class<?> futureStubClass) {
        this.futureStubClass = futureStubClass;
    }

    @Override
    public String toString() {
        return "PbGrpcClassGroup{" +
                "serviceProtoClass=" + serviceProtoClass +
                ", grpcClass=" + grpcClass +
                ", stubClass=" + stubClass +
                ", blockingStubClass=" + blockingStubClass +
                ", futureStubClass=" + futureStubClass +
                ", serviceName='" + serviceName + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ProtoGrpcServiceClass)) return false;

        ProtoGrpcServiceClass that = (ProtoGrpcServiceClass) o;

        return new EqualsBuilder().append(getServiceProtoClass(), that.getServiceProtoClass()).append(getGrpcClass(), that.getGrpcClass()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getServiceProtoClass()).append(getGrpcClass()).toHashCode();
    }

    /**
     * grpc-api服务的方法
     */
    public static class HttpMethodInfo {
        private String path;
        private HttpMethod httpMethod;
        private String methodName;
        private Method newBlockingStub;
        private Method requestMessageBuilder;
        private Method rpcMethod;

        public Message.Builder newBuilder() throws Exception{
            return getMessageBuilder((Object) null);
        }

        public Message.Builder getMessageBuilder(Object target,Object... args) throws Exception{
            return (Message.Builder)requestMessageBuilder.invoke(target,args);
        }

        public Object getBlockingStub(Object target, Object... args) throws Exception{
            return newBlockingStub.invoke(target,args);
        }

        public Message invokeRpc(Channel channel, Message request, Map<String,String> metadata) throws Exception{
            AbstractStub blockingStub = (AbstractStub) getBlockingStub((Object) null, channel);
            return (Message) rpcMethod.invoke(blockingStub, request);
        }

        public Message invokeRpc(Channel channel, Message request) throws Exception{
            Object blockingStub = getBlockingStub((Object) null, channel);
            return (Message) rpcMethod.invoke(blockingStub, request);
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public Method getNewBlockingStub() {
            return newBlockingStub;
        }

        public void setNewBlockingStub(Method newBlockingStub) {
            this.newBlockingStub = newBlockingStub;
        }

        public Method getRequestMessageBuilder() {
            return requestMessageBuilder;
        }

        public void setRequestMessageBuilder(Method requestMessageBuilder) {
            this.requestMessageBuilder = requestMessageBuilder;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public Method getRpcMethod() {
            return rpcMethod;
        }

        public void setRpcMethod(Method rpcMethod) {
            this.rpcMethod = rpcMethod;
        }
    }
}
