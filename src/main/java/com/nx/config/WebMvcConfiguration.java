package com.nx.config;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.tiles2.dialect.TilesDialect;
import org.thymeleaf.extras.tiles2.spring4.web.configurer.ThymeleafTilesConfigurer;
import org.thymeleaf.extras.tiles2.spring4.web.view.ThymeleafTilesView;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neal on 2014-09-28.
 */
@Configurable
@EnableWebMvc
@ComponentScan("com.nx.controller")
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    private boolean develop = true;
    @Autowired
    private WebSecurityManager securityManager;

    @Autowired
    private FormattingConversionService mvcConversionService;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/").setCachePeriod(31556926);
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

//    @Bean
//    public MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter() {
//        MappingJacksonHttpMessageConverter mappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
//        mappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(new MediaType("text/html;charset=UTF-8")));
//        return mappingJacksonHttpMessageConverter;
//    }

    @Bean
    public TemplateResolver templateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("views/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCacheable(!develop);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.addDialect(new TilesDialect());
        return templateEngine;
    }

    @Bean
    public ViewResolver tilesViewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");
        viewResolver.setContentType("text/html;charset=UTF-8");
        viewResolver.setOrder(Ordered.LOWEST_PRECEDENCE);
        viewResolver.setCache(!develop);
        viewResolver.setViewClass(ThymeleafTilesView.class);
        return viewResolver;
    }

    @Bean
    public ThymeleafTilesConfigurer tilesConfigurer() {
        ThymeleafTilesConfigurer tilesConfigurer = new ThymeleafTilesConfigurer();
        tilesConfigurer.setDefinitions(new String[]{"classpath:tiles/tiles-def.xml"});
        tilesConfigurer.setCheckRefresh(develop);
        return tilesConfigurer;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterBean(){
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        Map<String, String> definitionsMap = new HashMap<>();
        definitionsMap.put("/login", "authc");
        definitionsMap.put("/message/**", "authc, roles[admin]");
        definitionsMap.put("/**", "authc");
        shiroFilter.setFilterChainDefinitionMap(definitionsMap);
        shiroFilter.setLoginUrl("/login");
        shiroFilter.setSuccessUrl("/message");
        shiroFilter.setSecurityManager(securityManager);
        return shiroFilter;
    }

    @Bean
    public DomainClassConverter<?> domainClassConverter() {
        return new DomainClassConverter<>(mvcConversionService);
    }
}
