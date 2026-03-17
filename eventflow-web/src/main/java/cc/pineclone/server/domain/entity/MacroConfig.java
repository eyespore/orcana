package cc.pineclone.server.domain.entity;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MacroConfig {

    /**
     * 配置 ID，宏配置文件的唯一标识符
     */
    private UUID id;
    /**
     * 配置名称，用于向客户端显示
     */
    private String name;
    /**
     * 宏配置版本号，对应宏内核版本
     */
    private String version;
    /**
     * 配置文件创建时间
     */
    private Instant createdAt;
    /**
     * 配置文件上一次修改时间
     */
    private Instant lastModifiedAt;

}

