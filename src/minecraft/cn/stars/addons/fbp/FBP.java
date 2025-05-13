package cn.stars.addons.fbp;

import cn.stars.addons.fbp.particle.FBPParticleDigging;
import cn.stars.addons.fbp.particle.FBPParticleManager;
import cn.stars.addons.fbp.renderer.FBPWeatherRenderer;
import cn.stars.reversal.module.impl.addons.FancyBlockParticles;
import cn.stars.reversal.util.misc.ModuleInstance;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

public class FBP {
	public static final ResourceLocation LOCATION_PARTICLE_TEXTURE = new ResourceLocation(
			"textures/particle/particles.png");
	public static final Vec3[] CUBE = {
			// TOP
			new Vec3(1, 1, -1), new Vec3(1, 1, 1), new Vec3(-1, 1, 1), new Vec3(-1, 1, -1),

			// BOTTOM
			new Vec3(-1, -1, -1), new Vec3(-1, -1, 1), new Vec3(1, -1, 1), new Vec3(1, -1, -1),

			// FRONT
			new Vec3(-1, -1, 1), new Vec3(-1, 1, 1), new Vec3(1, 1, 1), new Vec3(1, -1, 1),

			// BACK
			new Vec3(1, -1, -1), new Vec3(1, 1, -1), new Vec3(-1, 1, -1), new Vec3(-1, -1, -1),

			// LEFT
			new Vec3(-1, -1, -1), new Vec3(-1, 1, -1), new Vec3(-1, 1, 1), new Vec3(-1, -1, 1),

			// RIGHT
			new Vec3(1, -1, 1), new Vec3(1, 1, 1), new Vec3(1, 1, -1), new Vec3(1, -1, -1) };
	public static final Vec3[] CUBE_NORMALS = { new Vec3(0, 1, 0), new Vec3(0, -1, 0),

			new Vec3(0, 0, 1), new Vec3(0, 0, -1),

			new Vec3(-1, 0, 0), new Vec3(1, 0, 0) };
	public static FBP INSTANCE;
	public static File config = null;
	public static int minAge, maxAge, particlesPerAxis;
	public static double scaleMult, gravityMult, rotationMult, weatherParticleDensity;

	public static boolean enabled = false;
	public static boolean infiniteDuration = false;
	public static boolean randomRotation, cartoonMode, spawnWhileFrozen, randomizedScale,
			randomFadingSpeed, entityCollision, bounceOffWalls, lowTraction, smartBreaking, fancyRain, fancySnow,
			fancyFlame, fancySmoke, fancyAdditions, waterPhysics, restOnFloor, frozen;

	public static SplittableRandom random = new SplittableRandom();

	public static VertexFormat POSITION_TEX_COLOR_LMAP_NORMAL;
	public static FBPWeatherRenderer fancyWeatherRenderer;
	public static FBPParticleManager fancyEffectRenderer;
	public static EffectRenderer originalEffectRenderer;

	public List<String> blockParticleBlacklist;
	public List<Material> floatingMaterials;

	public FBP() {
		INSTANCE = this;

		POSITION_TEX_COLOR_LMAP_NORMAL = new VertexFormat();

		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.POSITION_3F);
		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.TEX_2F);
		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.COLOR_4UB);
		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.TEX_2S);
		POSITION_TEX_COLOR_LMAP_NORMAL.addElement(DefaultVertexFormats.NORMAL_3B);

		blockParticleBlacklist = Collections.synchronizedList(new ArrayList<>());
		floatingMaterials = Collections.synchronizedList(new ArrayList<>());
	}

	public static boolean isEnabled() {
		if (!enabled)
			frozen = false;

		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		if (enabled) {
			FBP.fancyEffectRenderer.carryOver();

			Minecraft.getMinecraft().effectRenderer = FBP.fancyEffectRenderer;
		} else {
			Minecraft.getMinecraft().effectRenderer = FBP.originalEffectRenderer;
		}

		ModuleInstance.getModule(FancyBlockParticles.class).updateAllValues();

		FBP.enabled = enabled;
	}


	public static void init() {
		INSTANCE = new FBP();

		ModuleInstance.getModule(FancyBlockParticles.class).updateAllValues();

		initRenderers(null, Minecraft.getMinecraft().renderEngine);

		setEnabled(ModuleInstance.getModule(FancyBlockParticles.class).enabled);
	}

	public boolean isBlacklisted(Block b) {
		if (b == null)
			return true;

		return blockParticleBlacklist.contains(b.getUnlocalizedName());
	}

	public boolean doesMaterialFloat(Material mat) {
		return floatingMaterials.contains(mat);
	}

	public void addToBlacklist(String name) {
		if (StringUtils.isEmpty(name))
			return;

		Block b = Block.getBlockFromName(name);

		if (b == null || b == Blocks.redstone_block)
			return;

		addToBlacklist(b);
	}

	public void addToBlacklist(Block b) {
		if (b == null)
			return;

		String name = b.getLocalizedName();

		if (!blockParticleBlacklist.contains(name))
			blockParticleBlacklist.add(name);
	}

	public void removeFromBlacklist(Block b) {
		if (b == null)
			return;

		String name = b.getLocalizedName();

        blockParticleBlacklist.remove(name);
	}

	public void resetBlacklist() {
		blockParticleBlacklist.clear();
	}

	public static void initRenderers(World world, TextureManager textureManager) {
		FBP.fancyEffectRenderer = new FBPParticleManager(world, textureManager,
				new FBPParticleDigging.Factory());
		FBP.fancyWeatherRenderer = new FBPWeatherRenderer();
		FBP.originalEffectRenderer = new EffectRenderer(world, textureManager);
	}
}