package org.grpc.proxy.requestparam;

import com.alibaba.fastjson2.JSONObject;
import com.google.protobuf.Message;
import org.apache.commons.io.IOUtils;
import org.grpc.proxy.grpc.MessageDescriptorService;
import org.grpc.proxy.http.GrpcHttpRequestWrapper;
import org.grpc.proxy.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-10-21 16:35
 * @description：multipart/form-data
 * 消息解析不支持文件和其他方式混合的表单
 */

public class MultipartFormDataParse {

    public static void parse(GrpcHttpRequestWrapper request, Message.Builder builder) throws ServletException, IOException {
        JSONObject jsonObject = new JSONObject();
        for (Part part : request.getParts()) {
            byte[] byteArray = IOUtils.toByteArray(part.getInputStream());
            String name = part.getName();
            if(part.getContentType() != null
                    && StringUtils.isNotBlank(part.getSubmittedFileName())){
                jsonObject.put(name,byteArray);
            }
            else {
                jsonObject.put(name,new String(byteArray, StandardCharsets.UTF_8));
            }
        }
        MessageDescriptorService.dynamicBuildMessage(builder,jsonObject);
    }
}
