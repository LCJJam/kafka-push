# Kafka 를 통한 Push 발송기 모듈

## 아키텍처

Kafka를 중심으로 메시지 발송을 생산자(Producer), 컨슈머(Consumer), 발송 엔진으로 나누어 처리합니다. 주요 구성 요소는 다음과 같습니다.

구성 요소

1. API 서버:
> 푸시 발송 요청을 클라이언트(App/관리자 웹)로부터 수신.
> Kafka에 메시지를 발행(Producer 역할).

2. Kafka:
> 푸시 발송 요청 메시지를 저장하고, Consumer로 전달.  
> 토픽을 활용해 메시지를 논리적으로 분리.

3. Push Worker (Consumer)
> Kafka의 메시지를 수신하여 FCM/APNs로 푸시 발송 요청. 
> 실패 메시지 재처리 로직 포함.

4. FCM / APNs:
> 실제 디바이스로 메시지를 발송.

5. 모니터링 및 로깅:
> 발송 상태, 실패 이유를 추적.
> Prometheus와 Grafana로 실시간 대시보드 제공.

## Push Message

```json
{
  "userId": "12345",
  "deviceToken": "abcde12345",
  
  "platform": "android",
  "title": "할인 안내",
  "body": "오늘만 20% 할인!",
  "imageUrl": "https://example.com/promo.jpg",
  "actionUrl": "https://example.com/coupon",
  "priority": "high",
  "ttl": 3600,
  "scheduleTime": "2024-11-26T10:00:00Z"
}
```


## HTTP/2 최적화

Connection Pooling
- TCP 연결 재사용.

배치 전송
- HTTP 요청 수를 줄여 네트워크 부하 감소.

메시지 크기 최적화
- 불필요한 데이터 제거.
- FCM/APNs의 최대 메시지 크기는 약 4KB(FCM) 및 2KB(APNs).

권장 프로토콜:
- FCM과 APNs에서 권장하는 HTTP/2 프로토콜을 사용

---

### FCM
```http request
POST https://fcm.googleapis.com/v1/projects/myproject/messages:send
Authorization: Bearer {your-access-token}
Content-Type: application/json

{
  "message": {
    "token": "device_token_here",
    "notification": {
      "title": "Hello World",
      "body": "This is a test message"
    },
    "data": {
      "key1": "value1",
      "key2": "value2"
    }
  }
}

```

### APNs
```http request
POST https://api.push.apple.com/3/device/{device-token}
Authorization: Bearer {your-jwt-token}
Content-Type: application/json

{
  "aps": {
    "alert": {
      "title": "Hello World",
      "body": "This is a test notification"
    },
    "badge": 1,
    "sound": "default"
  }
}

```
