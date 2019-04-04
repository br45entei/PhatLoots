package com.codisimus.plugins.phatloots.regions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 *
 * @author Codisimus
 */
public class WorldGuardRegionHook implements RegionHook {
	
	/** @return The WorldEdit plugin, if loaded */
	public static final Plugin getWorldEdit() {
		Plugin plugin = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");
		return plugin == null ? Bukkit.getPluginManager().getPlugin("WorldEdit") : plugin;
	}
	
	/** @return The WorldGuard plugin, if loaded */
	public static final Plugin getWorldGuard() {
		return Bukkit.getPluginManager().getPlugin("WorldGuard");
	}
	
	public static final Object getRegionManagerFor(World world) {
		if(getWorldEdit() != null && getWorldGuard() != null) {
			return _getRegionManagerFor(world);
		}
		return null;
	}
	
	private static final Object _getRegionManagerFor(World world) {
		if(getWorldGuard() == null) {
			return null;
		}
		if(getWorldEdit() == null) {
			return null;
		}
		try {
			//Older way was MUCH simpler >_>
			//com.sk89q.worldguard.bukkit.WorldGuardPlugin wg = com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst();
			//return wg.getRegionManager(world);
			com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst();
			com.sk89q.worldguard.protection.regions.RegionContainer container = com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer();
			com.sk89q.worldedit.bukkit.BukkitWorld wrld = new com.sk89q.worldedit.bukkit.BukkitWorld(world);
			return container.get(wrld);
		} catch(NoClassDefFoundError ex) {
			ex.printStackTrace(System.err);
			System.err.flush();
			return null;
		}
	}
	
	
    @Override
    public String getPluginName() {
        return "WorldGuard";
    }
    
    @Override
    public List<String> getRegionNames(Location loc) {
    	if(getWorldEdit() == null || getWorldGuard() == null) {
    		return new ArrayList<>();
    	}
        List<String> regionNames = new ArrayList<>(1);
        com.sk89q.worldguard.protection.managers.RegionManager regionManager = (com.sk89q.worldguard.protection.managers.RegionManager) getRegionManagerFor(loc.getWorld());
        ApplicableRegionSet applicableRegionSet = regionManager.getApplicableRegions(new Vector(loc.getX(), loc.getY(), loc.getZ()));
        Set<ProtectedRegion> regionSet = applicableRegionSet.getRegions();

        //Eliminate all parent Regions
        Iterator<ProtectedRegion> itr = applicableRegionSet.iterator();
        while (itr.hasNext()) {
            ProtectedRegion region = itr.next().getParent();
            while (region != null) {
                regionSet.remove(region);
                region = region.getParent();
            }
        }

        for (ProtectedRegion region : regionSet) {
            regionNames.add(region.getId());
        }
        return regionNames;
    }
}
