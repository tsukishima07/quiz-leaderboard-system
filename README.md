# 🏆 Quiz Leaderboard System

## 📌 Overview

This project is developed as part of the **Java Internship Assignment**. It simulates a real-world backend system that interacts with an external API, processes incoming data, removes duplicates, and generates a final leaderboard.

The system ensures accurate aggregation of participant scores across multiple API polls while handling duplicate data efficiently.

---

## 🎯 Objective

* Poll the API **10 times** to collect quiz data
* Handle **duplicate responses** using `(roundId + participant)`
* Calculate **total score per participant**
* Generate a **sorted leaderboard**
* Compute **overall total score**
* Submit the final result via API

---

## ⚙️ Tech Stack

* **Java**
* **Gson (JSON Parsing)**
* **HTTPURLConnection (API Calls)**

---

## 🔄 Working Flow

1. Send GET requests to:

   ```
   /quiz/messages?regNo=YOUR_REG_NO&poll=0-9
   ```

2. Maintain:

   * `Set` → to track duplicates
   * `Map` → to store total scores

3. Deduplicate using:

   ```
   roundId + participant
   ```

4. Aggregate scores per participant

5. Sort leaderboard in descending order

6. Submit result via POST:

   ```
   /quiz/submit
   ```

---

## 🧠 Key Concept: Deduplication

Duplicate API responses may appear across polls.

### ❌ Incorrect:

Poll 1 → Alice +10
Poll 3 → Alice +10 again → Total = 20

### ✅ Correct:

Poll 1 → Alice +10
Poll 3 → Duplicate → Ignored → Total = 10

---

## 📊 Sample Output

```
Leaderboard:
Bob -> 295
Alice -> 280
Charlie -> 260

Total Score = 835
```

---

## 📡 API Endpoints

### 🔹 GET (Polling)

```
https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/messages
```

### 🔹 POST (Submission)

```
https://devapigw.vidalhealthtpa.com/srm-quiz-task/quiz/submit
```

---

## 🚀 How to Run

### 1. Compile

```
javac -cp "lib/*" QuizApp.java
```

### 2. Run

```
java -cp ".;lib/*" QuizApp
```

---

## 📁 Project Structure

```
quiz-app/
 ├── QuizApp.java
 ├── lib/
 │    └── gson-2.10.1.jar
 └── .vscode/
      └── settings.json
```

---

## ⚠️ Challenges Faced

* Handling **duplicate API responses**
* Managing **API failures (502/503 errors)**
* Implementing **retry logic**
* Ensuring correct **aggregation and sorting**

---

## ✅ Features

* Duplicate-safe processing
* Retry mechanism for unstable API
* Accurate leaderboard generation
* Clean and structured code

---

## 📌 Conclusion

This project demonstrates backend integration, data processing, and error handling in distributed systems. It ensures reliable results even when APIs return duplicate or inconsistent data.

---

## 👨‍💻 Author

**Vamsi Krishna Kadama**
Registration No: **RA2311003020069**
