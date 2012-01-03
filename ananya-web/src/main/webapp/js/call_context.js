var CallContext = function(course) {
    this.init = function(course) {
        this.currentInteraction = course;
        Course.buildLinks(course);
    }

    this.goToChild = function(childNumber) {
        this.currentInteraction = this.currentInteraction.children[childNumber - 1];
        return this;
    }

    this.isValidChild = function(childNumber) {
        return 0 < childNumber && childNumber <= this.currentInteraction.children.length;
    }

    this.lessonFinished = function() {
        this.currentInteraction = this.currentInteraction.siblingOnRight.parent;
        return this;
    }

    this.init(course);
}