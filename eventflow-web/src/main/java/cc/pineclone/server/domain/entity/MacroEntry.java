package cc.pineclone.server.domain.entity;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * 宏配置实例，每一个 MacroEntry 最终都会被映射到一个具体的宏实例对象
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public final class MacroEntry {
    /**
     * 宏的唯一标识符，内核会维护当前至多一份配置文件生效，一份配置文件中可以定义多个宏实例，前端通过 UUID 来控制
     * 修改不同的宏实例对象
     */
    private UUID id;
    /**
     * 配置文件 UUID，描述了当前宏配置实例映射到哪一个宏配置文件，宏配置文件到宏配置实例为一对多的关系
     */
    private UUID configId;
    /**
     * 宏类型，内核维护了一份类型列表，Service层会对类型进行判断，以确定类型符合具体的要求
     */
    private String type;
    /**
     * 描述当前宏是否处于激活状态，该字段会直接影响
     */
    private Boolean enabled;
    /**
     * 宏实例创建时间
     */
    private Instant createdAt;

    /**
     * 宏实例最后修改时间
     */
    private Instant lastModifiedAt;
    /**
     * 直接面向 Json 读写，因此不会解析任何配置内容，也不会对配置内容做任何合法性校验，合法性校验的职责
     * 会被移交给Service层，由Service层直接负责Entity到DTO的转化
     */
    private JsonNode configNode;
}
