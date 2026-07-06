# TimeSync 🕒

TimeSync is a Java-based backend application built with **Spring Boot**, designed to streamline meeting scheduling across different timezones. It helps resolve the common "scheduling conflict" problem by ensuring that meeting times are accurately calculated and synchronized for all participants, regardless of their geographical location.

---

## 🚀 Features

*   **Timezone-Aware Logic:** Uses Java's `java.time` API to handle complex timezone conversions.
*   **Conflict Prevention:** Ensures no meeting overlaps exist for participants.
*   **RESTful API:** Clean, intuitive endpoints for scheduling, retrieving, and canceling meetings.
*   **Modular Architecture:** Built using Spring Boot best practices (Controller-Service-Repository pattern).

---

## 🛠️ Technical Stack

*   **Language:** Java 17+
*   **Framework:** Spring Boot 3.x
*   **Build Tool:** Maven
*   **Database:** H2 / MySQL / PostgreSQL (Configurable via `application.properties`)
*   **API Testing:** Postman / cURL

---

## 📂 Project Structure

src/
├── main/
│   ├── java/
│   │   └── com/timesync/
│   │       ├── controller/   # REST Controllers
│   │       ├── service/      # Business logic & Timezone handling
│   │       ├── model/        # Entity classes
│   │       ├── repository/   # Data access layer
│   │       └── TimeSyncApplication.java
│   └── resources/
│       └── application.properties

---

##⚙️ How to Run

1. Prerequisites
Ensure you have Java JDK 17 or higher and Maven installed on your system.

2. Clone the Repository
Bash

git clone [https://github.com/divyabauskar/TimeSync.git](https://github.com/divyabauskar/TimeSync.git)
cd TimeSync

---

3. Build & Run
Use the Maven wrapper to build and start the application:

Bash

# On Windows
mvnw.cmd spring-boot:run

# On Linux/macOS
./mvnw spring-boot:run

The application will be accessible at http://localhost:8080.

---

🤝 Contributing
Contributions are welcome! If you have suggestions for new features or improvements, feel free to fork the repository and submit a pull request.
