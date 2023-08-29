(ns healthcenter.routes.home
  (:require
   [healthcenter.layout :as layout]
   [healthcenter.db.core :as db]
   [clojure.java.io :as io]
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

(defn createPat-page [request]
  (layout/render request "patients/createPat.html"))

(defn updatePat-page [request]
  (layout/render request "patients/updatePat.html"))

(defn deletePat-page [request]
  (layout/render request "patients/deletePat.html"))

(defn treatments-page [request]
  (layout/render request "treatments/treatments.html"))

(defn createTreat-page [request]
  (layout/render request "treatments/createTreat.html"))

(defn updateTreat-page [request]
  (layout/render request "treatments/updateTreat.html"))

(defn deleteTreat-page [request]
  (layout/render request "treatments/deleteTreat.html"))

(defn appointments-page [request]
  (layout/render request "appointments/appointments.html"))

(defn createApp-page [request]
  (layout/render request "appointments/createApp.html"))

(defn updateApp-page [request]
  (layout/render request "appointments/updateApp.html"))

(defn deleteApp-page [request]
  (layout/render request "appointments/deleteApp.html"))

(defn completedApps-page [request]
  (layout/render request "appointments/completedApps.html"))

(defn doctorReport-page [request]
  (layout/render request "appointments/doctorReport.html"))

;-------------------------- SCHEMAS -----------------------------------------




;-------------------------- VALIDATIONS -----------------------------------------


;-------------------------- METHODS -----------------------------------------



;-------------------------- ROUTING -----------------------------------------

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}

   ; ---------- POST REQ ------------------------


   ; ---------- GET REQ ------------------------
   ["/" {:get home-page}]

   ["/patients" {:get patients-page}]
   ["/createPatient" {:get createPat-page}]
   ["/updatePatient" {:get updateApp-page}]
   ["/deletePatient" {:get deleteApp-page}]

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

