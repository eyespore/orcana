package cc.pineclone.server.dao.impl;

import cc.pineclone.server.dao.JsonStoreDAO;
import cc.pineclone.server.dao.MacroConfigDao;
import cc.pineclone.server.dao.MacroEntryDao;
import cc.pineclone.server.domain.entity.MacroConfig;
import cc.pineclone.server.domain.entity.MacroEntry;
import cc.pineclone.server.domain.mapper.MacroConfigMapStruct;
import cc.pineclone.server.domain.mapper.MacroEntryMapStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 基于 Json 文件读写的 MacroConfig 持久层
 */
public class JsonMacroConfigDao extends JsonStoreDAO<MacroConfig> implements MacroConfigDao {

    private static final String ERR_INSERT_WITH_NON_NULL_ID = "Cannot insert macro config with non-null id: ";

    private final MacroConfigMapStruct macroConfigMapStruct;  /* MacroConfig 属性映射创建 */
    private final MacroEntryMapStruct macroEntryMapStruct;  /* MacroEntry 属性映射创建 */

    private final MacroEntryDao macroEntryDAO;  /* 控制级联删除/更新行为 */

    private final Logger log = LoggerFactory.getLogger(getClass());

    public JsonMacroConfigDao(Path dataStorePath,
                              MacroEntryDao macroEntryDAO,
                              MacroEntryMapStruct macroEntryMapStruct,
                              MacroConfigMapStruct macroConfigMapStruct) {
        super(dataStorePath);
        this.macroConfigMapStruct = macroConfigMapStruct;
        this.macroEntryMapStruct = macroEntryMapStruct;

        if (!(macroEntryDAO instanceof JsonMacroEntryDao)) {  /* 仅支持使用 JsonMacroEntryDao，其他实现容易引发业务异常 */
            throw new IllegalArgumentException(
                    "JsonMacroConfigDao only supports JsonMacroEntryDao for cascading delete"
            );
        }

        this.macroEntryDAO = macroEntryDAO;
    }

    @Override
    public List<MacroConfig> selectAll() {
        validateDataStorePath();

        try {
            return objectMapper.readValue(
                    dataStorePath.toFile(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, MacroConfig.class));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read macro configs from " + dataStorePath, e);
        }
    }

    @Override
    public Optional<MacroConfig> selectById(UUID id) {
        if (id == null) return Optional.empty();   /* 传入的ID为空，不执行任何查询，直接返回空Optional */

        List<MacroConfig> matched = selectAll().stream()  /* 此处触发 DataStorePath 检查 */
                .filter(c -> c.getId().equals(id))
                .toList();  /* toList 返回只读列表，不允许外部修改此对象 */

        if (matched.isEmpty()) {  /* 没有查到任何数据，返回空Optional */
            return Optional.empty();
        } else if (matched.size() == 1) {  /* 查询成功 */
            return Optional.of(matched.get(0));
        } else {  /* 数据异常，UUID 主键重复 */
            throw new IllegalStateException("Duplicated macro configNode found with id: {}" + id);
        }
    }

    @Override
    public MacroConfig insert(MacroConfig config) {
        /* 当前配置UUID不为空，拒绝插入，避免造成覆盖 */
        if (config.getId() != null) throw new IllegalStateException(ERR_INSERT_WITH_NON_NULL_ID + config.getId());

        /* 当前配置没有具体的版本号，拒绝插入，通常在Service层注入版本信息 */
        if (config.getVersion() == null) throw new IllegalStateException("Config version is not set, cannot execute insert");

        /* 读取当前表已有的所有配置对象 */
        List<MacroConfig> current = new ArrayList<>(selectAll());  /* 此处触发 DataStorePath 检查 */

        MacroConfig copy = macroConfigMapStruct.copy(config);  /* 拷贝一份新的对象 */
        copy.setId(UUID.randomUUID());  /* 生成 UUID */
        Instant now = Instant.now();  /* 插入时间 */
        copy.setCreatedAt(now);
        copy.setLastModifiedAt(now);
        current.add(copy);  /* 将拷贝得到的新对象加入列表中 */

        writeAtomically(current);  /* 将数据重新写回数据库 */
        return copy;
    }

