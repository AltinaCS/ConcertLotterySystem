package org.example.concertlotterysystem.entities;

public class Member {
    private String memberId;
    private String name;
    private String email;
    private MemberQualificationStatus qualification;

    public Member(String memberId, String name, String email) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;

        if(name.equals("admin")){
            this.qualification = MemberQualificationStatus.ADMIN;
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
    public MemberQualificationStatus getQualification(){return qualification;}
}
