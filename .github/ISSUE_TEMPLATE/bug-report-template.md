name: "🐞 버그 제보"
description: 발생한 버그를 보고하고 수정이 필요함을 알립니다.
title: "🐞 버그 제보: "
labels: ["bug", "needs-triage"]
assignees: []

body:
  - type: textarea
    id: summary
    attributes:
      label: "✨ 어떤 버그인가요?"
      description: 발생한 버그를 간단하게 설명해주세요.
      placeholder: 예) 카카오 로그인 시 500 에러 발생
    validations:
      required: true

  - type: textarea
    id: situation
    attributes:
      label: "📌 어떤 상황에서 발생한 버그인가요?"
      description: 버그가 발생한 상황을 가능한 구체적으로 설명해주세요. Given-When-Then 형식을 추천합니다.
      placeholder: |
        Given: 로그인하지 않은 상태에서
        When: /my-page 접근 시
        Then: 로그인 페이지로 리다이렉트되지 않고 빈 화면이 나타남
    validations:
      required: true

  - type: textarea
    id: expected
    attributes:
      label: "🍀 예상 결과"
      description: 원래 기대했던 정상적인 동작이 무엇이었는지 설명해주세요.
      placeholder: 예) 로그인 페이지로 리다이렉트
    validations:
      required: true

  - type: textarea
    id: related
    attributes:
      label: "🗂 관련 화면 / API"
      description: 이 버그와 관련된 화면, 경로 또는 API가 있다면 작성해주세요.
      placeholder: 예) /my-page, GET /users/me 등
    validations:
      required: false

  - type: textarea
    id: screenshot
    attributes:
      label: "📸 스크린샷 / 로그"
      description: 스크린샷이나 콘솔/서버 로그 등 버그 재현에 도움이 되는 자료가 있다면 첨부해주세요.
      placeholder: 예) 500 Internal Server Error 로그 메시지
    validations:
      required: false
