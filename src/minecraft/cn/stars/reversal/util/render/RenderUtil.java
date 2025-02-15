package cn.stars.reversal.util.render;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.module.impl.render.TargetESP;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.shader.RiseShaders;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ConcurrentModificationException;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect;
import static org.lwjgl.opengl.GL11.*;

@UtilityClass
public final class RenderUtil implements GameInstance {

    private static final Frustum frustrum = new Frustum();

    public long lastFrame = System.currentTimeMillis();
    public long last2DFrame = System.currentTimeMillis();
    public long last3DFrame = System.currentTimeMillis();

    public float deltaFrameTime;
    public float delta2DFrameTime;
    public float delta3DFrameTime;
    public long initTime = System.currentTimeMillis();

    private static int imageWidth;
    private static int imageHeight;
    private static int internalformat;
    private static ByteBuffer imageBuffer;

    public static void calcDeltaFrameTime() {
        deltaFrameTime = (System.currentTimeMillis() - lastFrame) / 10F;
        lastFrame = System.currentTimeMillis();
    }

    public static void setBuffer(ByteBuffer buffer, int width, int height) {
        internalformat = 6407;
        imageWidth = width;
        imageHeight = height;
        imageBuffer = buffer;
    }

    public static void bindTexture() {
        GL13.glActiveTexture(33984);
        GL11.glBindTexture(3553, -1);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexImage2D(3553, 0, internalformat, imageWidth, imageHeight, 0, internalformat, 5121, imageBuffer);
    }

    public void push() {
        GL11.glPushMatrix();
    }

    public void pop() {
        GL11.glPopMatrix();
    }

    public void enable(final int glTarget) {
        GL11.glEnable(glTarget);
    }

    public void disable(final int glTarget) {
        GL11.glDisable(glTarget);
    }

    public void start() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public void stop() {
        GlStateManager.enableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        color(Color.white);
    }

