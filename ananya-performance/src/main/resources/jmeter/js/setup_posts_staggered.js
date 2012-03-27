
function add(start, end, counter)
{
    i=1;
    while(start != end+1) {
        if(vars.get("token_" + start) != null) {
            vars.put(counter + i, vars.get("token_" +start));
        }
        i++;
        start++;
    }
}

add(1, 56, "loopPost1_");
add(57, 95, "loopPost2_");
add(96, 174, "loopPost3_");

vars.put("dataForDisconnect", vars.get("token_175"));
