var CallContext = function(course, metadata) {
    this.init = function(course, metadata) {
        Course.buildLinks(course);
        this.course = course;
        this.currentInteraction = course;
        this.metadata = metadata;
        this.shouldPlayNextIntroduction = true;
    };

    this.navigateTo = function(shortCode) {
        for (var i = 0; i < shortCode.length; i++) {
            if (!this.isValidInput(shortCode.charAt(i))) break;
            this.handleInput(shortCode.charAt(i));
        }
    };

    this.handleInput = function(input) {
        if (input == 0) {
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
        return this.currentInteraction.data.type == "Lesson";
    };

    this.isAtCourseRoot = function() {
        return this.currentInteraction == this.course;
    };

    this.currentInteractionIntroduction = function() {
        return this.audioFileBase() + this.findContentByName("introduction").value;
    };

    this.shouldPlayIntroduction = function() {
        return this.findContentByName("introduction") != null && this.shouldPlayNextIntroduction;
    };

    this.currentInteractionMenu = function() {
        return this.audioFileBase() + this.findContentByName("menu").value;
    };

    this.currentInteractionLesson = function() {
        return this.audioFileBase() + this.findContentByName("lesson").value;
    };

    this.audioFileBase = function() {
        return this.metadata['audio.url']+this.metadata['jobaid.audio.url'];
    };

    this.resetPromptCounts = function() {
        this.noInputCount = 0;
        this.invalidInputCount = 0;
    };

    this.findContentByName = function(contentName) {
        var contents = this.currentInteraction.contents
        var contentLength = contents.length
        for (i = 0; i < contentLength; i++) {
            if (contents[i].name == contentName)
                return contents[i];
        }
        return undefined;
    };

    this.init(course, metadata);
};