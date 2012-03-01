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

};

var Utility = new Utility();