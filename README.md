# OPD Management Service

This service provides APIs for managing outpatient department (OPD) operations, including patient management, appointments, and billing.

## OPD Billing APIs

The OPD Management Service provides comprehensive billing APIs for various financial transactions including cash memos, invoices, receipts, advances, credit notes, and refunds.

### Cash Memo API

#### Create Cash Memo
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 500,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH"
  }' \
  http://localhost:8085/api/billing/cash-memo
```

**Response:**
```json
{
  "success": true,
  "message": "Cash memo created successfully",
  "data": {
    "id": "93630985-32a3-4d18-b3fa-cc5cf9d8ca8f",
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 500.0,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH",
    "billId": "CM-202506268C5291"
  }
}
```

#### Get All Cash Memos
```bash
curl -X GET http://localhost:8085/api/billing/cash-memo
```

#### Get Cash Memo by ID
```bash
curl -X GET http://localhost:8085/api/billing/cash-memo/{id}
```

**Response:**
```json
{
  "success": true,
  "message": "Cash memo retrieved successfully",
  "data": {
    "id": "93630985-32a3-4d18-b3fa-cc5cf9d8ca8f",
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 500.0,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH",
    "createdDate": "2025-06-26",
    "billId": "CM-202506268C5291"
  }
}
```

#### Get Cash Memos by Patient ID
```bash
curl -X GET http://localhost:8085/api/billing/cash-memo/patient/{patientId}
```

**Response:**
```json
{
  "success": true,
  "message": "Cash memos retrieved successfully",
  "data": [
    {
      "id": "93630985-32a3-4d18-b3fa-cc5cf9d8ca8f",
      "patientId": "patient123",
      "patientName": "John Doe",
      "date": "2025-06-26",
      "amount": 500.0,
      "createdBy": "Dr. Smith",
      "modeOfPayment": "CASH",
      "createdDate": "2025-06-26",
      "billId": "CM-202506268C5291"
    }
  ]
}
```

#### Update Cash Memo
```bash
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 600,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH"
  }' \
  http://localhost:8085/api/billing/cash-memo/{id}
```

#### Delete Cash Memo
```bash
curl -X DELETE http://localhost:8085/api/billing/cash-memo/{id}
```

### Invoice API

#### Create Invoice
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 750,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CARD"
  }' \
  http://localhost:8085/api/billing/invoice
```

**Response:**
```json
{
  "success": true,
  "message": "Invoice created successfully",
  "data": {
    "id": "03f0ba26-99bf-4ad1-8fe2-cff14ced19c4",
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 750.0,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CARD",
    "invoiceId": "INV-202506263954FB"
  }
}
```

#### Get All Invoices
```bash
curl -X GET http://localhost:8085/api/billing/invoice
```

#### Get Invoice by ID
```bash
curl -X GET http://localhost:8085/api/billing/invoice/{id}
```

#### Get Invoices by Patient ID
```bash
curl -X GET http://localhost:8085/api/billing/invoice/patient/{patientId}
```

#### Update Invoice
```bash
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 800,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CARD"
  }' \
  http://localhost:8085/api/billing/invoice/{id}
```

#### Delete Invoice
```bash
curl -X DELETE http://localhost:8085/api/billing/invoice/{id}
```

### Receipt API

#### Create Receipt
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 450,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "ONLINE"
  }' \
  http://localhost:8085/api/billing/receipt
```

**Response:**
```json
{
  "success": true,
  "message": "Receipt created successfully",
  "data": {
    "id": "1ef9f6d6-3375-4371-951b-4d82f0fbf85a",
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 450.0,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "ONLINE",
    "receiptId": "REC-202506263BAB7A"
  }
}
```

#### Get All Receipts
```bash
curl -X GET http://localhost:8085/api/billing/receipt
```

#### Get Receipt by ID
```bash
curl -X GET http://localhost:8085/api/billing/receipt/{id}
```

#### Get Receipts by Patient ID
```bash
curl -X GET http://localhost:8085/api/billing/receipt/patient/{patientId}
```

#### Update Receipt
```bash
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 500,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "ONLINE"
  }' \
  http://localhost:8085/api/billing/receipt/{id}
```

#### Delete Receipt
```bash
curl -X DELETE http://localhost:8085/api/billing/receipt/{id}
```

### Advance API

#### Create Advance Payment
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 1000,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "ONLINE"
  }' \
  http://localhost:8085/api/billing/advance
```

**Response:**
```json
{
  "success": true,
  "message": "Advance payment created successfully",
  "data": {
    "id": "9ba6a074-66ae-4b73-98ab-471fc3e2c3e3",
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 1000.0,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "ONLINE",
    "advanceId": "ADV-202506263C7BC8"
  }
}
```

#### Get All Advance Payments
```bash
curl -X GET http://localhost:8085/api/billing/advance
```

