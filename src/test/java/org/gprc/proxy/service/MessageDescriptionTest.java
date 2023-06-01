package org.gprc.proxy.service;

import com.alibaba.fastjson2.JSONObject;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.gprc.proxy.service.protobean.BaseDataTypeMessageBean;
import org.grpc.proxy.BaseDataTypeMessage;
import org.grpc.proxy.Sex;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-06-01 21:55
 * @description：
 */

public class MessageDescriptionTest {
    @Test
    public void t1() throws Exception {
        Message.Builder msgBuilder = BaseDataTypeMessage.newBuilder();
        Descriptors.Descriptor descriptor = msgBuilder.getDescriptorForType();
        formatMessage(descriptor);
    }

    @Test
    public void t2() throws Exception {
        Message.Builder msgBuilder = BaseDataTypeMessage.newBuilder();
        BaseDataTypeMessageBean messageBean = new BaseDataTypeMessageBean();
        messageBean.setName("dxh");
        messageBean.setId(12543254252454L);
        messageBean.setSex(Sex.MAN.name());
        messageBean.setAge(12);
        messageBean.setHeight(1111.11F);
        messageBean.setMoney(234342.3242D);
        messageBean.setImg("YWJjMTIzIT8kKiYoKSctPUB+");
        JsonFormat.parser()
                .ignoringUnknownFields()
                .merge(JSONObject.toJSONString(messageBean),msgBuilder);
        System.out.println(protobufToJson(msgBuilder.build()));
    }

    public String protobufToJson(Message message) throws Exception {
        return JsonFormat.printer().print(message);
    }

    public void formatMessage(Descriptors.Descriptor descriptor){
        StringBuilder stringBuilder = new StringBuilder();
        String name = descriptor.getName();
        stringBuilder.append("name = ").append(name).append("\n");
        List<Descriptors.EnumDescriptor> enumTypes = descriptor.getEnumTypes();
        if(!CollectionUtils.isEmpty(enumTypes)){
            stringBuilder.append("enumTypes = [");
            for (int i = 0; i < enumTypes.size(); i++) {
                Descriptors.EnumDescriptor enumDescriptor = enumTypes.get(i);
                stringBuilder.append(enumDescriptor.getName());
                if(i < enumTypes.size() - 1){
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append("]\n");
        }
        if(!CollectionUtils.isEmpty(descriptor.getFields())){
            stringBuilder.append("fields = [");
            List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
            for (int i = 0; i < fields.size(); i++) {
                Descriptors.FieldDescriptor fieldDescriptor = fields.get(i);
                stringBuilder.append("{name = ")
                        .append(fieldDescriptor.getName()).append(",")
                        .append("javaType = ")
                        .append(fieldDescriptor.getJavaType().name())
                        .append("}");
                if(i < fields.size() - 1){
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append("]\n");
        }
        System.out.println(stringBuilder);
    }
}
