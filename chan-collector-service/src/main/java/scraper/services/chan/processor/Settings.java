package scraper.services.chan.processor;

import scraper.common.Utils;
import scraper.properties.number.NumberProperty;
import scraper.properties.string.StringProperty;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.o;

/**
 * Settings for 4chan collector.
 */
public class Settings {

    @StringProperty(viewName = "Board Name", description = "Board Name", minLength = 1)
    private String boardName;

    @NumberProperty(viewName = "Max Pages", description = "Maximum number of pages to scrap at once", min = 1, required = false)
    private Integer maxPages = 20;

    public Settings() {
    }

    public Settings(String boardName, Integer maxPages) {
        this.boardName = boardName;
        this.maxPages = maxPages;
    }

    /**
     * Gets name of the 4chan board whose threads should be extracted.
     *
     * @return name of the 4chan board
     */
    public String getBoardName() {
        return boardName;
    }

    /**
     * Sets name of the 4chan board whose threads should be extracted.
     *
     * @param boardName name of the 4chan board
     */
    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    /**
     * Gets maximum number of pages to be scrapped at once.
     *
     * @return maximum number of pages. May return <tt>null</tt>
     */
    public Integer getMaxPages() {
        return maxPages;
    }

    /**
     * Sets maximum number of pages to be scrapped at once.
     *
     * @param maxPages maximum number of pages. May be <tt>null</tt>
     */
    public void setMaxPages(Integer maxPages) {
        this.maxPages = maxPages;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Settings other = (Settings) obj;

        return Utils.computeEq(boardName, other.boardName, maxPages, other.maxPages);
    }

    @Override
    public int hashCode() {
        return Utils.computeHash(boardName, maxPages);
    }
}
