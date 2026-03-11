/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interiordesignproject1;
import java.sql.*;
/**
 *
 * @author batoolsaeed
 */
public abstract class Price {
    
    protected double basePrice;
    protected double discount;
    protected int id;

    public Price(double basePrice, double discount , int id) {
        this.basePrice = basePrice;
        this.discount = discount;
        this.id = id;
    }
    
    public double getBasePrice() {
        return basePrice;
    }

    public double getDiscount() {
        return discount;
    }

    public int getId() {
        return id;
    }


    public abstract double calculateFinalPrice();

    public void applyDiscount(double newDiscount) {
        this.discount = newDiscount;
    }
    // Method to display price details, including base price, discount, and final price
    public void displayPriceDetails() {
        System.out.println("Base Price: $" + basePrice);
        System.out.println("Discount: " + discount + "%");
        System.out.println("Final Price: $" + calculateFinalPrice());
        System.out.println("ID: " + id);
    }
    
    
    
    // Insert a RegularDesignPrice into the database
    public static void insertRegularPrice(RegularDesignPrice price) throws SQLException {
    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);  // Start transaction
        
        // Insert to Price table
        try (PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO Price (price_id, basePrice, discount, userID) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, price.getId());
            pstmt.setDouble(2, price.getBasePrice());
            pstmt.setDouble(3, price.getDiscount());
            pstmt.setInt(4, price.getId());
            pstmt.executeUpdate();
        }
        
        // Insert to RegularDesignPrice
        try (PreparedStatement pstmt = conn.prepareStatement(
            "INSERT INTO RegularDesignPrice VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, price.getId());
            pstmt.setDouble(2, price.getStandardRate());
            pstmt.setInt(3, price.getStandardDeliveryTime());
            pstmt.setInt(4, price.getRevisionsAllowed());
            pstmt.executeUpdate();
        }
        
        conn.commit();  // Commit both inserts
    }
}
    
    public static RegularDesignPrice loadRegularPriceFromDatabase(int priceId) {
        RegularDesignPrice price = null;

        String selectQuery = "SELECT * FROM Price JOIN RegularDesignPrice ON Price.price_id = RegularDesignPrice.price_id WHERE Price.price_id = ?";

            try (Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(selectQuery)) {
                
                stmt.setInt(1, priceId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    double basePrice = rs.getDouble("basePrice");
                    double discount = rs.getDouble("discount");
                    double standardRate = rs.getDouble("standardRate");
                    int deliveryTime = rs.getInt("standardDeliveryTime");
                    int revisions = rs.getInt("revisionsAllowed");

                    price = new RegularDesignPrice(basePrice, discount, standardRate, deliveryTime, revisions, priceId);
                }
            }
            catch(SQLException ex){
                System.out.println("Error " + ex.getMessage());
            }
        return price;
    }

    
    // Insert a CreativeDesignPrice into the database
    public static void insertCreativePrice(CreativeDesignPrice price){
        String insertPrice = "INSERT INTO Price (price_id, basePrice, discount, userID) VALUES (?, ?, ?, ?)";
        
        String insertCreativeDesignPrice = "INSERT INTO CreativeDesignPrice (price_id, premiumSupport, extraFeatures, customFeaturesCost) VALUES (?, ?, ?, ?)";
        
        try{
            Connection connection = DBConnection.getConnection();
            
            // Insert into Price table
            PreparedStatement insertPriceStmt = connection.prepareStatement(insertPrice);
            insertPriceStmt.setInt(1, price.getId()); // for price_id
            insertPriceStmt.setDouble(2, price.getBasePrice());
            insertPriceStmt.setDouble(3, price.getDiscount());
            insertPriceStmt.setInt(4, price.getId()); // for userID, assuming price_id = userID
            insertPriceStmt.executeUpdate();
            
            // Insert into CreativeDesignPrice table
            PreparedStatement insertCreativeDesignStmt = connection.prepareStatement(insertCreativeDesignPrice);
            insertCreativeDesignStmt.setInt(1, price.getId());
            insertCreativeDesignStmt.setBoolean(2, price.hasPremiumSupport());
            insertCreativeDesignStmt.setDouble(3, price.getExtraFees());
            insertCreativeDesignStmt.setDouble(4, price.getCustomFeaturesCost());
            insertCreativeDesignStmt.executeUpdate(); 
            
            connection.close();
        }
        catch(SQLException ex){
            System.out.println("Error inserting into CreativeDesignPrice " + ex.getMessage());
        }
    }
    
   
    public static CreativeDesignPrice loadCreativePriceFromDatabase(int priceId) {
        CreativeDesignPrice price = null;

        String selectQuery = "SELECT * FROM Price JOIN CreativeDesignPrice ON Price.price_id = CreativeDesignPrice.price_id WHERE Price.price_id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(selectQuery)
        ) {
            stmt.setInt(1, priceId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double basePrice = rs.getDouble("basePrice");
                double discount = rs.getDouble("discount");
                boolean premiumSupport = rs.getBoolean("premiumSupport");
                double extraFeatures = rs.getDouble("extraFeatures");
                double customFeaturesCost = rs.getDouble("customFeaturesCost");

                price = new CreativeDesignPrice(basePrice, discount, extraFeatures, premiumSupport, customFeaturesCost, priceId);
            }
        }
        catch (SQLException ex) {
            System.out.println("Error " + ex.getMessage());
        }
        return price;
    }

}


