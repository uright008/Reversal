package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.Render3DEvent;
import cn.stars.reversal.event.impl.UpdateEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.module.impl.render.lineglyphs.GlyphVecRenderer;
import cn.stars.reversal.util.animation.simple.AnimationUtils2;
import cn.stars.reversal.util.math.MathUtil;
import cn.stars.reversal.util.math.RandomUtil;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ColorValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ModuleInfo(name = "LineGlyphs", localizedName = "线条图像", description = "Draw lines in world", localizedDescription = "在世界中绘制线条", category = Category.RENDER)
public class LineGlyphs extends Module {
    public final ColorValue colorValue = new ColorValue("Color", this);
    public final BoolValue SlowSpeed = new BoolValue("Slow Speed", this, false);
    public final NumberValue GlyphsCount = new NumberValue("Glyphs Count", this, 70, 0, 200, 1);
    private final List<Vec3> temp3dVecs = new ArrayList<>();
    public static final Tessellator tessellator = Tessellator.getInstance();
    private final List<GlyphsVecGen> Glyphs_VEC_GENS = new ArrayList<>();

    @Override
    public void onUpdate(UpdateEvent event) {
        this.glyphsUpdate();
        this.addAllGlyphs(this.maxObjCount());
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        this.glyphsRemoveAuto();
        this.drawAllGlyphs(event.getPartialTicks());
    }

    private int[] lineMoveSteps() {
        return new int[]{0, 3};
    }

    private int[] lineStepsAmount() {
        return new int[]{7, 12};
    }

    private int[] spawnRanges() {
        return new int[]{6, 24, 0, 12};
    }

    private int maxObjCount() {
        return this.GlyphsCount.getInt();
    }

    private int getR360X() {
        return RandomUtil.INSTANCE.nextInt(0, 4) * 90;
    }

    private int getR360Y() {
        return RandomUtil.INSTANCE.nextInt(-2, 2) * 90;
    }

    private int[] getR360XY() {
        return new int[]{RandomUtil.INSTANCE.nextInt(0, 4) * 90, RandomUtil.INSTANCE.nextInt(-1, 1) * 90};
    }

    private int[] getA90R(int[] outdated) {
        int maxAttempt;
        int b;
        int a;
        int ao = a = outdated[0];
        int bo = b = outdated[1];
        for (maxAttempt = 150; maxAttempt > 0 && Math.abs(b - bo) != 90; --maxAttempt) {
            b = this.getR360Y();
        }
        for (maxAttempt = 5; maxAttempt > 0 && (Math.abs(a - ao) != 90 || Math.abs(a - ao) != 270); --maxAttempt) {
            a = this.getR360X();
        }
        return new int[]{a, b};
    }

    private Vec3i offsetFromRXYR(Vec3i vec3i, int[] rxy, int r) {
        double yawR = Math.toRadians(rxy[0]);
        double pitchR = Math.toRadians(rxy[1]);
        double r1 = r;
        int ry = (int) (Math.sin(pitchR) * r1);
        if (pitchR != 0.0) {
            r1 = 0.0;
        }
        int rx = (int) (-(Math.sin(yawR) * r1));
        int rz = (int) (Math.cos(yawR) * r1);
        int xi = vec3i.getX() + rx;
        int yi = vec3i.getY() + ry;
        int zi = vec3i.getZ() + rz;
        return new Vec3i(xi, yi, zi);
    }

    private float moveAdvanceFromTicks(int ticksSet, int ticksExpiring, float pTicks) {
        return Math.min(Math.max(1.0f - ((float) ticksExpiring - pTicks) / (float) ticksSet, 0.0f), 1.0f);
    }

    private List<Vec3> getSmoothTickedFromList(List<Vec3i> vec3is, float moveAdvance) {
        if (!this.temp3dVecs.isEmpty()) {
            this.temp3dVecs.clear();
        }
        for (Vec3i vec3i : vec3is) {
            double x = vec3i.getX();
            double y = vec3i.getY();
            double z = vec3i.getZ();
            if (!vec3is.isEmpty() && vec3i == vec3is.get(vec3is.size() - 1)) {
                Vec3i prevVec3i = vec3is.get(vec3is.size() - 2);
                x = MathUtil.lerp(moveAdvance, prevVec3i.getX(), x);
                y = MathUtil.lerp(moveAdvance, prevVec3i.getY(), y);
                z = MathUtil.lerp(moveAdvance, prevVec3i.getZ(), z);
            }
            this.temp3dVecs.add(new Vec3(x, y, z));
        }
        return this.temp3dVecs;
    }

