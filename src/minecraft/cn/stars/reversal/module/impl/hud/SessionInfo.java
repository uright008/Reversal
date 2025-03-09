package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.*;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.util.math.MathUtil;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.render.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;
import java.util.ArrayList;

@ModuleInfo(name = "SessionInfo", localizedName = "module.SessionInfo.name", description = "Show your game stats",
        localizedDescription = "module.SessionInfo.desc", category = Category.HUD)
public class SessionInfo extends Module {
    private final ModeValue mode = new ModeValue("Mode", this, "Simple", "Simple", "Modern", "ThunderHack", "Empathy");
    public final ColorValue colorValue = new ColorValue("Color", this);
    private final TimeUtil timer = new TimeUtil();
    int second = 0;
    int minute = 0;
    int hour = 0;
    int killed = 0;
    ArrayList<EntityLivingBase> attackedEntityList = new ArrayList<>();
    ArrayList<EntityLivingBase> attackedEntityListToRemove = new ArrayList<>();
    MFont psb = FontManager.getPSB(20);
    MFont psm = FontManager.getPSM(18);
    MFont icon = FontManager.getIcon(24);
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

        if (mode.getMode().equals("Modern")) {
            if (event.isBloom()) RoundedUtil.drawRound(x - 2, y - 4, 148, 64, 4, color);
            else RoundedUtil.drawRound(x - 2, y - 4, 148, 64, 4, Color.BLACK);
        } else if (mode.getMode().equals("ThunderHack")) {
            RoundedUtil.drawGradientRound(x - 3.5f, y - 5.5f, 151, 67, 4,
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
        } else if (mode.getMode().equals("Simple")) {
            RenderUtil.rect(x - 2, y - 4, 148, 64, Color.BLACK);
        } else if (mode.getMode().equals("Empathy")) {
            RenderUtil.roundedRectangle(x - 4, y - 4, 150, 64, 3f, ColorUtil.empathyGlowColor());
            RenderUtil.roundedRectangle(x - 4.5, y - 1.5, 1.5, psb.height() - 2.5, 3f, color);
        }
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int x = getX() + 4;
        int y = getY() + 4;
        Color color = colorValue.getColor();

        updatePlayTime();
        String playtime = hour + "h " + minute + "m " + second + "s";
        String kills = String.valueOf(killed);
        String hurtTime = String.valueOf(mc.thePlayer.hurtTime);
        String speed = String.valueOf(MathUtil.round(mc.thePlayer.getSpeed(), 1));
        String health = String.valueOf(MathUtil.round(mc.thePlayer.getHealth(), 1));

        // 背景
        if (mode.getMode().equals("Modern")) {
            RoundedUtil.drawRound(x - 2, y - 4, 148, 64, 4, new Color(0, 0, 0, 80));
            RenderUtil.roundedOutlineRectangle(x - 3, y - 5, 150, 66, 3, 1, color);
        } else if (mode.getMode().equals("ThunderHack")) {
            RoundedUtil.drawGradientRound(x - 3.5f, y - 5.5f, 151, 67, 4,
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 1000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 2000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 4000, Color.WHITE, Color.BLACK, true),
                    ColorUtils.INSTANCE.interpolateColorsBackAndForth(3, 3000, Color.WHITE, Color.BLACK, true));
            RoundedUtil.drawRound(x - 3, y - 5, 150, 66, 4, new Color(0, 0, 0, 220));
        } else if (mode.getMode().equals("Simple")) {
            RenderUtil.rect(x - 2, y - 4, 148, 64, new Color(0, 0, 0, 80));
        } else if (mode.getMode().equals("Empathy")) {
            RenderUtil.roundedRectangle(x - 4, y - 4, 150, 64, 3f, ColorUtil.empathyColor());
            RenderUtil.roundedRectangle(x - 4.5, y - 1.5, 1.5, psb.height() - 2.5, 1f, color);
        }

        // 顶部
        psb.drawString("Session Info", x + 15, y - 0.7f, new Color(250, 250, 250, 200).getRGB());
        icon.drawString("I", x, y - 0.3f, new Color(250, 250, 250, 200).getRGB());

        // 第一行 游玩时间
        iconSmall.drawString("e", x, y + 12, new Color(250, 250, 250, 200).getRGB());
        psm.drawString("Play Time", x + 12, y + 11, new Color(250, 250, 250, 200).getRGB());
        psm.drawString(playtime, x + 145 - psm.getWidth(playtime), y + 11, new Color(250, 250, 250, 200).getRGB());

        // 第二行 击杀数量
        iconSmall.drawString("a", x, y + 22, new Color(250, 250, 250, 200).getRGB());
        psm.drawString("Killed", x + 12, y + 21, new Color(250, 250, 250, 200).getRGB());
        psm.drawString(kills, x + 145 - psm.getWidth(kills), y + 21, new Color(250, 250, 250, 200).getRGB());

        // 第三行 HurtTime
        iconSmall.drawString("c", x, y + 32, new Color(250, 250, 250, 200).getRGB());
        psm.drawString("HurtTime", x + 12, y + 31, new Color(250, 250, 250, 200).getRGB());
        psm.drawString(hurtTime, x + 145 - psm.getWidth(hurtTime), y + 31, new Color(250, 250, 250, 200).getRGB());

        // 第四行 速度
        iconSmall.drawString("b", x, y + 42, new Color(250, 250, 250, 200).getRGB());
        psm.drawString("Speed", x + 12, y + 41, new Color(250, 250, 250, 200).getRGB());
        psm.drawString(speed, x + 145 - psm.getWidth(speed), y + 41, new Color(250, 250, 250, 200).getRGB());

        // 第五行 血量
        iconSmall.drawString("s", x, y + 52, new Color(250, 250, 250, 200).getRGB());
        psm.drawString("HP", x + 12, y + 51, new Color(250, 250, 250, 200).getRGB());
        psm.drawString(health, x + 145 - psm.getWidth(health), y + 51, new Color(250, 250, 250, 200).getRGB());

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
