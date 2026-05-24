package dev.damagetilt.mixin;

import dev.damagetilt.DamageTiltConfig;
import dev.damagetilt.DamageTiltMod;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * After Camera.update() computes the base yaw/pitch orientation, this mixin
 * appends a directional roll (Z-axis rotation) proportional to the player's
 * hurtTime and scaled by sin(relativeAttackAngle), replicating the behaviour
 * that Mojang fixed natively in 1.19.4.
 *
 * DamageTiltHandlerMixin suppresses the vanilla always-rightward tilt in
 * GameRenderer so only our corrected version plays.
 */
@Mixin(Camera.class)
public abstract class CameraDirectionalTiltMixin {

    @Shadow private Quaternion rotation;

    @Inject(method = "update", at = @At("RETURN"), require = 0)
    private void applyDirectionalHurtTilt(BlockView area, Entity focusedEntity,
                                          boolean thirdPerson, boolean inverseView,
                                          float tickDelta, CallbackInfo ci) {
        if (!DamageTiltConfig.isEnabled()) return;
        if (!(focusedEntity instanceof LivingEntity entity)) return;

        float h = (float) entity.hurtTime - tickDelta;
        if (h <= 0.0f) return;

        // Same magnitude formula as vanilla
        float magnitude = MathHelper.sin(h / (float) entity.maxHurtTime * h * MathHelper.PI) * 14.0f;

        // sin(relativeAngle): +1 = attacker is fully to the left  → tilt left
        //                     -1 = attacker is fully to the right → tilt right
        float tiltAngle = magnitude * MathHelper.sin(DamageTiltMod.lastDamageAngle);

        // Multiply camera rotation by a local-frame Z roll (hamiltonProduct applies in local space)
        rotation.hamiltonProduct(Vec3f.POSITIVE_Z.getDegreesQuaternion(tiltAngle));
    }
}
