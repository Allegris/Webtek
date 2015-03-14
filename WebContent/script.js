//Run this function when we have loaded the HTML document
window.onload = function () {
    //This code is called when the body element has been loaded and the application starts

    //Request items from the server. The server expects no request body, so we set it to null
    sendRequest("GET", "rest/shop/items", null, function (itemsText) {
        //This code is called when the server has sent its data
        var items = JSON.parse(itemsText);
        addItemsToTable(items);
    });

    //Register an event listener for button clicks
    var updateButton = document.getElementById("update");
    addEventListener(updateButton, "click", function () {
        //Same as above, get the items from the server
        sendRequest("GET", "rest/shop/items", null, function (itemsText) {
            //This code is called when the server has sent its data
            var items = JSON.parse(itemsText);
            addItemsToTable(items);
        });
    });
    
    //Register an event listener for createButton clicks
    var createButton = document.getElementById("create");
    addEventListener(createButton, "click", function () {
    	var username = document.getElementById("username").value;
    	var password = document.getElementById("password").value;
    	var userRequest = {};
    	userRequest.username = username;
    	userRequest.password = password;
    	var stringOfJSON = JSON.stringify(userRequest);
    	sendRequest("GET", "rest/shop/createCustomer", stringOfJSON, function (itemsText) {
            //This code is called when the server has sent its data
            var items = JSON.parse(itemsText);
            addItemsToTable(items);
        sendRequest(httpMethod, url, body, responseHandler)
        });
    });
};

function addItemsToTable(items) {
    //Get the table body we we can add items to it
    var tableBody = document.getElementById("itemtablebody");
    //Remove all contents of the table body (if any exist)
    tableBody.innerHTML = "";

    //Loop through the items from the server
    for (var i = 0; i < items.length; i++) {
        var item = items[i];
        //Create a new line for this item
        var tr = document.createElement("tr");

        var nameCell = document.createElement("td");
        nameCell.textContent = item.name;
        tr.appendChild(nameCell);

        var priceCell = document.createElement("td");
        priceCell.textContent = item.price;
        tr.appendChild(priceCell);
        
        var descriptionCell = document.createElement("td");
        descriptionCell.textContent = item.description;
        tr.appendChild(descriptionCell);
        
        var stockCell = document.createElement("td");
        stockCell.textContent = item.stock;
        tr.appendChild(stockCell);
        
        var inCartCell = document.createElement("td");
        inCartCell.textContent = item.inCart;
        tr.appendChild(inCartCell);
        
        var idCell = document.createElement("td");
        idCell.textContent = item.id;
        tr.appendChild(idCell);

        tableBody.appendChild(tr);
    }
}


/////////////////////////////////////////////////////
// Code from slides
/////////////////////////////////////////////////////

/**
 * A function that can add event listeners in any browser
 */
function addEventListener(myNode, eventType, myHandlerFunc) {
    if (myNode.addEventListener)
        myNode.addEventListener(eventType, myHandlerFunc, false);
    else
        myNode.attachEvent("on" + eventType,
            function (event) {
                myHandlerFunc.call(myNode, event);
            });
}

var http;
if (!XMLHttpRequest)
    http = new ActiveXObject("Microsoft.XMLHTTP");
else
    http = new XMLHttpRequest();

function sendRequest(httpMethod, url, body, responseHandler) {
    http.open(httpMethod, url);
    if (httpMethod == "POST") {
        http.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
    }
    http.onreadystatechange = function () {
        if (http.readyState == 4 && http.status == 200) {
            responseHandler(http.responseText);
        }
    };
    http.send(body);
}

