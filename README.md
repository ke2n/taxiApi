# 택시 배차 앱 API.

## Contents
- [개발 프레임워크](#개발-프레임워크)
- [빌드 및 실행방법](#빌드-및-실행방법)
- [DB Table Specification](#DB-Table-Specification)
- [API Specification](#API-Specification)

## 개발 프레임워크
`SpringBoot 2.1.4` `JDK8`

### 사용 라이브러리
`JPA` `sqlite-jdbc` `lombok` `jbcrypt` `JWT`

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

### 구현 전략
- JWT로 API의 인증을 관리하는 구조로 구현하였습니다.
  - 회원가입시 password는 jbcrypt해시로 암호화되어 저장.
  - 회원가입/로그인 성공시 email과 userType(Role)정보가 포함되어있는 토큰(JWT) 출력.
  - 토큰 만료일은 60분으로 만료시 API접근이 불가하며 사전에 토큰갱신을 통해 새로운 만료일의 토큰으로 갱신필요.
  - 발급받은 토큰을 Header에 추가하여 호출하여야 인증 완료.
- 택시 배차 요청은 승객(PASSENGER)만, 기사 배차는 기사(DRIVER)만 접근 가능하도록 userType(Role)을 구분하였습니다.
  - 목록 조회는 승객/기사 모두 가능
- 기사배차의 경우 한번 할당이 되면 추가 기사 할당을 할 수 없도록 하였습니다.
  - 택시 배차 요청의 경우 한명의 승객이 추가로 배차 요청을 할수도 있다고 판단하여 여러건의 배차 요청을 제한하지는 않았습니다.

    
## 빌드 및 실행방법
```
// 빌드 수행 
cd taxiApi
mvn package (또는 ./mvnw package)

// 서버 기동(9876 port)
java -jar target/taxiApi-0.0.1-SNAPSHOT.jar
```

## DB Table Specification
### ERD
![ERD](erd.png)

### Users Table - 승객 / 기사 테이블
| name        | key       | type  | desc |
| ------------- |-------------| -----|----|
| id      | primary key | bigint |ID |
| email     |      | varchar(255) |이메일 |
| password |      | varchar(255) |비밀번호 |
| user_type |      | varchar(255) |유저 타입 (PASSENGER / DRIVER)|

### Call Table - 배차 정보 테이블
| name        | key           | type  | desc |
| ------------- |-------------| -----|----|
| id      | primary key | bigint |ID|
| passenger_id     | foreign key     | bigint |승객ID|
| driver_id |foreign key     | bigint |기사ID|
| address |      | varchar(255) |요청 주소|
| status |      | varchar(255) |배차 상태 (REQUESTED / ASSIGNED)|
| request_date |      | timestamp |배차 요청 시간|
| assign_date |      | timestamp |배차 완료 시간|

## API Specification
### 인증 관련
- [POST /api/auth/signup](#post-apiauthsignup)
- [POST /api/auth/signin](#post-apiauthsignin)
- [POST /api/auth/refresh](#post-apiauthrefresh)
  
### 배차 수행 관련
- [GET /api/call/list?page=[page]&size=[size]&sort=[sort]](#get-apicalllist)
- [POST /api/call/request](#post-apicallrequest)
- [POST /api/call/assign](#post-apicallassign)


**Swagger UI 참고 (서버기동후 실행)** http://localhost:9876/swagger-ui.html
  
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
로그인 수행
```
curl -X POST \
  http://localhost:9876/api/auth/signin \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d '{
    "email": "test@test.com",
    "password": "test1234"
}'
```

**Method** : `POST`

**Auth required** : NO

**Body constraints**

```json
{
    "email": "[email format]"
}
```
**Body example** 2개의 필수 입력 프로퍼티로 구성

```json
{
    "email": "test@test.com",
    "password": "test1234"
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

**Condition** : 가입한 이메일이 없거나 비밀번호가 일치하지 않을때.

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "NOT_FOUND_USER",
   "message": "유저정보가 존재하지 않습니다."
}
```

## POST /api/auth/refresh
토큰 갱신(재발급)
```
curl -X POST \
  http://localhost:9876/api/auth/refresh \
  -H 'Authorization: Bearer JWT_TOKEN' \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache'
  
 
// SAMPLE_JWT_TOKEN
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdFVzZXIiLCJleHAiOjE4Njg5NDYzMjcsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb0FwaeyXkOyEnCDrsJztlokifQ.r8RLJgRVvUyf1TecScqJMQPF_JMTF0vYFHDkE9_uPjI
```

**Method** : `POST`

**Auth required** : YES

### Success Responses
**Code** : `200 OK`

```json
{
    "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyVHlwZSI6IlBBU1NFTkdFUiIsImV4cCI6MTU1NTMzMTI1MiwiZW1haWwiOiJzdmxhZGFjb0BkLmNvbSIsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb1RheGlBcGnsl5DshJwg67Cc7ZaJIn0.LuYtm-gdFIeAAzA0ABRHmH3sOxKp5ennT_uUqnzGvPw"
}
```

### Error Response

**Condition** : 헤더(Authorization)에 JWT_TOKEN이 없거나 만료 변조된 토큰일 경우

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "UNAUTHORIZED_REQUEST",
   "message": "허가되지않은 요청입니다."
}
```

## GET /api/call/list
전체 배차 목록 조회
```
curl -X GET \
  http://localhost:9876/api/call/list \
  -H 'Authorization: Bearer JWT_TOKEN' \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache'
  
 
// SAMPLE_JWT_TOKEN
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdFVzZXIiLCJleHAiOjE4Njg5NDYzMjcsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb0FwaeyXkOyEnCDrsJztlokifQ.r8RLJgRVvUyf1TecScqJMQPF_JMTF0vYFHDkE9_uPjI
```

**Method** : `GET`

**Auth required** : YES

**Parameter constraints**

```
page=[default value 0]
size=[default value 50]
sort=[default value 'id, DESC']
```
**Parameter example** 3개의 선택 입력 파라메터로 구성

```
page=0&size=50&sort=id, DESC
```

### Success Responses
**Code** : `200 OK`

```json
[
    {
        "id": 6,
        "passenger": {
            "email": "svlada1@gmail.com",
            "userType": "PASSENGER"
        },
        "address": "서울시 도봉구 XX동",
        "status": "REQUESTED",
        "requestDate": "2019-04-15T12:17:34.236+0000"
    },
    {
        "id": 4,
        "passenger": {
            "email": "svlada1@gmail.com",
            "userType": "PASSENGER"
        },
        "driver": {
            "email": "svlada11@gmail.com",
            "userType": "DRIVER"
        },
        "address": "수원시 XX동",
        "status": "ASSIGNED",
        "requestDate": "2019-04-15T12:17:32.070+0000",
        "assignDate": "2019-04-15T12:18:44.446+0000"
    }
]
```

### Error Response

**Condition** : 헤더(Authorization)에 JWT_TOKEN이 없거나 만료 변조된 토큰일 경우

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "UNAUTHORIZED_REQUEST",
   "message": "허가되지않은 요청입니다."
}
```
## POST /api/call/request
택시 배차 요청
```
curl -X POST \
  http://localhost:9876/api/call/request \
  -H 'Authorization: Bearer JWT_TOKEN' \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d '{
  "address": "서울시 중구 을지로 XX"
}'
  
 
// SAMPLE_JWT_TOKEN
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdFVzZXIiLCJleHAiOjE4Njg5NDYzMjcsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb0FwaeyXkOyEnCDrsJztlokifQ.r8RLJgRVvUyf1TecScqJMQPF_JMTF0vYFHDkE9_uPjI
```

**Method** : `POST`

**Auth required** : YES

**Body constraints**

```json
{
    "address": "[100자 이하]"
}
```
**Body example** 1개의 필수 입력 프로퍼티로 구성

```json
{
    "address": "서울시 중구 을지로 XX"
}
```
### Success Responses
**Code** : `200 OK`

```json
{
    "code": "SUCCESS",
    "message": "요청이 정상적으로 완료 되었습니다."
}
```

### Error Response

**Condition** : Token상의 UserType이 PASSENGER가 아닐 경우

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "REQUEST_ONLY_PASSENGER",
   "message": "승객만 요청할 수 있는 기능 입니다."
}
```

#### Or

**Condition** : 필수 입력값이 누락되었거나 100자를 초과하였을 경우

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "ERROR_ADDRESS",
   "message": "주소값이 없거나 100자를 넘었습니다."
}
```

## POST /api/call/assign
기사 배차
```
curl -X POST \
  http://localhost:9876/api/call/assign \
  -H 'Authorization: Bearer JWT_TOKEN' \
  -H 'Content-Type: application/json' \
  -H 'cache-control: no-cache' \
  -d '{
  "id": 10
}'
  
 
// SAMPLE_JWT_TOKEN
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoidGVzdFVzZXIiLCJleHAiOjE4Njg5NDYzMjcsImRlc2MiOiLthYzsiqTtirjsmqkgRGVtb0FwaeyXkOyEnCDrsJztlokifQ.r8RLJgRVvUyf1TecScqJMQPF_JMTF0vYFHDkE9_uPjI
```

**Method** : `POST`

**Auth required** : YES

**Body constraints**

```json
{
    "id": "[NUMBER format]"
}
```
**Body example** 1개의 필수 입력 프로퍼티로 구성

```json
{
    "id": 10
}
```
### Success Responses

**Code** : `200 OK`

```json
{
    "code": "SUCCESS",
    "message": "요청이 정상적으로 완료 되었습니다."
}
```

### Error Response
**Condition** : Token상의 UserType이 DRIVER가 아닐 경우

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "REQUEST_ONLY_DRIVER",
   "message": "기사만 요청할 수 있는 기능 입니다."
}
```

#### Or

**Condition** : 필수 입력값이 누락되었을 경우

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "ERROR_ID",
   "message": "ID값이 없습니다."
}
```

#### Or

**Condition** : 존재하지 않는 ID일 경우

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "NOT_FOUND_CALL_ID",
   "message": "존재 하지 않는 ID 입니다."
}
```

#### Or

**Condition** : 이미 할당된 배차일 경우

**Code** : `400 Bad Request`

**Content** : 
```json
{
   "code": "ALREADY_ASSIGNED",
   "message": "이미 할당된 배차입니다."
}
```