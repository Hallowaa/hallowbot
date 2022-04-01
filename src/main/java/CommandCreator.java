import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommandCreator {
    private final GatewayDiscordClient client;
    private final long applicationId;
    private long guildId;

    CommandCreator(final GatewayDiscordClient client, final long applicationId, final long guildId) {
        this.client = client;
        this.applicationId = applicationId;
        this.guildId = guildId;
    }

    CommandCreator(final GatewayDiscordClient client, final long applicationId) {
        this.client = client;
        this.applicationId = applicationId;
    }

    public void buildInGuildFromDirectory(final String path) throws IOException {
        if (guildId == 0) {
            throw new UnsupportedOperationException("Error, guildId is 0!");
        }

        List<ApplicationCommandRequest> requests = getApplicationCommandRequests(path);

        for (ApplicationCommandRequest request : requests) {
            client.getRestClient().getApplicationService()
                    .createGuildApplicationCommand(applicationId, guildId, request)
                    .subscribe();
        }
    }

    public void buildInGlobalFromDirectory(final String path) throws IOException {
        List<ApplicationCommandRequest> requests = getApplicationCommandRequests(path);

        for (ApplicationCommandRequest request : requests) {
            client.getRestClient().getApplicationService()
                    .createGlobalApplicationCommand(applicationId, request)
                    .subscribe();
        }
    }


    private List<ApplicationCommandRequest> getApplicationCommandRequests(String path) throws IOException {
        List<ApplicationCommandRequest> requests = new ArrayList<>();
        File dir = new File(path);
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        module.addDeserializer(ImmutableApplicationCommandRequest.class, new CommandRequestDeserializer());
        mapper.registerModule(module);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

        for (File file : Objects.requireNonNull(dir.listFiles(pathname
                -> pathname.getName().toLowerCase().endsWith(".json")))) {

            String content = Files.readString(file.toPath());
            ApplicationCommandRequest request = mapper.readValue(content, ApplicationCommandRequest.class);
            requests.add(request);

        }
        return requests;
    }
}
