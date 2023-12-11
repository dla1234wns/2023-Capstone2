#include <DHT.h>        // 온습도 센서 라이브러리 참고를 위한 헤더파일 선언
#include <SoftwareSerial.h>
#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <WiFiClient.h>

#define DHTPIN    2      // 아두이노의 8번 핀에 연결된 온습도 센서와 연결
#define DHTTYPE   DHT22   // DHT-22 센서의 유형으로 선택

#define Relaypin 4

DHT dht(DHTPIN, DHTTYPE); // DHT라이브러리를 통해 핀과 타입을 설정

int Led = 14;
int buttonpin = 0;
int val;

const char* ssid = "LJH"; // Wi-Fi SSID
const char* password = "1357924680"; // Wi-Fi Password
const char* postUrl = "http://203.250.133.141:8080/arduino/sensor_upload/"; // 서버 URL
const char* getUrl = "http://203.250.133.141:8080/arduino/sensor_get/";


WiFiClient client;

void setup() {


  pinMode (Led, OUTPUT) ;// define LED as output interface
  pinMode (buttonpin, INPUT) ;// output interface as defined Reed sensor

  pinMode (Relaypin, OUTPUT);
  

  Serial.begin(9600);

  dht.begin();          // 온습도센서 초기화

      // Wi-Fi 연결
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");
}



void loop() {
  // put your main code here, to run repeatedly:


  float temperature_f = dht.readTemperature();  // 온도 측정
  float humidity_f = dht.readHumidity();        // 습도 측정
  int relayStatus = 1;
  int leadSwitch = 0;

  int temperature = round(temperature_f);  // 온도 정수로 반올림 해서 변환
  int humidity = round(humidity_f);        // 습도 정수로 반올림 해서 변환

  val = digitalRead (buttonpin) ;// digital interface will be assigned a value of 3 to read val

  WiFiClient client;
  HTTPClient http;
  http.begin(client, getUrl + String("333") + "/");
  http.addHeader("Content-Type", "application/json");
  
  int httpResponseCode5 = http.GET();
  
  if (httpResponseCode5 > 0) {
    Serial.printf("HTTP Response code: %d\n", httpResponseCode5);
    String payload = http.getString(); // 릴레이 스위치 응답값
    Serial.println("Server response: " + payload);

    String serverResponse = payload;

    DynamicJsonDocument jsonDoc(1024); // JSON 문서를 저장할 버퍼 크기 조절

    deserializeJson(jsonDoc, serverResponse);

    int deviceValue = jsonDoc["device_value"];

    Serial.print("Device Value: ");
    Serial.println(deviceValue);
    relayStatus = deviceValue;
    delay(1000);
    } 

  else {
    Serial.println("HTTP Request failed");
    }

  http.end();

  if (val == HIGH) {
    digitalWrite (Led, HIGH);
    Serial.print("리드스위치: 문 열림, value: ");
    leadSwitch = 0;
    Serial.println(leadSwitch);
    }

  else {
    digitalWrite (Led, LOW);
    Serial.print("리드스위치: 문 닫힘, value: ");
    leadSwitch = 1;
    Serial.println(leadSwitch);
    }
  

  if(relayStatus == 1) {
    Serial.print("릴레이 스위치 켜짐, relayStatus: ");
    Serial.println(relayStatus);
    digitalWrite(Relaypin,HIGH);
    delay(500);
  }

  else {
    Serial.print("릴레이 스위치 꺼짐, relayStatus: ");
    Serial.println(relayStatus);
    digitalWrite(Relaypin,LOW);
    delay(500);
  }


  // 시리얼 모니터에 온도 출력
  Serial.print("Temperature: ");
  Serial.print(temperature);

  // 시리얼 모니터에 습도 출력
  Serial.print(", Humidity: ");
  Serial.println(humidity);
  
  int Lux_Value = analogRead(A0);
  Serial.print("lux value: ");
  Serial.println(Lux_Value);



    http.begin(client, postUrl + String("1") + "/" + String("111") + "/" + String(temperature) + "/");
    http.addHeader("Content-Type", "application/json");
  
    int httpResponseCode = http.PUT("");
  
    if (httpResponseCode > 0) {
      Serial.printf("HTTP Response code: %d\n", httpResponseCode);
      String payload = http.getString(); // 서버로부터 받은 응답값
      Serial.print("Server response: " + payload);
    } else {
      Serial.println("HTTP Request failed");
    }
    

    http.begin(client, postUrl + String("2") + "/" + String("222") + "/" + String(humidity) + "/");
    http.addHeader("Content-Type", "application/json");
  
    int httpResponseCode2 = http.PUT("");
  
    if (httpResponseCode2 > 0) {
      Serial.printf("HTTP Response code: %d\n", httpResponseCode2);
      String payload = http.getString(); // 서버로부터 받은 응답값
      Serial.print("Server response: " + payload);
    } else {
      Serial.println("HTTP Request failed");
    }

    http.begin(client, postUrl + String("3") + "/" + String("333") + "/" + String(relayStatus) + "/");
    http.addHeader("Content-Type", "application/json");
  
    int httpResponseCode3 = http.PUT("");
  
    if (httpResponseCode3 > 0) {
      Serial.printf("HTTP Response code: %d\n", httpResponseCode3);
      String payload = http.getString(); // 서버로부터 받은 응답값
      Serial.print("Server response: " + payload);
    } else {
      Serial.println("HTTP Request failed");
    }

    http.begin(client, postUrl + String("4") + "/" + String("444") + "/" + String(leadSwitch) + "/");
    http.addHeader("Content-Type", "application/json");
  
    int httpResponseCode4 = http.PUT("");
  
    if (httpResponseCode4 > 0) {
      Serial.printf("HTTP Response code: %d\n", httpResponseCode4);
      String payload = http.getString(); // 서버로부터 받은 응답값
      Serial.print("Server response: " + payload);
    } else {
      Serial.println("HTTP Request failed");
    }
    
    http.end(); 

    Serial.println("---------");
    Serial.println("");

  
    delay(5000); // 5초마다 전송
  }