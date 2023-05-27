package org.grpc.proxy.service;


import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.grpc.proxy.HelloReply;
import org.grpc.proxy.HelloRequest;
import org.grpc.proxy.HelloWorldServiceGrpc;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-25 22:22
 * @description：
 */
@GrpcService
public class HelloWorldGrpcService extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello ==> " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
