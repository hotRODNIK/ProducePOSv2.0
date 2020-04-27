package VersionTwo;

import javafx.fxml.FXML;
import java.text.DecimalFormat;

public class Controller {
    // Fields which correspond to objects on the GUI or decimal formatting,
    // which are referenced to display output, get input or change properties
    public javafx.scene.control.TextField userIn;
    public javafx.scene.control.TextField passIn;
    public javafx.scene.control.TextField codeIn;
    public javafx.scene.control.TextField subTotal;
    public javafx.scene.control.TextField tax;
    public javafx.scene.control.TextField total;
    public javafx.scene.control.TextField paid;
    public javafx.scene.control.TextField change;
    public javafx.scene.control.TextField plu;
    public javafx.scene.control.TextField cost;
    public javafx.scene.control.TextField retail;
    public javafx.scene.control.TextField qty;
    public javafx.scene.control.TextField name;
    public javafx.scene.control.TextField unit;
    public javafx.scene.control.TextField pounds;
    public javafx.scene.control.TextArea productNames;
    public javafx.scene.control.TextArea reportArea;
    public javafx.scene.control.Button pay;
    public javafx.scene.control.Button scan;
    public javafx.scene.control.Button insert;
    public javafx.scene.control.Button save;
    public javafx.scene.control.Button saveButton;
    public javafx.scene.control.DatePicker start;
    public javafx.scene.control.DatePicker end;
    private String report;
    private static final DecimalFormat df = new DecimalFormat("#.##");

    // When invoked, this closes the entire system
    @FXML public void handleLoginExit(){
        System.exit(0);
    }

    // When invoked, this logs a user into the system
    @FXML public void handleLogin() throws Exception{
        // Get username and password
        String user = userIn.getText();
        String pass = passIn.getText();
        boolean login = SystemLogic.login(user, pass); // returns true if found, false if not
        if (login) {
            // If the login is found, let the user have access to the system
            new StartMain().start();
        }
        else {
            // Else, notify the user they are not allowed access
            new StartBadLogin().start();
        }
        userIn.clear();
        passIn.clear();
    }

    // When invoked, this handles the process involved in scanning an item
    @FXML public void handleScan() throws Exception{
        try {
            // Get the PLU
            String code = codeIn.getText();
            codeIn.clear();
            boolean isFound = SystemLogic.scan(code); // returns true if found, false if not

            // Use different logic for pounds or each items
            if (isFound && SystemLogic.getScanType().equals("lb")) {
                // Get the number of pounds
                if (Double.parseDouble(pounds.getText()) >= 0) {
                    SystemLogic.setNumPounds(Double.parseDouble(pounds.getText()));

                    // Get the prices, product name and calculate subtotal
                    String info = SystemLogic.getName();
                    double itemPrice = SystemLogic.getItemPrice() * SystemLogic.getNumPounds();
                    double sub = SystemLogic.calcSubTotal();

                    // Output results and enable buttons
                    productNames.setText(productNames.getText() + info + " " + df.format(itemPrice) + "\n");
                    subTotal.setText(df.format(sub));
                    pay.setDisable(false);

                    // Insert sales information into the sales database
                    SystemLogic.insertSalesInfo();
                }
                else{
                    new StartError().start();
                }
                pounds.clear();

            } else if (isFound) {
                // Get prices, etc. and compute subtotal
                pounds.clear();
                String info = SystemLogic.getName();
                double itemPrice = SystemLogic.getItemPrice();
                double sub = SystemLogic.calcSubTotal();

                // Output results and enable buttons
                productNames.setText(productNames.getText() + info + " " + df.format(itemPrice) + "\n");
                subTotal.setText(df.format(sub));
                pay.setDisable(false);

                // Insert sales information into the sales database
                SystemLogic.insertSalesInfo();
            } else {
                pounds.clear();
                new StartBadCode().start(); // Let the user know the code isn't in the system
            }
        }
        catch (Exception e){
            new StartError().start();
            pounds.clear();
        }
    }

    // Handles the clearing of the balancing report
    @FXML public void handleBalancingClear() { reportArea.clear(); }

    // Handles closure of the Main Screen
    @FXML public void handleMainScreenExit(){
        StartMain.killProcess();
    }

    // Handles the transition from the Main Screen to the Sale Screen
    @FXML public void handleSaleScreen()throws Exception{
        new StartSale().start();
        StartMain.killProcess();
    }

    // Handles transition from the Sale Screen to the Main Screen
    @FXML public void handleToMain()throws Exception{
        StartSale.killProcess();
        new StartMain().start();
    }

    // Handles everything involved in calculating amount owing, change and amount paid
    @FXML public void handleFinish() throws Exception{ // click finish once to display amount owed, click again to calculate change
        try {
            // Disable the scan button, and display tax
            scan.setDisable(true);
            total.setText(df.format(SystemLogic.calcTotal()));
            tax.setText(df.format(SystemLogic.getTotal() - SystemLogic.getSubTotal()));
            if (paid.getText().equals(""))
                paid.setText("Please enter an amount"); // If no amount has been entered, prompt an amount
            else
                change.setText(SystemLogic.pay(Double.parseDouble(paid.getText()))); // Else display the change due
        }
        catch (NumberFormatException e){
            // Handle any invalid inputs
            new StartError().start();
        }
    }

