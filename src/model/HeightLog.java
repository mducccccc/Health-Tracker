package model;

import java.util.Date;

public class HeightLog {
    private int id;
    private int userId;
    private float heightCm;
    private Date logDate;
    private String note;

    public HeightLog() {}

    public HeightLog(int userId, float heightCm, Date logDate, String note) {
        this.userId = userId;
        this.heightCm = heightCm;
        this.logDate = logDate;
        this.note = note;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public float getHeightCm() { return heightCm; }
    public void setHeightCm(float heightCm) { this.heightCm = heightCm; }

    public Date getLogDate() { return logDate; }
    public void setLogDate(Date logDate) { this.logDate = logDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    /** Phân loại chiều cao theo độ tuổi người Việt (người lớn) */
    public String getHeightCategory() {
        if (heightCm < 150) return "Thấp";
        else if (heightCm < 160) return "Trung bình thấp";
        else if (heightCm < 175) return "Trung bình";
        else if (heightCm < 185) return "Cao";
        else return "Rất cao";
    }
}
