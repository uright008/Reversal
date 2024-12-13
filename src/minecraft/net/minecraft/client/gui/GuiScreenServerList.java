package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.ui.modern.TextField;
import cn.stars.reversal.music.ui.ThemeColor;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

import static cn.stars.reversal.GameInstance.*;
import static cn.stars.reversal.GameInstance.UI_BLOOM_RUNNABLES;

public class GuiScreenServerList extends GuiScreen
{
    private final GuiScreen field_146303_a;
    private final ServerData field_146301_f;
    private TextField field_146302_g;
    private TextButton selectButton, cancelButton;
    private TextButton[] buttons;

    public GuiScreenServerList(GuiScreen p_i1031_1_, ServerData p_i1031_2_)
    {
        this.field_146303_a = p_i1031_1_;
        this.field_146301_f = p_i1031_2_;
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        selectButton = new TextButton(this.width / 2 - 100, this.height / 4 + 96 + 62, 200, 20, () -> {
            if (!this.field_146302_g.getText().isEmpty()) {
                this.field_146301_f.serverIP = this.field_146302_g.getText();
                this.field_146303_a.confirmClicked(true, 0);
            }
        }, "连接服务器", "", true, 1, 75, 5, 20);
        cancelButton = new TextButton(this.width / 2 - 100, this.height / 4 + 120 + 62, 200, 20, () -> this.field_146303_a.confirmClicked(false, 0), "取消", "", true, 1, 90, 5, 20);
        this.field_146302_g = new TextField(200, 20, GameInstance.regular16, ThemeColor.bgColor, ThemeColor.outlineColor);
        this.field_146302_g.setFocused(true);
        this.field_146302_g.setText(this.mc.gameSettings.lastServer);
        buttons = new TextButton[] {selectButton, cancelButton};
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.gameSettings.lastServer = this.field_146302_g.getText();
        this.mc.gameSettings.saveOptions();
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.field_146302_g.keyTyped(typedChar, keyCode);
        if (keyCode == 28 || keyCode == 156)
        {
            selectButton.runAction();
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
        this.field_146302_g.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.field_146302_g.mouseDragged(mouseX, mouseY, clickedMouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        // blur
        RiseShaders.GAUSSIAN_BLUR_SHADER.update();
        RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_BLUR_RUNNABLES);

        // bloom
        RiseShaders.POST_BLOOM_SHADER.update();
        RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_POST_BLOOM_RUNNABLES);

        GameInstance.clearRunnables();

        RoundedUtil.drawRound(width / 2f - 225, 150, 450, 300, 4, new Color(30, 30, 30, 160));
        GameInstance.NORMAL_BLUR_RUNNABLES.add(() -> RoundedUtil.drawRound(width / 2f - 225, 150, 450, 300, 4, Color.BLACK));
        RenderUtil.rect(width / 2f - 225, 170, 450, 0.5, new Color(220, 220, 220, 240));

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        GameInstance.regular24Bold.drawCenteredString("直接连接", width / 2f, 157, new Color(220, 220, 220, 240).getRGB());
        GameInstance.regular20.drawString("输入服务器IP", this.width / 2 - 100, 245, new Color(220, 220, 220, 240).getRGB());

        this.field_146302_g.draw(this.width / 2 - 100, 256, mouseX, mouseY);

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
