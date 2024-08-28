--Extra Credit: Performance Tuning

DROP INDEX IF EXISTS indexUsers;
DROP INDEX IF EXISTS indexStore;
DROP INDEX IF EXISTS indexProduct;
DROP INDEX IF EXISTS indexOrders;
DROP INDEX IF EXISTS indexProductUpdates;

-- Login
CREATE INDEX indexUsers ON Users (name, password);

-- Store lookup based on location
CREATE INDEX indexStore ON Store (latitude, longitude);

-- Product name search
CREATE INDEX indexProduct ON Product (productName);

-- Recent orders by user
CREATE INDEX indexOrders ON Orders (customerID, orderTime);

-- Recent product updates
CREATE INDEX indexProductUpdates ON ProductUpdates (updatedOn);
