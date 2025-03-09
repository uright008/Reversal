package cn.stars.reversal.module.impl.client;

import cn.stars.reversal.event.impl.Shader3DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.blur.KawaseBloom;
import cn.stars.reversal.util.render.blur.KawaseBlur;
import net.minecraft.client.shader.Framebuffer;

@ModuleInfo(name = "PostProcessing", localizedName = "module.PostProcessing.name", description = "Add blur and bloom effects", localizedDescription = "module.PostProcessing.desc", category = Category.CLIENT)
public class PostProcessing extends Module
{
    public final BoolValue blur = new BoolValue("Blur", this, true);
    private final NumberValue iterations = new NumberValue("Blur Iterations", this, 2, 1, 8, 1);
    private final NumberValue offset = new NumberValue("Blur Offset", this, 2, 1, 10, 1);
    public final BoolValue bloom = new BoolValue("Bloom", this, true);
    private final NumberValue shadowRadius = new NumberValue("Bloom Iterations", this, 2, 1, 8, 1);
    private final NumberValue shadowOffset = new NumberValue("Bloom Offset", this, 1, 1, 10, 1);
    public final BoolValue impactGUIs = new BoolValue("Impact GUIs", this, true);

    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    public PostProcessing() {
        setWidth(0);
        setHeight(0);
        setCanBeEdited(false);
    }

    @Override
    public void onUpdateAlways() {
        if (this.enabled) this.enabled = false;
        if (!blur.enabled) {
            MODERN_BLUR_RUNNABLES.clear();
        }
        if (!bloom.enabled) {
            MODERN_BLOOM_RUNNABLES.clear();
        }
    }

    public void blurScreen() {
        if (!ModuleInstance.canDrawHUD()) return;
        if (blur.isEnabled()) {
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);

            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);
            Shader3DEvent event = new Shader3DEvent(false);
            event.call();
            doBlur();

            stencilFramebuffer.unbindFramebuffer();


            KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, (int) iterations.getValue(), (int) offset.getValue());

        }


        if (bloom.isEnabled()) {
            stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
            stencilFramebuffer.framebufferClear();
            stencilFramebuffer.bindFramebuffer(false);

            Shader3DEvent event = new Shader3DEvent(true);
            event.call();
            doBloom();

            stencilFramebuffer.unbindFramebuffer();

            KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, (int) shadowRadius.getValue(), (int) shadowOffset.getValue());
        }
    }

    public void drawElementWithBlur(Runnable runnable) {
        if (mc.theWorld != null) drawElementWithBlur(runnable, (int) iterations.getValue(), (int) offset.getValue());
        else drawElementWithBlur(runnable, 2, 2);
    }

    public void drawElementWithBloom(Runnable runnable) {
        if (mc.theWorld != null) drawElementWithBloom(runnable, (int) shadowRadius.getValue(), (int) shadowOffset.getValue());
        else drawElementWithBloom(runnable, 2, 2);
    }

    public void drawElementWithBlur(Runnable runnable, int iterations, int offset) {
        if (!impactGUIs.enabled) return;
        stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
        runnable.run();
        stencilFramebuffer.unbindFramebuffer();
        KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, iterations, offset);
    }

    public void drawElementWithBloom(Runnable runnable, int radius, int offset) {
        if (!impactGUIs.enabled) return;
        stencilFramebuffer = RenderUtil.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
        runnable.run();
        stencilFramebuffer.unbindFramebuffer();
        KawaseBloom.renderBlur(stencilFramebuffer.framebufferTexture, radius, offset);
    }

    public void doBlur() {
        MODERN_BLUR_RUNNABLES.forEach(Runnable::run);
        MODERN_BLUR_RUNNABLES.clear();
    }

    public void doBloom() {
        MODERN_BLOOM_RUNNABLES.forEach(Runnable::run);
        MODERN_BLOOM_RUNNABLES.clear();
    }

    public void clear() {
        MODERN_BLUR_RUNNABLES.clear();
        MODERN_BLOOM_RUNNABLES.clear();
    }
}
