package dev.damagetilt.mixin;

import dev.damagetilt.DamageTiltConfig;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyArg(
        method = "renderWorld",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/GameRenderer;tiltViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V"
        ),
        index = 1
    )
    private float damagetilt_suppressTilt(float tickDelta) {
        if (!DamageTiltConfig.isEnabled()) {
            return 0.0f;
        }
        return tickDelta;
    }
}