class RegularDesignPrice extends Price {
    private double standardRate;
    private int revisionsAllowed;
    private final int standardDeliveryTime; // in days

    public RegularDesignPrice(double basePrice, double discount, double standardRate, int revisionsAllowed, int standardDeliveryTime , int id) {
        super(basePrice, discount , id);
        this.standardRate = standardRate;
        this.revisionsAllowed = revisionsAllowed;
        this.standardDeliveryTime = standardDeliveryTime;
    }

    public double getStandardRate() {
        return standardRate;
    }

    public void setStandardRate(double standardRate) {
        this.standardRate = standardRate;
    }

    public int getRevisionsAllowed() {
        return revisionsAllowed;
    }

    public void setRevisionsAllowed(int revisionsAllowed) {
        this.revisionsAllowed = revisionsAllowed;
    }
    
    public int getStandardDeliveryTime() {
        return standardDeliveryTime;
    }

    public int calculateDeliveryTime(int usedRevisions) {
        return standardDeliveryTime + (usedRevisions > revisionsAllowed ? 2 : 0); // Add 2 days for extra revisions
    }

    public boolean isRevisionsExceeded(int usedRevisions) {
        return usedRevisions > revisionsAllowed;
    }

    @Override
    public double calculateFinalPrice() {
        // Calculate the final price by applying the discount to the standard rate
        return standardRate - (standardRate * discount / 100);
    }
}


class CreativeDesignPrice extends Price {
    private double extraFees;
    private boolean premiumSupport;
    private double customFeaturesCost;

    public CreativeDesignPrice(double basePrice, double discount, double extraFees, boolean premiumSupport, double customFeaturesCost ,int id) {
        super(basePrice, discount , id); // Initialize base price and discount using the parent class constructor
        this.extraFees = extraFees;
        this.premiumSupport = premiumSupport;
        this.customFeaturesCost = customFeaturesCost;
    }

    public double getExtraFees() {
        return extraFees;
    }

    public void setExtraFees(double extraFees) {
        this.extraFees = extraFees;
    }

    public boolean hasPremiumSupport() {
        return premiumSupport;
    }

    public void setPremiumSupport(boolean premiumSupport) {
        this.premiumSupport = premiumSupport;
    }

    public double calculatePremiumCost() {
        return premiumSupport ? 100 : 0; // Add $100 for premium support
    }

    public void addCustomFeature(double featureCost) {
        this.customFeaturesCost += featureCost;
    }
    
    public double getCustomFeaturesCost() {
        return customFeaturesCost;
    }


    @Override
    public double calculateFinalPrice() {
         // Calculate final price considering base price, discount, extra fees, custom features, and premium support
        return (basePrice - (basePrice * discount / 100)) + extraFees + customFeaturesCost + calculatePremiumCost();
    } 
}