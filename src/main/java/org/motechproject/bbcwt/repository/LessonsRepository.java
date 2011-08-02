package org.motechproject.bbcwt.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.bbcwt.domain.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

//@Repository
public class LessonsRepository extends AbstractCouchRepository<Lesson>  {
    @Autowired
    public LessonsRepository(@Qualifier("bbcwtDbConnector")  CouchDbConnector db) {
        super(Lesson.class, db);
    }
}