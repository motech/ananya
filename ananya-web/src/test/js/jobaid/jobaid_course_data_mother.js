function jobAidCourseWithOneLesson(){
    return {
         "name":"JobAidCourse",
         "contents": [
            {
              "name" : "menu",
              "value" :"MenuLevels.wav",
              "language" : "hindi", "metadata": { "duration": "1"}
            },
            {
              "name" : "introduction",
              "value" : "Introduction.wav",
              "language" : "hindi", "metadata": { "duration": "2"}
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
                      "language" : "hindi", "metadata": { "duration": "3"}
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
                              "language" : "hindi", "metadata": { "duration": "4"}
                          },
                          {
                              "name" : "introduction",
                              "value" :"IntroductionLevel1Chapter1.wav",
                              "language" : "hindi", "metadata": { "duration": "5"}
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
                                  "language" : "hindi", "metadata": { "duration": "6"}
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
                   "language": "hindi", "metadata": { "duration": "1"}
               },
               {
                   "name": "introduction",
                   "value": "Introduction.wav",
                   "language": "hindi", "metadata": { "duration": "2"}
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
                           "language": "hindi",
                           "metadata": { "duration": "3"}
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
                                   "language": "hindi", "metadata": { "duration": "4"}
                               },
                               {
                                   "name": "introduction",
                                   "value": "IntroductionLevel1Chapter1.wav",
                                   "language": "hindi", "metadata": { "duration": "5"}
                               }
                           ],
                           "children": [
                               {
                                   "name": "Level 1 Chapter 1 Lesson 1",
                                   "data": {
                                       "number": "1",
                                       "type": "Lesson",
                                       "shortcode" : "3456"
                                   },
                                   "contents": [
                                       {
                                           "name": "lesson",
                                           "value": "chapter_1_lesson_1.wav",
                                           "language": "hindi", "metadata": { "duration": "6"}
                                       }
                                   ],
                                    "children" : [
                                    ]
                               },
                               {
                                   "name": "Level 1 Chapter 1 Lesson 2",
                                   "data": {
                                       "number": "2",
                                       "type": "Lesson",
                                       "shortcode" : "9876"
                                   },
                                   "contents": [
                                       {
                                           "name": "lesson",
                                           "value": "chapter_1_lesson_2.wav",
                                           "language": "hindi", "metadata": { "duration": "7"}
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
                                   "language": "hindi", "metadata": { "duration": "8"}
                               },
                               {
                                   "name": "introduction",
                                   "value": "IntroductionLevel1Chapter2.wav",
                                   "language": "hindi", "metadata": { "duration": "9"}
                               }
                           ],
                           "children": [
                               {
                                   "name": "Level 1 Chapter 2 Lesson 1",
                                   "data": {
                                       "number": "1",
                                       "type": "Lesson",
                                       "shortcode" : "1234"
                                   },
                                   "contents": [
                                       {
                                           "name": "lesson",
                                           "value": "chapter_2_lesson_1.wav",
                                           "language": "hindi", "metadata": { "duration": "10"}
                                       }
                                   ],
                                    "children" : [
                                    ],
                               },
                               {
                                   "name": "Level 1 Chapter 2 Lesson 2",
                                   "data": {
                                       "number": "2",
                                       "type": "Lesson",
                                       "shortcode" : "4567"
                                   },
                                   "contents": [
                                       {
                                           "name": "lesson",
                                           "value": "chapter_2_lesson_2.wav",
                                           "language": "hindi", "metadata": { "duration": "11"}
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
                           "language": "hindi", "metadata": { "duration": "12"}
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
                                   "language": "hindi", "metadata": { "duration": "13"}
                               },
                               {
                                   "name": "introduction",
                                   "value": "IntroductionLevel2Chapter1.wav",
                                   "language": "hindi", "metadata": { "duration": "14"}
                               }
                           ],
                           "children": [
                               {
                                   "name": "Level 2 Chapter 1 Lesson 1",
                                   "data": {
                                       "number": "1",
                                       "type": "Lesson",
                                       "shortcode" : "1111"
                                   },
                                   "contents": [
                                       {
                                           "name": "lesson",
                                           "value": "chapter_1_lesson_1.wav",
                                           "language": "hindi", "metadata": { "duration": "15"}
                                       }
                                   ],
                                    "children" : [
                                    ],
                               },
                               {
                                   "name": "Level 2 Chapter 1 Lesson 2",
                                   "data": {
                                       "number": 2,
                                       "type": "Lesson",
                                       "shortcode" : "2222"
                                   },
                                   "contents": [
                                       {
                                           "name": "lesson",
                                           "value": "chapter_1_lesson_2.wav",
                                           "language": "hindi", "metadata": { "duration": "16"}
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
                                   "language": "hindi", "metadata": { "duration": "17"}
                               },
                               {
                                   "name": "introduction",
                                   "value": "IntroductionLevel2Chapter2.wav",
                                   "language": "hindi", "metadata": { "duration": "18"}
                               }
                           ],
                           "children": [
                               {
                                   "name": "Level 2 Chapter 2 Lesson 1",
                                   "data": {
                                       "number": "1",
                                       "type": "Lesson",
                                       "shortcode" : "3333"
                                   },
                                   "contents": [
                                       {
                                           "name": "lesson",
                                           "value": "chapter_2_lesson_1.wav",
                                           "language": "hindi", "metadata": { "duration": "19"}
                                       }
                                   ],
                                    "children" : [
                                    ],
                               },
                               {
                                   "name": "Level 2 Chapter 2 Lesson 2",
                                   "data": {
                                       "number": "2",
                                       "type": "Lesson",
                                       "shortcode" : "4444"
                                   },
                                   "contents": [
                                       {
                                           "name": "lesson",
                                           "value": "chapter_2_lesson_2.wav",
                                           "language": "hindi", "metadata": { "duration": "20"}
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
