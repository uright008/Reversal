package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.util.Transformer;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.render.UIUtil;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

import static cn.stars.reversal.GameInstance.*;

public class GuiGameOver extends GuiScreen implements GuiYesNoCallback
{
    private TextButton firstButton, secondButton;
    private TextButton[] buttons;

    public void initGui()
    {
        if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
        {
            if (this.mc.isIntegratedServerRunning())
            {
                firstButton = new TextButton(this.width / 2 - 100, this.height / 4 + 96, 200, 20, () -> {
                    if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
                    {
                        this.mc.displayGuiScreen(Transformer.transformMainMenu());
                    }
                    else
                    {
                        GuiYesNo guiyesno = new GuiYesNo(this, I18n.format("deathScreen.quit.confirm", new Object[0]), "", I18n.format("deathScreen.titleScreen", new Object[0]), I18n.format("deathScreen.respawn", new Object[0]), 0);
                        this.mc.displayGuiScreen(guiyesno);
                        guiyesno.setButtonDelay(20);
                    }
                }, "删除世界", "", true, 1, 80, 5, 20);
            }
            else
            {
                firstButton = new TextButton(this.width / 2 - 100, this.height / 4 + 96, 200, 20, () -> {
                    if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
                    {
                        this.mc.displayGuiScreen(Transformer.transformMainMenu());
                    }
                    else
                    {
                        GuiYesNo guiyesno = new GuiYesNo(this, I18n.format("deathScreen.quit.confirm", new Object[0]), "", I18n.format("deathScreen.titleScreen", new Object[0]), I18n.format("deathScreen.respawn", new Object[0]), 0);
                        this.mc.displayGuiScreen(guiyesno);
                        guiyesno.setButtonDelay(20);
                    }
                }, "离开服务器", "", true, 1, 75, 5, 20);
            }
            buttons = new TextButton[] { firstButton };
        }
        else
        {
            firstButton = new TextButton(this.width / 2 - 100, this.height / 4 + 72, 200, 20, () -> {
                this.mc.thePlayer.respawnPlayer();
                this.mc.displayGuiScreen(null);
            }, "重生", "", true, 1, 90, 5, 20);
            secondButton = new TextButton(this.width / 2 - 100, this.height / 4 + 96, 200, 20, () -> {
                if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
                {
                    this.mc.displayGuiScreen(Transformer.transformMainMenu());
                }
                else
                {
                    GuiYesNo guiyesno = new GuiYesNo(this, I18n.format("deathScreen.quit.confirm", new Object[0]), "", I18n.format("deathScreen.titleScreen", new Object[0]), I18n.format("deathScreen.respawn", new Object[0]), 0);
                    this.mc.displayGuiScreen(guiyesno);
                    guiyesno.setButtonDelay(20);
                }
            }, "主菜单", "", true, 1, 85, 5, 20);
            buttons = new TextButton[] { firstButton, secondButton };
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        switch (button.id)
        {
            case 0:
                this.mc.thePlayer.respawnPlayer();
                this.mc.displayGuiScreen((GuiScreen)null);
                break;

            case 1:
                if (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
                {
                    this.mc.displayGuiScreen(Transformer.transformMainMenu());
                }
                else
                {
                    GuiYesNo guiyesno = new GuiYesNo(this, I18n.format("deathScreen.quit.confirm", new Object[0]), "", I18n.format("deathScreen.titleScreen", new Object[0]), I18n.format("deathScreen.respawn", new Object[0]), 0);
                    this.mc.displayGuiScreen(guiyesno);
                    guiyesno.setButtonDelay(20);
                }
        }
    }

    public void confirmClicked(boolean result, int id)
    {
        if (result)
        {
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
            this.mc.displayGuiScreen(Transformer.transformMainMenu());
        }
        else
        {
            this.mc.thePlayer.respawnPlayer();
            this.mc.displayGuiScreen(null);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
        // blur
        RiseShaders.GAUSSIAN_BLUR_SHADER.update();
        RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_BLUR_RUNNABLES);
        // bloom
        RiseShaders.POST_BLOOM_SHADER.update();
        RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_POST_BLOOM_RUNNABLES);

        GameInstance.clearRunnables();

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        FontManager.getRegularBold(32).drawCenteredString("你死了！", this.width / 2, 60, new Color(220, 220, 220, 240).getRGB());

        regular20.drawCenteredString("分数: " + EnumChatFormatting.YELLOW + this.mc.thePlayer.getScore(), this.width / 2, 100, new Color(220, 220, 220, 240).getRGB());

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public void updateScreen()
    {
        super.updateScreen();
    }
}
