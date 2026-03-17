package cc.pineclone.server.dao;

import cc.pineclone.server.domain.entity.MacroConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 宏配置持久层，与宏实例构成一对多的关系，一份宏配置会映射到多个宏实例
 */
public interface MacroConfigDao {

    /**
     * 查询当前可用的所有宏配置
     * @return 可用的宏配置列表
     */
    List<MacroConfig> selectAll();

    /**
     * 基于 UUID 查询获取指定的配置实例
     * @param id 实例的唯一 id
     * @return 宏配置实例，若基于 id 的查询无法找到具体的配置映射，那么会返回一个空的 Optional
     */
    Optional<MacroConfig> selectById(UUID id);

    /**
     * 插入/创建一份新的宏配置，持久化到文件系统，该方法不会影响传入的实例，而是会先创建一份MacroConfig的拷贝，然后对拷贝
     * 注入如ID、创建日期、修改日期字段后插入，并返回这份拷贝
     * @param config 宏配置
     * @return 返回创建的拷贝，外部可以通过拷贝查询插入结果，例如插入的配置的UUID，若插入失败则直接抛出异常
     */
    MacroConfig insert(MacroConfig config);

    /**
     * 批量插入多份配置，若该批次中某一份配置插入失败，那么会直接中断插入，并删除前面已经创建的配置，换句话说
     * 插入的实体要么全部成功，要么全部失败
     * @param configs 单批次插入的配置列表
     * @return 返回插入成功的对象的拷贝副本列表
     */
    List<MacroConfig> insertBatch(List<MacroConfig> configs);

    /**
     * 更新宏配置，基于 UUID 字段定位更新目标，若 UUID 为空或找不到指定的配置记录会更新失败
     * @param config 修改过后的配置实例，通过 UUID 与某个已经存在的配置实例建立映射
     * @return 若更新成功则返回 true，更新失败则会返回 false
     */
    boolean update(MacroConfig config);

    /**
     * 删除指定的配置实例，删除操作具备级联语义：
     * <ul>
     *   <li>若宏配置存在，则会同时删除所有关联的 MacroEntry 实例</li>
     *   <li>级联删除作为 Dao 层语义保证，不依赖 Service 层额外处理</li>
     * </ul>
     * @param id 删除目标的 id
     * @return 若成功删除则会返回一个带有被删除配置实例的Optional容器，若删除失败则返回一个空的Optional容器
     *

     */
    Optional<MacroConfig> deleteById(UUID id);

    /**
     * 删除所有的配置实例
     * @return 该方法会将所有被删除的配置实例加入一个列表当中返回
     */
    List<MacroConfig> deleteAll();

}
