package org.motechproject.bbcwt.repository;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ChaptersRespositoryTest extends SpringIntegrationTest {
    @Autowired
    private ChaptersRespository chaptersRespository;

    private Chapter chapter;

    @Before
    public void setUp(){
        chapter = new Chapter(1);
    }


    @Test
    public void shouldPersistAChapter() {

        chaptersRespository.add(chapter);
        markForDeletion(chapter);

        Chapter persistedChapter = chaptersRespository.get(chapter.getId());

        assertNotNull(persistedChapter);
        assertEquals("Chapter number should be the same which was saved.", chapter.getNumber(), persistedChapter.getNumber());
    }

    @Test
    public void shouldAssociateLessonsWithChapter() {
        Lesson lesson1 = new Lesson(1, "Lesson1");
        Lesson lesson2 = new Lesson(2, "Lesson2");

        chapter.addLesson(lesson1);
        chapter.addLesson(lesson2);

        chaptersRespository.add(chapter);
        markForDeletion(chapter);

        Chapter persistedChapter = chaptersRespository.get(chapter.getId());

        assertThat(persistedChapter.getLessons(), hasItems(lesson1,lesson2));
    }
}