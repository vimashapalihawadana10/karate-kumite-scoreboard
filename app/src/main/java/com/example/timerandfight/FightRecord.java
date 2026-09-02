package com.example.timerandfight;

public class FightRecord {

    public String aoName;
    public String akaName;
    public int aoScore;
    public int akaScore;
    public String winner;
    public String reason;
    public String dateTime;

    public FightRecord() {
        // Needed for Firebase
    }

    public FightRecord(String aoName, String akaName, int aoScore, int akaScore,
                       String winner, String reason, String dateTime) {
        this.aoName = aoName;
        this.akaName = akaName;
        this.aoScore = aoScore;
        this.akaScore = akaScore;
        this.winner = winner;
        this.reason = reason;
        this.dateTime = dateTime;
    }
}