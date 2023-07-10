package com.example.study.entity;


import lombok.*;

import javax.persistence.*;

@Getter @Setter
@ToString(exclude = "team") @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder @Entity
@Table(name = "tbl_member")
public class Member {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "member_id")
   private long id;
   
   private String userName;
  
   private int age;
   
   
   @ManyToOne(fetch = FetchType.LAZY) //불필요한 연산이 발생하지 않도록.
   @JoinColumn(name = "team_id")
   private Team team;
   
   
}
