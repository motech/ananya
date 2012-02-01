var CertificateCourse = function () {
};

CertificateCourse.interactions = new Array();

var CertificateCourseController = function(course, metadata) {
    this.init = function(course, metadata) {
        CertificateCourse.interactions["welcome"] = new WelcomeInteraction(metadata, course);
        CertificateCourse.interactions["startCourseOption"] = new StartCourseOption(metadata, course);
        
        this.setInteraction(CertificateCourse.interactions["welcome"]);
    };

    this.playAudio = function() {
        return this.interaction.playAudio();
    };

    this.nextAction = function() {
        if(this.interaction.doesTakeInput()) {
            return "#collectInput";
        }
        else {
            return "#playAudio";
        }
    };

    this.playingDone = function() {
        this.setInteraction(this.interaction.nextInteraction());
    };

    this.setInteraction = function(interaction) {
        this.interaction = interaction;
        this.state = "init";
    };

    this.init(course, metadata);
};