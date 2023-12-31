package com.example.study.repository;

import com.example.study.entity.Member;
import com.example.study.entity.QMember;
import com.example.study.entity.Team;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static com.example.study.entity.QMember.member;
import static com.example.study.entity.QTeam.team;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
   
   //QueryDSL로 쿼리문을 작성하기 위한 핵심 객체
   JPAQueryFactory factory;
   
   @BeforeEach
   void settingObject(){
      factory = new JPAQueryFactory(em);
   }
   

   void testInsertData(){
      
//      Team teamA = Team.builder()
//         .name("teamA")
//         .build();
//      Team teamB = Team.builder()
//         .name("teamB")
//         .build();
//
//      teamRepository.save(teamA);
//      teamRepository.save(teamB);

      Member member1 = Member.builder()
         .userName("member1")
         .age(10)
         .build();
      Member member2 = Member.builder()
         .userName("member2")
         .age(20)
         .build();
      Member member3 = Member.builder()
         .userName("member3")
         .age(30)
         .build();
      Member member4 = Member.builder()
         .userName("member4")
         .age(40)
         .build();
      Member member5 = Member.builder()
         .userName("member5")
         .age(50)
         .build();
      Member member6 = Member.builder()
         .userName("member6")
         .age(60)
         .build();
      Member member7 = Member.builder()
         .userName("member7")
         .age(70)
         .build();
      Member member8 = Member.builder()
         .userName("member8")
         .age(80)
         .build();
      
      memberRepository.save(member1);
      memberRepository.save(member2);
      memberRepository.save(member3);
      memberRepository.save(member4);
      memberRepository.save(member5);
      memberRepository.save(member6);
      memberRepository.save(member7);
      memberRepository.save(member8);
   }
   
   

   @Test
   @DisplayName("testJPA")
   void testJPA() {
      List<Member> members = memberRepository.findAll();
      members.forEach(System.out::println);
   }
   
   @Test
   @DisplayName("JPQL")
   void testJPQL() {
      //given
      String jpqlQuery = "SELECT m FROM Member m WHERE m.userName = :userName";
      
      //when
      Member foundMember = em.createQuery(jpqlQuery, Member.class)
         .setParameter("userName", "member2")
         .getSingleResult();//값이 하나라면 getSingleResult. 여러개라면 .getResultList()로 받기.
      
      //then
      assertEquals("teamA", foundMember.getTeam().getName());
      //왜 assert가 import 안되지
      
      System.out.println("\n\n\n");
      System.out.println("foundMember = " + foundMember);
      System.out.println("foundMember.getTeam() = " + foundMember.getTeam());
      System.out.println("\n\n\n");
      
   }
   
   @Test
   @DisplayName("QueryDSL")
   void testQueryDSL() {
       //given
//      QMember m = new QMember("m1");  이 방법도 있지만 하단의 방법이 더 간결함.
//      QMember m = member;
      
       //when
      Member foundMember = factory
         .select(member)
         .from(member)
         .where(member.userName.eq("member3"))
         .fetchOne();
      
      //then
      assertNotNull(foundMember);
      assertEquals("teamB", foundMember.getTeam().getName());
      
      System.out.println("\n\n\n");
      System.out.println("foundMember = " + foundMember);
      System.out.println("foundMember.getTeam() = " + foundMember.getTeam());
      System.out.println("\n\n\n");
   }
   
   @Test
   @DisplayName("search")
   void search() {
       //given
       String searchName = "member2";
       int searchAge = 88;
       //when
      Member foundMember = factory.selectFrom(member)
         .where(member.userName.eq(searchName)
            .and(member.age.eq(searchAge)))
         .fetchOne();
      //then
      assertNotNull(foundMember);
      assertEquals("teamA", foundMember.getTeam().getName());
      
      /*
      JPAQueryFactory를 이용해서 쿼리문을 조립한 후 반환 인자를 결정한다.
      - fetchOne() : 단일 건 조회. 여러 건 조회시 예외 발생
      - fetchFirst() : 단일 건 조회. 여러 개가 조회돼도 첫 번째 값만 반환
      - fetch(): List 형태로 반환
      
      * JPQL이 제공하는 모든 검색 조건은 queryDsl에서도 사용 가능하다.
      *
      * member.userName.eq("member1") // userName = 'member1'
      * member.userName.nq("member1") // userName != 'member1'
      * member.userName.eq("member1").not() // userName != 'member1'
      * member.userName.isNotNull() // userName이 is not null
      *
      * member.age.in(10, 20) // age in(10, 20) // age = 10 || age = 20
      * member.age.notIn(10, 20) // age not in(10, 20)
      * member.age.between(10, 30) // between 10 and 30
      * member.age.goe(30) // between 10 >= 30
      * member.age.gt(30) // between 10 > 30
      * member.age.loe(30) // between 10 <= 30
      * member.age.lt(30) // between 10 < 30
      *
      * member.userName.like("_김%")  // userName LIKE '_김%'
      * contains, startWith, endWith는 like로 대체 가능하다.
      * member.userName.contains("김")  // userName LIKE '%김%'
      * member.userName.startWith("김")  // userName LIKE '김%' // 김으로 시작한다면
      * member.userName.endWith("김")  // userName LIKE '%김' // 김으로 끝난다면
      */
   }
   

   @Test
   @DisplayName("결과 반환하기")
   void testFetchResult() {
       //fetch
      List<Member> fetch1 = factory.selectFrom(member).fetch();
      System.out.println("\n\n ========= fetch1 .fetch() =========");
      fetch1.forEach(System.out::println);
      
      Member fetch2 = factory.selectFrom(member)
         .where(member.id.eq(3L)).fetchOne();
      System.out.println("\n\n ========= fetch2 .fetchOne() =========");
      System.out.println("fetch2 = " + fetch2);
      
      Member fetch3 = factory.selectFrom(member).fetchFirst();
      System.out.println("\n\n ========= fetch3 .fetchFirst() =========");
      System.out.println("fetch3 = " + fetch3);
   }

   @Test
   @DisplayName("QueryDsl cusomt 설정 확인")
   void queryDslCustom() {
       //given
       String name = "member4";
       //when
      List<Member> result = memberRepository.findByName(name);
      //then
      assertEquals(1, result.size());
      assertEquals("teamB", result.get(0).getTeam().getName());
   }
   
   @Test
   @DisplayName("sort")
   void sort() {
       //given
       
       //when
      List<Member> result =
         factory.selectFrom(member).orderBy(member.age.desc()).fetch();
      
      //then
      assertEquals(result.size(), 8);
      
      System.out.println("\n\n\n");
      result.forEach(System.out::println);
      System.out.println("\n\n\n");
   }
   
   @Test
   @DisplayName("queryDsl paging")
   void paging() {
       //given
       //when
      List<Member> result
         = factory.selectFrom(member)
         .orderBy(member.userName.desc())
         .offset(0)
         .limit(3)
         .fetch();
      
      //then
      assertEquals(result.size(), 3);
      assertEquals(result.get(2).getUserName(), "member6");
   }
   
   @Test
   @DisplayName("그룹 함수의 종류")
   void Group() {
       //given
       //when
      List<Tuple> result =
         factory.select(
         member.count(),
         member.age.sum(),
         member.age.avg(),
         member.age.max(),
         member.age.min()
      ).from(member).fetch();
      
      Tuple tuple = result.get(0); // 튜플을 꺼낸 다음
      //then
      assertEquals(tuple.get(member.count()), 8);
      assertEquals(tuple.get(member.age.sum()), 360);
      assertEquals(tuple.get(member.age.avg()), 45);
      assertEquals(tuple.get(member.age.max()), 80);
      assertEquals(tuple.get(member.age.min()), 10);
      
      System.out.println("\n\n\n");
      System.out.println("tuple = " + tuple);
      System.out.println("\n\n\n");
   }
   
   @Test
   @DisplayName("Group By, Having")
   void testGroupBy() {
       //given
      
      //when
      List<Long> result = factory.select(member.age.count())
         .from(member)
         .orderBy(member.age.asc())
         .groupBy(member.age)
         .having(member.age.count().goe(2)) // age의 count가 2개 이상인 그룹
         .fetch();
       
       //then
      assertEquals(result.size(),2);
      System.out.println("\n\n\n");
      result.forEach(System.out::println);
      System.out.println("\n\n\n");
   }
   
   @Test
   @DisplayName("join 기능 구현")
   void join() {
       //given

       //when
      List<Member> result = factory.selectFrom(member)
         .leftJoin(member.team, team)
         .where(team.name.eq("teamA"))
         .fetch();
      
      //then
      System.out.println("\n\n\n");
      result.forEach(System.out::println);
      System.out.println("\n\n\n");
   }
   
   
   /**
    * ex ) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조회, 회원은 모두 조회
    * SQL: SELECT m.*, t.* FROM tbl_member m LEFT JOIN tbl_team t ON m.team_id = t.id AND t.name = 'teamA'
    * JPQL : .select(m, t).from(member m).leftJoin(m.team t).where(t.name = 'teamA');
    * member가 team까지 가지고 있기 때문에 join을 m.team으로 가져온다. 따라서 and를 생략할 수 있음.
    */
   @Test
   @DisplayName("left outer join 테스트")
   void leftJoinTest() {
       //given
       //when
      List<Tuple> result = factory.select(member, team)
         .from(member)
         .leftJoin(member.team, team)
         .on(team.name.eq("teamA"))
         .fetch();
      //then
      result.forEach(tuple -> System.out.println("tuple = " + tuple));
   }
   
   @Test
   @DisplayName("sub query 사용하기(나이가 가장 많은 회원을 조회 )")
   void what() {
       //given
      //같은 테이블에서 서브쿼리를 적용하려면 별도로 QClass의 객체를 생성해야 한다.
      QMember memberSub = new QMember("memberSub");
      
       //when
      List<Member> result =
         factory.selectFrom(member)
                 .where(member.age.eq(
                     JPAExpressions //서브쿼리를 사용할 수 있게 해주는 클래스
                        .select(memberSub.age.max())
                        .from(memberSub)
                 )).fetch();
      //then
      System.out.println("\n\n\n");
      result.forEach(System.out::println);
      System.out.println("\n\n\n");
   }
   
   
   @Test
   @DisplayName("나이가 평균 나이 이상인 회원을 조회")
   void subQueryGoe() {
       //given
      QMember m2 = new QMember("m2");
       //when
      //JPAExpressions는 from 절을 제외하고, select와 where절에서 사용이 가능하다.
      List<Member> result
         = factory.selectFrom(member)
                  .where(member.age.goe(
                        JPAExpressions
                           .select(m2.age.avg())
                           .from(m2)
                  )).fetch();
      //then
      assertEquals(result.size(), 5);
   }
   
   @Test
   @DisplayName("동적 SQL 테스트")
   void dynamicQueryTest() {
       //given
      String name = null;
      int age = 60;
      
      
      //when
      List<Member> result = memberRepository.findUser(name, age);
      
      //then
      assertEquals(result.size(), 3);
      
      System.out.println("\n\n\n");
      result.forEach(System.out::println);
      System.out.println("\n\n\n");
      
   }
   
   

}