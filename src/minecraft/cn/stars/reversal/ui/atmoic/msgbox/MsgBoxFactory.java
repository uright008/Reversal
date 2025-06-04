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
        checkParent();
        this.parent.setTitle(title);
        return this;
    }

    public MsgBoxFactory setStyle(AtomicMsgBox.MsgBoxStyle style) {
        checkParent();
        this.parent.setStyle(style);
        return this;
    }

    public MsgBoxFactory addLine(String msg) {
        checkParent();
        this.parent.getMessage().add(msg);
        return this;
    }

    public MsgBoxFactory clearLine() {
        checkParent();
        this.parent.getMessage().clear();
        return this;
    }

    public MsgBoxFactory onOK(Runnable runnable) {
        checkParent();
        this.parent.getOK_BUTTON_ACTIONS().add(runnable);
        return this;
    }

    public MsgBoxFactory onYes(Runnable runnable) {
        checkParent();
        this.parent.getYES_BUTTON_ACTIONS().add(runnable);
        return this;
    }

    public MsgBoxFactory onNo(Runnable runnable) {
        checkParent();
        this.parent.getNO_BUTTON_ACTIONS().add(runnable);
        return this;
    }

    public MsgBoxFactory onFinish(Runnable runnable) {
        checkParent();
        this.parent.getFINISH_ACTIONS().add(runnable);
        return this;
    }

    public MsgBoxFactory setMark(AtomicMsgBox.MsgBoxMark mark) {
        checkParent();
        this.parent.setMark(mark);
        return this;
    }

    public AtomicMsgBox build() {
        return this.parent;
    }

    private void checkParent() {
        if (this.parent == null) {
            this.parent = new AtomicMsgBox(AtomicMsgBox.MsgBoxStyle.INFO, "");
        }
    }
}
