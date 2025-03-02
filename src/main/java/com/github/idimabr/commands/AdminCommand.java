package com.github.idimabr.commands;

import com.github.idimabr.controllers.PlayerController;
import com.github.idimabr.models.CustomHorse;
import com.github.idimabr.models.PlayerCache;
import com.github.idimabr.storage.dao.PlayerRepository;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class AdminCommand implements CommandExecutor {

    private final PlayerController controller;
    private final PlayerRepository repository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("montariahash.admin")){
            sender.sendMessage("§cSem permissão!");
            return false;
        }

        if(args.length != 3 || !args[0].equalsIgnoreCase("give")){
            sender.sendMessage("§cUtilize /horsehash give <player> <horse>");
            return false;
        }

        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if(target == null || !target.hasPlayedBefore()){
            sender.sendMessage("§cJogador não encontrado!");
            return false;
        }

        PlayerCache load = repository.load(target.getUniqueId());
        if(load == null){
            load = new PlayerCache(target.getUniqueId());
            controller.getCacheMap().put(target.getUniqueId(), load);
        }

        final Map<String, CustomHorse> horses = controller.getHorses();
        final CustomHorse targetHorse = horses.get(args[2]);
        if(targetHorse == null){
            sender.sendMessage("§cCavalo não encontrado!");
            return false;
        }

        load.getHorses().add(targetHorse);
        sender.sendMessage("§aCavalo entregue com sucesso!");
        repository.update(load);
        return false;
    }
}
