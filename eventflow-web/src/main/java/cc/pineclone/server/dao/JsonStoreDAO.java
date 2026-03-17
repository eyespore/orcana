package cc.pineclone.server.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * 基于 JSON 实现的简单数据库存储
 * @param <T>
 */
public abstract class JsonStoreDAO<T> implements InitializingBean {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 数据存储路径，用于存储基于 Json 作为介质的 MacroConfig 表数据
     */
    protected final Path dataStorePath;  /* 数据存储路径 */
    protected final ObjectMapper objectMapper;

    public JsonStoreDAO(Path dataStorePath) {
        this.dataStorePath = dataStorePath;
        objectMapper = new ObjectMapper();
//        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);  /* 禁用美观输出，提高读写性能 */
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);  /* 即使读取到未知属性也不会报错 */

        objectMapper.registerModule(new JavaTimeModule());  /* 日期序列化模块 */
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  /* 日期美观输出 */
    }

    /**
     * 初始化数据库存储路径
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("Initializing JSON datastore at path: {}", dataStorePath);
        if (Files.notExists(dataStorePath.getParent())) Files.createDirectories(dataStorePath.getParent());
        if (Files.notExists(dataStorePath)) {
            objectMapper.writeValue(dataStorePath.toFile(), List.of());  // 初始化为空表，而不是空文件
        }
    }

    /**
     * 子类可以手动调用该方法校验 Json 文件数据库是否有效后再执行具体业务逻辑
     */
    protected void validateDataStorePath() {
        if (!Files.exists(dataStorePath)) {
            try {
                Files.createDirectories(dataStorePath.getParent());
                Files.writeString(dataStorePath, "[]"); // 初始化空 JSON 数组
            } catch (IOException e) {
                throw new RuntimeException("Failed to initialize JSON datastore: " + dataStorePath, e);
            }
        }
    }

    protected final void writeAtomically(List<T> list) {
        Path tempPath = dataStorePath.resolveSibling(dataStorePath.getFileName() + ".tmp");
        try {
            objectMapper.writeValue(tempPath.toFile(), list);
            Files.move(tempPath, dataStorePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON datastore: " + dataStorePath, e);
        } finally {
            try { Files.deleteIfExists(tempPath); } catch (IOException ee) {
                log.error("Failed to delete temp file: {}", tempPath, ee);
            }
        }
    }
}
