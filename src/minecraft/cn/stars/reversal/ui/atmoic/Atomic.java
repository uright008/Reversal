package cn.stars.reversal.ui.atmoic;

import cn.stars.reversal.GameInstance;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Atomic implements GameInstance {

    public static AtomicIslandRenderer INSTANCE = new AtomicIslandRenderer();

    public static float x;
    public static float y;
    public static float width;
    public static float height;

    public static float getRenderX(float x) {
        return x - width / 2;
    }

    public static float getRenderY(float y) {
        return y - height / 2;
    }

    public static void submitTask(Runnable task, long timeout) {
        INSTANCE.toRender.clear();
        INSTANCE.toRender.add(task);
        INSTANCE.setTaskScheduled(true);
        INSTANCE.getTaskTimeout().reset();
        INSTANCE.setCurrentTaskTimeout(timeout);
    }
}
