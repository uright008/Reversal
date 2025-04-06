package cn.stars.reversal.ui.atmoic.mainmenu.impl.misc;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.music.ui.ThemeColor;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.MiscGui;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.ui.modern.TextField;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RenderUtils;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class AddServerGui extends MiscGui {
    private final ServerData serverData;
    private TextField serverIPField;
    private TextField serverNameField;
    TextButton addButton, cancelButton;
    private TextButton[] buttons;

    public AddServerGui(ServerData p_i1033_2_)
    {
        super("add server");
        this.serverData = p_i1033_2_;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        addButton = new TextButton(this.width / 2f - 100, this.height / 2f + 26, 200, 20, () -> {
            this.serverData.serverName = this.serverNameField.getText();
            this.serverData.serverIP = this.serverIPField.getText();
            AtomicMenu.atomicGuis.get(2).confirmClicked(true, 0);
            AtomicMenu.switchGui(2);
        }, "添加服务器", "", true, 1, 75, 5, 20);
        cancelButton = new TextButton(this.width / 2f - 100, this.height / 2f + 50, 200, 20, () -> {
            AtomicMenu.atomicGuis.get(2).confirmClicked(false, 0);
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

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        this.serverNameField.keyTyped(typedChar, keyCode);
        this.serverIPField.keyTyped(typedChar, keyCode);

        if (keyCode == 15)
        {
            this.serverNameField.setFocused(!this.serverNameField.isFocused());
            this.serverIPField.setFocused(!this.serverIPField.isFocused());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
        this.serverIPField.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverNameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.serverIPField.mouseDragged(mouseX, mouseY, clickedMouseButton);
        this.serverNameField.mouseDragged(mouseX, mouseY, clickedMouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(width / 2f - 200, height / 2f - 75, 400, 150, 4, Color.BLACK);
        }, 2, 2);

        RoundedUtil.drawRound(width / 2f - 200, height / 2f - 75, 400, 150, 4, new Color(20, 20, 20, 160));

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        RoundedUtil.drawRound(this.width / 2f - 98, height / 2f - 60,3,3,1f, Color.WHITE);
        RoundedUtil.drawRound(this.width / 2f - 98, height / 2f - 20,3,3,1f, Color.WHITE);
        regular20.drawString("输入服务器名称", this.width / 2f - 90, height / 2f - 62, Color.WHITE.getRGB());
        regular20.drawString("输入服务器IP", this.width / 2f - 90, height / 2f - 22, Color.WHITE.getRGB());

        this.serverNameField.draw(this.width / 2f - 100, height / 2f - 50, mouseX, mouseY);
        this.serverIPField.draw(this.width / 2f - 100, height / 2f - 10, mouseX, mouseY);
    }
}
