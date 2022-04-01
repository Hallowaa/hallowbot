import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.util.Random;

public class NFTCommand extends Command{
    private static final int MAX_UNICODE = 10000;
    private static final int CHARS_PER_ROW = 20;
    private static final int NUMBER_OF_ROWS = 8;
    protected NFTCommand(ChatInputInteractionEvent event) {
        super(event);
    }

    @Override
    public Publisher<?> execute() {
        StringBuilder result = generateNFT();
        return event.deferReply().then(event.createFollowup(result.toString())).doOnError(Throwable::printStackTrace);

    }

    private StringBuilder generateNFT() {
        StringBuilder result = new StringBuilder("```");
        int[] excluded = new int[65];
        for (int cZero = 0; cZero <= 31; cZero++) {
            excluded[cZero] = cZero;
        }
        for (int cOne = 32; cOne <= 64; cOne++) {
            excluded[cOne] = cOne + 95;
        }

        for (int charIndex = 0; charIndex < CHARS_PER_ROW * NUMBER_OF_ROWS; charIndex++) {
            int sep = charIndex % CHARS_PER_ROW;
            int unicodeNumber = getRandomWithExclusion(new Random(), 0, MAX_UNICODE, excluded);
            result.append((char) unicodeNumber);
            if (sep == CHARS_PER_ROW - 1) {
                result.append(System.lineSeparator());
            }
        }
        result.append("```");
        return result;
    }

    private int getRandomWithExclusion(Random rnd, int start, int end, int... exclude) {
        int random = start + rnd.nextInt(end - start + 1 - exclude.length);
        for (int ex : exclude) {
            if (random < ex) {
                break;
            }
            random++;
        }
        return random;
    }
}
