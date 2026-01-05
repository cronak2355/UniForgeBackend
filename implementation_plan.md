# 서버 실행 및 최종 안정화 계획

백엔드 ID 타입 변경(UUID)에 따른 잔여 런타임 오류를 해결하고 서버를 즉시 실행합니다.

## 1. 백엔드 설정 및 코드 수정
- `application.yml`의 S3 설정 경로 확인 및 수정
- `S3Service.kt`, `S3Uploader.kt` 등에서 호출하는 `@Value` 경로를 `aws.s3.bucket`으로 통일
- `ddl-auto`를 `create`로 잠시 변경하여 DB 스키마 불일치 해결

## 2. 서버 실행
- 백엔드: `bootRun` 실행 (필요한 환경 변수 포함)
- 프론트엔드: `npm run dev` 실행

## 3. 최종 확인
- `http://localhost:8080/health` 접속 확인
- 프론트엔드 로그인 및 에셋 업로드 기능 동작 확인
