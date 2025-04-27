package cn.stars.reversal.ui.atmoic.mainmenu.impl.misc;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.MiscGui;
import cn.stars.reversal.ui.atmoic.misc.component.TextButton;
import cn.stars.reversal.ui.atmoic.misc.component.TextField;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import net.minecraft.client.multiplayer.ServerData;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class DirectConnectGui extends MiscGui {
    private final ServerData field_146301_f;
    private TextField field_146302_g;
    private TextButton selectButton, cancelButton;
    private TextButton[] buttons;

    public DirectConnectGui(ServerData p_i1031_2_)
    {
        super("direct connection");
        this.field_146301_f = p_i1031_2_;
    }

    public void initGui()
    {
        super.initGui();
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        selectButton = new TextButton(this.width / 2f - 100, this.height / 2f, 200, 20, () -> {
            if (!this.field_146302_g.getText().isEmpty()) {
                this.field_146301_f.serverIP = this.field_146302_g.getText();
                AtomicMenu.atomicGuis.get(2).confirmClicked(true, 0);
            }
        }, "连接服务器", "", true, 1, 75, 5, 20);
        cancelButton = new TextButton(this.width / 2f - 100, this.height / 2f + 24, 200, 20, () -> AtomicMenu.atomicGuis.get(2).confirmClicked(false, 0), "取消", "", true, 1, 90, 5, 20);
        this.field_146302_g = new TextField(200, 20, GameInstance.regular16, new Color(30, 30, 30, 100), new Color(30,30,30,120));
        this.field_146302_g.setSelectedLine(true);
        this.field_146302_g.setFocused(true);
        this.field_146302_g.setText(mc.gameSettings.lastServer);
        buttons = new TextButton[] {selectButton, cancelButton};
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        mc.gameSettings.lastServer = this.field_146302_g.getText();
        mc.gameSettings.saveOptions();
    }

    public void keyTyped(char typedChar, int keyCode)
    {
        this.field_146302_g.keyTyped(typedChar, keyCode);
        if (keyCode == 28 || keyCode == 156)
        {
            selectButton.runAction();
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
        this.field_146302_g.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        this.field_146302_g.mouseDragged(mouseX, mouseY, clickedMouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(width / 2f - 200, height / 2f - 50, 400, 100, 4, Color.BLACK);
        }, 2, 2);

        RoundedUtil.drawRound(width / 2f - 200, height / 2f - 50, 400, 100, 4, new Color(20, 20, 20, 160));
        
        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        RoundedUtil.drawRound(this.width / 2f - 98, height / 2f - 40,3,3,1f, Color.WHITE);
        regular20.drawString("输入服务器IP", this.width / 2f - 90, height / 2f - 42, Color.WHITE.getRGB());

        this.field_146302_g.draw(this.width / 2f - 100, height / 2f - 30, mouseX, mouseY);
    }
}
