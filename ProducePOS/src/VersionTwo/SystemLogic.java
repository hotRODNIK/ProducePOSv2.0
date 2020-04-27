package VersionTwo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class SystemLogic {
    // Fields which hold monetary amounts and product names
    // Some may seem like duplicates, but I didn't want any values getting overwritten
    // by mistake to avoid confusion since some of the same values are used in different contexts
    private static double total;
    private static double subTotal;
    private static double itemPrice;
    private static double numPounds;
    private static String cost;
    private static String retail;
    private static String caseQty;
    private static String unit;
    private static String name;
    private static String title;
    private static String plu;
    private static String prodName;
    private static String scanType;
    private static String prodCode;

    // Various getters and setters
    public static void setNumPounds(double pounds){ numPounds = pounds; }

    public static double getNumPounds() { return numPounds; }

    public static double getTotal() {
        return total;
    }

    public static double getSubTotal() {
        return subTotal;
    }

    public static double getItemPrice() {
        return itemPrice;
    }

    public static String getScanType() { return scanType; }

    public static String getCost() {
        return cost;
    }

    public static String getRetail() {
        return retail;
    }

    public static String getCaseQty() {
        return caseQty;
    }

    public static String getUnit() {
        return unit;
    }

    public static String getName() {
        return name;
    }

    public static String getPlu() {
        return plu;
    }

    public static String getProdName() {
        return prodName;
    }

    // Returns true if the employee logged in is a manager
    static boolean isManager(){
        return title.equals("T");
    }

    // Calculates tax, is called within calcTotal
    private static double calcTax(){
        return subTotal * 0.13;
    }

    // Login logic
    static boolean login(String user, String pass) throws Exception {
        // Holds whether or not the user can login
        boolean canLogin = false;
        try{
            // Connect to the database, and create a new query
            Connection c = DriverManager.getConnection("jdbc:sqlite:Databases/EmployeeBase.db");
            Statement s = c.createStatement();

            // Search the database to see if the userID and password are valid
            ResultSet r = s.executeQuery("SELECT * FROM empINFO");
            while(r.next() && !canLogin){
                String userID = r.getString("empID");
                title = r.getString("isMgr");
                String passWord = r.getString("pwd");
                if (userID.equals(user) && pass.equals(passWord)){
                    canLogin = true;
                }
            }
            // Clean up when finished
            s.close();
            c.close();
        }
        catch (SQLException e){
            // Handle any SQL errors
            new StartError().start();
        }
        return canLogin;
    }

    // Scan logic
    static boolean scan(String code) throws Exception {
        // Holds whether or not the code is valid
        boolean isFound = false;
        try{
            // Connect to the database
            Connection c = DriverManager.getConnection("jdbc:sqlite:Databases/ProductBase.db");
            Statement s = c.createStatement();

            // Search the database to see if the code is in the system
            ResultSet r = s.executeQuery("SELECT * FROM pluTable");
            while(r.next() && !isFound){
                name = r.getString("prodName");
                prodCode = r.getString("prodCode");
                scanType = r.getString("unit");
                itemPrice = Double.parseDouble(r.getString("retailPrice"));
                cost = r.getString("costPrice");
                unit = r.getString("unit");
                if (prodCode.equals(code)){
                    isFound = true;
                }
            }

            // Clean up when finished
            s.close();
            c.close();
        }
        catch (SQLException e){
            // Handle any SQL errors
            new StartError().start();
        }
        return isFound;
    }

    // Calculate the subtotal
    static double calcSubTotal(){
        if (scanType.equals("lb")){
            subTotal += (itemPrice * numPounds);
        }
        else {
            subTotal += itemPrice;
        }
        return subTotal;
    }

    // Calculate the total
    static double calcTotal(){
        total = subTotal + calcTax();
        return total;
    }

    // Logic of payment
    static String pay(double amt){
        DecimalFormat df = new DecimalFormat("#.##");
        total = Double.parseDouble(df.format(total));
        if (amt > total){
            // If the amount paid is more than the total, calculate change due
            double change = amt - total;
            return df.format(change);
        }
        else if (amt == total) // If amount and total are the same, change due is $0.00
            return "0.00";
        else
            return "Really!"; // If amount paid is less than total, let the user know
    }

    // Handles lookup of a product in the database
    static boolean codeFound(String query) throws Exception {
        // Holds whether or not the code is valid
        boolean isFound = false;
        try{
            // Connect to the database
            Connection c = DriverManager.getConnection("jdbc:sqlite:Databases/ProductBase.db");
            Statement s = c.createStatement();

            // Search the database to see if the code is in the system
            ResultSet r = s.executeQuery("SELECT * FROM pluTable");
            while(r.next() && !isFound){
                prodName = r.getString("prodName");
                plu = r.getString("prodCode");
                retail = r.getString("retailPrice");
                cost = r.getString("costPrice");
                unit = r.getString("unit");
                caseQty = r.getString("caseQuantity");
                if (query.equals(plu)){
                    isFound = true;
                }
            }
            // Clean up when finished
            s.close();
            c.close();
        }
        catch (SQLException e){
            // Handle any SQL errors
            new StartError().start();
        }
        return isFound;
    }

    // Inserts a new PLU into the database
    static void insert(String[] args) throws Exception {
        try {
            // Connect to the database
            Connection c = DriverManager.getConnection("jdbc:sqlite:Databases/ProductBase.db");

            // Prepare and execute the statement
            PreparedStatement p = c.prepareStatement("INSERT INTO pluTable(prodName, prodCode, retailPrice, unit, costPrice, caseQuantity) VALUES(?, ?, ?, ?, ?, ?)");
            p.setString(1, args[0]);
            p.setString(2, args[1]);
            p.setDouble(3, Double.parseDouble(args[2]));
            p.setString(4, args[3]);
            p.setDouble(5, Double.parseDouble(args[4]));
            p.setDouble(6, Double.parseDouble(args[5]));
            p.executeUpdate();

            // Clean up when finished
            p.close();
            c.close();
        }
        catch (SQLException e){
            // Handle any SQL errors
            new StartError().start();
        }
    }

    static void update(String[] args) throws Exception {
        try {
            // Connect to the database
            Connection c = DriverManager.getConnection("jdbc:sqlite:Databases/ProductBase.db");

            // Prepare and execute the statement
            PreparedStatement p = c.prepareStatement("UPDATE pluTable SET prodName = ?, retailPrice = ?, unit = ?, costPrice = ?, caseQuantity = ? WHERE prodCode = ?");
            p.setString(1, args[0]);
            p.setDouble(2, Double.parseDouble(args[2]));
            p.setString(3, args[3]);
            p.setDouble(4, Double.parseDouble(args[4]));
            p.setDouble(5, Double.parseDouble(args[5]));
            p.setString(6, args[1]);
            p.executeUpdate();

            // Clean up when finished
            p.close();
            c.close();
        }
        catch (SQLException e){
            // Handle any SQL errors
            new StartError().start();
        }
    }

    public static void insertSalesInfo() throws Exception {
        try {
            // Connect to the database
            Connection c = DriverManager.getConnection("jdbc:sqlite:Databases/SalesDatabase.db");

            // Prepare and execute the statement
            PreparedStatement p = c.prepareStatement("INSERT INTO salesTbl(productName, productCode, " +
                    "date, qtySold, retailPrice, costPrice) VALUES (?, ?, ?, ?, ?, ?)");

            // Insert information according to the units involved
            if (unit.equals("lb")){
                // Create a date string
                SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");

                // Prepare and execute
                p.setString(1, name);
                p.setString(2, prodCode);
                p.setString(3, d.format(new Date()));
                p.setDouble(4, numPounds);
                p.setDouble(5, (itemPrice));
            }
            else{
                // Create a date string
                SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");

                // Prepare and execute
                p.setString(1, name);
                p.setString(2, prodCode);
                p.setString(3, d.format(new Date()));
                p.setDouble(4, 1);
                p.setDouble(5, itemPrice);
            }

            p.setDouble(6, Double.parseDouble(cost));
            p.executeUpdate();

            // Clean up when finished
            p.close();
            c.close();
        }
        catch (SQLException e){
            // Handle any SQL errors
            new StartError().start();
        }
    }

    // Generates a sales report based on starting and ending dates
    public static String generateReport(String startDate, String endDate) throws Exception {
        try {
            // Connect to the database
            StringBuilder report;
            DecimalFormat df = new DecimalFormat("#.##");
            Connection c = DriverManager.getConnection("jdbc:sqlite:Databases/SalesDatabase.db");

            // Query the database based on the search parameters
            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("SELECT * FROM salesTbl WHERE date BETWEEN date(" + "\"" + startDate + "\"" +") " +
                    "AND date(" + "\"" + endDate + "\"" + ") ORDER BY productName ASC");

            // Generate the report
            report = new StringBuilder("Item name  Item PLU  Date  Number Sold  Cost Price   Retail Price  Profit on Item"
                                 + "\n" + "*********************************************************************************" + "\n");
            while (r.next()) {
                double profit = (Double.parseDouble(r.getString("qtySold")) * r.getDouble("retailPrice")) - r.getDouble("costPrice");
                report.append(r.getString("productName")).append("   ").append(r.getString("productCode"))
                        .append("   ").append(r.getString("date")).append("   ").append(r.getString("qtySold"))
                        .append("   ").append(r.getDouble("costPrice")).append("   ").append(r.getDouble("retailPrice"))
                        .append("   ").append(df.format(profit)).append("\n");
            }

            // Cleanup when finished
            s.close();
            c.close();
            return report.toString();
        }
        catch (SQLException e) {
            // Handle any SQL errors
            new StartError().start();
            return "";
        }
    }

    // Writes the sales report to disk
    public static void writeToFile(String report) throws FileNotFoundException {
        // Create an output stream object and write to it
        PrintWriter p = new PrintWriter("Reports/SalesReport.txt");
        p.println(report);
        p.close();
    }
}