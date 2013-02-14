package fi.hut.soberit.agilefant.db.export;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.support.JdbcUtils;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.DbConnectionInfo;

public class Atablesmodifier {
    public class AnonymousColumn {
        public final String tableName;
        public final String columnName;
        public final String dataType;
        public final boolean isUnique;

        public AnonymousColumn(String tableName, String columnName, String dataType, boolean isUnique) {
            this.tableName = tableName;
            this.columnName = columnName;
            this.dataType = dataType;
            this.isUnique = isUnique;
        }

    }

    private final JdbcTemplate jdbc;
    private final DbConnectionInfo dbInfo;

    private final ImmutableList<String> tables;
    private final ImmutableList<AnonymousColumn> columns;

    public Atablesmodifier(DataSource dataSource, DbConnectionInfo dbInfo) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        this.jdbc = new JdbcTemplate(dataSource);
        this.dbInfo = dbInfo;
        // important - initalizeAnonymizedColumns must be called BEFORE initializeTables
        this.columns = initializeAnonymizedColumns();
        this.tables = initializeTables();
    }

    public void dublicaTables() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        /**
         * generates anonymity tables with orginal non-anonymized data.
         * 
         */
        try {
            jdbc.execute(new StatementCallback<Void>() {
                @Override
                public Void doInStatement(Statement stmt) throws SQLException, DataAccessException {
                    for (String table : tables) {
                        stmt.executeUpdate("CREATE TABLE " + "anonym_" + table + " LIKE " + table);
                        stmt.executeUpdate("INSERT INTO " + "anonym_" + table + " SELECT * FROM " + table);
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            deletetables();
            Throwables.propagate(e);
        }
    }

    public void deletetables() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final ImmutableList<String> anonymousTables = jdbc.execute(new ConnectionCallback<ImmutableList<String>>() {
            @Override
            public ImmutableList<String> doInConnection(Connection con) throws SQLException, DataAccessException {
                DatabaseMetaData meta = con.getMetaData();
                ImmutableList.Builder<String> results = ImmutableList.builder();
                ResultSet rs = null;
                try {
                    rs = meta.getTables(con.getCatalog(), null, "anonym_%", new String[] { "TABLE" });
                    while (rs.next()) {
                        results.add(rs.getString("table_name"));
                    }
                    return results.build();
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }
            }
        });
        jdbc.execute(new StatementCallback<Void>() {
            @Override
            public Void doInStatement(Statement stmt) throws SQLException, DataAccessException {
                for (String table : anonymousTables) {
                    stmt.executeUpdate("DROP TABLE " + table);
                }
                return null;
            }
        });
    }

    // Modify anonym_table columns value to be anonymous
    // Change all Columns that have string value to "tablename id - length:[value length]" example
    // "stories 7 - length:10"
    // If columns is UNIQUE then replace the value with its id
    public void anonymizeTables() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final String passwordHash = SecurityUtil.MD5("password");

        jdbc.execute(new StatementCallback<Void>() {
            @Override
            public Void doInStatement(Statement stmt) throws SQLException, DataAccessException {
                for (AnonymousColumn column : columns) {
                    if (column.isUnique) {
                        stmt.executeUpdate("UPDATE anonym_" + column.tableName + " SET " + column.columnName + " = id;");
                    } else {
                        if ("users".equalsIgnoreCase(column.tableName) && "password".equalsIgnoreCase(column.columnName)) {
                            stmt.execute("UPDATE anonym_" + column.tableName + " SET " + column.columnName + " = \"" + passwordHash + "\";");
                        } else {
                            stmt.executeUpdate("UPDATE anonym_" + column.tableName + " SET " + column.columnName + " = CONCAT(\"" + column.tableName + " \","
                                    + "id, \" - length:\", LENGTH(" + column.columnName + "),\" - hash:\",MD5(" + column.columnName + "));");
                        }
                    }
                }
                return null;
            }
        });
    }

    // Get All tables from database
    public ImmutableList<String> initializeTables() {
        return jdbc.execute(new ConnectionCallback<ImmutableList<String>>() {
            @Override
            public ImmutableList<String> doInConnection(Connection con) throws SQLException, DataAccessException {
                DatabaseMetaData meta = con.getMetaData();
                ImmutableList.Builder<String> results = ImmutableList.builder();
                ResultSet rs = null;
                try {
                    rs = meta.getTables(con.getCatalog(), null, "%", new String[] { "TABLE" });
                    while (rs.next()) {
                        results.add(rs.getString("table_name"));
                    }
                    return results.build();
                } finally {
                    JdbcUtils.closeResultSet(rs);
                }
            }
        });
    }

    public ImmutableList<AnonymousColumn> initializeAnonymizedColumns() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return jdbc.query("SELECT table_name, column_name, data_type, column_key FROM information_schema.columns WHERE table_schema = \"" + dbInfo.databaseName
                + "\"" + "AND (data_type = \"longtext\" OR data_type = \"varchar\");", new ResultSetExtractor<ImmutableList<AnonymousColumn>>() {
            @Override
            public ImmutableList<AnonymousColumn> extractData(ResultSet rs) throws SQLException, DataAccessException {
                ImmutableList.Builder<AnonymousColumn> results = ImmutableList.builder();
                while (rs.next()) {
                    String tableName = rs.getString("table_name");
                    String columnName = rs.getString("column_name");
                    String dataType = rs.getString("data_type");
                    String columnKey = rs.getString("column_key");
                    boolean isUnique = columnKey.equalsIgnoreCase("UNI");

                    // Exclude all columns that are type of type string, but their values can't be changed
                    if ((tableName.equalsIgnoreCase("hourentries") && columnName.equalsIgnoreCase("DTYPE"))
                            || (tableName.equalsIgnoreCase("backlogs") && columnName.equalsIgnoreCase("backlogtype"))
                            || (tableName.equalsIgnoreCase("backlogs_aud") && columnName.equalsIgnoreCase("backlogtype"))
                            || (tableName.equalsIgnoreCase("widgets") && columnName.equalsIgnoreCase("type"))) {
                        continue;
                    }

                    results.add(new AnonymousColumn(tableName, columnName, dataType, isUnique));

                }
                return results.build();
            }
        });
    }

    public ImmutableList<String> getOriginalTables() {
        return this.tables;
    }

}
