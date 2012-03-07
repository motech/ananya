function extend(subClass, superClass)
{
    var lightWeightSuperClass = function(){};
    lightWeightSuperClass.prototype = superClass.prototype;
    subClass.prototype = new lightWeightSuperClass();
    subClass.prototype.constructor = subClass;
}

/*
 StartCertificationCourse
 */
var StartCertificationCourseInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, StartCertificationCourseInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.processSilentlyAndReturnNextState = function() {
        this.courseState.setChapterIndex(0);
        return CertificateCourse.interactions[StartNextChapterInteraction.KEY];
    }

    this.resumeCall = function() {
        return this;
    }

    this.getCourseType = function() {
        return CourseType.COURSE;
    }

    this.getCourseItemState = function() {
        return CourseState.START;
    }

    this.init(metadata, course, courseState);
};

extend(StartCertificationCourseInteraction,AbstractCourseInteraction);
StartCertificationCourseInteraction.KEY = "startCertificationCourse";



/*
    StartNextChapter
*/
var StartNextChapterInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, StartNextChapterInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.processSilentlyAndReturnNextState = function() {
        if(this.courseState.lessonOrQuestionIndex == null) {
            this.courseState.setLessonOrQuestionIndex(0);
        }
        return CertificateCourse.interactions[LessonInteraction.KEY];
    }

    this.resumeCall = function() {
        return this;
    }

    this.getCourseType = function() {
        return CourseType.CHAPTER;
    }

    this.getCourseItemState = function() {
        return CourseState.START;
    }

    this.init(metadata, course, courseState);
};

StartNextChapterInteraction.KEY = "startNextChapter";
extend(StartNextChapterInteraction,AbstractCourseInteraction);

/*
    LessonInteraction
*/
var LessonInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, LessonInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.getCourseItemState = function() {
        return CourseState.START;
    }

    this.init(metadata, course, courseState);

    this.playAudio = function() {
        var currentChapterIndex = this.courseState.chapterIndex;
        var currentLessonIndex = this.courseState.lessonOrQuestionIndex;

        var currentLesson = this.course.children[currentChapterIndex].children[currentLessonIndex];

        return this.findAudio(currentLesson, "lesson");
    };

    this.doesTakeInput = function() {
        return false;
    }

    this.nextInteraction = function() {
        return CertificateCourse.interactions[LessonEndMenuInteraction.KEY];
    }

    this.getCourseType = function() {
        return CourseType.LESSON;
    }
};

LessonInteraction.KEY = "lesson";
extend(LessonInteraction,AbstractCourseInteraction);

/*
    LessonEndMenuInteraction
*/

var LessonEndMenuInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, LessonEndMenuInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.getCourseItemState = function() {
        return CourseState.END;
    }

    this.doesTakeInput = function() {
        return true;
    }

    this.playAudio = function() {
        var chapterIndex = this.courseState.chapterIndex;
        var lessonIndex = this.courseState.lessonOrQuestionIndex;
        var currentLesson = this.course.children[chapterIndex].children[lessonIndex];
        return this.findAudio(currentLesson, "menu");
    }

    this.validateInput = function(input) {
        return input == '1' || input == '2';
    }

    this.continueWithoutInput = function(){
        var chapterIndex = this.courseState.chapterIndex;
        var lessonIndex = this.courseState.lessonOrQuestionIndex;
        var currentChapter = this.course.children[chapterIndex];
        var nextInLine = currentChapter.children[lessonIndex+1];
        if(nextInLine.data.type=="quiz"){
            return CertificateCourse.interactions[StartNextChapterInteraction.KEY];
        }
        this.courseState.setLessonOrQuestionIndex(lessonIndex+1);
        return CertificateCourse.interactions[LessonInteraction.KEY];
    }

    this.processInputAndReturnNextInteraction = function(input){
        if(input == 2) {
            var chapterIndex = this.courseState.chapterIndex;
            var lessonIndex = this.courseState.lessonOrQuestionIndex;
            var currentChapter = this.course.children[chapterIndex];
            var nextInLine = currentChapter.children[lessonIndex+1];
            if(nextInLine.data.type=="quiz"){
                return CertificateCourse.interactions[StartQuizInteraction.KEY];
            }
            this.courseState.setLessonOrQuestionIndex(lessonIndex+1);
        }
        return CertificateCourse.interactions[LessonInteraction.KEY];
    }

    this.getCourseType = function() {
        return CourseType.LESSON;
    }

    this.init(metadata, course, courseState);
};

