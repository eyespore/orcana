package cc.pineclone.server.service;

import cc.pineclone.server.dao.FontpackDao;
import cc.pineclone.server.domain.FontpackMetadata;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Deprecated
public class FontpackService {

    private final FontpackDao dao;

    @Getter
    private static final FontpackService instance = new FontpackService();

    private FontpackService() {
        this.dao = FontpackDao.getInstance();
    }

    /**
     * 创建一个新的字体包（分配uuid后建立目录，将字体包元数据存储进入metadata）
     */
    public FontpackMetadata createFontPack(String name, boolean enabled ,String desc, int type, int structure ,long size, boolean isBased) throws IOException {
        String uuid = UUID.randomUUID().toString();  /* 为新的fontpack生成uuid */
        Path packDir = getFontPackDir(uuid);

        if (!Files.exists(packDir)) {
            Files.createDirectories(packDir);
        }

        FontpackMetadata meta = FontpackMetadata.builder().id(uuid).name(name).enabled(enabled).size(size)
                .desc(desc).type(type).structure(structure).createAt(System.currentTimeMillis()).isBased(isBased).build();
        dao.save(meta);
        return meta;
    }

    /**
     * 删除一个字体包（同时删除metadata和目录）
     */
    public boolean deleteFontPack(String id) throws IOException {
        Optional<FontpackMetadata> found = dao.findById(id);
        if (found.isEmpty()) return false;

        // 遍历删除目录
        Path dir = getFontPackDir(id);
        if (Files.exists(dir)) {
            try (var stream = Files.walk(dir)) {
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        e.printStackTrace(); // 或记录日志
                    }
                });
            }
        }

        dao.delete(id);
        return true;
    }

    /**
     * 获取字体包列表
     */
    public List<FontpackMetadata> listFontPacks() {
        return dao.listAll().stream().sorted(Comparator.comparing(FontpackMetadata::getName)).toList();  // reversed 确保 true 排在后面

    }

    public List<FontpackMetadata> listFontPacksByCondition(FontpackMetadata metadata) {
        return dao.findByCondition(metadata);
    }

    /**
     * 修改字体包元数据（不会重命名目录）
     */
    public boolean updateFontPack(FontpackMetadata updatedMeta) throws IOException {
        Optional<FontpackMetadata> existing = dao.findById(updatedMeta.getId());
        if (existing.isEmpty()) return false;

        dao.update(updatedMeta);
        return true;
    }

    /**
     * 获取某个字体包的本地目录
     */
    public Path getFontPackDir(String id) {
        return dao.getBaseDir().resolve(id);
    }

    /**
     * 获取某个字体包的元数据
     */
    public Optional<FontpackMetadata> getFontPackMetadata(String id) {
        return dao.findById(id);
    }

}