    // Handles closure of the error form
    @FXML public void quitError(){ StartError.killProcess(); }

    // Handles closure of the bad PLU form
    @FXML public void quitBadCode(){ StartBadCode.killProcess(); }

    // Handles closure of the bad login form
    @FXML public void quitBadLogin(){ StartBadLogin.killProcess(); }

    // Handles closure of the access denied form
    @FXML public void quitAccessDenied() { StartAccessDenied.killProcess(); }

    // Handles closure of the update success form
    @FXML public void quitSuccess() { StartSuccess.killProcess(); }

    // Handles launching of PLU maintenance
    @FXML public void launchPLU() throws Exception {
        if (SystemLogic.isManager()){
            StartMain.killProcess();
            new StartPLU().start();
        }
        else{
            new StartAccessDenied().start();
        }
    }

    // Handles transition of PLU to main screen
    @FXML public void PLUToMain() throws Exception{
        StartPLU.killProcess();
        new StartMain().start();
    }

    // Launches the balancing window
    @FXML public void launchBalancing() throws Exception{
        if (SystemLogic.isManager()){
            StartMain.killProcess();
            new StartBalancing().start();
        }
        else{
            new StartAccessDenied().start();
        }
    }

    // Handles transition of balancing to the main screen
    @FXML public void quitBalancing() throws Exception{
        StartBalancing.killProcess();
        new StartMain().start();
    }

    // Handles lookup of a product in PLU maintenance
    @FXML public void handleLookup() throws Exception {
        String item = plu.getText();
        if (SystemLogic.codeFound(item)){
            plu.setText(SystemLogic.getPlu());
            cost.setText(SystemLogic.getCost());
            retail.setText(SystemLogic.getRetail());
            qty.setText(SystemLogic.getCaseQty());
            name.setText(SystemLogic.getProdName());
            unit.setText(SystemLogic.getUnit());
            insert.setDisable(true);
            save.setDisable(false);
        }
        else{
            new StartBadCode().start();
            cost.clear();
            retail.clear();
            qty.clear();
            name.clear();
            unit.clear();
            insert.setDisable(false);
            save.setDisable(true);

        }
    }

    // Handles Insertion of a new PLU
    @FXML public void handleInsert() throws Exception{
        // Declare an array of data to insert
        String[] args = new String[6];
        args[0] = name.getText();
        args[1] = plu.getText();
        args[2] = retail.getText();
        args[3] = unit.getText().toLowerCase();
        args[4] = cost.getText();
        args[5] = qty.getText();

        // Insert
        SystemLogic.insert(args);

        // Update values on the screen
        if (SystemLogic.codeFound(plu.getText()) && (args[3].equals("ea") || args[3].equals("lb"))){
            plu.setText(SystemLogic.getPlu());
            cost.setText(SystemLogic.getCost());
            retail.setText(SystemLogic.getRetail());
            qty.setText(SystemLogic.getCaseQty());
            name.setText(SystemLogic.getProdName());
            unit.setText(SystemLogic.getUnit());
            insert.setDisable(true);
            save.setDisable(false);
            new StartSuccess().start();
        }
        else{
            new StartError().start();
        }
    }

    // Handles updating of products in the database
    @FXML public void handleUpdate() throws Exception{
        // Declare an array of data to insert
        String[] args = new String[6];
        args[0] = name.getText();
        args[1] = plu.getText();
        args[2] = retail.getText();
        args[3] = unit.getText().toLowerCase();
        args[4] = cost.getText();
        args[5] = qty.getText();

        // Update
        SystemLogic.update(args);

        // Update values on the screen
        if (SystemLogic.codeFound(plu.getText()) && (args[3].equals("ea") || args[3].equals("lb"))){
            plu.setText(SystemLogic.getPlu());
            cost.setText(SystemLogic.getCost());
            retail.setText(SystemLogic.getRetail());
            qty.setText(SystemLogic.getCaseQty());
            name.setText(SystemLogic.getProdName());
            unit.setText(SystemLogic.getUnit());
            insert.setDisable(true);
            new StartSuccess().start();
        }
        else{
            new StartError().start();
        }
    }

    // Handles sales report generation
    @FXML public void handleReport() throws Exception {
        try {
            report = SystemLogic.generateReport(start.getValue().toString(), end.getValue().toString());
            reportArea.setText(report);
            saveButton.setDisable(false);
        }
        catch (Exception e){
            new StartError().start();
        }
    }

    // Handles writing of the report to file
    @FXML public void handleSave() throws Exception {
        SystemLogic.writeToFile(report);
        new StartSuccess().start();
    }
}