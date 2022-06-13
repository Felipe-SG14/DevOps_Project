#include <WiFi.h>
#include <HTTPClient.h>
#include <RBDdimmer.h>


int LEDS[]={23,22,21}; //Se definen los pines de salida para los leds

//Config para el foco con el dimmer
const int acdPin  = 26;
const int zeroCrossPin  = 25;
int power  = 0; //Alimentación para el foco
int valMax = 100; //Este valor varía dependiendo de a que rango de valores funciona el foco empleado
int valMin = 15; //Este valor varía dependiendo de a que rango de valores funciona el foco empleado
dimmerLamp acd(acdPin,zeroCrossPin);//Objects

//Datos para la conexión a internet
const char* ssid = "IZZI-DFA5"; //nombre de la red a conectar
const char* password = "F0AF85E8DFA5"; //contraseña de la red WiFi

//Conexión a servidor
String serverName = "https://davinci999.xyz"; //Nombre del servidor
unsigned long lastTime = 0;
// Timer set to 10 minutes (600000)
//unsigned long timerDelay = 600000;
// Set timer to 5 seconds (5000)
unsigned long timerDelay = 500;

//Variables que obtenemos del servidor
String idFoco[4];
String estadoFoco[4];
String intensidadFoco[4];

int antComa=-1;
int sigComa=-1;

/*//Señal PWM
const int canalPWM1=0;
int Frec = 100000;
int res = 8;*/

void setup() {
  Serial.begin(115200);
  acd.begin(NORMAL_MODE, OFF);
  for(int i=0; i<=2; i++)
  {
    pinMode(LEDS[i],OUTPUT);
  }
  //ledcSetup(canalPWM1, Frec, res);
  //ledcAttachPin(LEDS[3], canalPWM1);
  WiFiConnect();
}

void loop() {
  pathHTTP();
  antComa=-1;
  sigComa=-1;
}

void WiFiConnect()
{
  WiFi.begin(ssid, password);
  Serial.println("Connecting");
  while(WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi network with IP Address: ");
  Serial.println(WiFi.localIP());
 
  //Serial.println("Timer set to 5 seconds (timerDelay variable), it will take 5 seconds before publishing the first reading.");
}

void pathHTTP()
{
  //Send an HTTP POST request every 10 minutes
  if ((millis() - lastTime) > timerDelay) {
    //Check WiFi connection status
    if(WiFi.status()== WL_CONNECTED){
      
      HTTPClient http;

      String serverPath = serverName;
      serverPath += "/informacionESP.php";
      //Serial.println(serverPath);
      
      // Your Domain name with URL path or IP address with path
      http.begin(serverPath.c_str());
      
      // Send HTTP GET request
      int httpResponseCode = http.GET();
      
      if (httpResponseCode>0) {
        //Serial.print("HTTP Response code: ");
        //Serial.println(httpResponseCode);
        String payload = http.getString();
        //Serial.println(payload);

        //Separación de los valores de cada led
          int i=0;
          while(i<=3)
          {
            antComa=sigComa;
            sigComa = payload.indexOf(',', sigComa+1);
            idFoco[i] = payload.substring(antComa+1, sigComa);//sigComa no lo cuenta
            antComa=sigComa;
            sigComa = payload.indexOf(',', sigComa+1);
            estadoFoco[i] = payload.substring(antComa+1,sigComa);
            antComa=sigComa;
            sigComa = payload.indexOf(',', sigComa+1);
            intensidadFoco[i]=payload.substring(antComa+1,sigComa);
            i++;
          }
          
          for(int j=0; j<4; j++)
          {
            if(idFoco[j].toInt()==1)//Si es foco 1
            {
              if(estadoFoco[j].toInt()==1)//Si está encendido foco 1
              {
                digitalWrite(LEDS[idFoco[j].toInt()-1],HIGH);
              }else
              {
                digitalWrite(LEDS[idFoco[j].toInt()-1],LOW);
              }
            }
            if(idFoco[j].toInt()==2)//Si es foco 2
            {
              if(estadoFoco[j].toInt()==1)//Si está encendido foco 2
              {
                digitalWrite(LEDS[idFoco[j].toInt()-1],HIGH);
              }else
              {
                digitalWrite(LEDS[idFoco[j].toInt()-1],LOW);
              }
            }
            if(idFoco[j].toInt()==3)//Si es foco 3
            {
              if(estadoFoco[j].toInt()==1)//Si está encendido foco 3
              {
                digitalWrite(LEDS[idFoco[j].toInt()-1],HIGH);
              }else
              {
                digitalWrite(LEDS[idFoco[j].toInt()-1],LOW);
              }
            }
            if(idFoco[j].toInt()==4)//Si es foco 4
            {
              if(estadoFoco[j].toInt()==1)//Si está encendido foco 4
              {
                acd.setState(ON);
                power = (intensidadFoco[j].toInt() / 2.55) + valMin;
                //power = round((intensidadFoco[j].toInt() / 2.55) + valMin);
                if(power>=valMax)
                {
                  acd.setPower(valMax);
                  Serial.print("lampValue -> ");
                  Serial.print(acd.getPower());
                  Serial.println("%");
                }
                else
                {
                  acd.setPower(power);
                  Serial.print("lampValue -> ");
                  Serial.print(acd.getPower());
                  Serial.println("%");
                }
                //ledcWrite(canalPWM1, intensidadFoco[j].toInt()); //round(intensidadFoco[j].toInt()*2.55) conversión de 100 a 255
              }else
              {
                acd.setState(OFF);
                //ledcWrite(canalPWM1, 0);
              }
            }
            
          }//Fin del For, modificación de los focos en cada sala */
      }
      else {
        Serial.print("Error code: ");
        Serial.println(httpResponseCode);
      }
      // Free resources
      http.end();
    }
    else {
      Serial.println("WiFi Disconnected");
    }
    lastTime = millis();
  }
}
