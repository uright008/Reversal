package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.ThemeType;
import cn.stars.reversal.util.render.ThemeUtil;

import java.awt.*;

@ModuleInfo(name = "ArmorHud", chineseName = "装备显示", description = "Show your armors",
        chineseDescription = "显示你的装备", category = Category.HUD)
public class ArmorHud extends Module {
    public ArmorHud() {
        setCanBeEdited(true);
        setWidth(180);
        setHeight(80);
        setX(100);
        setY(100);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        RenderUtil.rect(getX(),getY(),getWidth(),getHeight(), ThemeUtil.getThemeColor(ThemeType.LOGO));
        RenderUtil.rect(getX(),getY(),getWidth(),getHeight(), new Color(0,0,0,120));
        mc.fontRendererObj.drawStringWithShadow("我去你妈的我去你妈的我去你妈的", getX() + 25, getY() + 30,  new Color(255,255,255).getRGB());
        mc.fontRendererObj.drawStringWithShadow("我现在很烦", getX() + 70, getY() + 40,  new Color(255,255,255).getRGB());
    }
}
