package com.github.idimabr.listeners;

import com.github.idimabr.controllers.PlayerController;
import com.github.idimabr.models.CustomHorse;
import com.github.idimabr.models.PlayerCache;
import com.github.idimabr.storage.dao.PlayerRepository;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class LoadListener implements Listener {

    private PlayerController controller;
    private PlayerRepository repository;

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        final Player player = e.getPlayer();
        PlayerCache loaded = repository.load(player.getUniqueId());
        if(loaded == null) loaded = new PlayerCache(player.getUniqueId());

        final Map<UUID, PlayerCache> dataMap = controller.getCacheMap();
        dataMap.put(player.getUniqueId(), loaded);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        final Player player = e.getPlayer();
        final Map<UUID, PlayerCache> cacheMap = controller.getCacheMap();
        final PlayerCache loaded = cacheMap.get(player.getUniqueId());
        if(loaded == null) return;

        for (CustomHorse horse : loaded.getHorses()) {
            if(horse.isSpawn() || horse.getEntity() != null) horse.remove();
        }

        repository.update(loaded);
        cacheMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onHit(EntityDamageEvent e){
        if(e.getEntityType() != EntityType.HORSE) return;

        final Horse horse = (Horse) e.getEntity();
        if(!horse.hasMetadata("customHorse")) return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e){
        final Player player = e.getPlayer();
        final Map<UUID, PlayerCache> cacheMap = controller.getCacheMap();
        final PlayerCache playerCache = cacheMap.get(player.getUniqueId());
        if(playerCache == null) return;

        final CustomHorse horse = playerCache.getActualHorse();
        if(horse == null) return;
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) return;

        horse.remove();
        player.sendMessage("§c§lAVISO: §7Seu cavalo foi removido por causa do teleporte.");
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) return;

        final Player player = (Player) e.getEntity();
        final Entity entity = e.getMount();
        if (entity.getType() != EntityType.HORSE) return;

        final Horse horse = (Horse) entity;
        if (!horse.hasMetadata("customHorse")) return;

        final Map<UUID, PlayerCache> cacheMap = controller.getCacheMap();
        final UUID owner = UUID.fromString(horse.getMetadata("customHorse").get(0).asString());
        final PlayerCache playerCache = cacheMap.get(owner);
        if (playerCache == null) {
            e.setCancelled(true);
            return;
        }

        if (player.getUniqueId().equals(owner) || playerCache.getFriends().contains(player.getUniqueId())) {
            if (horse.getInventory().getSaddle() == null) {
                e.setCancelled(true);
                player.sendMessage("§cVocê precisa ter uma sela no cavalo para montar.");
                return;
            }

            player.playSound(player.getLocation(), Sound.IRONGOLEM_THROW, 0.3f, 0.3f);
            return;
        }

        player.sendMessage("§cVocê não tem permissão para montar nesse cavalo.");
        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
        e.setCancelled(true);
    }

}
