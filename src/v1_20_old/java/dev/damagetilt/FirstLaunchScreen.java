package dev.damagetilt;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

// 1.20 / 1.20.1 – DrawContext was already introduced in 1.20
public class FirstLaunchScreen extends Screen {

    private boolean waitingForKey = false;
    private int selectedKey = DamageTiltConfig.getKeyCode();
    private String keyName = InputUtil.fromKeyCode(selectedKey, 0).getLocalizedText().getString();

    public FirstLaunchScreen() {
        super(Text.literal("DamageTilt — Set Hotkey"));
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(
                Text.literal("Press a key to set hotkey"),
                btn -> {
                    waitingForKey = true;
                    btn.setMessage(Text.literal("§ePress any key now..."));
                }
        ).dimensions(this.width / 2 - 100, this.height / 2 + 10, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.literal("Confirm & Start"),
                btn -> confirm()
        ).dimensions(this.width / 2 - 100, this.height / 2 + 40, 200, 20).build());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (waitingForKey) {
            if (keyCode == InputUtil.GLFW_KEY_ESCAPE) {
                waitingForKey = false;
                return true;
            }
            selectedKey = keyCode;
            keyName = InputUtil.fromKeyCode(keyCode, scanCode).getLocalizedText().getString();
            waitingForKey = false;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void confirm() {
        DamageTiltConfig.setKeyCode(selectedKey);
        DamageTiltConfig.setFirstLaunch(false);
        DamageTiltConfig.save();
        DamageTiltMod.getToggleKey().setBoundKey(InputUtil.fromKeyCode(selectedKey, 0));
        KeyBinding.updateKeysByCode();
        this.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("§6§lDamageTilt Mod"),
                this.width / 2, this.height / 2 - 60, 0xFFFFFF);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Choose a hotkey to toggle the Damage Tilt effect."),
                this.width / 2, this.height / 2 - 40, 0xAAAAAA);

        String display = waitingForKey ? "§ePress any key..." : "§aCurrent Hotkey: §f" + keyName;
        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal(display),
                this.width / 2, this.height / 2 - 15, 0xFFFFFF);

        context.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("§7You can change the hotkey anytime in Options → Controls."),
                this.width / 2, this.height / 2 + 70, 0x888888);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
