-- Create Database
DROP DATABASE IF EXISTS InteriorDesignOffice;
CREATE DATABASE IF NOT EXISTS InteriorDesignOffice;
USE InteriorDesignOffice;

-- User Details Table (Base for both customers and employees)
CREATE TABLE IF NOT EXISTS UserDetails (
    USER_id INT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    bankAccount VARCHAR(50),
    phoneNumber VARCHAR(15),
    email VARCHAR(100) NOT NULL,
    preferredContactMethod VARCHAR(50)
);

CREATE TABLE Employee (
    Employee_id INT PRIMARY KEY, 
    employeeLevel INT CHECK(employeeLevel >= 0),
    employeeIncome DOUBLE CHECK(employeeIncome >= 0),
    designsHandled VARCHAR(50),
    employeeName VARCHAR(50) NOT NULL,
    MAX_MONTHLY_INCOME DOUBLE CHECK(
        MAX_MONTHLY_INCOME >= 0  
        AND MAX_MONTHLY_INCOME < 10000
    ),
    totalIncome DOUBLE CHECK(totalIncome >= 0),
    FOREIGN KEY (Employee_id) REFERENCES UserDetails(USER_id)
        ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE TABLE Price (
    price_id INT PRIMARY KEY,
    basePrice DOUBLE NOT NULL,
    discount DOUBLE NOT NULL,
    userID INT,
    FOREIGN KEY (userID) REFERENCES UserDetails(USER_id)
);

CREATE TABLE RegularDesignPrice (
    price_id INT PRIMARY KEY,
    standardRate DOUBLE NOT NULL,
    standardDeliveryTime INT NOT NULL,
    revisionsAllowed INT NOT NULL,
    FOREIGN KEY (price_id) REFERENCES Price(price_id)
);

CREATE TABLE CreativeDesignPrice (
    price_id INT PRIMARY KEY,
    premiumSupport BOOLEAN NOT NULL,
    extraFeatures DOUBLE NOT NULL,
    customFeaturesCost DOUBLE NOT NULL,
    FOREIGN KEY (price_id) REFERENCES Price(price_id)
);

CREATE TABLE Design (
    design_id VARCHAR(255) PRIMARY KEY,
    designName VARCHAR(255) NOT NULL,
    typeid VARCHAR(10),
    designCounter INT,
    designType VARCHAR(50),
    features TEXT,
    des_cription TEXT,
    creationDate DATE NOT NULL,
    lastUpdatedDate DATE NOT NULL,
    cost DOUBLE NOT NULL,
    userID INT,
    priceID INT,
    FOREIGN KEY (userID) REFERENCES UserDetails(USER_id),
    FOREIGN KEY (priceID) REFERENCES Price(price_id)
);

CREATE TABLE RegularServiceDesign (
    design_id VARCHAR(255) PRIMARY KEY,
    serviceDetails TEXT NOT NULL,
    numberOfOrder INT NOT NULL,
    FOREIGN KEY (design_id) REFERENCES Design(design_id)
);

CREATE TABLE DistinctiveServiceDesign (
    design_id VARCHAR(255) PRIMARY KEY,
    distinctiveFeatures TEXT NOT NULL,
    clientFeedback TEXT,
    FOREIGN KEY (design_id) REFERENCES Design(design_id)
);

CREATE TABLE IF NOT EXISTS Customer (
    Customer_id INT PRIMARY KEY,
    membershipLevel VARCHAR(50),
    usageCount INT,
    choice INT,
    FOREIGN KEY (Customer_id) REFERENCES UserDetails(USER_id)
);

INSERT INTO UserDetails (USER_id, username, PASSWORD, phoneNumber, email, bankAccount, preferredContactMethod) VALUES
(101, 'mohammed', 'pass555', '0556666666', 'mohammed@gmail.com', '222333', 'email'),
(102, 'reem', 'pass777', '0557777777', 'reem@gmail.com', '888999', 'phone');
INSERT INTO Customer (Customer_id, membershipLevel, usageCount, choice) VALUES
(101, 'Gold', 5, 1),
(102, 'Silver', 3, 2);
INSERT INTO UserDetails (USER_id, username, PASSWORD, phoneNumber, email, bankAccount, preferredContactMethod) VALUES
(11, 'Ahmed', 'pass123', '0551111111', 'ahmed@gmail.com', '123456', 'email'),
(44, 'Sarah', 'pass456', '0552222222', 'sarah@gmail.com', '654321', 'phone'),
(89, 'Majed', 'pass789', '0553333333', 'Majed@gmail.com', '987654', 'email'),
(77, 'Nada', 'pass000', '0554444444', 'Nada@gmail.com', '456789', 'phone');
INSERT INTO Employee (Employee_id, employeeLevel, employeeIncome, designsHandled, employeeName, MAX_MONTHLY_INCOME, totalIncome) VALUES
(11, 5, 5000.0, 'D001', 'Ahmed', 9000.0, 5000.0),
(44, 8, 6000.0, 'D002', 'Sarah', 9500.0, 6000.0),
(89, 3, 4000.0, 'D001', 'Majed', 7000.0, 4000.0),
(77, 7, 7000.0, 'D002', 'Nada', 8500.0, 7000.0);

INSERT INTO Price (price_id, basePrice, discount, userID) VALUES
(301, 5000, 10, 101),
(302, 8000, 15, 102);
INSERT INTO RegularDesignPrice (price_id, standardRate, standardDeliveryTime, revisionsAllowed) VALUES
(301, 5200, 24, 2);
INSERT INTO CreativeDesignPrice (price_id, premiumSupport, extraFeatures, customFeaturesCost) VALUES
(302, TRUE, 3, 1500);
INSERT INTO Design (design_id, designName, typeid, designCounter, designType, features, des_cription, creationDate, lastUpdatedDate, cost, userID, priceID) VALUES
('D001', 'Classic Villa', 'T01', 2, 'Classic Design', 'Garden, Garage', 'Classic villa with garden and garage.', '2025-01-10', '2025-03-01', 9000, 101, 301),
('D002', 'Modern Apartment', 'T02', 1, 'Modern Design', 'Smart Home, Balcony', 'Modern apartment with smart system.', '2025-02-15', '2025-03-05', 7000, 102, 302);
INSERT INTO DistinctiveServiceDesign (design_id, distinctiveFeatures, clientFeedback) VALUES
('D001', 'Premium Materials, Advanced Security', 'Perfect work, highly recommended!');
INSERT INTO RegularServiceDesign (design_id, serviceDetails, numberOfOrder) VALUES
('D002', 'Standard Package with 2 revisions', 3);








