package cn.stars.reversal.module.impl.client;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.math.Profiler;
import cn.stars.reversal.util.render.RenderUtil;

import java.awt.*;

@ModuleInfo(name = "Debugger", localizedName = "Debugger", description = "Client debugger, don't enable if you are not developer.", localizedDescription = "Client debugger, don't enable if you are not developer.", category = Category.HUD, experimentOnly = true)
public class Debugger extends Module {
    public static Profiler render2dProfiler = new Profiler();
    public static Profiler postProcessingProfiler = new Profiler();
    public static Profiler eventProfiler = new Profiler();
    public static Profiler cguiProfiler = new Profiler();
    public static Profiler fontProfiler = new Profiler();
    public static Profiler cameraProfiler = new Profiler();

    @Override
    public void onRender2D(Render2DEvent event) {
        int x = getX();
        int y = getY();
        RenderUtil.roundedRectangle(x, y, 120, 200, 4, new Color(0,0,0,80));

        regular20Bold.drawString("Profiler", x + 3, y + 3, Color.WHITE.getRGB());

        regular18.drawString("Render2D", x + 2, y + 14, Color.WHITE.getRGB());
        regular18.drawString(render2dProfiler.getDebugTime(), x + 120 - regular18.width(render2dProfiler.getDebugTime()), y + 14, Color.WHITE.getRGB());
        render2dProfiler.reset();

        regular18.drawString("PostProcessing", x + 2, y + 24, Color.WHITE.getRGB());
        regular18.drawString(postProcessingProfiler.getDebugTime(), x + 120 - regular18.width(postProcessingProfiler.getDebugTime()), y + 24, Color.WHITE.getRGB());
        postProcessingProfiler.reset();

        regular18.drawString("Event", x + 2, y + 34, Color.WHITE.getRGB());
        regular18.drawString(eventProfiler.getDebugTime(), x + 120 - regular18.width(eventProfiler.getDebugTime()), y + 34, Color.WHITE.getRGB());
        eventProfiler.reset();

        regular18.drawString("ClickGUI", x + 2, y + 44, Color.WHITE.getRGB());
        regular18.drawString(cguiProfiler.getDebugTime(), x + 120 - regular18.width(cguiProfiler.getDebugTime()), y + 44, Color.WHITE.getRGB());
        cguiProfiler.reset();

        regular18.drawString("Font", x + 2, y + 54, Color.WHITE.getRGB());
        regular18.drawString(fontProfiler.getDebugTime(), x + 120 - regular18.width(fontProfiler.getDebugTime()), y + 54, Color.WHITE.getRGB());
        fontProfiler.reset();

        regular18.drawString("Camera", x + 2, y + 64, Color.WHITE.getRGB());
        regular18.drawString(cameraProfiler.getDebugTime(), x + 120 - regular18.width(cameraProfiler.getDebugTime()), y + 64, Color.WHITE.getRGB());
        cameraProfiler.reset();

        regular20Bold.drawString("World", x + 3, y + 83, Color.WHITE.getRGB());

        regular18.drawString("Entity", x + 2, y + 94, Color.WHITE.getRGB());
        regular18.drawString(mc.theWorld.loadedEntityList.size() + "", x + 120 - regular18.width(mc.theWorld.loadedEntityList.size() + ""), y + 94, Color.WHITE.getRGB());

        regular18.drawString("TileEntity", x + 2, y + 104, Color.WHITE.getRGB());
        regular18.drawString(mc.theWorld.loadedTileEntityList.size() + "", x + 120 - regular18.width(mc.theWorld.loadedTileEntityList.size() + ""), y + 104, Color.WHITE.getRGB());

        regular18.drawString("Player", x + 2, y + 114, Color.WHITE.getRGB());
        regular18.drawString(mc.theWorld.playerEntities.size() + "", x + 120 - regular18.width(mc.theWorld.playerEntities.size() + ""), y + 114, Color.WHITE.getRGB());

        setWidth(120);
        setHeight(200);
    }

    public Debugger() {
        setX(100);
        setY(100);
        setWidth(100);
        setHeight(200);
        setCanBeEdited(true);
    }
}
