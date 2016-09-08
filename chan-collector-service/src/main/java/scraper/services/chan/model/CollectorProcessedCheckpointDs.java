package scraper.services.chan.model;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

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

    public CollectorProcessedCheckpointDs(String boardName, int lastPageIndx) {
        this.boardName = boardName;
        this.lastPageIndx = lastPageIndx;
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
}
