package dev.damagetilt;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

// 1.18 / 1.18.1 / 1.18.2 – no Text.literal(), no builder ButtonWidget; drawCenteredTextWithShadow needs OrderedText
public class FirstLaunchScreen extends Screen {

    private boolean waitingForKey = false;
    private int selectedKey = DamageTiltConfig.getKeyCode();
    private String keyName = InputUtil.fromKeyCode(selectedKey, 0).getLocalizedText().getString();

    public FirstLaunchScreen() {
        super(new LiteralText("DamageTilt — Set Hotkey"));
    }

    @Override
    protected void init() {
        addDrawableChild(new ButtonWidget(
                this.width / 2 - 100, this.height / 2 + 10, 200, 20,
                new LiteralText("Press a key to set hotkey"),
                btn -> {
                    waitingForKey = true;
                    btn.setMessage(new LiteralText("§ePress any key now..."));
                }
        ));

        addDrawableChild(new ButtonWidget(
                this.width / 2 - 100, this.height / 2 + 40, 200, 20,
                new LiteralText("Confirm & Start"),
                btn -> confirm()
        ));
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
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);

        drawCenteredTextWithShadow(matrices, this.textRenderer,
                new LiteralText("§6§lDamageTilt Mod").asOrderedText(),
                this.width / 2, this.height / 2 - 60, 0xFFFFFF);

        drawCenteredTextWithShadow(matrices, this.textRenderer,
                new LiteralText("Choose a hotkey to toggle the Damage Tilt effect.").asOrderedText(),
                this.width / 2, this.height / 2 - 40, 0xAAAAAA);

        String display = waitingForKey ? "§ePress any key..." : "§aCurrent Hotkey: §f" + keyName;
        drawCenteredTextWithShadow(matrices, this.textRenderer,
                new LiteralText(display).asOrderedText(),
                this.width / 2, this.height / 2 - 15, 0xFFFFFF);

        drawCenteredTextWithShadow(matrices, this.textRenderer,
                new LiteralText("§7You can change the hotkey anytime in Options → Controls.").asOrderedText(),
                this.width / 2, this.height / 2 + 70, 0x888888);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
