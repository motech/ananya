var Course = function () {
};

Course.buildLinks = function(node) {
    var nodesByLevel = new Array();

    var linkWithParentAndGroupNodesByLevel = function(node, parentNode, level, myIndexAmongSiblings, nodesByLevel) {
        //Grouping by level
        if(!nodesByLevel[level]) {
            nodesByLevel[level] = new Array();
        }

        var currentLength = nodesByLevel[level].length;
        nodesByLevel[level][currentLength] = node;

        //linking to parent
        node.parent = parentNode;
        node.positionIndex = myIndexAmongSiblings;

        for(var i = 0; i < node.children.length; i++) {
            linkWithParentAndGroupNodesByLevel(node.children[i], node, level+1, i, nodesByLevel);
        }
    };

    var linkNodesWithSiblingOnRight = function(nodesByLevel) {
        var levels = nodesByLevel.length;

        for(var thisLevel = 0; thisLevel < levels ; thisLevel++) {
            var nodesInThisLevel = nodesByLevel[thisLevel];
            var indexOfLastNode = nodesInThisLevel.length-1;
            for(var i=0; i<indexOfLastNode; i++) {
                nodesInThisLevel[i].siblingOnRight = nodesInThisLevel[i+1];
            }
            nodesInThisLevel[indexOfLastNode].siblingOnRight = nodesInThisLevel[indexOfLastNode];
        }
    };

    linkWithParentAndGroupNodesByLevel(node, node, 0, 0, nodesByLevel);
    linkNodesWithSiblingOnRight(nodesByLevel);
};
