package cn.stars.reversal.util.reversal;

public enum ClientType {
    MAIN, HACK, FORGE;

    public static String getIdentifier(ClientType type) {
        switch (type) {
            case MAIN: return "";
            case HACK: return " [A]";
            case FORGE: return " [B]";
        }
        return "";
    }
}
