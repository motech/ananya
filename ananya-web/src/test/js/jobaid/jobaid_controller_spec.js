describe("Controller for jobaid ", function () {
    var controller;

    it("should return 'partially_registered' form for partially registered caller in jobaid when not dialled in via short code", function() {
        var callerData = {
            "isRegistered" : "false",
            "currentJobAidUsage" : 1,
            "maxAllowedUsageForOperator" : 999
        };
        var metadata = {'usage.general' : 50};
        var callContext = { "dialedViaShortCode" : false };
        controller = new JobAidController(callerData, metadata, callContext);
        expect(controller.decideFlowForJobAid()).toEqual("#partially_registered");
    });

    it("should return 'registered' form for registered caller in jobaid when not dialled in via short code", function() {
        var callerData = {
            "isRegistered" : "true",
           "currentJobAidUsage" : 1,
           "maxAllowedUsageForOperator" : 999
        };
        var metadata = {'usage.general' : 50};
        var callContext = { "dialedViaShortCode" : false };
        controller = new JobAidController(callerData, metadata, callContext);
        expect(controller.decideFlowForJobAid()).toEqual("#registered");
    });

    it("should return 'registered' form for registered caller in jobaid when dialled in via short code and not listened to registered user welcome prompt", function() {
        var callerData = {
            "isRegistered" : "true",
            "promptsHeard" : {},
            "currentJobAidUsage" : 1,
            "maxAllowedUsageForOperator" : 999
        };
        var metadata = {'usage.general' : 50};
        var callContext = { "dialedViaShortCode" : true };
        controller = new JobAidController(callerData, metadata, callContext);
        expect(controller.decideFlowForJobAid()).toEqual("#registered");
    });

    it("should return 'partially_registered' form for partially registered caller in jobaid when dialled in via short code and not listened to partially registered user welcome prompt", function() {
        var callerData = {
            "isRegistered" : "false",
            "promptsHeard" : {
            }
        };
        var metadata = {'usage.general' : 50};
        var callContext = { "dialedViaShortCode" : true };
        controller = new JobAidController(callerData, metadata, callContext);
        expect(controller.decideFlowForJobAid()).toEqual("#partially_registered");
    });

    it("should return 'controller' form for partially registered caller in jobaid when dialled in via short code and listened to partially registered user welcome prompt", function() {
        var callerData = {
            "isRegistered" : "false",
            "promptsHeard" : {
                "unregistered_welcome_message" : 1,
                "registered_welcome_message" : 1
            },
             "currentJobAidUsage" : 1,
             "maxAllowedUsageForOperator" : 999
        };
        var metadata = {'usage.general' : 50};
        var callContext = { "dialedViaShortCode" : true };
        controller = new JobAidController(callerData, metadata, callContext);
        expect(controller.decideFlowForJobAid()).toEqual("#controller");
    });

    it("should return 'controller' form for registered caller in jobaid when dialled in via short code and listened to registered user welcome prompt", function() {
        var callerData = {
            "isRegistered" : "true",
            "promptsHeard" : {
                "unregistered_welcome_message" : 1,
                "registered_welcome_message" : 1
            },
             "currentJobAidUsage" : 1,
             "maxAllowedUsageForOperator" : 999
        };
        var metadata = {'usage.general' : 50};
        var callContext = { "dialedViaShortCode" : true };
        controller = new JobAidController(callerData, metadata, callContext);
        expect(controller.decideFlowForJobAid()).toEqual("#controller");
    });

    it("should return 'max_usage' form for registered caller in jobaid with max usage reached", function() {
        var callerData = {
            "isRegistered" : "true",
            "currentJobAidUsage" : 1000,
            "maxAllowedUsageForOperator" : 999
        };
        controller = new JobAidController(callerData);
        expect(controller.decideFlowForJobAid()).toEqual("#max_usage");
    });

    it("should tell if the caller is registered", function () {
        var registeredCaller = {
            "isRegistered" : "true"
        };
        controller = new JobAidController(registeredCaller);
        expect(controller.isCallerRegistered()).toEqual(true);

        var unRegisteredCaller = {
            "isRegistered" : "false"
        };
        controller = new JobAidController(unRegisteredCaller);
        expect(controller.isCallerRegistered()).toEqual(false);
    });

    it("should return jobaid welcome prompt for registered users", function() {
         var registeredCaller = {};
         var metaData = {
            "audio.url" : "audio/",
            "jobaid.audio.url" : "jobaid/",
            "jobaid.welcome.registered" : "0001_welcome_job_aid_reg.wav"
         };
         controller = new JobAidController(registeredCaller , metaData);
         expect(controller.jobAidWelcomePromptRegistered()).toEqual("audio/jobaid/0001_welcome_job_aid_reg.wav")
    });

    it("should return jobaid welcome prompt for partially registered users", function() {
         var registeredCaller = {};
         var metaData = {
            "audio.url" : "audio/",
            "jobaid.audio.url" : "jobaid/",
            "jobaid.welcome.registered" : "0001_welcome_job_aid_not_reg.wav"
         };
         controller = new JobAidController(registeredCaller , metaData);
         expect(controller.jobAidWelcomePromptRegistered()).toEqual("audio/jobaid/0001_welcome_job_aid_not_reg.wav")
    });
});
