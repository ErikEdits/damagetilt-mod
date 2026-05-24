package dev.damagetilt.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Suppresses the vanilla broken-direction hurt tilt in GameRenderer.
 * CameraDirectionalTiltMixin applies a properly-directional tilt instead.
 * (1.19.3 has the same broken tilt as 1.19–1.19.2; 1.19.4 has it natively
 * fixed, but suppressing here + re-applying in Camera is equivalent and safe.)
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
        return 0;
    }
}
