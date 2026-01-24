package net.quepierts.els.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.TitleScreen;
import net.quepierts.els.render.EndfieldBackgroundRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {

    @Inject(
            method = "<init>(ZLnet/minecraft/client/gui/components/LogoRenderer;)V",
            at = @At("RETURN")
    )
    public void els$setup(CallbackInfo ci) {
        EndfieldBackgroundRenderer.setup();
    }

    @Inject(
            method = "renderPanorama",
            at = @At("HEAD")
    )
    public void els$preparePanorama(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        var renderer = EndfieldBackgroundRenderer.getInstance();
        renderer.preparePanorama();
    }

    @Inject(
            method = "renderPanorama",
            at = @At("RETURN")
    )
    public void els$renderScene(GuiGraphics guiGraphics, float partialTick, CallbackInfo ci) {
        var renderer = EndfieldBackgroundRenderer.getInstance();
        renderer.renderScene(guiGraphics, partialTick);
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/TitleScreen;renderPanorama(Lnet/minecraft/client/gui/GuiGraphics;F)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void e$cancel(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        ci.cancel();
    }

}
