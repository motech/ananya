/*
 StartCertificationCourse
 */
var StartCertificationCourse = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, StartCertificationCourse.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.processSilentlyAndReturnNextState = function() {
        this.courseState.setChapterIndex(0);
        return CertificateCourse.interactions[StartNextChapter.KEY];
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

StartCertificationCourse.KEY = "startCertificationCourse";
StartCertificationCourse.prototype = new AbstractCourseInteraction();
StartCertificationCourse.prototype.constructor = LessonInteraction;


/*
    StartNextChapter
*/
var StartNextChapter = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, StartNextChapter.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.processSilentlyAndReturnNextState = function() {
        var nextState;

        if(this.courseState.lessonOrQuestionIndex == null) {
            this.courseState.setLessonOrQuestionIndex(0);
            nextState = CertificateCourse.interactions[LessonInteraction.KEY];
        }
        else {
            var currentChapterIndex = this.courseState.chapterIndex;
            var currentLessonOrQuestionIndex = this.courseState.lessonOrQuestionIndex;
            var maxChapterIndex = course.children.length-1;
            if(currentChapterIndex >= maxChapterIndex) {
               nextState = CertificateCourse.interactions[PlayThanksInteraction.KEY];
            }
            else {
                this.courseState.chapterIndex++;
                this.courseState.setLessonOrQuestionIndex(0);
                nextState = CertificateCourse.interactions[LessonInteraction.KEY];
            }
        }
        return nextState;
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

StartNextChapter.KEY = "startNextChapter";
StartNextChapter.prototype = new AbstractCourseInteraction();
StartNextChapter.prototype.constructor = LessonInteraction;


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
};

LessonInteraction.KEY = "lesson";

LessonInteraction.prototype = new AbstractCourseInteraction();
LessonInteraction.prototype.constructor = LessonInteraction;

LessonInteraction.prototype.playAudio = function() {
    var currentChapterIndex = this.courseState.chapterIndex;
    var currentLessonIndex = this.courseState.lessonOrQuestionIndex;

    var currentLesson = this.course.children[currentChapterIndex].children[currentLessonIndex];

    return this.findAudio(currentLesson, "lesson");
};

LessonInteraction.prototype.doesTakeInput = function() {
    return false;
}

LessonInteraction.prototype.nextInteraction = function() {
    return CertificateCourse.interactions[LessonEndMenuInteraction.KEY];
}

LessonInteraction.prototype.getCourseType = function() {
    return CourseType.LESSON;
}

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

    this.init(metadata, course, courseState);
};

LessonEndMenuInteraction.KEY = "lessonEndMenu";

LessonEndMenuInteraction.prototype = new AbstractCourseInteraction();
LessonEndMenuInteraction.prototype.constructor = LessonEndMenuInteraction;

LessonEndMenuInteraction.prototype.doesTakeInput = function() {
    return true;
}

LessonEndMenuInteraction.prototype.playAudio = function() {
    var chapterIndex = this.courseState.chapterIndex;
    var lessonIndex = this.courseState.lessonOrQuestionIndex;
    var currentLesson = this.course.children[chapterIndex].children[lessonIndex];
    return this.findAudio(currentLesson, "menu");
};


LessonEndMenuInteraction.prototype.validateInput = function(input) {
    return input == '1' || input == '2';
};

LessonEndMenuInteraction.prototype.continueWithoutInput = function(){
    var chapterIndex = this.courseState.chapterIndex;
    var lessonIndex = this.courseState.lessonOrQuestionIndex;
    var currentChapter = this.course.children[chapterIndex];
    var nextInLine = currentChapter.children[lessonIndex+1];
    if(nextInLine.data.type=="quiz"){
        return CertificateCourse.interactions[StartNextChapter.KEY];
    }
    this.courseState.setLessonOrQuestionIndex(lessonIndex+1);
    return CertificateCourse.interactions[LessonInteraction.KEY];
};

LessonEndMenuInteraction.prototype.processInputAndReturnNextInteraction = function(input){
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

LessonEndMenuInteraction.prototype.getCourseType = function() {
    return CourseType.LESSON;
}

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

    this.init(metadata, course, courseState);
};

StartQuizInteraction.KEY = "startQuiz";

StartQuizInteraction.prototype = new AbstractCourseInteraction();
StartQuizInteraction.prototype.constructor = StartQuizInteraction;

StartQuizInteraction.prototype.playAudio = function() {
    var currentChapter = this.course.children[this.courseState.chapterIndex];
    return this.findAudio(currentChapter, "quizHeader");
};

StartQuizInteraction.prototype.doesTakeInput = function() {
    return false;
}

StartQuizInteraction.prototype.nextInteraction = function() {
    var currentChapter = this.courseState.chapterIndex;
    this.courseState.scoresByChapter[currentChapter] = 0;
    this.courseState.setLessonOrQuestionIndex(this.courseState.lessonOrQuestionIndex + 1);
    return CertificateCourse.interactions[PoseQuestionInteraction.KEY];
}

