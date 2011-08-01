package org.motechproject.bbcwt.repository;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.Lesson;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;
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
    public void shouldReturnAChapterNumberForAGivenChapterNumber() {
        chaptersRespository.add(chapter);
        markForDeletion(chapter);

        Chapter chapterByNumber = chaptersRespository.findByNumber(1);

        assertNotNull(chapterByNumber);
        assertEquals("Chapter number should the same as what was requested.", chapterByNumber.getNumber(), chapter.getNumber());
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

        assertThat(persistedChapter, hasLessons(lesson1, lesson2));
    }

    private Matcher<Chapter> hasLesson(final Lesson lesson) {
        return new BaseMatcher<Chapter>() {
            @Override
            public boolean matches(Object item) {
                if(item instanceof Chapter){
                    Chapter chapter = (Chapter) item;
                    return CollectionUtils.exists(chapter.getLessons(), new Predicate() {
                        @Override
                        public boolean evaluate(Object o) {
                            Lesson aLesson = (Lesson) o;
                            return aLesson.getId().equals(lesson.getId());
                        }
                    });
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Lesson " + lesson.getId() + ", at location: " + lesson.getLocation() + " is not present.");
            }
        };
    }

    private Matcher<Chapter> hasLessons(Lesson... lessons) {
        List<Matcher<? extends Chapter>> matchersByLesson = new ArrayList<Matcher<? extends Chapter>>();
        for(Lesson eachLesson: lessons) {
            matchersByLesson.add(hasLesson(eachLesson));
        }
        return allOf(matchersByLesson);
    }
}