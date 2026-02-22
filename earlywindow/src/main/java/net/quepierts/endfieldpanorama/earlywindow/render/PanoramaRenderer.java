package net.quepierts.endfieldpanorama.earlywindow.render;

import lombok.RequiredArgsConstructor;
import net.quepierts.endfieldpanorama.earlywindow.Resource;
import net.quepierts.endfieldpanorama.earlywindow.render.pipeline.Mesh;
import net.quepierts.endfieldpanorama.earlywindow.render.pipeline.VertexBuffer;
import net.quepierts.endfieldpanorama.earlywindow.render.shader.ShaderManager;
import net.quepierts.endfieldpanorama.earlywindow.render.shader.program.PanoramaShader;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL31;

@RequiredArgsConstructor
public final class PanoramaRenderer implements Resource {

    private final Mesh              mesh;
    private final VertexBuffer      buffer;
    private final PanoramaShader    shader;
    private final CubeMapTexture    texture;

    public PanoramaRenderer(@NotNull ShaderManager manager) {
        this.mesh = mesh();
        this.buffer = new VertexBuffer();
        this.shader = new PanoramaShader(manager);
        this.texture = CubeMapTexture.fromResource("skybox");

        this.buffer.upload(this.mesh);
    }

    public void render(@NotNull Matrix4f projectionMatrix, float time) {
        var matrix = new Matrix4f(projectionMatrix)
                .rotateY(time * 0.05f);

        this.shader.uProjectionViewMatrix.setMatrix4f(matrix);
        this.shader.bind();
        this.shader.upload();

        GL31.glDepthMask(false);
        this.texture.bind(0);
        this.buffer.draw();
        this.texture.unbind(0);
        GL31.glDepthMask(true);

        this.shader.unbind();
    }

    private static Mesh mesh() {

        final float x0  =  1.0f;
        final float y0  =  1.0f;
        final float z0  =  1.0f;

        final float x1  = -1.0f;
        final float y1  = -1.0f;
        final float z1  = -1.0f;

        var p000 = new float[] { x0, y0, z0 };
        var p001 = new float[] { x0, y0, z1 };
        var p010 = new float[] { x0, y1, z0 };
        var p011 = new float[] { x0, y1, z1 };
        var p100 = new float[] { x1, y0, z0 };
        var p101 = new float[] { x1, y0, z1 };
        var p110 = new float[] { x1, y1, z0 };
        var p111 = new float[] { x1, y1, z1 };

        return Mesh.builder(DefaultVertexFormats.BLIT_SCREEN, 64)
                .quad(p101, p100, p110, p111)
                .quad(p000, p001, p011, p010)
                .quad(p011, p111, p110, p010)
                .quad(p100, p101, p001, p000)
                .quad(p001, p101, p111, p011)
                .quad(p100, p000, p010, p110)
                .build();
    }

    @Override
    public void free() {
        texture.free();
    }
}
