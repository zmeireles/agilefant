package fi.hut.soberit.agilefant.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class DatabaseInitializer implements InitializingBean {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DataSource dataSource;

    @Override
    public void afterPropertiesSet() throws Exception {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);

        if (jdbc.queryForInt("SELECT COUNT(*) FROM users") > 0) {
            log.info("Users table has data -> skipping initialization");
            return;
        }

        jdbc.update("INSERT INTO users (fullName, loginName, passwd, initials, enabled, recentItemsNumberOfWeeks) VALUES (?, ?, ?, ?, ?, ?)",
                "Administrator", "admin", "$2a$10$fkULKc1/AmruYR9HTmh5ROc5692D3WaPidZ7dHz073W33AvU2Vqom", "Admin", 1, 16);
        jdbc.update("INSERT INTO users (admin, fullName, loginName, passwd, initials, enabled, recentItemsNumberOfWeeks) VALUES (?, ?, ?, ?, ?, ?, ?)", 0,
                "readonly", "readonly", "$2a$10$fkULKc1/AmruYR9HTmh5ROc5692D3WaPidZ7dHz073W33AvU2Vqom", "readonly", 1, 0);

        log.info("Initialized users table with default users");
    }

}
