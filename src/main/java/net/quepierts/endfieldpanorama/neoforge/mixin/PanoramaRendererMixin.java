package net.quepierts.endfieldpanorama.neoforge.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.quepierts.endfieldpanorama.neoforge.EndfieldPanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PanoramaRenderer.class)
public class PanoramaRendererMixin {

    @Inject(
            method = "render",
            at = @At("RETURN")
    )
    public void endfieldpanorama$renderScene(GuiGraphics guiGraphics, int width, int height, float fade, float partialTick, CallbackInfo ci) {
        var renderer = EndfieldPanoramaRenderer.getInstance();
        renderer.update(partialTick);
        renderer.renderScene();
    }

}
