package fi.hut.soberit.agilefant.db.export;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class Sqlfilecontentgenerator {

    private static List<String> extractTableNames(ResultSet rs) throws SQLException {
        List<String> names = new ArrayList<String>();
        while (rs.next()) {
            names.add(rs.getString("table_name"));
        }
        return names;
    }

    private static String createSqlString(List<String> names) {
        StringBuilder sb = new StringBuilder();

        for (String name : names) {
            sb.append("RENAME TABLE ");
            sb.append(name);
            sb.append(" TO ");
            sb.append(name.substring(name.indexOf('_') + 1));
            sb.append(';');
            sb.append(System.getProperty("line.separator"));
        }

        return sb.toString();
    }

    public static String generateSqlScript(DataSource dataSource) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'agilefant' AND table_name LIKE 'anonym_%'";

        return jdbc.query(query, new ResultSetExtractor<String>() {
            public String extractData(ResultSet rs) throws SQLException, DataAccessException {
                return createSqlString(extractTableNames(rs));
            }
        });
    }
}
