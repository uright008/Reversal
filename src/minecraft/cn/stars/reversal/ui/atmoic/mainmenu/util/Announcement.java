package cn.stars.reversal.ui.atmoic.mainmenu.util;

import cn.stars.reversal.GameInstance;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Announcement {
    public String title;
    public String date;
    public ArrayList<String> content = new ArrayList<>();
    public int id;
    public float maxWidth;

    public Announcement(String title, String date, int id) {
        this.title = title;
        this.date = date;
        this.id = id;
    }

    public Announcement addContent(String content) {
        if (this.content.contains(content)) return addContent(content + " ");
        this.content.add(content);
        return this;
    }

    public Announcement calcMaxWidth() {
        for (String line : content) {
            maxWidth = Math.max(maxWidth, GameInstance.psm18.width(line));
        }
        maxWidth = Math.max(maxWidth, GameInstance.psm24.width(title) + 15);
        return this;
    }
}
