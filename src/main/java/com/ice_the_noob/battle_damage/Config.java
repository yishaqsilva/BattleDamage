package com.ice_the_noob.battle_damage;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = BattleDamage.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue GLOBAL_BATTLE_DAMAGE = BUILDER
            .comment("Display battle damage for all players")
            .define("BattleDamageForAll", false);

    private static final ForgeConfigSpec.BooleanValue DAMAGE_THROUGH_ARMOR = BUILDER
            .comment("Clothing can be torn even when wearing armor")
            .define("DamageThroughArmor", false);

    private static final ForgeConfigSpec.DoubleValue DAMAGE_CHANCE = BUILDER
            .comment("Probability to increase the damage level (Higher values will rip clothes faster)")
            .defineInRange("damageChance", 0.4D, 0.1D, 1.0D);
    private static final ForgeConfigSpec.IntValue MAX_DAMAGE_LEVEL = BUILDER
            .comment("Maximum level of damage")
            .defineInRange("MaxDamage", 4, 0, 4);

    private static final ForgeConfigSpec.IntValue SKIN_PIXEL_X = BUILDER
            .comment("X Coordinate of the pixel to be used as skin color")
            .defineInRange("SkinColor_X", 11, 0, 64);

    private static final ForgeConfigSpec.IntValue SKIN_PIXEL_Y = BUILDER
            .comment("Y Coordinate of the pixel to be used as skin color")
            .defineInRange("SkinColor_Y", 12, 0, 64);
    static final ForgeConfigSpec SPEC = BUILDER.build();
    public static boolean damageThroughArmor;
    public static boolean globalDamage;
    public static int maxDamageLevel;
    public static int skinPixelX;
    public static int skinPixelY;
    public static double damageChance;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        damageThroughArmor = DAMAGE_THROUGH_ARMOR.get();
        globalDamage = GLOBAL_BATTLE_DAMAGE.get();
        maxDamageLevel = MAX_DAMAGE_LEVEL.get();
        damageChance = DAMAGE_CHANCE.get();
        skinPixelX = SKIN_PIXEL_X.get();
        skinPixelY = SKIN_PIXEL_Y.get();
    }
}
