import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.command.Interaction;
import discord4j.rest.util.Image;
import discord4j.rest.util.Permission;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public class EmotestealCommand extends Command {
    protected EmotestealCommand(ChatInputInteractionEvent event) {
        super(event);
    }

    @Override
    public Publisher<?> execute() {

        Interaction interaction = event.getInteraction();

        Mono<Boolean> botHasPermissionMono = interaction.getGuild().flatMap(guild -> guild.getSelfMember()
                .flatMap(self -> self.getBasePermissions()
                        .map(permissions
                                -> permissions.contains(Permission.MANAGE_EMOJIS))));

        Mono<Boolean> userHasPermissionMono = interaction
                .getGuild()
                .flatMap(guild -> guild.getSelfMember()
                        .flatMap(self -> self.getBasePermissions()
                                .map(permissions -> permissions.contains(Permission.MANAGE_EMOJIS))));

        String name = event.getOption("name")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get();

        String url = event.getOption("link")
                .flatMap(ApplicationCommandInteractionOption::getValue)
                .map(ApplicationCommandInteractionOptionValue::asString)
                .get()
                .replace(".webp", ".png");

        return botHasPermissionMono.flatMap(botPerm
                -> userHasPermissionMono.flatMap(userPerm -> {
            if (botPerm && userPerm) {
                String substring = url.substring(url.lastIndexOf(':') + 1, url.length() - 1);
                String finalUrl = url.matches("<:\\w+:[0-9]+>") ? "https://cdn.discordapp.com/emojis/"
                        + substring + ".png"
                        : url.matches("<a:\\w+:[0-9]+>") ? "https://cdn.discordapp.com/emojis/"
                        + substring + ".gif" : url.replace(".webp", ".png");

                return Image.ofUrl(finalUrl).flatMap(image
                        -> interaction.getGuild().flatMap(guild
                                -> guild.createEmoji(name, image)
                                .onErrorStop()))
                        .then(event.reply("Done!"))
                        .onErrorResume(error
                                -> event.reply("Error adding the emote!"));
            } else {
                return event.reply("Either you or I don't have permission to do that. Too lazy to check.");
            }
        }));

    }
}
