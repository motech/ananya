var CallDetailLogger = function(dataTransferList){
    this.init=function(dataTransferList){
        this.dataTransferList = dataTransferList;
    };

    this.start_call=function() {
        this.dataTransferList.add({ "callEvent": "CALL_START" ,"time" :  new Date().getTime()  }, DataTransferList.TYPE_CALL_DURATION);
    };

    this.registration_start=function() {
        this.dataTransferList.add({ "callEvent": "REGISTRATION_START" ,"time" :  new Date().getTime()  }, DataTransferList.TYPE_CALL_DURATION);
    };

    this.registration_end=function() {
        this.dataTransferList.add({ "callEvent": "REGISTRATION_END" ,"time" :  new Date().getTime()  }, DataTransferList.TYPE_CALL_DURATION);
    };

    this.cc_start=function() {
        this.dataTransferList.add({ "callEvent": "CERTIFICATECOURSE_START" ,"time" :  new Date().getTime()  }, DataTransferList.TYPE_CALL_DURATION);
        this.dataTransferList.add({ "callEvent": "CERTIFICATECOURSE_START" ,"ticall_detail_logger.jsme" :  new Date().getTime()  }, DataTransferList.TYPE_CALL_DURATION);
    };

    this.cc_end=function() {
        this.dataTransferList.add({ "callEvent": "CERTIFICATECOURSE_END" ,"time" :  new Date().getTime()  }, DataTransferList.TYPE_CALL_DURATION);
    };

    this.jobaid_start=function() {
        this.dataTransferList.add({ "callEvent": "JOBAID_START" ,"time" :  new Date().getTime()  }, DataTransferList.TYPE_CALL_DURATION);
    };

    this.jobaid_end=function() {
        this.dataTransferList.add({ "callEvent": "JOBAID_END" ,"time" :  new Date().getTime()  }, DataTransferList.TYPE_CALL_DURATION);
    };

    this.disconnect=function() {
        this.dataTransferList.add({ "callEvent": "DISCONNECT" ,"time" :  new Date().getTime()  }, DataTransferList.TYPE_CALL_DURATION);
    };

    this.init(dataTransferList);
}
