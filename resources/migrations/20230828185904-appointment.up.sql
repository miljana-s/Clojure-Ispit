CREATE TABLE appointments
(idAppointment INTEGER PRIMARY KEY AUTO_INCREMENT,
 idPatient INT,
 idTreatment INT,
 date VARCHAR(30),
 time VARCHAR(30),
 price DECIMAL,
 doctorReport VARCHAR(500),
 FOREIGN KEY (idPatient) REFERENCES patients(idPatient),
 FOREIGN KEY (idTreatment) REFERENCES treatments(idTreatment)
);