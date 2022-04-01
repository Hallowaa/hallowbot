import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.ReactiveEventAdapter;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.io.IOException;

public class Main {

    public static final String TOKEN = System.getenv("BOT_TOKEN");

    public static void main(String[] args) {
        init();
    }

    private static void init() {

        GatewayDiscordClient client = DiscordClient.create(TOKEN)
                .login()
                .block();
        assert client != null;

        createCommands(client);

        client.on(new ReactiveEventAdapter() {
            @Override
            public Publisher<?> onChatInputInteraction(ChatInputInteractionEvent event) {
                switch (event.getCommandName()) {
                    case "ping": {
                        PingCommand pingCommand = new PingCommand(event);
                        return pingCommand.execute();
                    }
                    case "emotesteal": {
                        EmotestealCommand emotestealCommand = new EmotestealCommand(event);
                        return emotestealCommand.execute();
                    }
                    case "nft": {
                        NFTCommand nftCommand = new NFTCommand(event);
                        return nftCommand.execute();
                    }
                    case "ban": {
                        BanCommand banCommand = new BanCommand(event);
                        return banCommand.execute();
                    }
                    default: {
                        return Mono.empty();
                    }
                }
            }
        }).blockLast();
    }

    public static void createCommands(GatewayDiscordClient gateway) {
        long applicationId = gateway.getRestClient().getApplicationId().block();
        CommandCreator creator = new CommandCreator(gateway, applicationId);
        try {
            creator.buildInGlobalFromDirectory("src\\main\\resources\\commands");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
