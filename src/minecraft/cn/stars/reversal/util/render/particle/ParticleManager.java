package cn.stars.reversal.util.render.particle;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.RainyAPI;
import cn.stars.reversal.util.math.RandomUtil;
import cn.stars.reversal.util.math.TimerUtil;
import lombok.Setter;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ChengFeng
 * @since 2024/8/22
 **/
public class ParticleManager implements GameInstance {
    private final List<Particle> particleList = new ArrayList<>();
    private final TimerUtil timer = new TimerUtil();
    @Setter
    private int amount = 0;
    public void renderParticles() {
        if (!RainyAPI.guiSnow) return;
        if (particleList.size() <= amount && timer.hasTimeElapsed(RandomUtil.INSTANCE.nextInt(400, 2000))) {
            ScaledResolution sr = new ScaledResolution(mc);
            float startX = RandomUtil.INSTANCE.nextFloat(0, (float) sr.getScaledWidth_double());
            particleList.add(new Particle(startX, RandomUtil.INSTANCE.nextFloat(1.5f, 3f)));
            timer.reset();
        }
        if (particleList.size() > amount) {
            particleList.remove(particleList.size() - 1);
        }
        particleList.forEach(Particle::render);
    }
}