package cn.stars.reversal.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Event {
    private boolean cancelled;

    public void call() {
        EventHandler.handle(this);
    }
}
