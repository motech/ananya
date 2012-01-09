package org.motechproject.bbcwt.service;

import org.motechproject.bbcwt.domain.Chapter;
import org.motechproject.bbcwt.domain.JobAidCourse;
import org.motechproject.bbcwt.domain.Lesson;
import org.motechproject.bbcwt.domain.Level;
import org.motechproject.bbcwt.domain.tree.Node;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TreeToJobAidCourse {

    private static final Map<Integer, NodeToDomainTransformer> LEVEL_TO_DATA_MANIPULATION_MAP = new HashMap();

    static {
        LEVEL_TO_DATA_MANIPULATION_MAP.put(0, new NodeToJobAidCourse());
        LEVEL_TO_DATA_MANIPULATION_MAP.put(1, new NodeToLevel());
        LEVEL_TO_DATA_MANIPULATION_MAP.put(2, new NodeToChapter());
        LEVEL_TO_DATA_MANIPULATION_MAP.put(3, new NodeToLesson());
    }

    public JobAidCourse transform(Node courseNode) {
        return (JobAidCourse)traverse(null, courseNode, 0);
    }

    private Object traverse(Object parentDomainObj, Node currentNode, int level) {
        NodeToDomainTransformer transformer = LEVEL_TO_DATA_MANIPULATION_MAP.get(level);
        Object domainObj = transformer.transform(parentDomainObj, currentNode);
        for(Node childNode : currentNode.children()) {
            traverse(domainObj, childNode, level+1);
        }
        return domainObj;
    }

    private interface NodeToDomainTransformer {
        Object transform(Object parentDomainObj, Node nodeToTransform);
    }

    private static class NodeToLesson implements NodeToDomainTransformer {
        public Object transform(Object parentDomainObj, Node nodeToTransform) {
            if(!(parentDomainObj instanceof Chapter)) {
                throw new RuntimeException("Parent object for a lesson node should be a chapter.");
            }
            Chapter chapterInWhichLessonIsToBeAdded = (Chapter)parentDomainObj;

            Lesson lesson = new Lesson();
            Map<String, Object> lessonData = nodeToTransform.data();
            lesson.setNumber((Integer)lessonData.get("number"));
            lesson.setFileName((String)lessonData.get("lesson"));

            chapterInWhichLessonIsToBeAdded.addLesson(lesson);

            return lesson;
        }
    }

    private static class NodeToChapter implements NodeToDomainTransformer {
        @Override
        public Object transform(Object parentDomainObj, Node nodeToTransform) {
            if(!(parentDomainObj instanceof Level)) {
                throw new RuntimeException("Parent object for a chapter node should be a level.");
            }
            Level levelInWhichChapterIsToBeAdded = (Level)parentDomainObj;

            Chapter chapter = new Chapter();
            Map<String, Object> chapterData = nodeToTransform.data();
            chapter.setNumber((Integer)chapterData.get("number"));
            chapter.setMenu((String)chapterData.get("menu"));
            chapter.setTitle((String)chapterData.get("introduction"));

            levelInWhichChapterIsToBeAdded.addChapter(chapter);

            return chapter;
        }
    }

    private static class NodeToLevel implements NodeToDomainTransformer {
        @Override
        public Object transform(Object parentDomainObj, Node nodeToTransform) {
            if(!(parentDomainObj instanceof JobAidCourse)) {
                throw new RuntimeException("Parent object for a Level node should be a JobAidCourse.");
            }
            JobAidCourse jobAidCourseToWhichTheLevelIsToBeAdded = (JobAidCourse)parentDomainObj;

            Map<String, Object> levelData = nodeToTransform.data();
            Level level = new Level((Integer)levelData.get("number"), (String)levelData.get("menu"));
            level.setIntroduction((String)levelData.get("introduction"));

            jobAidCourseToWhichTheLevelIsToBeAdded.addLevel(level);
            return level;
        }
    }

    private static class NodeToJobAidCourse implements NodeToDomainTransformer {
        @Override
        public Object transform(Object parentDomainObj, Node nodeToTransform) {
            Map<String, Object> jobAidCourseData = nodeToTransform.data();

            JobAidCourse jobAidCourse = new JobAidCourse(nodeToTransform.getName(),
                                                    (String)jobAidCourseData.get("introduction"),
                                                    (String)jobAidCourseData.get("menu"));

            return jobAidCourse;
        }
    }
}