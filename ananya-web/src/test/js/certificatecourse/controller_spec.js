// TODO: duplicate code to generate test interaction object. return via testutils
describe("Certificate course controller spec", function() {

    var metadata, course;
    var audioFileBase = "../audio/";
    var certificateCourseLocation = "certificatecourse/";
    var controller;

    beforeEach(function() {
        metadata = {
                        "audio.url": audioFileBase,
                        "certificate.audio.url" : certificateCourseLocation,
                        "maximum.invalid.input.count" : "2",
                        "maximum.noinput.count" : "2"
     };

        course = certificationCourseWithTwoLessonsInEveryChapter();
        controller = new CertificateCourseController(course, metadata,
            new CourseState(
                {
                    "bookmark" : {
                        "type" : "lesson",
                        "chapterIndex" : 0,
                        "lessonIndex" : 0
                    }
                },
                certificationCourseWithTwoLessonsInEveryChapter()), new DataTransferList(),"../");
    });

    it("should get the audio to be played", function () {
        expect(controller.playAudio()).toEqual("../audio/certificatecourse/chapter_1_lesson_1.wav");
    });

    it("should initialize prompt context at start", function() {
        expect(controller.promptContext).toBeDefined();
    });

    it("should increment no input count in prompt context if there is no input", function() {
        expect(controller.promptContext.noInputCount).toEqual(0);
        controller.gotNoInput();
        expect(controller.promptContext.noInputCount).toEqual(1);
    });

    it("should return the interaction's no input interaction if no input count has exceeded max", function () {
        var interactionWithoutInput = {
                                        getInteractionKey:function() {return "anotherKey";},
                                        getCourseType :function() {return "course";},
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
        };
        var currentInteraction = { continueWithoutInput : function() {return interactionWithoutInput;},
                                        getInteractionKey:function() {return "myKey";},
                                        getCourseType :function() {return "course";},
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
                                 };

        controller.setInteraction(currentInteraction);

        controller.gotNoInput();
        expect(controller.interaction).toEqual(currentInteraction);
        controller.gotNoInput();
        expect(controller.interaction).toEqual(currentInteraction);
        controller.gotNoInput();
        expect(controller.interaction).toEqual(interactionWithoutInput);
    });

    it("for interactions involving input, if the interaction is progressing to next due to successive no inputs by user, the prompt counts should be reset.", function () {
        var interactionWithoutInput = {
                                        getInteractionKey:function() {return "anotherKey";},
                                        getCourseType: function() { return "myCourseType"; },
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
        };
        var currentInteraction = {
                                    validateInput : function(input) { return input == 1; },
                                    continueWithoutInput : function() {return interactionWithoutInput;},
                                    getInteractionKey:function() {return "myKey";},
                                    getCourseType: function() { return "myCourseType"; },
                                    getCourseItemState: function() { return "start"; },
                                    shouldLog: function() { return false; }
                                 };

        var invalidInput = 2;
        controller.setInteraction(currentInteraction);

        controller.gotNoInput();
        expect(controller.promptContext.noInputCount).toEqual(1);
        expect(controller.promptContext.invalidInputCount).toEqual(0);

        controller.processInput(invalidInput);
        expect(controller.promptContext.noInputCount).toEqual(1);
        expect(controller.promptContext.invalidInputCount).toEqual(1);

        controller.playingDone();

        controller.gotNoInput();
        expect(controller.promptContext.noInputCount).toEqual(2);
        expect(controller.promptContext.invalidInputCount).toEqual(1);

        controller.gotNoInput();
        expect(controller.promptContext.noInputCount).toEqual(0);
        expect(controller.promptContext.invalidInputCount).toEqual(0);
    });

    it("for interactions involving input, the interaction should progress to the next if input is valid.", function() {
        var interactionAfterInput = {
                                        getInteractionKey:function() {return "myKey";},
                                        getCourseType :function() {return "course";},
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
        };
        var interactionNeedingInput = {
                                        validateInput : function(input) { return true;},
                                        processInputAndReturnNextInteraction : function(input) { return interactionAfterInput; },
                                        getInteractionKey:function() {return "myKey";},
                                        getCourseType :function() {return "course";},
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
                                       };

        controller.setInteraction(interactionNeedingInput);
        controller.processInput(1);
        expect(controller.interaction).toEqual(interactionAfterInput);
    });

    it("for interactions involving input, the prompt counts should be reset if input is valid", function () {
        var interactionAfterInput = {
                                        getInteractionKey:function() {return "myKey";},
                                        getCourseType :function() {return "course";},
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
        };
        var interactionAllowingOnly1AsValidInput = {
                                        validateInput : function(input) { return input == 1;},
                                        processInputAndReturnNextInteraction : function(input) { return interactionAfterInput; },
                                        getInteractionKey:function() {return "myKey";},
                                        getCourseType :function() {return "course";},
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
                                       };

        var validInput = 1, invalidInput = 2;

        controller.setInteraction(interactionAllowingOnly1AsValidInput);

        controller.processInput(invalidInput);
        expect(controller.promptContext.invalidInputCount).toEqual(1);
        expect(controller.promptContext.noInputCount).toEqual(0);

        controller.playingDone();

        controller.gotNoInput();
        expect(controller.promptContext.invalidInputCount).toEqual(1);
        expect(controller.promptContext.noInputCount).toEqual(1);

        controller.processInput(invalidInput);
        expect(controller.promptContext.invalidInputCount).toEqual(2);
        expect(controller.promptContext.noInputCount).toEqual(1);

        controller.playingDone();
        
        controller.processInput(validInput);
        expect(controller.promptContext.invalidInputCount).toEqual(0);
        expect(controller.promptContext.noInputCount).toEqual(0);
    });
    
    it("for interactions involving input, if the input is invalid, should progress to invalid input interaction with interactionToReturnTo current interaction", function () {

        var interactionNeedingInput = {
                                        validateInput : function(input) { return false;},
                                        getInteractionKey:function() {return "myKey";},
                                        getCourseType :function() {return "course";},
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
                                       };
        controller.setInteraction(interactionNeedingInput);
        controller.processInput(1);
        expect(controller.interaction.nextInteraction()).toEqual(interactionNeedingInput);

    });

    it("for interactions involving input, if the input is not valid for more than allowed number of times, the call should disconnect", function () {
        var interactionNeedingInput = {
                                        validateInput : function(input) { return false;},
                                        getInteractionKey:function() {return "myKey";},
                                        getCourseType :function() {return "course";},
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
                                       };
        controller.setInteraction(interactionNeedingInput);

        controller.processInput(1);
        expect(controller.interaction.disconnect).toBeUndefined();
        controller.playingDone();

        controller.processInput(1);
        expect(controller.interaction.disconnect).toBeUndefined();
        controller.playingDone();

        controller.processInput(1);
        expect(controller.interaction.disconnect()).toEqual(true);
    });

    it("should quietly execute current interaction and all subsequent interactions, till interaction does not need phone", function() {
        var phoneInteraction = {
                                        getInteractionKey:function() {return "myKey";},
                                        getCourseType :function() {return "course";},
                                        getCourseItemState: function() { return "start"; },
                                        shouldLog: function() { return false; }
        };
        var noPhoneInteraction3 = {
                                    processSilentlyAndReturnNextState : function() {return phoneInteraction;}
        };
        var noPhoneInteraction2 = {
                                    processSilentlyAndReturnNextState : function() {return noPhoneInteraction3;}
        };
        var noPhoneInteraction1 = {
                                    processSilentlyAndReturnNextState : function() {return noPhoneInteraction2;}
        };

        spyOn(noPhoneInteraction1, "processSilentlyAndReturnNextState").andCallThrough();
        spyOn(noPhoneInteraction2, "processSilentlyAndReturnNextState").andCallThrough();
        spyOn(noPhoneInteraction3, "processSilentlyAndReturnNextState").andCallThrough();

        controller.setInteraction(noPhoneInteraction1);

        expect(noPhoneInteraction1.processSilentlyAndReturnNextState).toHaveBeenCalled();
        expect(noPhoneInteraction2.processSilentlyAndReturnNextState).toHaveBeenCalled();
        expect(noPhoneInteraction3.processSilentlyAndReturnNextState).toHaveBeenCalled();
        expect(controller.interaction).toEqual(phoneInteraction);
    });

    it("should set a bookmarkable interaction's key in courseState, when a transition is happening to that interaction", function () {
        var keyForInteraction = "keyForInteraction";
        var bookmarkableInteraction = {
                                         getInteractionKey:function() {return keyForInteraction;},
                                         getCourseType :function() {return "course";},
                                         getCourseItemState: function() { return "start"; },
                                         shouldLog: function() { return false; }
                                      };
        controller.setInteraction(bookmarkableInteraction);

        expect(controller.courseState.interactionKey).toEqual(keyForInteraction);
    });

    it("whenever switching to an interaction, should add the current state's json to the data transfer list", function() {
        var phoneInteraction = {
                                     getInteractionKey:function() {return "myKey";},
                                     getCourseType :function() {return "course";},
                                     getCourseItemState: function() { return "start"; },
                                     shouldLog: function() { return false; }
        };

        var jsonState = {"chapterIndex":1, "lessonOrQuestionIndex":5};
        spyOn(controller.courseState, "toJson").andReturn(jsonState);

        spyOn(controller.dataTransferList, "add");

        controller.setInteraction(phoneInteraction);

        expect(controller.courseState.toJson).toHaveBeenCalled();
        expect(controller.dataTransferList.add).toHaveBeenCalledWith(jsonState, DataTransferList.TYPE_CC_STATE);
     });

     it("should set exit interaction if the call is disconnected.", function () {
         controller.callDisconnected();
         expect(controller.interaction.exit()).toEqual(true);
     });


});
