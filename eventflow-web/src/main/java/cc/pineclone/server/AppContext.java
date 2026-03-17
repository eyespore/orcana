package cc.pineclone.server;

import cc.pineclone.server.config.AppConfig;
import cc.pineclone.server.dao.MacroConfigDao;
import cc.pineclone.server.dao.MacroEntryDao;
import cc.pineclone.server.dao.impl.JsonMacroConfigDao;
import cc.pineclone.server.dao.impl.JsonMacroEntryDao;
import cc.pineclone.server.domain.mapper.MacroConfigMapStruct;
import cc.pineclone.server.domain.mapper.MacroEntryMapStruct;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.file.Files;

@Slf4j
@SpringBootApplication
public class AppContext {


//    private final MacroFactory macroFactory;  /* 宏工厂 */
//    private final MacroRegistry macroRegistry;  /* 宏注册表 */

//    private final PlatformFocusMonitor platformFocusMonitor;  /* 平台焦点监听器 */

//    private final MacroEntryMapStruct macroEntryMapStruct;  /* 面向 MacroEntry 的映射器 */
//    private final MacroEntryDAO macroEntryDao;  /* 宏实例持久层 */

//    private final MacroConfigMapStruct macroConfigMapStruct;  /* 面向 MacroConfig 的映射器 */
//    private final MacroConfigDAO macroConfigDao;  /* 宏配置持久层 */

    public AppContext() {

        /* 工具实例化 */
//        this.macroFactory = new MacroFactory();
//        this.macroTaskScheduler = new MacroTaskScheduler();
//        this.platformFocusMonitor = new PlatformFocusMonitor();
//        this.macroRegistry = new MacroRegistry(platformFocusMonitor);

        /* MapStruct 实例化 */
//        this.macroEntryMapStruct = ;
//        this.macroConfigMapStruct = ;

        /* 持久层初始化 */
//        this.macroEntryDao = ;

//        this.macroConfigDao = ;

        /* 底层资源生命周期注册 */
//        registerLifecycleObj(macroEntryDao);
//        registerLifecycleObj(macroConfigDao);

        /* 上层工具生命周期注册 */
//        registerLifecycleObj(macroRegistry);
//        registerLifecycleObj(macroTaskScheduler);
//        registerLifecycleObj(platformFocusMonitor);

    }

    /* MacroEntryMapStruct 映射器 */
    @Bean
    public MacroEntryMapStruct macroEntryMapStruct() {
        return Mappers.getMapper(MacroEntryMapStruct.class);
    }

    /* MacroConfigMapStruct 映射器 */
    @Bean
    public MacroConfigMapStruct macroConfigMapStruct() {
        return Mappers.getMapper(MacroConfigMapStruct.class);
    }

    /* MacroEntryDao 实例 */
    @Bean
    public MacroEntryDao macroEntryDAO(AppConfig appConfig,
                                       MacroEntryMapStruct macroEntryMapStruct) {
        return new JsonMacroEntryDao(appConfig.getJsonMacroDataStoreSettings().getMacroEntryPath(),
                macroEntryMapStruct);
    }

    /* MacroConfigDao 实例 */
    @Bean
    public MacroConfigDao macroConfigDAO(AppConfig appConfig,
                                         MacroEntryDao macroEntryDao,
                                         MacroEntryMapStruct macroEntryMapStruct,
                                         MacroConfigMapStruct macroConfigMapStruct) {
        return new JsonMacroConfigDao(
                appConfig.getJsonMacroDataStoreSettings().getMacroConfigPath(),
                macroEntryDao,
                macroEntryMapStruct,
                macroConfigMapStruct
        );
    }

    @Autowired
    private AppConfig appConfig;

    @PostConstruct
    public void init() throws Exception {
        log.info("Initializing macro core app home directory");  /* 初始化宏后端应用家目录 */
        Files.createDirectories(appConfig.getCoreHomePath());

        /* 注册 jnativehook 全局钩子在应用启动阶段调用，注册jnativehook全局监听钩子，从而确保后续所有InputSource监听器能够正常工作，*/
//        log.info("Register jnativehook global native hook for macro core");
//        JNativeHookManager.register(AppContext.class);
    }

    @PreDestroy
    public void stop() throws Exception {
//        JNativeHookManager.unregister(AppContext.class); /* 注销 jnativehook 全局钩子 */
    }

    public static void main(String[] args) {
//        log.debug("Working directory: {}", System.getProperty("user.dir"));
        SpringApplication.run(AppContext.class, args);
    }
}
