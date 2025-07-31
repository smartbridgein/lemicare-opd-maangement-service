# Service Management API Documentation

This document provides the API endpoints and curl commands for the Service Management functionality of the OPD Management System.

## Base URL

```
http://localhost:8084/api/services
```

## Endpoints

### 1. Create a Service

**Endpoint:** `POST /api/services`

**Request:**
```bash
curl -X POST http://localhost:8084/api/services \
  -H "Content-Type: application/json" \
  -d '{
    "name": "General Consultation", 
    "description": "Regular consultation with general physician", 
    "group": "CONSULTATION", 
    "rate": 500.0
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Service created successfully",
  "data": {
    "id": "5354cc42-43ec-4ece-b7ee-3d7da29bc9e3",
    "name": "General Consultation",
    "description": "Regular consultation with general physician",
    "group": "CONSULTATION",
    "rate": 500.0,
    "active": true,
    "createdAt": "2025-07-12T08:12:24.819077",
    "updatedAt": "2025-07-12T08:12:24.819099"
  }
}
```

### 2. Get All Services

**Endpoint:** `GET /api/services`

**Request:**
```bash
curl -X GET http://localhost:8084/api/services
```

**Response:**
```json
{
  "success": true,
  "message": "Services retrieved successfully",
  "data": [
    {
      "id": "5354cc42-43ec-4ece-b7ee-3d7da29bc9e3",
      "name": "General Consultation",
      "description": "Regular consultation with general physician",
      "group": "CONSULTATION",
      "rate": 500.0,
      "active": true,
      "createdAt": "2025-07-12T08:12:24.819",
      "updatedAt": "2025-07-12T08:12:24.819"
    },
    // Additional services...
  ]
}
```

### 3. Get Services by Group

**Endpoint:** `GET /api/services/group/{groupName}`

**Request:**
```bash
curl -X GET http://localhost:8084/api/services/group/CONSULTATION
```

**Response:**
```json
{
  "success": true,
  "message": "Services retrieved successfully for group: CONSULTATION",
  "data": [
    {
      "id": "5354cc42-43ec-4ece-b7ee-3d7da29bc9e3",
      "name": "General Consultation",
      "description": "Regular consultation with general physician",
      "group": "CONSULTATION",
      "rate": 500.0,
      "active": true,
      "createdAt": "2025-07-12T08:12:24.819",
      "updatedAt": "2025-07-12T08:12:24.819"
    }
  ]
}
```

### 4. Get a Service by ID

**Endpoint:** `GET /api/services/{serviceId}`

**Request:**
```bash
curl -X GET http://localhost:8084/api/services/5354cc42-43ec-4ece-b7ee-3d7da29bc9e3
```

**Response:**
```json
{
  "success": true,
  "message": "Service retrieved successfully",
  "data": {
    "id": "5354cc42-43ec-4ece-b7ee-3d7da29bc9e3",
    "name": "General Consultation",
    "description": "Regular consultation with general physician",
    "group": "CONSULTATION",
    "rate": 500.0,
    "active": true,
    "createdAt": "2025-07-12T08:12:24.819",
    "updatedAt": "2025-07-12T08:12:24.819"
  }
}
```

### 5. Update a Service

**Endpoint:** `PUT /api/services/{serviceId}`

**Request:**
```bash
curl -X PUT http://localhost:8084/api/services/5354cc42-43ec-4ece-b7ee-3d7da29bc9e3 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Consultation", 
    "description": "Updated consultation description", 
    "group": "CONSULTATION", 
    "rate": 600.0
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "Service updated successfully",
  "data": {
    "id": "5354cc42-43ec-4ece-b7ee-3d7da29bc9e3",
    "name": "Updated Consultation",
    "description": "Updated consultation description",
    "group": "CONSULTATION",
    "rate": 600.0,
    "active": true,
    "createdAt": "2025-07-12T08:12:24.819",
    "updatedAt": "2025-07-12T08:14:41.375584"
  }
}
```

### 6. Update Service Status (Active/Inactive)

**Endpoint:** `PATCH /api/services/{serviceId}/status`

**Request (Set to Active):**
```bash
curl -X PATCH http://localhost:8084/api/services/5354cc42-43ec-4ece-b7ee-3d7da29bc9e3/status \
  -H "Content-Type: application/json" \
  -d '{"active": true}'
```

**Request (Set to Inactive):**
```bash
curl -X PATCH http://localhost:8084/api/services/5354cc42-43ec-4ece-b7ee-3d7da29bc9e3/status \
  -H "Content-Type: application/json" \
  -d '{"active": false}'
```

**Response:**
```json
{
  "success": true,
  "message": "Service status updated successfully to active",
  "data": {
    "id": "5354cc42-43ec-4ece-b7ee-3d7da29bc9e3",
    "name": "Updated Consultation",
    "description": "Updated consultation description",
    "group": "CONSULTATION",
    "rate": 600.0,
    "active": true,
    "createdAt": "2025-07-12T08:12:24.819",
    "updatedAt": "2025-07-12T08:15:12.64341"
  }
}
```

### 7. Search Services by Name

**Endpoint:** `GET /api/services/search?name={searchTerm}`

**Request:**
```bash
curl -X GET "http://localhost:8084/api/services/search?name=Consultation"
```

**Response:**
```json
{
  "success": true,
  "message": "Service search completed",
  "data": [
    {
      "id": "5354cc42-43ec-4ece-b7ee-3d7da29bc9e3",
      "name": "Updated Consultation",
      "description": "Updated consultation description",
      "group": "CONSULTATION",
      "rate": 600.0,
      "active": true,
      "createdAt": "2025-07-12T08:12:24.819",
      "updatedAt": "2025-07-12T08:15:12.643"
    }
  ]
}
```

### 8. Delete a Service

**Endpoint:** `DELETE /api/services/{serviceId}`

**Request:**
```bash
curl -X DELETE http://localhost:8084/api/services/427a7798-1bbc-4947-97a3-f2eadb58adb4
```

**Response:**
```json
{
  "success": true,
  "message": "Service deleted successfully",
  "data": "Service with ID 427a7798-1bbc-4947-97a3-f2eadb58adb4 was deleted"
}
```

## Service Model

```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "group": "string",
  "rate": "number",
  "active": "boolean",
  "createdAt": "timestamp",
  "updatedAt": "timestamp"
}
```

## Group Types
Common group types include:
- CONSULTATION
- OPD
- PACKAGE

## Integration Notes
This API can be integrated with:
- Frontend service creation modal forms
- Cash memo forms that need to select services
- Invoicing systems

## Testing with pretty-printed output
Add `| json_pp` to the end of any curl command to get pretty-printed JSON output.
