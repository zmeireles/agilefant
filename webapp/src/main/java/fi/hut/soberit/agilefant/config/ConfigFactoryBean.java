package fi.hut.soberit.agilefant.config;

import org.springframework.beans.factory.FactoryBean;

import com.typesafe.config.Config;

public class ConfigFactoryBean implements FactoryBean<Config> {

    private final Config object = BootstrapperListener.getConfig();

    @Override
    public Config getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return Config.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
