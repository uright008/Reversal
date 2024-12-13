package cn.stars.reversal.util.reversal;

import cn.stars.reversal.module.impl.render.EnvironmentEffect;
import cn.stars.reversal.module.impl.render.JumpCircle;
import cn.stars.reversal.module.impl.render.Trail;
import cn.stars.reversal.util.ReversalLogger;
import cn.stars.reversal.util.misc.ModuleInstance;
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
                bindResource(rs2.getResource());
            }
        }
        for (Trail.ResourceLocationWithSizes rs: trail.DASH_CUBIC_TEXTURES) {
            bindResource(rs.getResource());
        }

        // Load EnvironmentEffect.class
        EnvironmentEffect environmentEffect = ModuleInstance.getModule(EnvironmentEffect.class);
        bindResource(environmentEffect.FIRE_PART_TEX);
        bindResource(environmentEffect.STARS_TEX);
        bindResource(environmentEffect.SNOWFLAKE_TEX);

        // Load JumpCircle.class
        JumpCircle jumpCircle = ModuleInstance.getModule(JumpCircle.class);
        jumpCircle.initResources();
        bindResource(new ResourceLocation(jumpCircle.staticLoc + "circle.png"));
        bindResource(new ResourceLocation(jumpCircle.staticLoc + "konchal.png"));

        Minecraft.getMinecraft().getTextureManager().resetTexture();

        ReversalLogger.info("Successfully loaded " + count + " resources!");
    }

    private void bindResource(ResourceLocation toBind) {
        SimpleTexture texture = new SimpleTexture(toBind);
        Minecraft.getMinecraft().getTextureManager().loadTexture(toBind, texture);
        count++;
    }
}
