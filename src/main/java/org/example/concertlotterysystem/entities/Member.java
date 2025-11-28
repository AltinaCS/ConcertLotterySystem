package org.example.concertlotterysystem.entities;

public class Member {
    private String memberId;
    private String name;
    private String email;

    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
    }

    public String getMemberId(){
        return memberId;
    }
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
}
