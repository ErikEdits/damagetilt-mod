package dev.damagetilt.mixin;

import dev.damagetilt.DamageTiltConfig;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
