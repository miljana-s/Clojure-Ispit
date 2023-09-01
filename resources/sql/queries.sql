------------------- PATIENTS --------------------------------

-- :name create-patient! :! :n
-- :doc creates a new patient record
INSERT INTO patients
(healthCardNumber, name, surname, city, address, dateOfBirth, loyalityMember)
VALUES (:healthCardNumber, :name, :surname, :city, :address, :dateOfBirth, :loyalityMember)

-- :name get-patient-by-id :? :1
-- :doc get patient by id
SELECT * FROM patients
WHERE idPatient = :idPatient

-- :name get-patient-by-card :? :1
-- :doc get patient by card
SELECT * FROM patients
WHERE healthCardNumber = :healthCardNumber AND idPatient != :idPatient

-- :name update-patient! :! :n
-- :doc update existing patient with id
UPDATE patients
SET healthCardNumber = :healthCardNumber, name = :name,
    surname = :surname,city = :city, address = :address,
    dateOfBirth = :dateOfBirth, loyalityMember = :loyalityMember
WHERE idPatient = :idPatient

-- :name get-patients :? :*
-- :doc shows all patients with number of appointments
SELECT *, COUNT(appointments.idAppointment) AS 'numberOfApps' FROM patients
    LEFT JOIN appointments ON appointments.idPatient1 = patients.idPatient
    GROUP BY patients.idPatient

-- :name check-patient-treatments :? :1
-- :doc returns a entity if patient had any treatments
SELECT * FROM patients
    JOIN appointments ON appointments.idPatient1 = patients.idPatient
    WHERE idPatient = :id
    GROUP BY patients.idPatient

-- :name delete-patient! :! :n
-- :doc delete patient with id
DELETE FROM patients
WHERE idPatient = :id

-- :name get-patient-loyality :? :1
-- :doc returns if patient is loyalityMember
SELECT loyalityMember FROM patients
    WHERE idPatient = :idPatient

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
SELECT *, COUNT(appointments.idAppointment) AS 'numberOfApps' FROM treatments
        LEFT JOIN appointments ON appointments.idTreatment1 = treatments.idTreatment
        GROUP BY treatments.idTreatment

-- :name check-treatment-patients :? :1
-- :doc returns a entity if treatment had any patients
SELECT * FROM treatments
                  JOIN appointments ON appointments.idTreatment1 = treatments.idTreatment
WHERE idTreatment = :id
GROUP BY treatments.idTreatment


-- :name delete-treatment! :! :n
-- :doc delete treatment with id
DELETE FROM treatments
WHERE idTreatment = :id

------------------- APPOINTMENTS --------------------------------

-- :name create-loyal-appointment! :! :n
-- :doc creates a new appointment record
    INSERT INTO appointments
(idPatient1, idTreatment1, date, time, price)
VALUES (:idPatient, :idTreatment, :date, :time, (
    SELECT loyalityPrice FROM treatments WHERE idTreatment = :idTreatment))

-- :name create-regular-appointment! :! :n
-- :doc creates a new appointment record
INSERT INTO appointments
(idPatient1, idTreatment1, date, time, price)
VALUES (:idPatient, :idTreatment, :date, :time, (
    SELECT regularPrice FROM treatments WHERE idTreatment = :idTreatment))

-- :name get-appointment-by-id :? :1
-- :doc selects appointment by id
SELECT * FROM appointments
WHERE idAppointment = :idAppointment

-- :name update-loyal-appointment! :! :n
-- :doc creates a new appointment record
UPDATE appointments
SET idPatient1 = :idPatient, idTreatment1 = :idTreatment,
    date = :date, time = :time, price = (
    SELECT loyalityPrice FROM treatments WHERE idTreatment = :idTreatment)
WHERE idAppointment = :idAppointment

-- :name update-regular-appointment! :! :n
-- :doc creates a new appointment record
UPDATE appointments
SET idPatient1 = :idPatient, idTreatment1 = :idTreatment,
    date = :date, time = :time, price = (
    SELECT regularPrice FROM treatments WHERE idTreatment = :idTreatment)
WHERE idAppointment = :idAppointment

-- :name delete-appointment! :! :n
-- :doc delete appointment with id
DELETE FROM appointments
WHERE idAppointment = :id

-- :name get-appointments :? :*
-- :doc shows all appointments (uncompleted)
SELECT * from appointments
                  JOIN treatments ON appointments.idTreatment1 = treatments.idTreatment
                  JOIN patients ON appointments.idPatient1 = patients.idPatient
WHERE doctorReport IS NULL
ORDER BY idAppointment ASC

-- :name get-appointments-by-patient :? :*
-- :doc shows all appointments that patient with id had
SELECT *FROM appointments
                 JOIN treatments ON appointments.idTreatment1 = treatments.idTreatment
                 JOIN patients ON appointments.idPatient1 = patients.idPatient
WHERE appointments.idPatient = :idPatient
ORDER BY idAppointment ASC

-- :name get-appointment-by-time-and-date :? :1
-- :doc selects appointment by time and date
SELECT * FROM appointments
WHERE time = :time AND date = :date

---------------- COMPLETED APPOINTMENTS -------------------------

-- :name get-completed-appointments :? :*
-- :doc shows all appointments (completed)
SELECT * from appointments
                  JOIN treatments ON appointments.idTreatment1 = treatments.idTreatment
                  JOIN patients ON appointments.idPatient1 = patients.idPatient
WHERE doctorReport IS NOT NULL
ORDER BY idAppointment ASC

-- :name complete-appointment :! :n
-- :doc complete appointment
UPDATE appointments
SET doctorReport = :doctorReport
WHERE idAppointment = :idAppointment

-- :name get-appointments-sum :? :*
-- :doc get total earingings from treatments
SELECT SUM(price) AS 'total' FROM appointments
WHERE doctorReport IS NOT NULL

