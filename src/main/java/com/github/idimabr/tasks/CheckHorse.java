package com.github.idimabr.tasks;

import com.github.idimabr.controllers.PlayerController;
import com.github.idimabr.models.CustomHorse;
import com.github.idimabr.models.PlayerCache;
import com.github.idimabr.utils.WGUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class CheckHorse extends BukkitRunnable {

    private PlayerController controller;
    @Override
    public void run() {
        for (Map.Entry<UUID, PlayerCache> entry : controller.getCacheMap().entrySet()) {
            final UUID uuid = entry.getKey();
            final Player player = Bukkit.getPlayer(uuid);
            if(player == null) continue;

            final PlayerCache cache = entry.getValue();
            CustomHorse horse = cache.getActualHorse();
            if(horse == null) continue;
            if(WGUtil.isAllowRegion(player)) continue;

            player.sendMessage("§cÁrea não permitida para cavalos.");
            horse.remove();
        }
    }
}
