/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package interiordesignproject1;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import net.proteanit.sql.DbUtils;
import java.util.Set;
import java.util.HashSet;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import java.util.Collections;

 
public class Interiordesignproject1 {
    private JFrame mainFrame;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private Employee manager;
    private UserDetails currentClient;
    private Design currentDesign;
    private Employee selectedEmployee;
    private Customer currentCustomer;
    private boolean isManager = false;
    private JComboBox<String> tableDropdown;
private JTable dataTable;
private JButton editBtn;



    public Interiordesignproject1() {
        manager = new Employee("Manager", "admin", "0000000000", "0000000000", "manager@example.com", 9999, "email", 0.0);

        prepareGUI();
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Interior Design Office System");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(createWelcomePanel(), "WELCOME");
        cardPanel.add(createClientPanel(), "CLIENT");
        cardPanel.add(createDesignTypePanel(), "DESIGN_TYPE");
        cardPanel.add(createCustomDesignPanel(), "CUSTOM_DESIGN");
        cardPanel.add(createPricingPanel(), "PRICING");
        cardPanel.add(createEmployeeSelectionPanel(), "SELECT_EMPLOYEE");
        cardPanel.add(createEmployeeLoginPanel(), "EMPLOYEE_LOGIN");
        cardPanel.add(createEmployeePanel(), "EMPLOYEE");
        cardPanel.add(createAdminDashboardPanel(), "ADMIN_DASHBOARD"); // ✅ هنا
        cardPanel.add(createLoginPanel(), "LOGIN");
cardPanel.add(createSignUpPanel(), "SIGNUP");
        cardPanel.add(createEmployeeRoleSelectionPanel(), "EMPLOYEE_ROLE_SELECT");



        mainFrame.add(cardPanel);
        mainFrame.setVisible(true);
    }
    private boolean authenticateUser(String username, String password) {
    boolean isAuthenticated = false;
    try {
        Connection conn = DBConnection.getConnection();
        if (conn != null) {
            String sql = "SELECT * FROM UserDetails WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                isAuthenticated = true;
            }

            rs.close();
            pstmt.close();
            conn.close();
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Database error during login: " + ex.getMessage(), 
            "Connection Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    return isAuthenticated;
}
    private String getUserRole(String username) {
    String role = "";
    try (Connection conn = DBConnection.getConnection()) {
        // First get user ID
        String idQuery = "SELECT USER_id FROM UserDetails WHERE username = ?";
        PreparedStatement idStmt = conn.prepareStatement(idQuery);
        idStmt.setString(1, username);
        ResultSet idRs = idStmt.executeQuery();

        if (idRs.next()) {
            int userId = idRs.getInt("USER_id");

            // Check if employee
            String empSql = "SELECT * FROM Employee WHERE Employee_id = ?";
            PreparedStatement empStmt = conn.prepareStatement(empSql);
            empStmt.setInt(1, userId);
            ResultSet empRs = empStmt.executeQuery();

            if (empRs.next()) {
                role = "employee";
            } else {
                // Check if customer
                String clientSql = "SELECT * FROM Customer WHERE Customer_id = ?";
                PreparedStatement clientStmt = conn.prepareStatement(clientSql);
                clientStmt.setInt(1, userId);
                ResultSet clientRs = clientStmt.executeQuery();

                if (clientRs.next()) {
                    role = "client";
                }
                clientRs.close();
                clientStmt.close();
            }
            empRs.close();
            empStmt.close();
        }
        idRs.close();
        idStmt.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, 
            "Role check error: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
    return role;
}



   private JPanel createWelcomePanel() {
    GradientPanel mainPanel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255));
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Header panel
    JPanel headerPanel = new JPanel();
    headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

    ImageIcon originalIcon = new ImageIcon("/Users/batoolsaeed/NetBeansProjects/interiordesignproject1/src/folder/Logo.jpg");
    Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
    JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
    imageLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

    JLabel title = new JLabel("Interior Design Office System");
    title.setFont(new Font("Verdana", Font.BOLD, 24));
    title.setAlignmentY(Component.CENTER_ALIGNMENT);

    headerPanel.add(imageLabel);
    headerPanel.add(Box.createRigidArea(new Dimension(15, 0)));
    headerPanel.add(title);
    headerPanel.add(Box.createHorizontalGlue());

    // Buttons panel
    JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
    buttonPanel.setOpaque(false);

    JButton loginBtn = new JButton("Login");
    JButton signUpBtn = new JButton("Sign Up");

    loginBtn.setFont(new Font("Verdana", Font.BOLD, 20));
    signUpBtn.setFont(new Font("Verdana", Font.BOLD, 20));

    // Navigate to login screen
           loginBtn.addActionListener(e -> cardLayout.show(cardPanel, "LOGIN"));


    // Navigate to sign-up screen
    signUpBtn.addActionListener(e -> cardLayout.show(cardPanel, "SIGNUP"));

    buttonPanel.add(loginBtn);
    buttonPanel.add(signUpBtn);

    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(buttonPanel, BorderLayout.CENTER);

    return mainPanel;
}
  
