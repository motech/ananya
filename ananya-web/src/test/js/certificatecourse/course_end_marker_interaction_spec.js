describe("Course end marker", function() {
    var metadata, course, courseEndMarker;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        CertificateCourse.interactions = new Array();
    });

    it("should set start next chapter interaction, first chapter, first lesson in state and go to end of course", function () {
        var courseState = new CourseState();
        var courseEndMarker = new CourseEndMarkerInteraction(metadata, course, courseState);

        CertificateCourse.interactions["endOfCourse"] = {};

        var nextState = courseEndMarker.processSilentlyAndReturnNextState();

        expect(courseState.chapterIndex).toEqual(null);
        expect(courseState.lessonOrQuestionIndex).toEqual(null);
        expect(courseState.interactionKey).toEqual(StartNextChapter.KEY);
        expect(nextState).toEqual(CertificateCourse.interactions["endOfCourse"]);
    });

    it("should resume call at the same place where the call was left", function () {
        var courseState = new CourseState();
        var courseEndMarker = new CourseEndMarkerInteraction(metadata, course, courseState);
        expect(courseEndMarker.resumeCall()).toEqual(courseEndMarker);
    });
});