# Hospital Management Backend API 

## Run Server Using Maven
### Prerequisites
- Java installed (JDK 17 or compatible)
- Maven installed

### Verify installations

```
java -version
mvn -version
```

### Steps to Run
Go to project root (where pom.xml exists)
```
cd path\to\hospitalManagement
```

### Clean and build
```
mvn clean install
```

### Run the application

```
mvn spring-boot:run
```

## Authentication

### Signup
```
curl -X POST http://localhost:8080/auth/signup -H "Content-Type: application/json" -d "{ \"username\": \"shaurya\", \"password\": \"password123\" }"
```

### Login
```
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d "{ \"username\": \"shaurya\", \"password\": \"password123\" }"
```

## Public APIs

### Get all doctors
```
curl http://localhost:8080/public/doctors
```

## Admin APIs

### Get all patients
```
curl "http://localhost:8080/admin/patients?page=0&size=10" -H "Authorization: Bearer YOUR_JWT"
```

## Doctor APIs

### Get appointments
```
curl http://localhost:8080/doctors/appointments -H "Authorization: Bearer YOUR_JWT"
```

## Patient APIs

### Create appointment
```
curl -X POST http://localhost:8080/patients/appointments -H "Content-Type: application/json" -H "Authorization: Bearer YOUR_JWT" -d "{ \"doctorId\": 1, \"patientId\": 4, \"appointmentTime\": \"2026-01-10T10:30:00\", \"reason\": \"Routine checkup\" }"
```

### Get profile
```
curl http://localhost:8080/patients/profile -H "Authorization: Bearer YOUR_JWT"
```