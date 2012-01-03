describe("Build Links With Siblings And Parents", function() {
it("should link last lesson of the last chapter to the same chapter.", function() {
    var courseFromCouchDB = courseWithOneLesson();

    buildLinksWithSiblingsAndParents(courseFromCouchDB);

    expect(courseFromCouchDB.data.introduction).toEqual("Introduction.wav");
    expect(courseFromCouchDB.data.menu).toEqual("MenuLevels.wav");
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
        var courseFromCouchDB = courseWithOneLesson();

        buildLinksWithSiblingsAndParents(courseFromCouchDB);

        var level1 = courseFromCouchDB.children[0];
        var chapter1 = level1.children[0];
        var lesson1 = chapter1.children[0];

        expect(courseFromCouchDB.siblingOnRight).toEqual(courseFromCouchDB);
        expect(level1.siblingOnRight).toEqual(level1);
        expect(chapter1.siblingOnRight).toEqual(chapter1);
        expect(lesson1.siblingOnRight).toEqual(lesson1);
    });

    it("should link every node to its sibling on right", function() {
        var courseFromCouchDB = courseWithTwoLessonsInEveryChapter();

        buildLinksWithSiblingsAndParents(courseFromCouchDB);

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
}); 

function courseWithOneLesson() {
    return {
            //Course Starts
            "data":
            {
                "introduction" : "Introduction.wav",
                "menu" : "MenuLevels.wav"
            },
            //Levels
            "children" :
            [
                //Level 1
                {
                    "data" :
                    {
                        "introduction" : "IntroductionLevel1.wav",
                        "menu" : "MenuLevel1Chapters.wav",
                    },
                    //Chapters
                    "children" :
                    [
                        //Chapter 1
                        {
                            "data" :
                            {
                                "introduction" : "IntroductionLevel1Chapter1.wav",
                                "menu" : "MenuLevel1Chapter1Lessons.wav",
                            },
                            //Lessons
                            "children" :
                            [
                                //lesson 1
                                {
                                    "data" :
                                    {
                                        "lesson": "chapter_1_lesson_1.wav",
                                    },
                                    "children" :
                                    [
                                    ],
                                }
                            ]
                        }
                    ]
                }
            ]
            //Course Ends
    };
}

function courseWithTwoLessonsInEveryChapter() {
    return {
            // Course Starts
            "data":
            {
                "introduction" : "Introduction.wav",
                "menu" : "MenuLevels.wav"
            },
            // Levels
            "children" :
            [
                // Level 1
                {
                    "data" :
                    {
                        "introduction" : "IntroductionLevel1.wav",
                        "menu" : "MenuLevel1Chapters.wav",
                    },
                    //Chapters
                    "children" :
                    [
                        //Chapter 1
                        {
                            "data" :
                            {
                                "introduction" : "IntroductionLevel1Chapter1.wav",
                                "menu" : "MenuLevel1Chapter1Lessons.wav",
                            },
                            //Lessons
                            "children" :
                            [
                                // Lesson 1
                                {
                                    "data" :
                                    {
                                        "lesson": "chapter_1_lesson_1.wav",
                                    },
                                    "children" :
                                    [
                                    ],
                                },
                                // Lesson 2
                                {
                                    "data" :
                                    {
                                        "lesson": "chapter_1_lesson_2.wav",
                                    },
                                    "children" :
                                    [
                                    ],
                                }
                            ]
                        },
                        // Chapter 2
                        {
                            "data" :
                            {
                                "introduction" : "IntroductionLevel1Chapter2.wav",
                                "menu" : "MenuLevel1Chapter2Lessons.wav",
                            },
                            //Lessons
                            "children" :
                            [
                                // Lesson 1
                                {
                                    "data" :
                                    {
                                        "lesson": "chapter_2_lesson_1.wav",
                                    },
                                    "children" :
                                    [
                                    ],
                                },
                                // Lesson 2
                                {
                                    "data" :
                                    {
                                        "lesson": "chapter_2_lesson_2.wav",
                                    },
                                    "children" :
                                    [
                                    ],
                                }
                            ]
                        }
                    ]
                },
                // Level 2
                {
                    "data" :
                    {
                        "introduction" : "IntroductionLevel2.wav",
                        "menu" : "MenuLevel2Chapters.wav",
                    },
                    //Chapters
                    "children" :
                    [
                        //Chapter 1
                        {
                            "data" :
                            {
                                "introduction" : "IntroductionLevel2Chapter1.wav",
                                "menu" : "MenuLevel2Chapter1Lessons.wav",
                            },
                            //Lessons
                            "children" :
                            [
                                // Lesson 1
                                {
                                    "data" :
                                    {
                                        "lesson": "chapter_1_lesson_1.wav",
                                    },
                                    "children" :
                                    [
                                    ],
                                },
                                // Lesson 2
                                {
                                    "data" :
                                    {
                                        "lesson": "chapter_1_lesson_2.wav",
                                    },
                                    "children" :
                                    [
                                    ],
                                }
                            ]
                        },
                        // Chapter 2
                        {
                            "data" :
                            {
                                "introduction" : "IntroductionLevel2Chapter2.wav",
                                "menu" : "MenuLevel2Chapter2Lessons.wav",
                            },
                            //Lessons
                            "children" :
                            [
                                // Lesson 1
                                {
                                    "data" :
                                    {
                                        "lesson": "chapter_2_lesson_1.wav",
                                    },
                                    "children" :
                                    [
                                    ],
                                },
                                // Lesson 2
                                {
                                    "data" :
                                    {
                                        "lesson": "chapter_2_lesson_2.wav",
                                    },
                                    "children" :
                                    [
                                    ],
                                }
                            ]
                        }
                    ]
                }
            ]
            //Course Ends
    };
}