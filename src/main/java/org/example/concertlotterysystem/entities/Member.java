package org.example.concertlotterysystem.entities;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private String memberId;
    private String name;
    private String email;
    private List<Event> eventlist;
    private List<Ticket> ticketList;
    private MemberQualificationStatus qualification;
    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.eventlist = new ArrayList<>();
        this.ticketList= new ArrayList<>();
        if(name.equals("admin")){
            this.qualification = MemberQualificationStatus.ADMIN;
        } else if (name.equals("vip")) {
            this.qualification = MemberQualificationStatus.VIP;
        }
        else{
            this.qualification=MemberQualificationStatus.MEMBER;
        }
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
    public List<Event> getEvents(){return eventlist;}
    public List<Ticket> getTickets(){return ticketList;}
    public MemberQualificationStatus getQualification(){return qualification;}
}
