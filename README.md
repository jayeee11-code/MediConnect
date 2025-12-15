<<<<<<< HEAD
# MediConnect Backend

Real-Time Dispensary and Doctor Availability Tracker - Backend API

## Overview

MediConnect is a comprehensive healthcare management system that connects patients with dispensaries and doctors in real-time. The backend provides RESTful APIs for patient management, doctor availability tracking, queue management, and medical records.

## Features

- **User Authentication & Authorization**: JWT-based authentication with role-based access control
- **Patient Management**: Complete patient profile management with medical history
- **Doctor Management**: Doctor profiles with availability status updates
- **Dispensary Management**: Dispensary information with geolocation support
- **Real-time Queue System**: Join queues remotely, track position, and receive notifications
- **Medical Records**: Secure storage and retrieval of patient medical records
- **WebSocket Support**: Real-time updates for queue status changes
- **Geolocation Services**: Find nearby dispensaries based on location

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT
- **MongoDB** for data persistence
- **WebSocket** for real-time communication
- **Swagger/OpenAPI** for API documentation
- **Lombok** for reducing boilerplate code
- **MapStruct** for object mapping

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- MongoDB 4.4+

## Installation

1. **Clone the repository**
```bash
git clone <repository-url>
cd mediconnect-backend
```

2. **Configure MongoDB**

Update `application.properties` with your MongoDB connection string:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/mediconnect
```

3. **Configure JWT Secret**

Generate a secure JWT secret and update in `application.properties`:
```properties
jwt.secret=your-very-long-and-secure-secret-key-here
jwt.expiration=86400000
```

4. **Build the project**
```bash
mvn clean install
```

5. **Run the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

API documentation is also available at:
```
http://localhost:8080/api-docs
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user

### Patients
- `GET /api/patients/me` - Get current patient profile
- `GET /api/patients/{id}` - Get patient by ID
- `PUT /api/patients/{id}` - Update patient profile
- `DELETE /api/patients/{id}` - Delete patient

### Doctors
- `GET /api/doctors` - Get all doctors
- `GET /api/doctors/{id}` - Get doctor by ID
- `GET /api/doctors/me` - Get current doctor profile
- `PUT /api/doctors/{id}` - Update doctor profile
- `PATCH /api/doctors/{id}/availability` - Update availability status

### Dispensaries
- `GET /api/dispensaries` - Get all dispensaries
- `GET /api/dispensaries/open` - Get open dispensaries
- `GET /api/dispensaries/nearby` - Find nearby dispensaries
- `GET /api/dispensaries/search?city={city}` - Search by city
- `PUT /api/dispensaries/{id}` - Update dispensary
- `PATCH /api/dispensaries/{id}/status` - Update open/close status

### Queue Management
- `POST /api/queue/join` - Join queue
- `GET /api/queue/dispensary/{dispensaryId}` - Get dispensary queue
- `GET /api/queue/doctor/{doctorId}` - Get doctor queue
- `GET /api/queue/patient/{patientId}/history` - Get patient queue history
- `PATCH /api/queue/{id}/status` - Update queue status
- `DELETE /api/queue/{id}` - Cancel queue entry

### Medical Records
- `POST /api/medical-records` - Create medical record
- `GET /api/medical-records/{id}` - Get record by ID
- `GET /api/medical-records/patient/{patientId}` - Get patient records
- `GET /api/medical-records/doctor/{doctorId}` - Get doctor records
- `PUT /api/medical-records/{id}` - Update medical record
- `DELETE /api/medical-records/{id}` - Delete medical record

## WebSocket Connection

Connect to WebSocket endpoint for real-time updates:
```
ws://localhost:8080/ws
```

### Subscribe to Queue Updates
```javascript
// Dispensary queue
stompClient.subscribe('/topic/queue/{dispensaryId}', callback);

// Doctor queue
stompClient.subscribe('/topic/queue/doctor/{doctorId}', callback);

// Personal notifications
stompClient.subscribe('/user/queue/updates', callback);
```

## User Roles

- **PATIENT**: Can view dispensaries, join queues, view own medical records
- **DOCTOR**: Can manage availability, view queues, create medical records
- **DISPENSARY_ADMIN**: Can manage dispensary, view all queues and records

## Security

- All endpoints (except `/auth/**` and public dispensary search) require JWT authentication
- Passwords are encrypted using BCrypt
- Role-based access control using Spring Security
- CORS configured for specified origins

## Database Schema

### Collections
- `users` - User accounts
- `patients` - Patient profiles
- `doctors` - Doctor profiles
- `dispensaries` - Dispensary information
- `queue_entries` - Queue management
- `medical_records` - Patient medical records

## Error Handling

The API returns standard HTTP status codes:
- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `500` - Internal Server Error

Error responses include:
```json
{
  "status": 400,
  "message": "Error description",
  "timestamp": "2024-01-01T10:00:00"
}
```

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package -DskipTests
```

The JAR file will be created in the `target` directory.

## Environment Variables

For production deployment, set these environment variables:
- `MONGODB_URI` - MongoDB connection string
- `JWT_SECRET` - JWT signing secret
- `JWT_EXPIRATION` - Token expiration time in milliseconds
- `CORS_ALLOWED_ORIGINS` - Comma-separated list of allowed origins

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues and questions, please open an issue on GitHub.

## Authors

MediConnect Development Team

---

**Note**: This is a backend API only. A separate frontend application is required to provide the user interface.
=======
# MediConnect
A mobile app connecting local dispensaries with patients using real-time doctor availability, queue management, and medical records.
>>>>>>> f1b5cef3a3f9419e7c8d26a182758b8e6b09a9ce
