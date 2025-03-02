package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.*;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.animation.advanced.impl.DecelerateAnimation;
import cn.stars.reversal.util.math.MathUtil;
import cn.stars.reversal.util.math.RandomUtil;
import cn.stars.reversal.util.math.TimeUtil;
import cn.stars.reversal.util.math.TimerUtil;
import cn.stars.reversal.util.render.ColorUtil;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.util.animation.advanced.Animation;
import cn.stars.reversal.util.animation.advanced.Direction;
import cn.stars.reversal.util.animation.advanced.impl.SmoothStepAnimation;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

@ModuleInfo(name = "TargetESP", chineseName = "敌人标记", description = "Display a ESP when you hit targets", chineseDescription = "当你攻击目标时渲染ESP", category = Category.RENDER)
public class TargetESP extends Module {
    public final ModeValue mode = new ModeValue("Mode", this, "Rectangle", "Rectangle", "Round", "Bubble", "Stars", "Surrounding");
    public final ColorValue colorValue = new ColorValue("Color", this);
    public final NumberValue surroundingSpeed = new NumberValue("Surrounding Speed", this, 2.0F, 1.0F, 5.0F, 0.1F);
    EntityLivingBase attackedEntity;
    private final Animation auraESPAnim = new SmoothStepAnimation(650, 1);
    static final ArrayList<Bubble> bubbles = new ArrayList<>();
    static final ArrayList<Bubble> stars = new ArrayList<>();
    private final Tessellator tessellator = Tessellator.getInstance();
    private final WorldRenderer buffer = this.tessellator.getWorldRenderer();
    public static final ResourceLocation STAR_TEXTURE = new ResourceLocation("reversal/images/texture/fireflies/star.png");
    public static final ResourceLocation BUBBLE_TEXTURE = new ResourceLocation("reversal/images/texture/targetesp/bubble.png");
    public static final ResourceLocation SURROUNDING_TEXTURE = new ResourceLocation("reversal/images/texture/targetesp/glow_circle.png");
    private final long lastTime = System.currentTimeMillis();
    private final Animation alphaAnim = new DecelerateAnimation(400, 1);
    private final TimeUtil drawTimer = new TimeUtil();

    @Override
    public void onUpdateAlwaysInGui() {
        surroundingSpeed.hidden = !mode.getMode().equals("Surrounding");
    }

