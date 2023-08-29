------------------- PATIENTS --------------------------------

-- :name create-patient! :! :n
-- :doc creates a new patient record
INSERT INTO patients
(healthCardNumber, name, surname, city, address, dateOfBirth, loyalityMember)
VALUES (:healthCardNumber, :name, :surname, :city, :address, :dateOfBirth, :loyalityMember)

-- :name get-user-by-id :? :1
-- :doc get patient by id
SELECT * FROM patients
WHERE idPatient = :idPatient

-- :name update-patient! :! :n
-- :doc update existing patient with id
UPDATE patients
SET healthCardNumber = :healthCardNumber, name = :name,
    surname = :surname,city = :city, address = :address,
    dateOfBirth = :dateOfBirth, loyalityMember = :loyalityMember
WHERE idPatient = :idPatient

-- :name get-patients :? :*
-- :doc shows all patients with number of appointments
SELECT *, COUNT(appointments.idAppointment) AS 'numberOfApps' FROM `patients`
    JOIN appointments ON appointments.idPatient = patients.idPatient

------------------- TREATMENTS --------------------------------

-- :name create-treatment! :! :n
-- :doc creates a new treatment record
    INSERT INTO treatments
(treatmentName, regularPrice, loyalityPrice)
VALUES (:treatmentName, :regularPrice, :loyalityPrice)

-- :name get-treatment-by-id :? :1
-- :doc get treatment by id
SELECT * FROM treatments
WHERE idTreatment = :idTreatment

-- :name update-treatment! :! :n
-- :doc update existing treatment with id
UPDATE treatments
SET treatmentName = :treatmentName, regularPrice = :regularPrice,
    loyalityPrice = :loyalityPrice
WHERE idTreatment = :idTreatment

-- :name get-treatments :? :*
-- :doc shows all treatments
SELECT * from treatments

------------------- APPOINTMENTS --------------------------------

-- :name create-appointment! :! :n
-- :doc creates a new appointment record
    INSERT INTO appointments
(idPatient, idTreatment, date, time, price)
VALUES (:patient, :treatment, :date, :time, :price)

-- :name get-appointment-by-id :? :1
-- :doc selects appointment by id
SELECT * FROM appointments
WHERE idAppointment = :idAppointment

-- :name update-appointment! :! :n
-- :doc update existing appointment with id
UPDATE appointments
SET idPatient = :idPatient, idTreatment = :idTreatment,
    date = :date, time = :time, price = :price
WHERE idAppointment = :idAppointment

-- :name delete-appointment! :! :n
-- :doc delete appointment with id
DELETE FROM appointments
WHERE idAppointment = :idAppointment

-- :name get-appointments :? :*
-- :doc shows all appointments
SELECT * from appointments
                  JOIN treatments ON appointments.idTreatment = treatments.idTreatment
                  JOIN patients ON appointments.idPatient = patients.idPatient
ORDER BY idAppointment ASC

-- :name get-appointments-by-patient :? :*
-- :doc shows all appointments that patient with id had
SELECT *FROM appointments
                 JOIN treatments ON appointments.idTreatment = treatments.idTreatment
                 JOIN patients ON appointments.idPatient = patients.idPatient
WHERE appointments.idPatient = :idPatient
ORDER BY idAppointment ASC
