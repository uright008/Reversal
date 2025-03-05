/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025Aerolite Society, All rights reserved.
 */
package cn.stars.reversal.ui.atmoic.mainmenu.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.ui.atmoic.mainmenu.util.Announcement;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.util.ArrayList;

import static cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu.*;

public class AnnouncementGui extends AtomicGui {
    public GuiScreen parent;
    private TextButton exitButton, previousButton, nextButton;
    private TextButton[] buttons;
    private final ArrayList<Announcement> announcements = new ArrayList<>();
    private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private boolean isDragging;
    private float deltaX;
    private float deltaY;

    public AnnouncementGui() {
        super("Announcement", "f");
    }

    @Override
    public void drawIcon(int posX, int posY, int color) {
        atomic24.drawString(icon, posX + 0.5, posY + 0.5, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, Color.BLACK);

            RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
            FontManager.getRainbowParty(48).drawString("announcement", 75, 35, Color.WHITE.getRGB());
        }, 2, 2);

        RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, new Color(20, 20, 20, 160));

        RoundedUtil.drawRound(55,45,4,4,1.5f, Color.WHITE);
        FontManager.getRainbowParty(48).drawString("announcement", 75, 35, Color.WHITE.getRGB());

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        if (!announcements.isEmpty()) {
            Announcement announcement = announcements.get(announcementIndex);
            hoverAnimation.run(RenderUtil.isHovered(anPosX, anPosY, announcement.maxWidth + 20, 35 + announcement.content.size() * 10, mouseX, mouseY) ? 150 : 100);

            psm24.drawCenteredString("[" + announcement.id + "] - " + announcement.getDate(), width / 2f, 73, new Color(250,250,250,250).getRGB());

            ModuleInstance.getPostProcessing().drawElementWithBloom(() -> RoundedUtil.drawRound(anPosX, anPosY, announcement.maxWidth + 20, 35 + announcement.content.size() * 10, 3, Color.BLACK), 2, 2);

            ModuleInstance.getPostProcessing().drawElementWithBlur(() -> RoundedUtil.drawRound(anPosX, anPosY, announcement.maxWidth + 20, 35 + announcement.content.size() * 10, 3, Color.BLACK), 2, 2);

            RoundedUtil.drawRound(anPosX, anPosY, announcement.maxWidth + 20, 35 + announcement.content.size() * 10, 3, new Color(30,30,30, (int) hoverAnimation.getValue()));

            atomic24.drawString("1", anPosX + 5, anPosY + 10, new Color(250,250,250,250).getRGB());
            psm24.drawString(announcement.title, anPosX + 20, anPosY + 9, new Color(250,250,250,250).getRGB());
            for (String line : announcement.content) {
                psm18.drawString(line, anPosX + 10, anPosY + 25 + announcement.content.indexOf(line) * 10, new Color(220,220,220,240).getRGB());
            }

            psm16.drawString(announcement.date, anPosX + announcement.maxWidth - psm16.width(announcement.date) + 15, anPosY + 28 + announcement.content.size() * 10, new Color(220,220,220,240).getRGB());

            // Update Position
            if (isDragging) {
                anPosX = mouseX - deltaX;
                anPosY = mouseY - deltaY;
            }
            if (anPosX < 50) anPosX = 50;
            if (anPosX + announcement.maxWidth + 20 > width - 50) anPosX = width - announcement.maxWidth - 70;
            if (anPosY < 65) anPosY = 65;
            if (anPosY + 35 + announcement.content.size() * 10 > height - 20) anPosY = height - 55 - announcement.content.size() * 10;
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.exitButton = new TextButton(width / 2f - 60, height - 60, 120, 35, () -> AtomicMenu.switchGui(0),
                "返回主菜单", "g", true, 12, 38, 11);
        this.previousButton = new TextButton(width / 2f - 100, 68, 20, 20, () -> updateAnnouncementIndex(announcementIndex - 1), "←", "", true, 0, 4, 5);
        this.nextButton = new TextButton(width / 2f + 80, 68, 20, 20, () -> updateAnnouncementIndex(announcementIndex + 1), "→", "", true, 0, 4, 5);
        buttons = new TextButton[]{exitButton, previousButton, nextButton};

        loadAnnouncements();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        UIUtil.onButtonClick(buttons, mouseX, mouseY, mouseButton);
        if (!announcements.isEmpty()) {
            Announcement announcement = announcements.get(announcementIndex);
            if (RenderUtil.isHovered(anPosX, anPosY, announcement.maxWidth + 20, 35 + announcement.content.size() * 10, mouseX, mouseY)) {
                isDragging = true;
                deltaX = mouseX - anPosX;
                deltaY = mouseY - anPosY;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        isDragging = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    public void updateAnnouncementIndex(int index) {
        if (index < 0) {
            announcementIndex = 0;
            this.previousButton.setEnabled(false);
        } else if (index >= announcements.size()) {
            announcementIndex = announcements.size() - 1;
            this.nextButton.setEnabled(false);
        } else {
            announcementIndex = index;
            this.previousButton.setEnabled(true);
            this.nextButton.setEnabled(true);
        }
        if (index == 0) {
            this.previousButton.setEnabled(false);
        }
        if (index == announcements.size() - 1) {
            this.nextButton.setEnabled(false);
        }
    }

    private void loadAnnouncements() {
        Announcement announcement1 = new Announcement("Reversal Announcement", "2025/3/2", 0)
                .addContent("你正在使用" + Reversal.NAME + " " + Reversal.VERSION + " (Minecraft " + Reversal.MINECRAFT_VERSION + ").")
                .addContent("本客户端由" + Reversal.AUTHOR + "制作,由Aerolite Society强力驱动.")
                .addContent("感谢您对Reversal的支持!")
                .addContent("")
                .addContent("[Github] https://www.github.com/RinoRika/Reversal")
                .addContent("[QQ] https://qm.qq.com/q/M6dDLtw3oS")
                .addContent("")
                .addContent("© 2025 Aerolite Society. 保留部分权利.")
                .calcMaxWidth();
        Announcement announcement2 = new Announcement("Update Log - Reversal 2.0.0+beta.04", "2025/3/5", 1)
                .addContent("[+] 所有功能支持自定义颜色")
                .calcMaxWidth();
        Announcement announcement3 = new Announcement("Update Log - Reversal 2.0.0+beta.05", "2025/3/4", 2)
                .addContent("[+] CustomText新增Bloom效果")
                .addContent("[*] 修复音效可能无法加载的问题")
                .addContent("[*] 修复物品栏物品显示问题")
                .addContent("[*] 修复Notification后处理错位")
                .addContent("[*] 修复RealFirstPerson下蹲渲染错位")
                .addContent("[+] ClickGUI允许拖动 (突破底层代码?)")
                .addContent("[+] 新增随机标题")
                .addContent("[/] 修改部分功能的数值")
                .addContent("[/] 部分忘记加自定义颜色的功能已补全")
                .calcMaxWidth();
        announcements.clear();
        announcements.add(announcement1);
        announcements.add(announcement2);
        announcements.add(announcement3);

        updateAnnouncementIndex(announcementIndex);
    }
}
