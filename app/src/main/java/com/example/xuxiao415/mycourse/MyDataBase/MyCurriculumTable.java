package com.example.xuxiao415.mycourse.MyDataBase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Kai on 2016/10/13.
 * 课程表实体类
 * 在数据库中生成对应的表
 */

@Entity
public class MyCurriculumTable {
    @Id
    private Long id;
    @Property(nameInDb = "c_id")
    private int c_id;
    @Property(nameInDb = "studentNumber")
    private String studentNumber;
    @Property(nameInDb = "classNumber")
    private String classNumber;
    @Property(nameInDb = "cNumber")
    private String cNumber;
    @Property(nameInDb = "cName")
    private String cName;
    @Property(nameInDb = "cType")
    private String cType;
    @Property(nameInDb = "tName")
    private String tName;
    @Property(nameInDb = "period")
    private String period;
    @Property(nameInDb = "cTime")
    private String cTime;
    @Property(nameInDb = "weekday")
    private String weekday;
    @Property(nameInDb = "location")
    private String location;
    public String getLocation() {
        return this.location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getWeekday() {
        return this.weekday;
    }
    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }
    public String getCTime() {
        return this.cTime;
    }
    public void setCTime(String cTime) {
        this.cTime = cTime;
    }
    public String getPeriod() {
        return this.period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
    public String getTName() {
        return this.tName;
    }
    public void setTName(String tName) {
        this.tName = tName;
    }
    public String getCType() {
        return this.cType;
    }
    public void setCType(String cType) {
        this.cType = cType;
    }
    public String getCName() {
        return this.cName;
    }
    public void setCName(String cName) {
        this.cName = cName;
    }
    public String getCNumber() {
        return this.cNumber;
    }
    public void setCNumber(String cNumber) {
        this.cNumber = cNumber;
    }
    public String getClassNumber() {
        return this.classNumber;
    }
    public void setClassNumber(String classNumber) {
        this.classNumber = classNumber;
    }
    public String getStudentNumber() {
        return this.studentNumber;
    }
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    public int getC_id() {
        return this.c_id;
    }
    public void setC_id(int c_id) {
        this.c_id = c_id;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    @Generated(hash = 202200875)
    public MyCurriculumTable(Long id, int c_id, String studentNumber,
            String classNumber, String cNumber, String cName, String cType,
            String tName, String period, String cTime, String weekday,
            String location) {
        this.id = id;
        this.c_id = c_id;
        this.studentNumber = studentNumber;
        this.classNumber = classNumber;
        this.cNumber = cNumber;
        this.cName = cName;
        this.cType = cType;
        this.tName = tName;
        this.period = period;
        this.cTime = cTime;
        this.weekday = weekday;
        this.location = location;
    }
    @Generated(hash = 390820896)
    public MyCurriculumTable() {
    }
    
}
