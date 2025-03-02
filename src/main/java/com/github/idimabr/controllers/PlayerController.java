package com.github.idimabr.controllers;

import com.github.idimabr.models.CustomHorse;
import com.github.idimabr.models.PlayerCache;
import com.github.idimabr.utils.ConfigUtil;
import com.github.idimabr.utils.ItemBuilder;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class PlayerController {

    private final ConfigUtil config;
    private Map<UUID, PlayerCache> cacheMap = Maps.newHashMap();
    private Map<String, CustomHorse> horses = Maps.newHashMap();
    private List<String> allowedRegions = new ArrayList<>();
    private int limitHorse;

    public void load(){
        this.limitHorse = config.getInt("player-config.limit", 3);
        this.allowedRegions = config.getStringList("allowed-regions");

        final ConfigurationSection section = config.getConfigurationSection("horses");
        for (String key : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(key);

            if(!item.isSet("id")) continue;

            final int id = item.getInt("id");
            final double velocity = item.getDouble("velocity", 1.0);
            final String variant = item.getString("variant", "HORSE");

            final ItemBuilder builder = new ItemBuilder(item.getString("item.material"));

            if(item.isSet("item.data"))
                builder.setDurability((short) item.getInt("item.data"));

            if(item.isSet("item.name"))
                builder.setName(item.getString("item.name"));

            if(item.isSet("item.lore"))
                builder.setLore(item.getStringList("item.lore")
                        .stream().map($ -> $.replace("&","§"))
                        .collect(Collectors.toList()));

            final ItemStack build = builder.build();

            final CustomHorse horse = new CustomHorse(id, velocity, variant);
            horse.setItem(build);

            horses.put(key, horse);
            System.out.println("Cavalo '" + key + "' carregado.");
        }
    }

}
