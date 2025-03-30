package cn.stars.reversal.event;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

@Getter
@Setter
public abstract class Event {
    private boolean cancelled;

    public void call() {
        EventHandler.handle(this);
    }
}
