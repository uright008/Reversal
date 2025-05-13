package cn.stars.addons.optimization.entityculling;

import cn.stars.addons.culling.OcclusionCullingInstance;
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

public abstract class EntityCullingModBase {

    public static EntityCullingModBase instance = new EntityCullingMod();
    public OcclusionCullingInstance culling;
    public boolean debugHitboxes = false;
    public static boolean enabled = true; // public static to make it faster for the jvm
    public CullTask cullTask;
    private Thread cullThread;
    protected KeyBinding keybind = new KeyBinding("key.entityculling.toggle", -1, "EntityCulling");
    protected boolean pressed = false;

    private final File settingsFile = new File("config", "entityculling.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    //stats
    public int renderedBlockEntities = 0;
    public int skippedBlockEntities = 0;
    public int renderedEntities = 0;
    public int skippedEntities = 0;
    //public int tickedEntities = 0;
    //public int skippedEntityTicks = 0;

    public void onInitialize() {
        ReversalLogger.info("[*] Initializing Entity Culling!");
        instance = this;
        culling = new OcclusionCullingInstance(128, new Provider());
        cullTask = new CullTask(culling, new HashSet<>(Collections.singletonList("tile.beacon")));

        cullThread = new Thread(cullTask, "CullThread");
        cullThread.setUncaughtExceptionHandler((thread, ex) -> {
            System.out.println("The CullingThread has crashed! Please report the following stacktrace!");
            ex.printStackTrace();
        });
        cullThread.start();
        initModloader();
    }

    public void worldTick() {
        cullTask.requestCull = true;
    }

    public void clientTick() {
        if (keybind.isKeyDown()) {
            if (pressed)
                return;
            pressed = true;
            enabled = !enabled;
            EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
            if(enabled) {
                if (player != null) {
                    player.addChatMessage(new ChatComponentText("Culling on"));
                }
            } else {
                if (player != null) {
                    player.addChatMessage(new ChatComponentText("Culling off"));
                }
            }
        } else {
            pressed = false;
        }
        cullTask.requestCull = true;
    }

    public abstract void initModloader();

}