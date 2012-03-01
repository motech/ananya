var DataTransferList = function() {

    this.init = function() {
        this.counter = 0;
        this.drain();
    };

    this.add = function(data, type) {
        var dataPacket = new Object();
        //header elements follow
        dataPacket["token"] = this.counter++;
        dataPacket["type"] = type;

        // data elements follow
        dataPacket["data"] = data;

        this.transferList[this.transferList.length] = dataPacket;
    };

    this.drain = function() {
        this.transferList = new Array();
    };

    this.anyData = function() {
        return this.transferList.length > 0;
    };

    this.dataToPost = function() {
        return Utility.stringify(this.transferList);
    };

    this.dataPostUrl = function() {
        return metaData['transfer.data.url'];
    };

    this.init();
};

DataTransferList.TYPE_CC_STATE = "ccState";
DataTransferList.TYPE_CALL_DURATION = "callDuration";