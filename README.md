펫스타그램 (Petstagram)


프로젝트 소개

펫스타그램은 사용자가 자신의 반려동물 사진을 업로드하고 공유할 수 있는 플랫폼입니다. 

사용자들은 사진을 게시하고, 좋아요와 댓글을 통해 소통하며, 다른 사용자와의 상호작용을 즐길 수 있습니다. 

본 프로젝트는 Spring Boot와 React를 기반으로 하여 무상태(stateless) 인증 및 권한 부여 시스템을 구축하였습니다.


개발 기간

2024.05.17 ~ 2024.07.05


개발 환경

운영 체제: Windows / macOS

IDE: IntelliJ IDEA, Visual Studio Code

버전 관리: Git, GitHub


기술 스택

프론트엔드: React, Axios

백엔드: Java, Spring Boot, Spring Security, Spring WebSocket, Spring Data JPA

데이터베이스: MySQL


주요 기능

사용자 인증: Spring Security와 JWT를 활용하여 무상태 인증 및 권한 부여 시스템 구현

사진 업로드 및 공유: 사용자가 반려동물 사진을 업로드하고 다른 사용자와 공유

실시간 메시징: Spring WebSocket을 사용하여 인스타그램 DM과 유사한 양방향 실시간 통신 메시지 구현

데이터 관리: Spring Data JPA를 사용하여 엔티티 간의 연관 관계 형성 및 데이터 관리


프로젝트 아키텍처

클라이언트: React 기반의 프론트엔드로, Axios를 통해 백엔드와 통신하며 사용자 인증 토큰(JWT)을 로컬스토리지에 저장합니다.

서버: Spring Boot를 기반으로 하여 RESTful API를 제공하며, Spring Security를 활용하여 인증 및 권한 부여를 처리합니다.

실시간 통신: Spring WebSocket을 통해 사용자 간의 실시간 메시징 기능을 제공합니다.

데이터베이스: MySQL을 사용하여 사용자 데이터 및 사진 정보를 저장하고 관리합니다.

