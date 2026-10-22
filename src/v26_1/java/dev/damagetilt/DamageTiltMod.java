package dev.damagetilt;

import com.mojang.blaze3d.platform.InputConstants;
import dev.damagetilt.mixin.GameOptionsAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// 26.x – unobfuscated Mojang names; the directional damage tilt is native (damageTiltStrength option)
public class DamageTiltMod implements ClientModInitializer {

    public static final String MOD_ID = "damagetilt";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static KeyMapping toggleKey;
    private static boolean initialStateApplied = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("DamageTilt Mod loaded!");
        DamageTiltConfig.load();

        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.damagetilt.toggle",
                InputConstants.Type.KEYSYM,
                DamageTiltConfig.getKeyCode(),
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!initialStateApplied && client.options != null) {
                ((GameOptionsAccessor) client.options).getDamageTiltStrength()
                        .set(DamageTiltConfig.isEnabled() ? 1.0 : 0.0);
                initialStateApplied = true;
            }

            if (DamageTiltConfig.isFirstLaunch() && client.screen == null && client.level != null) {
                client.setScreen(new FirstLaunchScreen());
            }

            while (toggleKey.consumeClick()) {
                boolean newState = !DamageTiltConfig.isEnabled();
                DamageTiltConfig.setEnabled(newState);
                DamageTiltConfig.save();

                ((GameOptionsAccessor) client.options).getDamageTiltStrength().set(newState ? 1.0 : 0.0);
                client.options.save();

                if (client.player != null) {
                    client.gui.setOverlayMessage(
                            Component.literal("[DamageTilt] Damage Tilt: " + (newState ? "ON" : "OFF")),
                            false
                    );
                }
            }
        });
    }

    public static KeyMapping getToggleKey() {
        return toggleKey;
    }
}
