package dev.damagetilt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import dev.damagetilt.mixin.GameOptionsAccessor;

public class DamageTiltMod implements ClientModInitializer {

    public static final String MOD_ID = "damagetilt";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static KeyBinding toggleKey;
    private static boolean initialStateApplied = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("DamageTilt Mod loaded!");
        DamageTiltConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.damagetilt.toggle",
                InputUtil.Type.KEYSYM,
                DamageTiltConfig.getKeyCode(),
                "category.damagetilt"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!initialStateApplied && client.options != null) {
                ((GameOptionsAccessor) client.options).getDamageTiltStrength()
                        .setValue(DamageTiltConfig.isEnabled() ? 1.0 : 0.0);
                initialStateApplied = true;
            }

            if (DamageTiltConfig.isFirstLaunch() && client.currentScreen == null && client.world != null) {
                client.setScreen(new FirstLaunchScreen());
            }

            while (toggleKey.wasPressed()) {
                boolean newState = !DamageTiltConfig.isEnabled();
                DamageTiltConfig.setEnabled(newState);
                DamageTiltConfig.save();

                ((GameOptionsAccessor) client.options).getDamageTiltStrength().setValue(newState ? 1.0 : 0.0);
                client.options.write();

                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§6[DamageTilt] §rDamage Tilt: " + (newState ? "§aON" : "§cOFF")),
                            true
                    );
                }
            }
        });
    }

    public static KeyBinding getToggleKey() {
        return toggleKey;
    }
}
