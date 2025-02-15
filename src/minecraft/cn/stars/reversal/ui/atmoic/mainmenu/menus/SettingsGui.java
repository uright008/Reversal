package cn.stars.reversal.ui.atmoic.mainmenu.menus;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.gui.GuiReversalSettings;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.render.UIUtil;
import com.google.common.collect.Lists;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.EnumDifficulty;

import java.awt.*;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.List;

public class SettingsGui extends AtomicGui {
    private final MFont upperIcon = FontManager.getAtomic(24);
    private static final GameSettings.Options[] field_146440_f = new GameSettings.Options[] {GameSettings.Options.FOV};
    private GuiButton field_175357_i;
    private GuiLockIconButton field_175356_r;
    protected String field_146442_a = "Options";
    private TextButton reversalSettings;

    public SettingsGui() {
        super("Settings", "d");
    }

    @Override
    public void drawIcon(int posX, int posY) {
        upperIcon.drawString(icon, posX + 1.5, posY + 1, Color.WHITE.getRGB());
    }

    @Override
    public void onGuiClosed() {
        mc.gameSettings.saveOptions();
    }

    @Override
    public void initGui()
    {
        super.initGui();
        reversalSettings = new TextButton(10, height - 40, 120, 35, () -> mc.displayGuiScreen(new GuiReversalSettings(Reversal.atomicMenu)),
                "Reversal设置", "e", true, 12, 30, 11);
        int i = 0;
        this.field_146442_a = I18n.format("options.title");

        for (GameSettings.Options gamesettings$options : field_146440_f)
        {
            if (gamesettings$options.getEnumFloat())
            {
                this.buttonList.add(new GuiOptionSlider(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options));
            }
            else
            {
                GuiOptionButton guioptionbutton = new GuiOptionButton(gamesettings$options.returnEnumOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options, mc.gameSettings.getKeyBinding(gamesettings$options));
                this.buttonList.add(guioptionbutton);
            }

            ++i;
        }

        if (mc.theWorld != null)
        {
            EnumDifficulty enumdifficulty = mc.theWorld.getDifficulty();
            this.field_175357_i = new GuiButton(108, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.func_175355_a(enumdifficulty));
            this.buttonList.add(this.field_175357_i);

            if (mc.isSingleplayer() && !mc.theWorld.getWorldInfo().isHardcoreModeEnabled())
            {
                this.field_175357_i.setWidth(this.field_175357_i.getButtonWidth() - 20);
                this.field_175356_r = new GuiLockIconButton(109, this.field_175357_i.xPosition + this.field_175357_i.getButtonWidth(), this.field_175357_i.yPosition);
                this.buttonList.add(this.field_175356_r);
                this.field_175356_r.func_175229_b(mc.theWorld.getWorldInfo().isDifficultyLocked());
                this.field_175356_r.enabled = !this.field_175356_r.func_175230_c();
                this.field_175357_i.enabled = !this.field_175356_r.func_175230_c();
            }
            else
            {
                this.field_175357_i.enabled = false;
            }
        }

        this.buttonList.add(new GuiButton(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation")));
        this.buttonList.add(new GuiButton(8675309, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, "Super Secret Settings...")
        {
            public void playPressSound(SoundHandler soundHandlerIn)
            {
                SoundEventAccessorComposite soundeventaccessorcomposite = soundHandlerIn.getRandomSoundFromCategories(SoundCategory.ANIMALS, SoundCategory.BLOCKS, SoundCategory.MOBS, SoundCategory.PLAYERS, SoundCategory.WEATHER);

                if (soundeventaccessorcomposite != null)
                {
                    soundHandlerIn.playSound(PositionedSoundRecord.create(soundeventaccessorcomposite.getSoundEventLocation(), 0.5F));
                }
            }
        });
        this.buttonList.add(new GuiButton(106, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.sounds")));
        this.buttonList.add(new GuiButton(107, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.stream")));
        this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.video")));
        this.buttonList.add(new GuiButton(100, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.controls")));
        this.buttonList.add(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.language")));
        this.buttonList.add(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.chat.title")));
        this.buttonList.add(new GuiButton(105, this.width / 2 - 155, this.height / 6 + 144 - 6, 150, 20, I18n.format("options.resourcepack")));
        this.buttonList.add(new GuiButton(104, this.width / 2 + 5, this.height / 6 + 144 - 6, 150, 20, I18n.format("options.snooper.view")));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }

    public String func_175355_a(EnumDifficulty p_175355_1_)
    {
        IChatComponent ichatcomponent = new ChatComponentText("");
        ichatcomponent.appendSibling(new ChatComponentTranslation("options.difficulty"));
        ichatcomponent.appendText(": ");
        ichatcomponent.appendSibling(new ChatComponentTranslation(p_175355_1_.getDifficultyResourceKey()));
        return ichatcomponent.getFormattedText();
    }

    public void confirmClicked(boolean result, int id)
    {
        mc.displayGuiScreen(Reversal.atomicMenu);

        if (id == 109 && result && mc.theWorld != null)
        {
            mc.theWorld.getWorldInfo().setDifficultyLocked(true);
            this.field_175356_r.func_175229_b(true);
            this.field_175356_r.enabled = false;
            this.field_175357_i.enabled = false;
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0)
        {
            try {
                for (GuiButton guibutton : this.buttonList) {
                    if (guibutton.mousePressed(mc, mouseX, mouseY)) {
                        guibutton.playPressSound(mc.getSoundHandler());
                        this.actionPerformed(guibutton);
                    }
                }
            } catch (ConcurrentModificationException ignored) {}
        }
        if (mc.theWorld == null) UIUtil.onButtonClick(new TextButton[] {reversalSettings}, mouseX, mouseY, mouseButton);
    }

    @Override
    public void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id < 100 && button instanceof GuiOptionButton)
            {
                GameSettings.Options gamesettings$options = ((GuiOptionButton)button).returnEnumOptions();
                mc.gameSettings.setOptionValue(gamesettings$options, 1);
                button.displayString = mc.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(button.id));
            }

            if (button.id == 108)
            {
                mc.theWorld.getWorldInfo().setDifficulty(EnumDifficulty.getDifficultyEnum(mc.theWorld.getDifficulty().getDifficultyId() + 1));
                this.field_175357_i.displayString = this.func_175355_a(mc.theWorld.getDifficulty());
            }

            if (button.id == 109)
            {
                mc.displayGuiScreen(new GuiYesNo(Reversal.atomicMenu, (new ChatComponentTranslation("difficulty.lock.title")).getFormattedText(), (new ChatComponentTranslation("difficulty.lock.question", new ChatComponentTranslation(mc.theWorld.getWorldInfo().getDifficulty().getDifficultyResourceKey(), new Object[0]))).getFormattedText(), 109));
            }

            if (button.id == 110)
            {
                mc.gameSettings.saveOptions();
                mc.displayGuiScreen(new GuiCustomizeSkin(Reversal.atomicMenu));
            }

            if (button.id == 8675309)
            {
                mc.entityRenderer.activateNextShader();
            }

            if (button.id == 101)
            {
                mc.gameSettings.saveOptions();
                mc.displayGuiScreen(new GuiVideoSettings(Reversal.atomicMenu, mc.gameSettings));
            }

            if (button.id == 100)
            {
                mc.gameSettings.saveOptions();
                mc.displayGuiScreen(new GuiControls(Reversal.atomicMenu, mc.gameSettings));
            }

            if (button.id == 102)
            {
                mc.gameSettings.saveOptions();
                mc.displayGuiScreen(new GuiLanguage(Reversal.atomicMenu, mc.gameSettings, mc.getLanguageManager()));
            }

            if (button.id == 103)
            {
                mc.gameSettings.saveOptions();
                mc.displayGuiScreen(new ScreenChatOptions(Reversal.atomicMenu, mc.gameSettings));
            }

            if (button.id == 104)
            {
                mc.gameSettings.saveOptions();
                mc.displayGuiScreen(new GuiSnooper(Reversal.atomicMenu, mc.gameSettings));
            }

            if (button.id == 200)
            {
                mc.gameSettings.saveOptions();
                AtomicMenu.switchGui(0);
                mc.displayGuiScreen(Reversal.atomicMenu);
            }

            if (button.id == 105)
            {
                mc.gameSettings.saveOptions();
                mc.displayGuiScreen(new GuiScreenResourcePacks(Reversal.atomicMenu));
            }

            if (button.id == 106)
            {
                mc.gameSettings.saveOptions();
                mc.displayGuiScreen(new GuiScreenOptionsSounds(Reversal.atomicMenu, mc.gameSettings));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {

        if (mc.theWorld == null) {
            reversalSettings.draw(mouseX, mouseY, partialTicks);
        }

        mc.fontRendererObj.drawCenteredString(this.field_146442_a, this.width / 2f, 15, 16777215);

        for (GuiButton guiButton : this.buttonList) {
            guiButton.drawButton(mc, mouseX, mouseY);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
