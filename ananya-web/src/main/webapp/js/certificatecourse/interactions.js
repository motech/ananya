/*
    WelcomeInteraction
*/

var WelcomeInteraction = function(metadata, course) {
    this.init = function(metadata, course) {
        AbstractCourseInteraction.call(this, metadata, WelcomeInteraction.KEY);
        this.course = course;
    }

    this.init(metadata, course);
};

WelcomeInteraction.KEY = "welcome";

WelcomeInteraction.prototype = new AbstractCourseInteraction();
WelcomeInteraction.prototype.constructor = WelcomeInteraction;

WelcomeInteraction.prototype.playAudio = function() {
    return this.findAudio(this.course, "introduction");
};

WelcomeInteraction.prototype.doesTakeInput = function() {
    return false;
}

WelcomeInteraction.prototype.nextInteraction = function() {
    return CertificateCourse.interactions[StartCourseOption.KEY];
}

WelcomeInteraction.prototype.bookMark = function() {

}

/*
    StartCourseOption
*/
var StartCourseOption = function(metadata, course) {
    this.init = function(metadata, course) {
        AbstractCourseInteraction.call(this, metadata, StartCourseOption.KEY);
        this.course = course;
    }

    this.init(metadata, course);
};

StartCourseOption.KEY = "startCourseOption";

StartCourseOption.prototype = new AbstractCourseInteraction();
StartCourseOption.prototype.constructor = StartCourseOption;

StartCourseOption.prototype.doesTakeInput = function() {
    return true;
}

StartCourseOption.prototype.playAudio = function() {
    return this.findAudio(this.course, "menu");
};

StartCourseOption.prototype.validateInput = function(input) {
    return input == '1' || input == '2';
};

StartCourseOption.prototype.continueWithoutInput = function(){
    return CertificateCourse.interactions[StartNextChapter.KEY];
}

StartCourseOption.prototype.processInputAndReturnNextInteraction = function(input){
    if(input == 1) {
        return CertificateCourse.interactions[WelcomeInteraction.KEY];
    }
    return CertificateCourse.interactions[StartNextChapter.KEY];
}

/*
    StartNextChapter
*/
//TODO: This should extend from something called silent interaction.
var StartNextChapter = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        this.course = course;
        this.courseState = courseState;
    }

    this.processSilentlyAndReturnNextState = function() {
        var nextState;
        if(this.courseState.chapterIndex == null) {
            this.courseState.setChapterIndex(0);
            this.courseState.setLessonOrQuestionIndex(0);
            nextState = CertificateCourse.interactions[LessonInteraction.KEY];
        }
        else {
            var currentChapterIndex = this.courseState.chapterIndex;
            var currentLessonOrQuestionIndex = this.courseState.lessonOrQuestionIndex;
            var maxChapterIndex = course.children.length-1;
            if(currentChapterIndex >= maxChapterIndex) {
               nextState = CertificateCourse.interactions["endOfCourse"];
            }
            else {
                this.courseState.chapterIndex++;
                this.courseState.setLessonOrQuestionIndex(0);
                nextState = CertificateCourse.interactions[LessonInteraction.KEY];
            }
        }
        return nextState;
    }

    this.init(metadata, course, courseState);
};

StartNextChapter.KEY = "startNextChapter";

/*
    LessonInteraction
*/
var LessonInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, LessonInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
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

/*
    LessonEndMenuInteraction
*/

var LessonEndMenuInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, LessonEndMenuInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.init(metadata, course, courseState);
};

LessonEndMenuInteraction.KEY = "lessonEndMenu";

LessonEndMenuInteraction.prototype = new AbstractCourseInteraction();
LessonEndMenuInteraction.prototype.constructor = StartCourseOption;

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


/*
    StartQuizInteraction
*/
var StartQuizInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, StartQuizInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
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

//TODO: Here we will need to populate some shared DS with response, so that it can be sent to server.
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

/*
    PlayAnswerExplanation
*/
var PlayAnswerExplanationInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, PlayAnswerExplanationInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
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

PlayAnswerExplanationInteraction.prototype.bookMark = function() {

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

//InvalidInputInteraction.prototype.getInteractionKey = function() {
//    return this.interactionToReturnTo.getInteractionKey();
//};

InvalidInputInteraction.prototype.nextInteraction = function() {
    return this.interactionToReturnTo;
};

/*
    ReportChapterScore
*/
var ReportChapterScoreInteraction = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        AbstractCourseInteraction.call(this, metadata, ReportChapterScoreInteraction.KEY);
        this.course = course;
        this.courseState = courseState;
    }

    this.init(metadata, course, courseState);
};
ReportChapterScoreInteraction.KEY = "reportChapterScore";

ReportChapterScoreInteraction.prototype = new AbstractCourseInteraction();
ReportChapterScoreInteraction.prototype.constructor = ReportChapterScoreInteraction;

//TODO: Figure out the max number of questions on the fly, right now assuming it to be 4.
ReportChapterScoreInteraction.prototype.playAudio = function() {
    var currentChapterIndex = this.courseState.chapterIndex;
    var chapterScore = this.courseState.scoresByChapter[currentChapterIndex];
    var currentChapterNumber = currentChapterIndex + 1;

    var audioFileName = this.audioFileBase() + "chapter" + currentChapterNumber + "_" + chapterScore + "_out_of_4.wav";

    return audioFileName;
};

ReportChapterScoreInteraction.prototype.doesTakeInput = function() {
    return false;
}

ReportChapterScoreInteraction.prototype.nextInteraction = function() {
    return CertificateCourse.interactions[EndOfChapterMenuInteraction.KEY];
}

ReportChapterScoreInteraction.prototype.bookMark = function() {

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
