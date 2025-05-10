package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.*;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.util.math.MathUtil;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.render.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;
import java.util.ArrayList;

@ModuleInfo(name = "SessionInfo", localizedName = "module.SessionInfo.name", description = "Show your game stats",
        localizedDescription = "module.SessionInfo.desc", category = Category.HUD)
public class SessionInfo extends Module {
    private final ModeValue mode = new ModeValue("Mode", this, "Simple", "Simple", "Modern", "ThunderHack", "Empathy", "Shader");
    public final ColorValue colorValue = new ColorValue("Color", this);
    private final TimeUtil timer = new TimeUtil();
    int second = 0;
    int minute = 0;
    int hour = 0;
    int killed = 0;
    ArrayList<EntityLivingBase> attackedEntityList = new ArrayList<>();
    ArrayList<EntityLivingBase> attackedEntityListToRemove = new ArrayList<>();
    MFont iconSmall = FontManager.getIcon(18);

    public SessionInfo() {
        setCanBeEdited(true);
        setWidth(180);
        setHeight(80);
        setX(100);
        setY(100);
    }

    @Override
    public void onShader3D(Shader3DEvent event) {
        int x = getX() + 4;
        int y = getY() + 4;
        Color color = colorValue.getColor();

        switch (mode.getMode()) {
            case "Modern":
                if (event.isBloom()) RenderUtil.roundedRectangle(x - 2, y - 4, 148, 64, roundStrength, color);
                else RenderUtil.roundedRectangle(x - 2, y - 4, 148, 64, roundStrength, Color.BLACK);
                break;
            case "ThunderHack":
                RoundedUtil.drawGradientRound(x - 3.5f, y - 5.5f, 151, 67, roundStrength,
                        ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                        ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                        ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                        ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                break;
            case "Simple":
                RenderUtil.rect(x - 2, y - 4, 148, 64, Color.BLACK);
                break;
            case "Shader":
                if (event.isBloom())
                    RenderUtil.rectForShaderTheme(x - 2, y - 4, 148, 64, colorValue, true);
                else
                    RenderUtil.roundedRectangle(x - 2, y - 4, 148, 64, roundStrength, Color.BLACK);
                break;
            case "Empathy":
                RenderUtil.roundedRectangle(x - 4, y - 4, 150, 64, 3f, ColorUtil.empathyGlowColor());
                RenderUtil.roundedRectangle(x - 4.5, y - 1.5, 1.5, regular18Bold.height() - 2.5, 3f, color);
                break;
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int x = getX() + 4;
        int y = getY() + 4;
        Color color = colorValue.getColor();

        updatePlayTime();
        String playtime = I18n.format("info.SessionInfo.playtimeValue", hour, minute, second);
        String kills = String.valueOf(killed);
        String hurtTime = String.valueOf(mc.thePlayer.hurtTime);
        String speed = String.valueOf(MathUtil.round(mc.thePlayer.getSpeed(), 1));
        String health = String.valueOf(MathUtil.round(mc.thePlayer.getHealth(), 1));

        // 背景
        switch (mode.getMode()) {
            case "Modern":
                RenderUtil.roundedRectangle(x - 2, y - 4, 148, 64, roundStrength, new Color(0, 0, 0, 80));
                RenderUtil.roundedOutlineRectangle(x - 3, y - 5, 150, 66, roundStrength, 1, color);
                break;
            case "ThunderHack":
                RoundedUtil.drawGradientRound(x - 3.5f, y - 5.5f, 151, 67, roundStrength,
                        ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                        ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                        ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                        ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
                RoundedUtil.drawRound(x - 3, y - 5, 150, 66, roundStrength, new Color(0, 0, 0, 220));
                break;
            case "Simple":
                RenderUtil.rect(x - 2, y - 4, 148, 64, new Color(0, 0, 0, 80));
                break;
            case "Shader":
                RenderUtil.rectForShaderTheme(x - 2, y - 4, 148, 64, colorValue, false);
                break;
            case "Empathy":
                RenderUtil.roundedRectangle(x - 4, y - 4, 150, 64, 3f, ColorUtil.empathyColor());
                RenderUtil.roundedRectangle(x - 4.5, y - 1.5, 1.5, regular18Bold.height() - 2.5, 1f, color);
                break;
        }

        // 顶部
        regular18Bold.drawString(I18n.format("info.SessionInfo.title"), x + 14, y, new Color(250, 250, 250, 200).getRGB());
        FontManager.getIcon(20).drawString("I", x + 1, y + 1f, color.getRGB());

        // 第一行 游玩时间
        iconSmall.drawString("e", x, y + 12, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(I18n.format("info.SessionInfo.playtime"), x + 12, y + 11, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(playtime, x + 145 - regular18.getWidth(playtime), y + 11, new Color(250, 250, 250, 200).getRGB());

        // 第二行 击杀数量
        iconSmall.drawString("a", x, y + 22, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(I18n.format("info.killed"), x + 12, y + 21, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(kills, x + 145 - regular18.getWidth(kills), y + 21, new Color(250, 250, 250, 200).getRGB());

        // 第三行 HurtTime
        iconSmall.drawString("c", x, y + 32, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(I18n.format("info.hurtTime"), x + 12, y + 31, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(hurtTime, x + 145 - regular18.getWidth(hurtTime), y + 31, new Color(250, 250, 250, 200).getRGB());

        // 第四行 速度
        iconSmall.drawString("b", x, y + 42, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(I18n.format("info.speed"), x + 12, y + 41, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(speed, x + 145 - regular18.getWidth(speed), y + 41, new Color(250, 250, 250, 200).getRGB());

        // 第五行 血量
        iconSmall.drawString("s", x, y + 52, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(I18n.format("info.hp"), x + 12, y + 51, new Color(250, 250, 250, 200).getRGB());
        regular18.drawString(health, x + 145 - regular18.getWidth(health), y + 51, new Color(250, 250, 250, 200).getRGB());

    }

    // 计时器
    private void updatePlayTime() {

        if (mc.theWorld != null) {

            if (timer.hasReached(1000)) {

                second += 1;
                timer.reset();
            }
            if (second == 60) {
                minute += 1;
                second = 0;
            }
            if (minute == 60) {
                hour += 1;
                minute = 0;
            }
        }
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        attackedEntityList.forEach(i -> {
            if (i.isDead) {
                killed += 1;
                attackedEntityListToRemove.add(i);
            }
        });
        if (!attackedEntityListToRemove.isEmpty()) {
            attackedEntityList.removeAll(attackedEntityListToRemove);
            attackedEntityListToRemove.clear();
        }
    }

    @Override
    public void onAttack(AttackEvent event) {
        Entity target = event.getTarget();

        if (target instanceof EntityLivingBase) {
            if (!attackedEntityList.contains(target)) {
                attackedEntityList.add((EntityLivingBase) target);
            }
        }
    }

    @Override
    public void onWorld(WorldEvent event) {
        attackedEntityList.clear();
    }
}
