import java.sql.*;

public class RoomDAO {
    
    // Check if a room is available
    public static boolean checkRoomAvailability(String roomNumber) {
        String query = "SELECT status FROM rooms WHERE room_number = ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                return status.equals("available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Book a room (update its status to 'reserved')
    public static boolean bookRoom(String roomNumber) {
        String query = "UPDATE rooms SET status = 'reserved' WHERE room_number = ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, roomNumber);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Reset a room's status to 'available'
    public static boolean resetRoom(String roomNumber) {
        String query = "UPDATE rooms SET status = 'available' WHERE room_number = ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, roomNumber);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
