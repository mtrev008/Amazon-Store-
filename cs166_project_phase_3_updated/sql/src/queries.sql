SELECT * FROM USERS WHERE name = 'maya' AND password = 'nanma'; --user login

SELECT latitude, longitude FROM Users WHERE userID = 1; --fetch user location

SELECT * FROM Store; --get all stores

SELECT * FROM Product WHERE storeID = 1; --view products

SELECT latitude, longitude FROM Store WHERE storeID = 1; --fetch store location

SELECT MAX(orderNumber) FROM Orders; --max order number

SELECT * FROM Orders WHERE customerID = 1 ORDER BY orderTime DESC LIMIT 5; --get 5 most recent orders

SELECT name FROM Users WHERE userID = 1 AND type = 'manager'; --get name of manager

SELECT managerID FROM Store WHERE storeID = 1; --get managerID from store

SELECT * FROM ProductUpdates ORDER BY updatedOn DESC LIMIT 5; --get 5 most recent product updates

SELECT productName, COUNT(*) as orders FROM Orders GROUP BY productName ORDER BY orders DESC LIMIT 5; --get 5 most popular products

SELECT customerID, COUNT(*) as orders FROM Orders GROUP BY customerID ORDER BY orders DESC LIMIT 5; --get 5 most popular customers