    private JPanel createLoginPanel() {
        GradientPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255));
        panel.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel userLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JLabel messageLabel = new JLabel("");

        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);
        formPanel.add(new JLabel());
        formPanel.add(messageLabel);

        JButton loginButton = new JButton("Login");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);

        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT * FROM UserDetails WHERE username = ? AND password = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("USER_id");
                    String contact = rs.getString("preferredContactMethod");

                    currentClient = new UserDetails(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("bankAccount"),
                        rs.getString("phoneNumber"),
                        rs.getString("email"),
                        userId,
                        contact
                    );

                    if (UserDetails.isCustomer(userId)) {
                        currentCustomer = new Customer(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("bankAccount"),
                            rs.getString("phoneNumber"),
                            rs.getString("email"),
                            userId,
                            contact
                        );

                        JOptionPane.showMessageDialog(panel, 
                            "Welcome, " + currentCustomer.getUsername() + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);

                        cardLayout.show(cardPanel, "DESIGN_TYPE");
                    } else if (UserDetails.isEmployee(userId)) {
                        cardLayout.show(cardPanel, "EMPLOYEE_ROLE_SELECT");
                    } else {
                        messageLabel.setText("Unknown role.");
                    }
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(panel, 
                    "Database error: " + ex.getMessage(),
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
                }
        });

        return panel;
    }

   private JPanel createSignUpPanel() {
    GradientPanel signUpPanel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255));
    signUpPanel.setLayout(new GridLayout(8, 2, 10, 10));

    JLabel idLabel = new JLabel("User ID:");
    JSpinner idSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));

    JLabel usernameLabel = new JLabel("Username:");
    JTextField usernameField = new JTextField();

    JLabel passwordLabel = new JLabel("Password:");
    JPasswordField passwordField = new JPasswordField();

    JLabel emailLabel = new JLabel("Email:");
    JTextField emailField = new JTextField();

    JLabel phoneLabel = new JLabel("Phone:");
    JTextField phoneField = new JTextField();

    JLabel bankLabel = new JLabel("Bank Account:");
    JTextField bankField = new JTextField();

    JButton createBtn = new JButton("Create Account");

    createBtn.addActionListener(e -> {
        int id = (Integer) idSpinner.getValue();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        String phone = phoneField.getText();
        String bank = bankField.getText();

        UserDetails newUser = new UserDetails(username, password, bank, phone, email, id, "email");

      if (saveUserToDatabase(newUser)) {
            try (Connection conn = DBConnection.getConnection()) {
                // Insert into Customer table
                String customerSQL = "INSERT INTO Customer (Customer_id) VALUES (?)";
                try (PreparedStatement pstmt = conn.prepareStatement(customerSQL)) {
                    pstmt.setInt(1, newUser.getId());
                    pstmt.executeUpdate();
                }
                
                JOptionPane.showMessageDialog(null, "Account created successfully!");
                cardLayout.show(cardPanel, "LOGIN");
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, 
                    "Error creating customer: " + ex.getMessage(),
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    signUpPanel.add(idLabel);
    signUpPanel.add(idSpinner);

    signUpPanel.add(usernameLabel);
    signUpPanel.add(usernameField);

    signUpPanel.add(passwordLabel);
    signUpPanel.add(passwordField);

    signUpPanel.add(emailLabel);
    signUpPanel.add(emailField);

    signUpPanel.add(phoneLabel);
    signUpPanel.add(phoneField);

    signUpPanel.add(bankLabel);
    signUpPanel.add(bankField);

    signUpPanel.add(new JLabel()); 
    signUpPanel.add(createBtn);

    return signUpPanel;
}
   private JPanel createEmployeeRoleSelectionPanel() {
        GradientPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255));
        panel.setLayout(new BorderLayout());

        JLabel title = new JLabel("Select Your Role", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(40, 10, 30, 10));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 200, 80, 200));

        JButton managerBtn = new JButton("Manager");
        JButton employeeBtn = new JButton("Employee");

        managerBtn.setFont(new Font("Verdana", Font.BOLD, 18));
        employeeBtn.setFont(new Font("Verdana", Font.BOLD, 18));
        managerBtn.setPreferredSize(new Dimension(200, 50));
        employeeBtn.setPreferredSize(new Dimension(200, 50));

        buttonPanel.add(managerBtn);
        buttonPanel.add(employeeBtn);

        panel.add(title, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        // Button actions
       managerBtn.addActionListener(e -> {
    String adminPassword = JOptionPane.showInputDialog(panel, "Enter Manager Password:");
    
    if ("Admin123".equals(adminPassword)) {
        isManager = true;
        JOptionPane.showMessageDialog(panel, "Welcome Manager!");
        cardLayout.show(cardPanel, "ADMIN_DASHBOARD");
    } else {
        JOptionPane.showMessageDialog(panel, "Incorrect password. Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
    }
});

        employeeBtn.addActionListener(e -> {
            cardLayout.show(cardPanel, "EMPLOYEE_LOGIN");
        });

        return panel;
    }
    private JPanel createClientPanel() {
    GradientPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255));
    panel.setLayout(new GridLayout(7, 2, 5, 5)); // Changed to 7 rows for ID field

    JTextField username = new JTextField();
    JTextField bankAccount = new JTextField();
    JTextField phone = new JTextField();
    JTextField email = new JTextField();
    JSpinner idSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
    JSpinner usageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

    panel.add(new JLabel("Username:"));
    panel.add(username);
    panel.add(new JLabel("Bank Account:"));
    panel.add(bankAccount);
    panel.add(new JLabel("Phone:"));
    panel.add(phone);
    panel.add(new JLabel("Email:"));
    panel.add(email);
    panel.add(new JLabel("Customer ID:"));
    panel.add(idSpinner);
    panel.add(new JLabel("Usage Count:"));
    panel.add(usageSpinner);

    JButton nextBtn = new JButton("Next");
    nextBtn.addActionListener(e -> {
        currentCustomer = new Customer();
        currentCustomer.setUsername(username.getText());
        currentCustomer.setBankAccount(bankAccount.getText());
        currentCustomer.setPhoneNumber(phone.getText());
        currentCustomer.setEmail(email.getText());
        currentCustomer.setId((Integer) idSpinner.getValue());
        currentCustomer.setUsageCount((Integer) usageSpinner.getValue());
        
        cardLayout.show(cardPanel, "DESIGN_TYPE");
    });

    panel.add(nextBtn);
    return panel;
}
    private boolean saveUserToDatabase(UserDetails user) {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "INSERT INTO UserDetails (USER_id, username, password, "
            + "bankAccount, phoneNumber, email, preferredContactMethod) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, user.getId());
        stmt.setString(2, user.getUsername());
        stmt.setString(3, user.getPassword());
        stmt.setString(4, user.getBankAccount());
        stmt.setString(5, user.getPhoneNumber());
        stmt.setString(6, user.getEmail());
        stmt.setString(7, user.getPreferredContactMethod());

        int rowsInserted = stmt.executeUpdate();
        return rowsInserted > 0;
        
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Error saving user: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
}


    private JPanel createDesignTypePanel() {
        GradientPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255)); // Pink to Light Blue
        panel.setLayout(new GridLayout(6, 2, 5, 5));
        JPanel radioPanel = new JPanel(new GridLayout(4, 1));
        ButtonGroup group = new ButtonGroup();
        
        JRadioButton classicBtn = new JRadioButton("Classic Design");
        JRadioButton modernBtn = new JRadioButton("Modern Design");
        JRadioButton rusticBtn = new JRadioButton("Rustic Design");
        JRadioButton customBtn = new JRadioButton("Custom Design");
        
        group.add(classicBtn);
        group.add(modernBtn);
        group.add(rusticBtn);
        group.add(customBtn);
        
        radioPanel.add(classicBtn);
        radioPanel.add(modernBtn);
        radioPanel.add(rusticBtn);
        radioPanel.add(customBtn);
        
        JButton nextBtn = new JButton("Next");
        nextBtn.addActionListener(e -> {
            if (classicBtn.isSelected()) handleDesignSelection(Design.DesignType.CLASSIC);
            else if (modernBtn.isSelected()) handleDesignSelection(Design.DesignType.MODERN);
            else if (rusticBtn.isSelected()) handleDesignSelection(Design.DesignType.RUSTIC);
            else if (customBtn.isSelected()) cardLayout.show(cardPanel, "CUSTOM_DESIGN");
            else JOptionPane.showMessageDialog(mainFrame, "Please select a design type!");
        });

        panel.add(new JLabel("Select Design Style:", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(radioPanel, BorderLayout.CENTER);
        panel.add(nextBtn, BorderLayout.SOUTH);
        
        return panel;
    }

    private void handleDesignSelection(Design.DesignType type) {
        RegularServiceDesign design = new RegularServiceDesign();
        design.setDesignType(type);
        design.setDesignName(type + " Design");
        design.setCost(type == Design.DesignType.MODERN ? 800 : type == Design.DesignType.RUSTIC ? 700 : 500);
        currentDesign = design;
        cardLayout.show(cardPanel, "PRICING");
    }

    private JPanel createCustomDesignPanel() {
        GradientPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255)); // Pink to Light Blue
        panel.setLayout(new GridLayout(6, 2, 5, 5));
        JTextField designName = new JTextField();
        JSpinner costSpinner = new JSpinner(new SpinnerNumberModel(1000.0, 100.0, 10000.0, 50.0));
        JTextArea feedbackArea = new JTextArea(3, 20);
        
        panel.add(new JLabel("Design Name:"));
        panel.add(designName);
        panel.add(new JLabel("Cost:"));
        panel.add(costSpinner);
        panel.add(new JLabel("Feedback (optional):"));
        panel.add(new JScrollPane(feedbackArea));

        JButton createBtn = new JButton("Create Design");
        createBtn.addActionListener(e -> {
            DistinctiveServiceDesign customDesign = new DistinctiveServiceDesign();
            customDesign.setDesignName(designName.getText());
            customDesign.setCost((Double) costSpinner.getValue());
            customDesign.setId("CUST-" + System.currentTimeMillis()); // Unique timestamp ID
            
            if(!feedbackArea.getText().trim().isEmpty()) {
                customDesign.addFeedback(feedbackArea.getText());
            }
            
            currentDesign = customDesign;
            cardLayout.show(cardPanel, "PRICING");
        });

        panel.add(createBtn);
        return panel;
    }
    

    private JPanel createPricingPanel() {
        GradientPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255));
        panel.setLayout(new GridLayout(6, 2, 5, 5));

        JTextArea priceDetails = new JTextArea();
        priceDetails.setEditable(false);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton calcBtn = new JButton("Calculate Price");
        JButton nextBtn = new JButton("Next to Employee Selection");

        calcBtn.addActionListener(e -> {
            int clientId = (currentCustomer != null ? currentCustomer.getId() : currentClient.getId());

            if (currentDesign instanceof RegularServiceDesign) {
                RegularDesignPrice price = new RegularDesignPrice(
                    currentDesign.getCost(), 
                    10, 
                    currentDesign.getCost(),
                    3, 
                    14,
                    clientId
                );
                priceDetails.setText(String.format(
                    "Design ID: %s\nBase Price: $%.2f\nDiscount: %.0f%%\nFinal Price: $%.2f\nCustomer ID: %d\n\n",
                    currentDesign.getId(),
                    price.getStandardRate(), 
                    price.discount, 
                    price.calculateFinalPrice(), 
                    clientId
                ));
            } else {
                CreativeDesignPrice price = new CreativeDesignPrice(
                    currentDesign.getCost(),
                    15, 
                    0, 
                    false, 
                    0,
                    clientId
                );
                priceDetails.setText(String.format(
                    "Design ID: %s\nBase Price: $%.2f\nDiscount: %.0f%%\nFinal Price: $%.2f\nCustomer ID: %d\n\n",
                    currentDesign.getId(), 
                    price.basePrice, 
                    price.discount, 
                    price.calculateFinalPrice(), 
                    clientId
                ));
            }
        });

        nextBtn.addActionListener(e -> cardLayout.show(cardPanel, "SELECT_EMPLOYEE"));

        buttonPanel.add(calcBtn);
        buttonPanel.add(nextBtn);

        panel.add(new JScrollPane(priceDetails), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
   
    
    private JPanel createEmployeeSelectionPanel() {
        GradientPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255)); // Pink to Light Blue
        panel.setLayout(new GridLayout(6, 2, 5, 5));
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> employeeList = new JList<>(listModel);
        
        for(Employee emp : manager.getAllEmployeesFromDB()) {
        listModel.addElement(emp.getUsername() + " - ID: " + emp.getId() + " - Level: " + emp.getEmployeeLevel());
    }

        employeeList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                selectedEmployee = manager.getAllEmployeesFromDB().get(employeeList.getSelectedIndex());
                JOptionPane.showMessageDialog(mainFrame, 
                    "Selected Employee: " + selectedEmployee.getUsername() + 
                    "\nContact: " + selectedEmployee.getEmail());
                showCompletionDialog();
            }
        });

        panel.add(new JLabel("Select Preferred Employee:", SwingConstants.CENTER), BorderLayout.NORTH);
        panel.add(new JScrollPane(employeeList), BorderLayout.CENTER);
        return panel;
    }
    

    private JPanel createEmployeeLoginPanel() {
        GradientPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255)); // Pink to Light Blue
        panel.setLayout(new GridLayout(6, 2, 5, 5));
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> employeeList = new JList<>(listModel);
        
        for(Employee emp : manager.getAllEmployeesFromDB()) {
            listModel.addElement(emp.getUsername());
        }

        employeeList.addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()) {
                selectedEmployee = manager.getAllEmployeesFromDB().get(employeeList.getSelectedIndex());
                cardLayout.show(cardPanel, "EMPLOYEE");
            }
        });

        panel.add(new JScrollPane(employeeList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEmployeePanel() {
        GradientPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255)); // Pink to Light Blue
        panel.setLayout(new GridLayout(6, 2, 5, 5));
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        JTextField designNameField = new JTextField();
        JSpinner incomeSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 100.0));
        JButton addDesignBtn = new JButton("Add Design");
        
        inputPanel.add(new JLabel("Design Name:"));
        inputPanel.add(designNameField);
        inputPanel.add(new JLabel("Income:"));
        inputPanel.add(incomeSpinner);
        inputPanel.add(new JLabel());
        inputPanel.add(addDesignBtn);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);

        addDesignBtn.addActionListener(e -> {
            String designName = designNameField.getText();
            double income = (Double) incomeSpinner.getValue();
            
            if(!designName.isEmpty() && income > 0) {
                selectedEmployee.addDesign(designName, income);
                designNameField.setText("");
                incomeSpinner.setValue(0.0);
                updateEmployeeDetails(detailsArea);
                JOptionPane.showMessageDialog(mainFrame, "Design added successfully!");
                showCompletionDialog();
            }
        });

        JButton refreshBtn = new JButton("Refresh Details");
        refreshBtn.addActionListener(e -> updateEmployeeDetails(detailsArea));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        
        return panel;
    }
  private void loadTableNames(JComboBox<String> dropdown) {
    try (Connection conn = DBConnection.getConnection()) {
        DatabaseMetaData meta = conn.getMetaData();
        dropdown.removeAllItems();
        
        // Use the actual catalog from the connection
        String catalog = conn.getCatalog();
        System.out.println("Loading tables from catalog: " + catalog); // Debug
        
        ResultSet rs = meta.getTables(catalog, null, "%", new String[]{"TABLE"});
        
        while (rs.next()) {
            String tableName = rs.getString("TABLE_NAME");
            dropdown.addItem(tableName);
            System.out.println("Found table: " + tableName); // Debug
        }
        
        if (dropdown.getItemCount() == 0) {
            JOptionPane.showMessageDialog(null, 
                "No tables found in database: " + catalog,
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, 
            "Error loading tables: " + e.getMessage(),
            "Database Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

    private void loadTableData(String tableName, JTable table) {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT * FROM " + tableName;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        table.setModel(DbUtils.resultSetToTableModel(rs));
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null,
            "Error loading table data: " + e.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
private void openAddRowDialog(String tableName) {
    JDialog dialog = new JDialog(mainFrame, "Add New Row - " + tableName, true);
    dialog.setLayout(new GridLayout(0, 2, 10, 10));

    List<JTextField> fieldInputs = new ArrayList<>();
    List<String> columnNames = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection()) {
        DatabaseMetaData metaData = conn.getMetaData();
        
        ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName, null);
        
    
        Set<String> uniqueColumns = new HashSet<>();
        
        while (rs.next()) {
            String columnName = rs.getString("COLUMN_NAME");
          
            if (uniqueColumns.add(columnName) && !columnName.startsWith("_")) {
                columnNames.add(columnName);
                dialog.add(new JLabel(columnName + ":"));
                JTextField input = new JTextField();
                dialog.add(input);
                fieldInputs.add(input);
            }
        }
        rs.close();
        
        if (columnNames.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "No columns found for table: " + tableName, 
                "Error", JOptionPane.ERROR_MESSAGE);
            dialog.dispose();
            return;
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(dialog, "Error loading columns: " + ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        dialog.dispose();
        return;
    }

    JButton saveBtn = new JButton("Save");
    saveBtn.addActionListener(e -> {
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        StringBuilder placeholders = new StringBuilder(" VALUES (");

        for (int i = 0; i < columnNames.size(); i++) {
            sql.append(columnNames.get(i));
            placeholders.append("?");
            if (i < columnNames.size() - 1) {
                sql.append(", ");
                placeholders.append(", ");
            }
        }
        sql.append(")").append(placeholders).append(")");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < fieldInputs.size(); i++) {
                String value = fieldInputs.get(i).getText();
              
                pstmt.setString(i + 1, value.isEmpty() ? null : value);
            }

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(dialog, "Row inserted successfully!");
                dialog.dispose();
                loadTableData(tableName, dataTable);
                askForAnotherService("Row inserted");
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to insert row", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Database error: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    dialog.add(new JLabel()); // Empty cell for alignment
    dialog.add(saveBtn);

    dialog.pack();
    dialog.setLocationRelativeTo(mainFrame);
    dialog.setVisible(true);
}

private JPanel createAdminDashboardPanel() {
    // Create main panel with gradient background
    JPanel panel = new GradientPanel(new Color(255, 204, 204), new Color(204, 255, 255));
    panel.setLayout(new BorderLayout());

    // Top panel with table selection
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    tableDropdown = new JComboBox<>();
    JButton refreshBtn = new JButton("🔁 Refresh");
    JButton loadBtn = new JButton("📂 Load Tables");
    
    topPanel.add(new JLabel("Select Table:"));
    topPanel.add(tableDropdown);
    topPanel.add(refreshBtn);
    topPanel.add(loadBtn);

    // Table with scroll pane
    dataTable = new JTable();
    JScrollPane scrollPane = new JScrollPane(dataTable);

    // CRUD buttons panel
    JPanel crudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JButton addBtn = new JButton("➕ Add");
    JButton editBtn = new JButton("✏️ Edit");
    JButton deleteBtn = new JButton("🗑️ Delete");

    crudPanel.add(addBtn);
    crudPanel.add(editBtn);
    crudPanel.add(deleteBtn);

    // Add components to main panel
    panel.add(topPanel, BorderLayout.NORTH);
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(crudPanel, BorderLayout.SOUTH);

    // Load tables initially
    loadTableNames(tableDropdown);

    // Event listeners
    loadBtn.addActionListener(e -> loadTableNames(tableDropdown));
    
    tableDropdown.addActionListener(e -> {
    String selectedTable = (String) tableDropdown.getSelectedItem();
    if (selectedTable != null && !selectedTable.isEmpty()) {
        loadTableData(selectedTable, dataTable);
    }
});

    refreshBtn.addActionListener(e -> {
        String selectedTable = (String) tableDropdown.getSelectedItem();
        if (selectedTable != null) {
            loadTableData(selectedTable, dataTable);
        }
    });

    addBtn.addActionListener(e -> {
        String selectedTable = (String) tableDropdown.getSelectedItem();
        if (selectedTable != null) {
            openAddRowDialog(selectedTable);
        }
    });
    
    loadBtn.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Data File");
        
        // Set file filters
        FileNameExtensionFilter csvFilter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
        FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Excel Files (*.xls, *.xlsx)", "xls", "xlsx");
        fileChooser.addChoosableFileFilter(csvFilter);
        fileChooser.addChoosableFileFilter(excelFilter);
        fileChooser.setFileFilter(csvFilter); // Set CSV as default

        int returnValue = fileChooser.showOpenDialog(mainFrame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                loadDataFromFile(selectedFile);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame,
                    "Error loading file: " + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    editBtn.addActionListener(e -> {
        String selectedTable = (String) tableDropdown.getSelectedItem();
        if (selectedTable != null && dataTable.getSelectedRow() != -1) {
            openEditRowDialog(selectedTable, dataTable);
        } else {
            JOptionPane.showMessageDialog(mainFrame, 
                "Please select a table and a row to edit.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
        }
    });

    deleteBtn.addActionListener(e -> {
        String tableName = (String) tableDropdown.getSelectedItem();
        int selectedRow = dataTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, 
                "Please select a row to delete.",
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
            "Are you sure you want to delete this row?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                String primaryKey = dataTable.getColumnName(0);
                Object keyValue = dataTable.getValueAt(selectedRow, 0);
                
                String sql = "DELETE FROM " + tableName + " WHERE " + primaryKey + " = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setObject(1, keyValue);
                
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Row deleted successfully.");
                    loadTableData(tableName, dataTable);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Error deleting row: " + ex.getMessage(),
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    });

    return panel;
}

private void loadDataFromFile(File file) throws IOException, SQLException {
    try (BufferedReader br = new BufferedReader(new FileReader(file));
         Connection conn = DBConnection.getConnection()) {
        
        String line = br.readLine(); // Read header
        if (line == null) return;
        
        String[] headers = line.split(",");
        String tableName = (String) tableDropdown.getSelectedItem();
        
        // Clear existing data if needed
        if (JOptionPane.showConfirmDialog(mainFrame, 
            "Clear existing data before import?", 
            "Import Option", 
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM " + tableName);
            }
        }
        
        // Prepare insert statement
        String sql = "INSERT INTO " + tableName + " (" + String.join(",", headers) + ") VALUES (" + 
                     String.join(",", Collections.nCopies(headers.length, "?")) + ")";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                
                for (int i = 0; i < values.length; i++) {
                    pstmt.setString(i + 1, values[i].trim());
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
        
        // Refresh table view
        loadTableData(tableName, dataTable);
        JOptionPane.showMessageDialog(mainFrame, 
            "Data imported successfully to " + tableName,
            "Import Complete",
            JOptionPane.INFORMATION_MESSAGE);
    }
}

private void openEditRowDialog(String tableName, JTable table) {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) return;

    JDialog dialog = new JDialog(mainFrame, "Edit Row - " + tableName, true);
    dialog.setLayout(new GridLayout(0, 2, 10, 10));

    try (Connection conn = DBConnection.getConnection()) {
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet columns = metaData.getColumns(null, null, tableName, null);
        
        List<JTextField> fields = new ArrayList<>();
        String primaryKey = table.getColumnName(0);
        Object primaryValue = table.getValueAt(selectedRow, 0);

        // First collect all column names
        List<String> columnNames = new ArrayList<>();
        while (columns.next()) {
            columnNames.add(columns.getString("COLUMN_NAME"));
        }
        columns.close(); // Close and reopen to reset the result set
        
        // Reopen the result set
        columns = metaData.getColumns(null, null, tableName, null);
        
        int columnIndex = 0;
        while (columns.next()) {
            String colName = columns.getString("COLUMN_NAME");
            // Only process columns that exist in both the table and metadata
            if (columnIndex < table.getColumnCount()) {
                Object colValue = table.getValueAt(selectedRow, columnIndex);
                
                dialog.add(new JLabel(colName + ":"));
                JTextField field = new JTextField(colValue != null ? colValue.toString() : "");
                dialog.add(field);
                fields.add(field);
                columnIndex++;
            }
        }

        JButton saveBtn = new JButton("Save Changes");
        saveBtn.addActionListener(e -> {
            try {
                StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
                for (int i = 0; i < fields.size(); i++) {
                    sql.append(table.getColumnName(i)).append(" = ?");
                    if (i < fields.size() - 1) sql.append(", ");
                }
                sql.append(" WHERE ").append(primaryKey).append(" = ?");

                PreparedStatement pstmt = conn.prepareStatement(sql.toString());
                for (int i = 0; i < fields.size(); i++) {
                    pstmt.setString(i + 1, fields.get(i).getText());
                }
                pstmt.setObject(fields.size() + 1, primaryValue);

                int rowsUpdated = pstmt.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(dialog, "Row updated successfully!");
                    loadTableData(tableName, table);
                    dialog.dispose();
                    askForAnotherService("Row updated");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Error updating row: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(new JLabel());
        dialog.add(saveBtn);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(mainFrame,
            "Error loading table structure: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void loadTableNames2(JComboBox<String> dropdown) {
    try (Connection conn = DBConnection.getConnection()) {
        dropdown.removeAllItems();
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});
        
        while (tables.next()) {
            dropdown.addItem(tables.getString("TABLE_NAME"));
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(mainFrame,
            "Error loading tables: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void loadTableData2(String tableName, JTable table) {
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT * FROM " + tableName;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        table.setModel(DbUtils.resultSetToTableModel(rs));
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(mainFrame,
            "Error loading table data: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

    private void updateEmployeeDetails(JTextArea detailsArea) {
        StringBuilder sb = new StringBuilder();
        sb.append("Employee ID: ").append(selectedEmployee.getId()).append("\n")
        .append("Name: ").append(selectedEmployee.getUsername()).append("\n")
        .append("Total Income: $").append(selectedEmployee.getTotalIncome()).append("\n")
        .append("Designs Handled:\n");
        
        for(String design : selectedEmployee.getDesignsHandled()) {
            sb.append("- ").append(design).append("\n");
        }
        
        detailsArea.setText(sb.toString());
    }

    private void showCompletionDialog() {
        int choice = JOptionPane.showConfirmDialog(null,
            "Thank you for using our service!\nWould you like to start another process?",
            "Process Complete",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            resetApplication();
            cardLayout.show(cardPanel, "WELCOME");
        } else {
            mainFrame.dispose();
        }
    }
    
private void askForAnotherService(String operation) {
    int choice = JOptionPane.showConfirmDialog(mainFrame,
        operation + " successfully! Would you like to perform another service?",
        "Service Completed",
        JOptionPane.YES_NO_OPTION);
    
    if (choice == JOptionPane.NO_OPTION) {
        mainFrame.dispose(); 
    }
}
    class GradientPanel extends JPanel {
    private Color startColor;
    private Color endColor;

    public GradientPanel(Color startColor, Color endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        setOpaque(false); // Ensure transparency
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        GradientPaint gradient = new GradientPaint(0, 0, startColor, width, height, endColor);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        }
}


    private void resetApplication() {
        currentClient = null;
        currentDesign = null;
        selectedEmployee = null;
        currentCustomer = null;
    }
    


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interiordesignproject1());
    }
}
class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/InteriorDesignOffice";
    private static final String USER = "root";
    private static final String PASS = "0568334173";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

