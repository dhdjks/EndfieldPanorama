package net.quepierts.endfieldpanorama.earlywindow.render;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.quepierts.endfieldpanorama.earlywindow.EarlyResourceLoader;
import net.quepierts.endfieldpanorama.earlywindow.Resource;
import org.lwjgl.opengl.GL31;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class CubeMapTexture extends BaseTexture implements Resource {

    private static final int[] REMAP = {
            4, 0, 5, 1, 2, 3
    };

    @Getter
    private final int textureId;

    private boolean free = false;

    public static CubeMapTexture fromResource(String name) {

        var raw     = new byte[6][];
        for (int i = 0; i < 6; i++) {
            raw[i]  = EarlyResourceLoader.loadByteArray("textures/" + name + "_" + i + ".png");

            if (raw[i].length == 0) {
                throw new RuntimeException("Failed to load texture: " + name);
            }
        }

        var id  = GL31.glGenTextures();
        GL31.glBindTexture(GL31.GL_TEXTURE_CUBE_MAP, id);

        var width       = new int[1];
        var height      = new int[1];
        var channels    = new int[1];

        for (int i = 0; i < 6; i++) {
            var tmp          = MemoryUtil.memAlloc(raw[i].length);
            tmp.put(raw[i]);
            tmp.flip();

            var buffer      = STBImage.stbi_load_from_memory(tmp, width, height, channels, 4);

            if (buffer == null) {
                var reason = STBImage.stbi_failure_reason();
                throw new RuntimeException("Failed to load cube texture [" + name + "_" + i + "]: " + reason);
            }

            GL31.glTexImage2D(
                    GL31.GL_TEXTURE_CUBE_MAP_POSITIVE_X + REMAP[i],
                    0,
                    GL31.GL_RGBA,
                    width[0],
                    height[0],
                    0,
                    GL31.GL_RGBA,
                    GL31.GL_UNSIGNED_BYTE,
                    buffer
            );

            MemoryUtil.memFree(tmp);
            STBImage.stbi_image_free(buffer);
        }

        GL31.glTexParameteri(GL31.GL_TEXTURE_CUBE_MAP, GL31.GL_TEXTURE_MIN_FILTER, GL31.GL_NEAREST);
        GL31.glTexParameteri(GL31.GL_TEXTURE_CUBE_MAP, GL31.GL_TEXTURE_MAG_FILTER, GL31.GL_NEAREST);
        GL31.glTexParameteri(GL31.GL_TEXTURE_CUBE_MAP, GL31.GL_TEXTURE_WRAP_S, GL31.GL_CLAMP_TO_EDGE);
        GL31.glTexParameteri(GL31.GL_TEXTURE_CUBE_MAP, GL31.GL_TEXTURE_WRAP_T, GL31.GL_CLAMP_TO_EDGE);
        GL31.glTexParameteri(GL31.GL_TEXTURE_CUBE_MAP, GL31.GL_TEXTURE_WRAP_R, GL31.GL_CLAMP_TO_EDGE);

        return new CubeMapTexture(id);
    }

    @Override
    public void free() {
        if (this.free) {
            return;
        }
        this.free = true;
        if (this.textureId != 0) {
            this.unbind();
            GL31.glDeleteTextures(textureId);
        }
    }

    @Override
    public void bind() {
        GL31.glBindTexture(GL31.GL_TEXTURE_CUBE_MAP, textureId);
    }

    @Override
    public void unbind() {
        GL31.glBindTexture(GL31.GL_TEXTURE_CUBE_MAP, 0);
    }
}
