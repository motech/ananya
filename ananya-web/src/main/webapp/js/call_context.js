var CallContext = function(course, metadata) {
    this.init = function(course, metadata) {
        Course.buildLinks(course);
        this.course = course;
        this.currentInteraction = course;
        this.metadata = metadata;
        this.shouldPlayNextIntroduction = true;
    };

    this.handleInput = function(input) {
        if(input == 0) {
            this.shouldPlayNextIntroduction = false;
            this.currentInteraction = course;
            return this;
        }
        this.shouldPlayNextIntroduction = true;
        this.currentInteraction = this.currentInteraction.children[input - 1];
        return this;
    };

    this.lessonFinished = function() {
        var nextChapterWhoseMenuIsToBePlayed = this.currentInteraction.siblingOnRight.parent;
        var chapterContainingCurrentLesson = this.currentInteraction.parent;

        this.shouldPlayNextIntroduction = (chapterContainingCurrentLesson != nextChapterWhoseMenuIsToBePlayed);

        this.currentInteraction = nextChapterWhoseMenuIsToBePlayed;

        return this;
    };


    this.isValidInput = function(childNumber) {
        return 0 <= childNumber && childNumber <= this.currentInteraction.children.length;
    };

    this.isAtALesson = function() {
        return this.currentInteraction.data.lesson ? true : false;
    };

    this.isAtCourseRoot = function() {
        return this.currentInteraction == this.course;
    };

    this.currentInteractionIntroduction = function() {
        return this.audioFileBase() + this.currentInteraction.data.introduction;
    };

    this.currentInteractionMenu = function() {
        return this.audioFileBase() + this.currentInteraction.data.menu;
    };

    this.currentInteractionLesson = function() {
        return this.audioFileBase() + this.currentInteraction.data.lesson;
    };

    this.audioFileBase = function() {
        return this.metadata.audioFileBase;
    };

    this.resetPromptCounts = function() {
        this.noInputCount = 0;
        this.invalidInputCount = 0;
    }

    this.init(course, metadata);
};