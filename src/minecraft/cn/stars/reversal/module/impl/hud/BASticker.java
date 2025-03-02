package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NoteValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "BASticker", chineseName = "蔚蓝档案贴纸", description = "ARIS doesn't have a b",
        chineseDescription = "哎,等等,爱丽丝怎么没有b~", category = Category.HUD)
public class BASticker extends Module {
    private final ModeValue character = new ModeValue("Character", this, "Aris",
            "Aris", "Shiroko", "Azusa", "Hina Swimsuit", "Ui", "Hoshino Swimsuit", "Mika", "Ibuki");
    private final NumberValue scale = new NumberValue("Scale", this, 1, 0.1, 2, 0.1);
    private final NoteValue note = new NoteValue("增大Scale可以使图片效果变好,但可能会挡住屏幕", this);
    ScaledResolution sr;

    public BASticker() {
        setCanBeEdited(true);
        setWidth(100);
        setHeight(100);
        setX(100);
        setY(100);
    }

    @Override
    public void onUpdateAlways() {
        setSuffix(character.getMode());
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        sr = new ScaledResolution(mc);
        int x = getX() + 5;
        int y = getY() + 5;
        Runnable runnable = () -> {
            switch (character.getMode()) {
                case "Aris": {
                    setRoundedWidth(160, scale.getValue());
                    setRoundedHeight(170, scale.getValue());
                    drawImage(new ResourceLocation("reversal/images/box_weapon/aris.png"), x, y, 150, 160, scale.getValue());
                    break;
                }
                case "Shiroko": {
                    setRoundedWidth(95, scale.getValue());
                    setRoundedHeight(165, scale.getValue());
                    drawImage(new ResourceLocation("reversal/images/box_weapon/shiroko.png"), x, y, 85, 160, scale.getValue());
                    break;
                }
                case "Azusa": {
                    setRoundedWidth(130, scale.getValue());
                    setRoundedHeight(170, scale.getValue());
                    drawImage(new ResourceLocation("reversal/images/box_weapon/azusa.png"), x, y, 120, 160, scale.getValue());
                    break;
                }
                case "Hina Swimsuit": {
                    setRoundedWidth(110, scale.getValue());
                    setRoundedHeight(170, scale.getValue());
                    drawImage(new ResourceLocation("reversal/images/box_weapon/hina_swimsuit.png"), x, y, 100, 160, scale.getValue());
                    break;
                }
                case "Ui": {
                    setRoundedWidth(110, scale.getValue());
                    setRoundedHeight(170, scale.getValue());
                    drawImage(new ResourceLocation("reversal/images/box_weapon/ui.png"), x, y, 100, 160, scale.getValue());
                    break;
                }
                case "Hoshino Swimsuit": {
                    setRoundedWidth(140, scale.getValue());
                    setRoundedHeight(170, scale.getValue());
                    drawImage(new ResourceLocation("reversal/images/box_weapon/hoshino_swimsuit.png"), x, y, 130, 160, scale.getValue());
                    break;
                }
                case "Mika": {
                    setRoundedWidth(110, scale.getValue());
                    setRoundedHeight(170, scale.getValue());
                    drawImage(new ResourceLocation("reversal/images/box_weapon/mika.png"), x, y, 95, 160, scale.getValue());
                    break;
                }
                case "Ibuki": {
                    setRoundedWidth(110, scale.getValue());
                    setRoundedHeight(180, scale.getValue());
                    drawImage(new ResourceLocation("reversal/images/box_weapon/ibuki.png"), x, y, 100, 170, scale.getValue());
                    break;
                }
            }
        };

        NORMAL_RENDER_RUNNABLES.add(runnable);
        MODERN_BLOOM_RUNNABLES.add(runnable);
        MODERN_BLUR_RUNNABLES.add(runnable);
    }

    public void setRoundedWidth(int width, double scale) {
        setWidth(Math.round(width * (float)scale));
    }

    public void setRoundedHeight(int height, double scale) {
        setHeight(Math.round(height * (float)scale));
    }


    public static void drawImage(ResourceLocation image, int x, int y, int width, int height, double scale) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0,
                (int) Math.round(width * scale), (int) Math.round(height * scale), Math.round(width * scale), Math.round(height * scale));
        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
    }
}