LessonEndMenuInteraction.KEY = "lessonEndMenu";
extend(LessonEndMenuInteraction,AbstractCourseInteraction);

/*
    StartQuizInteraction
*/
var StartQuizInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, StartQuizInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.getCourseItemState = function() {
        return CourseState.START;
    }

    this.playAudio = function() {
        var currentChapter = this.course.children[this.courseState.chapterIndex];
        return this.findAudio(currentChapter, "quizHeader");
    };

    this.doesTakeInput = function() {
        return false;
    }

    this.nextInteraction = function() {
        var currentChapter = this.courseState.chapterIndex;
        this.courseState.scoresByChapter[currentChapter] = 0;
        this.courseState.setLessonOrQuestionIndex(this.courseState.lessonOrQuestionIndex + 1);
        return CertificateCourse.interactions[PoseQuestionInteraction.KEY];
    }

    this.bookMark = function() {

    }

    this.getCourseType = function() {
        return CourseType.QUIZ;
    }

    this.init(metadata, course, courseState);
};

StartQuizInteraction.KEY = "startQuiz";
extend(StartQuizInteraction,AbstractCourseInteraction);


/*
    PoseQuestionInteraction
*/
var PoseQuestionInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, PoseQuestionInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.doesTakeInput = function() {
        return true;
    }

    this.playAudio = function() {
        var chapterIndex = this.courseState.chapterIndex;
        var questionIndex = this.courseState.lessonOrQuestionIndex;
        var currentQuestion = this.course.children[chapterIndex].children[questionIndex];
        return this.findAudio(currentQuestion, "question");
    }

    this.validateInput = function(input) {
        return input == '1' || input == '2';
    }

    this.continueWithoutInput = function(){
        return CertificateCourse.interactions[StartNextChapterInteraction.KEY];
    }

    this.processInputAndReturnNextInteraction = function(userResponse){
        var currentChapterIndex = this.courseState.chapterIndex;
        var currentQuestion = this.course.children[currentChapterIndex].children[this.courseState.lessonOrQuestionIndex];
        var isAnswerCorrect = (userResponse == currentQuestion.data.correctAnswer);

        this.courseState.setCurrentQuestionResponse(userResponse);
        this.courseState.setAnswerCorrect(isAnswerCorrect);
        if(isAnswerCorrect) {
            this.courseState.scoresByChapter[currentChapterIndex]++;
        }

        return CertificateCourse.interactions[PlayAnswerExplanationInteraction.KEY];
    }

    this.shouldLog = function() {
        return false;
    }

    this.init(metadata, course, courseState);
}

PoseQuestionInteraction.KEY = "poseQuestion";
extend(PoseQuestionInteraction,AbstractCourseInteraction);


/*
    PlayAnswerExplanation
*/
var PlayAnswerExplanationInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, PlayAnswerExplanationInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.getCourseItemState = function() {
        return CourseState.START;
    }

    this.playAudio = function() {
        var currentQuestion = this.course.children[this.courseState.chapterIndex].children[this.courseState.lessonOrQuestionIndex];
        if(this.courseState.isAnswerCorrect) {
            contentType = "correct";
        } else {
            contentType = "incorrect";
        }
        return this.findAudio(currentQuestion, contentType);
    };

    this.doesTakeInput = function() {
        return false;
    }

    this.nextInteraction = function() {
        var nextQuestionIndex = this.courseState.lessonOrQuestionIndex + 1;
        var nextQuestion = this.course.children[this.courseState.chapterIndex].children[nextQuestionIndex];

        if(nextQuestion) {
            this.courseState.setLessonOrQuestionIndex(nextQuestionIndex);
            return CertificateCourse.interactions[PoseQuestionInteraction.KEY];
        }

        this.courseState.setAnswerCorrect(null);
        this.courseState.setCurrentQuestionResponse(null);
        return CertificateCourse.interactions[ReportChapterScoreInteraction.KEY];
    }

    this.resumeCall = function() {
        return this.nextInteraction();
    }

    this.shouldLog = function() {
        return false;
    }


    this.init(metadata, course, courseState);
};

