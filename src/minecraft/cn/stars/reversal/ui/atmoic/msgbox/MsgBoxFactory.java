package cn.stars.reversal.ui.atmoic.msgbox;

public class MsgBoxFactory {
    private AtomicMsgBox parent;

    public MsgBoxFactory(AtomicMsgBox parent) {
        this.parent = parent;
    }

    public MsgBoxFactory() {
        this.parent = null;
    }

    public MsgBoxFactory setTitle(String title) {
        if (this.parent != null) {
            this.parent.setTitle(title);
        } else {
            this.parent = new AtomicMsgBox(title);
        }
        return this;
    }

    public MsgBoxFactory setStyle(AtomicMsgBox.MsgBoxStyle style) {
        if (this.parent != null) {
            this.parent.setStyle(style);
        } else {
            this.parent = new AtomicMsgBox(style, "");
        }
        return this;
    }

    public MsgBoxFactory addLine(String msg) {
        if (this.parent != null) {
            this.parent.getMessage().add(msg);
        } else {
            this.parent = new AtomicMsgBox(AtomicMsgBox.MsgBoxStyle.INFO, "");
            this.parent.getMessage().add(msg);
        }
        return this;
    }

    public MsgBoxFactory addLine(String msg, int index) {
        if (this.parent != null) {
            this.parent.getMessage().add(index, msg);
        } else {
            this.parent = new AtomicMsgBox(AtomicMsgBox.MsgBoxStyle.INFO, "");
            this.parent.getMessage().add(index, msg);
        }
        return this;
    }

    public MsgBoxFactory clearLine() {
        if (this.parent != null) {
            this.parent.getMessage().clear();
        } else {
            this.parent = new AtomicMsgBox(AtomicMsgBox.MsgBoxStyle.INFO, "");
            this.parent.getMessage().clear();
        }
        return this;
    }

    public AtomicMsgBox build() {
        return this.parent;
    }
}
