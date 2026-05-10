package dev.damagetilt;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DamageTiltMod implements ClientModInitializer {

    public static final String MOD_ID = "damagetilt";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding toggleKey;

    @Override
    public void onInitializeClient() {
        LOGGER.info("DamageTilt Mod geladen!");

        DamageTiltConfig.load();

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.damagetilt.toggle",
                InputUtil.Type.KEYSYM,
                DamageTiltConfig.getKeyCode(),
                "category.damagetilt"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (DamageTiltConfig.isFirstLaunch() && client.currentScreen == null && client.world != null) {
                client.setScreen(new FirstLaunchScreen());
            }

            while (toggleKey.wasPressed()) {
                DamageTiltConfig.setEnabled(!DamageTiltConfig.isEnabled());
                DamageTiltConfig.save();

                String status = DamageTiltConfig.isEnabled() ? "§aAN" : "§cAUS";
                if (client.player != null) {
                    client.player.sendMessage(
                            Text.literal("§6[DamageTilt] §rDamage Tilt: " + status),
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
