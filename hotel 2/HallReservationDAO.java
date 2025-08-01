import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HallReservationDAO {

    // Reserve a hall with a selected package and date
    public static boolean reserveHall(int userId, int packageId, Date reservationDate) {
        String query = "INSERT INTO hall_reservations (user_id, package_id, reservation_date) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, packageId);
            stmt.setDate(3, reservationDate);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // View all hall reservations for a specific date
    public static List<String> viewAppointments(Date date) {
        List<String> appointments = new ArrayList<>();
        String query = "SELECT u.username, hp.package_name, hr.reservation_date " +
                       "FROM hall_reservations hr " +
                       "JOIN users u ON hr.user_id = u.user_id " +
                       "JOIN hall_packages hp ON hr.package_id = hp.package_id " +
                       "WHERE hr.reservation_date = ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String username = rs.getString("username");
                String packageName = rs.getString("package_name");
                Date reservationDate = rs.getDate("reservation_date");
                appointments.add(username + " reserved " + packageName + " on " + reservationDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    // Delete a hall reservation by reservation ID
    public static boolean deleteAppointment(int reservationId) {
        String query = "DELETE FROM hall_reservations WHERE reservation_id = ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reservationId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
