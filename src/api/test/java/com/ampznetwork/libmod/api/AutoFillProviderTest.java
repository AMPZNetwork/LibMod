package com.ampznetwork.libmod.api;

import com.ampznetwork.libmod.api.entity.Player;
import com.ampznetwork.libmod.api.interop.game.IPlayerAdapter;
import com.ampznetwork.libmod.api.model.AutoFillProvider;
import org.comroid.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.easymock.EasyMock.*;

public class AutoFillProviderTest {
    static final String[]                  WORLD_NAMES     = new String[]{
            "world", "nether", "end", "nether_world"
    };
    static final Player[]                  CURRENT_PLAYERS = new Player[]{
            Player.builder().id(UUID.randomUUID()).name("Steve").build(),
            Player.builder().id(UUID.randomUUID()).name("Herobrine").build(),
            Player.builder().id(UUID.randomUUID()).name("Heroic_Steve").build(),
            };
    static final TestUtil.AutoFillProvider helper;
    static       LibMod                    lib;
    static       IPlayerAdapter            playerAdapter;

    static {
        lib           = mock(LibMod.class);
        playerAdapter = mock(IPlayerAdapter.class);

        helper = new TestUtil.AutoFillProvider() {{getContext().add(lib);}};
    }

    @AfterEach
    public void validate() {
        verify(lib, playerAdapter);
    }

    private void mockWorldNames() {
        reset(lib, playerAdapter);

        expect(lib.getLib()).andReturn(lib).times(4);
        expect(lib.worldNames()).andAnswer(() -> Arrays.stream(WORLD_NAMES)).times(4);

        replay(lib, playerAdapter);
    }

    @Test
    public void directWorldNames() {
        mockWorldNames();

        helper.testCaseDirect("on empty filter", AutoFillProvider.WorldNames.INSTANCE, "", WORLD_NAMES);
        helper.testCaseDirect("on match many",
                AutoFillProvider.WorldNames.INSTANCE,
                "nether",
                "nether",
                "nether_world");
        helper.testCaseDirect("on match wildcard",
                AutoFillProvider.WorldNames.INSTANCE,
                "*world",
                "world",
                "nether_world");
        helper.testCaseDirect("on match one", AutoFillProvider.WorldNames.INSTANCE, "end", "end");
    }

    @Test
    public void callWorldNames() {
        mockWorldNames();

        helper.testCaseCall("on empty filter", AutoFillProvider.WorldNames.class, "", WORLD_NAMES);
        helper.testCaseCall("on match many", AutoFillProvider.WorldNames.class, "nether", "nether", "nether_world");
        helper.testCaseCall("on match wildcard", AutoFillProvider.WorldNames.class, "*world", "world", "nether_world");
        helper.testCaseCall("on match one", AutoFillProvider.WorldNames.class, "end", "end");
    }

    private void mockPlayerNames() {
        reset(lib, playerAdapter);

        expect(lib.getPlayerAdapter()).andReturn(playerAdapter).times(4);
        expect(playerAdapter.getCurrentPlayers()).andAnswer(() -> Arrays.stream(CURRENT_PLAYERS)).times(4);

        replay(lib, playerAdapter);
    }

    @Test
    public void directPlayerNames() {
        mockPlayerNames();

        helper.testCaseDirect("on empty filter",
                AutoFillProvider.PlayerNames.INSTANCE,
                "",
                "Steve",
                "Herobrine",
                "Heroic_Steve");
        helper.testCaseDirect("on match many",
                AutoFillProvider.PlayerNames.INSTANCE,
                "hero",
                "Herobrine",
                "Heroic_Steve");
        helper.testCaseDirect("on match wildcard",
                AutoFillProvider.PlayerNames.INSTANCE,
                "*Steve",
                "Steve",
                "Heroic_Steve");
        helper.testCaseDirect("on match one", AutoFillProvider.PlayerNames.INSTANCE, "Herobrine", "Herobrine");
    }

    @Test
    public void callPlayerNames() {
        mockPlayerNames();

        helper.testCaseCall("on empty filter",
                AutoFillProvider.PlayerNames.class,
                "",
                "Steve",
                "Herobrine",
                "Heroic_Steve");
        helper.testCaseCall("on match many", AutoFillProvider.PlayerNames.class, "hero", "Herobrine", "Heroic_Steve");
        helper.testCaseCall("on match wildcard", AutoFillProvider.PlayerNames.class, "*Steve", "Steve", "Heroic_Steve");
        helper.testCaseCall("on match one", AutoFillProvider.PlayerNames.class, "Herobrine", "Herobrine");
    }
}
