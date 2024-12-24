package com.ampznetwork.libmod.api;

import com.ampznetwork.libmod.api.messaging.MessagingService;
import com.ampznetwork.libmod.api.model.API;
import com.ampznetwork.libmod.api.model.info.DatabaseInfo;
import lombok.experimental.UtilityClass;
import org.comroid.api.func.util.Command;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Collection;

public interface LibMod extends SubMod, MessagingService.Type.Provider, API.Delegate {
    static boolean equalResourceKey(String actual, String expected) {
        var colonIndexActual   = actual.indexOf(':');
        var colonIndexExpected = expected.indexOf(':');
        if (colonIndexActual != colonIndexExpected) {
            if (colonIndexActual != -1) actual = actual.substring(colonIndexActual + 1);
            if (colonIndexExpected != -1) expected = expected.substring(colonIndexExpected + 1);
        }
        return expected.equalsIgnoreCase(actual);
    }

    @Override
    default LibMod getLib() {
        return this;
    }

    @Override
    default Class<?> getModuleType() {
        return LibMod.class;
    }

    Collection<SubMod> getRegisteredSubMods();

    DatabaseInfo getDatabaseInfo();

    void register(SubMod mod);

    @UtilityClass
    final class Strings {
        public static final String AddonName          = "LibMod";
        public static final String AddonId            = "libmod";
        public static final String IssuesUrl          = "https://github.com/AMPZNetwork/BanMod/issues";
        public static final String PleaseCheckConsole = "Please check console for further information";
    }

    @UtilityClass
    final class Resources {
        public static String DefaultDbType               = "h2";
        public static String DefaultDbUrl                = "jdbc:h2:file:./database.h2";
        public static String DefaultDbUsername           = "sa";
        public static String DefaultDbPassword           = "";
        public static String DefaultMessagingServiceType = "polling-db";

        public static void printExceptionWithIssueReportUrl(Logger log, String message, Throwable t) {
            log.error(message, new RuntimeException("An unexpected internal error occurred. Please open a bugreport at " + Strings.IssuesUrl, t));
        }

        public static Command.@NotNull Error couldNotSaveError() {
            return new Command.Error("Could not save changes");
        }
    }
}
