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
```
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
  
### 배차 수행 관련
- [GET /api/call/list](#get-apicalllist)
- [POST /api/call/request](#post-apicallrequest)
- [GET /api/call/assign/[id]](#get-apicallassign)
  
## POST /api/auth/signup
회원가입 수행
```
curl -X POST \
  http://localhost:9876/api/auth/signup \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d '{
  "email": "test@email.com",
  "password": "test1234",
  "userType": "PASSENGER"
}'
```

**Method** : `POST`

**Auth required** : NO

**Body constraints**

```json
{
    "email": "[email format]",
    "userType": "[PASSENGER or DRIVER]"
}
```
**Body example** 3개의 필수 입력 프로퍼티로 구성

```json
{
    "email": "test@email.com",
    "password": "test1234",
    "userType": "PASSENGER"
}
```
### Success Responses
**Code** : `200 OK`

```json
{
    "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyVHlwZSI6IlBBU1NFTkdFUiIsImV4cCI6MTU1NTMzMTI1MiwiZW1haWwiOiJzdmxhZGFjb0BkLmNvbSIsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb1RheGlBcGnsl5DshJwg67Cc7ZaJIn0.LuYtm-gdFIeAAzA0ABRHmH3sOxKp5ennT_uUqnzGvPw"
}
```

### Error Response

**Condition** : 이메일 형식이 잘못 되었을때.

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "INVALID_EMAIL_FORMAT",
   "message": "잘못된 이메일 형식입니다."
}
```

#### Or

**Condition** : 3개의 필수 입력중 한개라도 누락 되었을때.

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "SIGNUP_REQUIRED_EMAIL",
   "message": "등록할 이메일을 입력해 주세요."
}

// or

{
   "code": "SIGNUP_REQUIRED_PASSWORD",
   "message": "등록할 패스워드를 입력해 주세요."
}

// or

{
   "code": "SIGNUP_REQUIRED_USERTYPE",
   "message": "유저의 타입(승객/기사)을 입력해 주세요."
}

```

#### Or

**Condition** : 이미 등록된 이메일일때.

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "SIGNUP_EXIST_EMAIL",
   "message": "이미 등록된 이메일 입니다."
}

```

## POST /api/auth/signin
## POST /api/auth/refresh
## GET /api/call/list
## POST /api/call/request
## GET /api/call/assign