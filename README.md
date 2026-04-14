# 🎓 Student Budget Tracker

A complete personal finance management web application for students, featuring automated budget tracking, gamified savings, and intelligent financial forecasting.

---

## 🎯 Live Demo

> [loading....]

---

## 📌 Project Overview

Student Budget Tracker helps students manage their finances through automated budget alerts, smart piggy bank savings, streak-based motivation, and monthly financial reports. Built with Spring Boot and modern web technologies.

---

## 🛠️ Technical Stack

| Category | Technologies |
|----------|-------------|
| Backend | Java 17, Spring Boot 3, Spring Security, Spring Data JPA |
| Frontend | Thymeleaf, Bootstrap 5, Chart.js, HTML5/CSS3 |
| Database | MySQL, Hibernate |
| Reporting | iText (PDF), Apache Commons CSV |
| Email | JavaMailSender (SMTP) |
| Build | Maven |

---

## ✨ Key Features

### 🔐 Security & Authentication
- BCrypt password encoding
- Spring Security form-based authentication
- Role-based access control (USER role)

### 📊 Financial Core
- CRUD operations for expenses, incomes, and categories
- Real-time budget tracking with 90% warning and exceeded alerts
- Monthly profit/loss calculation with automated piggy bank transfers
- Borrowing system with repayment scheduling
- PDF & CSV report generation

### 🎮 Gamification
- Streak system (consecutive days without overspending)
- Progressive savings goals (must complete in order)
- Daily tracking streaks to encourage consistency

### ⏰ Automation
Scheduled tasks (cron jobs) for:
- Monthly category creation
- End-of-month financial closing
- Daily budget checks
- Streak updates

### 🎨 UI/UX
- Light/Dark theme toggle (CSS variables)
- Responsive design (mobile/tablet/desktop)
- Interactive charts (Chart.js)
- Glassmorphism effects

---

## 🏗️ Architecture Highlights

- MVC pattern with clean separation of concerns
- Repository layer using Spring Data JPA
- Service layer with `@Transactional` for data integrity
- DTOs for API responses (`ChartDataDto`)
- Custom `UserDetailsService` for authentication
- Scheduled tasks with `@EnableScheduling`

---

## 📂 Project Structure

```text
├── controller/      # 10+ REST controllers
├── service/         # Business logic with transactions
├── repository/      # JPA repositories
├── entity/          # JPA entities (9 entities)
├── enums/           # CategoryName, NotificationType, StatusType
├── dto/             # Data transfer objects
├── config/          # Security configuration
└── resources/
    ├── templates/   # Thymeleaf HTML templates
    └── static/      # CSS with theme support
```

---

## 🚀 Setup Instructions

```bash
# Clone repository
git clone https://github.com/yourusername/student-budget-tracker.git

# Configure database in application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/budget_tracker

# Build and run
mvn clean install
mvn spring-boot:run

# Access at http://localhost:8080
```

---

## 💡 What This Project Demonstrates

| Skill | Implementation |
|-------|---------------|
| Java/Spring Boot | REST APIs, dependency injection, lifecycle management |
| Database Design | 9 normalized entities with relationships (`@OneToOne`, `@ManyToOne`) |
| Security | Custom authentication, password encoding, protected routes |
| Scheduling | Cron jobs for automated monthly closing |
| Reporting | PDF/CSV generation with iText |
| Frontend | Thymeleaf dynamic pages, Chart.js, responsive CSS |
| Email Integration | SMTP with template-based notifications |
| Testing | Transactional rollback for data integrity |
| OOP Principles | Encapsulation, inheritance, polymorphism, design patterns |

---

## 📊 Database Schema (Simplified)

- `users` → `expenses` (one-to-many)
- `users` → `expense_categories` (one-to-many, unique per month)
- `users` → `piggy_banks` (one-to-one)
- `users` → `streaks` (one-to-one)
- `expense_categories` → `expenses` (one-to-many)

---

## 🧠 Problem-Solving Highlights

- **Budget Alert System:** Real-time notifications when spending reaches 90% or exceeds limit
- **Piggy Bank Logic:** Automatically saves surplus, covers deficits, or triggers borrowing
- **Goal Progression:** Forces users to complete goals in order (teaches prioritization)
- **Streak System:** Tracks daily consistency with personal best records
- **Monthly Closing:** Atomic transactions ensure data consistency when transferring funds

---

## 🔧 Environment Variables

```properties
# Required configuration
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=

# Email (optional, for notifications)
spring.mail.username=
spring.mail.password=
```

---

## 📈 Future Improvements

- REST API with JWT for mobile app
- Docker containerization
- Unit & integration tests (JUnit, Mockito)
- CI/CD pipeline (GitHub Actions)
- Payment gateway integration
- Multi-currency support

---

<p align="center">⭐ If you find this project useful, please consider giving it a star!</p>
