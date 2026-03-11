package interiordesignproject1;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Design {
    
    public enum DesignType {
        CLASSIC("DES-CL"),
        MODERN("DES-MO"),
        RUSTIC("DES-RU");

        private final String typeId;

        DesignType(String typeId) {
            this.typeId = typeId;
        }

        public String getTypeId() {
            return typeId;
        }
    }

    // Instance fields
    private String id;
    private String designName;
    private double cost;
    private LocalDate creationDate;
    private LocalDate lastUpdatedDate;
    private String description;
    private List<String> features;
    private DesignType designType;
    private static int designCounter = 1000;

    public Design() {
        this.creationDate = LocalDate.now();
        this.lastUpdatedDate = LocalDate.now();
        this.features = new ArrayList<>();
        this.id = "DES-" + designCounter++;
    }

    // Database operations
    public void saveToDatabase(int userId, int priceId) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Save to Design table
            String designSql = "INSERT INTO Design (design_id, designName, typeid, designCounter, "
                             + "designType, features, des_cription, creationDate, lastUpdatedDate, "
                             + "cost, userID, priceID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(designSql)) {
                pstmt.setString(1, this.id);
                pstmt.setString(2, this.designName);
                pstmt.setString(3, this.designType.getTypeId());
                pstmt.setInt(4, designCounter);
                pstmt.setString(5, mapDesignTypeToDB(this.designType));
                pstmt.setString(6, String.join(",", features));
                pstmt.setString(7, description);
                pstmt.setDate(8, Date.valueOf(creationDate));
                pstmt.setDate(9, Date.valueOf(lastUpdatedDate));
                pstmt.setDouble(10, cost);
                pstmt.setInt(11, userId);
                pstmt.setInt(12, priceId);
                pstmt.executeUpdate();
            }

            // Save to subclass table if needed
            if (this instanceof RegularServiceDesign) {
                ((RegularServiceDesign) this).saveSpecificFields(conn);
            } else if (this instanceof DistinctiveServiceDesign) {
                ((DistinctiveServiceDesign) this).saveSpecificFields(conn);
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.close();
        }
    }

    public static Design loadFromDatabase(String designId) throws SQLException {
        Design design = null;
        String sql = "SELECT * FROM Design d "
                   + "LEFT JOIN RegularServiceDesign rsd ON d.design_id = rsd.design_id "
                   + "LEFT JOIN DistinctiveServiceDesign dsd ON d.design_id = dsd.design_id "
                   + "WHERE d.design_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, designId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create appropriate subclass instance
                String designType = rs.getString("designType");
                if (rs.getString("rsd.design_id") != null) {
                    design = new RegularServiceDesign();
                    ((RegularServiceDesign) design).setServiceDetails(rs.getString("serviceDetails"));
                    ((RegularServiceDesign) design).setNumberOfOrders(rs.getInt("numberOfOrder"));
                } else if (rs.getString("dsd.design_id") != null) {
                    DistinctiveServiceDesign dsd = new DistinctiveServiceDesign();
                    dsd.setDistinctiveFeatures(rs.getString("distinctiveFeatures"));
                    String feedback = rs.getString("clientFeedback");
                    if (feedback != null) {
                        for (String fb : feedback.split(";")) {
                            dsd.addFeedback(fb.trim());
                        }
                    }
                    design = dsd;
                } else {
                    design = new Design();
                }

                // Set common fields
                design.id = designId;
                design.designName = rs.getString("designName");
                design.designType = mapDBTypeToDesignType(designType);
                design.cost = rs.getDouble("cost");
                design.creationDate = rs.getDate("creationDate").toLocalDate();
                design.lastUpdatedDate = rs.getDate("lastUpdatedDate").toLocalDate();
                design.description = rs.getString("des_cription");
                
                // Parse features
                String featuresStr = rs.getString("features");
                if (featuresStr != null) {
                    design.features = new ArrayList<>();
                    for (String feature : featuresStr.split(",")) {
                        design.features.add(feature.trim());
                    }
                }
            }
        }
        return design;
    }

    // Helper methods
    private String mapDesignTypeToDB(DesignType type) {
        switch (type) {
            case CLASSIC: return "Classic Design";
            case MODERN: return "Modern Design";
            case RUSTIC: return "Rustic Design";
            default: return "Custom Design";
        }
    }

    private static DesignType mapDBTypeToDesignType(String dbType) {
        if (dbType == null) return null;
        switch (dbType) {
            case "Classic Design": return DesignType.CLASSIC;
            case "Modern Design": return DesignType.MODERN;
            case "Rustic Design": return DesignType.RUSTIC;
            default: return null;
        }
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { 
        this.id = id;
        updateLastUpdatedDate();
    }

    public DesignType getDesignType() { return designType; }
    public void setDesignType(DesignType designType) {
        this.designType = designType;
        updateDescriptionAndFeatures(designType);
        updateLastUpdatedDate();
    }

    public String getDesignName() { return designName; }
    public void setDesignName(String designName) {
        this.designName = designName;
        updateLastUpdatedDate();
    }

    public double getCost() { return cost; }
    public void setCost(double cost) {
        this.cost = cost;
        updateLastUpdatedDate();
    }

    public LocalDate getCreationDate() { return creationDate; }
    public LocalDate getLastUpdatedDate() { return lastUpdatedDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
        updateLastUpdatedDate();
    }

    public List<String> getFeatures() { return new ArrayList<>(features); }

    protected void updateLastUpdatedDate() {
        this.lastUpdatedDate = LocalDate.now();
    }

    private void updateDescriptionAndFeatures(DesignType type) {
        features.clear();
        switch (type) {
            case CLASSIC:
                description = "Timeless elegant design";
                features.add("Neutral colors");
                features.add("Symmetrical layouts");
                features.add("Classic furniture");
                break;
            case MODERN:
                description = "Sleek contemporary design";
                features.add("Clean lines");
                features.add("Open spaces");
                features.add("Modern furniture");
                break;
            case RUSTIC:
                description = "Natural countryside design";
                features.add("Wooden elements");
                features.add("Earthy tones");
                features.add("Natural materials");
                break;
            default:
                description = "Custom design";
        }
    }
}

