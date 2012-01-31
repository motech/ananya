function jobAidCourseWithOneLesson(){
    return {
         "name":"JobAidCourse",
         "contents": [
            {
              "name" : "menu",
              "value" :"MenuLevels.wav",
              "language" : "hindi"
            },
            {
              "name" : "introduction",
              "value" : "Introduction.wav",
              "language" : "hindi"
             }
         ],
         "data" : {
             "type" :  "Level"
         },
         "children":[
            {
               "name":"Level 1",
               "data":{
                  "number":1,
                  "type": "Level"
               },
               "contents" : [
                  {
                      "name" : "menu",
                      "value" : "MenuLevel1Chapters.wav",
                      "language" : "hindi"
                  }
               ],
               "children":[
                  {
                     "name":"Level 1 Chapter 1",
                     "data":{
                        "number":"1",
                        "type":"Chapter"
                     },
                     "contents": [
                          {
                              "name" : "menu",
                              "value" : "MenuLevel1Chapter1Lessons.wav",
                              "language" : "hindi"
                          },
                          {
                              "name" : "introduction",
                              "value" :"IntroductionLevel1Chapter1.wav",
                              "language" : "hindi"
                          }
                     ],
                     "children":[
                        {
                           "name":"Level 1 Chapter 1 Lesson 1",
                           "data":{
                              "number":"1",
                              "type":"Lesson"
                           },
                           "contents" : [
                              {
                                  "name" : "lesson",
                                  "value" : "chapter_1_lesson_1.wav",
                                  "language" : "hindi"
                              }
                           ],
                           "children" : [
                           ]
                        }
                     ]
                  }
               ]
            }
         ]
    };
}

function jobAidCourseWithTwoLessonsInEveryChapter() {
    return {
        "name": "JobAidCourse",
        "contents": [
            {
                "name": "menu",
                "value": "MenuLevels.wav",
                "language": "hindi"
            },
            {
                "name": "introduction",
                "value": "Introduction.wav",
                "language": "hindi"
            }
        ],
        "data": {
            "type": "Level"
        },
        "children": [
            {
                "name": "Level 1",
                "data": {
                    "number": 1,
                    "type": "Level"
                },
                "contents": [
                    {
                        "name": "menu",
                        "value": "MenuLevel1Chapters.wav",
                        "language": "hindi"
                    }
                ],
                "children": [
                    {
                        "name": "Level 1 Chapter 1",
                        "data": {
                            "number": "1",
                            "type": "Chapter"
                        },
                        "contents": [
                            {
                                "name": "menu",
                                "value": "MenuLevel1Chapter1Lessons.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "introduction",
                                "value": "IntroductionLevel1Chapter1.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": [
                            {
                                "name": "Level 1 Chapter 1 Lesson 1",
                                "data": {
                                    "number": "1",
                                    "type": "Lesson"
                                },
                                "contents": [
                                    {
                                        "name": "lesson",
                                        "value": "chapter_1_lesson_1.wav",
                                        "language": "hindi"
                                    }
                                ],
                                 "children" : [
                                 ]
                            },
                            {
                                "name": "Level 1 Chapter 1 Lesson 2",
                                "data": {
                                    "number": "2",
                                    "type": "Lesson"
                                },
                                "contents": [
                                    {
                                        "name": "lesson",
                                        "value": "chapter_1_lesson_2.wav",
                                        "language": "hindi"
                                    }
                                ],
                                 "children" : [
                                 ]
                            }
                        ]
                    },
                    {
                        "name": "Level 1 Chapter 2",
                        "data": {
                            "number": "1",
                            "type": "Chapter"
                        },
                        "contents": [
                            {
                                "name": "menu",
                                "value": "MenuLevel1Chapter2Lessons.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "introduction",
                                "value": "IntroductionLevel1Chapter2.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": [
                            {
                                "name": "Level 1 Chapter 2 Lesson 1",
                                "data": {
                                    "number": "1",
                                    "type": "Lesson"
                                },
                                "contents": [
                                    {
                                        "name": "lesson",
                                        "value": "chapter_2_lesson_1.wav",
                                        "language": "hindi"
                                    }
                                ],
                                 "children" : [
                                 ],
                            },
                            {
                                "name": "Level 1 Chapter 2 Lesson 2",
                                "data": {
                                    "number": "2",
                                    "type": "Lesson"
                                },
                                "contents": [
                                    {
                                        "name": "lesson",
                                        "value": "chapter_2_lesson_2.wav",
                                        "language": "hindi"
                                    }
                                ],
                                 "children" : [
                                 ]
                            }
                        ]
                    }
                ]
            },
            {
                "name": "Level 2",
                "data": {
                    "number": "2",
                    "type": "Level"
                },
                "contents": [
                    {
                        "name": "menu",
                        "value": "MenuLevel2Chapters.wav",
                        "language": "hindi"
                    }
                ],
                "children": [
                    {
                        "name": "Level 2 Chapter 1",
                        "data": {
                            "number": "1",
                            "type": "Level"
                        },
                        "contents": [
                            {
                                "name": "menu",
                                "value": "MenuLevel2Chapter1Lessons.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "introduction",
                                "value": "IntroductionLevel2Chapter1.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": [
                            {
                                "name": "Level 2 Chapter 1 Lesson 1",
                                "data": {
                                    "number": "1",
                                    "type": "Lesson"
                                },
                                "contents": [
                                    {
                                        "name": "lesson",
                                        "value": "chapter_1_lesson_1.wav",
                                        "language": "hindi"
                                    }
                                ],
                                 "children" : [
                                 ],
                            },
                            {
                                "name": "Level 2 Chapter 1 Lesson 2",
                                "data": {
                                    "number": 2,
                                    "type": "Lesson"
                                },
                                "contents": [
                                    {
                                        "name": "lesson",
                                        "value": "chapter_1_lesson_2.wav",
                                        "language": "hindi"
                                    }
                                ],
                                 "children" : [
                                 ],
                            }
                        ]
                    },
                    {
                        "name": "Level 2 Chapter 2",
                        "data": {
                            "number": "2",
                            "type": "Level"
                        },
                        "contents": [
                            {
                                "name": "menu",
                                "value": "MenuLevel2Chapter2Lessons.wav",
                                "language": "hindi"
                            },
                            {
                                "name": "introduction",
                                "value": "IntroductionLevel2Chapter2.wav",
                                "language": "hindi"
                            }
                        ],
                        "children": [
                            {
                                "name": "Level 2 Chapter 2 Lesson 1",
                                "data": {
                                    "number": "1",
                                    "type": "Lesson"
                                },
                                "contents": [
                                    {
                                        "name": "lesson",
                                        "value": "chapter_2_lesson_1.wav",
                                        "language": "hindi"
                                    }
                                ],
                                 "children" : [
                                 ],
                            },
                            {
                                "name": "Level 2 Chapter 2 Lesson 2",
                                "data": {
                                    "number": "2",
                                    "type": "Lesson"
                                },
                                "contents": [
                                    {
                                        "name": "lesson",
                                        "value": "chapter_2_lesson_2.wav",
                                        "language": "hindi"
                                    }
                                ],
                                "children" : [
                                ],
                            }
                        ]
                    }
                ]
            }
        ]
    };
}
