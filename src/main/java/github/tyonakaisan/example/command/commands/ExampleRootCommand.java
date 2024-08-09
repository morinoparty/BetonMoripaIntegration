package github.tyonakaisan.example.command.commands;

import com.google.inject.Inject;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import github.tyonakaisan.example.command.ExampleCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

import java.util.List;

import static io.papermc.paper.command.brigadier.Commands.literal;

@SuppressWarnings("UnstableApiUsage")
@DefaultQualifier(NonNull.class)
public final class ExampleRootCommand implements ExampleCommand {

    private final ReloadCommand reloadCommand;

    @Inject
    public ExampleRootCommand(
            final ReloadCommand reloadCommand
    ) {
        this.reloadCommand = reloadCommand;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> init() {
        return literal("example")
                .then(this.reloadCommand.root());
    }

    @Override
    public List<String> aliases() {
        return List.of("ex");
    }

    @Override
    public String description() {
        return "Example of root command.";
    }
}
