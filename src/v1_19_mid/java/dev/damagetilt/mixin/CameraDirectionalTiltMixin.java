package dev.damagetilt.mixin;

import dev.damagetilt.DamageTiltConfig;
import dev.damagetilt.DamageTiltMod;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 1.19.3 – MC switched from net.minecraft.util.math.Quaternion/Vec3f to JOML
 * (org.joml.Quaternionf) in this version.  Same directional-tilt logic as
 * v1_19_old but uses JOML's rotateLocalZ instead of hamiltonProduct.
 */
@Mixin(Camera.class)
public abstract class CameraDirectionalTiltMixin {

    @Shadow private Quaternionf rotation;

    @Inject(method = "update", at = @At("RETURN"), require = 0)
    private void applyDirectionalHurtTilt(BlockView area, Entity focusedEntity,
                                          boolean thirdPerson, boolean inverseView,
                                          float tickDelta, CallbackInfo ci) {
        if (!DamageTiltConfig.isEnabled()) return;
        if (!(focusedEntity instanceof LivingEntity entity)) return;

        float h = (float) entity.hurtTime - tickDelta;
        if (h <= 0.0f) return;

        float magnitude = MathHelper.sin(h / (float) entity.maxHurtTime * h * (float) Math.PI) * 14.0f;
        float tiltDeg   = magnitude * MathHelper.sin(DamageTiltMod.lastDamageAngle);

        // JOML: rotateLocalZ applies the rotation in the camera's own local frame
        rotation.rotateLocalZ((float) Math.toRadians(tiltDeg));
    }
}
