package fi.hut.soberit.agilefant.config;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.ContextLoaderListener;

import com.google.common.base.Preconditions;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Resolves configuration for the application, and bootstraps Spring.
 * 
 * <p>
 * It's possible to create the configuration in Spring context instead of using "magical" thread locals, but that
 * solution becomes problematic if we need access to the config in a class initialized by reflection. Many frameworks
 * (e.g. Hibernate) initialize extension classes directly without any support for injection, so the config must be
 * available through other means. Static fields are also an option, but a thread local is a much cleaner solution as
 * long as the thread local is cleaned immediately after initialization.
 * 
 */
public class BootstrapperListener implements ServletContextListener {

    /**
     * Holds the immutable config object during initialization
     */
    private static final ThreadLocal<Config> CONFIG = new ThreadLocal<Config>();

    /**
     * Returns the current config from the thread local, which is only available at initialization time.
     * 
     * <strong>This method exists only to pass Spring the config object without hassle</strong>
     * 
     * @throws NullPointerException
     *             if thread local is not available.
     * @return config object
     */
    static Config getConfig() {
        return Preconditions.checkNotNull(CONFIG.get(), "thread local config is not available");
    }

    private final ContextLoaderListener springListener = new ContextLoaderListener();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Config config = resolveConfig(sce.getServletContext());

        try {
            CONFIG.set(config);
            springListener.contextInitialized(sce);
        } finally {
            CONFIG.remove();
        }
    }

    /**
     * Parses and resolves an immutable config object from default sources.
     * 
     * The standard config chain is the following:
     * <ul>
     * <li>Servlet context init parameters
     * <li>Default overrides (System properties)
     * <li>Config from classpath (agilefant.conf)
     * <li>Default config from WAR (/WEB-INF/agilefant.conf)
     * 
     * @param ctx
     *            servlet context
     * @return config object
     */
    private Config resolveConfig(ServletContext ctx) {
        Config servletParams = readServletParams(ctx);
        Config overrides = ConfigFactory.defaultOverrides();
        Config classpathConfig = ConfigFactory.parseResources("agilefant.conf");
        Config warConfig = readWarConfig(ctx);

        Config config = servletParams.withFallback(overrides).withFallback(classpathConfig).withFallback(warConfig).resolve();

        return config;
    }

    /**
     * Parses an immutable config object from configuration file in the WAR.
     * 
     * @param ctx
     *            servlet context
     * @return config object
     */
    private Config readWarConfig(ServletContext ctx) {
        try {
            URL url = Preconditions.checkNotNull(ctx.getResource("/WEB-INF/agilefant.conf"), "WAR config could not be found");
            return ConfigFactory.parseURL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Failed to parse config from WAR", e);
        }
    }

    /**
     * Creates an immutable config object from servlet context init parameters.
     * 
     * @param ctx
     *            servlet context
     * @return config object
     */
    private Config readServletParams(ServletContext ctx) {
        Properties properties = new Properties();
        Enumeration<String> names = ctx.getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            properties.put(names, ctx.getInitParameter(name));
        }
        return ConfigFactory.parseProperties(properties);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        springListener.contextDestroyed(sce);
    }

}
