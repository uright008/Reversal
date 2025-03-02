package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.event.impl.UpdateEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
;
import cn.stars.reversal.util.math.MathUtil;
import cn.stars.reversal.util.render.*;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@ModuleInfo(name = "JumpCircle", chineseName = "跳跃圆圈", description = "Draw a circle when you jump", chineseDescription = "在你跳跃时画圆圈", category = Category.RENDER)
public class JumpCircle extends Module {
    private final ModeValue modeValue = new ModeValue("Mode", this, "Round", "Round", "Modern");
    public final ColorValue colorValue = new ColorValue("Color", this);
    private final ModeValue modernTextureValue = new ModeValue("Modern Texture", this, "Leeches", "KonchalEbal", "CubicalPieces", "Leeches", "Circle");
    private final NumberValue maxTime = new NumberValue("Max Time", this, 3000, 2000, 8000, 25);
    private final BoolValue deepestLight = new BoolValue("Deepest Light", this, true);
    private final NumberValue radiusValue = new NumberValue("Radius", this, 3, 1, 5, 1);
    private final NumberValue widthValue = new NumberValue("Width", this, 0.5F, 0.1F, 50F, 1);
    private final NumberValue strengthValue = new NumberValue("Strength", this, 0.02F, 0.01F, 0.2F, 0.01f);
    private final CopyOnWriteArrayList<Circle> circles = new CopyOnWriteArrayList<>();
    private final static List<JumpRenderer> modernCircles = new java.util.ArrayList<>();
    private boolean lastOnGround;
    public final String staticLoc = "reversal/images/texture/jumpcircles/default/", animatedLoc = "reversal/images/texture/jumpcircles/animated/";
    private final ResourceLocation JUMP_CIRCLE = new ResourceLocation(staticLoc + "circle.png");
    private final ResourceLocation JUMP_KONCHAL = new ResourceLocation(staticLoc + "konchal.png");
    public List<ArrayList> animatedGroups = Arrays.asList(new ArrayList<>(), new ArrayList<>());

    @Override
    public void onUpdateAlwaysInGui() {
        modernTextureValue.hidden = !modeValue.getMode().equals("Modern");
        maxTime.hidden = !modeValue.getMode().equals("Modern");
        deepestLight.hidden = !modeValue.getMode().equals("Modern");
    }

    @Override
    public void onEnable(){
        lastOnGround = true;
    }

    @Override
    public void onUpdate(UpdateEvent event) {
        if (mc.thePlayer.onGround && !lastOnGround) {
            if (modeValue.getMode().equals("Round")) {
                circles.add(new Circle(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ, (float) widthValue.getValue()));
            } else {
                addCircleForEntity(mc.thePlayer);
            }
        }
        lastOnGround = mc.thePlayer.onGround;
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if (modeValue.getMode().equals("Round")) {
            if (!modernCircles.isEmpty()) {
                drawRoundJumpCircle();
            }
        } else {
            if (modernCircles.isEmpty()) return;
            modernCircles.removeIf((final JumpRenderer circle) -> circle.getDeltaTime() >= 1.D);
            if (modernCircles.isEmpty()) return;
            float deepestLightAnim = deepestLight.isEnabled() ? 1 : 0, immersiveStrengh = 0;
            if (deepestLightAnim >= 1.F / 255.F) {
                switch (modernTextureValue.getMode()) {
                    case "Circle":
                    case "Emission":
                        immersiveStrengh = .1F;
                        break;
                    case "KonchalEbal":
                    case "CubicalPieces":
                    case "Inusual":
                        immersiveStrengh = .075F;
                        break;
                    case "Leeches":
                        immersiveStrengh = .2F;
                        break;
                }
            }
            float finalImmersiveStrengh = immersiveStrengh;
            setupDraw(() -> modernCircles.forEach(circle -> drawModernJumpCircle(circle.pos, radiusValue.getValue(), 1.F - circle.getDeltaTime(), circle.getIndex() * 30, deepestLightAnim, finalImmersiveStrengh)));
        }
    }

