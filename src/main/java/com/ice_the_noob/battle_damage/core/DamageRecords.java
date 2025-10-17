package com.ice_the_noob.battle_damage.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public class DamageRecords
{
    private static final HashMap<Player, ResourceLocation[]> MAP = new HashMap<>();
    private static final HashMap<Player, Integer> DAMAGE_MAP = new HashMap<>();
    public static boolean hasPlayer(Player player){
        return MAP.containsKey(player);
    }
    public static void setSkins(Player player, ResourceLocation[] skins){
        MAP.put(player, skins);
    }
    public static ResourceLocation[] getSkins(Player player){
        return MAP.get(player);
    }
    public static void incrementDamageLevel(Player player){
        if (DAMAGE_MAP.get(player) != null){
            if (DAMAGE_MAP.get(player) < 4) {
                DAMAGE_MAP.put(player, DAMAGE_MAP.get(player) + 1);
            }
        }else{
            DAMAGE_MAP.put(player, 0);
        }
    }
    public static int getDamageLevel(Player player){
        return DAMAGE_MAP.get(player);
    }

    public static void initDamageLevel(Player player){
        DAMAGE_MAP.put(player, 0);
    }

}
