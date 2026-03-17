package cc.pineclone.server.dao;

import cc.pineclone.server.config.AppConfig;
import cc.pineclone.server.dao.impl.JsonMacroConfigDao;
import cc.pineclone.server.dao.impl.JsonMacroEntryDao;
import cc.pineclone.server.domain.entity.MacroConfig;
import cc.pineclone.server.domain.mapper.MacroConfigMapStruct;
import cc.pineclone.server.domain.mapper.MacroEntryMapStruct;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class JsonMacroConfigDaoTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private AppConfig appConfig;

    @Autowired
    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    private final MacroEntryMapStruct macroEntryMapStruct = Mappers.getMapper(MacroEntryMapStruct.class);
    private final MacroConfigMapStruct macroConfigMapStruct = Mappers.getMapper(MacroConfigMapStruct.class);

    private final MacroEntryDao macroEntryDao = new JsonMacroEntryDao(
            appConfig.getJsonMacroDataStoreSettings().getMacroEntryPath(),
            macroEntryMapStruct);

    private final MacroConfigDao macroConfigDao = new JsonMacroConfigDao(
            appConfig.getJsonMacroDataStoreSettings().getMacroConfigPath(),  /* configs.json 路径 */
            macroEntryDao, macroEntryMapStruct, macroConfigMapStruct
    );

    @Test
    public void selectAll() {
        List<MacroConfig> list = macroConfigDao.selectAll();
        list.forEach(c -> log.debug("{}", c));
    }

    /**
     * 测试向配置插值，Dao操作最原始的JsonNode，不涉及任何JsonNode到DTO的反序列化
     * @throws JsonProcessingException 若 mapper 无法创建 JsonNode 则会抛出该异常
     */
    @Test
    public void insert() throws JsonProcessingException {
        MacroConfig macroConfig = new MacroConfig();
        macroConfig.setVersion("v0.1.0-alpha-6");
        macroConfig.setName("Test Macro Config");
        MacroConfig result = macroConfigDao.insert(macroConfig);
        Assertions.assertNotEquals(null, result);
    }

    @Test
    public void selectById() {
        String uuidStr = "732acc67-54df-4569-995e-d2e67ee4752b";
        Optional<MacroConfig> optional = macroConfigDao.selectById(UUID.fromString(uuidStr));
        optional.ifPresent(c -> log.debug("Macro config exists: {}", c));
    }

    @Test
    public void insertBatch() {
        MacroConfig macroConfig1 = new MacroConfig();
        macroConfig1.setVersion("v0.1.0-alpha-6");
        macroConfig1.setName("Insert Batch Test Macro Config");

        MacroConfig macroConfig2 = macroConfigMapStruct.copy(macroConfig1);
        List<MacroConfig> result = macroConfigDao.insertBatch(List.of(
                macroConfig1,
                macroConfig2,
                macroConfig2
                ));

        Assertions.assertNotEquals(macroConfig1, macroConfig2);
        Assertions.assertEquals(3, result.size());
    }

    @Test
    public void update() {
        String uuidStr = "56b81e2a-3af5-487d-9030-4068fdd360a5";  /* 待更新 MacroConfig UUID */
        Optional<MacroConfig> optional = macroConfigDao.selectById(UUID.fromString(uuidStr));  /* 查询获取 Entity */

        optional.ifPresent(c -> {
            c.setName("Tattoo");  /* 重命名配置 */
            Instant original = c.getLastModifiedAt();
            boolean result = macroConfigDao.update(c);/* 更新配置 */
            Assertions.assertTrue(result);  /* 结果应当更新成功 */

            /* 重新查询Entity，检查lastModifiedAt字段是否被更新 */
            Optional<MacroConfig> optional2 = macroConfigDao.selectById(UUID.fromString(uuidStr));
            optional2.ifPresent(c2 -> {
                Assertions.assertNotEquals(original, c2.getLastModifiedAt());
                log.debug("MacroConfig's lastModifiedAt field is updated from {} to {}", original, c2.getLastModifiedAt());
            });
        });
    }

    @Test
    public void deleteById() {
        String uuidStr = "732acc67-54df-4569-995e-d2e67ee4752b";  /* 待更新 MacroConfig UUID */
        Optional<MacroConfig> optional = macroConfigDao.deleteById(UUID.fromString(uuidStr));  /* 基于 UUID 删除指定 Entity */
        optional.ifPresent(c -> {
            log.debug("Successfully deleted {}", c);  /* 删除成功 */
        });
    }

    @Test
    public void deleteAll() {
        List<MacroConfig> result = macroConfigDao.deleteAll();
        for (MacroConfig config : result) {
            log.debug("Successfully deleted: {}", config);
        }
    }
}
