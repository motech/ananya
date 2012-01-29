describe("Caller", function () {
    var caller;

    it("should return 'registered_bookmark_present' form for registered caller with bookmark", function() {
        var callerData = {
            "isRegistered" : "true",
            "bookmark" : {"type":"AA"}
        };
        caller = new Caller(callerData);
        expect(caller.decideFlow()).toEqual("#registered_bookmark_present");
    });


    it("should return 'registered_bookmark_absent' form for registered caller without bookmark", function() {
        var callerData = {
            "isRegistered" : "true",
            "bookmark" : "{}"
        };
        caller = new Caller(callerData);
        expect(caller.decideFlow()).toEqual("#registered_bookmark_absent");
    });


    it("should return 'unregistered' form for unregistered caller", function() {
        var callerData = {
            "isRegistered" : "false"
        };
        caller = new Caller(callerData);
        expect(caller.decideFlow()).toEqual("#unregistered");
    });

});
