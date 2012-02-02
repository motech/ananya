/*
    WelcomeInteraction
*/

var WelcomeInteraction = function(metadata, course) {
    this.init = function(metadata, course) {
        AbstractCourseInteraction.call(this, metadata);
        this.course = course;
    }

    this.init(metadata, course);
};

WelcomeInteraction.prototype = new AbstractCourseInteraction();
WelcomeInteraction.prototype.constructor = WelcomeInteraction;

WelcomeInteraction.prototype.playAudio = function() {
    return this.findAudio(this.course, "introduction");
};

WelcomeInteraction.prototype.doesTakeInput = function() {
    return false;
}

WelcomeInteraction.prototype.nextInteraction = function() {
    return CertificateCourse.interactions["startCourseOption"];
}

WelcomeInteraction.prototype.bookMark = function() {

}

/*
    StartCourseOption
*/
var StartCourseOption = function(metadata, course) {
    this.init = function(metadata, course) {
        AbstractCourseInteraction.call(this, metadata);
        this.course = course;
    }

    this.init(metadata, course);
};

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
    return CertificateCourse.interactions["noInputAction"];
}

StartCourseOption.prototype.processInputAndReturnNextInteraction = function(input){
    if(input == 1) {
        return CertificateCourse.interactions["welcome"];
    }
    return CertificateCourse.interactions["startNextChapter"];
}

/*
    LessonInteraction
*/
var LessonInteraction = function(metadata, course) {
    this.init = function(metadata, course) {
        AbstractCourseInteraction.call(this, metadata);
        this.course = course;
    }

    this.init(metadata, course);
};

LessonInteraction.prototype = new AbstractCourseInteraction();
LessonInteraction.prototype.constructor = LessonInteraction;

/*
    InvalidInputInteraction
*/
var InvalidInputInteraction = function(interactionToReturnTo, metadata) {
    this.init = function(interactionToReturnTo, metadata) {
        AbstractCourseInteraction.call(null, metadata);
        this.metadata = metadata;
        this.interactionToReturnTo = interactionToReturnTo;
    }

    this.init(interactionToReturnTo, metadata);
};

InvalidInputInteraction.prototype = new AbstractCourseInteraction();
InvalidInputInteraction.prototype.constructor = InvalidInputInteraction;

InvalidInputInteraction.prototype.playAudio = function() {
    return this.metadata.audioFileBase + this.metadata.invalidInputRetryAudio;
};

InvalidInputInteraction.prototype.doesTakeInput = function() {
    return false;
};

InvalidInputInteraction.prototype.nextInteraction = function() {
    return this.interactionToReturnTo;
}

/*
    StartNextChapter
*/
//TODO: This should extend from something called silent interaction.
var StartNextChapter = function(metadata, course, courseState) {
    this.init = function(metadata, course, courseState) {
        this.course = course;
    }

    this.processSilentlyAndReturnNextState = function() {
        var nextState;
        if(courseState.chapterIndex == null) {
            courseState.setChapterIndex(0);
            courseState.setLessonOrQuestionIndex(0);
            nextState = CertificateCourse.interactions["lesson"];
        }
        else {
            var currentChapterIndex = courseState.chapterIndex;
            var currentLessonOrQuestionIndex = courseState.lessonOrQuestionIndex;
            var maxChapterIndex = course.children.length-1;
            if(currentChapterIndex >= maxChapterIndex) {
               nextState = CertificateCourse.interactions["endOfCourse"];
            }
            else {
                courseState.chapterIndex++;
                courseState.setLessonOrQuestionIndex(0);
                nextState = CertificateCourse.interactions["lesson"];
            }
        }
        return nextState;
    }

    this.init(metadata, course, courseState);
};