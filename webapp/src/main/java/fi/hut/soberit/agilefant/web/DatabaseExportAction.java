package fi.hut.soberit.agilefant.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.db.export.Atablesmodifier;
import fi.hut.soberit.agilefant.db.export.DbBackupper;
import fi.hut.soberit.agilefant.util.DbConnectionInfo;

@Component("dbExportAction")
@Scope("prototype")
public class DatabaseExportAction extends ActionSupport {

    private static final long serialVersionUID = -1639488740106383276L;

    private DbBackupper takeDbBackup;
    private ByteArrayOutputStream databaseStream;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DbConnectionInfo connectionInfo;

    public String edit() {
        this.takeDbBackup = new DbBackupper();

        return Action.SUCCESS;
    }

    public String generateDatabaseExport() {
        try {
            this.takeDbBackup = new DbBackupper();
            this.databaseStream = takeDbBackup.generateDBDumpStream(dataSource, connectionInfo);
            return Action.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return Action.ERROR;
        }
    }

    public String generateAnonymousDatabaseExport() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        try {
            this.takeDbBackup = new DbBackupper();
            Atablesmodifier anonymousTableModifier = new Atablesmodifier(dataSource, connectionInfo);
            anonymousTableModifier.dublicaTables();
            anonymousTableModifier.anonymizeTables();

            this.databaseStream = takeDbBackup.generateAnonymousDBDumpStream(dataSource, connectionInfo, anonymousTableModifier.getOriginalTables());
            anonymousTableModifier.deletetables();
            return Action.SUCCESS;
        } catch (Throwable e) {
            e.printStackTrace();
            return Action.ERROR;
        }
    }

    public InputStream getDatabaseStream() {
        return new ByteArrayInputStream(this.databaseStream.toByteArray());
    }

    public void setDatabaseStream(ByteArrayOutputStream databaseStream) {
        this.databaseStream = databaseStream;
    }

}
