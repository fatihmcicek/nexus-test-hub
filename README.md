# Nexus Test Hub - API & Performance Testing

## Overview
This project contains both API and Performance tests for the Pet Store API. The framework is built using RestAssured and JMeter with TestNG integration.

## Project Branches
- `main`: Contains API Testing Framework
- `performance-tests`: Contains Performance Testing Framework

## API Testing Framework (main branch)

### API Test Structure
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

### API Test Scenarios
#### Pet API Tests
- TC01: Create and verify new pet
- TC02: Update existing pet
- TC03: Find pets by status
- TC04: Delete pet
- TC05: Verify non-existent pet
- TC06: Invalid pet creation

#### Store API Tests
- TC01: Create new order
- TC02: Get store inventory
- TC03: Get invalid order
- TC04: Delete order

#### User API Tests
- TC01: Create new user
- TC02: Create multiple users
- TC03: Update user
- TC04: User login/logout
- TC05: Delete user

## Performance Testing Framework (performance-tests branch)

To access the performance testing framework:
```bash
git checkout performance-tests
```

### Performance Test Structure
```
src/
├── main/java/com/nexustest/performance/
│   ├── config/
│   │   └── JMeterEngine.java       # JMeter configuration
│   ├── reports/
│   │   └── JMeterReportGenerator.java  # Report generation
│   └── utils/
│       └── ExtentReportManager.java    # Test execution reporting
└── test/
    ├── java/com/nexustest/performance/scenarios/
    │   ├── BasePerformanceTest.java
    │   ├── TC01_PetStoreLoadTest.java
    │   ├── TC02_PetStoreConcurrencyTest.java
    │   ├── TC03_StoreStressTest.java
    │   ├── TC04_PetStoreSpikeTest.java
    │   └── TC05_UserStressTest.java
    └── resources/
        ├── jmeter.properties
        └── performance-testng.xml
```

### Performance Test Scenarios
#### TC01: Basic Load Test
- Normal conditions testing
- 25 concurrent users
- 5 second ramp-up
- Tests GET /pet/findByStatus

#### TC02: Concurrency Test
- Simultaneous operations testing
- 50 concurrent users
- 2 second ramp-up
- Tests POST /pet

#### TC03: Store Stress Test
- Store inventory under load
- 30 concurrent users
- 5 second ramp-up
- 2 iterations per user

#### TC04: Spike Test
- Sudden traffic spike testing
- 200 concurrent users
- 1 second ramp-up
- Single iteration

#### TC05: User Stress Test
- User operations under load
- 100 concurrent users
- 10 second ramp-up
- 5 iterations per user

## Setup and Configuration

### Prerequisites
- Java 21
- Maven
- IntelliJ IDEA (recommended)

### Installation
1. Clone the repository:
```bash
git clone https://github.com/yourusername/nexus-test-hub.git
```

2. For API Testing:
```bash
git checkout main
mvn clean install
```

3. For Performance Testing:
```bash
git checkout performance-tests
mvn clean install
```

## Running Tests

### API Tests (main branch)
```bash
# Run all API tests
mvn clean test

# Run specific test suite
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml
```

### Performance Tests (performance-tests branch)
```bash
# Run all performance tests
mvn clean test -DsuiteXmlFile=src/test/resources/performance-testng.xml

# Run specific performance test
mvn test -Dtest=TC01_PetStoreLoadTest
```

## Test Reports

### API Test Reports
- ExtentReports: `test-output/extent-reports/`
- TestNG reports: `target/surefire-reports/`

### Performance Test Reports
- JMeter results: `target/jmeter/results/`
- Performance reports: `target/jmeter/reports/`
- Execution reports: `target/performance-reports/`

## Dependencies

### API Testing
- RestAssured
- TestNG
- Jackson
- Lombok
- ExtentReports
- Log4j2

### Performance Testing
- JMeter
- TestNG
- ExtentReports
- Log4j2

## Contributing
1. Create your feature branch (`git checkout -b feature/AmazingFeature`)
2. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
3. Push to the branch (`git push origin feature/AmazingFeature`)
4. Create a Pull Request

## Notes
- Switch branches based on testing needs (API vs Performance)
- Ensure sufficient system resources for performance tests
- Review logs and reports after test execution
- Configure thread counts based on system capacity

## Contact
* Fatih Mehmet ÇİÇEK - [@linkedin](https://www.linkedin.com/in/fatihmcicek/)
* Project Link: [https://github.com/fatihmcicek/nexus-test-hub](https://github.com/fatihmcicek/nexus-test-hub)