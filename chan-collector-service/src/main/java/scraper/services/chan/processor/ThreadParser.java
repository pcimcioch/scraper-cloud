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

    private static final String BOARD_REGEX = "(?s)\\/(.*?)\\/.*";

    private static final String THREAD_ID_REGEX = ".*thread\\/([0-9]+)\\/.*";

    private static final String DATE_FORMAT = "MM/dd/yy(EEE)HH:mm:ss";

    private static final String SIZE_REGEX = ".* (\\d+)x(\\d+).*";

    /**
     * Parse thread DOM {@code document} to new {@link ThreadDs} instance.
     *
     * @param document document with thread webpage
     * @return created thread
     */
    public ThreadDs parseThread(Document document) throws IOException {
        ThreadDs thread = buildThread(document);
        thread.addPosts(buildPosts(document));

        return thread;
    }

    private ThreadDs buildThread(Document document) {
        String threadId = extractThreadId(document);
        String board = extractBoardTitle(document);
        String subject = extractSubject(document);

        return new ThreadDs(threadId, board, subject);
    }

    private String extractThreadId(Element element) {
        Element elem = element.select("link[rel=canonical]").first();
        return elem == null ? "" : StringUtils.getSingleMatch(elem.absUrl("href"), THREAD_ID_REGEX, 1, "");
    }

    private String extractBoardTitle(Element element) {
        Element elem = element.select("title").first();
        String fullBoard = elem == null ? "" : elem.text();
        return StringUtils.getSingleMatch(fullBoard, BOARD_REGEX, 1, "");
    }

    private String extractSubject(Element element) {
        Element elem = element.select("span.subject").first();
        return elem == null ? "" : elem.text();
    }

    private List<PostDs> buildPosts(Element element) {
        Elements postElements = element.select("div.post");

        List<PostDs> posts = new ArrayList<>(postElements.size());
        for (Element el : postElements) {
            posts.add(buildPostBase(el));
        }

        Map<String, List<String>> replies = extractReplies(postElements);
        applyReplies(replies, posts);

        return posts;
    }

    private Map<String, List<String>> extractReplies(Collection<Element> elements) {
        Map<String, List<String>> replies = new HashMap<>();
        for (Element element : elements) {
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

    private PostDs buildPostBase(Element element) {
        String author = extractAuthor(element);
        Date date = extractDate(element);
        String postId = extractPostId(element);
        String comment = extractComment(element);
        String fileName = extractFileName(element);
        String md5 = extractMd5(element);
        String fileLink = extractFileLink(element);
        String thumbnailLink = extractThumbnailLink(element);
        String extension = extractExtension(element);
        Integer length = extractLength(element);
        Integer width = extractWidth(element);
        String size = extractSize(element);

        return new PostDs(author, date, postId, comment, fileName, md5, fileLink, thumbnailLink, extension, length, width, size);
    }

    private String extractAuthor(Element element) {
        Element elem = element.select("div.postInfo span.nameBlock span.name").first();
        return elem == null ? "" : elem.text();
    }

    private Date extractDate(Element element) {
        Element elem = element.select("div.postInfo span.dateTime").first();
        String dateStr = elem == null ? "" : elem.text();

        try {
            return new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).parse(dateStr);
        } catch (ParseException e) {
            return new Date(0L);
        }
    }

    private String extractPostId(Element element) {
        Element elem = element.select("div.postInfo input").first();
        return elem == null ? "" : elem.attr("name");
    }

    private String extractComment(Element element) {
        Element elem = element.select("blockquote.postMessage").first();
        return elem == null ? "" : elem.text();
    }

    private String extractFileName(Element element) {
        Element elem = element.select("div.file div.fileText a").first();
        return elem == null ? "" : elem.text();
    }

    private String extractMd5(Element element) {
        Element elem = element.select("div.file a.fileThumb img").first();
        return elem == null ? "" : elem.attr("data-md5");
    }

    private String extractFileLink(Element element) {
        Element elem = element.select("div.file div.fileText a").first();
        return elem == null ? "" : elem.absUrl("href");
    }

    private String extractThumbnailLink(Element element) {
        Element elem = element.select("div.file a.fileThumb img").first();
        return elem == null ? "" : elem.absUrl("src");
    }

    private String extractExtension(Element element) {
        Element elem = element.select("div.file div.mFileInfo").first();
        return elem == null ? "" : StringUtils.splitAndJoin(elem.text(), " ", "", 2);
    }

    private String extractSize(Element element) {
        Element elem = element.select("div.file div.mFileInfo").first();
        return elem == null ? "" : StringUtils.splitAndJoin(elem.text(), " ", "", 0, 1);
    }

    private Integer extractWidth(Element element) {
        Element elem = element.select("div.file div.fileText").first();
        return elem == null ? null : StringUtils.toInteger(StringUtils.getSingleMatch(elem.text(), SIZE_REGEX, 1));
    }

    private Integer extractLength(Element element) {
        Element elem = element.select("div.file div.fileText").first();
        return elem == null ? null : StringUtils.toInteger(StringUtils.getSingleMatch(elem.text(), SIZE_REGEX, 2));
    }

    private List<String> extractReplies(Element element) {
        Elements elems = element.select("blockquote.postMessage a.quotelink");
        return FuncUtils.map(elems, el -> el.text().substring(2));
    }
}
