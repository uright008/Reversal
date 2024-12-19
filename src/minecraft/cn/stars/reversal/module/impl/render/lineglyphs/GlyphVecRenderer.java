package cn.stars.reversal.module.impl.render.lineglyphs;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.module.impl.render.LineGlyphs;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.util.render.ThemeType;
import cn.stars.reversal.util.render.ThemeUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;

import static cn.stars.reversal.module.impl.render.LineGlyphs.tessellator;

public class GlyphVecRenderer implements GameInstance {

    public static void set3DRendering(Runnable render) {
        double glX = mc.getRenderManager().viewerPosX;
        double glY = mc.getRenderManager().viewerPosY;
        double glZ = mc.getRenderManager().viewerPosZ;
        GL11.glPushMatrix();
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        GL11.glEnable(3042);
        GL11.glLineWidth(1.0f);
        GL11.glPointSize(1.0f);
        GL11.glEnable(2832);
        GL11.glDisable(3553);
        mc.entityRenderer.disableLightmap();
        GL11.glDisable(2896);
        GL11.glShadeModel(7425);
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glTranslated(-glX, -glY, -glZ);
        render.run();
        GL11.glTranslated(glX, glY, glZ);
        GL11.glLineWidth(1.0f);
        GL11.glHint(3154, 4352);
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glLineWidth(1.0f);
        GL11.glPointSize(1.0f);
        GL11.glShadeModel(7424);
        GL11.glEnable(3553);
        GlStateManager.resetColor();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glPopMatrix();
    }

    private static float calcLineWidth(LineGlyphs.GlyphsVecGen gliphVecGen) {
        Vec3 cameraPos = new Vec3(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
        Vec3i pos = gliphVecGen.vecGens.stream().sorted(Comparator.comparingDouble(vec3i -> -vec3i.distanceTo(cameraPos))).findAny().orElse(new Vec3i(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE));
        double dst = cameraPos.getDistanceAtEyeByVec(mc.thePlayer, pos.getX(), pos.getY(), pos.getZ());
        return 1.0E-4f + 3.0f * (float) MathHelper.clamp_double(1.0 - dst / 20.0, 0.0, 1.0);
    }

    public static void clientColoredBegin(LineGlyphs.GlyphsVecGen gliphVecGen, int objIndex, float alphaPC, float pTicks) {
        float aPC;
        if (alphaPC * 255.0f < 1.0f || gliphVecGen.vecGens.size() < 2) {
            return;
        }
        float lineWidth = GlyphVecRenderer.calcLineWidth(gliphVecGen);
        GL11.glLineWidth(lineWidth);
        tessellator.getWorldRenderer().begin(3, DefaultVertexFormats.POSITION_COLOR);
        int colorIndex = objIndex;
        int index = 0;
        for (Vec3 vec3d : gliphVecGen.getPosVectors(pTicks)) {
            aPC = alphaPC * (0.25f + (float) index / (float) gliphVecGen.vecGens.size() / 1.75f);
            tessellator.getWorldRenderer().pos(vec3d).color(ColorUtil.reAlpha(ThemeUtil.getThemeColorInt(colorIndex, ThemeType.ARRAYLIST), aPC)).endVertex();
            colorIndex += 1;
            ++index;
        }
        tessellator.draw();
        GL11.glPointSize(lineWidth * 3.0f);
        tessellator.getWorldRenderer().begin(0, DefaultVertexFormats.POSITION_COLOR);
        colorIndex = objIndex;
        index = 0;
        for (Vec3 vec3d : gliphVecGen.getPosVectors(pTicks)) {
            aPC = alphaPC * (0.25f + (float) index / (float) gliphVecGen.vecGens.size() / 1.75f);
            tessellator.getWorldRenderer().pos(vec3d).color(ColorUtil.reAlpha(ThemeUtil.getThemeColorInt(colorIndex, ThemeType.ARRAYLIST), aPC)).endVertex();
            colorIndex += 1;
            ++index;
        }
        tessellator.draw();
    }
}