describe("Build Links With Siblings And Parents", function() {
    it("should link last lesson of the last chapter to the same chapter.", function() {
        var courseFromCouchDB = jobAidCourseWithOneLesson();

        Course.buildLinks(courseFromCouchDB);

        expect(courseFromCouchDB.name).toEqual("JobAidCourse");
        expect(courseFromCouchDB.data.type).toEqual("Level");
        expect(courseFromCouchDB.contents.length).toEqual(2);
        expect(courseFromCouchDB.children.length).toEqual(1);

        var level1 = courseFromCouchDB.children[0];
        var chapter1 = level1.children[0];
        var lesson1 = chapter1.children[0];

        expect(lesson1.parent).toEqual(chapter1);
        expect(chapter1.parent).toEqual(level1);
        expect(level1.parent).toEqual(courseFromCouchDB);
        expect(courseFromCouchDB.parent).toEqual(courseFromCouchDB);
    });

    it("should link last node in a level to itself as its sibling on right", function() {
        var courseFromCouchDB = jobAidCourseWithOneLesson();

        Course.buildLinks(courseFromCouchDB);

        var level1 = courseFromCouchDB.children[0];
        var chapter1 = level1.children[0];
        var lesson1 = chapter1.children[0];

        expect(courseFromCouchDB.siblingOnRight).toEqual(courseFromCouchDB);
        expect(level1.siblingOnRight).toEqual(level1);
        expect(chapter1.siblingOnRight).toEqual(chapter1);
        expect(lesson1.siblingOnRight).toEqual(lesson1);
    });

    it("should link every node to its sibling on right", function() {
        var courseFromCouchDB = jobAidCourseWithTwoLessonsInEveryChapter();

        Course.buildLinks(courseFromCouchDB);

        var level1 = courseFromCouchDB.children[0];
        var level1_chapter1 = level1.children[0];
        var level1_chapter1_lesson1 = level1_chapter1.children[0];
        var level1_chapter1_lesson2 = level1_chapter1.children[1];
        var level1_chapter2 = level1.children[1];
        var level1_chapter2_lesson1 = level1_chapter2.children[0];
        var level1_chapter2_lesson2 = level1_chapter2.children[1];

        var level2 = courseFromCouchDB.children[1];
        var level2_chapter1 = level2.children[0];
        var level2_chapter1_lesson1 = level2_chapter1.children[0];
        var level2_chapter1_lesson2 = level2_chapter1.children[1];
        var level2_chapter2 = level2.children[1];
        var level2_chapter2_lesson1 = level2_chapter2.children[0];
        var level2_chapter2_lesson2 = level2_chapter2.children[1];

        expect(level1_chapter1_lesson1.siblingOnRight).toEqual(level1_chapter1_lesson2);
        expect(level1_chapter1_lesson2.siblingOnRight).toEqual(level1_chapter2_lesson1);
        expect(level1_chapter2_lesson1.siblingOnRight).toEqual(level1_chapter2_lesson2);
        expect(level1_chapter2_lesson2.siblingOnRight).toEqual(level2_chapter1_lesson1);
        expect(level2_chapter1_lesson1.siblingOnRight).toEqual(level2_chapter1_lesson2);
        expect(level2_chapter1_lesson2.siblingOnRight).toEqual(level2_chapter2_lesson1);
        expect(level2_chapter2_lesson1.siblingOnRight).toEqual(level2_chapter2_lesson2);
        expect(level2_chapter2_lesson2.siblingOnRight).toEqual(level2_chapter2_lesson2);

        expect(level1_chapter1_lesson1.siblingOnRight.parent).toEqual(level1_chapter1);
        expect(level1_chapter1_lesson2.siblingOnRight.parent).toEqual(level1_chapter2);
        expect(level1_chapter2_lesson2.siblingOnRight.parent).toEqual(level2_chapter1);
        expect(level2_chapter2_lesson2.siblingOnRight.parent).toEqual(level2_chapter2);
    });

    it("should link every node in certification course to its sibling on right", function() {
        var courseFromCouchDB = certificationCourseWithTwoLessonsInEveryChapter();

        Course.buildLinks(courseFromCouchDB);

        var chapter_1 = courseFromCouchDB.children[0];
        var chapter_1_lesson_1 = chapter_1.children[0];
        var chapter_1_lesson_2 = chapter_1.children[1];
        var chapter_1_quiz_1 = chapter_1.children[2];
        var chapter_1_quiz_2 = chapter_1.children[3];

        var chapter_2 = courseFromCouchDB.children[1];
        var chapter_2_lesson_1 = chapter_2.children[0];
        var chapter_2_lesson_2 = chapter_2.children[1];
        var chapter_2_quiz_1 = chapter_2.children[2];
        var chapter_2_quiz_2 = chapter_2.children[3];

        expect(chapter_1.siblingOnRight).toEqual(chapter_2);
        expect(chapter_1_lesson_1.siblingOnRight).toEqual(chapter_1_lesson_2);
        expect(chapter_1_lesson_2.siblingOnRight).toEqual(chapter_1_quiz_1);
        expect(chapter_1_quiz_1.siblingOnRight).toEqual(chapter_1_quiz_2);
        expect(chapter_1_quiz_2.siblingOnRight).toEqual(chapter_2_lesson_1);

        expect(chapter_2_lesson_1.siblingOnRight).toEqual(chapter_2_lesson_2);
        expect(chapter_2_lesson_2.siblingOnRight).toEqual(chapter_2_quiz_1);
        expect(chapter_2_quiz_1.siblingOnRight).toEqual(chapter_2_quiz_2);
        expect(chapter_2_quiz_2.siblingOnRight).toEqual(chapter_2_quiz_2);
    });
});