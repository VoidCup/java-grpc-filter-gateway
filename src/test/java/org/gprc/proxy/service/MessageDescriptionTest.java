package org.gprc.proxy.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import org.gprc.proxy.service.protobean.BaseDataTypeMessageBean;
import org.grpc.proxy.BaseDataTypeMessage;
import org.grpc.proxy.Sex;
import org.grpc.proxy.grpc.MessageDescriptorService;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        messageBean.setFile(new byte[]{});
        JsonFormat.parser()
                .ignoringUnknownFields()
                .merge(JSONObject.toJSONString(messageBean),msgBuilder);
        System.out.println(protobufToJson(msgBuilder.build()));
    }

    @Test
    public void t3() throws Exception {
        BaseDataTypeMessage.Builder msgBuilder = BaseDataTypeMessage.newBuilder();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name","dxh");
        jsonObject.put("money",15000.2222);
        jsonObject.put("height",110);
        jsonObject.put("age",23);
        jsonObject.put("id",22222222222L);
        jsonObject.put("img",ByteString.copyFromUtf8("zzz").toByteArray());
        jsonObject.put("sex",Sex.MAN.name());
        jsonObject.put("file",ByteString.copyFromUtf8("dfadfadf").toByteArray());
        JSONObject m2 = new JSONObject();
        m2.put("n2","n2-----------1");
        m2.put("id2","n2---------2");
        jsonObject.put("m2",m2);
        JSONArray a2 = new JSONArray();
        a2.add("a2--1");
        a2.add("a2--2");
        jsonObject.put("a2",a2);
        JSONArray a3 = new JSONArray();
        a3.add(2L);
        a3.add(1L);
        jsonObject.put("a3",a3);
        JSONArray a4 = new JSONArray();
        a4.add(m2);
        a4.add(m2);
        jsonObject.put("a4",a4);
        JSONArray a5 = new JSONArray();
        a5.add(Sex.MAN.name());
        a5.add(Sex.MAN.name());
        jsonObject.put("a5",a5);
        JSONArray a6 = new JSONArray();
        a6.add(ByteString.copyFromUtf8("a61").toByteArray());
        a6.add(ByteString.copyFromUtf8("a62").toByteArray());
        jsonObject.put("a6",a6);
        JSONObject req = new JSONObject();
        req.put("name","req-1");
        req.put("age",2);
        req.put("other", Lists.newArrayList("o1","o2"));
        req.put("xFormUrl1","xFormUrl1");
        req.put("xFormUrl2",1222L);
        req.put("xFormUrl3","xFormUrl3");
        jsonObject.put("req",req);
        MessageDescriptorService.dynamicBuildMessage(msgBuilder,jsonObject);
        String s = protobufToJson(msgBuilder.build());
        System.out.println(s);
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
