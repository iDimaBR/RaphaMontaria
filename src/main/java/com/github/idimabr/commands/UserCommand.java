package com.github.idimabr.commands;

import com.github.idimabr.menus.HorseMenu;
import com.github.idimabr.utils.WGUtil;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AllArgsConstructor;
import me.saiintbrisson.minecraft.ViewFrame;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class UserCommand implements CommandExecutor {

    private final ViewFrame view;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("montariahash.user")){
            sender.sendMessage("§cSem permissão!");
            return false;
        }

        if(!(sender instanceof Player)){
            sender.sendMessage("§cOnly players!");
            return false;
        }

        final Player player = (Player) sender;


        if(!WGUtil.isAllowRegion(player)){
            player.sendMessage("§cÁrea não permitida para cavalos.");
            return false;
        }

        view.open(HorseMenu.class, player);
        return false;
    }
}
