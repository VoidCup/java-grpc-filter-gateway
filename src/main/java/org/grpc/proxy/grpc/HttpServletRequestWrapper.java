package org.grpc.proxy.grpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.grpc.proxy.util.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @author dxh
 * @version 1.0.0
 * @date 2023-05-27 14:52
 * @description：
 * 包装httpServletRequest，以便于更好的操作request
 */

public class HttpServletRequestWrapper {
    private final HttpServletRequest request;

    public HttpServletRequestWrapper(HttpServletRequest request) {
        this.request = request;
    }

    public Map<String,String> getHead(){
        Map<String,String> headMap = Maps.newHashMap();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String s = headerNames.nextElement();
            String header = request.getHeader(s);
            if(StringUtils.isNotBlank(header)){
                headMap.put(s,header);
            }
        }
        return headMap;
    }

    public Map<String, List<String>> getQueryParam(){
        Map<String,List<String>> queryParamMap = Maps.newHashMap();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()){
            String s = parameterNames.nextElement();
            String[] parameterValues = request.getParameterValues(s);
            if(parameterValues != null && parameterValues.length > 0){
                queryParamMap.put(s,Lists.newArrayList(parameterValues));
            }
        }
        return queryParamMap;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public boolean isApplicationJsonType(){
        String header = request.getHeader(HttpHeaders.CONTENT_TYPE);
        return header.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    public MediaType getContentType(){
        String header = request.getHeader(HttpHeaders.CONTENT_TYPE);
        return MediaType.valueOf(header);
    }

    public Map<String, Object> getJsonBody() throws Exception{
        Map<String, Object> result = Maps.newHashMap();
        if(isApplicationJsonType()){
            ServletInputStream inputStream = request.getInputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.readValue(inputStream, result.getClass());
            result.putAll(map);
        }
        return result;
    }

    public String getHttpRequestBody() throws IOException {
        return FileCopyUtils.copyToString(request.getReader());
    }
}
