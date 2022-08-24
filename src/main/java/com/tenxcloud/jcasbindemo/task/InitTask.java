package com.tenxcloud.jcasbindemo.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tenxcloud.jcasbindemo.config.EnforcerFactory;
import com.tenxcloud.jcasbindemo.entry.Policy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InitTask implements ApplicationRunner {

    @Autowired
    WebApplicationContext applicationContext;

    private static Enforcer enforcer;

    @Override
    public void run(ApplicationArguments args) {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            Map<String, String> map1 = new HashMap<String, String>();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();

            map1.put("className", method.getMethod().getDeclaringClass().getName()); // 类名
            map1.put("method", method.getMethod().getName()); // 方法名
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                map1.put("type", requestMethod.toString());
            }

            // 获取swagger，@Api注解中的tags
            Annotation[] annotations = method.getBeanType().getAnnotations();
            if (annotations.length > 0) {
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Api) {
                        Api api = (Api) annotation;
                        String[] tags = api.tags();
                        if (tags.length > 0) {
                            map1.put("classDesc", api.tags()[0]);
                        }
                    }
                }
            }
            // 获取swagger，@ApiOperation注解中的values
            Annotation[] declaredAnnotations = method.getMethod().getDeclaredAnnotations();
            if (declaredAnnotations.length > 0) {
                // 处理具体的方法信息
                for (Annotation annotation : declaredAnnotations) {
                    if (annotation instanceof ApiOperation) {
                        ApiOperation methodDesc = (ApiOperation) annotation;
                        String desc = methodDesc.value();
                        map1.put("desc",desc);
                    }
                }
            }

            for (String url : p.getPatterns()) {
                //过滤不需要的url
                PathMatcher matcher = new AntPathMatcher();
                boolean match = matcher.match("/api/v1/**", url);
                if (match){
                    map1.put("url", url);
                    list.add(map1);
                }
            }
        }
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list));
        System.out.println(jsonArray);

        enforcer = EnforcerFactory.getEnforcer();
        List<List<String>> permissionsForUser = enforcer.getPermissionsForUser("admin", "p");
        if (list.size() != permissionsForUser.size()){
            for (Map<String,String> m : list){
                Policy p = new Policy();
                p.setSub("admin");
                p.setObj(m.get("url"));
                p.setAct(m.get("type"));
                EnforcerFactory.addPolicy(p);
            }
        }
    }
}
