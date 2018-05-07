package com.honeywell.android.bean;

/**
 * Created by melo on 2018/5/2.
 */

public class BatteryInfo {
    private String Evaluation;
    private String Grade;
    private String Score;
    private String Evaluation0;
    private String Evaluation4;

    public BatteryInfo(String evaluation, String grade, String score, String evaluation0, String evaluation4) {
        Evaluation = evaluation;
        Grade = grade;
        Score = score;
        Evaluation0 = evaluation0;
        Evaluation4 = evaluation4;
    }

    public String getEvaluation() {
        return Evaluation;
    }

    public void setEvaluation(String evaluation) {
        Evaluation = evaluation;
    }

    public String getGrade() {
        return Grade;
    }

    public void setGrade(String grade) {
        Grade = grade;
    }

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score = score;
    }

    public String getEvaluation0() {
        return Evaluation0;
    }

    public void setEvaluation0(String evaluation0) {
        Evaluation0 = evaluation0;
    }

    public String getEvaluation4() {
        return Evaluation4;
    }

    public void setEvaluation4(String evaluation4) {
        Evaluation4 = evaluation4;
    }
}
