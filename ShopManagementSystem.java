import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

class Product {
    int serial;
    String name;
    double price;
    double discount;
    int quantity;

    public Product(int serial, String name, double price, double discount, int quantity) {
        this.serial = serial;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.quantity = quantity;
    }

    public double priceAfterDiscount() {
        return price * (1 - discount / 100.0);
    }
}

class Sale {
    Product product;
    int quantitySold;
    double totalPrice;
    String customerName;
    int billNumber;

    public Sale(Product product, int quantitySold, double totalPrice, String customerName, int billNumber) {
        this.product = product;
        this.quantitySold = quantitySold;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.billNumber = billNumber;
    }
}

public class ShopManagementSystem {
    static ArrayList<Product> products = new ArrayList<>();
    static ArrayList<Sale> dailySales = new ArrayList<>();
    static Map<Integer, ArrayList<Sale>> billSales = new HashMap<>();
    static JLabel totalRevenueLabel = new JLabel("Total Revenue: 0.00");
    static int billCounter = 1000;

    public static void main(String[] args) {
        preloadProducts();
        showLogin();
    }

    public static void showLogin() {
        JFrame loginFrame = new JFrame("Shop Owner Login");
        loginFrame.setSize(350, 180);
        loginFrame.setLayout(new FlowLayout());
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(12);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(12);
        JButton loginButton = new JButton("Login");

        loginFrame.add(userLabel);
        loginFrame.add(userField);
        loginFrame.add(passLabel);
        loginFrame.add(passField);
        loginFrame.add(loginButton);
        loginFrame.setVisible(true);

        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (user.equals("owner") && pass.equals("shop123")) {
                loginFrame.dispose();
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid Username or Password!");
            }
        });
    }

    public static void showDashboard() {
        JFrame dash = new JFrame("Shop Management Dashboard");
        dash.setSize(800, 500);
        dash.setLayout(new BorderLayout(10, 10));
        dash.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dash.setLocationRelativeTo(null);

        JLabel title = new JLabel("Shop Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        dash.add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JButton addBtn = new JButton("Add / Update Product");
        JButton sellBtn = new JButton("Sell Product");
        JButton deleteBtn = new JButton("Delete Product");
        JButton undoBtn = new JButton("Undo");
        JButton reportBtn = new JButton("Show Daily Report");
        JButton inventoryBtn = new JButton("View Inventory");
        JButton searchBtn = new JButton("Search Product");

        JButton[] buttons = { addBtn, sellBtn, deleteBtn, undoBtn, reportBtn, inventoryBtn };
        Color[] colors = {
            new Color(135, 206, 250),
            new Color(144, 238, 144),
            new Color(255, 182, 193),
            new Color(255, 228, 181),
            new Color(221, 160, 221),
            new Color(240, 230, 140)
        };

        for (int i = 0; i < buttons.length; i++) {
            JButton b = buttons[i];
            b.setFont(new Font("SansSerif", Font.PLAIN, 14));
            b.setPreferredSize(new Dimension(220, 70));
            b.setBackground(colors[i]);
            b.setOpaque(true);
            b.setBorderPainted(false);
            panel.add(b);
        }

        dash.add(panel, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        totalRevenueLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        footer.add(totalRevenueLabel);
        searchBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchBtn.setPreferredSize(new Dimension(220, 50));
        footer.add(searchBtn);
        dash.add(footer, BorderLayout.SOUTH);
        dash.setVisible(true);

        addBtn.addActionListener(e -> addOrUpdateProduct());
        sellBtn.addActionListener(e -> { sellProduct(); updateTotalRevenue(); });
        deleteBtn.addActionListener(e -> deleteProduct());
        undoBtn.addActionListener(e -> { undoByBillNumber(); updateTotalRevenue(); });
        reportBtn.addActionListener(e -> showDailyReport());
        inventoryBtn.addActionListener(e -> viewInventory());
        searchBtn.addActionListener(e -> searchProduct());
    }

    public static void preloadProducts() {
        for (int i = 1; i <= 30; i++) {
            products.add(new Product(i, "Product" + i, 100 + i * 10, 5, 10));
        }
    }

    // ===== SELL PRODUCT ===== (updated)
    public static void sellProduct() {
        String customerName;
        while (true) {
            customerName = JOptionPane.showInputDialog("Enter Customer Name (mandatory):");
            if (customerName == null) return;
            if (customerName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Customer name is required to proceed.");
            } else break;
        }

        ArrayList<Sale> currentSales = new ArrayList<>();
        double totalBill = 0;
        int billNo = ++billCounter;

        while (true) {
            String input = JOptionPane.showInputDialog("Enter Serial Number or Product Name (or type 'PRINT INVOICE' to finish):");
            if (input == null) return;
            if (input.equalsIgnoreCase("PRINT INVOICE")) break;

            Product p = null;
            try { p = findProductBySerial(Integer.parseInt(input.trim())); }
            catch (Exception e) { p = findProductByName(input.trim()); }

            if (p == null) {
                JOptionPane.showMessageDialog(null, "Product not found!");
                continue;
            }
            if (p.quantity <= 0) {
                JOptionPane.showMessageDialog(null, "Out of stock!");
                continue;
            }

            try {
                int qty = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity to sell:"));
                if (qty <= 0 || qty > p.quantity) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity!");
                    continue;
                }

                p.quantity -= qty;
                double total = qty * p.priceAfterDiscount();
                Sale sale = new Sale(p, qty, total, customerName, billNo);
                dailySales.add(sale);
                billSales.computeIfAbsent(billNo, k -> new ArrayList<>()).add(sale);
                currentSales.add(sale);
                totalBill += total;

                JOptionPane.showMessageDialog(null, qty + " added to the invoice for " + p.name);

                // --- New Low Stock / Out of Stock Warnings ---
                if (p.quantity == 0) {
                    JOptionPane.showMessageDialog(null, "Product " + p.name + " is now OUT OF STOCK!");
                } else if (p.quantity < 5) {
                    JOptionPane.showMessageDialog(null, "Low stock warning for " + p.name + " – only " + p.quantity + " left!");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Invalid input!");
            }
        }

        if (currentSales.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No products added for this invoice!");
            return;
        }

        StringBuilder invoice = new StringBuilder();
        invoice.append("BILL NO: ").append(billNo).append("\n");
        invoice.append("Customer: ").append(customerName).append("\n\nInvoice Summary:\n\n");
        for (Sale s : currentSales) {
            invoice.append(s.product.name + " | Qty: " + s.quantitySold + " | Total: " + String.format("%.2f", s.totalPrice) + "\n");
        }
        invoice.append("\nGrand Total: " + String.format("%.2f", totalBill));
        JOptionPane.showMessageDialog(null, invoice.toString(), "Final Invoice", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== UNDO (updated for total max qty)
    public static void undoByBillNumber() {
        try {
            int billNo = Integer.parseInt(JOptionPane.showInputDialog("Enter Bill Number to undo:"));
            ArrayList<Sale> billList = billSales.get(billNo);
            if (billList == null || billList.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No sales found for this bill!");
                return;
            }

            while (true) {
                String input = JOptionPane.showInputDialog("Enter Serial Number or Product Name to undo (or type 'PRINT INVOICE' to finish):");
                if (input == null) return;

                if (input.equalsIgnoreCase("PRINT INVOICE")) {
                    // Show updated invoice
                    StringBuilder invoice = new StringBuilder();
                    invoice.append("UPDATED INVOICE - BILL NO: ").append(billNo).append("\n");
                    if (!billList.isEmpty())
                        invoice.append("Customer: ").append(billList.get(0).customerName).append("\n\nInvoice Summary:\n\n");

                    double total = 0;
                    for (Sale s : billList) {
                        invoice.append(s.product.name + " | Qty: " + s.quantitySold + " | Total: " + String.format("%.2f", s.totalPrice) + "\n");
                        total += s.totalPrice;
                    }
                    invoice.append("\nGrand Total: " + String.format("%.2f", total));
                    JOptionPane.showMessageDialog(null, invoice.toString(), "Updated Invoice", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }

                ArrayList<Sale> matchedList = new ArrayList<>();
                try {
                    int serial = Integer.parseInt(input.trim());
                    for (Sale s : billList)
                        if (s.product.serial == serial) matchedList.add(s);
                } catch (Exception ex) {
                    for (Sale s : billList)
                        if (s.product.name.equalsIgnoreCase(input.trim())) matchedList.add(s);
                }

                if (matchedList.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "No matching sale found in this bill.");
                    continue;
                }

                int totalQtySold = 0;
                for (Sale s : matchedList) totalQtySold += s.quantitySold;

                int undoQty;
                try {
                    String qStr = JOptionPane.showInputDialog("Enter quantity to undo (max " + totalQtySold + "):");
                    if (qStr == null) return;
                    undoQty = Integer.parseInt(qStr.trim());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity!");
                    continue;
                }

                if (undoQty <= 0 || undoQty > totalQtySold) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity!");
                    continue;
                }

                int remaining = undoQty;
                for (Sale s : matchedList) {
                    if (remaining == 0) break;
                    int toUndo = Math.min(s.quantitySold, remaining);
                    s.product.quantity += toUndo;
                    double undoAmount = toUndo * s.product.priceAfterDiscount();
                    s.quantitySold -= toUndo;
                    s.totalPrice -= undoAmount;
                    remaining -= toUndo;

                    if (s.quantitySold == 0) dailySales.remove(s);
                }

                billList.removeIf(s -> s.quantitySold == 0);
                JOptionPane.showMessageDialog(null, "Undo completed for " + input + ". Stock updated successfully.");

                if (billList.isEmpty()) {
                    billSales.remove(billNo);
                    JOptionPane.showMessageDialog(null, "Bill " + billNo + " fully undone and removed.");
                    break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid Input!");
        }
    }

    // ===== Remaining methods unchanged =====
    public static void showDailyReport() {
        if (billSales.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No sales today!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        double overallTotal = 0;
        for (Map.Entry<Integer, ArrayList<Sale>> entry : billSales.entrySet()) {
            int billNo = entry.getKey();
            ArrayList<Sale> billList = entry.getValue();
            sb.append("Bill No: ").append(billNo).append("\n");
            if (!billList.isEmpty()) sb.append("Customer: ").append(billList.get(0).customerName).append("\n");
            double billTotal = 0;
            for (Sale s : billList) {
                sb.append(" " + s.product.name + " | Qty: " + s.quantitySold + " | Total: " + String.format("%.2f", s.totalPrice) + "\n");
                billTotal += s.totalPrice;
            }
            sb.append(" Bill Total: " + String.format("%.2f", billTotal) + "\n\n");
            overallTotal += billTotal;
        }
        sb.append("----------------------------------------\n");
        sb.append("Total Revenue: " + String.format("%.2f", overallTotal) + "\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(720, 380));
        JOptionPane.showMessageDialog(null, scroll, "Daily Report (Bill-wise)", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void addOrUpdateProduct() {
    String choice = JOptionPane.showInputDialog(null, "Enter 'new' to add new product or 'update' to modify existing product:");
    if (choice == null) return;

    if (choice.equalsIgnoreCase("new")) {
        try {
            int serial = Integer.parseInt(JOptionPane.showInputDialog("Enter Serial Number:"));
            Product existing = findProductBySerial(serial);
            if (existing != null) {
                int confirm = JOptionPane.showConfirmDialog(null, "Serial " + serial + " already exists for " + existing.name + ". Replace?", "Confirm Replace", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.NO_OPTION) return;
                else products.remove(existing);
            }
            String name = JOptionPane.showInputDialog("Enter Product Name:");
            double price = Double.parseDouble(JOptionPane.showInputDialog("Enter Price:"));
            double discount = Double.parseDouble(JOptionPane.showInputDialog("Enter Discount %:"));
            int quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter Quantity:"));
            products.add(new Product(serial, name, price, discount, quantity));
            JOptionPane.showMessageDialog(null, "Product added successfully!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid Input!");
        }
    } 
    
    else if (choice.equalsIgnoreCase("update")) {
        try {
            int serial = Integer.parseInt(JOptionPane.showInputDialog("Enter Serial Number to update:"));
            Product p = findProductBySerial(serial);
            if (p == null) {
                JOptionPane.showMessageDialog(null, "Product not found!");
                return;
            }

            // Ask for quantity update
            int confirmQty = JOptionPane.showConfirmDialog(null, "Do you want to change the quantity of " + p.name + "?", "Update Quantity", JOptionPane.YES_NO_OPTION);
            if (confirmQty == JOptionPane.YES_OPTION) {
                String action = JOptionPane.showInputDialog("Type 'increase' or 'decrease' quantity:");
                if (action != null && (action.equalsIgnoreCase("increase") || action.equalsIgnoreCase("decrease"))) {
                    int qty = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity to " + action + ":"));
                    if (action.equalsIgnoreCase("increase")) p.quantity += qty;
                    else {
                        if (qty > p.quantity) {
                            JOptionPane.showMessageDialog(null, "Cannot decrease more than available stock!");
                        } else {
                            p.quantity -= qty;
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Quantity updated! Current qty: " + p.quantity);
                }
            }

            // Ask to update price
            int confirmPrice = JOptionPane.showConfirmDialog(null, "Do you want to update the price of " + p.name + "?", "Update Price", JOptionPane.YES_NO_OPTION);
            if (confirmPrice == JOptionPane.YES_OPTION) {
                double newPrice = Double.parseDouble(JOptionPane.showInputDialog("Enter new price:"));
                p.price = newPrice;
                JOptionPane.showMessageDialog(null, "Price updated successfully!");
            }

            // Ask to update discount
            int confirmDisc = JOptionPane.showConfirmDialog(null, "Do you want to update the discount of " + p.name + "?", "Update Discount", JOptionPane.YES_NO_OPTION);
            if (confirmDisc == JOptionPane.YES_OPTION) {
                double newDisc = Double.parseDouble(JOptionPane.showInputDialog("Enter new discount %:"));
                p.discount = newDisc;
                JOptionPane.showMessageDialog(null, "Discount updated successfully!");
            }

            JOptionPane.showMessageDialog(null, "Product details updated successfully!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid Input!");
        }
    } 
    
    else {
        JOptionPane.showMessageDialog(null, "Enter 'new' or 'update' only.");
        }
    }

    public static void deleteProduct() {
        try {
            int s = Integer.parseInt(JOptionPane.showInputDialog("Enter Serial Number to delete:"));
            Product p = findProductBySerial(s);
            if (p == null) {
                JOptionPane.showMessageDialog(null, "Product not found!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + p.name + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) products.remove(p);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid Input!");
        }
    }

    public static void viewInventory() {
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No products in inventory.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Inventory:\n\n");
        for (Product p : products) {
            sb.append("Serial: " + p.serial + "\n");
            sb.append("Name: " + p.name + "\n");
            sb.append("Price before: " + String.format("%.2f", p.price) + "\n");
            sb.append("Price after: " + String.format("%.2f", p.priceAfterDiscount()) + "\n");
            sb.append("Discount: " + String.format("%.2f", p.discount) + "%\n");
            sb.append("Quantity: " + p.quantity + "\n");
            sb.append("-----------------------------\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(720, 380));
        JOptionPane.showMessageDialog(null, scroll, "Inventory", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void searchProduct() {
        String input = JOptionPane.showInputDialog("Enter Serial Number or Product Name:");
        if (input == null) return;
        Product p = null;
        try { p = findProductBySerial(Integer.parseInt(input.trim())); }
        catch (Exception e) { p = findProductByName(input.trim()); }
        if (p == null) { JOptionPane.showMessageDialog(null, "Product not found!"); return; }

        String info = "Serial: " + p.serial + "\n" +
                "Name: " + p.name + "\n" +
                "Price before: " + String.format("%.2f", p.price) + "\n" +
                "Discount: " + String.format("%.2f", p.discount) + "%\n" +
                "Price after: " + String.format("%.2f", p.priceAfterDiscount()) + "\n" +
                "Quantity: " + p.quantity + "\n";

        JOptionPane.showMessageDialog(null, info, "Product Details", JOptionPane.INFORMATION_MESSAGE);
    }

    public static Product findProductBySerial(int serial) {
        for (Product p : products) if (p.serial == serial) return p;
        return null;
    }

    public static Product findProductByName(String name) {
        for (Product p : products) if (p.name.equalsIgnoreCase(name)) return p;
        return null;
    }

    public static void updateTotalRevenue() {
        double total = 0;
        for (Sale s : dailySales) total += s.totalPrice;
        totalRevenueLabel.setText("Total Revenue: " + String.format("%.2f", total));
    }
}