package scraper.services.chan.processor;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scraper.common.FuncUtils;
import scraper.services.chan.model.ThreadDs;
import scraper.services.chan.repository.ThreadDsRepository;
import scraper.services.chan.web.WebService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Service responsible for parsing DOM document of one 4chan archive page to list of {@link ThreadDs}.
 */
// TODO add tests
@Service
public class PageParser {

    private final WebService webService;

    private final ThreadParser threadParser;

    private final ThreadDsRepository threadRepository;

    @Autowired
    public PageParser(WebService webService, ThreadParser threadParser, ThreadDsRepository threadRepository) {
        this.webService = webService;
        this.threadParser = threadParser;
        this.threadRepository = threadRepository;
    }

    /**
     * Parses archive page DOM {@code document} to list of {@link ThreadDs} instances.
     *
     * @param pageDom  page document
     * @param settings collection settings
     * @return list of scrapped threads
     * @throws IOException if io failed
     */
    public List<ThreadDs> parsePage(Document pageDom, Settings settings) throws IOException {
        Set<String> threadIds = extractThreadIds(pageDom);
        List<ThreadDs> threads = new ArrayList<>(threadIds.size());
        for (String threadId : threadIds) {
            ThreadDs thread = getThread(threadId, settings);
            if (thread != null) {
                threads.add(thread);
            }
        }

        return threads;
    }

    private Set<String> extractThreadIds(Document pageDom) {
        Elements postElements = pageDom.select("div.op div.postInfo .postNum");
        return FuncUtils.mapSet(postElements, el -> el.select("a").last().text());
    }

    private ThreadDs getThread(String threadId, Settings settings) throws IOException {
        if (threadRepository.findByThreadId(threadId) != null /* TODO Optimize not to obtain whole thread*/) {
            return null;
        }
        Document threadWebPage = getThreadWebPage(threadId, settings);

        return threadParser.parseThread(threadWebPage);
    }

    private Document getThreadWebPage(String threadId, Settings settings) throws IOException {
        String url = String.format("https://yuki.la/%s/%s", settings.getBoardName(), threadId);
        return webService.getDocument(url);
    }
}
