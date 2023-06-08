package org.grpc.proxy.requestparam;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.springframework.http.MediaType;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-06-08 00:19
 * @description：
 *
 */

public class ApplicationJsonParse {
    public static void parse(GrpcHttpRequestWrapper request, Message.Builder builder) throws Exception{
        if(MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType()) ||
            MediaType.APPLICATION_JSON.equals(request.getContentType())){
            JsonFormat.parser().ignoringUnknownFields().merge(request.getHttpRequestBody(), builder);
        }
    }
}
