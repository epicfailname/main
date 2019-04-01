package seedu.address.logic.commands;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Gets top stories or searches for given keywords on Google News.
 */
public class GoogleNewsCommand extends FeedCommand {

    private static final String GOOGLE_NEWS_TOP_STORIES_RSS_LINK = "https://news.google.com/rss";
    private static final String GOOGLE_NEWS_SEARCH_RSS_LINK = "https://news.google.com/rss/search?q=%s";

    public static final String COMMAND_WORD = "googlenews";
    public static final String COMMAND_ALIAS = "gnews";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Gets top stories or searches for given keywords on Google News.\n"
            + "Parameters: [KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD;

    public GoogleNewsCommand() {
        super(GOOGLE_NEWS_TOP_STORIES_RSS_LINK);
    }

    public GoogleNewsCommand(String keywords) throws UnsupportedEncodingException {
        super(String.format(GOOGLE_NEWS_SEARCH_RSS_LINK, URLEncoder.encode(keywords, "UTF-8")));
    }

}
