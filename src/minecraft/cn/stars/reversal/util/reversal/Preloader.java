package cn.stars.reversal.util.reversal;

import cn.stars.reversal.module.impl.render.EnvironmentEffect;
import cn.stars.reversal.module.impl.render.JumpCircle;
import cn.stars.reversal.module.impl.render.TargetESP;
import cn.stars.reversal.module.impl.render.Trail;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.misc.ModuleInstance;
import cn.stars.reversal.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class Preloader {
    private int count = 0;

    public void loadResources() {
        ReversalLogger.info("Loading resources on thread: " + Thread.currentThread().getName());
        // Load Trail.class
        Trail trail = ModuleInstance.getModule(Trail.class);
        trail.addAll_DASH_CUBIC_TEXTURES();
        trail.addAll_DASH_CUBIC_ANIMATED_TEXTURES();

        for (List<Trail.ResourceLocationWithSizes> rs1: trail.DASH_CUBIC_ANIMATED_TEXTURES) {
            for (Trail.ResourceLocationWithSizes rs2: rs1) {
                loadResource(rs2.getResource());
            }
        }
        for (Trail.ResourceLocationWithSizes rs: trail.DASH_CUBIC_TEXTURES) {
            loadResource(rs.getResource());
        }

        // Load EnvironmentEffect.class
        EnvironmentEffect environmentEffect = ModuleInstance.getModule(EnvironmentEffect.class);
        loadResource(environmentEffect.FIRE_PART_TEX);
        loadResource(environmentEffect.STARS_TEX);
        loadResource(environmentEffect.SNOWFLAKE_TEX);

        // Load JumpCircle.class
        JumpCircle jumpCircle = ModuleInstance.getModule(JumpCircle.class);
        jumpCircle.initResources();
        loadResource(new ResourceLocation(jumpCircle.staticLoc + "circle.png"));
        loadResource(new ResourceLocation(jumpCircle.staticLoc + "konchal.png"));

        // Load TargetHud.class
        loadResource(RenderUtil.getESPImage());
        loadResource(TargetESP.BUBBLE_TEXTURE);
        loadResource(TargetESP.SURROUNDING_TEXTURE);

        Minecraft.getMinecraft().getTextureManager().resetTexture();

        ReversalLogger.info("Successfully loaded " + count + " resources!");
    }

    private void loadResource(ResourceLocation toBind) {
        SimpleTexture texture = new SimpleTexture(toBind);
        Minecraft.getMinecraft().getTextureManager().loadTexture(toBind, texture);
        count++;
    }
}
