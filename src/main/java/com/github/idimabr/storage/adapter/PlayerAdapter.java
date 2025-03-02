package com.github.idimabr.storage.adapter;

import com.github.idimabr.RaphaMontaria;
import com.github.idimabr.models.CustomHorse;
import com.github.idimabr.models.CustomHorseData;
import com.github.idimabr.models.PlayerCache;
import com.google.gson.reflect.TypeToken;
import com.henryfabio.sqlprovider.executor.adapter.SQLResultAdapter;
import com.henryfabio.sqlprovider.executor.result.SimpleResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.idimabr.RaphaMontaria.GSON;

public class PlayerAdapter implements SQLResultAdapter<PlayerCache> {

    @Override
    public PlayerCache adaptResult(SimpleResultSet rs) {

        final UUID uuid = UUID.fromString(rs.get("uuid"));
        final List<CustomHorseData> horses = GSON.fromJson(
                (String) rs.get("horses"),
                new TypeToken<List<CustomHorseData>>(){}.getType()
        );

        final List<CustomHorse> horseList = new ArrayList<>();
        for (CustomHorseData horse : horses) {
            final CustomHorse customHorse = new CustomHorse(horse.getId(), horse.getVelocity(), horse.getVariant());
            final CustomHorse originalHorse = RaphaMontaria.getPlugin().getController().getHorses().values().stream().filter($ -> $.getId() == horse.getId()).findAny().orElse(customHorse);

            customHorse.setItem(originalHorse.getItem());
            customHorse.setSpawn(false);
            customHorse.setEntity(null);
            horseList.add(customHorse);
        }

        final List<UUID> friends = GSON.fromJson(
                (String) rs.get("friends"),
                new TypeToken<List<UUID>>(){}.getType()
        );

        return new PlayerCache(uuid, horseList, friends);
    }
}
