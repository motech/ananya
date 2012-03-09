
var thread = ctx.getThreadNum();

var myToken = 0;
var callEvent = "CALL_START";

var tokenStr = {
    "token" :myToken,
    "type" : "callDuration",
    "data" : { "callEvent": callEvent ,"time" :  new Date().valueOf() }
};


var dataStr = {
    "token" : 2,
    "type" : "ccState",
    "data" :
    {
        "chapterIndex":"$chapIndex",
        "lessonOrQuestionIndex":null,
        "questionResponse":null,
        "result":null,
        "interactionKey":null,

        "contentId":"0cccd9b516233e4bb1c6c04fed2cc69f",
        "contentName":"CertificationCourse",
        "contentType": "Course",
        "courseItemState":"START",
        "contentData":null,
        "time":"2012-03-08T10:16:49.972Z",
        "certificateCourseId":"1"
    }
};

var dataToPost = [
    tokenStr
]

var callerId = "9999" + thread;
var callId = callerId + new Date().valueOf();

vars.put("callId", callId);
vars.put("callerId", callerId);
vars.put("dataToPost", dataToPost);
