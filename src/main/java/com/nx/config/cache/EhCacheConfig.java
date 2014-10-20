package com.nx.config.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.*;
import org.springframework.cache.jcache.interceptor.SimpleExceptionCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;

/**
 * Created by Neal on 10/20 020.
 */
//@Configuration
public class EhCacheConfig{
    final static String CACHE_POLICY = "LRU";

    @Bean(destroyMethod = "shutdown")
    public net.sf.ehcache.CacheManager ehCacheManager() {
//        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
//        config.addCache(cacheConfig("userCache", 500));
//
        EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
        factoryBean.setConfigLocation(new PathResource("/ehcache.xml"));
        factoryBean.setShared(true);

        return factoryBean.getObject();

//        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean(name = "springCacheManager")
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

    @Bean
    public CacheResolver cacheResolver() {
        return new SimpleExceptionCacheResolver(cacheManager());
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler();
    }

//    private CacheConfiguration cacheConfig(String name, long maxEntries) {
//        CacheConfiguration config = new CacheConfiguration();
//        config.setName(name);
//        config.setMaxEntriesLocalHeap(maxEntries);
//        config.setMemoryStoreEvictionPolicy(CACHE_POLICY);
//        config.setEternal(true);
//        config.setOverflowToOffHeap(false);
//        return config;
//    }
}
