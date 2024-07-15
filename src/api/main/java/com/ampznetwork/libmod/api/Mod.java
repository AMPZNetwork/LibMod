package com.ampznetwork.libmod.api;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public interface Mod {
    static void printExceptionWithIssueReportUrl(Mod mod, String message, Throwable t) {
        printExceptionWithIssueReportUrl(mod.log(), message, t, mod.getRegistry().getGitHubUrl() + "/issues");
    }

    static void printExceptionWithIssueReportUrl(Logger log, String message, Throwable t, @Nullable String issuesUrl) {
        log.error(message, new RuntimeException("An unexpected internal error occurred." + (issuesUrl == null
                ? "" : (" Please open a bugreport at " + issuesUrl)), t));
    }

    Logger log();

    LibMod getLib();

    Registry getRegistry();

    void reload();

}
