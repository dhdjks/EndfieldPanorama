package net.quepierts.els.mixin;

import net.minecraft.client.Minecraft;
import net.quepierts.els.render.EndfieldBackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Inject(
            method = "resizeDisplay",
            at = @At("RETURN")
    )
    public void els$resizeDisplay(CallbackInfo ci) {
        if (!EndfieldBackgroundRenderer.setuped()) {
            return;
        }

        var renderer        = EndfieldBackgroundRenderer.getInstance();
        var window          = Minecraft.getInstance().getWindow();
        renderer.resize(window.getWidth(), window.getHeight());
    }

    @Inject(
            method = "destroy",
            at = @At("HEAD")
    )
    public void els$destroy(CallbackInfo ci) {
        EndfieldBackgroundRenderer.getInstance().destroy();
    }

}