    @Override
    public List<MacroConfig> insertBatch(List<MacroConfig> configs) {
        /* 若插入列表不存在或为空，直接返回空列表，表示没有任何插入被执行 */
        if (configs == null || configs.isEmpty()) return List.of();

        // 深拷贝 configs，并注入 UUID、时间
        Instant now = Instant.now();
        List<MacroConfig> copies = new ArrayList<>(configs.size());
        for (MacroConfig config : configs) {
            if (config.getId() != null) {
                throw new IllegalStateException(ERR_INSERT_WITH_NON_NULL_ID + config.getId());
            }
            if (config.getVersion() == null) {
                throw new IllegalStateException("Config version is not set, cannot execute insertBatch");
            }

            MacroConfig copy = macroConfigMapStruct.copy(config);
            copy.setId(UUID.randomUUID());
            copy.setCreatedAt(now);
            copy.setLastModifiedAt(now);

            copies.add(copy);
        }

        List<MacroConfig> current = new ArrayList<>(selectAll());  /* 此处触发 DataStorePath 检查 */
        current.addAll(copies); // 合并新插入的配置

        writeAtomically(current);
        return copies;
    }

    @Override
    public boolean update(MacroConfig config) {
        /* 若配置 ID 不存在，那么拒绝修改 */
        if (config.getId() == null) throw new IllegalArgumentException("Cannot update MacroConfig with null id");

        List<MacroConfig> current = new ArrayList<>(selectAll());  /* ，读取现有 configs.json，此处触发 DataStorePath 检查 */
        boolean updated = false;
        List<MacroConfig> newList = new ArrayList<>(current.size());
        UUID oldId = null;  /* 老旧 ID，用于记录更新是否造成 id 变动 */

        for (MacroConfig existing : current) {
            if (existing.getId().equals(config.getId())) {  // 找到目标，拷贝副本并更新时间
                MacroConfig copy = macroConfigMapStruct.copy(config);
                copy.setLastModifiedAt(Instant.now());  /* 将最后修改时间设置到当前 */
                newList.add(copy);

                oldId = existing.getId();  // 保存旧 ID
                updated = true;
            } else {
                newList.add(existing);
            }
        }

        if (!updated) return false; // 没有找到目标 UUID → 更新失败

        writeAtomically(newList);

        // 若 config.id 被修改，则触发级联更新MacroEntry
        if (oldId != null && !oldId.equals(config.getId())) {
            List<MacroEntry> entries = macroEntryDAO.selectByConfigId(oldId);
            if (!entries.isEmpty()) {
                List<MacroEntry> updatedEntries = new ArrayList<>(entries.size());

                for (MacroEntry entry : entries) {
                    MacroEntry copy = macroEntryMapStruct.copy(entry);  /* 拷贝 */
                    copy.setConfigId(config.getId());
                    copy.setLastModifiedAt(Instant.now());
                    updatedEntries.add(copy);
                }
                int result = macroEntryDAO.updateBatch(updatedEntries);/* 批量更新 */
            }
        }

        return true;
    }

    @Override
    public Optional<MacroConfig> deleteById(UUID id) {
        if (id == null) return Optional.empty();  /* ID 为空，不执行任何删除动作 */

        List<MacroConfig> current = new ArrayList<>(selectAll());  /* 读取现有 configs.json，此处触发 DataStorePath 检查 */
        Optional<MacroConfig> deleted = Optional.empty();
        List<MacroConfig> newList = new ArrayList<>(current.size());

        for (MacroConfig config : current) {
            if (config.getId().equals(id)) {
                MacroConfig copy = macroConfigMapStruct.copy(config);
                deleted = Optional.of(copy); // 拷贝返回

                macroEntryDAO.deleteByConfigId(config.getId());  // 级联删除

            } else {
                newList.add(config);
            }
        }

        if (deleted.isEmpty()) return Optional.empty(); // 没有找到 UUID → 删除失败

        writeAtomically(newList);  // 写回 configs.json
        return deleted;
    }

    @Override
    public List<MacroConfig> deleteAll() {
        // 读取现有数据（handleSelectAll 已保证校验与只读语义）
        List<MacroConfig> existing = selectAll();  /* 此处触发 DataStorePath 检查 */
        if (existing.isEmpty()) return List.of(); // 没有任何数据可删除

        // 返回值使用拷贝，避免外部持有内部状态
        List<MacroConfig> deleted = existing.stream()
                .map(macroConfigMapStruct::copy)
                .toList();

        macroEntryDAO.deleteAll();  // 级联删除所有宏实例

        writeAtomically(List.of());  /* 向库写入空列表 */
        return deleted;
    }
}
