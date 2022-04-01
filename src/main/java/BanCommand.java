import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.User;
import org.checkerframework.checker.units.qual.A;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

public class BanCommand extends Command {

    protected BanCommand(ChatInputInteractionEvent event) {
        super(event);
    }

    @Override
    public Publisher<?> execute() {
        Mono<User> user = event.getOption("user").flatMap(ApplicationCommandInteractionOption::getValue).map(
                ApplicationCommandInteractionOptionValue::asUser).get();
        return user.flatMap(user1 -> event.reply(
                "Banning " + user1.getMention() + " in 15 seconds..."));
    }
}
