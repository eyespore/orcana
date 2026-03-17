package cc.pineclone.server.dao;

import cc.pineclone.server.domain.entity.MacroEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 宏实例持久层
 */
public interface MacroEntryDao {

    /**
     * 查询当前所有的宏实例，返回一个不可变列表
     * @return 宏实例列表，若没有任何宏实例，那么返回空列表
     */
    List<MacroEntry> selectAll();

    /**
     * 基于宏 ID 查询具体的宏实例
     * @param id 宏实例的唯一标识 ID
     * @return 若宏实例存在，那么返回一个包含该宏实例的 Optional 容器，如果不存在，那么返回一个空的 Optional 容器
     */
    Optional<MacroEntry> selectById(UUID id);

    /**
     * 基于指定宏配置 ID，查询属于该配置的所有宏实例配置
     * @param configId 宏配置文件唯一标识 ID
     * @return 返回属于该配置的所有宏实例对象，若配置没有任何宏实例，那么返回一个空列表
     */
    List<MacroEntry> selectByConfigId(UUID configId);

    /**
     * 向 MacroEntry 表插入对象
     * @param macroEntry 待插入的宏配置实例
     * @return 若插入成功，则会返回一个插入对象的拷贝，该拷贝会携带插入时装配得到的 UUID、创建日期、最后修改日期
     * 等字段值，外部在插入之后可以通过该返回值继续执行其他业务逻辑
     */
    MacroEntry insert(MacroEntry macroEntry);

    /**
     * 批量插入 MacroEntry 实例对象
     * @param macroEntries MacroEntry 实例列表
     * @return 若插入成功，则会将插入后的 MacroEntry 实例拷贝一个副本并加入一个不可变列表当返回
     */
    List<MacroEntry> insertBatch(List<MacroEntry> macroEntries);

    /**
     * 更新已有的宏配置实例
     * @param macroEntry 已经写入更新内容的宏实例对象
     * @return 若更新成功返回 true，若更新失败则会直接返回 false
     */
    boolean update(MacroEntry macroEntry);

    /**
     * 批量更新
     * @param macroEntries 已经更新内容的宏实例列表
     * @return 更新成功的条目，如果没有记录被更新，那么返回0
     */
    int updateBatch(List<MacroEntry> macroEntries);

    /**
     * 基于唯一标识 ID 删除指定的宏实例
     * @param id 宏配置示例的唯一标识 ID
     * @return 若删除成功，则会返回一个装配了被删除的宏配置示例拷贝的 Optional 容器，若删除失败，
     * 则返回一个空的 Optional 容器
     */
    Optional<MacroEntry> deleteById(UUID id);

    /**
     * 删除指定宏配置下关联的所有配置实例
     * @param configId 宏配置文件的唯一标识 ID
     * @return 若实例被成功删除，则会被加入不可变列表中返回，若删除失败，则不会加入列表
     */
    List<MacroEntry> deleteByConfigId(UUID configId);

    /**
     * 删除所有的宏配置实例
     * @return 若实例被成功删除，则会被加入不可变列表中返回，若删除失败，则不会加入列表
     */
    List<MacroEntry> deleteAll();

}
