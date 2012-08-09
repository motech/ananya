var addInput = function(form) {
var aInputs=form.getElementsByTagName('div');
for(var i=0; i<aInputs.length; i++) {
    if(aInputs[i].className=='hide') {
        aInputs[i].className='control-group';
        }
}
_clearPasswordFields(form);
}

var _clearPasswordFields = function(form) {
var node_list = form.getElementsByTagName('input');
for (var i = 0; i < node_list.length; i++) {
    var node = node_list[i];
    if (node.getAttribute('type') == 'password') {
        node.value = '';
         node.className += node.className ? ' required' : 'required';
    }
}
}
