package cc.pineclone.server.dao;

import cc.pineclone.server.domain.FontpackMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Deprecated
public class FontpackDao {

    @Getter private Path baseDir;
    private Path metadataFile;
    private ObjectMapper mapper;

    private List<FontpackMetadata> cache = new ArrayList<>();

    @Getter
    private static final FontpackDao instance = new FontpackDao();

    private FontpackDao() {
//        this.baseDir = PathUtils.getFontpacksBaseDirPath();  /* 字体包基础路径 */
//        this.metadataFile = baseDir.resolve("metadata.json");
//        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        try {
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initialize() throws IOException {
        if (!Files.exists(baseDir)) {
            Files.createDirectories(baseDir);
        }
        if (!Files.exists(metadataFile)) {
            Files.writeString(metadataFile, "[]");
        }
        loadCache();
    }

    /* 读取元数据 */
    private void loadCache() throws IOException {
        cache = new ArrayList<>(Arrays.asList(
                mapper.readValue(metadataFile.toFile(), FontpackMetadata[].class)
        ));
    }

    /* 缓存元数据 */
    private void saveCache() throws IOException {
        mapper.writeValue(metadataFile.toFile(), cache);
    }

    /* 查询所有字体包 */
    public List<FontpackMetadata> listAll() {
        return List.copyOf(cache);
    }

    /* 根据id查找字体包 */
    public Optional<FontpackMetadata> findById(String id) {
        return cache.stream().filter(meta -> meta.getId().equals(id)).findFirst();
    }

    public List<FontpackMetadata> findByCondition(FontpackMetadata metadata) {
        if (metadata == null) {
            return List.copyOf(cache);
        }
        Stream<FontpackMetadata> stream = List.copyOf(cache).stream();
        if (metadata.getId() != null) {
            stream = stream.filter(meta -> meta.getId().equals(metadata.getId()));
        }
        if (metadata.getName() != null && !metadata.getName().trim().isEmpty()) {
            stream = stream.filter(meta -> meta.getName().equals(metadata.getName()));
        }
        if (metadata.getDesc() != null && !metadata.getDesc().trim().isEmpty()) {
            stream = stream.filter(meta -> meta.getDesc().equals(metadata.getDesc()));
        }
        if (metadata.getType() != null) {
            stream = stream.filter(meta -> meta.getType().equals(metadata.getType()));
        }
        if (metadata.getSize() != null) {
            stream = stream.filter(meta -> meta.getSize().equals(metadata.getSize()));
        }
        if (metadata.getCreateAt() != null) {
            stream = stream.filter(meta -> meta.getCreateAt().equals(metadata.getCreateAt()));
        }
        if (metadata.getEnabled() != null) {
            stream = stream.filter(meta -> meta.getEnabled().equals(metadata.getEnabled()));
        }
        if (metadata.getStructure() != null) {
            stream = stream.filter(meta -> meta.getStructure().equals(metadata.getStructure()));
        }
        if (metadata.getIsBased() != null) {
            stream = stream.filter(meta -> meta.getIsBased().equals(metadata.getIsBased()));
        }
        return stream.toList();
    }

    /* 保存字体包元数据 */
    public void save(FontpackMetadata meta) throws IOException {
        cache.add(meta);
        saveCache();
    }

    /* 更新字体包元数据 */
    public void update(FontpackMetadata updatedMeta) throws IOException {
        delete(updatedMeta.getId());
        save(updatedMeta);
    }

    /* 根据id删除 */
    public void delete(String id) throws IOException {
        cache.removeIf(meta -> meta.getId().equals(id));
        saveCache();
    }
}
