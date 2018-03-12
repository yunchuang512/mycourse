package com.example.xuxiao415.mycourse.MyDataClass;
/**
 * Created by xuxiao415 on 2016/9/6.
 */
/**
 * 课程表数据
 *
 * 学号    studentNumber
 * 教学班号 classNumber
 * 课程编号 cNumber
 * 课程名称 cName
 * 课程类别 cType
 * 教师    tName
 * 上课时间包括该课上几周(period)，周几上课(weekday)，哪一节上课(cTime)  location
 * 上课地点 location
 */

public class CurriculumEntity {

	private int id;
	private String studentNumber;
	private String classNumber;
	private String cNumber;
	private String cName;
	private String cType;
	private String tName;
	private String period;
	private String cTime;
	private String weekday;
	private String location;

	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getClassNumber() {
		return classNumber;
	}

	public void setClassNumber(String classNumber) {
		this.classNumber = classNumber;
	}

	public String getcNumber() {
		return cNumber;
	}

	public void setcNumber(String cNumber) {
		this.cNumber = cNumber;
	}

	public String getcName() {
		return cName;
	}

	public void setcName(String cName) {
		this.cName = cName;
	}

	public String getcType() {
		return cType;
	}

	public void setcType(String cType) {
		this.cType = cType;
	}

	public String gettName() {
		return tName;
	}

	public void settName(String tName) {
		this.tName = tName;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getcTime() {
		return cTime;
	}

	public void setcTime(String cTime) {
		this.cTime = cTime;
	}
}
