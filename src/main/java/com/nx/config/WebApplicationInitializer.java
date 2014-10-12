package com.nx.config;

import com.nx.config.security.SecurityConfiguration;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Created by Neal on 2014-09-28.
 */
public class WebApplicationInitializer implements org.springframework.web.WebApplicationInitializer //extends AbstractAnnotationConfigDispatcherServletInitializer
{
//    @Override
//    protected Class<?>[] getRootConfigClasses() {
//        return new Class<?>[]{RootConfiguration.class};
//    }
//
//    @Override
//    protected Class<?>[] getServletConfigClasses() {
//        return new Class<?>[]{WebMvcConfiguration.class};
//    }
//
//    @Override
//    protected String[] getServletMappings() {
//        return new String[]{"/"};
//    }
//
//    @Override
//    protected Filter[] getServletFilters() {
//        //OpenEntityManagerInViewFilter
//
//        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
//        characterEncodingFilter.setEncoding("UTF-8");
//
////        DelegatingFilterProxy shiroFilter = new DelegatingFilterProxy();
////        shiroFilter.setTargetFilterLifecycle(true);
//
//        return new Filter[]{characterEncodingFilter};
//    }
//
//    @Override
//    public void onStartup(ServletContext servletContext) throws ServletException {
//        super.onStartup(servletContext);
//    }

    @Override
    public void onStartup(ServletContext container) throws ServletException {
        // Create the 'root' Spring application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(RepositoryConfiguration.class, SecurityConfiguration.class);

        // Manage the lifecycle of the root application context
        container.addListener(new ContextLoaderListener(rootContext));


        // Create the dispatcher servlet's Spring application context
        AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
        dispatcherContext.setServletContext(container);
        dispatcherContext.setParent(rootContext);
        dispatcherContext.register(WebMvcConfiguration.class);


        // Register and map the dispatcher servlet
        ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");


        container.addFilter("shiroFilter", new DelegatingFilterProxy("shiroFilterBean", dispatcherContext))
                .addMappingForUrlPatterns(null, false, "/*");
    }
}
