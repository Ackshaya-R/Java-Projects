import java.sql.*;
import java.util.Scanner;

public class Healthdata {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Collect User Input
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Sodium Level: ");
        float sodium = scanner.nextFloat();
        System.out.print("Enter Potassium Level: ");
        float potassium = scanner.nextFloat();
        System.out.print("Enter Lactose Level: ");
        float lactose = scanner.nextFloat();

        // Calculate values
        float glucose = (sodium * 1.5f) + (potassium * 2) + (lactose * 0.5f);
        float hemoglobin = (sodium * 0.8f) + (potassium * 1.2f);
        float cholesterol = (sodium * 2) + (lactose * 1.5f);

        // Save to MySQL Database
        saveToDatabase(id, name, sodium, potassium, lactose, glucose, hemoglobin, cholesterol);

        // Show Results
        System.out.println("\nResults:");
        System.out.println("🔹 Blood Glucose Level: " + glucose + " mg/dL");
        System.out.println("🔹 Hemoglobin Level: " + hemoglobin + " g/dL");
        System.out.println("🔹 Cholesterol Level: " + cholesterol + " mg/dL");

        scanner.close();
    }

    public static void saveToDatabase(int id, String name, float sodium, float potassium, float lactose, float glucose, float hemoglobin, float cholesterol) {
        String url = "jdbc:mysql://localhost:3306/health_data?serverTimezone=UTC";
        String user = "root";  // Change this to your MySQL username
        String password = "1410";  // Change this to your MySQL password

        try {
            // ✅ Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to Database
            Connection conn = DriverManager.getConnection(url, "root","1410");
            String query = "INSERT INTO health_datas (id, name, sodium, potassium, lactose, glucose, hemoglobin, cholesterol) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setFloat(3, sodium);
            stmt.setFloat(4, potassium);
            stmt.setFloat(5, lactose);
            stmt.setFloat(6, glucose);
            stmt.setFloat(7, hemoglobin);
            stmt.setFloat(8, cholesterol);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✅ Data successfully inserted into MySQL!");
            }

            stmt.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("⚠ MySQL JDBC Driver not found! Make sure you added the JAR.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("⚠ Database connection error!");
            e.printStackTrace();
        }
    }
}
