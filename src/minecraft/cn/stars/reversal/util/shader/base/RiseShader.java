package cn.stars.reversal.util.shader.base;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

import java.util.List;

@Getter
@Setter
public abstract class RiseShader {
    public boolean active;
    public final Minecraft mc = Minecraft.getMinecraft();

    public abstract void run(ShaderRenderType type, float partialTicks, List<Runnable> runnable);

    public abstract void update();
}
