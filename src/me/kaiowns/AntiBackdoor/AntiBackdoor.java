package me.kaiowns.antibackdoor;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.event.EventHandler;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.Player;
import java.util.Arrays;
import java.util.List;

public class AntiBackdoor extends PluginBase implements Listener {

    private final List<String> blockedCommands = Arrays.asList(
        "pl", "plugins", "reload", "stop", "exec", "eval"
    );

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info(TextFormat.GREEN + "AntiBackdoor plugin enabled!");
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage().toLowerCase();
        String[] parts = message.split(" ");
        String command = parts[0].substring(1); // Remove "/"

        if (command.equals("op")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(TextFormat.RED + "Only the console can use this command.");
            getLogger().warning("Blocked unauthorized OP attempt by: " + event.getPlayer().getName());
            return;
        }

        if (blockedCommands.contains(command)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(TextFormat.RED + "This command is restricted for security reasons.");
            getLogger().warning("Blocked suspicious command: " + event.getMessage() + " from " + event.getPlayer().getName());
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        String[] parts = event.getCommand().toLowerCase().split(" ");
        String command = parts[0];

        if (command.equals("op")) {
            if (sender instanceof ConsoleCommandSender) {
                getLogger().info("Console has used /op. Proceeding.");
                return; 
            } else {
                event.setCancelled(true);
                sender.sendMessage(TextFormat.RED + "You are not allowed to use this command.");
                getLogger().warning("Blocked OP attempt from: " + sender.getName());
            }
        }

        if (blockedCommands.contains(command)) {
            event.setCancelled(true);
            sender.sendMessage(TextFormat.RED + "This command is restricted for security reasons.");
            getLogger().warning("Blocked suspicious server command: " + event.getCommand());
        }
    }
}
