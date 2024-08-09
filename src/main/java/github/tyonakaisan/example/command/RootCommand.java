package github.tyonakaisan.example.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

@SuppressWarnings("UnstableApiUsage")
public interface RootCommand {

    ArgumentBuilder<CommandSourceStack, ?> root();
}
