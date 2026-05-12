package db;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DAO {

    // ==================== USER ====================

    public static User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setUsername(rs.getString("username"));
                u.setFullName(rs.getString("full_name"));
                u.setBirthDate(rs.getDate("birth_date"));
                u.setGender(rs.getString("gender"));
                u.setHeightCm(rs.getFloat("height_cm"));
                return u;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static boolean register(String username, String password, String fullName,
                                   String birthDate, String gender, float height) {
        String sql = "INSERT INTO users (username, password, full_name, birth_date, gender, height_cm) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, birthDate);
            ps.setString(5, gender);
            ps.setFloat(6, height);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    /** Cập nhật chiều cao mặc định trong bảng users */
    public static boolean updateUserHeight(int userId, float heightCm) {
        String sql = "UPDATE users SET height_cm=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setFloat(1, heightCm);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ==================== WEIGHT ====================

    public static boolean addWeight(int userId, float weight, float bmi, String date, String note) {
        String sql = "INSERT INTO weight_log (user_id, weight_kg, bmi, log_date, note) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setFloat(2, weight);
            ps.setFloat(3, bmi);
            ps.setString(4, date);
            ps.setString(5, note);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean deleteWeight(int id) {
        String sql = "DELETE FROM weight_log WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static List<WeightLog> getWeightHistory(int userId) {
        List<WeightLog> list = new ArrayList<>();
        String sql = "SELECT * FROM weight_log WHERE user_id=? ORDER BY log_date DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                WeightLog w = new WeightLog();
                w.setId(rs.getInt("id"));
                w.setUserId(userId);
                w.setWeightKg(rs.getFloat("weight_kg"));
                w.setBmi(rs.getFloat("bmi"));
                w.setLogDate(rs.getDate("log_date"));
                w.setNote(rs.getString("note"));
                list.add(w);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static WeightLog getLatestWeight(int userId) {
        String sql = "SELECT * FROM weight_log WHERE user_id=? ORDER BY log_date DESC LIMIT 1";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                WeightLog w = new WeightLog();
                w.setWeightKg(rs.getFloat("weight_kg"));
                w.setBmi(rs.getFloat("bmi"));
                w.setLogDate(rs.getDate("log_date"));
                w.setNote(rs.getString("note"));
                return w;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ==================== WATER ====================

    public static boolean addWater(int userId, int amountMl) {
        String sql = "INSERT INTO water_log (user_id, amount_ml, log_time) VALUES (?,?,NOW())";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, amountMl);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean deleteWater(int id) {
        String sql = "DELETE FROM water_log WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static int getTodayWater(int userId) {
        String sql = "SELECT COALESCE(SUM(amount_ml),0) AS total FROM water_log WHERE user_id=? AND DATE(log_time)=CURDATE()";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static List<WaterLog> getTodayWaterLogs(int userId) {
        List<WaterLog> list = new ArrayList<>();
        String sql = "SELECT * FROM water_log WHERE user_id=? AND DATE(log_time)=CURDATE() ORDER BY log_time DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                WaterLog wl = new WaterLog();
                wl.setId(rs.getInt("id"));
                wl.setAmountMl(rs.getInt("amount_ml"));
                wl.setLogTime(rs.getTimestamp("log_time"));
                list.add(wl);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ==================== SLEEP ====================

    public static boolean addSleep(int userId, String sleepTime, String wakeTime, String quality, String note) {
        String sql = "INSERT INTO sleep_log (user_id, sleep_time, wake_time, quality, note) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, sleepTime);
            ps.setString(3, wakeTime);
            ps.setString(4, quality);
            ps.setString(5, note);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean deleteSleep(int id) {
        String sql = "DELETE FROM sleep_log WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static List<SleepLog> getSleepHistory(int userId) {
        List<SleepLog> list = new ArrayList<>();
        String sql = "SELECT * FROM sleep_log WHERE user_id=? ORDER BY sleep_time DESC LIMIT 14";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SleepLog sl = new SleepLog();
                sl.setId(rs.getInt("id"));
                sl.setUserId(userId);
                sl.setSleepTime(rs.getTimestamp("sleep_time"));
                sl.setWakeTime(rs.getTimestamp("wake_time"));
                sl.setQuality(rs.getString("quality"));
                sl.setNote(rs.getString("note"));
                list.add(sl);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static SleepLog getLatestSleep(int userId) {
        String sql = "SELECT * FROM sleep_log WHERE user_id=? ORDER BY sleep_time DESC LIMIT 1";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SleepLog sl = new SleepLog();
                sl.setSleepTime(rs.getTimestamp("sleep_time"));
                sl.setWakeTime(rs.getTimestamp("wake_time"));
                sl.setQuality(rs.getString("quality"));
                return sl;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ==================== HEIGHT ====================

    public static boolean addHeight(int userId, float heightCm, String date, String note) {
        String sql = "INSERT INTO height_log (user_id, height_cm, log_date, note) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setFloat(2, heightCm);
            ps.setString(3, date);
            ps.setString(4, note);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean deleteHeight(int id) {
        String sql = "DELETE FROM height_log WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static List<HeightLog> getHeightHistory(int userId) {
        List<HeightLog> list = new ArrayList<>();
        String sql = "SELECT * FROM height_log WHERE user_id=? ORDER BY log_date DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HeightLog h = new HeightLog();
                h.setId(rs.getInt("id"));
                h.setUserId(userId);
                h.setHeightCm(rs.getFloat("height_cm"));
                h.setLogDate(rs.getDate("log_date"));
                h.setNote(rs.getString("note"));
                list.add(h);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static HeightLog getLatestHeight(int userId) {
        String sql = "SELECT * FROM height_log WHERE user_id=? ORDER BY log_date DESC LIMIT 1";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HeightLog h = new HeightLog();
                h.setId(rs.getInt("id"));
                h.setHeightCm(rs.getFloat("height_cm"));
                h.setLogDate(rs.getDate("log_date"));
                h.setNote(rs.getString("note"));
                return h;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ==================== GOALS ====================

    public static boolean addGoal(int userId, String goalType, float targetValue, String deadline) {
        String sql = "INSERT INTO goals (user_id, goal_type, target_value, deadline, status) VALUES (?,?,?,?,'Đang thực hiện')";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, goalType);
            ps.setFloat(3, targetValue);
            ps.setString(4, deadline);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean deleteGoal(int id) {
        String sql = "DELETE FROM goals WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static boolean updateGoalStatus(int id, String status) {
        String sql = "UPDATE goals SET status=? WHERE id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static List<model.Goal> getGoals(int userId) {
        List<model.Goal> list = new ArrayList<>();
        String sql = "SELECT * FROM goals WHERE user_id=? ORDER BY FIELD(status,'Đang thực hiện','Đạt được','Thất bại'), deadline ASC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.Goal g = new model.Goal();
                g.setId(rs.getInt("id"));
                g.setUserId(userId);
                g.setGoalType(rs.getString("goal_type"));
                g.setTargetValue(rs.getFloat("target_value"));
                g.setDeadline(rs.getDate("deadline"));
                g.setStatus(rs.getString("status"));
                list.add(g);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static int countGoalsByStatus(int userId, String status) {
        String sql = "SELECT COUNT(*) AS cnt FROM goals WHERE user_id=? AND status=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cnt");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
