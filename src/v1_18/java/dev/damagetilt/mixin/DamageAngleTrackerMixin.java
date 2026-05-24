package dev.damagetilt.mixin;

import dev.damagetilt.DamageTiltMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts the moment a living entity takes damage and, if that entity is
 * the local player, records the horizontal angle (in radians) from the player
 * to the attacker relative to the player's current yaw.  This is used by
 * CameraDirectionalTiltMixin to apply a properly-directional hurt-tilt in
 * MC 1.19–1.19.2 where vanilla always tilts the same way (10-year-old bug).
 */
@Mixin(LivingEntity.class)
public abstract class DamageAngleTrackerMixin {

    @Inject(method = "damage", at = @At("HEAD"))
    private void trackAttackerAngle(DamageSource source, float amount,
                                    CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || (Object)this != client.player) return;

        Entity attacker = source.getAttacker();
        if (attacker == null) return;

        double dx = attacker.getX() - client.player.getX();
        double dz = attacker.getZ() - client.player.getZ();

        // atan2(-dx, dz) gives the attacker's angle in MC yaw convention
        // (0 = South/+Z, 90 = West/-X, -90 = East/+X, 180 = North/-Z)
        double worldAngleDeg = Math.toDegrees(Math.atan2(-dx, dz));

        // Relative to the player's facing direction; positive = attacker is to the left
        DamageTiltMod.lastDamageAngle = (float) Math.toRadians(worldAngleDeg - client.player.getYaw());
    }
}
