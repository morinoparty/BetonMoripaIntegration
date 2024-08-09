package github.tyonakaisan.example.command.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import github.tyonakaisan.example.command.RootCommand;
import github.tyonakaisan.example.config.ConfigFactory;
import github.tyonakaisan.example.message.Messages;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import static io.papermc.paper.command.brigadier.Commands.literal;

@SuppressWarnings("UnstableApiUsage")
@DefaultQualifier(NonNull.class)
public final class ReloadCommand implements RootCommand {

    private final ConfigFactory configFactory;
    private final Messages messages;

    @Inject
    public ReloadCommand(
            final ConfigFactory configFactory,
            final Messages messages
    ) {
        this.configFactory = configFactory;
        this.messages = messages;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> root() {
        return literal("reload")
                .requires(source -> source.getSender().hasPermission("example.command.reload"))
                .executes(context -> {
                    final CommandSender sender = context.getSource().getSender();
                    this.configFactory.reloadPrimaryConfig();
                    this.messages.reloadMessage();

                    sender.sendMessage(Messages.translate("example.reload", sender));

                    return Command.SINGLE_SUCCESS;
                });
    }
}