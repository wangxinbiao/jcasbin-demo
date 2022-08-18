package com.tenxcloud.jcasbindemo.config;

import com.tenxcloud.jcasbindemo.entry.Policy;
import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnforcerFactory implements InitializingBean {

    private static Enforcer enforcer;

    @Autowired
    private EnforcerConfigProperties enforcerConfigProperties;
    private static EnforcerConfigProperties config;

    @Override
    public void afterPropertiesSet() throws Exception {
        config = enforcerConfigProperties;
        //从数据库读取策略
        JDBCAdapter jdbcAdapter = new JDBCAdapter(config.getDriverClassName(), config.getUrl(), config.getUsername(), config.getPassword(), true);
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

    public static Enforcer getEnforcer() {
        //从数据库读取策略
        JDBCAdapter jdbcAdapter = new JDBCAdapter(config.getDriverClassName(), config.getUrl(), config.getUsername(), config.getPassword(), true);
        enforcer = new Enforcer(config.getModelPath(), jdbcAdapter);
        enforcer.loadPolicy();//Load the policy from DB.
        return enforcer;
    }
}