StartQuizInteraction.prototype.bookMark = function() {

}

StartQuizInteraction.prototype.getCourseType = function() {
    return CourseType.QUIZ;
}

/*
    PoseQuestionInteraction
*/
var PoseQuestionInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, PoseQuestionInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.init(metadata, course, courseState);
};

PoseQuestionInteraction.KEY = "poseQuestion";
PoseQuestionInteraction.prototype = new AbstractCourseInteraction();
PoseQuestionInteraction.prototype.constructor = PoseQuestionInteraction;

PoseQuestionInteraction.prototype.doesTakeInput = function() {
    return true;
}

PoseQuestionInteraction.prototype.playAudio = function() {
    var chapterIndex = this.courseState.chapterIndex;
    var questionIndex = this.courseState.lessonOrQuestionIndex;
    var currentQuestion = this.course.children[chapterIndex].children[questionIndex];
    return this.findAudio(currentQuestion, "question");
};


PoseQuestionInteraction.prototype.validateInput = function(input) {
    return input == '1' || input == '2';
};

PoseQuestionInteraction.prototype.continueWithoutInput = function(){
    return CertificateCourse.interactions[StartNextChapter.KEY];
};

PoseQuestionInteraction.prototype.processInputAndReturnNextInteraction = function(userResponse){
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

PoseQuestionInteraction.prototype.shouldLog = function() {
    return false;
}

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

    this.init(metadata, course, courseState);
};

PlayAnswerExplanationInteraction.KEY = "playAnswerExplanation";
PlayAnswerExplanationInteraction.prototype = new AbstractCourseInteraction();
PlayAnswerExplanationInteraction.prototype.constructor = PlayAnswerExplanationInteraction;

PlayAnswerExplanationInteraction.prototype.playAudio = function() {
    var currentQuestion = this.course.children[this.courseState.chapterIndex].children[this.courseState.lessonOrQuestionIndex];
    if(this.courseState.isAnswerCorrect) {
        contentType = "correct";
    } else {
        contentType = "incorrect";
    }
    return this.findAudio(currentQuestion, contentType);
};

PlayAnswerExplanationInteraction.prototype.doesTakeInput = function() {
    return false;
}

PlayAnswerExplanationInteraction.prototype.nextInteraction = function() {
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

PlayAnswerExplanationInteraction.prototype.resumeCall = function() {
    return this.nextInteraction();
}

PlayAnswerExplanationInteraction.prototype.shouldLog = function() {
    return false;
}

/*
    InvalidInputInteraction
*/
var InvalidInputInteraction = function(interactionToReturnTo, metadata) {
    this.init = function(interactionToReturnTo, metadata) {
        AbstractCourseInteraction.call(this, metadata, interactionToReturnTo.getInteractionKey());
        this.metadata = metadata;
        this.interactionToReturnTo = interactionToReturnTo;
    }

    this.init(interactionToReturnTo, metadata);
};

InvalidInputInteraction.prototype = new AbstractCourseInteraction();
InvalidInputInteraction.prototype.constructor = InvalidInputInteraction;

InvalidInputInteraction.prototype.playAudio = function() {
    return this.metadata['audio.url'] + this.metadata['invalid.input.retry.audio'];
};

InvalidInputInteraction.prototype.doesTakeInput = function() {
    return false;
};

InvalidInputInteraction.prototype.nextInteraction = function() {
    return this.interactionToReturnTo;
};

InvalidInputInteraction.prototype.shouldLog = function() {
    return false;
}

/*
    ReportChapterScore
*/
var ReportChapterScoreInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, ReportChapterScoreInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.getCourseItemState = function() {
        return CourseState.END;
    }

    this.init(metadata, course, courseState);
};
ReportChapterScoreInteraction.KEY = "reportChapterScore";

ReportChapterScoreInteraction.prototype = new AbstractCourseInteraction();
ReportChapterScoreInteraction.prototype.constructor = ReportChapterScoreInteraction;

ReportChapterScoreInteraction.prototype.playAudio = function() {
    var currentChapterIndex = this.courseState.chapterIndex;
    var currentChapter = this.course.children[currentChapterIndex];
    var chapterScore = this.courseState.scoresByChapter[currentChapterIndex];

    return this.findAudio(currentChapter, "score " + chapterScore);
};

ReportChapterScoreInteraction.prototype.doesTakeInput = function() {
    return false;
}

ReportChapterScoreInteraction.prototype.nextInteraction = function() {
    return CertificateCourse.interactions[EndOfChapterMenuInteraction.KEY];
}

ReportChapterScoreInteraction.prototype.bookMark = function() {

}

ReportChapterScoreInteraction.prototype.getCourseType = function() {
    return CourseType.QUIZ;
}

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

    this.init(metadata, course, courseState);
};

EndOfChapterMenuInteraction.KEY = "endOfChapterMenu";
EndOfChapterMenuInteraction.prototype = new AbstractCourseInteraction();
EndOfChapterMenuInteraction.prototype.constructor = EndOfChapterMenuInteraction;

