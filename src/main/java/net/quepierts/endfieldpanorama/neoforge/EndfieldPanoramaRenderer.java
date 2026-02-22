package net.quepierts.endfieldpanorama.neoforge;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.quepierts.endfieldpanorama.earlywindow.ResourceManager;
import net.quepierts.endfieldpanorama.earlywindow.scene.RenderScene;
import org.jetbrains.annotations.NotNull;

public final class EndfieldPanoramaRenderer {

    private static EndfieldPanoramaRenderer instance;

    private final RenderTarget  mainTarget;

    private float cachedPartialTick;

    private RenderScene scene;
    private ResourceManager manager;

    private boolean triggered;
    private float triggerTimer;

    private EndfieldPanoramaRenderer() {
        Minecraft minecraft = Minecraft.getInstance();
        this.mainTarget         = minecraft.getMainRenderTarget();
    }

    public void setup(RenderScene scene, ResourceManager manager) {
        this.scene = scene;
        this.manager = manager;
    }

    public void update(float partialTick) {
        this.cachedPartialTick = partialTick * 0.05f;
    }

    public void renderScene() {
        var delta = this.cachedPartialTick;
        if (!triggered && this.triggerTimer > 0.0f) {
            this.triggerTimer -= delta;

            if (this.triggerTimer <= 0.0f) {
                this.triggered = true;
                this.scene.trigger();
            }
        }

        this.mainTarget.bindWrite(false);
        this.scene.render(delta, () -> this.mainTarget.bindWrite(false));

        VertexBuffer.unbind();
    }

    public void resize(int width, int height) {
        if (this.scene != null) {
            this.scene.resize(width, height);
        }
    }

    public void destroy() {
        this.manager.free();
    }

    public static void setup() {
        if (instance != null) {
            return;
        }

        instance = new EndfieldPanoramaRenderer();
    }

    @NotNull
    public static EndfieldPanoramaRenderer getInstance() {
        return instance;
    }

    public static boolean setuped() {
        return instance != null;
    }

    public void trigger() {
        this.triggerTimer = 1.0f;
    }

}
