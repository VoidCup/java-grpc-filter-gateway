package org.gprc.proxy.service;

import com.google.protobuf.util.JsonFormat;
import io.grpc.Channel;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.grpc.proxy.HelloReply;
import org.grpc.proxy.HelloRequest;
import org.grpc.proxy.HelloWorldServiceGrpc;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-27 16:45
 * @description：
 */
@SpringBootTest
public class GrpcClientTest {
    @GrpcClient("localhost")
    private Channel channel;

    @Test
    public void sendMessage() throws Exception{
        //获取一个阻塞的stub
        HelloWorldServiceGrpc.HelloWorldServiceBlockingStub blockingStub
                = HelloWorldServiceGrpc.newBlockingStub(channel);
        //构建请求参数
        HelloRequest.Builder builder = HelloRequest.newBuilder();
        builder.setName("aaa");
        //发送请求
        HelloReply helloReply = blockingStub.sayHello(builder.build());
        //响应消息
        String print = JsonFormat.printer().print(helloReply);
        System.out.println(print);
    }
}
