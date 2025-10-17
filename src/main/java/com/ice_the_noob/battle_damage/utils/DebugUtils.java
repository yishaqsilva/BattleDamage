package com.ice_the_noob.battle_damage.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class DebugUtils
{
    public static void log(String value, boolean actionBar){
        Player player = Minecraft.getInstance().player;
        if (player != null)
            player.displayClientMessage(Component.literal(value), actionBar);
    }

    public static void log(Object value, boolean actionBar)
    {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            Minecraft.getInstance().player.displayClientMessage(Component.literal(String.valueOf(value)), actionBar);
        }
    }

    public static void log(Object value){
        log(value, false);
    }
}
