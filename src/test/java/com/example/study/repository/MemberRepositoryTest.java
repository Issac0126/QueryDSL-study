package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {
   
   @Autowired
   MemberRepository memberRepository;
   @Autowired
   TeamRepository teamRepository;
   @Autowired
   EntityManager em;
   
   JPAQueryFactory factory = new JPAQueryFactory(em);
   
   @BeforeEach
   void testInsertData(){
      
      Team teamA = Team.builder()
         .name("team1")
         .build();
      Team teamB = Team.builder()
         .name("team2")
         .build();
      
      teamRepository.save(teamA);
      teamRepository.save(teamB);
      
      Member member1 = Member.builder()
         .userName("member1")
         .age(98)
         .team(teamA)
         .build();
      Member member2 = Member.builder()
         .userName("member2")
         .age(88)
         .team(teamA)
         .build();
      Member member3 = Member.builder()
         .userName("member3")
         .age(51)
         .team(teamB)
         .build();
      Member member4 = Member.builder()
         .userName("member4")
         .age(42)
         .team(teamB)
         .build();
      
      memberRepository.save(member1);
      memberRepository.save(member2);
      memberRepository.save(member3);
      memberRepository.save(member4);
   }

   @Test
   @DisplayName("start QueryDsl")
   void startQueryDsl() {
       //given
//       QMember
       //when
       
       //then
   }
   
   




}