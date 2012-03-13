describe("Short Code", function () {
    var shortCode;

    beforeEach(function() {
        shortCode = new ShortCode();
    });

    it("should extract shortcode from called number", function(){
        expect(shortCode.getCode("123456")).toEqual("6");
        expect(shortCode.getCode("12341234")).toEqual("234");
        expect(shortCode.getCode("1234")).toEqual("");
    });

});
