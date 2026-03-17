package cc.pineclone.server.dao.impl;

import cc.pineclone.server.dao.JsonStoreDAO;
import cc.pineclone.server.dao.MacroEntryDao;
import cc.pineclone.server.domain.entity.MacroEntry;
import cc.pineclone.server.domain.mapper.MacroEntryMapStruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


public class JsonMacroEntryDao extends JsonStoreDAO<MacroEntry> implements MacroEntryDao {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MacroEntryMapStruct macroEntryMapStruct;

    public JsonMacroEntryDao(Path dataStorePath,
                             MacroEntryMapStruct macroEntryMapStruct) {
        super(dataStorePath);
        this.macroEntryMapStruct = macroEntryMapStruct;
    }

    @Override
    public List<MacroEntry> selectAll() {
        validateDataStorePath();
        try {
            return objectMapper.readValue(
                    dataStorePath.toFile(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, MacroEntry.class));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read macro entries from " + dataStorePath, e);
        }
    }

    @Override
    public Optional<MacroEntry> selectById(UUID id) {
        if (id == null) return Optional.empty();

        // 先获取全部，再按 UUID 匹配
        List<MacroEntry> allEntries = selectAll();  /* 此处触发 DataStorePath 检查 */
        List<MacroEntry> matched = allEntries.stream()
                .filter(e -> id.equals(e.getId()))
                .toList(); // toList 返回不可变列表

        if (matched.isEmpty()) {
            return Optional.empty();
        } else if (matched.size() == 1) {
            return Optional.of(matched.get(0));
        } else {
            // 数据异常，UUID 主键重复
            throw new IllegalStateException("Duplicated MacroEntry found with id: " + id);
        }
    }

    @Override
    public List<MacroEntry> selectByConfigId(UUID configId) {
        if (configId == null) {
            return List.of();  // 传入 null，直接返回空列表
        }

        // 获取所有 MacroEntry
        List<MacroEntry> allEntries = selectAll();  /* 此处触发 DataStorePath 检查 */
        List<MacroEntry> matched = allEntries.stream()
                .filter(e -> configId.equals(e.getConfigId()))
                .toList();  // toList 返回不可变列表

        return List.copyOf(matched);  // 保证外部不可修改
    }

    @Override
    public MacroEntry insert(MacroEntry macroEntry) {
        if (macroEntry == null) {
            throw new IllegalArgumentException("Cannot insert null MacroEntry");
        }

        if (macroEntry.getConfigId() == null) {  /* 宏配置 ID 为空，直接跳过 */
            throw new IllegalStateException("MacroEntry must have a valid configId before insertion");
        }

        // 读取现有 entries
        List<MacroEntry> current = selectAll();  /* 此处触发 DataStorePath 检查 */

        if (macroEntry.getId() != null && current.stream().anyMatch(e -> e.getId().equals(macroEntry.getId()))) {
            throw new IllegalStateException("MacroEntry with id " + macroEntry.getId() + " already exists");
        }

        // 创建副本并初始化 ID、时间戳
        MacroEntry copy = macroEntryMapStruct.copy(macroEntry);
        if (copy.getId() == null) {
            copy.setId(UUID.randomUUID());
        }
        Instant now = Instant.now();
        copy.setCreatedAt(now);
        copy.setLastModifiedAt(now);

        // 合并到列表
        List<MacroEntry> updated = new ArrayList<>(current);
        updated.add(copy);

        // 写入 entries.json 临时文件，保证原子性
        writeAtomically(updated);
        return copy;
    }

