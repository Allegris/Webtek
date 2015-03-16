var customerID =-1;
var arrayOfItems = [];
var totalPrice = 0;

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
    function updateList() {
        //Same as above, get the items from the server
        sendRequest("GET", "rest/shop/items", null, function (itemsText) {
            //This code is called when the server has sent its data
            var items = JSON.parse(itemsText);
            addItemsToTable(items);
        });
    }
    
   
    
    //Register an event listener for createButton clicks
    var createButton = document.getElementById("create");
    addEventListener(createButton, "click", function () {
    	var username = document.getElementById("username").value;
    	var password = document.getElementById("password").value;
    	var userRequest = {};
    	userRequest.username = username;
    	userRequest.password = password;
    	var stringOfJSON = JSON.stringify(userRequest);
    	sendRequest("POST", "rest/shop/createCustomer", stringOfJSON, function (response) {
            //This code is called when the server has sent its data
    	    var responseDiv = document.getElementById("response");
    	    responseDiv.innerHTML = response;
        });
    });
    
    var loginButton = document.getElementById("login");
    addEventListener(loginButton, "click", function () {
    	var username = document.getElementById("username").value;
    	var password = document.getElementById("password").value;
    	var userRequest = {};
    	userRequest.username = username;
    	userRequest.password = password;
    	var stringOfJSON = JSON.stringify(userRequest);
    	sendRequest("POST", "rest/shop/login", stringOfJSON, function (response) {
            //This code is called when the server has sent its data
    	    var responseDiv = document.getElementById("response");
    	    if (response == "Error"){
    	    	responseDiv.innerHTML = "Error";
    	    }else{
    	    	customerID = response;
    	    	responseDiv.innerHTML = response;
    	    }
    	    
        });
    });
    
    
    var buyButton = document.getElementById("buy");
    addEventListener(buyButton, "click", function () {
   
    	var JSONObj = {customerID: customerID, items: arrayOfItems};
    	var stringOfJSON = JSON.stringify(JSONObj);
    	sendRequest("POST", "rest/shop/sellItems", stringOfJSON, function (response) {
            //This code is called when the server has sent its data
    	    var responseDiv = document.getElementById("buyFeedback");
    	    if (response == "Success"){
    	    	responseDiv.innerHTML = "Your items were bought!";
    	    	totalPrice = 0;
    	    	arrayOfItems = [];
    	    	
    	    	var cartContent = document.getElementById("cartContent");
    			cartContent.innerHTML = "";
    			var total = document.getElementById("totalPrice");
    			total.innerHTML = totalPrice;
    			updateList();
    	    }else{
    	    	responseDiv.innerHTML = "Something went wrong. Make sure you are logged in!";

    	   
    	    }
    	    
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
        descriptionCell.innerHTML = item.description;
        tr.appendChild(descriptionCell);
        
        var stockCell = document.createElement("td");
        stockCell.textContent = item.stock;
        tr.appendChild(stockCell);
                
        var buttonCell = document.createElement("td");
        var button = document.createElement("button");
        addEventListener(button, "click", (function(id, price, stock, name) {
            return function() {
                addToCart(id, price, stock, name);
             };
         })(item.id, item.price, item.stock, item.name));
        button.innerHTML = "Add to cart";
        buttonCell.appendChild(button);
        tr.appendChild(buttonCell);
        
        var idCell = document.createElement("td");
        idCell.textContent = item.id;
        tr.appendChild(idCell);

        
        tableBody.appendChild(tr);
    }
}


function addToCart(id, price, stock, name){
	if (stock > 0){
		var cartContent = document.getElementById("cartContent");
		cartContent.innerHTML = cartContent.innerHTML + name + ", ";
		arrayOfItems.push(id);
		totalPrice += price;
		var total = document.getElementById("totalPrice");
		total.innerHTML = totalPrice;
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
    http.send("json="+body);
    http.onreadystatechange = function () {
        if (http.readyState == 4 && http.status == 200) {
            responseHandler(http.responseText);
        }
    };
    
}

