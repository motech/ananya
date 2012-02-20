describe("Call Detail Logger", function() {
    beforeEach(function() {
        callDetailLogger = new CallDetailLogger();
    });

    it("when initialized should have empty data transfer list.", function() {
        expect(callDetailLogger.dataTransferList.size()).toEqual(0);
    });

    it("on start call should add item to data transfer list.", function() {
        callDetailLogger.start_call();
        expect(callDetailLogger.dataTransferList.size()).toEqual(1);
        expect(callDetailLogger.dataTransferList.transferList[0].data.start).toContain("GMT+0530 (IST)");
    });

    it("on end call should add item to data transfer list.", function() {
        callDetailLogger.end_call();
        expect(callDetailLogger.dataTransferList.size()).toEqual(1);
        expect(callDetailLogger.dataTransferList.transferList[0].data.end).toContain("GMT+0530 (IST)");
    });

    it("on data post successful should drain data transfer list.", function() {
        callDetailLogger.start_call();
        callDetailLogger.end_call();
        expect(callDetailLogger.dataTransferList.size()).toEqual(2);
        callDetailLogger.dataPostSuccessful();
        expect(callDetailLogger.dataTransferList.size()).toEqual(0);
    });

    it("on and call should add item to data transfer list.", function() {
        callDetailLogger.end_call();
        expect(callDetailLogger.dataTransferList.size()).toEqual(1);
        expect(callDetailLogger.dataToPost()).toEqual(Utility.stringify(callDetailLogger.dataTransferList.transferList));
    });
})