/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.ui.gui;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.Transformer;
import cn.stars.reversal.util.misc.ModuleInstance;
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

public class GuiPreInit extends GuiScreen {
    private TextButton acceptButton, rejectButton;
    private TextButton[] buttons;

    @Override
    public void initGui() {
        acceptButton = new TextButton(this.width / 2 - 100, this.height - 60, 200, 20, () -> {
            RainyAPI.isPreInited = true;
            ModuleInstance.getModule(PostProcessing.class).setEnabled(true);
            mc.displayGuiScreen(Transformer.transformMainMenu());
        }, "开启", "", true, 1, 90, 5, 20);
        rejectButton = new TextButton(this.width / 2 - 100, this.height - 35, 200, 20, () -> {
            RainyAPI.isPreInited = true;
            ModuleInstance.getModule(PostProcessing.class).setEnabled(false);
            mc.displayGuiScreen(Transformer.transformMainMenu());
        }, "关闭", "", true, 1, 90, 5, 20);
        buttons = new TextButton[]{acceptButton, rejectButton};
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

        GameInstance.regular24Bold.drawCenteredString("是否开启后处理？", width / 2f, 16, new Color(220, 220, 220, 240).getRGB());

        GameInstance.regular20.drawString("后处理即为所有特殊UI和GUI添加额外的美化效果， \n包括但不限于背景模糊(Blur)和发光(Bloom)。 \n开启后客户端的视觉效果将更加美丽，但这会造成严重的性能下降。 \n\n" +
                "如果电脑配置优秀，您可以选择开启这一项目。 \n在开启后，预计运行帧率将降低30~80%左右（视觉开启越多降低越大）。 \n\n在选择完后，您依旧可以通过开关客户端的\"PostProcessing\"功能来开关后处理。 \n同时，该功能内还可以单独开启美化效果并且调整参数。", width / 2f - 220, 50, new Color(220, 220, 220, 240).getRGB());

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
    }
}
