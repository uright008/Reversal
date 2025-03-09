package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.util.render.ThemeType;
import cn.stars.reversal.util.render.ThemeUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@ModuleInfo(name = "ChinaHat", localizedName = "module.ChinaHat.name", description = "Gives you a chinese hat",
        localizedDescription = "module.ChinaHat.desc", category = Category.RENDER)
public class ChinaHat extends Module {

    private final ModeValue  quality = new ModeValue("Quality", this, "Normal", "Special", "Umbrella", "Very Low", "Low", "Normal", "High", "Very High", "Smooth");
    private final BoolValue showInFirstPerson = new BoolValue("First Person", this, true);
    private final BoolValue rotate = new BoolValue("Rotate", this, false);

    @Override
    protected void onEnable() {
    }

    public static long lastFrame = 0;
    private int ticks;

    @Override
    public void onRender3D(final Render3DEvent event) {
        if (mc.gameSettings.thirdPersonView == 0 && !showInFirstPerson.isEnabled()) return;

        //ticks++;
        /*

        GL11.glDisable(3553);
        GL11.glLineWidth(1f);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        final double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        final double y = (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY) + mc.thePlayer.getEyeHeight() + 0.5;
        final double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        final double radius = 0.5;

        for (int i = 0; i <= 8640; ++i) {

            RenderUtil.color(new Color(ColorUtil.getColor(i / 100f, 0.7f, 1)));

//            final Color color1 = new Color(71, 148, 253);
//            final Color color2 = new Color(71, 253, 160);
//
//            final double factor = (Math.sin(((System.currentTimeMillis() % 100000) / 100f) + (i / 69f) * 0.4f) + 1) * 0.5f;
//
//            RenderUtil.color(ColorUtil.mixColors(color1, color2, factor));

            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x + radius * Math.cos(i * 6.283185307179586 / 4320), y - 0.24, z + radius * Math.sin(i * 6.283185307179586 / 4320));
        }

        GL11.glEnd();
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        RenderUtil.color(Color.WHITE);*/

        ticks += .004 * (System.currentTimeMillis() - lastFrame);
        lastFrame = System.currentTimeMillis();

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();
        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        final double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        final double y = (mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY) + mc.thePlayer.getEyeHeight() + 0.5 + (mc.thePlayer.isSneaking() ? -0.2 : 0);
        final double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        Color c;

        final double rad = 0.65f;

        int q = 64;

        boolean increaseCount = false;

        switch (quality.getMode()) {
            case "Special":
                q = 8;
                increaseCount = true;
                break;
            case "Umbrella":
                q = 16;
                break;
            case "Very Low":
                q = 32;
                increaseCount = true;
                break;
            case "Low":
                q = 64;
                increaseCount = true;
                break;
            case "Normal":
                q = 128;
                break;
            case "High":
                q = 256;
                increaseCount = true;
                break;
            case "Very High":
                q = 512;
                increaseCount = true;
                break;
            case "Smooth":
                q = 1024;
                increaseCount = true;
                break;
        }

        final double rotations = rotate.isEnabled() ? ((mc.thePlayer.prevRenderYawOffset + (mc.thePlayer.renderYawOffset - mc.thePlayer.prevRenderYawOffset) * event.getPartialTicks()) / 60) + 20 : 0;

        for (float i = 0; i < Math.PI * 2 + (increaseCount ? 0.01 : 0); i += Math.PI * 4 / q) {
            final double vecX = x + rad * Math.cos(i + rotations);
            final double vecZ = z + rad * Math.sin(i + rotations);

            c = ThemeUtil.getThemeColor(i, ThemeType.GENERAL);

            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0.8f
            );

            GL11.glVertex3d(vecX, y - 0.25, vecZ);

            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0.8f
            );

            GL11.glVertex3d(x, y, z);

        }

        GL11.glEnd();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.depthMask(true);
        GL11.glEnable(2929);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();

        GL11.glColor3f(255, 255, 255);
    }
}
