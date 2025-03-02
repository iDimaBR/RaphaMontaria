package com.github.idimabr.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class PlayerCache {

    private UUID uuid;
    private List<CustomHorse> horses;
    private List<UUID> friends;
    private CustomHorse actualHorse;

    public List<CustomHorseData> getHorseData(){
        return horses.stream().map($ -> new CustomHorseData($.getId(), $.getVelocity(), $.getVariant())).collect(Collectors.toList());
    }

    public PlayerCache(UUID uuid, List<CustomHorse> horses, List<UUID> friends) {
        this.uuid = uuid;
        this.horses = horses;
        this.friends = friends;
    }

    public PlayerCache(UUID uuid) {
        this.uuid = uuid;
        this.horses = new ArrayList<>();
        this.friends = new ArrayList<>();
    }
}
