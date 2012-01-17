var CertificationCourseContext = function(course, metadata) {
    this.init = function(course, metadata) {
        Course.buildLinks(course);
        this.course = course;
        this.currentInteraction = course;
        this.metadata = metadata;

        this.hasFinishedChapter = false;
    };

    this.isAtCourseRoot = function() {
        return this.currentInteraction == course;
    };

    this.isAtLesson = function() {
        return this.currentInteraction.data.type == "lesson";
    };

    this.currentInteractionLesson = function() {
        return this.findAudio("lesson");
    };

    this.currentInteractionMenu = function() {
        return this.findAudio("menu");
    };

    this.lessonOrQuizFinished = function() {
        var isAtLastQuizOfChapter = this.currentInteraction.parent != this.currentInteraction.siblingOnRight.parent;
        if (isAtLastQuizOfChapter) {
            this.hasFinishedChapter = true;
        }
        else {
            this.currentInteraction = this.currentInteraction.siblingOnRight;
        }
    };

    this.findContentByName = function(contentName) {
        var contents = this.currentInteraction.contents
        var contentLength = contents.length
        for(i = 0; i< contentLength; i++){
            if(contents[i].name == contentName)
                return contents[i];
        }
        return undefined;
    };

    this.findAudio = function(contentName) {
        return this.audioFileBase() + this.findContentByName(contentName).value;
    };

    this.audioFileBase = function() {
        return this.metadata.audioFileBase;
    };

    this.init(course, metadata);
};