    @Override
    public List<MacroEntry> insertBatch(List<MacroEntry> macroEntries) {
        if (macroEntries == null || macroEntries.isEmpty()) return List.of();

        // 读取现有 entries
        List<MacroEntry> current = new ArrayList<>(selectAll());  /* 此处触发 DataStorePath 检查 */
        List<MacroEntry> copies = new ArrayList<>(macroEntries.size());
        Instant now = Instant.now();

        for (MacroEntry entry : macroEntries) {
            if (entry == null) continue;
            if (entry.getConfigId() == null) continue;  /* 宏配置 ID 为空，直接跳过 */

            MacroEntry copy = macroEntryMapStruct.copy(entry);
            if (copy.getId() == null) {
                copy.setId(UUID.randomUUID());
            }
            copy.setCreatedAt(now);
            copy.setLastModifiedAt(now);

            // 避免重复 UUID
            if (current.stream().anyMatch(e -> e.getId().equals(copy.getId()))) {
                throw new IllegalStateException("Duplicated MacroEntry id: " + copy.getId());
            }

            current.add(copy);
            copies.add(copy);
        }

        // 写入 entries.json
        writeAtomically(current);
        return List.copyOf(copies);  // 返回不可变副本列表
    }

    @Override
    public boolean update(MacroEntry macroEntry) {
        if (macroEntry.getId() == null) {
            throw new IllegalArgumentException("Cannot update MacroEntry with null id");
        }

        List<MacroEntry> current = selectAll();  // 读取当前列表

        // 找到目标条目的索引
        int index = -1;
        for (int i = 0; i < current.size(); i++) {
            if (macroEntry.getId().equals(current.get(i).getId())) {
                index = i;
                break;
            }
        }

        if (index == -1) return false;  // 没找到对应的ID → 更新失败

        MacroEntry copy = macroEntryMapStruct.copy(macroEntry);  // 拷贝对象，避免修改外部传入对象
        copy.setLastModifiedAt(Instant.now());

        current.set(index, copy);  // 替换列表中的对象
        writeAtomically(current);  // 原子写回

        return true;
    }

    @Override
    public int updateBatch(List<MacroEntry> macroEntries) {
        if (macroEntries == null || macroEntries.isEmpty()) return 0;

        List<MacroEntry> current = new ArrayList<>(selectAll());  // 读取现有 MacroEntry 数据
        if (current.isEmpty()) return 0;

        // 将待更新列表按 id 建立映射，方便快速查找
        Map<UUID, MacroEntry> updateMap = macroEntries.stream()
                .filter(e -> e.getId() != null)
                .collect(Collectors.toMap(MacroEntry::getId, e -> e));

        int updatedCount = 0;
        List<MacroEntry> newList = new ArrayList<>(current.size());

        for (MacroEntry existing : current) {
            MacroEntry toUpdate = updateMap.get(existing.getId());
            if (toUpdate != null) {
                MacroEntry copy = macroEntryMapStruct.copy(toUpdate);
                copy.setLastModifiedAt(Instant.now());  // 更新时间
                newList.add(copy);
                updatedCount++;
            } else {
                newList.add(existing);
            }
        }

        if (updatedCount > 0) writeAtomically(newList);  // 写回 JSON 文件
        return updatedCount;
    }

    @Override
    public Optional<MacroEntry> deleteById(UUID id) {
        if (id == null) return Optional.empty();

        List<MacroEntry> current = selectAll();
        Optional<MacroEntry> toDelete = current.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();  // 找到要删除的条目

        if (toDelete.isEmpty()) return Optional.empty(); // 没找到
        current.remove(toDelete.get());  // 移除目标条目
        writeAtomically(current);  // 原子写回
        return toDelete;
    }

    @Override
    public List<MacroEntry> deleteByConfigId(UUID configId) {
        if (configId == null) return List.of();
        List<MacroEntry> current = selectAll();

        List<MacroEntry> toDelete = current.stream()
                .filter(e -> configId.equals(e.getConfigId()))
                .toList();  // 找出要删除的条目

        if (toDelete.isEmpty()) return List.of();

        current.removeAll(toDelete);  // 移除这些条目
        writeAtomically(current);  // 原子写回

        return toDelete;
    }

    @Override
    public List<MacroEntry> deleteAll() {
        List<MacroEntry> current = selectAll();  // 读取当前所有条目
        if (current.isEmpty()) return List.of();  // 没有任何条目可删除
        writeAtomically(List.of());  // 清空列表
        return current;  // 返回删除的条目列表
    }
}
