package framework;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class RequestMappingCollector {

    private Method method;
    private RequestMappingInfo mappingInfo;
    private Object handler;

    public RequestMappingCollector(Method method, RequestMappingInfo mappingInfo, Object handler) {
        this.method = method;
        this.mappingInfo = mappingInfo;
        this.handler = handler;
    }

    public static List<RequestMappingCollector> loadMapping(List<Class<?>> classes, BeanFactory factory) {
        List<RequestMappingCollector> mappingCollectors = new ArrayList<>();
        for (Class<?> clazz : classes) {
            RequestMappingInfo classInfo = clazz.isAnnotationPresent(RequestMapping.class) ?
                    getMappingInfo(clazz.getAnnotation(RequestMapping.class)):
                    getMappingInfo(getBaseName(clazz));
            for (Method method : clazz.getDeclaredMethods()) {

                RequestMappingInfo info = method.isAnnotationPresent(RequestMapping.class) ?
                        getMappingInfo(method.getAnnotation(RequestMapping.class)) :
                        getMappingInfo(method.getName());

                Object handler = factory.getBean(clazz);
                mappingCollectors.add(new RequestMappingCollector(method, classInfo.combine(info), handler));
            }
        }
        return mappingCollectors;
    }

    private static RequestMappingInfo getMappingInfo(String url) {
        return new RequestMappingInfo(
                new PatternsRequestCondition(url),
                null, null, null, null, null, null
        );
    }

    private static String getBaseName(Class<?> clazz) {
        String prefix = clazz.getSimpleName();
        return StringUtils.uncapitalize(prefix).substring(0, prefix.length() - 6);
    }

    private static RequestMappingInfo getMappingInfo(RequestMapping mapping) {
        return new RequestMappingInfo(
                new PatternsRequestCondition(mapping.value()),
                new RequestMethodsRequestCondition(mapping.method()),
                new ParamsRequestCondition(mapping.params()),
                new HeadersRequestCondition(mapping.headers()),
                new ConsumesRequestCondition(mapping.consumes()),
                new ProducesRequestCondition(mapping.produces()),
                null
        );
    }


    public static String[] addArrayPrefix(String[] strings, String prefix) {
        for (int i = 0; i < strings.length; i++) {
            strings[i] = prefix + "/" + strings[i];
        }
        return strings;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public RequestMappingInfo getMappingInfo() {
        return mappingInfo;
    }

    public void setMappingInfo(RequestMappingInfo mappingInfo) {
        this.mappingInfo = mappingInfo;
    }

    public Object getHandler() {
        return handler;
    }

    public void setHandler(Object handler) {
        this.handler = handler;
    }
}
