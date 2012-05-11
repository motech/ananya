
var operators = [
       "airtel", "reliance", "tata", "idea", "bsnl", "vodafone"
    ];

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

var operator_counter = fetch_from_vars_as_int("operator_counter");
var user_counter = fetch_from_vars_as_int("user_counter");

if ((user_counter + thread) > 25000) {
    user_counter = 0;
    operator_counter = operator_counter + 1;
    if (operator_counter > 6) {
        operator_counter = 0;
    }
}

var user = user_counter + thread;
vars.put("user_counter", user_counter + thread_count);
vars.put("operator_counter", operator_counter);

var num = operator_counter + 1;

var callerId = "9999" + num + "" + user
var callId = "9" + new Date().valueOf();
var calledNumber = "550011"
vars.put("callId", callId);
vars.put("callerId", callerId);
vars.put("calledNumber", calledNumber);
vars.put("operator", operators[operator_counter]);
vars.put("thread", thread);
