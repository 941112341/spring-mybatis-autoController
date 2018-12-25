package framework;


import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.ParamsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class TestServlet extends DispatcherServlet {



    @Override
    protected void initStrategies(ApplicationContext context) {
        super.initStrategies(context);
        // 重新设置request
        XmlWebApplicationContext webApplicationContext = (XmlWebApplicationContext)context;
        BeanFactory factory = webApplicationContext.getBeanFactory();
        String basePackage = getInitParameter("basePackage");

        for (HandlerMapping handlerMapping : getHandlerMappings()) {
            if (handlerMapping instanceof RequestMappingHandlerMapping) {
                RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping)handlerMapping;
                try {
                    List<Class<?>> classes = PackageLoader.getClass(basePackage);
                    List<RequestMappingCollector> collectors = RequestMappingCollector.loadMapping(classes, factory);
                    collectors.forEach(collector -> requestMappingHandlerMapping.registerMapping(
                            collector.getMappingInfo(),
                            collector.getHandler(),
                            collector.getMethod()
                    ));
                } catch (FileNotFoundException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
