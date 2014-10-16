package com.nx.config.security;

import com.nx.config.web.AccessFilter;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.session.mgt.SessionValidationScheduler;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
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

//    @Bean
//    public DefaultPasswordService defaultPasswordService() {
//        DefaultPasswordService defaultPasswordService = new DefaultPasswordService();
//        return defaultPasswordService;
//    }

    @Bean
    public EhCacheManager cacheManager() {
        EhCacheManager cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
        return cacheManager;
    }

    @Bean
    public RetryLimitHashedCredentialsMatcher credentialsMatcher() {
        RetryLimitHashedCredentialsMatcher matcher = new RetryLimitHashedCredentialsMatcher(cacheManager());
        matcher.setHashAlgorithmName("md5");
        matcher.setStoredCredentialsHexEncoded(true);
        return matcher;
    }

    @Bean
    public CustomSecurityRealm customSecurityRealm() {
        CustomSecurityRealm customSecurityRealm = new CustomSecurityRealm();
//        customSecurityRealm.setCredentialsMatcher(credentialsMatcher());
        customSecurityRealm.setCachingEnabled(true);
        customSecurityRealm.setAuthenticationCachingEnabled(true);
        customSecurityRealm.setAuthenticationCacheName("authenticationCache");
        customSecurityRealm.setAuthorizationCachingEnabled(true);
        customSecurityRealm.setAuthorizationCacheName("authorizationCache");
        return customSecurityRealm;
    }

    @Bean
    public EnterpriseCacheSessionDAO sessionDAO() {
        EnterpriseCacheSessionDAO sessionDAO = new EnterpriseCacheSessionDAO();
        sessionDAO.setActiveSessionsCacheName("shiro-activeSessionCache");
        sessionDAO.setSessionIdGenerator(new JavaUuidSessionIdGenerator());
        return sessionDAO;
    }

    @Bean
    public SimpleCookie sessionIdCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("sid");
        simpleCookie.setHttpOnly(true);
        simpleCookie.setMaxAge(180000);
        return simpleCookie;
    }

    @Bean
    public SessionValidationScheduler sessionValidationScheduler() {
        ExecutorServiceSessionValidationScheduler scheduler = new ExecutorServiceSessionValidationScheduler();
        scheduler.setInterval(3600000);
        return scheduler;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(sessionDAO());

        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionValidationScheduler(sessionValidationScheduler());


        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(sessionIdCookie());

        sessionManager.setGlobalSessionTimeout(1800000);
        sessionManager.setDeleteInvalidSessions(true);

        ((ExecutorServiceSessionValidationScheduler)sessionManager.getSessionValidationScheduler()).setSessionManager(sessionManager);
        return sessionManager;
    }

    @Bean
    public WebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customSecurityRealm());
        securityManager.setCacheManager(cacheManager());
        securityManager.setSessionManager(sessionManager());
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
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        return defaultAdvisorAutoProxyCreator;
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
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setSuccessUrl("/message");
        shiroFilter.setUnauthorizedUrl("/unauthorized");

        //Custom Filter
        shiroFilter.getFilters().put("accessFilter", new AccessFilter());

        //Definitions
        Map<String, String> definitionsMap = new HashMap<>();
        definitionsMap.put("/login", "authc");
        definitionsMap.put("/unauthorized", "anon");
        definitionsMap.put("/logout", "logout");
        definitionsMap.put("/resources/**", "anon");

        //If need config path , must add accessFilter
//        definitionsMap.put("/message/**", "accessFilter,authc");
        definitionsMap.put("/**", "accessFilter,authc");
        shiroFilter.setFilterChainDefinitionMap(definitionsMap);


        return shiroFilter;
    }
}
