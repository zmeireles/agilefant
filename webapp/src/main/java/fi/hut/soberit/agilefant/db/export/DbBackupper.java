package fi.hut.soberit.agilefant.db.export;

import java.io.ByteArrayOutputStream;

import javax.sql.DataSource;

import fi.hut.soberit.agilefant.util.DbConnectionInfo;

/**
 * Provides a zipped database dump in an output stream
 */
public class DbBackupper {

    private ByteArrayOutputStream dbOutputStream;

    /**
     * 
     */
    public DbBackupper() {
        dbOutputStream = new ByteArrayOutputStream();
    }

    /**
     * Calls Dbbackup to generate zipped stream Returns ByteArrayOutputStream that is zipped DBdump from mysqldump or
     * null if generating stream failed for some reason.
     */
    public ByteArrayOutputStream generateDBDumpStream(DataSource dataSource, DbConnectionInfo connectionInfo) {
        DbBackupStreamGenerator dbbackup = new DbBackupStreamGenerator();
        int exitvalue = dbbackup.generateZippedDbOutputStream(dataSource, connectionInfo);

        if (exitvalue != 0) {
            System.out.println(dbbackup.getErrorMessages());
            // exit value -5 is result of failure in try catch. which shouldn't happen. values +0
            // are result for mysqldump failure debugging getErrorMessages() will give more info
            return null;
        }

        this.dbOutputStream = dbbackup.getZippedDbOutputStream();
        return dbbackup.getZippedDbOutputStream();
    }

    /**
     * Anonymous dumping Calls Dbbackup to generate zipped stream Returns ByteArrayOutputStream that is zipped DBdump
     * from mysqldump or null if generating stream failed for some reason.
     */
    public ByteArrayOutputStream generateAnonymousDBDumpStream(DataSource dataSource, DbConnectionInfo connectionInfo, Iterable<String> excludedTables) {
        DbBackupStreamGenerator dbbackup = new DbBackupStreamGenerator();

        int exitvalue = dbbackup.generateZippedAnonymousDbOutputStream(dataSource, connectionInfo, excludedTables);

        if (exitvalue != 0) {
            System.out.println(dbbackup.getErrorMessages());
            // exit value -5 is result of failure in try catch. which shouldn't happen. values +0
            // are result for mysqldump failure debugging getErrorMessages() will give more info
            return null;
        }

        this.dbOutputStream = dbbackup.getZippedDbOutputStream();
        return dbbackup.getZippedDbOutputStream();
    }

    /**
     * Returns and output stream that contains the zipped dbdump
     */
    public ByteArrayOutputStream getDbOutputStream() {
        return this.dbOutputStream;
    }

}
