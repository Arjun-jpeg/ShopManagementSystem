# Shop Management System

This is a **Java Swing-based desktop application** designed for small shop owners to manage their product inventory, sales, and daily revenue.  
It provides an easy-to-use graphical interface for adding, updating, selling, deleting, and searching products — all with real-time stock management and invoice generation.

---

## Overview

The **Shop Management System** is a Java Swing-based desktop application designed to help small shop owners manage:
- Products and stock
- Customer billing and sales
- Daily sales reports
- Low-stock and out-of-stock alerts  

It includes login authentication, product inventory control, billing, undo functionality, and real-time stock tracking.

---

## Features

### **Login System**
- Secure owner login  
- Default credentials:  
  - **Username:** `owner`  
  - **Password:** `shop123`

### **Product Management**
- Add new products or update existing ones  
- Modify price, discount, and quantity  
- Delete products from inventory  
- Search products by serial number or name  

### **Sales and Billing**
- Sell products using serial number or name  
- Generate invoices with bill number and customer name  
- View detailed invoice before confirmation  
- Automatically updates stock quantity after each sale  

### **Undo Functionality**
- Undo any sale using the bill number  
- Restore product quantities accurately  
- Displays updated invoice after undo completion  

### **Stock Management**
- Displays **“Low Stock Warning”** when quantity < 5  
- Displays **“Out of Stock”** message when quantity reaches 0  

### **Reports and Inventory**
- View complete product inventory  
- Display daily report with all bills and total revenue  
- Calculates and updates total revenue automatically  

---

## Technologies Used

- **Programming Language:** Java  
- **GUI Framework:** Swing  
- **Data Structures:** ArrayList, HashMap  
- **Layouts Used:** BorderLayout, GridLayout, FlowLayout  

---

## How to Run

1. **Download or clone** this repository:
   ```bash
   git clone https://github.com/Arjun-jpeg/ShopManagementSystem.git
2.**Open the project** in your Java IDE (IntelliJ IDEA, Eclipse, or NetBeans).

3.**Compile and run** the main file: ShopManagementSystem.java

4. **Login credentials:**
- **Username:** `owner`  
- **Password:** `shop123`

5. **Use the dashboard to:**
- Manage products  
- Perform sales  
- View inventory  
- Check daily reports  

---

## Author

**A. Arjun**  
B.Tech CSE (AI & ML)  
SRM Institute of Science and Technology  
