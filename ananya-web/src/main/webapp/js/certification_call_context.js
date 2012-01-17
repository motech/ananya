var CertificationCallContext = function(course, metadata) {
    this.init = function(course, metadata) {
        Course.buildLinks(course);
        this.course = course;
        this.currentInteraction = course.children[0].children[0];
        this.metadata = metadata;
    };

    this.isAtCourseRoot = function() {
        return false;
    };

    this.isAtLesson = function() {
        return true;
    };

    this.currentInteractionLesson = function() {
        return this.findAudio("lesson");
    };

    this.currentInteractionMenu = function() {
        return this.findAudio("menu");
    };

    this.lessonFinished = function() {
        this.currentInteraction = this.currentInteraction.siblingOnRight;
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