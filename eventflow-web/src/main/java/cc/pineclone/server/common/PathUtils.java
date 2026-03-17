package cc.pineclone.server.common;

import java.nio.file.Path;

public class PathUtils {

    private static final String APP_HOME_DIRECTORY_NAME = ".gtav-ops";

    /* 配置目录路径( %appdata%/roaming/.gtav-ops/* (windows) ) */
    public static Path getAppHomePath() {
        String os = System.getProperty("os.name").toLowerCase();

        /* GTAV-ops不支持在除Windows外的平台运行 */
        if (!os.contains("windows"))
            throw new IllegalStateException("GTAV-ops is not allowed to run on non-windows platform");

        String appdata = System.getenv("APPDATA");
        if (appdata != null) return Path.of(appdata, APP_HOME_DIRECTORY_NAME);

        return Path.of(System.getProperty("user.home"), APP_HOME_DIRECTORY_NAME);
    }
}
