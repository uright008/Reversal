/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
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
import cn.stars.reversal.util.render.RenderUtils;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.util.render.UIUtil;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

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
        super("Announcement", "announcement", "f");
    }

    @Override
    public void drawIcon(int posX, int posY, int color) {
        atomic24.drawString(icon, posX + 0.5, posY + 0.5, color);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        ModuleInstance.getPostProcessing().drawElementWithBloom(() -> {
            RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, Color.BLACK);
        }, 2, 2);

        RoundedUtil.drawRound(50, 65, width - 100, height - 85, 3, new Color(20, 20, 20, 160));

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        if (!announcements.isEmpty()) {
            Announcement announcement = announcements.get(announcementIndex);
            hoverAnimation.run(RenderUtil.isHovered(anPosX, anPosY, announcement.maxWidth + 20, 35 + announcement.content.size() * 10, mouseX, mouseY) ? 150 : 100);

            psm24.drawCenteredString("[" + announcement.id + "] - " + announcement.getDate(), width / 2f, 73, Color.WHITE.getRGB());

            ModuleInstance.getPostProcessing().drawElementWithBloom(() -> RoundedUtil.drawRound(anPosX, anPosY, announcement.maxWidth + 20, 35 + announcement.content.size() * 10, 3, Color.BLACK), 2, 2);

            ModuleInstance.getPostProcessing().drawElementWithBlur(() -> RoundedUtil.drawRound(anPosX, anPosY, announcement.maxWidth + 20, 35 + announcement.content.size() * 10, 3, Color.BLACK), 2, 2);

            RoundedUtil.drawRound(anPosX, anPosY, announcement.maxWidth + 20, 35 + announcement.content.size() * 10, 3, new Color(30,30,30, (int) hoverAnimation.getValue()));

            atomic24.drawString("1", anPosX + 5, anPosY + 10, Color.WHITE.getRGB());
            psm24.drawString(announcement.title, anPosX + 20, anPosY + 9, Color.WHITE.getRGB());
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
        this.previousButton = new TextButton(width / 2f - 100, 68, 20, 20, () -> updateAnnouncementIndex(announcementIndex - 1), "←", "", true, 0, 4, 4);
        this.nextButton = new TextButton(width / 2f + 80, 68, 20, 20, () -> updateAnnouncementIndex(announcementIndex + 1), "→", "", true, 0, 4, 4);
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
        Announcement announcement1 = new Announcement("Reversal Announcement", "2025/3/26", 0)
                .addContent("你正在使用" + Reversal.NAME + " " + Reversal.VERSION + " (Minecraft " + Reversal.MINECRAFT_VERSION + ").")
                .addContent("本客户端由" + Reversal.AUTHOR + "制作,由Aerolite Society强力驱动.")
                .addContent("感谢您对Reversal的支持!")
                .addContent("")
                .addContent("[Github] https://www.github.com/RinoRika/Reversal")
                .addContent("[QQ] https://qm.qq.com/q/M6dDLtw3oS")
                .addContent("[BiliBili] https://space.bilibili.com/670866766")
                .addContent("")
                .addContent("© 2025 Aerolite Society. 保留部分权利.")
                .calcMaxWidth();
        Announcement announcement2 = new Announcement("Reversal 2.1.2", "2025/4/18", 1)
                .addContent("[+] 主题系统配色 新增更改Alpha值")
                .addContent("[+] 新增随机标题")
                .addContent("[/] 优化主题系统")
                .addContent("[*] 修复网易云音乐API")
                .addContent("[*] 修复输入框粘贴崩溃")
                .addContent("[*] 修复计分板字体位置异常")
                .addContent("[*] (修复Kotlin导致的构建错误)")
                .calcMaxWidth();
        Announcement announcement3 = new Announcement("一些重要的事情...", "2025/4/20", 2)
                .addContent("大家好,我是Stars,客户端的主要开发者.")
                .addContent("非常遗憾告诉大家,从今天起,Reversal客户端将暂停更新一段时间,直到大约暑假开始.")
                .addContent("")
                .addContent("这个客户端是我在探索MCP时的第一个练手项目(StarX),在我于B站发布了一个视频以后得到了许多人的支持.这一次偶然的机会,我决定投入心血到这个客户端中.")
                .addContent("在开发过程中,确实遇到了许多困难.由于在项目初期我了解的知识并不多,因此我使用了Rise5的部分代码作为底层(如ModuleManager).")
                .addContent("与某些人口中的'Rise5 base'不同,这个项目由1.8.9原版代码写起,而不是Rise5删除了黑客功能,因此我们可以保证该客户端完全合法.")
                .addContent("虽然在宣传上略有成效,然而客户端的使用情况并不理想.因此我也在努力编写新的功能,听取大家的建议,希望能给大家更好的体验,让更多人使用这个客户端.")
                .addContent("当初打着'黑客视觉PVP端'的旗号,我们在此基础上不断发展,逐渐将客户端改造成了美观性和实用性兼具的一款适合玩家日常使用的PVP客户端.")
                .addContent("一年时光,说长不长,说短不短.在此期间,客户端的开发过程十分坎坷.期间遭到了许多人的诋毁谩骂,将我贬成'抄袭狗'.")
                .addContent("不过我也认识了许多新的朋友,我们一起陪伴度过了许多难忘的时光.非常感谢你们,对我的开发工作一直保持支持.")
                .addContent("")
                .addContent("我本人在2023年由于多方面原因患上心理疾病,并在步入高中后由于忽视而严重恶化.现在患有重度抑郁,重度焦虑,躯体化,双相等疾病.")
                .addContent("关系的稀缺,父母的不理解,学业的压迫,无端的谩骂......我被折磨得痛不欲生.")
                .addContent("我曾3次尝试过自杀:跳楼,割腕,吞药.世界一次又一次地把我推向深渊,又一次又一次地把我带回来.为什么要这么熬我呢?")
                .addContent("客户端自开始以来,长时间都是只有我一个开发者,基本上所有东西都是我在搞,只有我的几个朋友提供过一些帮助.")
                .addContent("再加上客户端使用效果不理想,甚至还招骂,我已经失去了创作热情.")
                .addContent("沉重的生活,我再也无法继续忍受.我想,应该歇一下了.")
                .addContent("")
                .addContent("这个项目并没有停止更新,而只是暂时停止开发新的功能.作为开发者也是Aerolite Society的群主,我仍会与大家保持日常沟通,异常bug的修复也将继续进行.")
                .addContent("恳请大家给我一段休息的时间,在学业暂时结束后,我会重新投入开发.对不起!")
                .addContent("暂停时间大约持续两个月,期间仍会进行小型修复类更新,宣传工作也将继续.")
                .addContent("非常感谢屏幕前的你,对本人开发工作的支持,对本客户端的支持,对本人的支持.")
                .addContent("")
                .addContent("")
                .addContent("")
                .addContent("本客户端由于一直保持免费开源状态,因此并没有渠道盈利.并且我们还需开设IRC服务器和维护一些相关内容,可以说是亏损的生意.")
                .addContent("项目的进行与否,其实完全都看大家的使用状况.有好几次因为没人用,我差点停止更新.")
                .addContent("在2.0.0版本后,我们开始接受赞助.至今已经得到了约1000元的,来自各个网友的赞赏.非常感谢你们,给了我们继续开发下去的动力.")
                .addContent("然而或许你曾发现,我总是在请求赞助后不久就又一贫如洗.难道是我挥金如土?")
                .addContent("事实上,在这笔费用中,我将大部分的收入都捐赠给了公益项目...")
                .addContent("")
                .addContent("从2025年至今,本人共计在帮助贫困地区学生的公益项目中捐赠200元,在帮助抑郁症等心理疾病的公益项目中捐赠500元.")
                .addContent("写下公告的今天,我又在BiliBili的抑郁症心理咨询陪伴项目中捐赠200元.")
                .addContent("Stars是我从接触互联网开始,从未修改过一次的网名.我从小向往太空,我希望做一个活泼开朗的人,可惜我最终活成了自己讨厌的模样.")
                .addContent("医者可以自医吗?我不知道.我只希望每个陷入困境中的孩子,每当你们仰望星空的时候,都能看到,我还在天空闪耀,你的前路一片光明.")
                .addContent("What I've called: Stars.")
                .addContent("")
                .addContent("")
                .addContent("")
                .addContent("以上仅是简述我个人的想法和经历. 我没有任何别的想法!!! 特么的别以为我要死了.(流汗)")
                .addContent("任何客户端问题,或者想聊天的话,随时可以在群里找我.")
                .addContent("如果有异常问题,或者新功能建议,请前往该链接进行报告: https://github.com/RinoRika/Reversal/issues")
                .calcMaxWidth();
        announcements.clear();
        announcements.add(announcement1);
        announcements.add(announcement2);
        announcements.add(announcement3);

        updateAnnouncementIndex(announcementIndex);
    }
}
