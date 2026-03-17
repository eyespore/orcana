package cc.pineclone.server.dao;

import cc.pineclone.server.config.AppConfig;
import cc.pineclone.server.dao.impl.JsonMacroEntryDao;
import cc.pineclone.server.domain.entity.MacroEntry;
import cc.pineclone.server.domain.mapper.MacroEntryMapStruct;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class JsonMacroEntryDaoTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AppConfig appConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MacroEntryMapStruct macroEntryMapStruct = Mappers.getMapper(MacroEntryMapStruct.class);

    private final MacroEntryDao macroEntryDao = new JsonMacroEntryDao(
            appConfig.getJsonMacroDataStoreSettings().getMacroEntryPath(),
            macroEntryMapStruct);

    @Test
    void selectAll() {
        List<MacroEntry> result = macroEntryDao.selectAll();
        result.forEach(e -> log.debug("{}", e));
    }

    @Test
    void selectById() {
        String idStr = "8465d0b8-fc73-481a-a4fe-0cefe2e7a964";
        Optional<MacroEntry> result = macroEntryDao.selectById(UUID.fromString(idStr));
        result.ifPresent(e -> log.debug("{}", e));
    }

    @Test
    void selectByConfigId() {
        String configIdStr = "56b81e2a-3af5-487d-9030-4068fdd360a5";
        List<MacroEntry> result = macroEntryDao.selectByConfigId(UUID.fromString(configIdStr));
        result.forEach(e -> log.debug("{}", e));
    }

    @Test
    void insert() throws JsonProcessingException {
        String configIdStr = "56b81e2a-3af5-487d-9030-4068fdd360a5";

        MacroEntry entry = new MacroEntry();
        entry.setConfigId(UUID.fromString(configIdStr));
        entry.setType("MELEE_GLITCH");
        entry.setEnabled(false);
        entry.setConfigNode(objectMapper.readTree("""
                {
                    "delay": 150,
                    "key": "W",
                    "count": 3
                }
                """));

        MacroEntry result = macroEntryDao.insert(entry);
        log.debug("Insert result: {}", result);
    }

    @Test
    void insertBatch() throws JsonProcessingException {
        String configIdStr = "91127183-6a30-439a-9e6c-0f588550471f";
        UUID configId = UUID.fromString(configIdStr);

        List<MacroEntry> batch = List.of(
                new MacroEntry(
                        null,
                        configId,
                        "AUTO_FIRE",
                        true,
                        null,
                        null,
                        objectMapper.readTree("""
                                { "delay": 180 }
                                """)),
                new MacroEntry(
                        null,
                        configId,
                        "START_ENGINE",
                        true,
                        null,
                        null,
                        objectMapper.readTree("""
                                { "delay": 180 }
                                """)),
                new MacroEntry(
                        null,
                        configId,
                        "SWAP_GLITCH",
                        true,
                        null,
                        null,
                        objectMapper.readTree("""
                                { "delay": 180 }
                                """))
        );

        List<MacroEntry> result = macroEntryDao.insertBatch(batch);
        result.forEach(e -> log.debug("{}", e));
    }

    @Test
    void update() {
        String idStr = "8465d0b8-fc73-481a-a4fe-0cefe2e7a964";
        Optional<MacroEntry> entry = macroEntryDao.selectById(UUID.fromString(idStr));
        entry.ifPresent(e -> {
            log.debug("Before macro entry update: {}", e);
            e.setType("CUSTOMIZE");

            boolean result = macroEntryDao.update(e);
            Assertions.assertTrue(result);

            Optional<MacroEntry> updated = macroEntryDao.selectById(UUID.fromString(idStr));
            updated.ifPresent(ee -> log.debug("After macro entry updated: {}", ee));
        });
    }

    @Test
    void updateBatch() {
        /* skip */
    }

    @Test
    void deleteById() {
        UUID id = UUID.fromString("8465d0b8-fc73-481a-a4fe-0cefe2e7a964");
        Optional<MacroEntry> result = macroEntryDao.deleteById(id);

        result.ifPresent(e -> log.debug("Delete successfully: {}", e));

        List<MacroEntry> list = macroEntryDao.selectAll();
        list.forEach(e -> log.debug("Current list: {}", e));
    }

    @Test
    void deleteByConfigId() {
        UUID configId = UUID.fromString("56b81e2a-3af5-487d-9030-4068fdd360a5");
        List<MacroEntry> result = macroEntryDao.deleteByConfigId(configId);
        result.forEach(e -> log.debug("Successfully delete: {}", e));
    }

    @Test
    void deleteAll() {

    }
}