    public static void setAlphaLimit(float limit) {
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL_GREATER, (float) (limit * .01));
    }

    public static void resetColor() {
        GlStateManager.color(1, 1, 1, 1);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        if (needsNewFramebuffer(framebuffer)) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight;
    }

    public static void bindTexture(int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public boolean isHovered(final double x, final double y, final double width, final double height, final int mouseX, final int mouseY) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public boolean isHovered(final double x, final double y, final double width, final double height, GuiScreen guiScreen) {
        int i = Mouse.getEventX() * guiScreen.width / mc.displayWidth;
        int j = guiScreen.height - Mouse.getEventY() * guiScreen.height / mc.displayHeight - 1;
        return i >= x && i < x + width && j >= y && j < y + height;
    }

    public static void drawLoadingCircle(float x, float y) {
        for (int i = 0; i < 2; i++) {
            int rot = (int) ((System.nanoTime() / 5000000 * i) % 360);
            drawCircle(x, y, i * 8, rot - 180, rot);
        }
    }

    public static void drawRectWH(double x, double y, double width, double height, int color) {
        RenderUtil.resetColor();
        RenderUtil.setAlphaLimit(0);
        GlUtils.setup2DRendering(true);

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(x, y, 0.0D).color(color).endVertex();
        worldrenderer.pos(x, y + height, 0.0D).color(color).endVertex();
        worldrenderer.pos(x + width, y + height, 0.0D).color(color).endVertex();
        worldrenderer.pos(x + width, y, 0.0D).color(color).endVertex();
        tessellator.draw();

        GlUtils.end2DRendering();
    }

    public static void renderSteveModelTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight) {
        final ResourceLocation skin = new ResourceLocation("textures/entity/steve.png");
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GlStateManager.enableBlend();
        drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GlStateManager.disableBlend();
    }

    public static void renderPlayerModelTexture(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GlStateManager.enableBlend();
        drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GlStateManager.disableBlend();
    }

    public static void quickDrawHead(ResourceLocation skin, int x, int y, int width, int height) {
        mc.getTextureManager().bindTexture(skin);
        drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height,
                64F, 64F);
        drawScaledCustomSizeModalRect(x, y, 40F, 8F, 8, 8, width, height,
                64F, 64F);
    }

    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldrenderer.pos((double)(x + 0), (double)(y + height), (double)Gui.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + height), (double)Gui.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
        worldrenderer.pos((double)(x + width), (double)(y + 0), (double)Gui.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        worldrenderer.pos((double)(x + 0), (double)(y + 0), (double)Gui.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
        tessellator.draw();
    }

    public static void setupOrientationMatrix(double x, double y, double z) {
        GlStateManager.translate(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
    }

    public static Vector2f targetESPSPos(EntityLivingBase entity, float partialTicks) {
        EntityRenderer entityRenderer = mc.entityRenderer;
        int scaleFactor = new ScaledResolution(mc).getScaleFactor();
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        double height = entity.height / (entity.isChild() ? 1.75f : 1.0f) / 2.0f;
        AxisAlignedBB aabb = new AxisAlignedBB(x - 0.0, y, z - 0.0, x + 0.0, y + height, z + 0.0);
        Vector3d[] vectors = new Vector3d[]{new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ)};
        entityRenderer.setupCameraTransform(partialTicks, 0);
        Vector4d position = null;
        Vector3d[] vecs3 = vectors;
        int vecLength = vectors.length;
        for (int vecI = 0; vecI < vecLength; ++vecI) {
            Vector3d vector = vecs3[vecI];
            vector = project2D(scaleFactor, vector.x - mc.getRenderManager().viewerPosX, vector.y - mc.getRenderManager().viewerPosY, vector.z - mc.getRenderManager().viewerPosZ);
            if (vector == null || !(vector.z >= 0.0) || !(vector.z < 1.0)) continue;
            if (position == null) {
                position = new Vector4d(vector.x, vector.y, vector.z, 0.0);
            }
            position.x = Math.min(vector.x, position.x);
            position.y = Math.min(vector.y, position.y);
            position.z = Math.max(vector.x, position.z);
            position.w = Math.max(vector.y, position.w);
        }
        entityRenderer.setupOverlayRendering();
        if (position != null) {
            return new Vector2f((float)position.x, (float)position.y);
        }
        return null;
    }


    private static Vector3d project2D(int scaleFactor, double x, double y, double z) {
        IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        FloatBuffer modelView = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
        FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
        GL11.glGetFloat(2982, modelView);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        return GLU.gluProject((float)x, (float)y, (float)z, modelView, projection, viewport, vector) ? new Vector3d(vector.get(0) / (float)scaleFactor, ((float)Display.getHeight() - vector.get(1)) / (float)scaleFactor, vector.get(2)) : null;
    }

    public static void drawTargetESP2D(float x, float y, Color color, Color color2, float scale, int index, float alpha) {
        ResourceLocation resource = getESPImage();
        if (resource == null) {
            return;
        }

        long millis = System.currentTimeMillis() + (long) index * 400L;
        double angle = MathHelper.clamp_double((Math.sin((double) millis / 150.0) + 1.0) / 2.0 * 30.0, 0.0, 30.0);
        double scaled = MathHelper.clamp_double((Math.sin((double) millis / 500.0) + 1.0) / 2.0, 0.8, 1.0);
        double rotate = MathHelper.clamp_double((Math.sin((double) millis / 1000.0) + 1.0) / 2.0 * 360.0, 0.0, 360.0);
        rotate = (double) 45 - (angle - 15.0) + rotate;
        float size = 128.0f * scale * (float) scaled;
        float x2 = (x -= size / 2.0f) + size;
        float y2 = (y -= size / 2.0f) + size;
        GlStateManager.pushMatrix();
        RenderUtil.customRotatedObject2D(x, y, size, size, (float) rotate);
        GL11.glDisable(3008);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        drawESPImage(resource, x, y, x2, y2, color, color2, alpha);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.resetColor();
        GlStateManager.shadeModel(7424);
        GlStateManager.depthMask(true);
        GL11.glEnable(3008);
        GlStateManager.popMatrix();
    }

    private static void drawESPImage(ResourceLocation resource, double x, double y, double x2, double y2, Color c, Color c2, float alpha) {
        mc.getTextureManager().bindTexture(resource);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        bufferbuilder.pos(x, y2, 0.0).tex(0.0, 1.0).color(c.getRed(), c.getGreen(), c.getBlue(), (int) (alpha * 255)).endVertex();
        bufferbuilder.pos(x2, y2, 0.0).tex(1.0, 1.0).color(c2.getRed(), c2.getGreen(), c2.getBlue(), (int) (alpha * 255)).endVertex();
        bufferbuilder.pos(x2, y, 0.0).tex(1.0, 0.0).color(c.getRed(), c.getGreen(), c.getBlue(), (int) (alpha * 255)).endVertex();
        bufferbuilder.pos(x, y, 0.0).tex(0.0, 0.0).color(c2.getRed(), c2.getGreen(), c2.getBlue(), (int) (alpha * 255)).endVertex();
        GlStateManager.shadeModel(7425);
        GlStateManager.depthMask(false);
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.shadeModel(7424);
    }

    public static ResourceLocation getESPImage() {
        switch (ModuleInstance.getModule(TargetESP.class).mode.getMode()) {
            case "Round":
                return new ResourceLocation("reversal/images/texture/targetesp/round.png");
            case "Rectangle":
                return new ResourceLocation("reversal/images/texture/targetesp/rectangle.png");
        }
        return null;
    }

    public static void customRotatedObject2D(float oXpos, float oYpos, float oWidth, float oHeight, float rotate) {
        GL11.glTranslated(oXpos + oWidth / 2.0f, oYpos + oHeight / 2.0f, 0.0);
        GL11.glRotated(rotate, 0.0, 0.0, 1.0);
        GL11.glTranslated(-oXpos - oWidth / 2.0f, -oYpos - oHeight / 2.0f, 0.0);
    }

    public void begin(final int glMode) {
        GlStateManager.glBegin(glMode);
    }

    public void end() {
        GlStateManager.glEnd();
    }

    public void vertex(final double x, final double y) {
        GL11.glVertex2d(x, y);
    }

    public void translate(final double x, final double y) {
        GL11.glTranslated(x, y, 0);
    }

    public void scale(final double x, final double y) {
        GL11.glScaled(x, y, 1);
    }

    public void rotate(final double x, final double y, final double z, final double angle) {
        GL11.glRotated(angle, x, y, z);
    }

    public void color(final double red, final double green, final double blue, final double alpha) {
        GL11.glColor4d(red, green, blue, alpha);
    }

    public static void color(int color) {
        glColor4ub(
                (byte) (color >> 16 & 0xFF),
                (byte) (color >> 8 & 0xFF),
                (byte) (color & 0xFF),
                (byte) (color >> 24 & 0xFF));
    }

    public void color(final double red, final double green, final double blue) {
        color(red, green, blue, 1);
    }

    public void color(Color color) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
    }

    public void color(Color color, final int alpha) {
        if (color == null)
            color = Color.white;
        color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 0.5);
    }

    public void lineWidth(final double width) {
        GL11.glLineWidth((float) width);
    }


    public void renderParticles(final List<HitParticleUtils> particles) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int i = 0;
        try {
            for (final HitParticleUtils particle : particles) {
                i++;
                final Vec3 v = particle.getPosition();
                boolean draw = true;

                final double x = v.xCoord - (mc.getRenderManager()).renderPosX;
                final double y = v.yCoord - (mc.getRenderManager()).renderPosY;
                final double z = v.zCoord - (mc.getRenderManager()).renderPosZ;

                final double distanceFromPlayer = mc.thePlayer.getDistance(v.xCoord, v.yCoord - 1, v.zCoord);
                int quality = (int) (distanceFromPlayer * 4 + 10);

                if (quality > 350)
                    quality = 350;

                if (!RenderUtil.isInViewFrustrum(new EntityEgg(mc.theWorld, v.xCoord, v.yCoord, v.zCoord)))
                    draw = false;

                if (i % 10 != 0 && distanceFromPlayer > 25)
                    draw = false;

                if (i % 3 == 0 && distanceFromPlayer > 15)
                    draw = false;

                if (draw) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y, z);

                    final float scale = 0.04F;
                    GL11.glScalef(-scale, -scale, -scale);

                    GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                    GL11.glRotated(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0D : 1.0D, 0.0D, 0.0D);

                    final Color c = ThemeUtil.getThemeColor(i / 5F, ThemeType.GENERAL);

                    drawFilledCircleNoGL(0, -3, 0.7, c.hashCode(), quality);

                    if (distanceFromPlayer < 4)
                        drawFilledCircleNoGL(0, -3, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

                    if (distanceFromPlayer < 20)
                        drawFilledCircleNoGL(0, -3, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);

                    GL11.glScalef(0.8F, 0.8F, 0.8F);
                    GL11.glPopMatrix();
                }
            }
        } catch (final ConcurrentModificationException ignored) {
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        GL11.glColor3d(255, 255, 255);
    }

    public static void drawParallelogram(double x, double y, double width, double height, final double skew, final boolean filled, final Color color) {
        start();

        if (filled) {
            x += 0.2;
            y += 0.2;
            width -= 0.4;
            height -= 0.4;
            // Draw filled shape
            if (color != null)
                color(color);

            begin(GL11.GL_TRIANGLE_FAN);
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width + skew, y + height);
            vertex(x + skew, y + height);
            end();

            // Draw smooth outline with slightly darker color
            Color outlineColor = new Color(
                    Math.max(0, color.getRed() - 15),
                    Math.max(0, color.getGreen() - 15),
                    Math.max(0, color.getBlue() - 15),
                    color.getAlpha()
            );
            color(outlineColor);

            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(1.0f);

            begin(GL11.GL_LINE_LOOP);
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width + skew, y + height);
            vertex(x + skew, y + height);
            end();

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        } else {
            // Draw non-filled shape with smooth lines
            if (color != null)
                color(color);

            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            GL11.glLineWidth(1.5f);

            begin(GL11.GL_LINE_LOOP);
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width + skew, y + height);
            vertex(x + skew, y + height);
            end();

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        stop();
    }

    public static void rect(final double x, final double y, final double width, final double height, final boolean filled, final Color color) {
        start();
        if (color != null)
            color(color);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINES);

        {
            vertex(x, y);
            vertex(x + width, y);
            vertex(x + width, y + height);
            vertex(x, y + height);
            if (!filled) {
                vertex(x, y);
                vertex(x, y + height);
                vertex(x + width, y);
                vertex(x + width, y + height);
            }
        }
        end();
        stop();
    }

    public void rect(final double x, final double y, final double width, final double height, final boolean filled) {
        rect(x, y, width, height, filled, null);
    }

    public void rect(final double x, final double y, final double width, final double height, final Color color) {
        rect(x, y, width, height, true, color);
    }

    public void rect(final double x, final double y, final double width, final double height) {
        rect(x, y, width, height, true, null);
    }

    public void rectCentered(double x, double y, final double width, final double height, final boolean filled, final Color color) {
        x -= width / 2;
        y -= height / 2;
        rect(x, y, width, height, filled, color);
    }

    public void rectCentered(double x, double y, final double width, final double height, final boolean filled) {
        x -= width / 2;
        y -= height / 2;
        rect(x, y, width, height, filled, null);
    }

    public void rectCentered(double x, double y, final double width, final double height, final Color color) {
        x -= width / 2;
        y -= height / 2;
        rect(x, y, width, height, true, color);
    }

    public void rectCentered(double x, double y, final double width, final double height) {
        x -= width / 2;
        y -= height / 2;
        rect(x, y, width, height, true, null);
    }

    public void gradient(final double x, final double y, final double width, final double height, final boolean filled, final Color color1, final Color color2) {
        start();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableAlpha();
        GL11.glAlphaFunc(GL_GREATER, 0);
        if (color1 != null)
            color(color1);
        begin(filled ? GL11.GL_QUADS : GL11.GL_LINES);
        {
            vertex(x, y);
            vertex(x + width, y);
            if (color2 != null)
                color(color2);
            vertex(x + width, y + height);
            vertex(x, y + height);
            if (!filled) {
                vertex(x, y);
                vertex(x, y + height);
                vertex(x + width, y);
                vertex(x + width, y + height);
            }
        }
        end();
        GL11.glAlphaFunc(GL_GREATER, 0.1f);
        GlStateManager.disableAlpha();
        GL11.glShadeModel(GL11.GL_FLAT);
        stop();
    }

    public void gradient(final double x, final double y, final double width, final double height, final Color color1, final Color color2) {
        gradient(x, y, width, height, true, color1, color2);
    }

    public void gradientCentered(double x, double y, final double width, final double height, final Color color1, final Color color2) {
        x -= width / 2;
        y -= height / 2;
        gradient(x, y, width, height, true, color1, color2);
    }

    public void gradientSideways(final double x, final double y, final double width, final double height, final boolean filled, final Color color1, final Color color2) {
        start();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableAlpha();
        if (color1 != null)
            color(color1);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINES);
        {
            vertex(x, y);
            vertex(x, y + height);
            if (color2 != null)
                color(color2);
            vertex(x + width, y + height);
            vertex(x + width, y);
        }
        end();
        GlStateManager.enableAlpha();
        GL11.glShadeModel(GL11.GL_FLAT);
        stop();
    }

    public void gradientSideways(final double x, final double y, final double width, final double height, final Color color1, final Color color2) {
        gradientSideways(x, y, width, height, true, color1, color2);
    }

    public void gradientSidewaysCentered(double x, double y, final double width, final double height, final Color color1, final Color color2) {
        x -= width / 2;
        y -= height / 2;
        gradientSideways(x, y, width, height, true, color1, color2);
    }

    public void polygon(final double x, final double y, double sideLength, final double amountOfSides, final boolean filled, final Color color) {
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        if (!filled) GL11.glLineWidth(2);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
        {
            for (double i = 0; i <= amountOfSides / 4; i++) {
                final double angle = i * 4 * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final boolean filled) {
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides, final Color color) {
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygon(final double x, final double y, final double sideLength, final int amountOfSides) {
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, color);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final boolean filled) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, filled, null);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, color);
    }

    public void polygonCentered(double x, double y, final double sideLength, final int amountOfSides) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, amountOfSides, true, null);
    }

    public void circle(final double x, final double y, final double radius, final boolean filled, final Color color) {
        polygon(x, y, radius, 360, filled, color);
    }

    public void circle(final double x, final double y, final double radius, final double angle, final boolean filled, final Color color) {
        polygon(x, y, radius, angle, filled, color);
    }

    public void circle(final double x, final double y, final double radius, final boolean filled) {
        polygon(x, y, radius, 360, filled);
    }

    public void circle(final double x, final double y, final double radius, final Color color) {
        polygon(x, y, radius, 360, color);
    }

    public void circle(final double x, final double y, final double radius) {
        polygon(x, y, radius, 360);
    }

    public void circleCentered(double x, double y, final double radius, final double angle, final boolean filled, final Color color) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, angle, filled, color);
    }

    public void circleCentered(double x, double y, final double radius, final boolean filled, final Color color) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360, filled, color);
    }

    public void circleCentered(double x, double y, final double radius, final boolean filled) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360, filled);
    }

    public void circleCentered(double x, double y, final double radius, final boolean filled, final int sides) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, sides, filled);
    }

    public void circleCentered(double x, double y, final double radius, final Color color) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360, color);
    }

    public void circleCentered(double x, double y, final double radius) {
        x -= radius / 2;
        y -= radius / 2;
        polygon(x, y, radius, 360);
    }

    public void triangle(final double x, final double y, final double sideLength, final boolean filled, final Color color) {
        polygon(x, y, sideLength, 3, filled, color);
    }

    public void triangle(final double x, final double y, final double sideLength, final boolean filled) {
        polygon(x, y, sideLength, 3, filled);
    }

    public void triangle(final double x, final double y, final double sideLength, final Color color) {
        polygon(x, y, sideLength, 3, color);
    }

    public void triangle(final double x, final double y, final double sideLength) {
        polygon(x, y, sideLength, 3);
    }

    public void triangleCentered(double x, double y, final double sideLength, final boolean filled, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, filled, color);
    }

    public void triangleCentered(double x, double y, final double sideLength, final boolean filled) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, filled);
    }

    public void triangleCentered(double x, double y, final double sideLength, final Color color) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3, color);
    }

    public void triangleCentered(double x, double y, final double sideLength) {
        x -= sideLength / 2;
        y -= sideLength / 2;
        polygon(x, y, sideLength, 3);
    }

    public void lineNoGl(final double firstX, final double firstY, final double secondX, final double secondY, final Color color) {

        start();
        if (color != null)
            color(color);
        RenderUtil.lineWidth(1);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);
        {
            vertex(firstX, firstY);
            vertex(secondX, secondY);
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public void line(final double firstX, final double firstY, final double secondX, final double secondY, final double lineWidth, final Color color) {
        start();
        if (color != null)
            color(color);
        lineWidth(lineWidth);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);
        {
            vertex(firstX, firstY);
            vertex(secondX, secondY);
        }
        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();
    }

    public void line(final double firstX, final double firstY, final double secondX, final double secondY, final double lineWidth) {
        line(firstX, firstY, secondX, secondY, lineWidth, null);
    }

    public void line(final double firstX, final double firstY, final double secondX, final double secondY, final Color color) {
        line(firstX, firstY, secondX, secondY, 0, color);
    }

    public void line(final double firstX, final double firstY, final double secondX, final double secondY) {
        line(firstX, firstY, secondX, secondY, 0, null);
    }

    /**
     * Scales the data that you put in the runnable
     *
     * @param x     start x pos
     * @param y     start y pos
     * @param scale scale
     */
    public static void scaleStart(float x, float y, float scale) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        glScalef(scale, scale, 1);
        glTranslatef(-x, -y, 0);
    }

    /**
     * End scale
     */
    public static void scaleEnd() {
        glPopMatrix();
    }

    public void image(final ResourceLocation imageLocation, final float x, final float y, final float width, final float height) {
        enable(GL11.GL_BLEND);
        mc.getTextureManager().bindTexture(imageLocation);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        disable(GL11.GL_BLEND);
    }

    public static void image(DynamicTexture image, float x, float y, float imgWidth, float imgHeight) {
        GlUtils.startBlend();

        // 绑定纹理并设置过滤参数
        GlStateManager.bindTexture(image.getGlTextureId());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // 启用多重采样
        glEnable(GL13.GL_MULTISAMPLE);

        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);

        // 禁用多重采样
        glDisable(GL13.GL_MULTISAMPLE);

        GlUtils.endBlend();
    }

    public static void image(ResourceLocation resourceLocation, float x, float y, float imgWidth, float imgHeight, Color color) {
        glColor(color);
        image(resourceLocation, x, y, imgWidth, imgHeight);
        resetColor();
    }

    public static void image(ResourceLocation resource, float x, float y, float x2, float y2, int c) {
        mc.getTextureManager().bindTexture(resource);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(9, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(x, y2).tex(0.0, 1.0).color(c).endVertex();
        worldRenderer.pos(x2, y2).tex(1.0, 1.0).color(c).endVertex();
        worldRenderer.pos(x2, y).tex(1.0, 0.0).color(c).endVertex();
        worldRenderer.pos(x, y).tex(0.0, 0.0).color(c).endVertex();
        GL11.glShadeModel(7425);
        GL11.glDepthMask(false);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glShadeModel(7424);
    }

    public void image(final ResourceLocation imageLocation, final float x, final float y, final float width, final float height, float alpha) {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        enable(GL11.GL_BLEND);
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        mc.getTextureManager().bindTexture(imageLocation);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        disable(GL11.GL_BLEND);
    }

    public void imageCentered(final ResourceLocation imageLocation, float x, float y, final float width, final float height) {
        x -= width / 2f;
        y -= height / 2f;
        image(imageLocation, x, y, width, height);
    }

    public void roundedRectangle(double x, double y, double width, double height, double radius, Color color) {
        RiseShaders.RQ_SHADER.draw(x, y, width, height, radius, color);
    }

    public void roundedGradientRectangle(double x, double y, double width, double height, double radius, Color color1, Color color2, boolean vertical) {
        RiseShaders.RGQ_SHADER.draw(x, y, width, height, radius, color1, color2, vertical);
    }

    public void roundedOutlineRectangle(double x, double y, double width, double height, double radius, double borderSize, Color color) {
        RiseShaders.ROQ_SHADER.draw(x, y, width, height, radius, borderSize, color);
    }

    public void roundedOutlineGradientRectangle(double x, double y, double width, double height, double radius, double borderSize, Color color1, Color color2) {
        RiseShaders.ROGQ_SHADER.draw(x, y, width, height, radius, borderSize, color1, color2);
    }

    public void rectangle(final double x, final double y, final double width, final double height, final Color color) {
        start();

        if (color != null) {
            glColor(color);
        }

        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x + width, y);
        GL11.glVertex2d(x + width, y + height);
        GL11.glVertex2d(x, y + height);
        GL11.glEnd();

        stop();
    }

    static void glColor(final Color color) {
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
    }

    public static void scissor(double x, double y, double width, double height) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        width *= scale;
        height *= scale;

        GL11.glScissor((int) x, (int) (y - height), (int) width, (int) height);
    }

    public static void scissorXy(double x, double y, double x2, double y2) {
        final ScaledResolution sr = new ScaledResolution(mc);
        final double scale = sr.getScaleFactor();

        y = sr.getScaledHeight() - y;

        x *= scale;
        y *= scale;
        x2 *= scale;
        y2 *= scale;

        GL11.glScissor((int) x, (int) (y - (y2 - y)), (int) (x2 - x), (int) (y2 - y));
    }

    public void outlineInlinedGradientRect(final double x, final double y, final double width, final double height, final double inlineOffset, final Color color1, final Color color2) {
        gradient(x, y, width, inlineOffset, color1, color2);
        gradient(x, y + height - inlineOffset, width, inlineOffset, color2, color1);
        gradientSideways(x, y, inlineOffset, height, color1, color2);
        gradientSideways(x + width - inlineOffset, y, inlineOffset, height, color2, color1);
    }

    /**
     * ClickGui shit
     */

    public void roundedRect(final double x, final double y, double width, double height, final double edgeRadius, final Color color) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        float sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 180; i <= 270; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + sideLength, y + sideLength);
        }

        end();
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 0; i <= 90; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + width, y + height);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 270; i <= 360; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + width, y + sideLength);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_TRIANGLE_FAN);

        {
            for (double i = 90; i <= 180; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + sideLength, y + height);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        // Main block
        rect(x + halfRadius, y + halfRadius, width - halfRadius, height - halfRadius, color);

        // Horizontal bars
        rect(x, y + halfRadius, edgeRadius / 2, height - halfRadius, color);
        rect(x + width, y + halfRadius, edgeRadius / 2, height - halfRadius, color);

        // Vertical bars
        rect(x + halfRadius, y, width - halfRadius, halfRadius, color);
        rect(x + halfRadius, y + height, width - halfRadius, halfRadius, color);
    }

    public void roundedOutLine(final double x, final double y, double width, double height, final double thickness, final double edgeRadius, final Color color) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        float sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);

        {
            for (double i = 180; i <= 270; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + sideLength, y + sideLength);
        }

        end();
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL11.GL_LINES);

        {
            for (double i = 0; i <= 90; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + width, y + height);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL_LINES);

        {
            for (double i = 270; i <= 360; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + width, y + sideLength);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        begin(GL_LINES);

        {
            for (double i = 90; i <= 180; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + sideLength, y + height);
        }

        end();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        stop();

        // Main block
        //rect(x + halfRadius, y + halfRadius, width - halfRadius, height - halfRadius, color);

        // Horizontal bars
        /*outline(x, y + halfRadius, edgeRadius / 2, height - halfRadius, thickness, color);
        outline(x + width, y + halfRadius, edgeRadius / 2, height - halfRadius, thickness, color);

        // Vertical bars
        outline(x + halfRadius, y, width - halfRadius, halfRadius, thickness, color);
        outline(x + halfRadius, y + height, width - halfRadius, halfRadius, thickness, color);*/
    }

    public static void drawCircle(float cx, float cy, float r, int num_segments, int c) {
        glPushMatrix();
        cx *= 2.0f;
        cy *= 2.0f;
        float f = (float) (c >> 24 & 0xFF) / 255.0f;
        float f1 = (float) (c >> 16 & 0xFF) / 255.0f;
        float f2 = (float) (c >> 8 & 0xFF) / 255.0f;
        float f3 = (float) (c & 0xFF) / 255.0f;
        float theta = (float) (6.2831852 / (double) num_segments);
        float p = (float) Math.cos(theta);
        float s = (float) Math.sin(theta);
        float x = r * 2.0f;
        float y = 0.0f;
        GlUtils.setup2DRendering();
        glScalef(0.5f, 0.5f, 0.5f);
        glColor4f(f1, f2, f3, f);
        glBegin(2);
        for (int ii = 0; ii < num_segments; ++ii) {
            glVertex2f(cx, cy);
            glVertex2f(x + cx, y + cy);
            float t = x;
            x = p * x - s * y;
            y = s * t + p * y;
        }
        // 结束绘制
        glEnd();
        glScalef(2.0f, 2.0f, 2.0f);
        GlUtils.end2DRendering();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        glPopMatrix();
    }

    public void roundedRectCustom(final double x, final double y, double width, double height, final double edgeRadius, final Color color, final boolean topLeft, final boolean topRight, final boolean bottomLeft, final boolean bottomRight) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        float sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);

        if (topLeft) {

            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            begin(GL11.GL_TRIANGLE_FAN);

            for (double i = 180; i <= 270; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + sideLength, y + sideLength);


            end();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            stop();

        } else {

            rect(x, y, sideLength, sideLength, color);

        }

        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);


        if (bottomRight) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            begin(GL11.GL_TRIANGLE_FAN);
            for (double i = 0; i <= 90; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + width, y + height);
            end();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            stop();
        } else {
            rect(x + width, y + height, sideLength, sideLength, color);
        }


        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);


        if (topRight) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            begin(GL11.GL_TRIANGLE_FAN);
            for (double i = 270; i <= 360; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + width + (sideLength * Math.cos(angle)), y + (sideLength * Math.sin(angle)) + sideLength);
            }
            vertex(x + width, y + sideLength);
            end();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            stop();
        } else {
            rect(x + width, y, sideLength, sideLength, color);
        }


        sideLength = (float) edgeRadius;
        sideLength /= 2;
        start();
        if (color != null)
            color(color);


        if (bottomLeft) {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            begin(GL11.GL_TRIANGLE_FAN);
            for (double i = 90; i <= 180; i++) {
                final double angle = i * (Math.PI * 2) / 360;
                vertex(x + (sideLength * Math.cos(angle)) + sideLength, y + height + (sideLength * Math.sin(angle)));
            }
            vertex(x + sideLength, y + height);
            end();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            stop();
        } else {
            rect(x, y + height, sideLength, sideLength, color);
        }


        // Main block
        rect(x + halfRadius, y + halfRadius, width - halfRadius, height - halfRadius, color);

        // Horizontal bars
        rect(x, y + halfRadius, edgeRadius / 2, height - halfRadius, color);
        rect(x + width, y + halfRadius, edgeRadius / 2, height - halfRadius, color);

        // Vertical bars
        rect(x + halfRadius, y, width - halfRadius, halfRadius, color);
        rect(x + halfRadius, y + height, width - halfRadius, halfRadius, color);
    }

    public void roundedRectTop(final double x, final double y, double width, double height, final double edgeRadius, final Color color) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        // Top left and right circles
        circle(x, y, edgeRadius, color);
        circle(x + width - edgeRadius / 2, y, edgeRadius, color);
        // Main block
        rect(x, y + halfRadius, width + halfRadius, height, color);

        // Vertical bar
        rect(x + halfRadius, y, width - halfRadius, halfRadius, color);
    }

    public void roundedRectBottom(final double x, final double y, double width, double height, final double edgeRadius, final Color color) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        // Bottom left and right circles
        circle(x + width - edgeRadius / 2, y + height - edgeRadius / 2, edgeRadius, color);
        circle(x, y + height - edgeRadius / 2, edgeRadius, color);

        // Main block
        rect(x, y, width + halfRadius, height, color);

        // Vertical bar
        rect(x + halfRadius, y + height, width - halfRadius, halfRadius, color);
    }

    public void roundedRectRight(final double x, final double y, double width, double height, final double edgeRadius, final Color color1, final Color color2) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        // Top left and right circles
        circle(x + width - edgeRadius / 2, y, edgeRadius, color2);
        circle(x + width - edgeRadius / 2, y + height - edgeRadius / 2, edgeRadius, color2);

        // Main block
        gradientSideways(x, y, width, height + halfRadius, color1, color2);

        // Vertical bar
        rect(x + width, y + halfRadius, 5, height - halfRadius, color2);
    }

    public void roundedRectRightTop(final double x, final double y, double width, double height, final double edgeRadius, final Color color1, final Color color2) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        // Top left and right circles
        circle(x + width - edgeRadius / 2, y, edgeRadius, color2);

        // Main block
        gradientSideways(x, y, width, height + halfRadius, color1, color2);

        // Vertical bar
        rect(x + width, y + halfRadius, 5, height, color2);
    }

    public void roundedRectRightBottom(final double x, final double y, double width, double height, final double edgeRadius, final Color color1, final Color color2) {
        final double halfRadius = edgeRadius / 2;
        width -= halfRadius;
        height -= halfRadius;

        // Bottom left and right circles
        circle(x + width - edgeRadius / 2, y + height - edgeRadius / 2, edgeRadius, color2);

        // Main block
        gradientSideways(x, y, width, height + halfRadius, color1, color2);
        // Vertical bar
        rect(x + width, y, 5, height, color2);
    }

    public void drawBorder(final float x, final float y, final float x2, final float y2, final float width, final int color1) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);

        color(new Color(color1));
        glLineWidth(width);

        glBegin(GL_LINE_LOOP);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();

        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
    }

    public static void drawTracerLine(final double x, final double y, final double z, final float red, final float green, final float blue, final float alpha, final float lineWdith) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableTexture2D();
        GL11.glBlendFunc(770, 771);
        GlStateManager.enableBlend();
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(2);
        GL11.glVertex3d(0.0D, 0.0D + Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0.0D);
        GL11.glVertex3d(x, y, z);
        GL11.glEnd();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void drawLine(final double x, final double y, final double z, final double x2, final double y2, final double z2, final float red, final float green, final float blue, final float alpha, final float lineWdith) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GlStateManager.disableTexture2D();
        GL11.glBlendFunc(770, 771);
        GlStateManager.enableBlend();
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(2);
        Vec3 renderPos = RenderUtil.getRenderPos(x2, y2, z2);
        GL11.glVertex3d(renderPos.xCoord, renderPos.yCoord, renderPos.zCoord);

        renderPos = RenderUtil.getRenderPos(x, y, z);
        GL11.glVertex3d(renderPos.xCoord, renderPos.yCoord, renderPos.zCoord);
        GL11.glEnd();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public void renderBreadCrumb(final Vec3 vec3) {

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        try {

            final double x = vec3.xCoord - (mc.getRenderManager()).renderPosX;
            final double y = vec3.yCoord - (mc.getRenderManager()).renderPosY;
            final double z = vec3.zCoord - (mc.getRenderManager()).renderPosZ;

            final double distanceFromPlayer = mc.thePlayer.getDistance(vec3.xCoord, vec3.yCoord - 1, vec3.zCoord);
            int quality = (int) (distanceFromPlayer * 4 + 10);

            if (quality > 350)
                quality = 350;

            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);

            final float scale = 0.04f;
            GL11.glScalef(-scale, -scale, -scale);

            GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
            GL11.glRotated((mc.getRenderManager()).playerViewX, 1.0D, 0.0D, 0.0D);

            final Color c = ThemeUtil.getThemeColor(ThemeType.GENERAL);

            RenderUtil.drawFilledCircleNoGL(0, 0, 0.7, c.hashCode(), quality);

            if (distanceFromPlayer < 4)
                RenderUtil.drawFilledCircleNoGL(0, 0, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

            if (distanceFromPlayer < 20)
                RenderUtil.drawFilledCircleNoGL(0, 0, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);


            GL11.glScalef(0.8f, 0.8f, 0.8f);

            GL11.glPopMatrix();


        } catch (final ConcurrentModificationException ignored) {
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();

        GL11.glColor3d(255, 255, 255);
    }

    public void renderBreadCrumbs(final List<Vec3> vec3s) {

        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int i = 0;
        try {
            for (final Vec3 v : vec3s) {

                i++;

                boolean draw = true;

                final double x = v.xCoord - (mc.getRenderManager()).renderPosX;
                final double y = v.yCoord - (mc.getRenderManager()).renderPosY;
                final double z = v.zCoord - (mc.getRenderManager()).renderPosZ;

                final double distanceFromPlayer = mc.thePlayer.getDistance(v.xCoord, v.yCoord - 1, v.zCoord);
                int quality = (int) (distanceFromPlayer * 4 + 10);

                if (quality > 350)
                    quality = 350;

                if (i % 10 != 0 && distanceFromPlayer > 25) {
                    draw = false;
                }

                if (i % 3 == 0 && distanceFromPlayer > 15) {
                    draw = false;
                }

                if (draw) {

                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y, z);

                    final float scale = 0.04f;
                    GL11.glScalef(-scale, -scale, -scale);

                    GL11.glRotated(-(mc.getRenderManager()).playerViewY, 0.0D, 1.0D, 0.0D);
                    GL11.glRotated((mc.getRenderManager()).playerViewX, 1.0D, 0.0D, 0.0D);

                    final Color c = ThemeUtil.getThemeColor(i / 5f, ThemeType.GENERAL);

                    RenderUtil.drawFilledCircleNoGL(0, 0, 0.7, c.hashCode(), quality);

                    if (distanceFromPlayer < 4)
                        RenderUtil.drawFilledCircleNoGL(0, 0, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

                    if (distanceFromPlayer < 20)
                        RenderUtil.drawFilledCircleNoGL(0, 0, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);

                    GL11.glScalef(0.8f, 0.8f, 0.8f);

                    GL11.glPopMatrix();

                }

            }
        } catch (final ConcurrentModificationException ignored) {
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();

        GL11.glColor3d(255, 255, 255);
    }


  /*  public void renderParticles(final List<Particle> particles) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        int i = 0;
        try {
            for (final Particle particle : particles) {
                i++;
                final Vec3 v = particle.getPosition();
                boolean draw = true;

                final double x = v.xCoord - (mc.getRenderManager()).renderPosX;
                final double y = v.yCoord - (mc.getRenderManager()).renderPosY;
                final double z = v.zCoord - (mc.getRenderManager()).renderPosZ;

                final double distanceFromPlayer = mc.thePlayer.getDistance(v.xCoord, v.yCoord - 1, v.zCoord);
                int quality = (int) (distanceFromPlayer * 4 + 10);

                if (quality > 350)
                    quality = 350;

                if (!RenderUtil.isInViewFrustrum(new EntityEgg(mc.theWorld, v.xCoord, v.yCoord, v.zCoord)))
                    draw = false;

                if (i % 10 != 0 && distanceFromPlayer > 25)
                    draw = false;

                if (i % 3 == 0 && distanceFromPlayer > 15)
                    draw = false;

                if (draw) {
                    GL11.glPushMatrix();
                    GL11.glTranslated(x, y, z);

                    final float scale = 0.04F;
                    GL11.glScalef(-scale, -scale, -scale);

                    GL11.glRotated(-mc.getRenderManager().playerViewY, 0.0D, 1.0D, 0.0D);
                    GL11.glRotated(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0D : 1.0D, 0.0D, 0.0D);

                    final Color c = ThemeUtil.getThemeColor(i / 5F, ThemeType.GENERAL);

                    drawFilledCircleNoGL(0, -3, 0.7, c.hashCode(), quality);

                    if (distanceFromPlayer < 4)
                        drawFilledCircleNoGL(0, -3, 1.4, new Color(c.getRed(), c.getGreen(), c.getBlue(), 50).hashCode(), quality);

                    if (distanceFromPlayer < 20)
                        drawFilledCircleNoGL(0, -3, 2.3, new Color(c.getRed(), c.getGreen(), c.getBlue(), 30).hashCode(), quality);

                    GL11.glScalef(0.8F, 0.8F, 0.8F);
                    GL11.glPopMatrix();
                }
            }
        } catch (final ConcurrentModificationException ignored) {
        }

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

        GL11.glColor3d(255, 255, 255);
    } */

    public static void putVertex3DInWorld(final double x, final double y, final double z) {
        putVertex3d(getRenderPos(x, y, z));
    }

    public static void drawBoundingBox(final AxisAlignedBB aa) {

        begin(GL_TRIANGLE_STRIP);
        putVertex3DInWorld(aa.minX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.maxZ);
        end();

        begin(GL_TRIANGLE_STRIP);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.minX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.maxZ);
        end();

        begin(GL_TRIANGLE_STRIP);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.minZ);
        end();

        begin(GL_TRIANGLE_STRIP);
        putVertex3DInWorld(aa.minX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.minX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.minZ);
        end();

        begin(GL_TRIANGLE_STRIP);
        putVertex3DInWorld(aa.minX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.minX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.minZ);
        end();

        begin(GL_TRIANGLE_STRIP);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.minY, aa.maxZ);
        putVertex3DInWorld(aa.minX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.minX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.minZ);
        putVertex3DInWorld(aa.maxX, aa.maxY, aa.maxZ);
        putVertex3DInWorld(aa.maxX, aa.minY, aa.maxZ);
        end();
    }


    public static void drawSolidBlockESP(final double x, final double y, final double z, final float red, final float green, final float blue,
                                         final float alpha) {
        GL11.glPushMatrix();
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
        GL11.glPopMatrix();
    }

    public static void drawSolidBlockESP(final double x, final double y, final double z, final Color color) {
        GL11.glPushMatrix();
        color(color);
        drawBoundingBox(new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D));
        GL11.glPopMatrix();
    }

    public static void drawSolidEntityESP(final double x, final double y, final double z, final double width, final double height, final float red,
                                          final float green, final float blue, final float alpha) {
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glBlendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(red, green, blue, alpha);
        drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void drawSolidEntityESPFixed(final double x, final double y, final double z, final double width, final double height, final float red,
                                               final float green, final float blue, final float alpha, final Entity e) {
        drawSolidEntityESP(x, y, z, width, height, red, green, blue, alpha);
    }

    public static void draw3DLine(double x, double y, double z, double x1, double y1, double z1, final float red, final float green,
                                  final float blue, final float alpha, final float lineWdith) {

        x = x - mc.getRenderManager().renderPosX;
        x1 = x1 - mc.getRenderManager().renderPosX;
        y = y - mc.getRenderManager().renderPosY;
        y1 = y1 - mc.getRenderManager().renderPosY;
        z = z - mc.getRenderManager().renderPosZ;
        z1 = z1 - mc.getRenderManager().renderPosZ;

        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableTexture2D();
        GL11.glBlendFunc(770, 771);
        GlStateManager.enableBlend();
        GL11.glLineWidth(lineWdith);
        GL11.glColor4f(red, green, blue, alpha);
        GL11.glBegin(2);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glEnd();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

    public static void color4f(final float red, final float green, final float blue, final float alpha) {
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void lineWidth(final float width) {
        GL11.glLineWidth(width);
    }

    public static void glBegin(final int mode) {
        GL11.glBegin(mode);
    }

    public static void glEnd() {
        GL11.glEnd();
    }

    public static void putVertex3d(final double x, final double y, final double z) {
        GL11.glVertex3d(x, y, z);
    }

    public static void putVertex3d(final Vec3 vec) {
        GL11.glVertex3d(vec.xCoord, vec.yCoord, vec.zCoord);
    }

    public static Vec3 getRenderPos(double x, double y, double z) {

        x = x - mc.getRenderManager().renderPosX;
        y = y - mc.getRenderManager().renderPosY;
        z = z - mc.getRenderManager().renderPosZ;

        return new Vec3(x, y, z);
    }

    public static void drawCircle(final int x, final int y, final double r, final float f1, final float f2, final float f3, final float f) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i <= 360; i++) {
            final double x2 = Math.sin(((i * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawFilledCircle(final int x, final int y, final double r, final int c) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360; i++) {
            final double x2 = Math.sin(((i * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawFilledCircle(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawFilledCircle(final double x, final double y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / 20; i++) {
            final double x2 = Math.sin(((i * 20 * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * 20 * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();

    }

    public static void drawFilledCircleNoGL(final int x, final int y, final double r, final int c, final int quality) {
        final float f = ((c >> 24) & 0xff) / 255F;
        final float f1 = ((c >> 16) & 0xff) / 255F;
        final float f2 = ((c >> 8) & 0xff) / 255F;
        final float f3 = (c & 0xff) / 255F;

        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        for (int i = 0; i <= 360 / quality; i++) {
            final double x2 = Math.sin(((i * quality * Math.PI) / 180)) * r;
            final double y2 = Math.cos(((i * quality * Math.PI) / 180)) * r;
            GL11.glVertex2d(x + x2, y + y2);
        }

        GL11.glEnd();
    }

    public static void enableDefaults() {
        mc.entityRenderer.disableLightmap();
        GL11.glEnable(3042 /* GL_BLEND */);
        GL11.glDisable(3553 /* GL_TEXTURE_2D */);
        GL11.glDisable(2896 /* GL_LIGHTING */);
        // GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848 /* GL_LINE_SMOOTH */);
        GL11.glPushMatrix();
    }

    public static void disableDefaults() {
        GL11.glPopMatrix();
        GL11.glDisable(2848 /* GL_LINE_SMOOTH */);
        GL11.glDepthMask(true);
        // GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glEnable(3553 /* GL_TEXTURE_2D */);
        GL11.glEnable(2896 /* GL_LIGHTING */);
        GL11.glDisable(3042 /* GL_BLEND */);
        mc.entityRenderer.enableLightmap();
    }

    public static boolean isInViewFrustrum(final Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = mc.getRenderViewEntity();
        frustrum.setPosition(current.posX, current.posY, current.posZ);
        return frustrum.isBoundingBoxInFrustum(bb);
    }

    public static void quickDrawRect(final float x, final float y, final float x2, final float y2, final int color) {
        glColor(color);
        glBegin(GL_QUADS);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }

    public static void quickDrawBorderedRect(final float x, final float y, final float x2, final float y2, final float width, final int color1, final int color2) {
        quickDrawRect(x, y, x2, y2, color2);

        glColor(color1);
        glLineWidth(width);

        glBegin(GL_LINE_LOOP);

        glVertex2d(x2, y);
        glVertex2d(x, y);
        glVertex2d(x, y2);
        glVertex2d(x2, y2);

        glEnd();
    }

    private static void glColor(final int hex) {
        final float alpha = (hex >> 24 & 0xFF) / 255F;
        final float red = (hex >> 16 & 0xFF) / 255F;
        final float green = (hex >> 8 & 0xFF) / 255F;
        final float blue = (hex & 0xFF) / 255F;

        GlStateManager.color(red, green, blue, alpha);
    }

    public static void renderGradientRectLeftRight(final int left, final int top, final int right, final int bottom, final int startColor, final int endColor) {
        final float f = (float) (startColor >> 24 & 255) / 255.0F;
        final float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        final float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        final float f3 = (float) (startColor & 255) / 255.0F;
        final float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        final float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        final float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        final float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, bottom, Gui.zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, top, Gui.zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(left, top, Gui.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, Gui.zLevel).color(f1, f2, f3, f).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void rainbowRectangle(final float x, final float y, final float width, final float height, final float divider) {
        for (int i = 0; i <= width; i++) {
            RenderUtil.rect(x + i, y, 1, height, new Color(ColorUtil.getColor(i / divider, 0.7f, 1)));
        }
    }

    public static void drawGradientRect(final int left, final int top, final int right, final int bottom, final int startColor, final int endColor) {
        final float f = (float) (startColor >> 24 & 255) / 255.0F;
        final float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        final float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        final float f3 = (float) (startColor & 255) / 255.0F;
        final float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        final float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        final float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        final float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, Gui.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, Gui.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, Gui.zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, Gui.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    // Nicklas stuff.

    public static void drawModel(final float yaw, final float pitch, final EntityLivingBase entityLivingBase) {
        GlStateManager.resetColor();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, 0.0f, 50.0f);
        GlStateManager.scale(-50.0f, 50.0f, 50.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        final float renderYawOffset = entityLivingBase.renderYawOffset;
        final float rotationYaw = entityLivingBase.rotationYaw;
        final float rotationPitch = entityLivingBase.rotationPitch;
        final float prevRotationYawHead = entityLivingBase.prevRotationYawHead;
        final float rotationYawHead = entityLivingBase.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate((float) (-Math.atan(pitch / 40.0f) * 20.0), 1.0f, 0.0f, 0.0f);
        entityLivingBase.renderYawOffset = yaw - yaw / yaw * 0.4f;
        entityLivingBase.rotationYaw = yaw - yaw / yaw * 0.2f;
        entityLivingBase.rotationPitch = pitch;
        entityLivingBase.rotationYawHead = entityLivingBase.rotationYaw;
        entityLivingBase.prevRotationYawHead = entityLivingBase.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        final RenderManager renderManager = mc.getRenderManager();
        renderManager.setPlayerViewY(180.0f);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(entityLivingBase, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        renderManager.setRenderShadow(true);
        entityLivingBase.renderYawOffset = renderYawOffset;
        entityLivingBase.rotationYaw = rotationYaw;
        entityLivingBase.rotationPitch = rotationPitch;
        entityLivingBase.prevRotationYawHead = prevRotationYawHead;
        entityLivingBase.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.resetColor();
    }

    public static void skeetRect(final double x, final double y, final double x1, final double y1, final double size) {
        RenderUtil.rectangleBordered(x, y + -4.0, x1 + size, y1 + size, 0.5, new Color(60, 60, 60).getRGB(), new Color(10, 10, 10).getRGB());
        RenderUtil.rectangleBordered(x + 1.0, y + -3.0, x1 + size - 1.0, y1 + size - 1.0, 1.0, new Color(40, 40, 40).getRGB(), new Color(40, 40, 40).getRGB());
        RenderUtil.rectangleBordered(x + 2.5, y + -1.5, x1 + size - 2.5, y1 + size - 2.5, 0.5, new Color(40, 40, 40).getRGB(), new Color(60, 60, 60).getRGB());
        RenderUtil.rectangleBordered(x + 2.5, y + -1.5, x1 + size - 2.5, y1 + size - 2.5, 0.5, new Color(22, 22, 22).getRGB(), new Color(255, 255, 255, 0).getRGB());
    }

    public static void skeetRectSmall(final double x, final double y, final double x1, final double y1, final double size) {
        RenderUtil.rectangleBordered(x + 4.35, y + 0.5, x1 + size - 84.5, y1 + size - 4.35, 0.5, new Color(48, 48, 48).getRGB(), new Color(10, 10, 10).getRGB());
        RenderUtil.rectangleBordered(x + 5.0, y + 1.0, x1 + size - 85.0, y1 + size - 5.0, 0.5, new Color(17, 17, 17).getRGB(), new Color(255, 255, 255, 0).getRGB());
    }

    public static void rectangleBordered(final double x, final double y, final double x1, final double y1, final double width, final int internalColor, final int borderColor) {
        RenderUtil.rectangle(x + width, y + width, x1 - width, y1 - width, internalColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x + width, y, x1 - width, y + width, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x, y, x + width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x1 - width, y, x1, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.rectangle(x + width, y1 - width, x1 - width, y1, borderColor);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void rectangle(double left, double top, double right, double bottom, final int color) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        final float var11 = (float) (color >> 24 & 0xFF) / 255.0f;
        final float var6 = (float) (color >> 16 & 0xFF) / 255.0f;
        final float var7 = (float) (color >> 8 & 0xFF) / 255.0f;
        final float var8 = (float) (color & 0xFF) / 255.0f;
        final WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(left, bottom, 0.0).endVertex();
        worldRenderer.pos(right, bottom, 0.0).endVertex();
        worldRenderer.pos(right, top, 0.0).endVertex();
        worldRenderer.pos(left, top, 0.0).endVertex();
        Tessellator.getInstance().draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void renderEnchantText(ItemStack stack, int x, float y) {
        RenderHelper.disableStandardItemLighting();
        float enchantmentY = y + 24f;
        if (stack.getItem() instanceof ItemArmor) {
            int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            int thornLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack);
            if (protectionLevel > 0) {
                RenderUtil.drawEnchantTag("P" + ColorUtil.getColor(protectionLevel) + protectionLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                RenderUtil.drawEnchantTag("U" + ColorUtil.getColor(unbreakingLevel) + unbreakingLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (thornLevel > 0) {
                RenderUtil.drawEnchantTag("T" + ColorUtil.getColor(thornLevel) + thornLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemBow) {
            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
            int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (powerLevel > 0) {
                RenderUtil.drawEnchantTag("Pow" + ColorUtil.getColor(powerLevel) + powerLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (punchLevel > 0) {
                RenderUtil.drawEnchantTag("Pun" + ColorUtil.getColor(punchLevel) + punchLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (flameLevel > 0) {
                RenderUtil.drawEnchantTag("F" + ColorUtil.getColor(flameLevel) + flameLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                RenderUtil.drawEnchantTag("U" + ColorUtil.getColor(unbreakingLevel) + unbreakingLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
        }
        if (stack.getItem() instanceof ItemSword) {
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
            int knockbackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.knockback.effectId, stack);
            int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
            if (sharpnessLevel > 0) {
                RenderUtil.drawEnchantTag("S" +  ColorUtil.getColor(sharpnessLevel) + sharpnessLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (knockbackLevel > 0) {
                RenderUtil.drawEnchantTag("K" + ColorUtil.getColor(knockbackLevel) + knockbackLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (fireAspectLevel > 0) {
                RenderUtil.drawEnchantTag("F" + ColorUtil.getColor(fireAspectLevel) + fireAspectLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
            if (unbreakingLevel > 0) {
                RenderUtil.drawEnchantTag("U" + ColorUtil.getColor(unbreakingLevel) + unbreakingLevel, x * 2, enchantmentY);
                enchantmentY += 8;
            }
        }
        if (stack.getRarity() == EnumRarity.EPIC) {
            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            FontRenderer.drawOutlinedStringCock(Minecraft.getMinecraft().fontRendererObj, "God", x * 2, (int) enchantmentY, new Color(255, 255, 0).getRGB(), new Color(100, 100, 0, 200).getRGB());
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    private static void drawEnchantTag(String text, int x, float y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        FontRenderer.drawOutlinedStringCock(Minecraft.getMinecraft().fontRendererObj, text, x, (int) y, -1, new Color(0, 0, 0, 220).darker().getRGB());
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }
}
