package org.grpc.proxy.service;


import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.grpc.proxy.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import javax.annotation.Resource;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-25 22:22
 * @description：
 */
@GrpcService
public class HelloWorldGrpcService extends HelloWorldServiceGrpc.HelloWorldServiceImplBase {

    @Override
    public void getRequest(SimpleGetRequest request, StreamObserver<SimpleGetReply> responseObserver) {
        SimpleGetReply.Builder replyBuilder = SimpleGetReply.newBuilder();
        try {
            String res = JsonFormat.printer()
                    .includingDefaultValueFields()
                    .omittingInsignificantWhitespace()
                    .print(request);
            JsonFormat.parser().ignoringUnknownFields().merge(res, replyBuilder);
            replyBuilder.setCode(HttpStatus.OK.value());
            replyBuilder.setMessage(HttpStatus.OK.name());
        } catch (InvalidProtocolBufferException e) {
            replyBuilder.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            replyBuilder.setMessage(HttpStatus.INTERNAL_SERVER_ERROR.name());
            throw new RuntimeException(e);
        }
        finally {
            responseObserver.onNext(replyBuilder.build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void postRequest(SimplePostRequest request, StreamObserver<SimplePostReply> responseObserver) {
        super.postRequest(request, responseObserver);
    }

    @Override
    public void putRequest(SimplePutRequest request, StreamObserver<SimplePutReply> responseObserver) {
        super.putRequest(request, responseObserver);
    }

    @Override
    public void deleteRequest(SimpleDeleteRequest request, StreamObserver<SimpleDeleteReply> responseObserver) {
        super.deleteRequest(request, responseObserver);
    }

    @Override
    public void patchRequest(SimplePatchRequest request, StreamObserver<SimplePatchReply> responseObserver) {
        super.patchRequest(request, responseObserver);
    }
}
