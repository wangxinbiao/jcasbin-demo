package com.tenxcloud.jcasbindemo.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.tenxcloud.jcasbindemo.config.EnforcerFactory;
import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@WebFilter(urlPatterns = "/*" , filterName = "JCasbinAuthzFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)//执行顺序，最高级别最先执行，int从小到大
public class JCasbinAuthzFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(JCasbinAuthzFilter.class);

    private static Enforcer enforcer;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String user = request.getParameter("name");
        String path = request.getRequestURI();
        String method = request.getMethod();

        enforcer = EnforcerFactory.getEnforcer();
        if (path.contains("anon")) {
            chain.doFilter(request, response);
        } else if (enforcer.enforce(user, path, method)) {
            chain.doFilter(request, response);
        } else {
            log.info("无权访问");
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("code", 1001);
            result.put("msg", "用户权限不足");
            result.put("data", null);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(JSONObject.toJSONString(result, SerializerFeature.WriteMapNullValue));
        }

    }

    @Override
    public void destroy() {

    }
}
