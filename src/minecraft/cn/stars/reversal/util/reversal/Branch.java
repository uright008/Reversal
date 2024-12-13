package cn.stars.reversal.util.reversal;

public enum Branch {
    PRODUCTION, DEVELOPMENT, SNAPSHOT, PRIVATE, PRE_RELEASE;

    public static String getBranchName(Branch branch) {
        if (branch == DEVELOPMENT) {
            return "(DEVELOPMENT)";
        }
        if (branch == PRODUCTION) {
            return "(PRODUCTION)";
        }
        if (branch == SNAPSHOT) {
            return "(SNAPSHOT)";
        }
        if (branch == PRIVATE) {
            return "(PRIVATE)";
        }
        if (branch == PRE_RELEASE) {
            return "(PRE-RELEASE)";
        }
        return null;
    }
}
