import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CommandRequestDeserializer extends StdDeserializer<ImmutableApplicationCommandRequest> {

    public CommandRequestDeserializer() {
        super(ApplicationCommandRequest.class);
    }

    @Override
    public ImmutableApplicationCommandRequest deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        String name = node.get("name").asText();
        String description = node.get("description").asText();

        List<ApplicationCommandOptionData> optionsData = new ArrayList<>();

        for (Iterator<JsonNode> it = node.get("options").elements(); it.hasNext(); ) {

            JsonNode jsonNode = it.next();
            String optionName = jsonNode.get("name").asText();
            String optionDescription = jsonNode.get("description").asText();
            int type = jsonNode.get("type").asInt();
            boolean required = jsonNode.get("required").asBoolean();

            ApplicationCommandOptionData optionData = ApplicationCommandOptionData.builder()
                    .name(optionName)
                    .description(optionDescription)
                    .type(type)
                    .required(required).build();

            optionsData.add(optionData);
        }

        return ApplicationCommandRequest.builder()
                .name(name)
                .description(description)
                .options(optionsData)
                .build();

    }


}