    @Override
    public void onAttack(AttackEvent event) {
        if (event.getTarget() instanceof EntityLivingBase) {
            attackedEntity = (EntityLivingBase) event.getTarget();
            drawTimer.reset();
            if (attackedEntity.isEntityAlive()) {
                Vec3 to = attackedEntity.getPositionVector().addVector(0.0, attackedEntity.height / 1.6f, 0.0);
                addBubble(to);
                addStar(to, 3);
                addStar(to, 3);
            }
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (attackedEntity == null) return;
        switch (mode.getMode()) {
            case "Bubble": {
                float aPC = this.getAlphaPC();
                if ((double) aPC < 0.05) {
                    return;
                }
                if (bubbles.isEmpty()) {
                    return;
                }
                try {
                    this.setupDrawsBubbles3D(() -> bubbles.forEach(bubble -> {
                        if (bubble != null && bubble.getDeltaTime() <= 1.0f) {
                            this.drawBubble(bubble, aPC);
                        }
                    }));
                    this.removeAuto();
                } catch (ConcurrentModificationException ignored) {
                    // you can actually ignore this.
                }
                break;
            }
            case "Stars": {
                float aPC = this.getAlphaPC();
                if ((double) aPC < 0.05) {
                    return;
                }
                if (stars.isEmpty()) {
                    return;
                }
                try {
                    this.setupDrawsBubbles3D(() -> stars.forEach(star -> {
                        if (star != null && star.getDeltaTime() <= 1.0f) {
                            this.drawStar(star, aPC);
                        }
                    }));
                    this.removeAuto();
                } catch (ConcurrentModificationException ignored) {
                    // you can actually ignore this.
                }
                break;
            }
            case "Surrounding":
                if (attackedEntity.isDead || mc.thePlayer.getDistanceToEntity(attackedEntity) > 10) {
                    attackedEntity = null;
                    return;
                }
                GlStateManager.pushMatrix();
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.shadeModel(7425);
                GlStateManager.disableCull();
                GlStateManager.disableAlpha();
                GlStateManager.tryBlendFuncSeparate(770, 1, 0, 1);
                double radius = 0.67;
                float speed = 45;
                float size = 0.4f;
                double distance = 19;
                int lenght = 20;

                Vec3 interpolated = new Vec3(
                        attackedEntity.lastTickPosX + (attackedEntity.posX - attackedEntity.lastTickPosX) * event.getPartialTicks(),
                        attackedEntity.lastTickPosY + (attackedEntity.posY - attackedEntity.lastTickPosY) * event.getPartialTicks(),
                        attackedEntity.lastTickPosZ + (attackedEntity.posZ - attackedEntity.lastTickPosZ) * event.getPartialTicks());
                interpolated.yCoord += 0.75f;

                RenderUtil.setupOrientationMatrix(interpolated.xCoord, interpolated.yCoord + 0.5f, interpolated.zCoord);

                float[] idk = new float[]{mc.getRenderManager().playerViewY, mc.getRenderManager().playerViewX};

                GL11.glRotated(-idk[0], 0.0, 1.0, 0.0);
                GL11.glRotated(idk[1], 1.0, 0.0, 0.0);

                for (int i = 0; i < lenght; i++) {
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = Math.sin(angle) * radius;
                    double c = Math.cos(angle) * radius;
                    GlStateManager.translate(s, (c), -c);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    int color = ColorUtil.reAlpha(colorValue.getColor(i).getRGB(), (float) alphaAnim.getOutput());
                    RenderUtil.image(SURROUNDING_TEXTURE, 0f, 0f, -size, -size, color);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    GlStateManager.translate(-(s), -(c), (c));
                }
                for (int i = 0; i < lenght; i++) {
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = Math.sin(angle) * radius;
                    double c = Math.cos(angle) * radius;
                    GlStateManager.translate(-s, s, -c);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    int color = ColorUtil.reAlpha(colorValue.getColor(i).getRGB(), (float) alphaAnim.getOutput());
                    RenderUtil.image(SURROUNDING_TEXTURE, 0f, 0f, -size, -size, color);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    GlStateManager.translate((s), -(s), (c));
                }
                for (int i = 0; i < lenght; i++) {
                    double angle = 0.15f * (System.currentTimeMillis() - lastTime - (i * distance)) / (speed);
                    double s = Math.sin(angle) * radius;
                    double c = Math.cos(angle) * radius;
                    GlStateManager.translate(-(s), -(s), (c));
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    int color = ColorUtil.reAlpha(colorValue.getColor(i).getRGB(), (float) alphaAnim.getOutput());
                    RenderUtil.image(SURROUNDING_TEXTURE, 0f, 0f, -size, -size, color);
                    GlStateManager.translate(-size / 2f, -size / 2f, 0);
                    GlStateManager.translate(size / 2f, size / 2f, 0);
                    GlStateManager.translate((s), (s), -(c));
                }
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.disableBlend();
                GlStateManager.enableCull();
                GlStateManager.enableAlpha();
                GlStateManager.depthMask(true);
                GlStateManager.popMatrix();
                break;
            default:
                // No null pointer anymore
                auraESPAnim.setDirection(!(attackedEntity.isDead || mc.thePlayer.getDistanceToEntity(attackedEntity) > 10 || drawTimer.hasReached(4350)) ? Direction.FORWARDS : Direction.BACKWARDS);
                if (auraESPAnim.finished(Direction.BACKWARDS)) {
                    attackedEntity = null;
                    return;
                }
                Color color = colorValue.getColor(1);
                Color color2 = colorValue.getColor(2);
                float dst = mc.thePlayer.getSmoothDistanceToEntity(attackedEntity);
                Vector2f vector2f = RenderUtil.targetESPSPos(attackedEntity, event.getPartialTicks());
                if (vector2f == null) return;
                RenderUtil.drawTargetESP2D(vector2f.x, vector2f.y, color, color2, 1.0f - MathHelper.clamp_float(Math.abs(dst - 6.0f) / 60.0f, 0.0f, 0.75f), 1, (float) auraESPAnim.getOutput());
                break;
        }
        if (drawTimer.hasReached(5000)) {
            attackedEntity = null;
        }
    }

    @Override
    public void onWorld(WorldEvent event) {
        drawTimer.reset();
        attackedEntity = null;
    }

    private static void addStar(Vec3 addToCoord, int bubbleCount) {
        float angleStep = 360.0f / bubbleCount;

        for (int i = 0; i < bubbleCount; i++) {
            float viewYaw = i * angleStep;

            float viewPitch = (float) Math.asin(2 * (i / (float) bubbleCount) - 1) * (180 / (float) Math.PI);

            stars.add(new Bubble(viewYaw, viewPitch, addToCoord));
        }
    }

    private static void addBubble(Vec3 addToCoord) {
        RenderManager manager = mc.getRenderManager();
        bubbles.add(new Bubble(manager.playerViewX, -manager.playerViewY, addToCoord));
    }


    private void setupDrawsBubbles3D(Runnable render) {
        RenderManager manager = mc.getRenderManager();
        Vec3 conpense = new Vec3(manager.renderPosX, manager.renderPosY, manager.renderPosZ);
        boolean light = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GlStateManager.pushMatrix();
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        if (light)
            GlStateManager.disableLighting();
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.tryBlendFuncSeparate(770, 32772, 1, 0);
        GL11.glTranslated(-conpense.xCoord, -conpense.yCoord, -conpense.zCoord);
        render.run();

        GL11.glTranslated(conpense.xCoord, conpense.yCoord, conpense.zCoord);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.resetColor();
        GL11.glShadeModel(GL11.GL_FLAT);
        if (light)
            GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
    }

    private void drawBubble(Bubble bubble, float alphaPC) {
        GL11.glPushMatrix();

        mc.getTextureManager().bindTexture(BUBBLE_TEXTURE);

        GL11.glTranslated(bubble.pos.xCoord, bubble.pos.yCoord, bubble.pos.zCoord);

        float extS = bubble.getDeltaTime();
        GlStateManager.translate(-Math.sin(Math.toRadians(bubble.viewPitch)) * (double) extS / 3.0,
                Math.sin(Math.toRadians(bubble.viewYaw)) * (double) extS / 2.0,
                -Math.cos(Math.toRadians(bubble.viewPitch)) * (double) extS / 3.0);

        GL11.glNormal3d(1.0, 1.0, 1.0);
        GL11.glRotated(bubble.viewPitch, 0.0, 1.0, 0.0);
        GL11.glRotated(bubble.viewYaw, mc.gameSettings.thirdPersonView == 2 ? -1.0 : 1.0, 0.0, 0.0);
        GL11.glScaled(-0.1, -0.1, 0.1);

        this.drawBeginsNullCoord(bubble, alphaPC);

        GL11.glPopMatrix();
    }

    private void drawStar(Bubble bubble, float alphaPC) {
        GL11.glPushMatrix();

        mc.getTextureManager().bindTexture(STAR_TEXTURE);

        // gravity
        bubble.updatePosition();

        GL11.glTranslated(bubble.pos.xCoord, bubble.pos.yCoord, bubble.pos.zCoord);

        float extS = bubble.getDeltaTime();
        GlStateManager.translate(-Math.sin(Math.toRadians(bubble.viewPitch)) * (double) extS / 3.0,
                Math.sin(Math.toRadians(bubble.viewYaw)) * (double) extS / 2.0,
                -Math.cos(Math.toRadians(bubble.viewPitch)) * (double) extS / 3.0);

        GL11.glNormal3d(1.0, 1.0, 1.0);
        GL11.glRotated(bubble.viewPitch, 0.0, 1.0, 0.0);
        GL11.glRotated(bubble.viewYaw, mc.gameSettings.thirdPersonView == 2 ? -1.0 : 1.0, 0.0, 0.0);
        GL11.glScaled(-0.1, -0.1, 0.1);

        this.drawBeginsNullCoord(bubble, alphaPC);

        GL11.glPopMatrix();
    }

    private void drawBeginsNullCoord(Bubble bubble, float alphaPC) {
        float r = 25.0f * bubble.getDeltaTime() * (1.0f - bubble.getDeltaTime());
        int speedRotate = 3;
        float III = (float) (System.currentTimeMillis() % (long) (3600 / speedRotate)) / 10.0f * (float) speedRotate;
        RenderUtil.customRotatedObject2D(-1.0f, -1.0f, 2.0f, 2.0f, -III);
        this.buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        this.buffer.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).color(ColorUtil.reAlpha(colorValue.getColor().getRGB(), alphaPC)).endVertex();
        this.buffer.pos(0.0, r, 0.0).tex(0.0, 1.0).color(ColorUtil.reAlpha(colorValue.getColor().getRGB(), alphaPC)).endVertex();
        this.buffer.pos(r, r, 0.0).tex(1.0, 1.0).color(ColorUtil.reAlpha(colorValue.getColor().getRGB(), alphaPC)).endVertex();
        this.buffer.pos(r, 0.0, 0.0).tex(1.0, 0.0).color(ColorUtil.reAlpha(colorValue.getColor().getRGB(), alphaPC)).endVertex();
        GlStateManager.blendFunc(770, 772);
        GlStateManager.translate(-r / 2.0f, -r / 2.0f, 0.0f);
        GlStateManager.shadeModel(7425);
        GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
        this.tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.translate(r / 2.0f, r / 2.0f, 0.0f);
        GlStateManager.blendFunc(770, 771);
    }

