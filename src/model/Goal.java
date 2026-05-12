package model;

import java.sql.Date;

public class Goal {
    private int id;
    private int userId;
    private String goalType;
    private float targetValue;
    private Date deadline;
    private String status;

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }

    public float getTargetValue() { return targetValue; }
    public void setTargetValue(float targetValue) { this.targetValue = targetValue; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // ==================== HELPER ====================

    public String getUnit() {
        if (goalType == null) return "";
        switch (goalType) {
            case "Giảm cân":
            case "Tăng cân": return "kg";
            case "Uống nước": return "ml";
            case "Giấc ngủ": return "giờ";
            default: return "";
        }
    }

    public String getIcon() {
        if (goalType == null) return "[?]";
        switch (goalType) {
            case "Giảm cân": return "[↓]";
            case "Tăng cân": return "[↑]";
            case "Uống nước": return "[~]";
            case "Giấc ngủ": return "[z]";
            default: return "[?]";
        }
    }

    public long getDaysRemaining() {
        if (deadline == null) return -1;
        long diff = deadline.getTime() - System.currentTimeMillis();
        return Math.max(0, diff / (1000 * 60 * 60 * 24));
    }

    public boolean isExpired() {
        return deadline != null && deadline.before(new Date(System.currentTimeMillis()));
    }
}
