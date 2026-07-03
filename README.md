# 🚗 Car Rental Management System

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)

A robust, desktop-based **Car Rental Management application** built with Java Swing and MySQL. This project provides an intuitive graphical user interface (with custom styling and dark mode support) for efficiently managing vehicles, drivers, and customer bookings.

## ✨ Features

* **🚘 Vehicle Management:** View available vehicles, capacities, statuses, and rates per kilometer.
* **👨‍✈️ Driver Tracking:** Monitor driver availability, contact information, and statuses.
* **📋 Booking System:** Create new bookings, calculate total amounts dynamically based on distance, and generate user records.
* **🌗 Modern UI:** Custom-built Java Swing interface featuring gradient panels, rounded components, and dynamic table sorting.
* **🗄️ Database Integration:** Seamless and secure data handling using JDBC and MySQL.

## 🛠️ Tech Stack

* **Frontend:** Java Swing, AWT
* **Backend:** Java (Core)
* **Database:** MySQL
* **API/Driver:** MySQL Connector/J (`mysql-connector.jar`)

## 📁 Project Structure

```text
📦 java-project
 ┣ 📂 connector
 ┃ ┗ 📜 mysql-connector.jar       # Database driver
 ┣ 📂 src
 ┃ ┣ 📂 backend
 ┃ ┃ ┣ 📜 BookingService.java     # Handles booking logic & calculations
 ┃ ┃ ┣ 📜 DatabaseConnection.java # JDBC connection configuration
 ┃ ┃ ┣ 📜 DriverService.java      # Handles driver data retrieval
 ┃ ┃ ┗ 📜 VehicleService.java     # Handles vehicle data retrieval
 ┃ ┗ 📂 frontend
 ┃   ┗ 📜 CarRentalGUI.java       # Main application window & UI components
