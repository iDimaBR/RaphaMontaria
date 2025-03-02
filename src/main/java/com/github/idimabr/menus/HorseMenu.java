package com.github.idimabr.menus;

import com.github.idimabr.RaphaMontaria;
import com.github.idimabr.controllers.PlayerController;
import com.github.idimabr.conversations.NewFriendConversation;
import com.github.idimabr.conversations.RemoveFriendConversation;
import com.github.idimabr.models.CustomHorse;
import com.github.idimabr.models.PlayerCache;
import com.github.idimabr.utils.ConfigUtil;
import com.github.idimabr.utils.ItemBuilder;
import me.saiintbrisson.minecraft.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import java.util.stream.Collectors;

public class HorseMenu extends PaginatedView<CustomHorse> {

    private final ConfigUtil config;
    private final PlayerController controller;

    private String[] LAYOUT = {
            "XXXXXXXXX",
            "XOOOOOOOX",
            "XOOOOOOOX",
            "XOOOOOOOX",
            "XXXXXXXXX"
    };

    public HorseMenu(RaphaMontaria plugin, int row, String title) {
        super(row, title);
        scheduleUpdate(5);
        setCancelOnClick(true);
        setLayout(LAYOUT);
        this.config = plugin.getConfig();
        this.controller = plugin.getController();
    }

    @Override
    public void onRender(@NotNull ViewContext context){
        final Player player = context.getPlayer();
        final PlayerCache cache = controller.getCacheMap().get(player.getUniqueId());

        final ConfigurationSection section = config.getConfigurationSection("menu.items");
        for (String identifier : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(identifier);

            final String name = item.getString("name").replace("&","§");
            final ItemBuilder builder = new ItemBuilder(item.getString("material")).setName(name);

            if(!item.isSet("slot")) continue;
            final int slot = item.getInt("slot");

            if(item.isSet("data"))
                builder.setDurability((short) item.getInt("data"));

            if(item.isSet("name"))
                builder.setName(item.getString("name").replace("&", "§"));

            if(item.isSet("lore"))
                builder.setLore(item.getStringList("lore").stream()
                        .map($ -> $.replace("&", "§"))
                        .collect(Collectors.toList()));

            context.slot(slot, builder.build()).onClick(e -> {
                if(cache == null){
                    player.sendMessage("§cNão foram encontrados seus dados.");
                    return;
                }
                
                Prompt prompt = null;
                switch (identifier){
                    case "add-friend":
                        prompt = new NewFriendConversation();
                        break;
                    case "remove-friend":
                        prompt = new RemoveFriendConversation();
                        break;
                    case "list-friend":
                        break;
                    default:
                        return;
                }

                if(prompt != null) {
                    final Conversation conversation = new ConversationFactory(RaphaMontaria.getPlugin())
                            .withFirstPrompt(prompt)
                            .withTimeout(30)
                            .withLocalEcho(false)
                            .withEscapeSequence("cancelar")
                            .addConversationAbandonedListener(event -> {
                                if (!event.gracefulExit()) {
                                    player.sendMessage("§cConversa cancelada.");
                                }
                            })
                            .buildConversation(player);
                    conversation.getContext().setSessionData("cache", cache);
                    conversation.begin();
                    return;
                }

                player.sendMessage(
                        StringUtils.join(config.getStringList("messages.list-friend")
                                .stream()
                                .map($ -> $.replace("&","§")
                                        .replace("{friends}", cache.getFriends()
                                                .stream()
                                                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                                                .collect(Collectors.joining(", "))
                                        )
                                ).collect(Collectors.toList()), "\n")
                );
            });
        }

        context.paginated().setSource(cache.getHorses());
    }

    @Override
    protected void onItemRender(@NotNull PaginatedViewSlotContext<CustomHorse> context, @NotNull ViewItem viewItem, @NotNull CustomHorse custom) {
        if(custom.getItem() == null) return;

        viewItem.withItem(custom.getItem()).onClick($ -> {
            final Player player = $.getPlayer();

            final PlayerCache playerCache = controller.getCacheMap().get(player.getUniqueId());
            if(playerCache == null){
                player.sendMessage("§cNão foram encontrados seus dados.");
                return;
            }

            if(custom.isSpawn()){
                player.sendMessage("§cSeu cavalo foi desativado.");
                custom.remove();
                return;
            }

            final Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
            horse.setVariant(horse.getVariant());
            horse.setVelocity(new Vector(0, custom.getVelocity(), 0));
            horse.setCanPickupItems(false);
            horse.setAdult();
            horse.setCustomNameVisible(false);
            horse.setOwner(player);
            horse.setRemoveWhenFarAway(false);
            horse.setTamed(true);
            horse.setColor(Horse.Color.BLACK);
            horse.setStyle(Horse.Style.WHITE_DOTS);
            horse.setMetadata("customHorse", new FixedMetadataValue(RaphaMontaria.getPlugin(), player.getUniqueId().toString()));
            custom.setEntity(horse);
            custom.setSpawn(true);
            playerCache.setActualHorse(custom);
            player.sendMessage("§aSeu cavalo foi chamado.");
        });
    }
}

