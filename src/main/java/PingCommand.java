import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.spec.InteractionApplicationCommandCallbackReplyMono;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Random;

public class PingCommand extends Command {

    protected PingCommand(ChatInputInteractionEvent event) {
        super(event);
    }

    @Override
    public Publisher<?> execute() {
        Random rand = new Random();
        int n = rand.nextInt(101);
        if (n <= 7) {
            return event.reply("I missed!");
        } else {
            return event.reply("Pong");
        }
    }
}