    private void drawModernJumpCircle(final Vec3 pos, double maxRadius, float deltaTime, int index, float immersiveShift, float immersiveIntense) {
        boolean immersive = immersiveShift >= 1.F / 255.F;
        float waveDelta = valWave01(1.F - deltaTime);
        float alphaPC = (float) easeOutCirc(valWave01(1 - deltaTime));
        if (deltaTime < .5F) alphaPC *= (float) easeInOutExpo(alphaPC);
        float radius = (float) ((deltaTime > .5F ? easeOutElastic(waveDelta * waveDelta) : easeOutBounce(waveDelta)) * maxRadius);
        double rotate = easeInOutElastic(waveDelta) * 90.D / (1.D + waveDelta);
        ResourceLocation res = jumpTexture(index, deltaTime);
        mc.getTextureManager().bindTexture(res);
        mc.getTextureManager().getTexture(res).setBlurMipmap(true, true);
        GlStateManager.pushMatrix();
        GlStateManager.translate(pos.xCoord - radius / 2.D, pos.yCoord, pos.zCoord - radius / 2.D);
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        RenderUtil.customRotatedObject2D(0, 0, radius, radius, (float) rotate);
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        worldRenderer.pos(0, 0, 0).tex(0, 0).color(getColor(alphaPC)).endVertex();
        worldRenderer.pos(0, radius, 0).tex(0, 1).color(getColor(alphaPC)).endVertex();
        worldRenderer.pos(radius, radius, 0).tex(1, 1).color(getColor(alphaPC)).endVertex();
        worldRenderer.pos(radius, 0, 0).tex(1, 0).color(getColor(alphaPC)).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        if (immersive) {
            int[] colors = new int[4];
            colors[0] = getColor(alphaPC);
            colors[1] = getColor(alphaPC);
            colors[2] = getColor(alphaPC);
            colors[3] = getColor(alphaPC);
            GlStateManager.pushMatrix();
            GlStateManager.translate(pos.xCoord, pos.yCoord, pos.zCoord);
            GL11.glRotated(rotate, 0.0f, 1.0f, 0.0f);
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
            float polygons = 40, extMaxY = radius / 3.5F, extMaxXZ = radius / 7.F, minAPC = immersiveIntense * immersiveShift;
            float aPC;
            for (int i = 1; i < (int) polygons; i++) {
                float iPC = i / polygons, extY = extMaxY * i / polygons - extMaxY / polygons;
                if ((aPC = MathUtil.lerp(alphaPC * minAPC, 0, iPC)) * 255 < 1) continue;
                float radiusPost = radius + (float) easeOutCirc(valWave01(iPC - 1.5F / polygons)) * extMaxXZ;
                worldRenderer.pos(-radiusPost / 2.F, extY, -radiusPost / 2.F).tex(0, 0).color(ColorUtil.darker(colors[0], aPC)).endVertex();
                worldRenderer.pos(-radiusPost / 2.F, extY, radiusPost / 2.F).tex(0, 1).color(ColorUtil.darker(colors[1], aPC)).endVertex();
                worldRenderer.pos(radiusPost / 2.F, extY, radiusPost / 2.F).tex(1, 1).color(ColorUtil.darker(colors[2], aPC)).endVertex();
                worldRenderer.pos(radiusPost / 2.F, extY, -radiusPost / 2.F).tex(1, 0).color(ColorUtil.darker(colors[3], aPC)).endVertex();
            }
            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    private void drawRoundJumpCircle() {
        for (Circle circle : circles) {
            if (circle.add(strengthValue.getValue()) > radiusValue.getValue()) {
                circles.remove(circle);
                continue;
            }
            GL11.glPushMatrix();
            GL11.glTranslated(
                    circle.posX - mc.getRenderManager().renderPosX,
                    circle.posY - mc.getRenderManager().renderPosY,
                    circle.posZ - mc.getRenderManager().renderPosZ
            );
            GlStateManager.enableBlend();
            GlStateManager.enableLineSmooth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

            GL11.glLineWidth(circle.width / 2f);
            GL11.glRotatef(90F, 1F, 0F, 0F);

            GL11.glBegin(GL11.GL_LINE_STRIP);

            for (int i = 0; i <= 360; i += 5) { // You can change circle accuracy  (60 - accuracy)
                RenderUtils.color(colorValue.getColor(i / 5));
                GL11.glVertex2f(
                        (float) (Math.cos(i * Math.PI / 180.0) * circle.radius),
                        (float) (Math.sin(i * Math.PI / 180.0) * circle.radius)
                );
            }

            GL11.glEnd();

            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.disableLineSmooth();
            GL11.glPopMatrix();
        }
    }

    static class Circle {
        public double posX, posY, posZ, lastTickPosX, lastTickPosY, lastTickPosZ;
        public float radius, width;
        Circle(double posX, double posY, double posZ, double lastTickPosX, double lastTickPosY, double lastTickPosZ, float width) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.lastTickPosX = lastTickPosX;
            this.lastTickPosY = lastTickPosY;
            this.lastTickPosZ = lastTickPosZ;
            this.width = width;
        }
        public double add(double radius) {
            this.radius += radius;
            return this.radius;
        }
    }

    private ResourceLocation jumpTexture(int index, float progress) {
        if (modernTextureValue.getMode().equals("CubicalPieces") || modernTextureValue.getMode().equals("Leeches")) {
            ArrayList currentGroupTextures = modernTextureValue.getMode().equals("CubicalPieces") ? animatedGroups.get(0) : animatedGroups.get(1);
            final boolean animateByProgress = modernTextureValue.getMode().equals("Leeches");
            if (modernTextureValue.getMode().equals("Leeches")) {
                progress += .6F;
            }
            float frameOffset01 = progress % 1F;
            if (!animateByProgress) {
                final int ms = 1500;
                frameOffset01 = ((System.currentTimeMillis() + index) % ms) / (float) ms;
            }
            return (ResourceLocation) currentGroupTextures.get((int) Math.min(frameOffset01 * (currentGroupTextures.size() - .5F), currentGroupTextures.size()));
        } else {
            return modernTextureValue.getMode().equals("Circle") ? JUMP_CIRCLE : JUMP_KONCHAL;
        }
    }

    public void initResources() {
        ResourceLocation loc;
        final int[] groupsFramesLength = new int[]{100/*, 60*/, 200};
        final String[] groupsFramesFormat = new String[]{"jpeg", /*"png", */"png"};
        int groupIndex = groupsFramesLength.length - 1;
        if (animatedGroups.stream().allMatch(ArrayList::isEmpty)) {
            while (groupIndex >= 0) {
                int framesCounter = 0;
                while (framesCounter < groupsFramesLength[groupIndex]) {
                    framesCounter++;
                    loc = new ResourceLocation(animatedLoc + ("animation" + (groupIndex + 1)) + ("/circleframe_" + framesCounter) + ("." + groupsFramesFormat[groupIndex]));
                    animatedGroups.get(groupIndex).add(loc);
                }
                --groupIndex;
            }
        }
    }

    private void addCircleForEntity(final Entity entity) {
        Vec3 vec = getVec3dFromEntity(entity).add(new Vec3(0.D, .005D, 0.D));
        BlockPos pos = new BlockPos(vec);
        IBlockState state = mc.theWorld.getBlockState(pos);
        if (state.getBlock() == Blocks.snow) {
            vec = vec.add(new Vec3(0.D, .125D, 0.D));
        }
        modernCircles.add(new JumpRenderer(vec, circles.size()));
    }

    private void reset() {
        if (!circles.isEmpty()) circles.clear();
    }

    private static Vec3 getVec3dFromEntity(final Entity entityIn) {
        final float PT = mc.timer.renderPartialTicks;
        final double dx = entityIn.posX - entityIn.lastTickPosX, dy = entityIn.posY - entityIn.lastTickPosY, dz = entityIn.posZ - entityIn.lastTickPosZ;
        return new Vec3((entityIn.lastTickPosX + dx * PT + dx * 2.D), (entityIn.lastTickPosY + dy * PT), (entityIn.lastTickPosZ + dz * PT + dz * 2.D));
    }

    private void setupDraw(final Runnable render) {
        final boolean light = GL11.glIsEnabled(GL11.GL_LIGHTING);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        if (light) GlStateManager.disableLighting();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);

        //GL_ONE_MINUS_CONSTANT_ALPHA
        GlStateManager.blendFunc(770, 1);
        RenderUtil.setupOrientationMatrix(0, 0, 0);
        render.run();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color(1.F, 1.F, 1.F);
        GlStateManager.shadeModel(GL11.GL_FLAT);
        if (light) GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.alphaFunc(GL11.GL_GREATER, .1F);
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    private int getColor(float alphaPC) {
        int colorize = colorValue.getColor().getRGB();
        return ColorUtil.getOverallColorFrom(colorize, new Color(255, 255, 255, (int) (255.F * alphaPC)).getRGB(), .1F);
    }

    private final Tessellator tessellator = Tessellator.getInstance();
    private final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

    private final class JumpRenderer {
        private final long time = System.currentTimeMillis();
        private final Vec3 pos;
        int index;

        private JumpRenderer(Vec3 pos, int index) {
            this.pos = pos;
            this.index = index;
        }

        private float getDeltaTime() {
            return (System.currentTimeMillis() - time) / maxTime.getFloat();
        }

        private int getIndex() {
            return this.index;
        }
    }

    public static double easeOutBounce(double value) {
        double n1 = 7.5625, d1 = 2.75;
        if (value < 1 / d1) {
            return n1 * value * value;
        } else if (value < 2 / d1) {
            return n1 * (value -= 1.5 / d1) * value + 0.75;
        } else if (value < 2.5 / d1) {
            return n1 * (value -= 2.25 / d1) * value + 0.9375;
        } else {
            return n1 * (value -= 2.625 / d1) * value + 0.984375;
        }
    }

    public static double easeInOutElastic(double value) {
        double c5 = (2 * Math.PI) / 4.5;
        return value < 0 ? 0 : value > 1 ? 1 : value < 0.5 ? -(Math.pow(2, 20 * value - 10) * Math.sin((20 * value - 11.125) * c5)) / 2 : (Math.pow(2, -20 * value + 10) * Math.sin((20 * value - 11.125) * c5)) / 2 + 1;
    }

    public static double easeOutElastic(double value) {
        double c4 = (2 * Math.PI) / 3;
        return value < 0 ? 0 : value > 1 ? 1 : Math.pow(2, -10 * value) * Math.sin((value * 10 - 0.75) * c4) + 1;
    }

    public static float valWave01(float value) {
        return (value > .5 ? 1 - value : value) * 2.F;
    }

    public static double easeOutCirc(double value) {
        return Math.sqrt(1 - Math.pow(value - 1, 2));
    }

    public static double easeInOutExpo(double value) {
        return value < 0 ? 0 : value > 1 ? 1 : value < 0.5 ? Math.pow(2, 20 * value - 10) / 2 : (2 - Math.pow(2, -20 * value + 10)) / 2;
    }

    public static float lerp(float value, float to, float pc) {
        return value + pc * (to - value);
    }
}