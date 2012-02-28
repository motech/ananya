describe("Registration Controller", function () {
    var controller;
    var metadata;

    beforeEach(function() {
    });

    it("should capture records and return next fields in order", function() {
        metadata = {};
        controller = new RegisterController(metadata);

        expect(controller.nextField()).toEqual("designation");
        controller.capture("ANM");

        expect(controller.nextField()).toEqual("name");
        controller.capture("Sri");

        expect(controller.nextField()).toEqual("district");
        controller.capture("District 9");

        expect(controller.nextField()).toEqual("block");
        controller.capture("Block 13");

        expect(controller.nextField()).toEqual("panchayat");
        controller.capture("Patti");

        expect(controller.name()).toEqual("Sri");
        expect(controller.designation()).toEqual("ANM");
        expect(controller.block()).toEqual("Block 13");
        expect(controller.district()).toEqual("District 9");
        expect(controller.panchayat()).toEqual("Patti");
        expect(controller.allCaptured()).toEqual(true);

    });

    it("should play all prompts based on field", function() {

        metadata = {
            "audio.url":"audio",
            "register.audio.url":"/register",
            "register.name.say":"/say.name.wav",
            "register.name.confirm":"/confirm.name.wav",
            "register.name.noinput":"/noinput.name.wav",
            "register.name.rerecord":"/rerecord.name.wav"
        };
        controller = new RegisterController(metadata,"../");

        expect(controller.playPrompt("name")).toEqual("../audio/register/say.name.wav");
        expect(controller.playConfirmPrompt("name")).toEqual("../audio/register/confirm.name.wav");
        expect(controller.playNoInputPrompt("name")).toEqual("../audio/register/noinput.name.wav");
        expect(controller.playRerecordPrompt("name")).toEqual("../audio/register/rerecord.name.wav");
    });

    it("should play back prompt for fields that are not voice recognised", function() {
        metadata = {};
        controller = new RegisterController(metadata);
        expect(controller.playBackPrompt("name","record")).toEqual("record");
        expect(controller.playBackPrompt("designation","record")).toEqual("record");
    });

    it("should play back recorded audio for fields that are voice recognised", function() {
       metadata = {
                   "audio.url":"audio",
                   "location.audio.url":"/location/",
               };
        var map1 = {"resultKey":"districtAudio" };
        var map2 = {"resultKey":"blockAudio" };
        var map3 = {"resultKey":"panchayatAudio" };

        controller = new RegisterController(metadata,"../");
        expect(controller.playBackPrompt("district",map1)).toEqual("../audio/location/districtAudio.wav");
        expect(controller.playBackPrompt("block",map2)).toEqual("../audio/location/blockAudio.wav");
        expect(controller.playBackPrompt("panchayat",map3)).toEqual("../audio/location/panchayatAudio.wav");
    });

    it("should play registration done prompt and the beep", function() {
        metadata = {
                    "audio.url":"audio",
                    "register.audio.url":"/register/",
                    "register.complete":"regdone.wav",
                    "registration.beep.audio" : "beep.wav"
            };
        controller = new RegisterController(metadata, "../");
        expect(controller.playRegistrationDone()).toEqual("../audio/register/regdone.wav");
        expect(controller.playBeep()).toEqual("../audio/register/beep.wav");
    });

    it("should go to certification flow for certification number", function() {
        metadata = {
                        "certificate.application.number" : "123"
                    };
        controller = new RegisterController(metadata);
        expect(controller.nextFlow("123")).toEqual("certificatecourse.vxml");
    });

    it("should go to jobaid flow for jobaid application number", function() {
         metadata = {
                        "certificate.application.number" : "123",
                    };
        controller = new RegisterController(metadata);
        expect(controller.nextFlow("456")).toEqual("jobaid.vxml");
    });
    
    it("should give the URL to submit registration data", function () {
         metadata = {
                        "register.submit.url" : "flw/register"
                    };

        controller = new RegisterController(metadata, "../");
        expect(controller.submitUrl()).toEqual("../flw/register");
    });

    it("should give the URL to submit name", function () {
         metadata = {
                        "register.name.submit.url" : "flw/record/name"
                    };

        controller = new RegisterController(metadata, "../../");
        expect(controller.submitNameUrl()).toEqual("../../flw/record/name");
    });

    it("should give the URL to submit name if the URL specified starts with http://", function () {
         metadata = {
                        "context.path" : "/ananya",
                        "url.version" : "v1.2",
                        "register.name.submit.url" : "http://someOMURL/ananya/flw/record/name"
                    };

        controller = new RegisterController(metadata, "../");
        expect(controller.submitNameUrl()).toEqual("http://someOMURL/ananya/flw/record/name");
    });

    it("should give the URL to get grammar", function () {
         metadata = {
                        "grammar.url" : "grammar"
                    };

        controller = new RegisterController(metadata, "../");
        expect(controller.getDistrictGrammar()).toEqual("../grammar/ANANYA_S001_DISTRICTS.grxml");
    });




});
