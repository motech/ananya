package org.motechproject.bbcwt.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.bbcwt.domain.Chapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChaptersRepository extends AbstractCouchRepository<Chapter> {

    @Autowired
    protected ChaptersRepository(@Qualifier("bbcwtDbConnector") CouchDbConnector db) {
        super(Chapter.class, db);
    }

    @GenerateView
    public Chapter findByNumber(int chapterNumber) {
        List<Chapter> chapters = queryView("by_number", chapterNumber);
        if(chapters!=null && !chapters.isEmpty()) {
            return chapters.get(0);
        }
        return null;
    }
}