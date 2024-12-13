package cn.stars.reversal;

import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.font.MFont;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.value.Value;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public interface GameInstance {
    Minecraft mc = Minecraft.getMinecraft();

    MFont gs = FontManager.getPSR(18);
    MFont gsTitle = FontManager.getPSR(24);

    MFont psr16 = FontManager.getPSR(16);
    MFont psm16 = FontManager.getPSM(16);
    MFont psm17 = FontManager.getPSM(17);
    MFont psb20 = FontManager.getPSB(20);
    MFont psm18 = FontManager.getPSM(18);
    MFont regular16 = FontManager.getRegular(16);
    MFont regular18 = FontManager.getRegular(18);
    MFont regular20 = FontManager.getRegular(20);
    MFont regular20Bold = FontManager.getRegularBold(20);
    MFont regular24Bold = FontManager.getRegularBold(24);
    MFont regular32 = FontManager.getRegular(32);

    List<Runnable> UI_BLOOM_RUNNABLES = new ArrayList<>();
    List<Runnable> UI_POST_BLOOM_RUNNABLES = new ArrayList<>();
    List<Runnable> UI_RENDER_RUNNABLES = new ArrayList<>();
    List<Runnable> NORMAL_PRE_RENDER_RUNNABLES = new ArrayList<>();
    List<Runnable> NORMAL_BLUR_RUNNABLES = new ArrayList<>();
    List<Runnable> NORMAL_POST_BLOOM_RUNNABLES = new ArrayList<>();
    List<Runnable> NORMAL_OUTLINE_RUNNABLES = new ArrayList<>();
    List<Runnable> NORMAL_RENDER_RUNNABLES = new ArrayList<>();
    List<Runnable> NORMAL_POST_RENDER_RUNNABLES = new ArrayList<>();

    List<Runnable> MODERN_BLUR_RUNNABLES = new ArrayList<>();
    List<Runnable> MODERN_BLOOM_RUNNABLES = new ArrayList<>();

    static void render2DRunnables(float partialTicks, boolean shaders) {

        NORMAL_PRE_RENDER_RUNNABLES.forEach(Runnable::run);

        if (shaders) {
            RiseShaders.OUTLINE_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_OUTLINE_RUNNABLES);
            RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_BLUR_RUNNABLES);
        }

        NORMAL_RENDER_RUNNABLES.forEach(Runnable::run);

        NORMAL_POST_RENDER_RUNNABLES.forEach(Runnable::run);

        if (shaders) {
            RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_POST_BLOOM_RUNNABLES);
        }

        UI_RENDER_RUNNABLES.forEach(Runnable::run);

        if (mc.currentScreen != null) {

            RiseShaders.UI_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, UI_BLOOM_RUNNABLES);

            RiseShaders.UI_POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, UI_POST_BLOOM_RUNNABLES);
        }

    }

    static void render3DRunnables(float partialTicks) {
        RiseShaders.OUTLINE_SHADER.run(ShaderRenderType.CAMERA, partialTicks, NORMAL_OUTLINE_RUNNABLES);
        RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.CAMERA, partialTicks, NORMAL_POST_BLOOM_RUNNABLES);
        RiseShaders.UI_BLOOM_SHADER.run(ShaderRenderType.CAMERA, partialTicks, UI_BLOOM_RUNNABLES);
        RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.CAMERA, partialTicks, NORMAL_BLUR_RUNNABLES);
    }

    static void clearRunnables() {
        NORMAL_BLUR_RUNNABLES.clear();
        NORMAL_POST_BLOOM_RUNNABLES.clear();
        NORMAL_OUTLINE_RUNNABLES.clear();
        NORMAL_RENDER_RUNNABLES.clear();
        UI_BLOOM_RUNNABLES.clear();
        UI_RENDER_RUNNABLES.clear();
        NORMAL_PRE_RENDER_RUNNABLES.clear();
        NORMAL_POST_RENDER_RUNNABLES.clear();
        UI_POST_BLOOM_RUNNABLES.clear();
    }
}
