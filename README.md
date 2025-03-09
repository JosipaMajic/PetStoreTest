# 🐾 PetStore API Test Automation

This project is an **API Test Automation Framework** for the **Pet Online Store** backend, built using:
- **Java 17+**
- **JUnit 5**
- **Rest Assured** for API testing
- **ExtentReports** for detailed reporting
- **SLF4J + Logback** for extensive logging

---

## 📂 Project Structure

```
petstore-api-tests/
│── src/
│   ├── main/
│   │   ├── java/com/example/petstore/
│   │   │   ├── dto/
│   │   │   │   ├── Category.java
│   │   │   │   ├── Pet.java
│   │   │   │   ├── Tag.java
│   │   │   ├── config/
│   │   │   │   ├── ConfigReader.java
│   ├── test/
│   │   ├── java/com/example/petstore/
│   │   │   ├── CreatePetTest.java
│   │   │   ├── GetPetTest.java
│   │   │   ├── UpdatePetTest.java
│   │   ├── utils/
│   │   │   ├── ExtentReportUtil.java
│   │   ├── resources/
│   │   │   ├── config.properties
│── pom.xml                      
│── README.md                    
│── target/                      
│   ├── ExtentReport_<timestamp>.html  
```

---

## 🛠️ Setup

### 1️⃣ Prerequisites
- **Java 17+** installed
- **Maven** installed

### 2️⃣ Install Dependencies
Run this command to download all dependencies:
```bash
mvn clean install
```

---

## 🚀 Running Tests

### 1️⃣ Run All Tests
```bash
mvn clean test
```

### 2️⃣ Run a Specific Test Class
```bash
mvn -Dtest=CreatePetTest test
```

### 3️⃣ Run Multiple Specific Test Classes
```bash
mvn -Dtest=CreatePetTest,GetPetTest test
```

---

## 📊 Test Reports

- The **ExtentReport** is generated after each test run inside the **target/** folder.
- It is **timestamped**, so each run has a separate report.
- Open the latest report in a browser:
  ```
  target/ExtentReport_<timestamp>.html
  ```

---

## 📜 Logging

- We use **SLF4J + Logback** for **detailed logs**.
- All API requests, responses, and test steps are **logged extensively**.
- Logs are printed in the console and saved in the reports.

---

## 🔧 Configuration

The **config.properties** file in `src/test/resources/` contains the API base URL and other settings.  

---

## ✨ Future Enhancements

- Add more negative test cases
- Improve performance testing
- Integrate with CI/CD pipeline

---

