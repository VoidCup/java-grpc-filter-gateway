package org.grpc.proxy.service;


import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.io.FileUtils;
import org.grpc.proxy.*;
import org.springframework.http.HttpStatus;

import java.io.File;

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
            UserDto.Builder userBuilder = UserDto.newBuilder();
            userBuilder.setName("dxh");
            userBuilder.setAge(1);
            userBuilder.setHeight(1.11);
            userBuilder.setId(122434234L);
            userBuilder.setIdCard("dfadfadfadfadfadfa");
            userBuilder.addAllTags(Lists.newArrayList("t1","t2","t3"));
            replyBuilder.setRequest(request);
            replyBuilder.setTotal(1);
            replyBuilder.setPages(1);
            replyBuilder.setCurrentPage(1);
            replyBuilder.addUsers(userBuilder);
            replyBuilder.setCode(HttpStatus.OK.value());
            replyBuilder.setMessage(HttpStatus.OK.name());
        } catch (Exception e) {
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
        SimplePostReply.Builder replyBuilder = SimplePostReply.newBuilder();
        try {
            ByteString file = request.getFile();
            String name = request.getName() + request.getAge();
            FileUtils.writeByteArrayToFile(new File("/Users/xinhuadeng/JavaProject/grpc-filter-gateway/src/main/resources/"+name+".txt"),
                    file.toByteArray());
            replyBuilder.setCode(HttpStatus.OK.value());
            replyBuilder.setMessage(HttpStatus.OK.name());
        } catch (Exception e) {
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
