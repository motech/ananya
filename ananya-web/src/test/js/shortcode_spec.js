describe("Short Code", function () {
    var shortCode;

    beforeEach(function() {
        shortCode = new ShortCode();
    });

    it("should extract shortcode from called number", function(){
        expect(shortCode.getCode("1234","123456")).toEqual("56");
        expect(shortCode.getCode("1234","12341234")).toEqual("1234");
        expect(shortCode.getCode("1234","1234")).toEqual("");
    });

    it("should return basenumber for invalid number", function(){
        expect(shortCode.getCode("1234","12")).toEqual("");
        expect(shortCode.getCode("1234","551234")).toEqual("");
        expect(shortCode.getCode("1234","55234")).toEqual("");
    });

});
