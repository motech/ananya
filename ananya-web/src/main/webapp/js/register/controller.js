var RegisterController = function() {
    var count = 0;
    var fields = ["designation", "name", "district", "block", "village"];
    var records = [];

    this.nextField = function() {
        return fields[count];
    };

    this.capture = function(record) {
        records[count] = record;
        count++;
    };

    this.say = function() {
        return records.length;
    };

    this.getPrompt = function(field) {
        return "please say your " + field;
    };

    this.getConfirmPrompt = function(field) {
        return "we heard your " + field + " as ";
    };

    this.getNoInputPrompt = function(field) {
        return "you did not say anything. please say your " + field;
    };

    this.getRerecordPrompt = function(field) {
        return "please enter 1 if you want to rerecord your " + field;
    };

    this.isVoiceRecognised = function(field) {
        return field != "name" && field != "designation" && false;
    };

    this.getGrammar = function(field, record) {
        return "some.grxml";
    };

    this.allCaptured = function() {
        return count >= fields.length;
    };

};

