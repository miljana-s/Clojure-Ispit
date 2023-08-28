CREATE TABLE patients
(idPatient INTEGER PRIMARY KEY AUTO_INCREMENT,
 healthCardNumber VARCHAR(50),
 name VARCHAR(30),
 surname VARCHAR(30),
 city VARCHAR(30),
 address VARCHAR(100),
 dateOfBirth VARCHAR(30),
 loyalityMember SMALLINT
);