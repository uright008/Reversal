package net.minecraft.client.gui;

import java.awt.*;
import java.io.IOException;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.opengl.GL11;

public class GuiControls extends GuiScreen
{
    private static final GameSettings.Options[] optionsArr = new GameSettings.Options[] {GameSettings.Options.INVERT_MOUSE, GameSettings.Options.SENSITIVITY, GameSettings.Options.TOUCHSCREEN};
    private GuiScreen parentScreen;
    protected String screenTitle = "Controls";
    private GameSettings options;
    public KeyBinding buttonId = null;
    public long time;
    private GuiKeyBindingList keyBindingList;
    private GuiButton buttonReset;

    public GuiControls(GuiScreen screen, GameSettings settings)
    {
        this.parentScreen = screen;
        this.options = settings;
    }

    public void initGui()
    {
        this.keyBindingList = new GuiKeyBindingList(this, this.mc);
        this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done")));
        this.buttonList.add(this.buttonReset = new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("controls.resetAll")));
        this.screenTitle = I18n.format("controls.title");
        int i = 0;

        for (GameSettings.Options gamesettings$options : optionsArr)
        {
            if (gamesettings$options.getEnumFloat())
            {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 40 + 24 * (i >> 1), gamesettings$options));
            }
            else
            {
                this.buttonList.add(new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, 40 + 24 * (i >> 1), gamesettings$options, this.options.getKeyBinding(gamesettings$options)));
            }

            ++i;
        }
    }

    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.keyBindingList.handleMouseInput();
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 200)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        else if (button.id == 201)
        {
            for (KeyBinding keybinding : this.mc.gameSettings.keyBindings)
            {
                keybinding.setKeyCode(keybinding.getKeyCodeDefault());
            }

            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else if (button.id < 100 && button instanceof GuiOptionButton)
        {
            this.options.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
            button.displayString = this.options.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        if (this.buttonId != null)
        {
            this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
            this.buttonId = null;
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton))
        {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state))
        {
            super.mouseReleased(mouseX, mouseY, state);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.buttonId != null)
        {
            if (keyCode == 1)
            {
                this.options.setOptionKeyBinding(this.buttonId, 0);
            }
            else if (keyCode != 0)
            {
                this.options.setOptionKeyBinding(this.buttonId, keyCode);
            }
            else if (typedChar > 0)
            {
                this.options.setOptionKeyBinding(this.buttonId, typedChar + 256);
            }

            this.buttonId = null;
            this.time = Minecraft.getSystemTime();
            KeyBinding.resetKeyBindingArrayAndHash();
        }
        else
        {
            super.keyTyped(typedChar, keyCode);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        if (mc.theWorld == null) ModuleInstance.getPostProcessing().drawElementWithBlur(() -> RenderUtil.rect(0,0,width,height, new Color(0,0,0, 255)), 2, 2);
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> RoundedUtil.drawRound(width / 2f - 225, 10, 450, height - 15, 4, Color.BLACK));

        RoundedUtil.drawRound(width / 2f - 225, 10, 450, height - 15, 4, new Color(20, 20, 20, 160));
        RenderUtil.rect(width / 2f - 225, 30, 450, 0.5, new Color(220, 220, 220, 240));
        RenderUtil.rect(width / 2f - 225, 95, 450, 0.5, new Color(220, 220, 220, 240));

        GameInstance.regular24Bold.drawCenteredString("控制", width / 2f, 16, Color.WHITE.getRGB());

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        RenderUtil.scissor(width / 2f - 225, 100, 450, height - 145);
        this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        boolean flag = true;

        for (KeyBinding keybinding : this.options.keyBindings)
        {
            if (keybinding.getKeyCode() != keybinding.getKeyCodeDefault())
            {
                flag = false;
                break;
            }
        }

        this.buttonReset.enabled = !flag;

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