    private void removeAuto() {
        bubbles.removeIf(bubble -> bubble.getDeltaTime() >= 1.0f);
    }

    private float getAlphaPC() {
        return 1f;
    }

    private static float getMaxTime() {
        return 1000.0f;
    }

    private static final class Bubble {
        Vec3 pos;
        long time = System.currentTimeMillis();
        float maxTime = getMaxTime();
        float viewYaw;
        float viewPitch;

        // 新增的属性
        double initialUpwardSpeed = 0.006;
        double fallSpeed = 0.0001;
        double horizontalSpeed = 0.01;

        double velocityY = initialUpwardSpeed;
        double velocityX;
        double velocityZ;

        boolean isRising = true;
        boolean oppositeX;
        boolean oppositeZ;

        public Bubble(float viewYaw, float viewPitch, Vec3 pos) {
            this.viewYaw = viewYaw;
            this.viewPitch = viewPitch;
            this.pos = pos;
            this.oppositeX = RandomUtil.INSTANCE.nextBoolean();
            this.oppositeZ = RandomUtil.INSTANCE.nextBoolean();

            this.initialUpwardSpeed *= RandomUtil.INSTANCE.nextDouble(0.8, 1.2) / Minecraft.getDebugFPS() * 200;
            this.fallSpeed *= RandomUtil.INSTANCE.nextDouble(0.5, 0.8) / Minecraft.getDebugFPS() * 200;
            this.horizontalSpeed *= RandomUtil.INSTANCE.nextDouble(0.4, 0.8) / Minecraft.getDebugFPS() * 200;

            this.velocityX = Math.sin(Math.toRadians(viewYaw)) * horizontalSpeed;
            this.velocityZ = Math.cos(Math.toRadians(viewYaw)) * horizontalSpeed;
        }

        private float getDeltaTime() {
            return (float) (System.currentTimeMillis() - this.time) / this.maxTime;
        }

        public void updatePosition() {
            if (isRising) {
                pos.yCoord += velocityY;
                velocityY -= fallSpeed;

                if (velocityY <= 0) {
                    isRising = false;
                }
            } else {
                pos.yCoord -= velocityY;
                velocityY += fallSpeed;
            }

            if (oppositeX) {
                pos.xCoord -= velocityX;
            } else {
                pos.xCoord += velocityX;
            }
            if (oppositeZ) {
                pos.zCoord -= velocityZ;
            } else {
                pos.zCoord += velocityZ;
            }
        }
    }

}
