#!/bin/bash

# API endpoint
API_URL="http://localhost:8084/api/doctors"
CONTENT_TYPE="Content-Type: application/json"

echo "Adding Dr. R. Amin Hanan..."
curl -X POST -H "${CONTENT_TYPE}" -d '{
  "name": "R. Amin Hanan",
  "email": "dr.amin.hanan@lemicare.com",
  "phoneNumber": "+91-9876543201",
  "specialization": "Dermatology",
  "qualification": "MBBS., MD.(DVL), FFAM.",
  "licenseNumber": "TN-MED-AMN2025",
  "hospital": "Hanan Clinic",
  "address": "123 Anna Nagar",
  "city": "Chennai",
  "state": "Tamil Nadu",
  "country": "India",
  "zipCode": "600040",
  "isAvailable": true,
  "profileImage": "https://storage.googleapis.com/lemicare-app.appspot.com/doctors/dr_hanan.jpg",
  "active": true,
  "isActive": true,
  "experience": "25 years",
  "availableDays": ["Monday", "Wednesday", "Friday"],
  "availableTimeSlots": ["13:00-23:00", "14:00-17:00"],
  "password": "hanan@2025",
  "location": {
    "latitude": 13.0827,
    "longitude": 80.2707
  },
  "leaves": []
}' ${API_URL}
echo -e "\n"

echo "Adding Dr. Mohamed Basith..."
curl -X POST -H "${CONTENT_TYPE}" -d '{
  "name": "Mohamed Basith",
  "email": "dr.mohamed.basith@lemicare.com",
  "phoneNumber": "+91-9876543202",
  "specialization": "Anesthesiology",
  "qualification": "MBBS., MD., FHT.",
  "licenseNumber": "TN-MED-BAS2025",
  "hospital": "Hanan Clinic",
  "address": "123 Anna Nagar",
  "city": "Chennai",
  "state": "Tamil Nadu",
  "country": "India",
  "zipCode": "600040",
  "isAvailable": true,
  "profileImage": "https://storage.googleapis.com/lemicare-app.appspot.com/doctors/dr_basith.jpg",
  "active": true,
  "isActive": true,
  "experience": "20 years",
  "availableDays": ["Monday", "Tuesday", "Thursday", "Saturday"],
  "availableTimeSlots": ["13:00-23:00", "16:00-18:00"],
  "password": "basith@2025",
  "location": {
    "latitude": 13.0827,
    "longitude": 80.2707
  },
  "leaves": []
}' ${API_URL}
echo -e "\n"

echo "Adding Dr. U. SANDEEP..."
curl -X POST -H "${CONTENT_TYPE}" -d '{
  "name": "U. SANDEEP",
  "email": "dr.sandeep@lemicare.com",
  "phoneNumber": "+91-9876543203",
  "specialization": "Plastic Surgery",
  "qualification": "MS., DNB., M.Ch., DNB.",
  "licenseNumber": "TN-MED-SAN2025",
  "hospital": "Hanan Clinic",
  "address": "123 Anna Nagar",
  "city": "Chennai",
  "state": "Tamil Nadu",
  "country": "India",
  "zipCode": "600040",
  "isAvailable": true,
  "profileImage": "https://storage.googleapis.com/lemicare-app.appspot.com/doctors/dr_sandeep.jpg",
  "active": true,
  "isActive": true,
  "experience": "15 years",
  "availableDays": ["Tuesday", "Wednesday", "Friday"],
  "availableTimeSlots": ["13:00-23:00", "15:00-18:00"],
  "password": "sandeep@2025",
  "location": {
    "latitude": 13.0827,
    "longitude": 80.2707
  },
  "leaves": []
}' ${API_URL}
echo -e "\n"

echo "Adding Dr. Shruthi Janardhanan..."
curl -X POST -H "${CONTENT_TYPE}" -d '{
  "name": "Shruthi Janardhanan",
  "email": "dr.shruthi@lemicare.com",
  "phoneNumber": "+91-9876543204",
  "specialization": "Dermatology",
  "qualification": "MD., DNB., MRCP (UK, Dermatology)",
  "licenseNumber": "TN-MED-SHR2025",
  "hospital": "Hanan Clinic",
  "address": "123 Anna Nagar",
  "city": "Chennai",
  "state": "Tamil Nadu",
  "country": "India",
  "zipCode": "600040",
  "isAvailable": true,
  "profileImage": "https://storage.googleapis.com/lemicare-app.appspot.com/doctors/dr_shruthi.jpg",
  "active": true,
  "isActive": true,
  "experience": "12 years",
  "availableDays": ["Monday", "Thursday", "Saturday"],
  "availableTimeSlots": ["13:00-23:00", "14:00-16:00"],
  "password": "shruthi@2025",
  "location": {
    "latitude": 13.0827,
    "longitude": 80.2707
  },
  "leaves": []
}' ${API_URL}
echo -e "\n"

echo "All doctors added!"
