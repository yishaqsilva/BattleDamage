package com.ice_the_noob.battle_damage.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.loading.FMLPaths;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

public class MiscUtils
{
    public static void generateImage(File file, BufferedImage image){
        try {
            ImageIO.write(image, "png", file);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public static String getPlayerName(Player player){
        return player.getName().getString();
    }

    public static ResourceLocation getDefaultSkin(Player player){
        return Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(player.getGameProfile());
    }

    public static BufferedImage getPlayerSkinOnline(String username) {
        try {
            // 1️⃣ Get UUID
            String uuidJson = readUrl("https://api.mojang.com/users/profiles/minecraft/" + username);
            if (uuidJson == null || uuidJson.isEmpty()) {
                System.err.println("Player not found: " + username); //most likely a cracked player
                return null;
            }

            JsonObject uuidObj = JsonParser.parseString(uuidJson).getAsJsonObject();
            String uuid = uuidObj.get("id").getAsString();

            // 2️⃣ Get profile with textures
            String profileJson = readUrl("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
            JsonObject profileObj = JsonParser.parseString(profileJson).getAsJsonObject();
            JsonObject textureProperty = profileObj.getAsJsonArray("properties").get(0).getAsJsonObject();

            // 3️⃣ Decode Base64 texture data
            String base64Value = textureProperty.get("value").getAsString();
            String decoded = new String(Base64.getDecoder().decode(base64Value));

            JsonObject textures = JsonParser.parseString(decoded)
                    .getAsJsonObject().getAsJsonObject("textures");
            JsonObject skin = textures.getAsJsonObject("SKIN");
            String skinUrl = skin.get("url").getAsString();

            // 4️⃣ Download and return as BufferedImage
            try (InputStream in = new URL(skinUrl).openStream()) {
                return ImageIO.read(in);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readUrl(String urlString) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                sb.append(line);
            return sb.toString();
        }
    }

    public static boolean isLocalPlayer(Player player){
        return player.getName().getString().equals(Minecraft.getInstance().player.getName().getString());
    }
}
