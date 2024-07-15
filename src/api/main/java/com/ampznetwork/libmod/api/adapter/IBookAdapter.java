package com.ampznetwork.libmod.api.adapter;

import com.ampznetwork.libmod.api.addon.ModComponent;
import net.kyori.adventure.text.Component;

import java.util.List;

public interface IBookAdapter extends ModComponent {
    List<Component[]> getPages();
}
