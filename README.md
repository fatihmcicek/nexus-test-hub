# Nexus Test Hub

[![Nexus Test Hub CI/CD](https://github.com/fatihmcicek/nexus-test-hub/actions/workflows/maven.yml/badge.svg)](https://github.com/fatihmcicek/nexus-test-hub/actions/workflows/maven.yml)

## Overview
This project contains both API and Performance tests for the Pet Store API, utilizing RestAssured for API testing and JMeter for performance testing, both integrated with TestNG framework.

## Project Branches
- `main`: API Testing Framework
- `performance-tests`: Performance Testing Framework

## 📂 Project Structure

### API Testing Framework (main branch)
```
src/
├── main/java/com/nexustest/
│   ├── api/
│   │   ├── base/           
│   │   ├── models/         
│   │   └── services/       
│   └── core/
│       ├── config/         
│       └── constants/      
└── test/
    ├── java/com/nexustest/api/tests/
    │   ├── PetTest.java
    │   ├── StoreTest.java
    │   └── UserTest.java
    └── resources/
        ├── test-data/      
        └── testng.xml      
```

### Performance Testing Framework (performance-tests branch)
```
src/
├── main/java/com/nexustest/performance/
│   ├── config/
│   │   └── JMeterEngine.java
│   ├── reports/
│   │   └── JMeterReportGenerator.java
│   └── utils/
│       └── ExtentReportManager.java
└── test/
    └── java/com/nexustest/performance/scenarios/
        ├── TC01_PetStoreLoadTest.java
        ├── TC02_PetStoreConcurrencyTest.java
        ├── TC03_StoreStressTest.java
        ├── TC04_PetStoreSpikeTest.java
        └── TC05_UserStressTest.java
```

## 🔍 Test Scenarios

### API Tests
- **Pet API**
    - Create, update, and delete pets
    - Find pets by status
    - Handle invalid cases

- **Store API**
    - Place and manage orders
    - Check inventory

- **User API**
    - User management operations
    - Authentication tests

### Performance Tests
- **TC01: Load Test**
    - 25 users
    - 5s ramp-up
    - Basic API behavior

- **TC02: Concurrency Test**
    - 50 users
    - 2s ramp-up
    - Multiple operations

- **TC03: Store Stress**
    - 30 users
    - 5s ramp-up
    - 2 iterations

- **TC04: Spike Test**
    - 200 users
    - 1s ramp-up
    - Sudden load

- **TC05: User Stress**
    - 100 users
    - 10s ramp-up
    - 5 iterations

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven
- Git
- IntelliJ IDEA (recommended)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/fatihmcicek/nexus-test-hub.git
cd nexus-test-hub
```

2. Choose your testing type:

For API Testing:
```bash
git checkout main
mvn clean install
```

For Performance Testing:
```bash
git checkout performance-tests
mvn clean install
```

## 🏃 Running Tests

### API Tests
```bash
# All tests
mvn clean test

# Specific suite
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml
```

### Performance Tests
```bash
# All tests
mvn clean test -DsuiteXmlFile=src/test/resources/performance-testng.xml

# Single test
mvn test -Dtest=TC01_PetStoreLoadTest
```

## 📊 Test Reports

### API Test Reports
```
test-output/extent-reports/    # Detailed HTML reports
target/surefire-reports/       # TestNG reports
```

### Performance Test Reports
```
target/jmeter/results/         # Raw data
target/jmeter/reports/         # Performance metrics
target/performance-reports/    # Execution details
```

## 🔄 CI/CD Pipeline

Our CI/CD pipeline uses GitHub Actions with separate workflows for API and Performance tests:

### Workflow Features
- Automatic triggers on push and PR
- Separate jobs for API and Performance tests
- Test report generation
- Artifact upload for results
- Branch-specific workflows

### Status Badges
- Main Branch (API): ![API Tests](https://github.com/fatihmcicek/nexus-test-hub/actions/workflows/maven.yml/badge.svg?branch=main)
- Performance Branch: ![Performance Tests](https://github.com/fatihmcicek/nexus-test-hub/actions/workflows/maven.yml/badge.svg?branch=performance-tests)

## 🛠 Technologies

### API Testing
- RestAssured
- TestNG
- Jackson
- ExtentReports
- Log4j2

### Performance Testing
- JMeter
- TestNG
- ExtentReports
- Log4j2

## 📝 Contributing

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📌 Notes
- Switch branches based on testing needs
- Ensure system resources for performance tests
- Check logs and reports after execution
- Adjust thread counts based on system capacity

## 📧 Contact

Fatih Mehmet ÇİÇEK - [LinkedIn](https://www.linkedin.com/in/fatihmcicek)

Project Link: [https://github.com/fatihmcicek/nexus-test-hub](https://github.com/fatihmcicek/nexus-test-hub)