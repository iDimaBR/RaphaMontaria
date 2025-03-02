package com.github.idimabr.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Horse;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Getter
@Setter
public class CustomHorse extends CustomHorseData {

    private boolean spawn;
    private Horse entity;
    private ItemStack item;

    public CustomHorse(int id, double velocity, String variant) {
        super(id, velocity, variant);
        this.spawn = false;
        this.entity = null;
        this.item = null;
    }

    public void remove(){
        if(this.entity == null) return;
        if(this.entity.getEquipment().getArmorContents() != null){
            for (ItemStack drop : this.entity.getEquipment().getArmorContents())
                this.entity.getLocation().getWorld().dropItemNaturally(this.entity.getLocation(), drop);
        }
        this.entity.remove();
        this.spawn = false;
    }
}
