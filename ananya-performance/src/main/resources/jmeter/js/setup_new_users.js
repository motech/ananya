
var thread = ctx.getThreadNum();
var thread_count = ctx.getThreadGroup().getNumThreads();

function convertToInt(value) {
    value = parseInt(value);
    if (isNaN(value)) {
        value = 0;
    }
    return value;
}

function fetch_from_vars_as_int(key) {
    var value = vars.get(key);
    value = convertToInt(value);
    return value;
}

var new_user_counter = fetch_from_vars_as_int("new_user_counter");

var new_user = new_user_counter + thread;

new_user_counter = new_user_counter + thread_count;
vars.put("new_user_counter", new_user_counter);

vars.put("callerId", "9" + new_user);
vars.put("operator", "undefined");
