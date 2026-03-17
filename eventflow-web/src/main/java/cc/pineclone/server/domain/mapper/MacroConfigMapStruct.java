package cc.pineclone.server.domain.mapper;

import cc.pineclone.server.domain.entity.MacroConfig;
import org.mapstruct.Mapper;

@Mapper
public interface MacroConfigMapStruct {

    /**
     * 为一份MacroConfig创建拷贝，面向Dao层
     * @param source 拷贝数据源
     * @return 拷贝结果
     */
    MacroConfig copy(MacroConfig source);

}
