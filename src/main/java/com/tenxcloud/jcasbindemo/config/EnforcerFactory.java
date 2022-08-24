package com.tenxcloud.jcasbindemo.config;

import com.tenxcloud.jcasbindemo.entry.Policy;
import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class EnforcerFactory implements InitializingBean {

    private static Enforcer enforcer;

    @Autowired
    private EnforcerConfigProperties enforcerConfigProperties;
    private static EnforcerConfigProperties config;

    @Override
    public void afterPropertiesSet() throws Exception {
        //读取配置文件
        config = enforcerConfigProperties;
        //从数据库读取策略
        JDBCAdapter jdbcAdapter = new JDBCAdapter(config.getDriverClassName(), config.getUrl(), config.getUsername(), config.getPassword());
        enforcer = new Enforcer(config.getModelPath(), jdbcAdapter);
        enforcer.loadPolicy();//Load the policy from DB.
    }

    /**
     * 添加权限
     *
     * @param policy
     * @return
     */
    public static boolean addPolicy(Policy policy) {
        boolean addPolicy = enforcer.addPolicy("admin", policy.getObj(), policy.getAct());
        enforcer.addGroupingPolicy(policy.getSub(),"admin");
        enforcer.savePolicy();

        return addPolicy;
    }

    /**
     * 修改权限
     *
     * @param policy
     * @return
     */
    public static boolean updatePolicy(Policy policy) {
//        boolean addPolicy = enforcer.addPolicy("admin", policy.getObj(), policy.getAct());
//        enforcer.addGroupingPolicy(policy.getSub(),"admin");
        String[][] rules = {
                {"jack", "/api/v1/data-service/user/list", "GET"},
                {"katy", "/api/v1/data-service/user/list", "GET"},
                {"leyo", "/api/v1/data-service/user/list", "GET"},
                {"ham", "/api/v1/data-service/user/list", "GET"},
        };
        boolean addPolicy = enforcer.addPolicies(rules);
        enforcer.savePolicy();

        return addPolicy;
    }

    /**
     * 删除权限
     *
     * @param policy
     * @return
     */
    public static boolean removePolicy(Policy policy) {
        boolean removePolicy = enforcer.removePolicy(policy.getSub(), policy.getObj(), policy.getAct());
        enforcer.savePolicy();

        return removePolicy;
    }

    public static Enforcer getEnforcer(){
        return enforcer;
    }
}