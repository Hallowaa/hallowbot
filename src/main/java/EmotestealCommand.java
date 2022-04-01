import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.rest.util.Image;
import discord4j.rest.util.Permission;
import org.reactivestreams.Publisher;

public class EmotestealCommand extends Command {
    protected EmotestealCommand(ChatInputInteractionEvent event) {
        super(event);
    }

    @Override
    public Publisher<?> execute() {
        return event.getInteraction()
                .getGuild()
                .flatMap(guild -> guild.getSelfMember()
                        .flatMap(member -> member.getBasePermissions()
                                .flatMap(permissions -> {
            if (permissions.contains(Permission.MANAGE_EMOJIS)) {
                String url = event.getOption("link")
                        .flatMap(ApplicationCommandInteractionOption::getValue)
                        .map(ApplicationCommandInteractionOptionValue::asString).get().replace(".webp", ".png");

                String substring = url.substring(url.lastIndexOf(':') + 1, url.length() - 1);
                String finalUrl = url.matches("<:\\w+:[0-9]+>") ? "https://cdn.discordapp.com/emojis/"
                        + substring + ".png"
                        : url.matches("<a:\\w+:[0-9]+>") ? "https://cdn.discordapp.com/emojis/"
                        + substring + ".gif" : url.replace(".webp", ".png");

                String name = event.getOption("name")
                        .flatMap(ApplicationCommandInteractionOption::getValue)
                        .map(ApplicationCommandInteractionOptionValue::asString).get();
                return Image.ofUrl(finalUrl).flatMap(image
                        -> guild.createEmoji(name, image).doOnError(error
                        -> event.reply("Something went wrong! So unexpected!"))).doOnError(error
                        -> event.reply("Something went wrong! So unexpected!")).then(event.reply("Done!"));
            } else {
                return event.reply("I don't have permission to do that!");
            }
        })));
    }
}
