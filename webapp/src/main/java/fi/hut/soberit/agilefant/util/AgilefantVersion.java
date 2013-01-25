package fi.hut.soberit.agilefant.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class AgilefantVersion {

    /**
     * WAR-artifact version. If lookup fails, version is set to "x.x.x"
     */
    public final static String VERSION;
    /**
     * Build timestamp. If lookup fails, timestamp is set to current time
     */
    public final static Instant BUILD_TIMESTAMP;

    /**
     * Git information. If lookup fails, all fields will be either "?" or "null" (the string "null", not a null
     * pointer).
     */
    public final static GitInformation GIT_INFORMATION;

    static {
        ClassLoader cl = AgilefantVersion.class.getClassLoader();

        String version = "x.x.x";
        Instant buildTimestamp = new Instant();
        try {
            URL manifest = cl.getResource("META-INF/MANIFEST.MF");
            if (manifest != null) {
                DateTimeFormatter format = DateTimeFormat.forPattern("yyyyMMdd-HHmm");
                InputStream input = null;
                try {
                    input = manifest.openStream();

                    String versionPrefix = "Implementation-Version: ";
                    String timestampPrefix = "Agilefant-Build: ";

                    for (String line : IOUtils.readLines(input, "UTF-8")) {
                        if (line.startsWith(versionPrefix)) {
                            version = line.substring(versionPrefix.length());
                        } else if (line.startsWith(timestampPrefix)) {
                            String value = line.substring(timestampPrefix.length());
                            try {
                                buildTimestamp = format.parseDateTime(value).toInstant();
                            } catch (IllegalArgumentException e) {
                            }
                        }
                    }
                } finally {
                    IOUtils.closeQuietly(input);
                }

            }
        } catch (Exception e) {
        }
        VERSION = version;
        BUILD_TIMESTAMP = buildTimestamp;

        GitInformation gitInformation = GitInformation.EMPTY;
        try {
            URL gitProperties = cl.getResource("git.properties");
            if (gitProperties != null) {
                Properties properties = new Properties();
                InputStream input = null;
                try {
                    input = gitProperties.openStream();
                    properties.load(input);

                    gitInformation = new GitInformation(properties);
                } finally {
                    IOUtils.closeQuietly(input);
                }
            }
        } catch (Exception e) {
        }
        GIT_INFORMATION = gitInformation;
    }

    public static class GitInformation {
        public final String branch;
        public final String describe;
        public final String commitId;
        public final String buildUserName;
        public final String buildUserEmail;
        public final String buildTime;
        public final String commitUserName;
        public final String commitUserEmail;
        public final String commitMessageShort;
        public final String commitMessageFull;
        public final String commitTime;

        public static final GitInformation EMPTY = new GitInformation("?", "?", "?", "?", "?", "?", "?", "?", "?", "?", "?");

        public GitInformation(String branch, String describe, String commitId, String buildUserName, String buildUserEmail, String buildTime,
                String commitUserName, String commitUserEmail, String commitMessageShort, String commitMessageFull, String commitTime) {
            this.branch = branch;
            this.describe = describe;
            this.commitId = commitId;
            this.buildUserName = buildUserName;
            this.buildUserEmail = buildUserEmail;
            this.buildTime = buildTime;
            this.commitUserName = commitUserName;
            this.commitUserEmail = commitUserEmail;
            this.commitMessageShort = commitMessageShort;
            this.commitMessageFull = commitMessageFull;
            this.commitTime = commitTime;
        }

        public GitInformation(Properties properties) {
            this(String.valueOf(properties.get("git.branch")), String.valueOf(properties.get("git.commit.id.describe")), String.valueOf(properties
                    .get("git.commit.id")), String.valueOf(properties.get("git.build.user.name")), String.valueOf(properties.get("git.build.user.email")),
                    String.valueOf(properties.get("git.build.time")), String.valueOf(properties.get("git.commit.user.name")), String.valueOf(properties
                            .get("git.commit.user.email")), String.valueOf(properties.get("git.commit.message.short")), String.valueOf(properties
                            .get("git.commit.message.full")), String.valueOf(properties.get("git.commit.time")));
        }
    }

    private AgilefantVersion() {
    }

}