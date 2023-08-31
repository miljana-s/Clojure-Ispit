(ns healthcenter.routes.home
  (:require
    [healthcenter.layout :as layout]
    [healthcenter.db.core :as db]
    [clojure.java.io :as io]
    [struct.core :as st]
    [healthcenter.middleware :as middleware]
    [ring.util.response]
    [ring.util.http-response :as response]))

;-------------------------- PAGE CONFIG -----------------------------------------
(defn home-page [request]
  (layout/render request "home.html"))

(defn patients-page [request]
  (layout/render
    request "patients/patients.html"
    {:patients (db/get-patients)}))

(defn createPat-page [{:keys [flash] :as request}]
  (layout/render
    request "patients/createPat.html"
    (select-keys flash [:name :surname :healthCardNumber :city :address :dateOfBirth :errors])))

(defn updatePat-page [{:keys [flash] :as request}]
  (layout/render
    request "patients/updatePat.html"
    (merge {:patients (db/get-patients)}
           (select-keys flash [:name :surname :healthCardNumber :city :address :dateOfBirth :errors]))))

(defn deletePat-page [{:keys [flash] :as request}]
  (layout/render
    request "patients/deletePat.html"
    (merge {:patients (db/get-patients)}
           (select-keys flash [:id :errors]))))

(defn treatments-page [request]
  (layout/render
    request "treatments/treatments.html"
    {:treatments (db/get-treatments)}))

(defn createTreat-page [{:keys [flash] :as request}]
  (layout/render
    request "treatments/createTreat.html"
    (select-keys flash [:treatmentName :regularPrice :loyalityPrice :errors])))

(defn updateTreat-page [{:keys [flash] :as request}]
  (layout/render
    request "treatments/updateTreat.html"
    (merge {:treatments (db/get-treatments)}
           (select-keys flash [:treatmentName :regularPrice :loyalityPrice :errors]))))

(defn deleteTreat-page [{:keys [flash] :as request}]
  (layout/render
    request "treatments/deleteTreat.html"
    (merge {:treatments (db/get-treatments)}
           (select-keys flash [:id :errors]))))

(defn appointments-page [request]
  (layout/render
    request "appointments/appointments.html"
    {:appointments (db/get-appointments)}))

(defn createApp-page [{:keys [flash] :as request}]
  (layout/render
    request "appointments/createApp.html"
    (merge {:patients (db/get-patients)}
           {:treatments (db/get-treatments)}
           (select-keys flash [:time :date :errors]))))

(defn updateApp-page [{:keys [flash] :as request}]
  (layout/render
    request "appointments/updateApp.html"
    (merge {:appointments (db/get-appointments)}
           {:patients (db/get-patients)}
           {:treatments (db/get-treatments)}
           (select-keys flash [:time :date :errors]))))

(defn deleteApp-page [request]
  (layout/render request "appointments/deleteApp.html"))

(defn completedApps-page [request]
  (layout/render request "appointments/completedApps.html"))

(defn doctorReport-page [request]
  (layout/render request "appointments/doctorReport.html"))

;-------------------------- SCHEMAS -----------------------------------------

