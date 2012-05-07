var tokens = [];
tokens[0] = {"token":0,"type":"callDuration","data":{"callEvent":"CALL_START","time":1331211295810}};
tokens[1] = {"token":1,"type":"callDuration","data":{"callEvent":"JOBAID_START","time":1331211297476}};
tokens[1] = {"token":2,"type":"callDuration","data":{"callEvent":"DISCONNECT","time":1331211652263}};

for(var i in contentIds){
    tokens.push(
            {
                "token": parseInt(i)+3,
                "type": "audioTracker",
                "data":
                    {
                        "contentId": contentIds[i],
                        "time": new Date().getTime(),
                        "duration": Math.floor((Math.random()*1000)+1)
                    }
            }
    );
}

var Utility = function() {
    this.stringify = function(value) {
        switch (typeof value) {
            case 'string':
                return this.quote(value);
            case 'number':
            case 'boolean':
            case 'null':
                return value;
            case 'object':
                if(!value) {
                    return value;
                }

                if(value instanceof Array) {
                    var result = "[";
                    for(var i = 0; i < value.length; ++i) {
                        result += this.stringify(value[i]) + ',';
                    }
                    result = this.removeCharAtEnd(result,',') + "]";

                    return result;
                } else {
                    return this.objectToString(value);
                }
        }
    }

    this.objectToString = function(object) {
        var result = "{";
        for(var key in object) {
            var value = this.stringify(object[key]);
            result += this.quote(key) + ':';
            result += value + ',';
        }
        result = this.removeCharAtEnd(result,',') + "}";

        return result;
    }

    this.quote = function(string) {
        return "\"" + string + "\"";
    }

    this.removeCharAtEnd = function(value, c) {
        if((value.length > 0) && (value.charAt(value.length - 1) == c)) {
            return value.substring(0, value.length - 1);
        }
    }

    this.format = function() {
        var formatted = arguments[0];
        var i = 0;
        for(var arg = 1; arg < arguments.length; ++arg) {
            var index = arg - 1;
            formatted = formatted.replace("{" + index + "}", arguments[arg]);
        }
        return formatted;
    };

    /*
     * https://developer.mozilla.org/en/Core_JavaScript_1.5_Reference:Global_Objects:Date
     * YaaY to Mozilla!
     */
    this.toISODateString = function(d) {
        function pad(n){return n<10 ? '0'+n : n}
        return d.getUTCFullYear()+'-'
            + pad(d.getUTCMonth()+1)+'-'
            + pad(d.getUTCDate())+'T'
            + pad(d.getUTCHours())+':'
            + pad(d.getUTCMinutes())+':'
            + pad(d.getUTCSeconds())+'Z'
    }
};

var utility = new Utility();

vars.put("data_to_post", utility.stringify(tokens));