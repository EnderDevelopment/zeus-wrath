package com.enderdevelopment.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class Main extends JavaPlugin implements Listener {
    private NamespacedKey abilityKey;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Integer> hitCounts = new HashMap<>();
    private static final long COOLDOWN_MILLIS = 30_000L;

    @Override
    public void onEnable() {
        abilityKey = new NamespacedKey(this, "ability");
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        cooldowns.clear();
        hitCounts.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String name = command.getName().toLowerCase();
        if (name.equals("polarsmp")) {
            if (args.length != 2 || !args[0].equalsIgnoreCase("give") || !args[1].equalsIgnoreCase("Zeus_Wrath")) {
                sender.sendMessage(Component.text("Usage: /polarsmp give Zeus_Wrath", NamedTextColor.RED));
                return true;
            }
            if (!sender.hasPermission("polarsmp.admin")) {
                sender.sendMessage(Component.text("You do not have permission.", NamedTextColor.RED));
                return true;
            }
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("This command must be used by a player.", NamedTextColor.RED));
                return true;
            }
            player.getInventory().addItem(createZeusItem()).values()
                .forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
            player.sendMessage(Component.text("You received Zeus's Wrath.", NamedTextColor.GOLD));
            return true;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by a player.", NamedTextColor.RED));
            return true;
        }
        if (name.equals("withdraw")) {
            if (!isZeusItem(player.getInventory().getItemInMainHand())) {
                player.sendMessage(Component.text("Hold Zeus's Wrath to withdraw it.", NamedTextColor.RED));
                return true;
            }
            player.getInventory().setItemInMainHand(null);
            player.sendMessage(Component.text("Zeus's Wrath withdrawn.", NamedTextColor.YELLOW));
            return true;
        }
        if (name.equals("a1") || name.equals("a2")) {
            if (!isZeusItem(player.getInventory().getItemInMainHand())) {
                player.sendMessage(Component.text("Hold Zeus's Wrath to use its abilities.", NamedTextColor.RED));
                return true;
            }
            if (isCoolingDown(player)) return true;
            if (name.equals("a1")) lightningBarrage(player);
            else empower(player);
            cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + COOLDOWN_MILLIS);
            return true;
        }
        return false;
    }

    private ItemStack createZeusItem() {
        ItemStack item = new ItemStack(Material.HONEY_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.text("Zeus's Wrath", NamedTextColor.GOLD));
            meta.lore(java.util.List.of(
                Component.text("Drinkable ability item", NamedTextColor.GRAY),
                Component.text("/a1: Thunderstorm", NamedTextColor.YELLOW),
                Component.text("/a2: Olympian Might", NamedTextColor.YELLOW),
                Component.text("Does not drop on death", NamedTextColor.DARK_GRAY)
            ));
            meta.getPersistentDataContainer().set(abilityKey, PersistentDataType.STRING, "zeus_wrath");
            item.setItemMeta(meta);
        }
        return item;
    }

    private boolean isZeusItem(ItemStack item) {
        if (item == null || item.getType() != Material.HONEY_BOTTLE || !item.hasItemMeta()) return false;
        String id = item.getItemMeta().getPersistentDataContainer().get(abilityKey, PersistentDataType.STRING);
        return "zeus_wrath".equals(id);
    }

    private boolean isCoolingDown(Player player) {
        long remaining = cooldowns.getOrDefault(player.getUniqueId(), 0L) - System.currentTimeMillis();
        if (remaining <= 0) return false;
        player.sendMessage(Component.text("Ability cooldown: " + ((remaining + 999) / 1000) + "s", NamedTextColor.RED));
        return true;
    }

    private void lightningBarrage(Player player) {
        UUID id = player.getUniqueId();
        for (int strike = 0; strike < 4; strike++) {
            long delay = strike * 46L;
            Bukkit.getScheduler().runTaskLater(this, () -> {
                Player target = Bukkit.getPlayer(id);
                if (target == null || !target.isOnline() || target.isDead()) return;
                target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.getLocation().add(0, 1, 0), 35, 0.45, 0.8, 0.45, 0.08);
                target.getWorld().spawnParticle(Particle.CLOUD, target.getLocation(), 18, 0.35, 0.1, 0.35, 0.04);
                target.getWorld().playSound(target.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.7f, 1.5f);
                applyTrueDamage(target, 4.0);
            }, delay);
        }
        player.sendMessage(Component.text("The storm answers your call.", NamedTextColor.AQUA));
    }

    private void empower(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 1, false, true, true));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 300, 1, false, true, true));
        player.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation().add(0, 1, 0), 90, 0.7, 1.0, 0.7, 0.12);
        player.getWorld().spawnParticle(Particle.END_ROD, player.getLocation().add(0, 1, 0), 30, 0.45, 0.8, 0.45, 0.04);
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.8f, 1.2f);
        player.sendMessage(Component.text("Olympian Might surges through you.", NamedTextColor.AQUA));
    }

    private void applyTrueDamage(LivingEntity target, double damage) {
        double remainingHealth = Math.max(0.0, target.getHealth() - damage);
        target.setHealth(remainingHealth);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker) || !isZeusItem(attacker.getInventory().getItemInMainHand())) return;
        UUID id = attacker.getUniqueId();
        int hits = hitCounts.getOrDefault(id, 0) + 1;
        if (hits >= 5) {
            hitCounts.put(id, 0);
            if (event.getEntity() instanceof LivingEntity target) {
                target.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, target.getLocation().add(0, 1, 0), 28, 0.3, 0.7, 0.3, 0.08);
                target.getWorld().strikeLightningEffect(target.getLocation());
                applyTrueDamage(target, 3.0);
            }
        } else {
            hitCounts.put(id, hits);
        }
    }

    @EventHandler
    public void onDrink(PlayerInteractEvent event) {
        if (!isZeusItem(event.getItem())) return;
        event.getPlayer().sendMessage(Component.text("Use /a1 or /a2 to activate Zeus's Wrath.", NamedTextColor.YELLOW));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!isZeusItem(event.getItemDrop().getItemStack())) return;
        event.setCancelled(true);
        event.getPlayer().sendMessage(Component.text("Zeus's Wrath cannot be dropped. Use /withdraw to remove it.", NamedTextColor.RED));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().removeIf(this::isZeusItem);
    }
}
