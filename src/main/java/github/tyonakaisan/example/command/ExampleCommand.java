package github.tyonakaisan.example.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public interface ExampleCommand {

    LiteralArgumentBuilder<CommandSourceStack> init();

    default List<String> aliases() {
        return List.of();
    }

    default String description() {
        return "";
    }
}
