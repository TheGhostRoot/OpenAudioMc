package com.craftmend.openaudiomc.spigot.modules.regions.adapters;

import com.craftmend.openaudiomc.spigot.modules.regions.RegionModule;
import com.craftmend.openaudiomc.spigot.modules.regions.interfaces.AbstractRegionAdapter;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LegacyRegionAdapter extends AbstractRegionAdapter {

    public LegacyRegionAdapter(RegionModule regionModule) {
        super(regionModule);
    }

    @Override
    public Set<ProtectedRegion> getRegionsAtLocation(Location location) {
        return new HashSet<>(WGBukkit.getRegionManager(location.getWorld()).getApplicableRegions(location).getRegions());
    }

    @Override
    public Boolean doesRegionExist(String name) {
        for (World world : Bukkit.getWorlds()) {
            for (Map.Entry<String, ProtectedRegion> entry : WGBukkit.getRegionManager(world).getRegions().entrySet()) {
                if (name.equals(entry.getKey())) return true;
            }
        }
        return false;
    }
}