PlayAnswerExplanationInteraction.KEY = "playAnswerExplanation";
extend(PlayAnswerExplanationInteraction,AbstractCourseInteraction);


/*
    InvalidInputInteraction
*/
var InvalidInputInteraction = function(interactionToReturnTo, metadata) {
    this.init = function(interactionToReturnTo, metadata) {
        AbstractCourseInteraction.call(this, metadata, interactionToReturnTo.getInteractionKey());
        this.metadata = metadata;
        this.interactionToReturnTo = interactionToReturnTo;
    }

    this.playAudio = function() {
        return this.metadata['audio.url'] + this.metadata['invalid.input.retry.audio'];
    };

    this.doesTakeInput = function() {
        return false;
    };

    this.nextInteraction = function() {
        return this.interactionToReturnTo;
    };

    this.shouldLog = function() {
        return false;
    }

    this.init(interactionToReturnTo, metadata);
};

extend(InvalidInputInteraction,AbstractCourseInteraction);

/*
    ReportChapterScore
*/
var ReportChapterScoreInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, ReportChapterScoreInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.playAudio = function() {
        var currentChapterIndex = this.courseState.chapterIndex;
        var currentChapter = this.course.children[currentChapterIndex];
        var chapterScore = this.courseState.scoresByChapter[currentChapterIndex];

        return this.findAudio(currentChapter, "score " + chapterScore);
    };

    this.doesTakeInput = function() {
        return false;
    }

    this.nextInteraction = function() {
        return CertificateCourse.interactions[EndOfChapterMenuInteraction.KEY];
    }

    this.bookMark = function() {

    }

    this.getCourseType = function() {
        return CourseType.QUIZ;
    }


    this.getCourseItemState = function() {
        return CourseState.END;
    }

    this.init(metadata, course, courseState);
};
ReportChapterScoreInteraction.KEY = "reportChapterScore";
extend(ReportChapterScoreInteraction,AbstractCourseInteraction);


/*
    EndOfChapterMenu
*/
var EndOfChapterMenuInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, EndOfChapterMenuInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.getCourseItemState = function() {
        return CourseState.END;
    }

    this.doesTakeInput = function() {
        return true;
    }

    this.playAudio = function() {
        var currentChapter = this.course.children[this.courseState.chapterIndex];
        return this.findAudio(currentChapter, "menu");
    };

    this.validateInput = function(input) {
        return input == '1' || input == '2';
    };

    this.continueWithoutInput = function(){
        return CertificateCourse.interactions[StartNextChapterInteraction.KEY];
    }

    this.processInputAndReturnNextInteraction = function(input){
        this.courseState.lessonOrQuestionIndex = 0;
        if(input == 1) {
            return CertificateCourse.interactions[LessonInteraction.KEY];
        }

        this.courseState.chapterIndex++;

         var maxChapterIndex = course.children.length - 1;
         if(this.courseState.chapterIndex  > maxChapterIndex) {
           return CertificateCourse.interactions[PlayThanksInteraction.KEY];
         }

         return CertificateCourse.interactions[StartNextChapterInteraction.KEY];
    }

    this.getCourseType = function() {
        return CourseType.CHAPTER;
    }

    this.init(metadata, course, courseState);
};

EndOfChapterMenuInteraction.KEY = "endOfChapterMenu";
extend(EndOfChapterMenuInteraction,AbstractCourseInteraction);


