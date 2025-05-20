package cn.stars.reversal.util.render;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.module.impl.hud.CPSCounter;
import cn.stars.reversal.module.impl.hud.Keystrokes;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class KeystrokeUtil implements GameInstance {

    private final Animation alphaAnimation = new Animation(Easing.EASE_OUT_EXPO, 250);

    KeyBinding key;
    int mouseButton;
    boolean forMouse;

    public void setUpKey(final KeyBinding k) {
        key = k;
        forMouse = false;
    }

    public void setUpMouse(int m) {
        mouseButton = m;
        forMouse = true;
    }

    public void drawButton(final double x, final double y, final double width) {
        final Color color = ModuleInstance.getModule(Keystrokes.class).colorValue.getColor();
        final boolean shadow = ModuleInstance.getModule(PostProcessing.class).bloom.enabled;
        final String keyName = Keyboard.getKeyName(key.getKeyCode());

        float offset = -5;

        switch (keyName) {
            case "A":
            case "D":
                offset = -4;
                break;
            case "S":
                offset = -3.5f;
                break;
        }

        if (ModuleInstance.getModule(Keystrokes.class).modeValue.getMode().equals("Modern")) {
            if (key == Minecraft.getMinecraft().gameSettings.keyBindJump) {
                RenderUtil.roundedRectangle(x - width - 2, y - 30, width * 3 + 8, width, 2, new Color(255, 255, 255, 15 + (int) alphaAnimation.getValue()));
                regular18.drawString(keyName, (float) (x + 1), (float) (y - 19), key.isKeyDown() ? color.getRGB() : -1);
                if (shadow) {
                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x - width - 2, y - 30, width * 3 + 8, width, 2, key.isKeyDown() ? new Color(255, 255, 255, 255) : new Color(255, 255, 255, 15));
                    });
                }
            } else {
                RenderUtil.roundedRectangle(x, y, width, width, 2, new Color(255, 255, 255, 15 + (int) alphaAnimation.getValue()));
                regular18.drawString(keyName, (float) (x + 13 + offset), (float) (y + 15 - 4.5), key.isKeyDown() ? color.getRGB() : -1);
                if (shadow) {
                    MODERN_BLOOM_RUNNABLES.add(() -> {
                        RenderUtil.roundedRectangle(x, y, width, width, 2, key.isKeyDown() ? new Color(255, 255, 255, 255) : new Color(255, 255, 255, 15));
                    });
                }
            }
        } else {
            if (key == Minecraft.getMinecraft().gameSettings.keyBindJump) {
                RenderUtil.rect(x - width - 2, y, width * 3 + 8, width - 6, new Color((int) alphaAnimation.getValue(), (int) alphaAnimation.getValue(), (int) alphaAnimation.getValue(), 100));
                mc.fontRendererObj.drawStringWithShadow(keyName, (float) (x), (float) (y + 15 - 8), key.isKeyDown() ? color.getRGB() : -1);
            } else {
                RenderUtil.rect(x, y, width, width, new Color((int) alphaAnimation.getValue(), (int) alphaAnimation.getValue(), (int) alphaAnimation.getValue(), 100));
                mc.fontRendererObj.drawStringWithShadow(keyName, (float) (x + 10), (float) (y + 15 - 5), key.isKeyDown() ? color.getRGB() : -1);
            }
        }
    }

    public void drawButtonForMouse(final double x, final double y, final double width) {
        final Color color = ModuleInstance.getModule(Keystrokes.class).colorValue.getColor();
        final int leftCps = ModuleInstance.getModule(CPSCounter.class).leftClicks.size();
        final int rightCps = ModuleInstance.getModule(CPSCounter.class).rightClicks.size();

        if (ModuleInstance.getModule(Keystrokes.class).modeValue.getMode().equals("Minecraft")) {
            RenderUtil.rect(x, y, width + 15, width, new Color((int) alphaAnimation.getValue(), (int) alphaAnimation.getValue(), (int) alphaAnimation.getValue(), 100));
            if (ModuleInstance.getModule(Keystrokes.class).showCpsValue.enabled) {
                mc.fontRendererObj.drawStringWithShadow(mouseButton == 0 ? "LMB" : "RMB", (float) (x + 12), (float) (y + 15 - 7), Mouse.isButtonDown(mouseButton) ? color.getRGB() : -1);
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5f, 0.5f, 0.5f);
                mc.fontRendererObj.drawCenteredStringWithShadow((mouseButton == 0 ? leftCps : rightCps) + " CPS", (float) (x + 20) * 2, (float) (y + 15 + 2) * 2, Mouse.isButtonDown(mouseButton) ? color.getRGB() : -1);
                GlStateManager.popMatrix();
            } else {
                mc.fontRendererObj.drawStringWithShadow(mouseButton == 0 ? "LMB" : "RMB", (float) (x + 12), (float) (y + 15 - 4.5), Mouse.isButtonDown(mouseButton) ? color.getRGB() : -1);
            }
        }
    }

    public void updateAnimations() {
        if (key == null)
            return;

        if (key.isKeyDown())
            alphaAnimation.run(150);
        else
            alphaAnimation.run(0);
    }

    public void updateAnimationsForMouse() {
        if (Mouse.isButtonDown(mouseButton))
            alphaAnimation.run(150);
        else
            alphaAnimation.run(0);
    }

}
