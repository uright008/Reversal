/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2024 Starlight, All rights reserved.
 */
package cn.stars.reversal.ui.gui;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.Transformer;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

import static cn.stars.reversal.GameInstance.*;

public class GuiDonate extends GuiScreen {
    private TextButton backButton;
    private TextButton[] buttons;

    @Override
    public void initGui() {
        backButton = new TextButton(this.width / 2 - 100, this.height - 60, 200, 20, () -> {
            mc.displayGuiScreen(Transformer.transformMainMenu());
        }, "返回", "", true, 1, 90, 5, 20);
        buttons = new TextButton[]{backButton};
    }

    @SneakyThrows
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // blur
        RiseShaders.GAUSSIAN_BLUR_SHADER.update();
        RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_BLUR_RUNNABLES);
        // bloom
        RiseShaders.POST_BLOOM_SHADER.update();
        RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_POST_BLOOM_RUNNABLES);

        GameInstance.clearRunnables();

        RoundedUtil.drawRound(width / 2f - 225, 10, 450, height - 15, 4, new Color(30, 30, 30, 160));
        GameInstance.NORMAL_BLUR_RUNNABLES.add(() -> RoundedUtil.drawRound(width / 2f - 225, 10, 450, height - 15, 4, Color.BLACK));

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        RenderUtil.rect(width / 2f - 225, 30, 450, 0.5, new Color(220, 220, 220, 240));

        GameInstance.regular24Bold.drawCenteredString("赞助", width / 2f, 16, new Color(220, 220, 220, 240).getRGB());

        GameInstance.regular20.drawString("你也有自己的生活不是吗?\n吃饭，出行，玩乐...你的钱可以花在别的事上。\n我们只是路人，将来还会相见吗？\n或许你不会再打开这个客户端，或许你会继续使用Lunar或者别的...\n做点更有意义的事吧。\n", width / 2f - 220, 50, new Color(220, 220, 220, 240).getRGB());

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
    }
}
