package org.grpc.proxy.util;

import com.google.common.collect.Lists;
import org.grpc.proxy.convert.HttpGetRequestConvertGrpcMessage;
import org.grpc.proxy.convert.HttpMessageConvertGrpcMessage;
import org.grpc.proxy.grpc.ProtoGrpcServiceClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-25 23:07
 * @description：
 * ClassPathScanningCandidateComponentProvider类扫描指定包名下的所有类
 */

public class ClassPathScanningUtils {
    private static final Logger log = LoggerFactory.getLogger(ClassPathScanningUtils.class);

    private static final TypeFilter ProtoServiceClassFilter = (metadataReader, metadataReaderFactory) -> {
        String className = metadataReader.getClassMetadata().getClassName();
        return className.endsWith(ProtoGrpcServiceClass.PROTO_SERVICE_SUFFIX);
    };

    public static List<HttpGetRequestConvertGrpcMessage> getHttpConvertGrpcMsg(String... packageNames){
        List<Class<?>> convertClassList = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(HttpMessageConvertGrpcMessage.class));
        for (String packageName : packageNames) {
            for (BeanDefinition beanDefinition : provider.findCandidateComponents(packageName)) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    convertClassList.add(clazz);
                } catch (ClassNotFoundException e) {
                    // 处理类未找到的异常
                    log.error(beanDefinition.getBeanClassName()+": class not found exception",e);
                }
            }
        }
        List<HttpGetRequestConvertGrpcMessage> orderConvert = Lists.newArrayList();
        try {
            //不存在order注解的直接放到最前面
            Iterator<Class<?>> iterator = convertClassList.iterator();
            while (iterator.hasNext()){
                Class<?> convertClazz = iterator.next();
                Order annotation = convertClazz.getAnnotation(Order.class);
                //不存在直接移除并创建该实例
                if(annotation == null){
                    Constructor<?> constructor = convertClazz.getConstructor();
                    orderConvert.add((HttpGetRequestConvertGrpcMessage) constructor.newInstance());
                    iterator.remove();
                }
            }
            //存在order注解进行排序
            convertClassList.sort((t1, t2) -> {
                int v1 = t1.getAnnotation(Order.class).value();
                int v2 = t2.getAnnotation(Order.class).value();
                return v2 - v1;
            });
            for (Class<?> convertClazz : convertClassList) {
                Constructor<?> constructor = convertClazz.getConstructor();
                orderConvert.add((HttpGetRequestConvertGrpcMessage) constructor.newInstance());
            }
        } catch (Exception e){
            log.error("create HttpGetRequestConvertGrpcMessage object fail");
        }
        return orderConvert;
    }

    public static List<ProtoGrpcServiceClass> getAllServiceProtoClasses(String... packageNames) {
        Set<ProtoGrpcServiceClass> serviceClasses = new HashSet<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(ProtoServiceClassFilter);
        for (String packageName : packageNames) {
            for (BeanDefinition beanDefinition : provider.findCandidateComponents(packageName)) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    ProtoGrpcServiceClass pbGrpcClassGroup = ProtoGrpcServiceClass.parseServiceProtoClass(clazz);
                    serviceClasses.add(pbGrpcClassGroup);
                } catch (ClassNotFoundException e) {
                    // 处理类未找到的异常
                    log.error(beanDefinition.getBeanClassName()+": class not found exception",e);
                }
            }
        }
        return Lists.newArrayList(serviceClasses);
    }

    public static void main(String[] args) {
        List<ProtoGrpcServiceClass> allServiceProtoClasses = getAllServiceProtoClasses("org.grpc");
        for (ProtoGrpcServiceClass allServiceProtoClass : allServiceProtoClasses) {
            System.out.println(allServiceProtoClass);
        }
    }
}
