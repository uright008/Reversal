package cn.stars.reversal.ui.atmoic.mainmenu.menus;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.impl.client.PostProcessing;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicGui;
import cn.stars.reversal.ui.atmoic.mainmenu.AtomicMenu;
import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.*;
import lombok.SneakyThrows;

import java.awt.*;

public class MainGui extends AtomicGui {
    private final MFont upperIcon = FontManager.getAtomic(32);
    private final MFont atomic48 = FontManager.getAtomic(48);
    private final MFont rp32 = FontManager.getRainbowParty(32);
    private final Animation singlePlayerAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation multiPlayerAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation settingsAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation exitAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    public MainGui() {
        super("Main", "a");
    }

    @Override
    public void drawIcon(int posX, int posY) {
        upperIcon.drawString(icon, posX, posY - 1, Color.WHITE.getRGB());
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtil.isHovered(width / 2f - 450, height / 2f - 75, 150, 150, mouseX, mouseY)) {
            AtomicMenu.switchGui(1);
        } else if (RenderUtil.isHovered(width / 2f - 300, height / 2f - 75, 150, 150, mouseX, mouseY)) {
            AtomicMenu.switchGui(2);
        } else if (RenderUtil.isHovered(width / 2f + 150, height / 2f - 75, 150, 150, mouseX, mouseY)) {
            AtomicMenu.switchGui(3);
        } else if (RenderUtil.isHovered(width / 2f + 300, height / 2f - 75, 150, 150, mouseX, mouseY)) {
            mc.shutdown();
        }
    }

    @SneakyThrows
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderUtil.rect(0, height / 2f - 75, width, 150, new Color(50,50,50,240));

        singlePlayerAnimation.run(RenderUtil.isHovered(width / 2f - 450, height / 2f - 75, 150, 150, mouseX, mouseY) ? 50 : 0);
        multiPlayerAnimation.run(RenderUtil.isHovered(width / 2f - 300, height / 2f - 75, 150, 150, mouseX, mouseY) ? 50 : 0);
        settingsAnimation.run(RenderUtil.isHovered(width / 2f + 150, height / 2f - 75, 150, 150, mouseX, mouseY) ? 50 : 0);
        exitAnimation.run(RenderUtil.isHovered(width / 2f + 300, height / 2f - 75, 150, 150, mouseX, mouseY) ? 50 : 0);

        RenderUtil.drawParallelogram(width / 2f - 450, height / 2f - 75, 150, 150, -15, true, new Color(100 + (int)singlePlayerAnimation.getValue(),100 + (int)singlePlayerAnimation.getValue(),200 + (int)singlePlayerAnimation.getValue(),220));
        RenderUtil.drawParallelogram(width / 2f - 300, height / 2f - 75, 150, 150, -15, true, new Color(200 + (int)multiPlayerAnimation.getValue(),200 + (int)multiPlayerAnimation.getValue(),50 + (int)multiPlayerAnimation.getValue(),220));
        RenderUtil.drawParallelogram(width / 2f + 150, height / 2f - 75, 150, 150, -15, true, new Color(100 + (int)settingsAnimation.getValue(),200 + (int)settingsAnimation.getValue(),50 + (int)settingsAnimation.getValue(),220));
        RenderUtil.drawParallelogram(width / 2f + 300, height / 2f - 75, 150, 150, -15, true, new Color(100 + (int)exitAnimation.getValue(),100 + (int)exitAnimation.getValue(),100 + (int)exitAnimation.getValue(),220));

        atomic48.drawStringWithShadow("b", width / 2f - 396, height / 2f - 8 - singlePlayerAnimation.getValue() / 10, new Color(250,250,250,250).getRGB());
        rp32.drawStringWithShadow("single", width / 2f - 402, height / 2f + 35 - singlePlayerAnimation.getValue() / 10, new Color(250,250,250,250).getRGB());

        atomic48.drawStringWithShadow("c", width / 2f - 246, height / 2f - 8 - multiPlayerAnimation.getValue() / 10, new Color(250,250,250,250).getRGB());
        rp32.drawStringWithShadow("multi", width / 2f - 250, height / 2f + 35 - multiPlayerAnimation.getValue() / 10, new Color(250,250,250,250).getRGB());

        atomic48.drawStringWithShadow("d", width / 2f + 206, height / 2f - 8 - settingsAnimation.getValue() / 10, new Color(250,250,250,250).getRGB());
        rp32.drawStringWithShadow("settings", width / 2f + 190, height / 2f + 35 - settingsAnimation.getValue() / 10, new Color(250,250,250,250).getRGB());

        atomic48.drawStringWithShadow("e", width / 2f + 356, height / 2f - 8 - exitAnimation.getValue() / 10, new Color(250,250,250,250).getRGB());
        rp32.drawStringWithShadow("exit", width / 2f + 352, height / 2f + 35 - exitAnimation.getValue() / 10, new Color(250,250,250,250).getRGB());

        RoundedUtil.drawGradientRound(width / 2f - 25, height / 2f - 50, 50, 50, 24,
                ColorUtils.INSTANCE.interpolateColorsBackAndForth(8, 1000, new Color(100,200,255, 255), ColorUtil.transparent, false),
                ColorUtils.INSTANCE.interpolateColorsBackAndForth(8, 2000, new Color(100,200,255, 255), ColorUtil.transparent, false),
                ColorUtils.INSTANCE.interpolateColorsBackAndForth(8, 4000, new Color(100,200,255, 255), ColorUtil.transparent, false),
                ColorUtils.INSTANCE.interpolateColorsBackAndForth(8, 3000, new Color(100,200,255, 255), ColorUtil.transparent, false));
        RoundedUtil.drawRound(width / 2f - 24, height / 2f - 49, 48, 48, 23, new Color(100,200,255, 100));
        FontManager.getAtomic(64).drawString("2", width / 2f - 16, height / 2f - 36, new Color(250,250,250, 220).getRGB());
        FontManager.getRainbowParty(64).drawCenteredString("Reversal", width / 2f, height / 2f + 15, new Color(250,250,250,250).getRGB());
        FontManager.getRainbowParty(24).drawCenteredString(Reversal.VERSION, width / 2f, height / 2f + 50, new Color(250,250,250,250).getRGB());
        ModuleInstance.getModule(PostProcessing.class).drawElementWithBloom(() -> {
            FontManager.getAtomic(64).drawString("2", width / 2f - 16, height / 2f - 36, Color.WHITE.getRGB());
            FontManager.getRainbowParty(64).drawCenteredString("Reversal", width / 2f, height / 2f + 15, Color.WHITE.getRGB());
        }, 2, 2);

        psm18.drawString("Reversal " + Reversal.VERSION, 2, height - 30, new Color(250,250,250,250).getRGB());
        psm18.drawString("Minecraft " + Reversal.MINECRAFT_VERSION, 2, height - 20, new Color(250,250,250,250).getRGB());
        psm18.drawString("OptiFine_1.8.9_HD_U_M6_pre2", 2, height - 10, new Color(250,250,250,250).getRGB());

        String license = "© 2025 Aerolite Foundation. 保留部分权利.";
        psm18.drawString(license, width - psm18.width(license) + 5, height - 10, new Color(250,250,250,250).getRGB());
    }
}
