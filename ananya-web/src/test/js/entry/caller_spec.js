describe("Controller for certificate course entry", function () {
    var controller;

    it("should return 'registered_bookmark_present' form for registered caller with bookmark", function() {
        var callerData = {
            "isRegistered" : "true",
            "bookmark" : {"type":"AA"}
        };
        controller = new Controller(callerData);
        expect(controller.decideFlow()).toEqual("#registered_bookmark_present");
    });


    it("should return 'registered_bookmark_absent' form for registered caller without bookmark", function() {
        var callerData = {
            "isRegistered" : "true",
            "bookmark" : "{}"
        };
        controller = new Controller(callerData);
        expect(controller.decideFlow()).toEqual("#registered_bookmark_absent");
    });


    it("should return 'unregistered' form for unregistered caller", function() {
        var callerData = {
            "isRegistered" : "false"
        };
        controller = new Controller(callerData);
        expect(controller.decideFlow()).toEqual("#unregistered");
    });

});
