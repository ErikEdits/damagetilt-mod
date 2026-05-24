package dev.damagetilt.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Always suppresses the vanilla hurtTime-based camera tilt in GameRenderer
 * (which always tilts the same direction — a 10-year-old bug in 1.19–1.19.2).
 * CameraDirectionalTiltMixin applies the correct directional tilt instead
 * when the mod is enabled.
 */
@Mixin(GameRenderer.class)
public abstract class DamageTiltHandlerMixin {

    @Redirect(
        method = "renderWorld",
        at = @At(
            value  = "FIELD",
            target = "Lnet/minecraft/entity/LivingEntity;hurtTime:I",
            opcode = Opcodes.GETFIELD
        ),
        require = 0
    )
    private int suppressVanillaBrokenTilt(LivingEntity entity) {
        // Always return 0: CameraDirectionalTiltMixin handles the tilt correctly.
        return 0;
    }
}
