package fi.hut.soberit.agilefant.config;

import java.util.Map.Entry;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

@Configuration
@ComponentScan(basePackages = { "fi.hut.soberit.agilefant.db.hibernate", "fi.hut.soberit.agilefant.db.history.impl" })
public class DaoConfiguration {

    @Autowired
    private Config config;
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private DataSource dataSource;

    @Bean
    public AnnotationSessionFactoryBean sessionFactory() throws Exception {
        AnnotationSessionFactoryBean bean = new AnnotationSessionFactoryBean();
        bean.setConfigLocation(resourceLoader.getResource("/WEB-INF/hibernate.cfg.xml"));
        bean.setDataSource(dataSource);

        Properties props = new Properties();
        Config hibernateConfig = config.getConfig("agilefant.hibernate");

        for (Entry<String, ConfigValue> entry : hibernateConfig.entrySet()) {
            props.put(entry.getKey(), hibernateConfig.getString(entry.getKey()));
        }

        bean.setHibernateProperties(props);

        return bean;
    }

}
