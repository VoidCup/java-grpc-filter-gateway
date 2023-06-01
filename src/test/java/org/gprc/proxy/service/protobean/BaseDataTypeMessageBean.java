package org.gprc.proxy.service.protobean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-06-01 23:03
 * @description：
 */
@Data
public class BaseDataTypeMessageBean implements Serializable {
    private String name;
    private Double money;
    private Float height;
    private Integer age;
    private Long id;
    private String img;
    private String sex;
}
