package me.tiger.simplydupe;

import java.util.Locale;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class SimplyDupe extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        PluginCommand dupeCommand = getCommand("dupe");
        if (dupeCommand == null) {
            getLogger().severe("Command 'dupe' is missing from plugin.yml.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        dupeCommand.setExecutor(this);
        getLogger().info("SimplyDupe enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("SimplyDupe disabled.");
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {
        if (!command.getName().equalsIgnoreCase("dupe")) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 0) {
            player.sendMessage("Usage: /" + label);
            return true;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType() == Material.AIR || itemInHand.getAmount() <= 0) {
            player.sendMessage("Hold an item in your main hand first.");
            return true;
        }

        ItemStack duplicate = itemInHand.clone();
        duplicate.setAmount(1);

        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(duplicate);
        dropLeftovers(player, leftovers);
        playSuccessEffect(player);

        player.sendMessage("Duplicated one " + itemInHand.getType().name().toLowerCase(Locale.ROOT) + ".");
        return true;
    }

    private void dropLeftovers(Player player, Map<Integer, ItemStack> leftovers) {
        if (leftovers.isEmpty()) {
            return;
        }

        for (ItemStack leftover : leftovers.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }

    private void playSuccessEffect(Player player) {
        Location location = player.getLocation();
        player.playSound(location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, 1.0f, 1.2f);
        player.getWorld().spawnParticle(
                Particle.HAPPY_VILLAGER,
                location.add(0.0, 1.0, 0.0),
                12,
                0.35,
                0.5,
                0.35,
                0.0
        );
    }
}