    private Vec3i randGlyphsSpawnPos() {
        int[] spawnRanges = this.spawnRanges();
        double dst = RandomUtil.INSTANCE.nextInt(spawnRanges[0], spawnRanges[1]);
        double fov = LineGlyphs.mc.gameSettings.fovSetting;
        double radianYaw = Math.toRadians(RandomUtil.INSTANCE.nextInt((int) ((double) mc.thePlayer.rotationYaw - fov * 0.75), (int) ((double) mc.thePlayer.rotationYaw + fov * 0.75)));
        int randXOff = (int) (-(Math.sin(radianYaw) * dst));
        int randYOff = RandomUtil.INSTANCE.nextInt(-spawnRanges[2], spawnRanges[3]);
        int randZOff = (int) (Math.cos(radianYaw) * dst);
        return new Vec3i(mc.getRenderManager().viewerPosX + (double) randXOff, mc.getRenderManager().viewerPosY + (double) randYOff, mc.getRenderManager().viewerPosZ + (double) randZOff);
    }

    private void addAllGlyphs(int countCap) {
        for (int maxAttempt = 8; maxAttempt > 0 && this.Glyphs_VEC_GENS.stream().filter(GlyphsVecGen -> GlyphsVecGen.alphaPC.to != 0.0f).count() < (long) countCap; --maxAttempt) {
            int[] lineStepsAmount = this.lineStepsAmount();
            while (this.Glyphs_VEC_GENS.size() < countCap) {
                Vec3i pos = this.randGlyphsSpawnPos();
                this.Glyphs_VEC_GENS.add(new GlyphsVecGen(pos, RandomUtil.INSTANCE.nextInt(lineStepsAmount[0], lineStepsAmount[1])));
            }
        }
    }

    private void glyphsRemoveAuto() {
        this.Glyphs_VEC_GENS.removeIf(GlyphsVecGen -> GlyphsVecGen.isToRemove(1));
    }

    private void glyphsUpdate() {
        if (!this.Glyphs_VEC_GENS.isEmpty()) {
            this.Glyphs_VEC_GENS.forEach(GlyphsVecGen::update);
        }
    }

    private void drawAllGlyphs(float pTicks) {
        if (this.Glyphs_VEC_GENS.isEmpty()) {
            return;
        }
        List<GlyphsVecGen> filteredGens = this.Glyphs_VEC_GENS.stream().filter(GlyphsVecGen -> GlyphsVecGen.getAlphaPC() * 255.0f >= 1.0f).collect(Collectors.toList());
        if (filteredGens.isEmpty()) {
            return;
        }
        GlyphVecRenderer.set3DRendering(() -> {
            int colorIndex = 0;
            for (GlyphsVecGen filteredGen : filteredGens) {
                GlyphVecRenderer.clientColoredBegin(filteredGen, ++colorIndex, filteredGen.alphaPC.anim, pTicks);
            }
        });
    }

    public class GlyphsVecGen {
        public final List<Vec3i> vecGens = new ArrayList<>();
        private int currentStepTicks;
        private int lastStepSet;
        private int stepsAmount;
        private int[] lastYawPitch;
        private final AnimationUtils2 alphaPC = new AnimationUtils2(0.1f, 1.0f, 0.075f);

        public GlyphsVecGen(Vec3i spawnPos, int maxStepsAmount) {
            this.vecGens.add(spawnPos);
            this.lastYawPitch = LineGlyphs.this.getR360XY();
            this.stepsAmount = maxStepsAmount;
        }

        private void update() {
            if (this.stepsAmount == 0) {
                this.alphaPC.to = 0.0f;
            }
            if (this.currentStepTicks > 0) {
                this.currentStepTicks -= LineGlyphs.this.SlowSpeed.isEnabled() ? 1 : 2;
                if (this.currentStepTicks < 0) {
                    this.currentStepTicks = 0;
                }
                return;
            }
            this.lastYawPitch = LineGlyphs.this.getA90R(this.lastYawPitch);
            this.lastStepSet = this.currentStepTicks = RandomUtil.INSTANCE.nextInt(LineGlyphs.this.lineMoveSteps()[0], LineGlyphs.this.lineMoveSteps()[1]);
            this.vecGens.add(LineGlyphs.this.offsetFromRXYR(this.vecGens.get(this.vecGens.size() - 1), this.lastYawPitch, this.currentStepTicks));
            --this.stepsAmount;
        }

        public List<Vec3> getPosVectors(float pTicks) {
            return LineGlyphs.this.getSmoothTickedFromList(this.vecGens, LineGlyphs.this.moveAdvanceFromTicks(this.lastStepSet, this.currentStepTicks, pTicks));
        }

        public float getAlphaPC() {
            return MathHelper.clamp_float(this.alphaPC.getAnim(), 0.0f, 1.0f);
        }

        public boolean isToRemove(float moduleAlphaPC) {
            return moduleAlphaPC * (this.alphaPC.to == 0.0f ? this.getAlphaPC() : 1.0f) * 255.0f < 1.0f;
        }
    }
}
