package cn.stars.reversal.util.reversal;

public enum Branch {
    PRODUCTION, DEVELOPMENT;

    public static String getBranchName(Branch branch) {
        if (branch == DEVELOPMENT) {
            return "(DEVELOPMENT)";
        }
        if (branch == PRODUCTION) {
            return "(PRODUCTION)";
        }
        return null;
    }
}
