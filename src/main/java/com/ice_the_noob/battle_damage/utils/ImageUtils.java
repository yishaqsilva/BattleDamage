package com.ice_the_noob.battle_damage.utils;

import com.ice_the_noob.battle_damage.Config;
import com.ice_the_noob.battle_damage.BattleDamage;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.fml.loading.FMLPaths;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.ice_the_noob.battle_damage.utils.MiscUtils.getPlayerName;

public class ImageUtils
{
    private static final int DAMAGE_FILTER = 0xff << 24 | 0xDB1D1D;

    public static ResourceLocation[] getDamagedSkins(AbstractClientPlayer player){

        String playerName = getPlayerName(player).toLowerCase();

        //work with 5 levels of damage later on

        BufferedImage playerSkinImage = MiscUtils.getPlayerSkinOnline(getPlayerName(player));

        if (playerSkinImage == null){
            playerSkinImage = fromFile(FMLPaths.GAMEDIR.get().resolve("battle_damage").resolve(getPlayerName(player).toLowerCase() + ".png").toFile());
            if (playerSkinImage == null){
                playerSkinImage = getImage(Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(player.getGameProfile()));
            }
        }

        int skinColor;
        if (getPlayerName(player).equals(getPlayerName(Minecraft.getInstance().player))){
            skinColor = samplePixel(playerSkinImage, Config.skinPixelX, Config.skinPixelY); //apply the custom location only to the client player
        }else{
            skinColor = samplePixel(playerSkinImage, 11, 12); //otherwise use the standard default
        }

        BufferedImage damageMapImage;
        ResourceLocation damageSkin;
        ResourceLocation[] damageSkins = {null, null, null, null, null};

        for (int i = 1; i <= 4; i++){
            damageMapImage = getImage(new ResourceLocation(BattleDamage.MODID, String.format("textures/damage_map/%d.png", i)));
            damageSkin = toResourceLocation(replaceColor(damageMapImage, DAMAGE_FILTER, skinColor), String.format("%s_damage_%d", playerName, i));
            damageSkins[i] = damageSkin;
        }
        return damageSkins;
    }

    public static BufferedImage copyToTarget(BufferedImage imageA, BufferedImage imageB, int filter) //provide a color to be used as the filter
    {
        for (int y = 0; y < imageA.getHeight(); y++) {
            for (int x = 0; x < imageA.getWidth(); x++) {
                int pixelA = imageA.getRGB(x, y);
                int pixelB = imageB.getRGB(x,y);

                if (colorsMatch(pixelB, filter)){
                    imageB.setRGB(x, y,  pixelA);
                }else{
                    imageB.setRGB(x, y, pixelB);
                }
            }
        }

        return imageB;
    }
    public static BufferedImage replaceColor(BufferedImage originalImage, int sourceColor, int targetColor)
    {
        BufferedImage targetImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < originalImage.getHeight(); y++) {
            for (int x = 0; x < originalImage.getWidth(); x++) {
                int pixel = originalImage.getRGB(x, y);

                if (colorsMatch(pixel, sourceColor)) {
                    targetImage.setRGB(x, y, targetColor);
                } else {
                    targetImage.setRGB(x, y, pixel);
                }
            }
        }

        return targetImage;
    }

    public static BufferedImage getImage(ResourceLocation resourceLocation) {
        Minecraft mc = Minecraft.getInstance();

        // 1. First try TextureManager (handles player skins & other dynamic textures)
        AbstractTexture texture = mc.getTextureManager().getTexture(resourceLocation);
        if (texture instanceof DynamicTexture dynTex) {
            NativeImage nativeImage = dynTex.getPixels();
            if (nativeImage != null) {
                return toBufferedImage(nativeImage);
            }
        }
        // 2. Fallback: try ResourceManager (vanilla assets/resources)
        try {
            Resource resource = mc.getResourceManager().getResourceOrThrow(resourceLocation);
            BufferedImage img = ImageIO.read(resource.open());
            if (img != null) {
                return img;
                }
            } catch (Exception ignored) {
        }
        return null;
    }

    public static BufferedImage toBufferedImage(NativeImage nativeImage) {
        BufferedImage image = new BufferedImage(nativeImage.getWidth(), nativeImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < nativeImage.getHeight(); y++) {
            for (int x = 0; x < nativeImage.getWidth(); x++) {
                image.setRGB(x, y, nativeImage.getPixelRGBA(x, y));
            }
        }
        return image;
    }

    public static ResourceLocation toResourceLocation(BufferedImage image, String destinationName){
        DynamicTexture dynamicTexture = new DynamicTexture(toNativeImage(image));
        return Minecraft.getInstance().getTextureManager().register(destinationName, dynamicTexture);
    }

    public static NativeImage toNativeImage(BufferedImage bufferedImage){
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        NativeImage nativeImage = new NativeImage(width, height, true);

        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int argb = bufferedImage.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                int rgba = (a << 24) | (b << 16) | (g << 8) | r;
                nativeImage.setPixelRGBA(x, y, rgba);
            }
        }
        return nativeImage;
    }

    public static int samplePixel(BufferedImage image, int x, int y){
        return image.getRGB(x, y);
    }
    private static boolean colorsMatch(int colorA, int colorB){
        return (colorA & 0xFFFFFF) == (colorB & 0xFFFFFF);
    }

    public static int darkenColor(int color, double factor) {
        // Extract ARGB channels
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        // Darken RGB by factor (e.g. 0.9 for 10% darker)
        r = (int)(r * factor);
        g = (int)(g * factor);
        b = (int)(b * factor);

        // Clamp between 0–255
        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        // Recombine into ARGB
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static BufferedImage fromFile(File file){
        try {
             return ImageIO.read(file);

            }catch (Exception e){
                System.out.println(e);
                return null;
            }
        }
    }

