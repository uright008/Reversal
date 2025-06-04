package cn.stars.addons.optimization.entityculling;

import cn.stars.addons.culling.OcclusionCullingInstance;
import cn.stars.reversal.Reversal;
import cn.stars.reversal.util.ReversalLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;

public class EntityCullingModBase {

    public static EntityCullingModBase instance = new EntityCullingMod();
    public OcclusionCullingInstance culling;
    public static boolean enabled = true; // public static to make it faster for the jvm
    public CullTask cullTask;

    //stats
    public int renderedBlockEntities = 0;
    public int skippedBlockEntities = 0;
    public int renderedEntities = 0;
    public int skippedEntities = 0;

    public void onInitialize() {
        ReversalLogger.info("[*] Initializing Entity Culling!");
        instance = this;
        culling = new OcclusionCullingInstance(128, new Provider());
        cullTask = new CullTask(culling, new HashSet<>(Collections.singletonList("tile.beacon")));

        Thread cullThread = new Thread(cullTask, "CullThread");
        cullThread.setUncaughtExceptionHandler((thread, ex) -> {
            ReversalLogger.error("Cull thread exception", ex);
        });
        cullThread.setPriority(10);
        cullThread.start();
    }

    public void worldTick() {
        cullTask.requestCull = true;
    }

    public void clientTick() {
        cullTask.requestCull = true;
    }

}