#### Get Advance Payment by ID
```bash
curl -X GET http://localhost:8085/api/billing/advance/{id}
```

#### Get Advance Payments by Patient ID
```bash
curl -X GET http://localhost:8085/api/billing/advance/patient/{patientId}
```

#### Update Advance Payment
```bash
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 1200,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "ONLINE"
  }' \
  http://localhost:8085/api/billing/advance/{id}
```

#### Delete Advance Payment
```bash
curl -X DELETE http://localhost:8085/api/billing/advance/{id}
```

### Credit Note API

#### Create Credit Note
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 200,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH",
    "reason": "Billing correction"
  }' \
  http://localhost:8085/api/billing/credit-note
```

**Response:**
```json
{
  "success": true,
  "message": "Credit note created successfully",
  "data": {
    "id": "fed549ed-a7c7-4c39-af61-89a5cf6cc214",
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 200.0,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH",
    "creditNoteId": "CN-202506265C53A7",
    "reason": "Billing correction"
  }
}
```

#### Get All Credit Notes
```bash
curl -X GET http://localhost:8085/api/billing/credit-note
```

#### Get Credit Note by ID
```bash
curl -X GET http://localhost:8085/api/billing/credit-note/{id}
```

#### Get Credit Notes by Patient ID
```bash
curl -X GET http://localhost:8085/api/billing/credit-note/patient/{patientId}
```

#### Update Credit Note
```bash
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 250,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH",
    "reason": "Updated billing correction"
  }' \
  http://localhost:8085/api/billing/credit-note/{id}
```

#### Delete Credit Note
```bash
curl -X DELETE http://localhost:8085/api/billing/credit-note/{id}
```

### Refund API

#### Create Refund
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 350,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH",
    "originalReceiptId": "REC-202506263BAB7A",
    "reason": "Service not provided"
  }' \
  http://localhost:8085/api/billing/refund
```

**Response:**
```json
{
  "success": true,
  "message": "Refund created successfully",
  "data": {
    "id": "516788e3-ffb9-4fbf-b3e0-54e5f2164f80",
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 350.0,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH",
    "refundId": "REF-20250626C8CFC1",
    "reason": "Service not provided"
  }
}
```

#### Get All Refunds
```bash
curl -X GET http://localhost:8085/api/billing/refund
```

#### Get Refund by ID
```bash
curl -X GET http://localhost:8085/api/billing/refund/{id}
```

#### Get Refunds by Patient ID
```bash
curl -X GET http://localhost:8085/api/billing/refund/patient/{patientId}
```

#### Update Refund
```bash
curl -X PUT \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "patientName": "John Doe",
    "date": "2025-06-26",
    "amount": 400,
    "createdBy": "Dr. Smith",
    "modeOfPayment": "CASH",
    "originalReceiptId": "REC-202506263BAB7A",
    "reason": "Updated: Service not provided"
  }' \
  http://localhost:8085/api/billing/refund/{id}
```

#### Delete Refund
```bash
curl -X DELETE http://localhost:8085/api/billing/refund/{id}
```

## API Authentication

To be implemented.

## Data Models

### BillingItem (Base class)
- id: String (Firestore document ID)
- patientId: String 
- patientName: String
- date: String (YYYY-MM-DD)
- amount: double
- createdBy: String
- modeOfPayment: String (CASH/CARD/ONLINE)
- createdDate: String (auto-generated)

### Specific Billing Types
1. **CashMemo**: Extends BillingItem + billId (CM-YYYYMMDDXXXXX)
2. **Invoice**: Extends BillingItem + invoiceId (INV-YYYYMMDDXXXXX)
3. **Receipt**: Extends BillingItem + receiptId (REC-YYYYMMDDXXXXX)
4. **Advance**: Extends BillingItem + advanceId (ADV-YYYYMMDDXXXXX)
5. **CreditNote**: Extends BillingItem + creditNoteId (CN-YYYYMMDDXXXXX) + reason
6. **Refund**: Extends BillingItem + refundId (REF-YYYYMMDDXXXXX) + originalReceiptId + reason

## Firestore Structure

- Collection: `cash_memos` - All Cash Memo documents
- Collection: `invoices` - All Invoice documents
- Collection: `receipts` - All Receipt documents
- Collection: `advances` - All Advance Payment documents
- Collection: `credit_notes` - All Credit Note documents
- Collection: `refunds` - All Refund documents
- Collection: `patients` - Patient documents
  - Sub-collection: `billing_history` - Contains copies of all billing records for the patient

## Error Handling

All API endpoints return appropriate HTTP status codes:
- 200: Success
- 404: Resource not found
- 500: Server error

## API Response Format

```json
{
  "success": true|false,
  "message": "Human-readable message",
  "data": { ... } | null
}
```
