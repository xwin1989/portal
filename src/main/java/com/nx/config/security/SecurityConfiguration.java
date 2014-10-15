package com.nx.config.security;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by neal.xu on 2014/10/10.
 */
@Configuration
public class SecurityConfiguration {

    @Bean
    public CustomSecurityRealm customSecurityRealm() {
        return new CustomSecurityRealm();
    }

    @Bean
    public DefaultPasswordService defaultPasswordService() {
        DefaultPasswordService defaultPasswordService = new DefaultPasswordService();
        return defaultPasswordService;
    }

    @Bean
    public WebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customSecurityRealm());
        EnterpriseCacheSessionDAO cacheSessionDAO = new EnterpriseCacheSessionDAO();
        cacheSessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");

        return securityManager;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        methodInvokingFactoryBean.setArguments(new Object[]{securityManager()});
        return methodInvokingFactoryBean;
    }

    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    @Bean(name = "shiroFilterBean")
    public ShiroFilterFactoryBean shiroFilterBean() {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        Map<String, String> definitionsMap = new HashMap<>();
        definitionsMap.put("/login", "authc");
        definitionsMap.put("/logout", "logout");

        definitionsMap.put("/resources/**", "anon");
        //If need config path , must add accessFilter
//        definitionsMap.put("/message/**", "accessFilter,authc");
        definitionsMap.put("/**", "accessFilter,authc");
        shiroFilter.setFilterChainDefinitionMap(definitionsMap);
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setSuccessUrl("/message");
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setUnauthorizedUrl("/message");
        AccessFilter accessFilter = new AccessFilter();
        shiroFilter.getFilters().put("accessFilter", accessFilter);

        return shiroFilter;
    }
}
