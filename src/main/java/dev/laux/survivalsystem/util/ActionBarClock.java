package dev.laux.survivalsystem.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class ActionBarClock {

    public void showRealTimeClock() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Berlin")); // Zeitzone anpassen
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = sdf.format(calendar.getTime());

        // Zeige die Zeit in der Action Bar für alle Spieler
        String actionBarMessage = "§7» §a" + time + "§7 «";
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionBarMessage));
        }
    }

}
