import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public abstract class Command {
    protected final ChatInputInteractionEvent event;
    protected Command(ChatInputInteractionEvent event) {
        this.event = event;
    }

    public Publisher<?> execute() {
        return Mono.empty();
    }
}
