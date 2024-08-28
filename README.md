# Amazon-Store-

<ins>Implementation Description</ins>
Our implementation of an interactive Amazon storefront consists of multiple queries that are used to fetch information from the user and return the information that the user is looking for. We separate the queries into the outlined functions of the storefront and design each one to retrieve the desired output. This Amazon storefront is user-friendly and clear, validating the user’s requests by displaying different messages. 

<ins>Query and Functionality Description</ins>
User Login:
User login fetches all of the information from Users according to the inputted ‘name’ and ‘password’. We define ‘currUser’ in this function, which we use throughout all of the system’s functions to dictate the current/authorized user logged in.

Admin:
‘Admin’ checks to see if the current user is an admin, and if so, the user can access operations available to admins only. We query the name from users to check if it is of type ‘admin’ and if yes, the user can change the product numberOfUnits or pricePerUnits or the Users name, password, latitude, or longitude. 

View Stores:
‘viewStores’ fetches the current user’s location to get the stores located within 30 miles from the user. We query the latitude and longitude from Users to do so and then query all the information from the desired store.

View Products: 
‘viewProducts’ queries all of the information from Product that is located in the store matching the storeID that the user inputs. It prints out all of the products in that store. 

Place Order:
‘placeOrder’ fetches the store location of the inputted storeID from the user and uses that information to check that the store is within 30 miles of the current user. We then run a query to get the maximum number from Orders, or the most recent order, to input the user’s new order. 

View Recent Orders:
‘viewRecentOrders’ queries all of the information from Orders in order of most recent to oldest, outputting the five most recent orders. 

Update Product:
‘updateProduct’ first queries the current user’s name to check if they are of type ‘manager’ and if so, prompts the user to enter the desired storeID, product name, new price, and number of units. Then, we check if the manager manages that specific store by running a query to fetch the managerID from Store and if so, update the Product table with the new criteria.

View Recent Updates:
‘viewRecentUpdates’ queries the five most recent product updates in order from newest to oldest and prints the result. 

View Popular Products:
‘viewPopularProducts’ queries the five most popular products by grouping the Orders by productName and ordering the groups according to orders. 

View Popular Customers:
‘viewPopularCustomers’ runs a query to get the five most popular customers by grouping Orders according to customerID in descending order.  

Place Product Supply Requests:
‘placeProductSupplyRequests’ queries the user name to check if they are of type ‘manager’ and if so, prompts them for a storeID, product name, quantity, and warehouseID. The inputted information is inserted into the table ‘ProductSupplyRequests’ and ‘Product’ is updated accordingly. 


