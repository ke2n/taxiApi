# 택시 배차 앱 API.

## 개발 프레임워크
`SpringBoot 2.1.4` `JDK8`

### 사용 라이브러리
`JPA` `H2` `lombok` `jbcrypt` `JWT`

### 디렉토리 구조 세부설명
- com.demo.taxiApi
    - common : exception Handler 및 유틸성 클래스
    - controller :
        - CallController(/api/call/*) : 배차목록조회, 택시 배차 요청, 기사 배차 기능수행 Controller
        - AuthController(/api/auth/*) : 회원가입, 로그인, 토큰 갱신 기능수행 Controller
    - domain : JPA Entity 클래스(Call, User)
    - exception : 커스텀 예외처리 클래스 및 예외처리 코드
    - interceptor : Header에 Authorization 확인 및 검증 interceptor 클래스
    - mapper : JPA Entity 클래스와 Model간의 컨버팅 클래스
    - model : Model 클래스
    - repository : JPA repository 클래스
    - service : service 클래스
- resources : property파일
- test : 테스트 수행 클래스
    
## 빌드 및 실행방법
```javascript
빌드 수행 
cd ${RootPath} (ex) taxiApi)
mvn package (또는 ./mvnw package)

서버 기동(9876 port)
java -jar target/taxiApi-0.0.1-SNAPSHOT.jar
```
## API Specification
### 인증 관련
  - [POST /api/auth/signup](#post-apiauthsignup)
  - [POST /api/auth/signin](#post-apiauthsignin)
  - [POST /api/auth/refresh](#post-apiauthrefresh)
  
### POST /api/auth/signup
회원가입 수행

**Method** : `POST`

**Auth required** : NO

**Body constraints**

```json
{
    "email": "[email format]",
    "userType": "[PASSENGER or DRIVER]"
}
```
**Body example** 3개의 프로퍼티로 구성

```json
{
    "email": "test@email.com",
    "password": "test1234",
    "userType": "PASSENGER"
}
```
#### Success Responses
**Code** : `200 OK`

```json
{
    "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyVHlwZSI6IlBBU1NFTkdFUiIsImV4cCI6MTU1NTMzMTI1MiwiZW1haWwiOiJzdmxhZGFjb0BkLmNvbSIsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb1RheGlBcGnsl5DshJwg67Cc7ZaJIn0.LuYtm-gdFIeAAzA0ABRHmH3sOxKp5ennT_uUqnzGvPw"
}
```

### POST /api/auth/signin
### POST /api/auth/refresh