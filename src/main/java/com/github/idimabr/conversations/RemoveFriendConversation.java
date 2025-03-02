package com.github.idimabr.conversations;

import com.github.idimabr.RaphaMontaria;
import com.github.idimabr.models.PlayerCache;
import com.github.idimabr.storage.dao.PlayerRepository;
import com.github.idimabr.utils.ConfigUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RemoveFriendConversation implements Prompt {

    @Override
    public String getPromptText(ConversationContext ctx) {
        final PlayerCache cache = (PlayerCache) ctx.getSessionData("cache");
        final ConfigUtil messages = RaphaMontaria.getPlugin().getConfig();
        return StringUtils.join(
                messages.getStringList("messages.remove-friend")
                        .stream()
                        .map($ -> $.replace("&","§")
                                .replace("{friends}", cache.getFriends()
                                        .stream()
                                        .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                                        .collect(Collectors.joining(", "))
                                ))
                        .collect(Collectors.toList()), "\n");
    }

    @Override
    public boolean blocksForInput(ConversationContext ctx) {
        return true;
    }

    @Override
    public Prompt acceptInput(ConversationContext ctx, String input) {
        final PlayerRepository repository = RaphaMontaria.getPlugin().getRepository();
        final Player player = (Player) ctx.getForWhom();

        final Player target = Bukkit.getPlayer(input);
        final PlayerCache cache = (PlayerCache) ctx.getSessionData("cache");

        if (cache == null) {
            player.sendRawMessage("§cErro: Seus dados não foram encontrados!");
            return END_OF_CONVERSATION;
        }

        if(target == null){
            player.sendRawMessage("§cJogador não encontrado!");
            return END_OF_CONVERSATION;
        }

        if(target == player){
            player.sendRawMessage("§cVocê não pode fazer isso com sí mesmo!");
            return END_OF_CONVERSATION;
        }

        final List<UUID> friends = cache.getFriends();
        friends.remove(target.getUniqueId());
        repository.update(cache);

        player.sendRawMessage("§aVocê removeu o jogador §f" + target.getName() + "§a dos seus cavalos.");
        return END_OF_CONVERSATION;
    }
}
