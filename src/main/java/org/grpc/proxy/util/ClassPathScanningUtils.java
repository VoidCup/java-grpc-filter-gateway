package org.grpc.proxy.util;

import org.grpc.proxy.grpc.ProtoGrpcServiceClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

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

    public static List<ProtoGrpcServiceClass> getAllServiceProtoClasses(String packageName) {
        List<ProtoGrpcServiceClass> serviceClasses = new ArrayList<>();
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(ProtoServiceClassFilter);
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
        return serviceClasses;
    }

    public static void main(String[] args) {
        List<ProtoGrpcServiceClass> allServiceProtoClasses = getAllServiceProtoClasses("org.grpc");
        for (ProtoGrpcServiceClass allServiceProtoClass : allServiceProtoClasses) {
            System.out.println(allServiceProtoClass);
        }
    }
}