;Patient
(def patient-schema
  [[:name
    st/required
    st/string]
   [:surname
    st/required
    st/string]
   [:healthCardNumber
    st/required
    {:message  "Card number must be positive number!"
     :validate (fn [num] (> (Integer/parseInt (re-find #"\A-?\d+" num)) 0))}
    {:message  "Card must have exactly five numbers!"
     :validate (fn [num] (= (count num) 5))}]
   [:city
    st/required
    st/string]
   [:address
    st/required
    st/string]
   [:dateOfBirth
    st/required]])

;Treatment
(def treatment-schema
  [[:treatmentName
    st/required
    st/string]
   [:regularPrice
    st/required
    {:message  "Price must be positive number!"
     :validate (fn [num] (> (Integer/parseInt (re-find #"\A-?\d+" num)) 0))}]
   [:loyalityPrice
    st/required
    {:message  "Price must be positive number!"
     :validate (fn [num] (> (Integer/parseInt (re-find #"\A-?\d+" num)) 0))}]])

;Appointment
(def appointment-schema
  [[:date
    st/required]
   [:time
    st/required]])


;-------------------------- VALIDATIONS -----------------------------------------

;Patient
(defn validate-patient [params]
  (first (st/validate params patient-schema)))

;Treatment
(defn validate-treatment [params]
  (first (st/validate params treatment-schema)))

;Appointment
(defn validate-appointment [params]
  (first (st/validate params appointment-schema)))

;-------------------------- METHODS -----------------------------------------

;Adding new patient
(defn add-patient! [{:keys [params]}]
  (if-let [errors (validate-patient params)]
    (-> (response/found "/createPatient")
        (assoc :flash (assoc params :errors errors)))
    (let [patient (db/get-patient-by-card params)]
      (if patient
        (-> (response/found "/createPatient")
            (assoc :flash (assoc params :errors {:healthCardNumber "Patient with that health card number already exists!!"})))
        (do
          (db/create-patient! params)
          (response/found "/patients"))))))

;Update patient
(defn update-patient! [{:keys [params]}]
  (if-let [errors (validate-patient params)]
    (-> (response/found "/updatePatient")
        (assoc :flash (assoc params :errors errors)))
    (let [patient (db/get-patient-by-card params)]
      (if patient
        (-> (response/found "/updatePatient")
            (assoc :flash (assoc params :errors {:healthCardNumber "Patient with that health card number already exists!!"})))
        (do
          (db/update-patient! params)
          (response/found "/patients"))))))

;Delete patient
(defn delete-patient! [{:keys [params]}]
  (let [params-no-token (dissoc params :__anti-forgery-token)
        patient (db/check-patient-treatments params-no-token)]
    (if patient
      (-> (response/found "/deletePatient")
          (assoc :flash (assoc params :errors {:id "You can't delete patient that has treatment record!!"})))
      (do
        (db/delete-patient! params)
        (response/found "/patients")))))

;Adding new treatment
(defn add-treatment! [{:keys [params]}]
  (if-let [errors (validate-treatment params)]
    (-> (response/found "/createTreatment")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/create-treatment! params)
      (response/found "/treatments"))))

;Update treatment
(defn update-treatment! [{:keys [params]}]
  (if-let [errors (validate-treatment params)]
    (-> (response/found "/updateTreatment")
        (assoc :flash (assoc params :errors errors)))
    (do
      (println params)
      (db/update-treatment! params)
      (response/found "/treatments"))))

;Delete treatment
(defn delete-treatment! [{:keys [params]}]
  (let [params-no-token (dissoc params :__anti-forgery-token)
        treatment (db/check-treatment-patients params-no-token)]
    (if treatment
      (-> (response/found "/deleteTreatment")
          (assoc :flash (assoc params :errors {:id "You can't delete treatment that has patient record!!"})))
      (do
        (println params)
        (db/delete-treatment! params)
        (response/found "/treatments")))))


; Adding new appointment
(defn add-appointment! [{:keys [params]}]
  (if-let [errors (validate-appointment params)]
    (-> (response/found "/createAppointment")
        (assoc :flash (assoc params :errors errors)))
    (let [appointment (db/get-appointment-by-time-and-date params)]
      (if appointment
        (-> (response/found "/createAppointment")
            (assoc :flash (assoc params :errors {:date "This time and date are already booked!!"})))
        (let [loyality (db/get-patient-loyality params)]
          (if (= loyality 1)
            (do
              (db/create-loyal-appointment! params)
              (response/found "/appointments"))
            (do
              (db/create-regular-appointment! params)
              (response/found "/appointments"))))))))

; Update appointment
(defn update-appointment! [{:keys [params]}]
  (if-let [errors (validate-appointment params)]
    (-> (response/found "/updateAppointment")
        (assoc :flash (assoc params :errors errors)))
    (let [appointment (db/get-appointment-by-time-and-date params)]
      (if appointment
        (-> (response/found "/updateAppointment")
            (assoc :flash (assoc params :errors {:date "This time and date are already booked!!"})))
        (let [loyality (db/get-patient-loyality params)]
          (if (= loyality 1)
            (do
              (db/update-loyal-appointment! params)
              (response/found "/appointments"))
            (do
              (db/update-regular-appointment! params)
              (response/found "/appointments"))))))))




;-------------------------- ROUTING -----------------------------------------

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}

   ; ---------- POST REQ ------------------------
   ["/addPatient" {:post add-patient!}]
   ["/editPatient" {:post update-patient!}]
   ["/removePatient" {:post delete-patient!}]

   ["/addTreatment" {:post add-treatment!}]
   ["/editTreatment" {:post update-treatment!}]
   ["/removeTreatment" {:post delete-treatment!}]

   ["/addAppointment" {:post add-appointment!}]
   ["/editAppointment" {:post update-appointment!}]

   ; ---------- GET REQ ------------------------
   ["/" {:get home-page}]

   ["/patients" {:get patients-page}]
   ["/createPatient" {:get createPat-page}]
   ["/updatePatient" {:get updatePat-page}]
   ["/deletePatient" {:get deletePat-page}]

   ["/treatments" {:get treatments-page}]
   ["/createTreatment" {:get createTreat-page}]
   ["/updateTreatment" {:get updateTreat-page}]
   ["/deleteTreatment" {:get deleteTreat-page}]

   ["/appointments" {:get appointments-page}]
   ["/createAppointment" {:get createApp-page}]
   ["/updateAppointment" {:get updateApp-page}]
   ["/deleteAppointment" {:get deleteApp-page}]

   ["/appointmentHistory" {:get createApp-page}]
   ["/addDoctorReport" {:get createApp-page}]
   ])

