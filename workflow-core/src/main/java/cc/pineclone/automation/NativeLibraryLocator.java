package cc.pineclone.automation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class NativeLibraryLocator {

    private final Logger log = LoggerFactory.getLogger(getClass());

    protected abstract String getLibName();

    public final Iterator<File> getLibraries() {
        List<File> libraries = new ArrayList<>(1);

        /* 库名，默认库名使用 Automation */
//        String libName = System.getProperty("automation.lib.name", "Automation");
        String libName = getLibName();

//        String basePackage = AutomationContext.class.getPackage().getName().replace('.', '/');

        /* 系统架构 */
        String libNativeArch = NativeSystem.getArchitecture().toString().toLowerCase();

        /* 建立系统库名映射 windows -> .dll linux -> .so macOS -> .dylib */
        String libNativeName = System.mapLibraryName(libName).replaceAll("\\.jnilib$", "\\.dylib");
//        String libResourcePath = "/" + basePackage + "/lib/" + NativeSystem.getFamily().toString().toLowerCase() + '/' + libNativeArch + '/' + libNativeName;
        String libResourcePath = "/native/" + NativeSystem.getFamily().toString().toLowerCase() + '/' + libNativeArch + '/' + libNativeName;
        URL classLocation = AutomationContext.class.getProtectionDomain().getCodeSource().getLocation();
        File classFile = null;

        try {
            classFile = new File(classLocation.toURI());
        } catch (URISyntaxException e) {
            log.warn(e.getMessage());
            classFile = new File(classLocation.getPath());
        }

        File libFile = null;
        if (classFile.isFile()) {
            String libPath = System.getProperty("automation.lib.path", classFile.getParentFile().getPath());
            InputStream resourceInputStream = AutomationContext.class.getResourceAsStream(libResourcePath);
            if (resourceInputStream == null) {
                throw new RuntimeException("Unable to extract the native library " + libResourcePath + "!\n");
            }

            String version = AutomationContext.class.getPackage().getImplementationVersion();
            if (version != null) {
                version = '-' + version;
            } else {
                version = "";
            }

            libFile = new File(libPath, libNativeName.replaceAll("^(.*)\\.(.*)$", "$1" + version + '.' + libNativeArch + ".$2"));
            if (!libFile.exists()) {
                try {
                    FileOutputStream libOutputStream = new FileOutputStream(libFile);
                    byte[] buffer = new byte[4096];

                    int size;
                    while((size = resourceInputStream.read(buffer)) != -1) {
                        libOutputStream.write(buffer, 0, size);
                    }

                    resourceInputStream.close();
                    libOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }

                log.info("Extracted library: {}", libFile.getPath());  // TODO: 观测文件拷贝到外部的具体路径
            }
        } else {
            libFile = Paths.get(classFile.getAbsolutePath(), libResourcePath.toString()).toFile();
        }

        if (!libFile.exists()) {
            throw new RuntimeException("Unable to locate JNI library at " + libFile.getPath() + "!\n");
        } else {
            log.info("Loading library: {}", libFile.getPath());
            libraries.add(libFile);
            return libraries.iterator();
        }
    }
}
