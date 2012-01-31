function certificationCourseWithTwoLessonsInEveryChapter() {
    return {
        "name": "CertificationCourse",
        "contents": [
            {
                "name": "menu",
                "value": "MenuCourse.wav",
                "language": "hindi"
            },
            {
                "name": "introduction",
                "value": "Introduction.wav",
                "language": "hindi"
            },
            {
                "name": "help",
                "value": "Help.wav",
                "language": "hindi"
            }
        ],
        "data": {
            "type": "course"
        },
        "children": [
            {
                "name": "Chapter 1",
                "data": {
                    "type": "chapter"
                },
                "contents": [
                    {
                        "name": "menu",
                        "value": "chapter_1_menu.wav",
                        "language": "hindi"
                    },
                    {
                        "name": "quizHeader",
                        "value": "chapter_1_quizHeader.wav",
                        "language": "hindi"
                    }
                ],
                "children": [
                    {
                        "name": "Chapter 1 Lesson 1",
                        "data": {
                            "type": "lesson"
                        },
                        "contents": [
                            {
                                "name": "lesson",
                                "value": "chapter_1_lesson_1.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "menu",
                                "value": "chapter_1_lesson_1_menu.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": []
                    },
                    {
                        "name": "Chapter 1 Lesson 2",
                        "data": {
                            "type": "lesson"
                        },
                        "contents": [
                            {
                                "name": "lesson",
                                "value": "chapter_1_lesson_2.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "menu",
                                "value": "chapter_1_lesson_2_menu.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": []
                    },
                    {
                        "name": "Chapter 1 Quiz 1",
                        "data": {
                            "type": "quiz",
                            "correctAnswer": "1"
                        },
                        "contents": [
                            {
                                "name": "question",
                                "value": "chapter_1_quiz_1.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "correct",
                                "value": "chapter_1_quiz_1_correct.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "incorrect",
                                "value": "chapter_1_quiz_1_wrong.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": []
                    },
                    {
                        "name": "Chapter 1 Quiz 2",
                        "data": {
                            "type": "quiz",
                            "correctAnswer": "2"
                        },
                        "contents": [
                            {
                                "name": "question",
                                "value": "chapter_1_quiz_2.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "correct",
                                "value": "chapter_1_quiz_2_correct.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "incorrect",
                                "value": "chapter_1_quiz_2_wrong.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": []
                    }
                ]
            },
            {
                "name": "Chapter 2",
                "data": {
                    "type": "chapter"
                },
                "contents": [
                    {
                        "name": "menu",
                        "value": "chapter_2_menu.wav",
                        "language": "hindi"
                    },
                    {
                        "name": "quizHeader",
                        "value": "chapter_2_quizHeader.wav",
                        "language": "hindi"
                    }
                ],
                "children": [
                    {
                        "name": "Chapter 2 Lesson 1",
                        "data": {
                            "type": "lesson"
                        },
                        "contents": [
                            {
                                "name": "lesson",
                                "value": "chapter_2_lesson_1.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "menu",
                                "value": "chapter_2_lesson_1_menu.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": []
                    },
                    {
                        "name": "Chapter 2 Lesson 2",
                        "data": {
                            "type": "lesson"
                        },
                        "contents": [
                            {
                                "name": "lesson",
                                "value": "chapter_2_lesson_2.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "menu",
                                "value": "chapter_2_lesson_2_menu.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": []
                    },
                    {
                        "name": "Chapter 2 Quiz 1",
                        "data": {
                            "type": "quiz",
                            "correctAnswer": "2"
                        },
                        "contents": [
                            {
                                "name": "question",
                                "value": "chapter_2_quiz_1.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "correct",
                                "value": "chapter_2_quiz_1_correct.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "incorrect",
                                "value": "chapter_2_quiz_1_wrong.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": []
                    },
                    {
                        "name": "Chapter 2 Quiz 2",
                        "data": {
                            "type": "quiz",
                            "correctAnswer": "1"
                        },
                        "contents": [
                            {
                                "name": "question",
                                "value": "chapter_2_quiz_2.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "correct",
                                "value": "chapter_2_quiz_2_correct.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "incorrect",
                                "value": "chapter_2_quiz_2_wrong.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": []
                    }
                ]
            }
        ]
    };
}