class RegularServiceDesign extends Design {
    private String serviceDetails;
    private int numberOfOrders;

    void saveSpecificFields(Connection conn) throws SQLException {
        String sql = "INSERT INTO RegularServiceDesign (design_id, serviceDetails, numberOfOrder) "
                   + "VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, getId());
            pstmt.setString(2, serviceDetails);
            pstmt.setInt(3, numberOfOrders);
            pstmt.executeUpdate();
        }
    }

    // Getters and setters
    public String getServiceDetails() { return serviceDetails; }
    public void setServiceDetails(String serviceDetails) {
        this.serviceDetails = serviceDetails;
        updateLastUpdatedDate();
    }

    public int getNumberOfOrders() { return numberOfOrders; }
    public void setNumberOfOrders(int numberOfOrders) {
        this.numberOfOrders = Math.max(numberOfOrders, 0);
        updateLastUpdatedDate();
    }
}

class DistinctiveServiceDesign extends Design {
    private String distinctiveFeatures;
    private List<String> clientFeedback = new ArrayList<>();

    void saveSpecificFields(Connection conn) throws SQLException {
        String sql = "INSERT INTO DistinctiveServiceDesign (design_id, distinctiveFeatures, clientFeedback) "
                   + "VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, getId());
            pstmt.setString(2, distinctiveFeatures);
            pstmt.setString(3, String.join(";", clientFeedback));
            pstmt.executeUpdate();
        }
    }

    // Additional methods
    public void addFeedback(String feedback) {
        if (feedback != null && !feedback.trim().isEmpty()) {
            clientFeedback.add(feedback);
            updateLastUpdatedDate();
        }
    }

    // Getters and setters
    public List<String> getClientFeedback() { return new ArrayList<>(clientFeedback); }
    public String getDistinctiveFeatures() { return distinctiveFeatures; }
    public void setDistinctiveFeatures(String distinctiveFeatures) {
        this.distinctiveFeatures = distinctiveFeatures;
        updateLastUpdatedDate();
    }
}