package com.ice_the_noob.battle_damage;

import com.ice_the_noob.battle_damage.core.DamageRecords;
import com.ice_the_noob.battle_damage.utils.MiscUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;
import java.util.Random;

@Mod(BattleDamage.MODID)
public class BattleDamage
{
    public static final String MODID = "battle_damage";
    private static final List<ResourceKey<DamageType>> SHARP_DAMAGE_TYPES = List.of(DamageTypes.ARROW, DamageTypes.CACTUS, DamageTypes.EXPLOSION,
            DamageTypes.FALLING_STALACTITE, DamageTypes.FIREBALL, DamageTypes.IN_FIRE, DamageTypes.MOB_ATTACK, DamageTypes.LIGHTNING_BOLT, DamageTypes.THORNS, DamageTypes.TRIDENT);

    public BattleDamage()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        if (Config.globalDamage) {
            MinecraftForge.EVENT_BUS.addListener(this::tickGlobalDamage);
        }else {
            MinecraftForge.EVENT_BUS.addListener(this::tickLocalDamage);
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    public void tickGlobalDamage(LivingDamageEvent event){
        if (event.getEntity() instanceof Player player){
            if (new Random().nextDouble(0,1) < Config.damageChance){
                DamageRecords.incrementDamageLevel(player);
            }
        }
    }

    public void tickLocalDamage(LivingDamageEvent event){
        if (event.getEntity() instanceof Player player) {
            if (Minecraft.getInstance().player != null && MiscUtils.isLocalPlayer(player)) {
                if (new Random().nextDouble(0, 1) < Config.damageChance) {
                    if (SHARP_DAMAGE_TYPES.stream().anyMatch( damageTypeResourceKey -> event.getSource().is(damageTypeResourceKey)))
                        DamageRecords.incrementDamageLevel(player);
                }
            }
        }
    }
}