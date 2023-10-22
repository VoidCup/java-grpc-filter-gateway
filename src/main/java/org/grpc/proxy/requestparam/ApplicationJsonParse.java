package org.grpc.proxy.requestparam;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.springframework.http.MediaType;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-06-08 00:19
 * @description：application/json
 *
 */

public class ApplicationJsonParse {
    public static void parse(GrpcHttpRequestWrapper request, Message.Builder builder) throws Exception{
        JsonFormat.parser().ignoringUnknownFields().merge(request.getHttpRequestBody(), builder);
    }
}
