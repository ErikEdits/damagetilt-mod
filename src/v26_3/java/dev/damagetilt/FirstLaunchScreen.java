package dev.damagetilt;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

// 26.x – GuiGraphicsExtractor rendering and KeyEvent input
public class FirstLaunchScreen extends Screen {

    private static final int KEY_ESCAPE = 256; // GLFW_KEY_ESCAPE; LWJGL is not on the 26.3 compile classpath

    private boolean waitingForKey = false;
    private int selectedKey = DamageTiltConfig.getKeyCode();
    private String keyName = InputConstants.Type.KEYBOARD.getOrCreate(selectedKey).getDisplayName().getString();

    public FirstLaunchScreen() {
        super(Component.literal("DamageTilt — Set Hotkey"));
    }

    @Override
    protected void init() {
        addRenderableWidget(Button.builder(
                Component.literal("Press a key to set hotkey"),
                btn -> {
                    waitingForKey = true;
                    btn.setMessage(Component.literal("Press any key now..."));
                }
        ).bounds(this.width / 2 - 100, this.height / 2 + 10, 200, 20).build());

        addRenderableWidget(Button.builder(
                Component.literal("Confirm & Start"),
                btn -> confirm()
        ).bounds(this.width / 2 - 100, this.height / 2 + 40, 200, 20).build());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (waitingForKey) {
            if (event.key() == KEY_ESCAPE) {
                waitingForKey = false;
                return true;
            }
            selectedKey = event.key();
            keyName = InputConstants.Type.KEYBOARD.getOrCreate(event.key()).getDisplayName().getString();
            waitingForKey = false;
            return true;
        }
        return super.keyPressed(event);
    }

    private void confirm() {
        DamageTiltConfig.setKeyCode(selectedKey);
        DamageTiltConfig.setFirstLaunch(false);
        DamageTiltConfig.save();
        DamageTiltMod.getToggleKey().setKey(InputConstants.Type.KEYBOARD.getOrCreate(selectedKey));
        KeyMapping.resetMapping();
        this.onClose();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);

        drawCentered(g, "DamageTilt Mod", this.height / 2 - 60, 0xFFFFAA00);
        drawCentered(g, "Choose a hotkey to toggle the Damage Tilt effect.", this.height / 2 - 40, 0xFFAAAAAA);
        drawCentered(g, waitingForKey ? "Press any key..." : "Current Hotkey: " + keyName, this.height / 2 - 15, 0xFFFFFFFF);
        drawCentered(g, "You can change the hotkey anytime in Options -> Controls.", this.height / 2 + 70, 0xFF888888);
    }

    private void drawCentered(GuiGraphicsExtractor g, String text, int y, int color) {
        g.text(this.font, text, this.width / 2 - this.font.width(text) / 2, y, color);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
