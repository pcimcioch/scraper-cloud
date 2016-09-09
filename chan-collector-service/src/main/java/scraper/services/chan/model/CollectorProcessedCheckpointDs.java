package scraper.services.chan.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import scraper.common.Utils;

// TODO add javadoc
@NodeEntity
public class CollectorProcessedCheckpointDs {

    @GraphId
    private Long id;

    @Property(name = "boardName")
    private String boardName;

    @Property(name = "lastPageIndx")
    private int lastPageIndx;

    public CollectorProcessedCheckpointDs() {
    }

    public CollectorProcessedCheckpointDs(Long id, String boardName, int lastPageIndx) {
        this.id = id;
        this.boardName = boardName;
        this.lastPageIndx = lastPageIndx;
    }

    public CollectorProcessedCheckpointDs(String boardName, int lastPageIndx) {
        this.boardName = boardName;
        this.lastPageIndx = lastPageIndx;
    }

    public Long getId() {
        return id;
    }

    public String getBoardName() {
        return boardName;
    }

    public void setBoardName(String boardName) {
        this.boardName = boardName;
    }

    public int getLastPageIndx() {
        return lastPageIndx;
    }

    public void setLastPageIndx(int lastPageIndx) {
        this.lastPageIndx = lastPageIndx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CollectorProcessedCheckpointDs other = (CollectorProcessedCheckpointDs) o;

        return Utils.computeEq(id, other.id, boardName, other.boardName, lastPageIndx, other.lastPageIndx);
    }

    @Override
    public int hashCode() {
        return Utils.computeHash(id, boardName, lastPageIndx);
    }
}
