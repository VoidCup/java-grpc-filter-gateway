package org.grpc.proxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-25 22:31
 * @description：
 */
@SpringBootApplication
public class GrpcGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GrpcGatewayApplication.class, args);
    }

}
