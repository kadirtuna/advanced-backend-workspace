package com.n11bootcamp.jwtornek.response;

public class WelcomeResponse {

    private String username;
    private String greeting;
    private String project;
    private String author;
    private String serverTime;

    public WelcomeResponse(String username, String greeting, String project, String author, String serverTime) {
        this.username = username;
        this.greeting = greeting;
        this.project = project;
        this.author = author;
        this.serverTime = serverTime;
    }

    public String getUsername()   { return username; }
    public String getGreeting()   { return greeting; }
    public String getProject()    { return project; }
    public String getAuthor()     { return author; }
    public String getServerTime() { return serverTime; }
}
