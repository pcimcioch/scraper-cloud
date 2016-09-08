package scraper.services.chan.web;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RefreshScope
public class WebService {

    private final String userAgent;

    private final String referrer;

    private final int timeout;

    @Autowired
    public WebService(@Value("${scraper.web.user-agent}") String userAgent, @Value("${scraper.web.referrer}") String referrer, @Value("${scraper.web.timeout}") int timeout) {
        this.userAgent = userAgent;
        this.referrer = referrer;
        this.timeout = timeout;
    }

    /**
     * Get DOM document from given url.
     *
     * @param url url
     * @return dom document
     * @throws IOException if connection failed. See {@link Connection#execute()} for details
     */
    public Document getDocument(String url) throws IOException {
        return getStandardResponse(url);
    }

    private Document getStandardResponse(String url) throws IOException {
        return Jsoup.connect(url).userAgent(userAgent).referrer(referrer).timeout(timeout).get();
    }
}
