package com.github.idimabr.utils;

import com.github.idimabr.RaphaMontaria;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class WGUtil {

    public static boolean isAllowRegion(Player player){
        final Location location = player.getLocation();
        final ApplicableRegionSet regions = WorldGuardPlugin.inst().getRegionManager(location.getWorld()).getApplicableRegions(location);
        final List<String> allowedRegions = RaphaMontaria.getPlugin().getController().getAllowedRegions();
        for (ProtectedRegion protectedRegion : regions) {
            if(allowedRegions.contains(protectedRegion.getId())) return true;
        }
        return false;
    }
}
