# 🐾 펫스타그램 (Petstagram)



## 📖 프로젝트 소개

펫스타그램은 사용자가 자신의 반려동물 사진을 업로드하고 공유할 수 있는 플랫폼입니다.

사용자들은 사진을 게시하고, 좋아요와 댓글을 통해 소통하며, 다른 사용자와의 상호작용을 즐길 수 있습니다.

본 프로젝트는 Spring Boot와 React를 기반으로 하여 무상태(stateless) 인증 및 권한 부여 시스템을 구축하였습니다.



## 📅 개발 기간

2024.05.17 ~ 2024.07.05


## 👨‍💻 개발자 소개

- 개발자 1: [구준모] - 백엔드 개발 (Spring Boot, Spring Security, JWT), 데이터베이스 및 아키텍처 설계 (MySQL, Spring Data JPA)

- 개발자 2: [허민석] - 프론트엔드 개발 (React, Axios)

- 개발자 3: [최지호] - 프론트엔드 개발 (React, Axios)


## 💻 개발 환경

- 운영 체제: Windows / macOS

- IDE: IntelliJ IDEA, Visual Studio Code

- 버전 관리: Git, GitHub



## ⚙️ 기술 스택

- 프론트엔드: React, Axios

- 백엔드: Java, Spring Boot, Spring Security, Spring WebSocket, Spring Data JPA, Spring MVC (SSE)

- 데이터베이스: MySQL



## 🌟 주요 기능

- **사용자 인증**: Spring Security와 JWT를 활용하여 무상태 인증 및 권한 부여 시스템 구현
- **사진 업로드 및 공유**: 사용자가 반려동물 사진을 업로드하고 다른 사용자와 공유
- **실시간 메시징**: Spring WebSocket을 사용하여 인스타그램 DM과 유사한 양방향 실시간 통신 메시지 구현
- **실시간 알림 (SSE)**: Server-Sent Events를 활용하여 팔로우, 좋아요, 댓글 등에 대한 실시간 알림 기능 구현
- **데이터 관리**: Spring Data JPA를 사용하여 엔티티 간의 연관 관계 형성 및 데이터 관리



## 🏗️ 프로젝트 아키텍처

- **클라이언트**  
  - React 기반 프론트엔드  
  - Axios로 REST API 호출  
  - EventSource를 이용해 SSE 수신  
  - JWT 토큰을 로컬스토리지에 저장하고, 요청 시 HTTP 헤더에 포함

- **서버**  
  - Spring Boot RESTful API  
  - Spring Security + JWT로 인증·권한 처리  
  - WebSocket으로 실시간 채팅 메시징 처리  
  - Spring MVC (SseEmitter)로 실시간 알림 스트림 제공
  - 알림 이벤트 발생 시 SseEmitter로 클라이언트에 실시간 푸시

- **데이터베이스**  
  - MySQL로 사용자, 사진, 댓글, 좋아요, 알림(Notification) 테이블 관리  
  - Notification 엔티티에 알림 타입, 대상 사용자, 상태(read/unread) 등 저장
