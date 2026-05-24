package dev.damagetilt.mixin;

import dev.damagetilt.DamageTiltConfig;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Suppresses the damage-tilt camera roll (pre-1.20.5, where damageTiltStrength
 * does not yet exist in GameOptions).  We redirect every read of
 * LivingEntity.hurtTime inside GameRenderer.renderWorld to 0 when the user
 * has the effect disabled – the rotation code sees no hurt-time and skips
 * the roll.  require=0 makes the injection optional so the mod still loads
 * even if the method signature changes between minor versions.
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
    private int suppressHurtTime(LivingEntity entity) {
        return DamageTiltConfig.isEnabled() ? entity.hurtTime : 0;
    }
}