/*
    PlayThanks
*/
var PlayThanksInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, PlayThanksInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.playAudio = function() {
        var audioFileName = this.audioFileBase() + this.metadata['certificate.end.thanks'];

        return audioFileName;
    };

    this.doesTakeInput = function() {
        return false;
    }

    this.nextInteraction = function() {
        return CertificateCourse.interactions[PlayFinalScoreInteraction.KEY];
    }

    this.shouldLog = function() {
        return false;
    }

    this.init(metadata, course, courseState);
};

PlayThanksInteraction.KEY = "playThanks";
extend(PlayThanksInteraction,AbstractCourseInteraction);


_calculateFinalScore = function (scoresByChapter) {
    var finalScore = 0;
    for (var key in scoresByChapter) {
        if (scoresByChapter.hasOwnProperty(key)) {
            finalScore += scoresByChapter[key];
        }
    }
    return finalScore;
}

/*
    PlayFinalScore
*/
var PlayFinalScoreInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, PlayFinalScoreInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.playAudio = function() {
        var scoresByChapter = this.courseState.scoresByChapter;
        var finalScore = _calculateFinalScore(scoresByChapter);

        var finalScoreFileRaw = this.metadata['certificate.end.final.score'];
        var finalScoreFile = Utility.format(finalScoreFileRaw, parseInt(this.metadata['certificate.end.final.score.prefix.start']) + finalScore, finalScore);


        var audioFileName = this.audioFileBase() + finalScoreFile;

        return audioFileName;
    }

    this.doesTakeInput = function() {
        return false;
    }

    this.nextInteraction = function() {
        return CertificateCourse.interactions[PlayCourseResultInteraction.KEY];
    }

    this.shouldLog = function() {
        return false;
    }

    this.init(metadata, course, courseState);
};
PlayFinalScoreInteraction.KEY = "playFinalScore";
extend(PlayFinalScoreInteraction,AbstractCourseInteraction);

/*
    PlayCourseResult
*/
var PlayCourseResultInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, PlayCourseResultInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.playAudio = function() {
        var scoresByChapter = this.courseState.scoresByChapter;
        var finalScore = _calculateFinalScore(scoresByChapter);
        var audioFileName  = this.audioFileBase();
        if(finalScore < 18){
            audioFileName += this.metadata["certificate.end.result.fail"];
        }
        else{
            audioFileName += this.metadata["certificate.end.result.pass"];
        }

        return audioFileName;
    };


    this.doesTakeInput = function() {
        return false;
    }

    this.nextInteraction = function() {
        return CertificateCourse.interactions[CourseEndMarkerInteraction.KEY];
    }

    this.shouldLog = function() {
        return false;
    }

    this.init(metadata, course, courseState);
};
PlayCourseResultInteraction.KEY = "playCourseResult";
extend(PlayCourseResultInteraction,AbstractCourseInteraction);

/*
    CourseEndMarker
*/
var CourseEndMarkerInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        this.course = course;
        this.courseState = courseState;
    }

    // TODO: Will all the scores be reset to 0 if users starts the course again?
    this.processSilentlyAndReturnNextState = function() {
        this.courseState.setChapterIndex(null);
        this.courseState.setLessonOrQuestionIndex(null);

        return CertificateCourse.interactions[EndOfCourseInteraction.KEY];
    }

    this.resumeCall = function() {
        return this;
    }

    this.getCourseItemState = function() {
        return null;
    }

    this.shouldLog = function() {
        return false;
    }

    this.init(metadata, course, courseState);
};
CourseEndMarkerInteraction.KEY="courseEndMarker"

/*
    EndOfCourse
*/
var EndOfCourseInteraction = function() {
    this.disconnect = function() {
        return true;
    }

    EndOfCourseInteraction.prototype.getInteractionKey = function() {
        return null;
    };

    EndOfCourseInteraction.prototype.getCourseType = function() {
        return CourseType.COURSE;
    }

    EndOfCourseInteraction.prototype.getCourseItemState = function() {
        return CourseState.END;
    }

    this.shouldLog = function() {
            return false;
    }
};
EndOfCourseInteraction.KEY = "endOfCourse";
