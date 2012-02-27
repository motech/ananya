var DataTransferList = function(metaData) {

    this.init = function(metaData) {
        this.counter = 0;
        this.drain();
        this.url = metaData['transfer.data.url'];
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
        return this.url;
    };

    this.setDataToPostUrlForDisconnect = function() {
        this.url = metaData['transfer.data.url.for.disconnect'];
    };

    this.init(metaData);
};

DataTransferList.TYPE_CC_STATE = "ccState";
DataTransferList.TYPE_CALL_DURATION = "callDuration";
