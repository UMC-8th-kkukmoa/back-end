# 👍 Kkukmoa Back-End

## 프로젝트 소개
모바일 금액권과 스탬프 적립으로 고객 충성도를 높이는 혁신적인 솔루션

<div align="center">
<img width="4500" height="3000" alt="Image" src="https://github.com/user-attachments/assets/2c897dc7-521a-4676-994a-bd66de52ebf3" />
</div>

#### 주요 기능
- 디지털 금액권 구매 및 사용: 원하는 금액의 e-금액권을 구매하여 입점 매장에서 자유롭게 사용합니다.
- 자동 스탬프 적립 시스템: 결제 내역과 연동되어 스탬프가 자동으로 적립됩니다.
- 맞춤형 서비스 쿠폰: 스탬프 10개 누적 시 자동 발급되며, 매장별 사장님이 직접 혜택을 설정합니다.
- QR 코드 기반 적립 및 선물: 카드 결제 시에도 QR 인식으로 스탬프 적립이 가능하며, 금액권을 선물할 수 있습니다.


## 기술 스택
- Language: Java 17
- Framework: Spring Boot
- Build Tool: Gradle
- Database: MySQL
- Deploy: GitHub Actions, AWS


## 서버 아키텍처
![Image](https://github.com/user-attachments/assets/8644b074-c7ca-4840-a19d-44832d01a13b)


##  프로젝트 구조
#### 도메인형
- 각 도메인 패키지는 엔티티, DTO, 컨트롤러, 서비스, 리포지토리 등 하위 패키지를 포함

```
src/
└── main/
    └── java/kkukmoa/kkukmoa
        ├── KkukmoaApplication.java
        ├── apiPayload/
        ├── config/
        ├── common/
        ├── admin/
        ├── category/
        └── payment/
        └── ... 
```

#### Branch Strategy
- main: 배포 가능한 최종 코드만 관리합니다.
- dev: 개발 중인 기능을 통합하는 브랜치입니다.
- feat: 새로운 기능 개발 시 사용합니다. (‎⁠예: `feat/login`⁠)
- fix: 버그 수정 시 사용합니다. (‎⁠예: `fix/login`)
- refactor: 코드 리팩토링 시 사용합니다. (예: `refactor/login`)
- chore: 자잘한 수정이나 빌드를 할 때 사용합니다. (예: `chore/login`)

#### Issue
- 구현해야 하는 기능, 문제점, 해당 화면, 예상 작업 항목 등을 이슈로 등록합니다.
- 이슈 템플릿을 참고하여 작성합니다.
- 이슈 제목은 제목 앞에 [타입/#이슈 번호]을 붙이고, 이슈 내용을 한 눈에 알 수 있게 작성합니다. (예: `[Feat/#1] 로그인 기능 구현`)
- 해당하는 라벨을 추가합니다.

#### Pull Request (PR)
- PR 템플릿을 참고하여 작성합니다.
- PR 제목은 제목 앞에 [타입][작성자]를 붙이고, PR 내용을 간결하게 작성합니다. (예: `[FEAT][mumi]: 로그인 기능 구현`)
- 관련 이슈가 있다면 연결합니다.
- 코드 리뷰를 거친 후 dev 브랜치로 머지합니다.


## 👥 Back-End Developer
<div align="center">

<table>
  <tr>
    <!-- 사진 행 -->
    <td align="center">
      <a href="https://github.com/ggamnunq">
        <img width="170" src="https://github.com/user-attachments/assets/f5e6c3a0-ec0f-467a-95e2-11dc88adbc74" alt="김준용" />
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/Federico-15">
        <img width="170" src="https://github.com/user-attachments/assets/8014bf0a-9b9f-4987-a124-c47761a840e8" alt="류승환" />
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/summinn9">
        <img src="https://github.com/user-attachments/assets/885b193f-cfff-4849-974b-176a183f2b3c" width="170" alt="장수민" />
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/jjaeroong">
        <img src="https://github.com/user-attachments/assets/0fd86601-ca5b-4736-86c2-df4221f3c368" width="170" alt="최재영" />
      </a>
    </td>
  </tr>
 <tr>
     <td align="center"><b>세인트/김준용</b></td>
    <td align="center"><b>페데리코/류승환</b></td>
    <td align="center"><b>무미/장수민</b></td>
    <td align="center"><b>제리/최재영</b></td>

  </tr>
  <tr>
    <td align="center">BE(Lead)</td>
    <td align="center">BE</td>
    <td align="center">BE</td>
    <td align="center">BE</td>
  </tr>
</table>
</div>
