package net.minecraft.client.gui;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.ui.atmoic.misc.component.TextButton;
import cn.stars.reversal.ui.atmoic.misc.component.TextField;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

import static cn.stars.reversal.GameInstance.*;

public class GuiScreenAddServer extends GuiScreen
{
    private final GuiScreen parentScreen;
    private final ServerData serverData;
    private TextField serverIPField;
    private TextField serverNameField;
    TextButton addButton, cancelButton;
    private TextButton[] buttons;

    public GuiScreenAddServer(GuiScreen p_i1033_1_, ServerData p_i1033_2_)
    {
        this.parentScreen = p_i1033_1_;
        this.serverData = p_i1033_2_;
    }

    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        addButton = new TextButton(this.width / 2 - 100, this.height / 4 + 96 + 68, 200, 20, () -> {
            this.serverData.serverName = this.serverNameField.getText();
            this.serverData.serverIP = this.serverIPField.getText();
            this.parentScreen.confirmClicked(true, 0);
        }, "添加服务器", "", true, 1, 75, 5, 20);
        cancelButton = new TextButton(this.width / 2 - 100, this.height / 4 + 120 + 68, 200, 20, () -> {
            this.parentScreen.confirmClicked(false, 0);
        }, "取消", "", true, 1, 90, 5, 20);
        this.serverNameField = new TextField(200, 20, GameInstance.regular16, new Color(30, 30, 30, 100), new Color(30,30,30,120));
        this.serverNameField.setSelectedLine(true);
        this.serverNameField.setFocused(true);
        this.serverNameField.setText(this.serverData.serverName);
        this.serverIPField = new TextField(200, 20, GameInstance.regular16, new Color(30, 30, 30, 100), new Color(30,30,30,120));
        this.serverIPField.setSelectedLine(true);
        this.serverIPField.setText(this.serverData.serverIP);
        buttons = new TextButton[] {addButton, cancelButton};
    }

    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        this.serverNameField.keyTyped(typedChar, keyCode);
        this.serverIPField.keyTyped(typedChar, keyCode);

        if (keyCode == 15)
        {
            this.serverNameField.setFocused(!this.serverNameField.isFocused());
            this.serverIPField.setFocused(!this.serverIPField.isFocused());
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
        this.serverIPField.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverNameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.serverIPField.mouseDragged(mouseX, mouseY, clickedMouseButton);
        this.serverNameField.mouseDragged(mouseX, mouseY, clickedMouseButton);
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

        GameInstance.regular24Bold.drawCenteredString("添加服务器", width / 2f, 157, new Color(220, 220, 220, 240).getRGB());
        GameInstance.regular20.drawString("输入服务器名称", this.width / 2f - 100, 193, new Color(220, 220, 220, 240).getRGB());
        GameInstance.regular20.drawString("输入服务器IP", this.width / 2f - 100, 234, new Color(220, 220, 220, 240).getRGB());

        this.serverNameField.draw(this.width / 2f - 100, 206, mouseX, mouseY);
        this.serverIPField.draw(this.width / 2f - 100, 246, mouseX, mouseY);

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
