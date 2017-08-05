package net.openaudiomc.utils;

import net.openaudiomc.core.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Reflection {

    private String version;

    public Reflection(Main main){
        version = main.getServer().getClass().getPackage().getName().split("\\.")[3];
        main.getLogger().info("Reflection has found MC " + version + " which is " + (isReflectionSupported() ? "" : "not ") + "supported");
        if(!isReflectionSupported()) {
            main.getLogger().info("Falling back to tellraw method because Reflection isn't supported for this version!");
        }
    }

    public boolean isReflectionSupported() {
        return version.equals("v1_12_R1") || version.equals("v1_11_R1") || version.equals("v1_10_R1") || version.equals("v1_9_R2") || version.equals("v1_9_R1")
                || version.equals("v1_8_R3") || version.equals("v1_8_R2") || version.equals("v1_8_R1") || version.equals("v1_7_R4") || version.equals("v1_7_R3")
                || version.equals("v1_7_R2") || version.equals("v1_7_R1");
    }

    public void sendChatPacket(Player p, String json) {
        if(version.equals("v1_12_R1") || version.equals("v1_11_R1") || version.equals("v1_10_R1") || version.equals("v1_9_R2") || version.equals("v1_9_R1")
          || version.equals("v1_8_R3") || version.equals("v1_8_R2") || version.equals("v1_8_R1") || version.equals("v1_7_R4") || version.equals("v1_7_R3")
          || version.equals("v1_7_R2") || version.equals("v1_7_R1")) {
            try {
                Object chatClass;
                if(version.equals("v1_8_R1") || version.equals("v1_7_R4") || version.equals("v1_7_R3") || version.equals("v1_7_R2") || version.equals("v1_7_R1")) {
                    chatClass = Class.forName("net.minecraft.server." + version + ".ChatSerializer").getMethod("a", String.class).invoke(null, json);
                } else {
                    chatClass = getDeclaredClassName(Class.forName("net.minecraft.server." + version + ".IChatBaseComponent"), "ChatSerializer").getMethod("a", String.class).invoke(null, json);
                }
                Object packet;
                if(version.equals("v1_12_R1")) {
                    Class chatEnum = Class.forName( "net.minecraft.server." + version + ".ChatMessageType" );
                    Constructor chatConstructor = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat").getConstructor(Class.forName("net.minecraft.server." + version + ".IChatBaseComponent"), chatEnum);
                    packet = chatConstructor.newInstance(chatClass, chatEnum.getEnumConstants()[0]);
                } else if(version.equals("v1_7_R4") || version.equals("v1_7_R3") || version.equals("v1_7_R2") || version.equals("v1_7_R1")) {
                    Constructor chatConstructor = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat").getConstructor(Class.forName("net.minecraft.server." + version + ".IChatBaseComponent"));
                    packet = chatConstructor.newInstance(chatClass);
                } else {
                    Constructor chatConstructor = Class.forName("net.minecraft.server." + version + ".PacketPlayOutChat").getConstructor(Class.forName("net.minecraft.server." + version + ".IChatBaseComponent"), Byte.TYPE);
                    packet = chatConstructor.newInstance(chatClass, (byte) 0);
                }

                Object handle = p.getClass().getMethod("getHandle").invoke(p);
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                playerConnection.getClass().getMethod("sendPacket", Class.forName("net.minecraft.server." + version + ".Packet")).invoke(playerConnection, packet);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchFieldException exception) {
                Main.get().getLogger().warning("Oh no! Something went wrong. Please open an issue at https://www.github.com/Mindgamednl/OpenAudioMC with this information:");
                Main.get().getLogger().warning("Reflection crash report for " + Main.get().getServer().getName() + " version " + Main.get().getServer().getVersion() + " (" +
                  Main.get().getServer().getBukkitVersion() + ") with reflection version " + version);
                Main.get().getLogger().warning("Reflection cause: " + exception.getMessage());
                Main.get().getLogger().warning("Reflection stack trace: ");
                exception.printStackTrace();
                p.sendMessage(ChatColor.RED + "Oh, something went wrong. Please check your Server Console for further actions");
            }
        }
    }

    private Class<?> getDeclaredClassName(Class<?> c, String toFind){
        for(Class<?> cl : c.getDeclaredClasses()) if(cl.getName().endsWith(toFind)) return cl;
        return null;
    }
}