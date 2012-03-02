var CallContext = function(course, metadata, promptContext) {
    this.init = function(course, metadata, promptContext) {
        Course.buildLinks(course);
        this.promptContext = promptContext;
        this.course = course;
        this.currentInteraction = course;
        this.metadata = metadata;
        this.shouldPlayNextIntroduction = true;
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

    this.navigateTo = function(shortCode) {
         this.shouldPlayNextIntroduction = true;
         var levels = this.course.children;
         for(levelNo=0; levelNo < levels.length ; levelNo++){
            var chapters = levels[levelNo].children;

            for (var chapterNo = 0; chapterNo < chapters.length ; chapterNo++) {
                var lessons = chapters[chapterNo].children;

                for(var lessonNo=0;lessonNo < lessons.length ; lessonNo++){
                    if (lessons[lessonNo].data.shortcode == shortCode)
                        {
                            this.currentInteraction = lessons[lessonNo];
                            return this;
                        }
                }
            }
         }
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

    this.audioForInvalidInputRetry = function() {
        return this.promptContext.audioForInvalidInputRetry();
    };

    this.audioForOptionToGoToTopLevel = function() {
        return this.audioFileBase() + this.metadata['option.to.top.level.audio'];
    };

    this.init(course, metadata,promptContext);
};