package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

import java.io.IOException;

public class GuiSnooper extends GuiScreen
{
    private final GuiScreen field_146608_a;
    private final GameSettings game_settings_2;
    private String field_146610_i;

    public GuiSnooper(GuiScreen p_i1061_1_, GameSettings p_i1061_2_)
    {
        this.field_146608_a = p_i1061_1_;
        this.game_settings_2 = p_i1061_2_;
    }

    public void initGui()
    {
        this.field_146610_i = I18n.format("options.snooper.title", new Object[0]);
    }

    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
    }

    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
            if (button.id == 2)
            {
                this.game_settings_2.saveOptions();
                this.game_settings_2.saveOptions();
                this.mc.displayGuiScreen(this.field_146608_a);
            }

            if (button.id == 1)
            {
                this.game_settings_2.setOptionValue(GameSettings.Options.SNOOPER_ENABLED, 1);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_146610_i, this.width / 2, 8, 16777215);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
