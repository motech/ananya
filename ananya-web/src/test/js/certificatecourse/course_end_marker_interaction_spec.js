describe("Course end marker", function() {
    var metadata, course, courseEndMarker;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        CertificateCourse.interactions = new Array();
    });

    it("should set start certificate course interaction, first chapter, first lesson in state and go to end of course", function () {
        var courseState = new CourseState();
        var courseEndMarker = new CourseEndMarkerInteraction(metadata, course, courseState);

        CertificateCourse.interactions[EndOfCourseInteraction.KEY] = {};

        var nextState = courseEndMarker.processSilentlyAndReturnNextState();

        expect(courseState.chapterIndex).toEqual(null);
        expect(courseState.lessonOrQuestionIndex).toEqual(null);
        expect(courseState.interactionKey).toEqual(StartCertificationCourseInteraction.KEY);
        expect(nextState).toEqual(CertificateCourse.interactions[EndOfCourseInteraction.KEY]);
    });

    it("should resume call at the same place where the call was left", function () {
        var courseState = new CourseState();
        var courseEndMarker = new CourseEndMarkerInteraction(metadata, course, courseState);
        expect(courseEndMarker.resumeCall()).toEqual(courseEndMarker);
    });
});

describe("End Of Course Interaction", function() {
    it("shouldLog method must be present on this interaction and return false", function () {
        var endOfCourseInteraction = new EndOfCourseInteraction();
        expect(endOfCourseInteraction.shouldLog()).toEqual(false);
    });
});