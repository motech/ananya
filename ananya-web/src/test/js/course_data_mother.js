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