# Doctor API Testing Guide

This document provides curl commands to test all doctor management API endpoints in the OPD Management Service.

## Base URL

All API calls use the base URL: `http://localhost:8084/api/doctors`

## Table of Contents

- [Get All Doctors](#get-all-doctors)
- [Get Doctor by ID](#get-doctor-by-id)
- [Create New Doctor](#create-new-doctor)
- [Update Doctor](#update-doctor)
- [Delete Doctor](#delete-doctor)
- [Doctor Login](#doctor-login)
- [Get Doctors by Specialization](#get-doctors-by-specialization) 
- [Get Available Doctors](#get-available-doctors)
- [Get Available Doctors by Specialization](#get-available-doctors-by-specialization)
- [Update Doctor Availability](#update-doctor-availability)
- [Add Doctor Leave](#add-doctor-leave)
- [Cancel Doctor Leave](#cancel-doctor-leave)
- [Get Doctor Leaves](#get-doctor-leaves)
- [Update Doctor Location](#update-doctor-location)

## Get All Doctors

Retrieves a list of all doctors in the system.

```bash
curl -X GET http://localhost:8084/api/doctors
```

## Get Doctor by ID

Retrieves a specific doctor by their ID.

```bash
curl -X GET http://localhost:8084/api/doctors/{doctorId}
```

Example:
```bash
curl -X GET http://localhost:8084/api/doctors/8a49c405-c865-40b2-8f07-b83a856af763
```

## Create New Doctor

Creates a new doctor record.

```bash
curl -X POST http://localhost:8084/api/doctors \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. New Doctor",
    "email": "new-doctor@example.com",
    "password": "password123",
    "specialization": "Neurology",
    "qualification": "MD, PhD",
    "experience": "15 years",
    "phoneNumber": "9876543210",
    "address": "456 New Street",
    "availableDays": ["Tuesday", "Thursday"],
    "availableTimeSlots": ["10:00-11:00", "15:00-16:00"]
  }'
```

## Update Doctor

Updates an existing doctor's information.

```bash
curl -X PUT http://localhost:8084/api/doctors/{doctorId} \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Updated Doctor",
    "email": "updated-doctor@example.com",
    "specialization": "Neurosurgery",
    "qualification": "MD, PhD, FRCS",
    "experience": "16 years",
    "phoneNumber": "9876543210",
    "address": "789 Updated Street",
    "availableDays": ["Tuesday", "Thursday", "Friday"],
    "availableTimeSlots": ["10:00-11:00", "15:00-16:00", "17:00-18:00"]
  }'
```

Example:
```bash
curl -X PUT http://localhost:8084/api/doctors/8a49c405-c865-40b2-8f07-b83a856af763 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Updated Doctor",
    "email": "new-doctor@example.com",
    "specialization": "Neurosurgery",
    "qualification": "MD, PhD, FRCS",
    "experience": "16 years",
    "phoneNumber": "9876543210",
    "address": "789 Updated Street",
    "availableDays": ["Tuesday", "Thursday", "Friday"],
    "availableTimeSlots": ["10:00-11:00", "15:00-16:00", "17:00-18:00"]
  }'
```

## Delete Doctor

Deletes a doctor by ID.

```bash
curl -X DELETE http://localhost:8084/api/doctors/{doctorId}
```

Example:
```bash
curl -X DELETE http://localhost:8084/api/doctors/8a49c405-c865-40b2-8f07-b83a856af763
```

## Doctor Login

Authenticates a doctor with email and password.

```bash
curl -X POST http://localhost:8084/api/doctors/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "doctor@example.com",
    "password": "password123"
  }'
```

Example:
```bash
curl -X POST http://localhost:8084/api/doctors/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "new-doctor@example.com",
    "password": "password123"
  }'
```

## Get Doctors by Specialization

Retrieves all doctors with a specific specialization.

```bash
curl -X GET http://localhost:8084/api/doctors/specialization/{specialization}
```

Example:
```bash
curl -X GET http://localhost:8084/api/doctors/specialization/Neurosurgery
```

## Get Available Doctors

Retrieves all doctors who are currently available.

```bash
curl -X GET http://localhost:8084/api/doctors/available
```

## Get Available Doctors by Specialization

Retrieves all available doctors with a specific specialization.

```bash
curl -X GET http://localhost:8084/api/doctors/available/{specialization}
```

Example:
```bash
curl -X GET http://localhost:8084/api/doctors/available/Neurosurgery
```

## Update Doctor Availability

Updates a doctor's availability status.

```bash
curl -X PATCH "http://localhost:8084/api/doctors/{doctorId}/availability?isAvailable=true|false"
```

Example:
```bash
curl -X PATCH "http://localhost:8084/api/doctors/8a49c405-c865-40b2-8f07-b83a856af763/availability?isAvailable=true"
```

## Add Doctor Leave

Adds a leave record for a doctor.

```bash
curl -X POST "http://localhost:8084/api/doctors/{doctorId}/leave?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&reason=ReasonText"
```

Example:
```bash
curl -X POST "http://localhost:8084/api/doctors/8a49c405-c865-40b2-8f07-b83a856af763/leave?startDate=2025-07-10&endDate=2025-07-15&reason=Vacation"
```

## Cancel Doctor Leave

Cancels a previously requested leave.

```bash
curl -X DELETE "http://localhost:8084/api/doctors/{doctorId}/leave/{leaveId}"
```

Example:
```bash
curl -X DELETE "http://localhost:8084/api/doctors/8a49c405-c865-40b2-8f07-b83a856af763/leave/leave-id-123"
```

## Get Doctor Leaves

Retrieves all leaves for a specific doctor.

```bash
curl -X GET "http://localhost:8084/api/doctors/{doctorId}/leaves"
```

Example:
```bash
curl -X GET "http://localhost:8084/api/doctors/8a49c405-c865-40b2-8f07-b83a856af763/leaves"
```

## Update Doctor Location

Updates a doctor's GPS location coordinates.

```bash
curl -X PATCH "http://localhost:8084/api/doctors/{doctorId}/location?latitude=XX.XXXX&longitude=YY.YYYY"
```

Example:
```bash
curl -X PATCH "http://localhost:8084/api/doctors/8a49c405-c865-40b2-8f07-b83a856af763/location?latitude=37.7749&longitude=-122.4194"
```

## Response Format

All API responses follow this format:

```json
{
  "success": true|false,
  "message": "Description of the result",
  "data": {}, // The actual response data
  "error": null // Error message if success is false
}
```
