package org.grpc.proxy.grpc;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import org.grpc.proxy.util.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-10-21 17:29
 * @description：
 */

public class MessageDescriptorService {
    private static final Map<Message,
            Map<String, Descriptors.FieldDescriptor>>
        MESSAGE_DESCRIPTOR = Maps.newConcurrentMap();

    public static Map<String,Descriptors.FieldDescriptor> getFieldsDescriptorMap(Message message){
        Map<String, Descriptors.FieldDescriptor> descriptorMap
                = MESSAGE_DESCRIPTOR.getOrDefault(message,Maps.newHashMap());
        if(descriptorMap == null || descriptorMap.isEmpty()){
            List<Descriptors.FieldDescriptor> descriptorList
                    = message.getDescriptorForType().getFields();
            Map<String, Descriptors.FieldDescriptor> collected = descriptorList.stream()
                    .collect(Collectors.toMap(Descriptors.FieldDescriptor::getName, t -> t, (t1, t2) -> t1));
            MESSAGE_DESCRIPTOR.put(message,collected);
            return collected;
        }
        return descriptorMap;
    }

    public static Descriptors.FieldDescriptor getFieldsDescriptor(Message message,String fieldName){
        return getFieldsDescriptorMap(message).get(fieldName);
    }

    public static void dynamicBuildMessage(Message.Builder builder, JSONObject jsonObject){
        Map<String, Descriptors.FieldDescriptor> descriptorMap
                = getFieldsDescriptorMap(builder.getDefaultInstanceForType());
        for (String s : jsonObject.keySet()) {
            Descriptors.FieldDescriptor descriptor = descriptorMap.get(s);
            String fieldName = descriptor.getName();
            boolean repeated = descriptor.isRepeated();
            switch (descriptor.getJavaType()){
                case INT:{
                    Object rs;
                    if(repeated){
                        rs = jsonObject.getList(fieldName, Integer.class);
                    }
                    else {
                        rs = jsonObject.getIntValue(fieldName, 0);
                    }
                    builder.setField(descriptor,rs);
                    break;
                }
                case LONG:{
                    Object rs;
                    if(repeated){
                        rs = jsonObject.getList(fieldName, Long.class);
                    }
                    else {
                        rs = jsonObject.getLongValue(fieldName, 0);
                    }
                    builder.setField(descriptor,rs);
                    break;
                }
                case DOUBLE:{
                    Object rs;
                    if(repeated){
                        rs = jsonObject.getList(fieldName, Double.class);
                    }
                    else {
                        rs = jsonObject.getDoubleValue(fieldName);
                    }
                    builder.setField(descriptor,rs);
                    break;
                }
                case FLOAT:{
                    Object rs;
                    if(repeated){
                        rs = jsonObject.getList(fieldName, Float.class);
                    }
                    else {
                        rs = jsonObject.getFloatValue(fieldName);
                    }
                    builder.setField(descriptor,rs);
                    break;
                }
                case BOOLEAN:{
                    Object rs;
                    if(repeated){
                        rs = jsonObject.getList(fieldName, Boolean.class);
                    }
                    else {
                        rs = jsonObject.getBooleanValue(fieldName, false);
                    }
                    builder.setField(descriptor,rs);
                    break;
                }
                case STRING:{
                    Object rs;
                    if(repeated){
                        rs = jsonObject.getList(fieldName, String.class);
                    }
                    else {
                        rs = jsonObject.getString(fieldName);
                    }
                    builder.setField(descriptor,rs);
                    break;
                }
                case BYTE_STRING:{
                    Object rs = null;
                    if(repeated){
                        List<byte[]> list = jsonObject.getList(fieldName, byte[].class);
                        if(!CollectionUtils.isEmpty(list)){
                            rs = list.stream().filter(Objects::nonNull).map(ByteString::copyFrom).collect(Collectors.toList());
                        }
                    }
                    else {
                        byte[] data = jsonObject.getObject(fieldName, byte[].class);
                        rs = data == null ? ByteString.EMPTY : ByteString.copyFrom(data);
                    }
                    builder.setField(descriptor,rs);
                    break;
                }
                case ENUM:{
                    Object rs;
                    if(repeated){
                        List<String> list = jsonObject.getList(fieldName, String.class);
                        if(CollectionUtils.isEmpty(list)){
                            break;
                        }
                        rs = list.stream()
                                .filter(StringUtils::isNotBlank).map(t ->
                                        descriptor.getEnumType().findValueByName(t.toUpperCase(Locale.ROOT)))
                                .collect(Collectors.toList());
                    }
                    else {
                        String ev = jsonObject.getString(fieldName);
                        if(StringUtils.isBlank(ev)){
                            break;
                        }
                        rs = descriptor.getEnumType().findValueByName(ev.toUpperCase(Locale.ROOT));
                    }
                    builder.setField(descriptor,rs);
                    break;
                }
                case MESSAGE:{
                    Object rs;
                    if(repeated){
                        List<DynamicMessage> dynamicMessageList = Lists.newArrayList();
                        JSONArray ja = jsonObject.getJSONArray(fieldName);
                        for (int index = 0; index < ja.size(); index++) {
                            DynamicMessage.Builder dynamicMsgBuilder = DynamicMessage.newBuilder(descriptor.getMessageType());
                            dynamicBuildMessage(dynamicMsgBuilder,ja.getJSONObject(index));
                            dynamicMessageList.add(dynamicMsgBuilder.build());
                        }
                        rs = dynamicMessageList;
                    }
                    else {
                        DynamicMessage.Builder dynamicMsgBuilder = DynamicMessage.newBuilder(descriptor.getMessageType());
                        dynamicBuildMessage(dynamicMsgBuilder,jsonObject.getJSONObject(fieldName));
                        rs = dynamicMsgBuilder.build();
                    }
                    builder.setField(descriptor,rs);
                }
                default:{
                    break;
                }
            }
        }
    }
}
