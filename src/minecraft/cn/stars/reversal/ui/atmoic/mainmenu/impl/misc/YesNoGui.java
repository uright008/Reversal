package cn.stars.reversal.ui.atmoic.mainmenu.impl.misc;

import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.impl.MiscGui;
import cn.stars.reversal.ui.atmoic.misc.component.TextButton;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import com.google.common.collect.Lists;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class YesNoGui extends MiscGui {
    private final int guiId;
    private final int targetId;
    protected String messageLine1;
    private List<String> messages = Lists.newArrayList();
    private TextButton yesButton, noButton;
    private TextButton[] buttons;

    public YesNoGui(int gui, int targetId, String msg1, String msg2)
    {
        super("confirm");
        this.guiId = gui;
        this.targetId = targetId;
        this.messageLine1 = msg1;

        this.messages.clear();
        this.messages = Arrays.asList(regular16.autoReturn(msg2, 350, 100).split("\n"));
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.yesButton =
                new TextButton(this.width / 2f - 100, this.height / 2f + messages.size() * 15 / 2f - 10, 200, 20, () -> {
                    AtomicMenu.switchGui(guiId);
                    AtomicMenu.atomicGuis.get(guiId).confirmClicked(true, targetId);
                    AtomicMenu.setMiscGui(new MiscGui());
                }, "确定", "", true, 1, 90, 5, 20);
        this.noButton =
                new TextButton(this.width / 2f - 100, this.height / 2f + messages.size() * 15 / 2f + 14, 200, 20, () -> {
                    AtomicMenu.switchGui(guiId);
                    AtomicMenu.atomicGuis.get(guiId).confirmClicked(false, targetId);
                    AtomicMenu.setMiscGui(new MiscGui());
                }, "取消", "", true, 1, 90, 5, 20);
        this.buttons = new TextButton[]{yesButton, noButton};
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(width / 2f - 200, height / 2f - messages.size() * 15 / 2f - 40, 400, messages.size() * 15 + 80, 4, Color.BLACK);
        }, 2, 2);

        RoundedUtil.drawRound(width / 2f - 200, height / 2f - messages.size() * 15 / 2f - 40, 400, messages.size() * 15 + 80, 4, new Color(20, 20, 20, 160));

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        regular20.drawCenteredString(this.messageLine1, this.width / 2f, height / 2f - messages.size() * 15 / 2f - 35, Color.WHITE.getRGB());
        float i = height / 2f - messages.size() * 15 / 2f - 15;

        for (String s : this.messages)
        {
            regular16.drawCenteredString(s, this.width / 2f, i, Color.WHITE.getRGB());
            i += regular16.height();
        }
    }
}
