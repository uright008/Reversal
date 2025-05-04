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
    public ArrayList<Content> content = new ArrayList<>();
    public int id;
    public float maxWidth;

    public Announcement(String title, String date, int id) {
        this.title = title;
        this.date = date;
        this.id = id;
    }

    public Announcement addContent(String content) {
        Content content1 = new Content(content);
        if (this.content.contains(content1)) return addContent(content + " ");
        this.content.add(content1);
        return this;
    }

    public Announcement mark() {
        content.get(content.size() - 1).isMarked = true;
        return this;
    }

    public Announcement calcMaxWidth() {
        for (Content line : content) {
            maxWidth = Math.max(maxWidth, GameInstance.psm18.width(line.content));
        }
        maxWidth = Math.max(maxWidth, GameInstance.psm24.width(title) + 15);
        return this;
    }

    public class Content {
        public String content;
        public boolean isMarked;

        public Content(String content, boolean isMarked) {
            this.content = content;
            this.isMarked = isMarked;
        }

        public Content(String content) {
            this.content = content;
            this.isMarked = false;
        }
    }
}
