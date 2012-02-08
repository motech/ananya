var Utility = function() {
    this.stringify = function(value) {
        switch (typeof value) {
            case 'string':
                return this.quote(value);
            case 'number':
            case 'boolean':
            case 'null':
                return String(value);
            case 'object':
                if(!value) {
                    return String(value);
                }

                if(value instanceof Array) {
                    var result = "[";
                    for(var i = 0; i < value.length; ++i) {
                        var ele = value[i];
                        result += this.stringify(ele) + ',';
                    }

                    result = result.substring(0, result.length - 1) + "]";
                    //result = this.removeCharAtEnd(result,',') + "]";
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
        result = result.substring(0, result.length - 1) + "}";
        //result = this.removeCharAtEnd(result,',') + "}";

        return result;
    }

    this.quote = function(string) {
        return "\"" + string + "\"";
    }

//    this.removeCharAtEnd = function(value, char) {
////        value = "" + value;
////        if((value.length > 0) && (value.charAt(value.length - 1) == char)) {
////            return value.substring(0, value.length - 1);
////        }
//    }
};

var Utility = new Utility();