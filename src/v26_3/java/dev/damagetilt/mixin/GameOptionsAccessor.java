package dev.damagetilt.mixin;

import net.minecraft.client.Options;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Options.class)
public interface GameOptionsAccessor {
    @Accessor("damageTiltStrength")
    OptionInstance<Double> getDamageTiltStrength();
}
