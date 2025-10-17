package com.ice_the_noob.battle_damage.event;

import com.ice_the_noob.battle_damage.BattleDamage;
import com.ice_the_noob.battle_damage.core.DamageLayer;
import com.ice_the_noob.battle_damage.core.DamageRecords;
import com.ice_the_noob.battle_damage.utils.ImageUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

public class Client
{
    @Mod.EventBusSubscriber(modid = BattleDamage.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents
    {
        @SubscribeEvent
        public static void generateDamagedSkins(TickEvent.PlayerTickEvent event){

            if (event.side != LogicalSide.CLIENT) return;
            Player player = event.player;
            if (!DamageRecords.hasPlayer(player)) {
                ResourceLocation[] damagedSkins = ImageUtils.getDamagedSkins((AbstractClientPlayer) player);
                DamageRecords.setSkins(player, damagedSkins);
                DamageRecords.initDamageLevel(player);
            }
        }

        @SubscribeEvent
        public static void resetOnDeath(LivingDeathEvent event){
            if (event.getEntity() instanceof Player player){
                DamageRecords.initDamageLevel(player);
            }
        }
    }

    @Mod.EventBusSubscriber(modid = BattleDamage.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void registerCustomRenderLayers(EntityRenderersEvent.AddLayers event)
        {
            for (String skinName : event.getSkins()) {
                LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> playerRenderer = event.getSkin(skinName);
                if (playerRenderer != null) {
                    playerRenderer.addLayer(new DamageLayer(playerRenderer));
                }
            }
        }
    }

}