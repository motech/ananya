describe("Play final score interaction", function() {

    var metadata, course, playFinalScoreInteraction;
    var audioFileBase = "./audio/";
    var certificateCourseLocation = "certificatecourse/";
    var courseState;

    beforeEach(function() {
        metadata = {"audio.url": audioFileBase, "certificate.audio.url" : certificateCourseLocation, "certificate.end.final.score":"0{0}_final_score_{1}.wav", "certificate.end.final.score.prefix.start":"248"};
        course = certificationCourseWithTwoLessonsInEveryChapter();
        courseState = new CourseState();
        CertificateCourse.interactions = new Array();
        playFinalScoreInteraction = new PlayFinalScoreInteraction(metadata, course, courseState);
    });

    it("should play the correct final score wave file", function () {
        courseState.setChapterIndex(2);
        courseState.setLessonOrQuestionIndex(4);
        courseState.scoresByChapter = {
            "0" : 3,
            "1" : 0,
            "2" : 1
        };

        expect(playFinalScoreInteraction.playAudio()).toEqual("./audio/certificatecourse/0252_final_score_4.wav");
    });

    it("should not take any input", function() {
        expect(playFinalScoreInteraction.doesTakeInput()).toEqual(false);
    });

    it("should return play course result as next interaction and should not change the course state", function () {
        courseState.setChapterIndex(2);
        courseState.setLessonOrQuestionIndex(4);
        CertificateCourse.interactions[PlayCourseResultInteraction.KEY] = {};

        expect(playFinalScoreInteraction.nextInteraction()).toEqual(CertificateCourse.interactions[PlayCourseResultInteraction.KEY]);
        expect(courseState.chapterIndex).toEqual(2);
        expect(courseState.lessonOrQuestionIndex).toEqual(4);
    });
});