EndOfChapterMenuInteraction.prototype.doesTakeInput = function() {
    return true;
}

EndOfChapterMenuInteraction.prototype.playAudio = function() {
    var currentChapter = this.course.children[this.courseState.chapterIndex];
    return this.findAudio(currentChapter, "menu");
};

EndOfChapterMenuInteraction.prototype.validateInput = function(input) {
    return input == '1' || input == '2';
};

EndOfChapterMenuInteraction.prototype.continueWithoutInput = function(){
    return CertificateCourse.interactions[StartNextChapter.KEY];
}

EndOfChapterMenuInteraction.prototype.processInputAndReturnNextInteraction = function(input){
    if(input == 1) {
        this.courseState.lessonOrQuestionIndex = 0;
        return CertificateCourse.interactions[LessonInteraction.KEY];
    }
    return CertificateCourse.interactions[StartNextChapter.KEY];
}

EndOfChapterMenuInteraction.prototype.getCourseType = function() {
    return CourseType.CHAPTER;
}

/*
    PlayThanks
*/
var PlayThanksInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, PlayThanksInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.init(metadata, course, courseState);
};
PlayThanksInteraction.KEY = "playThanks";

PlayThanksInteraction.prototype = new AbstractCourseInteraction();
PlayThanksInteraction.prototype.constructor = PlayThanksInteraction;

PlayThanksInteraction.prototype.playAudio = function() {
    var audioFileName = this.audioFileBase() + this.metadata['certificate.end.thanks'];

    return audioFileName;
};

PlayThanksInteraction.prototype.doesTakeInput = function() {
    return false;
}

PlayThanksInteraction.prototype.nextInteraction = function() {
    return CertificateCourse.interactions[PlayFinalScoreInteraction.KEY];
}

PlayThanksInteraction.prototype.shouldLog = function() {
    return false;
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

    this.init(metadata, course, courseState);
};
PlayFinalScoreInteraction.KEY = "playFinalScore";

PlayFinalScoreInteraction.prototype = new AbstractCourseInteraction();
PlayFinalScoreInteraction.prototype.constructor = PlayFinalScoreInteraction;

PlayFinalScoreInteraction.prototype.playAudio = function() {
    var scoresByChapter = this.courseState.scoresByChapter;
    var finalScore = calculateFinalScore(scoresByChapter);

    var finalScoreFileRaw = this.metadata['certificate.end.final.score'];
    var finalScoreFile = Utility.format(finalScoreFileRaw, parseInt(this.metadata['certificate.end.final.score.prefix.start']) + finalScore, finalScore);


    var audioFileName = this.audioFileBase() + finalScoreFile;

    return audioFileName;
};

calculateFinalScore = function (scoresByChapter) {
    var finalScore = 0;
    for (var key in scoresByChapter) {
        if (scoresByChapter.hasOwnProperty(key)) {
            finalScore += scoresByChapter[key];
        }
    }
    return finalScore;
}

PlayFinalScoreInteraction.prototype.doesTakeInput = function() {
    return false;
}

PlayFinalScoreInteraction.prototype.nextInteraction = function() {
    return CertificateCourse.interactions[PlayCourseResultInteraction.KEY];
}

PlayFinalScoreInteraction.prototype.shouldLog = function() {
    return false;
}


/*
    PlayCourseResult
*/
var PlayCourseResultInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, PlayCourseResultInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.init(metadata, course, courseState);
};
PlayCourseResultInteraction.KEY = "playCourseResult";

PlayCourseResultInteraction.prototype = new AbstractCourseInteraction();
PlayCourseResultInteraction.prototype.constructor = PlayCourseResultInteraction;

PlayCourseResultInteraction.prototype.playAudio = function() {
    var scoresByChapter = this.courseState.scoresByChapter;
    var finalScore = calculateFinalScore(scoresByChapter);
    var audioFileName  = this.audioFileBase();
    if(finalScore < 18){
        audioFileName += this.metadata["certificate.end.result.fail"];
    }
    else{
        audioFileName += this.metadata["certificate.end.result.pass"];
    }

    return audioFileName;
};


PlayCourseResultInteraction.prototype.doesTakeInput = function() {
    return false;
}

PlayCourseResultInteraction.prototype.nextInteraction = function() {
    return CertificateCourse.interactions[CourseEndMarkerInteraction.KEY];
}

PlayCourseResultInteraction.prototype.shouldLog = function() {
    return false;
}

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

    this.init(metadata, course, courseState);
};

CourseEndMarkerInteraction.prototype.shouldLog = function() {
    return false;
}

/*
    EndOfCourse
*/
var EndOfCourseInteraction = function() {
    this.disconnect = function() {
        return true;
    }
};

EndOfCourseInteraction.prototype.getInteractionKey = function() {
    return null;
};

EndOfCourseInteraction.prototype.getCourseType = function() {
    return CourseType.COURSE;
}

EndOfCourseInteraction.prototype.getCourseItemState = function() {
    return CourseState.END;
}


EndOfCourseInteraction.KEY = "endOfCourse";
