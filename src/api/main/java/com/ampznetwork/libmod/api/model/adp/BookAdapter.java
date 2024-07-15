package com.ampznetwork.libmod.api.model.adp;

import net.kyori.adventure.text.Component;

import java.util.List;

public interface BookAdapter {
    String TITLE = "Interactive LibMod Menu";
    String AUTHOR = "kaleidox@ampznetwork";

    List<Component[]> getPages();
}
