package scraper.services.chan.processor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import scraper.common.FuncUtils;
import scraper.common.StringUtils;
import scraper.common.function.ThrowingFunction;
import scraper.services.chan.model.PostDs;
import scraper.services.chan.model.ThreadDs;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static scraper.common.FuncUtils.mapFilter;

/**
 * Service responsible for parsing DOM document of 4chan thread to {@link ThreadDs}.
 */
@Service
public class ThreadParser {

    private static final String THREAD_ID_REGEX = ".*thread\\/([0-9]+)\\/.*";

    private static final String DATE_FORMAT = "MM/dd/yy(EEE)HH:mm:ss";

    private static final String SIZE_REGEX = ".* (\\d+)x(\\d+).*";

    /**
     * Parses thread DOM {@code document} to new {@link ThreadDs} instance.
     *
     * @param threadDom document with thread webpage
     * @param settings  collection settings
     * @return created thread
     */
    public ThreadDs parseThread(Document threadDom, Settings settings) throws IOException {
        ThreadDs thread = buildThread(threadDom, settings);
        thread.addPosts(buildPosts(threadDom));

        return thread;
    }

    private ThreadDs buildThread(Document threadDom, Settings settings) {
        String threadId = extractThreadId(threadDom);
        String board = settings.getBoardName();
        String subject = extractSubject(threadDom);

        return new ThreadDs(threadId, board, subject);
    }

    private String extractThreadId(Element threadDom) {
        Element elem = threadDom.select("link[rel=canonical]").first();
        return elem == null ? "" : StringUtils.getSingleMatch(elem.absUrl("href"), THREAD_ID_REGEX, 1, "");
    }

    private String extractSubject(Element threadDom) {
        Element elem = threadDom.select("span.subject").first();
        return elem == null ? "" : elem.text();
    }

    private List<PostDs> buildPosts(Element threadDom) {
        Elements postElements = threadDom.select("div.post");

        List<PostDs> posts = new ArrayList<>(postElements.size());
        for (Element el : postElements) {
            posts.add(buildPostBase(el));
        }

        Map<String, List<String>> replies = extractReplies(postElements);
        applyReplies(replies, posts);

        return posts;
    }

    private Map<String, List<String>> extractReplies(Collection<Element> postElements) {
        Map<String, List<String>> replies = new HashMap<>();
        for (Element element : postElements) {
            List<String> replyIds = extractReplies(element);
            if (!replyIds.isEmpty()) {
                String postId = extractPostId(element);
                replies.put(postId, replyIds);
            }
        }

        return replies;
    }

    private void applyReplies(Map<String, List<String>> repliesMap, List<PostDs> posts) {
        Map<String, PostDs> postsMap = FuncUtils.toMap(posts, PostDs::getPostId, ThrowingFunction.identity());
        for (Map.Entry<String, List<String>> entry : repliesMap.entrySet()) {
            PostDs post = postsMap.get(entry.getKey());
            if (post != null) {
                List<PostDs> replies = mapFilter(entry.getValue(), postsMap::get, Objects::nonNull);
                post.addReplyTo(replies);
            }
        }
    }

    private PostDs buildPostBase(Element postElement) {
        String author = extractAuthor(postElement);
        Date date = extractDate(postElement);
        String postId = extractPostId(postElement);
        String comment = extractComment(postElement);
        String fileName = extractFileName(postElement);
        String md5 = extractMd5(postElement);
        String fileLink = extractFileLink(postElement);
        String thumbnailLink = extractThumbnailLink(postElement);
        String extension = extractExtension(postElement);
        Integer length = extractLength(postElement);
        Integer width = extractWidth(postElement);
        String size = extractSize(postElement);

        return new PostDs(author, date, postId, comment, fileName, md5, fileLink, thumbnailLink, extension, length, width, size);
    }

    private String extractAuthor(Element postElement) {
        Element elem = postElement.select("div.postInfo span.nameBlock span.name").first();
        return elem == null ? "" : elem.text();
    }

    private Date extractDate(Element postElement) {
        Element elem = postElement.select("div.postInfo span.dateTime").first();
        String dateStr = elem == null ? "" : elem.text();

        try {
            return new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).parse(dateStr);
        } catch (ParseException e) {
            return new Date(0L);
        }
    }

    private String extractPostId(Element postElement) {
        Element elem = postElement.select("div.postInfo input").first();
        return elem == null ? "" : elem.attr("name");
    }

    private String extractComment(Element postElement) {
        Element elem = postElement.select("blockquote.postMessage").first();
        return elem == null ? "" : elem.text();
    }

    private String extractFileName(Element postElement) {
        Element elem = postElement.select("div.file div.fileText a").first();
        return elem == null ? "" : elem.text();
    }

    private String extractMd5(Element postElement) {
        Element elem = postElement.select("div.file a.fileThumb img").first();
        return elem == null ? "" : elem.attr("data-md5");
    }

    private String extractFileLink(Element postElement) {
        Element elem = postElement.select("div.file div.fileText a").first();
        return elem == null ? "" : elem.absUrl("href");
    }

    private String extractThumbnailLink(Element postElement) {
        Element elem = postElement.select("div.file a.fileThumb img").first();
        return elem == null ? "" : elem.absUrl("src");
    }

    private String extractExtension(Element postElement) {
        Element elem = postElement.select("div.file div.mFileInfo").first();
        return elem == null ? "" : StringUtils.splitAndJoin(elem.text(), " ", "", 2);
    }

    private String extractSize(Element postElement) {
        Element elem = postElement.select("div.file div.mFileInfo").first();
        return elem == null ? "" : StringUtils.splitAndJoin(elem.text(), " ", "", 0, 1);
    }

    private Integer extractWidth(Element postElement) {
        Element elem = postElement.select("div.file div.fileText").first();
        return elem == null ? null : StringUtils.toInteger(StringUtils.getSingleMatch(elem.text(), SIZE_REGEX, 1));
    }

    private Integer extractLength(Element postElement) {
        Element elem = postElement.select("div.file div.fileText").first();
        return elem == null ? null : StringUtils.toInteger(StringUtils.getSingleMatch(elem.text(), SIZE_REGEX, 2));
    }

    private List<String> extractReplies(Element postElement) {
        Elements elems = postElement.select("blockquote.postMessage a.quotelink");
        return FuncUtils.map(elems, el -> el.text().substring(2));
    }
}
