package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.event.impl.Shader3DEvent;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.value.impl.NoteValue;
import cn.stars.reversal.value.impl.NumberValue;
import cn.stars.reversal.util.render.ColorUtils;
import cn.stars.reversal.util.render.RoundedUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "TestElement", chineseName = "测试功能", description = "Only for test",
        chineseDescription = "仅供测试,别开", category = Category.HUD)
public class TestElement extends Module {
    private final NoteValue note = new NoteValue("Only for test purpose. DO NOT enable this.", this);
    private final NumberValue speed = new NumberValue("Scroll Speed", this, 2.0, 0.5, 9.0, 1.0);
    List<String> strings = new ArrayList<>();
    public TestElement() {
        setCanBeEdited(true);
        setX(100);
        setY(100);
        setWidth(100);
        setHeight(100);
    }


    @Override
    public void onShader3D(Shader3DEvent event) {
        int x = getX() + 2;
        int y = getY() + 2;
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        int x = getX() + 2;
        int y = getY() + 2;
        FontManager.getPSM(32).drawString("Test", x, y, new Color(255,255,255,255).getRGB());
    }

    @Override
    protected void onEnable() {
    }
}
