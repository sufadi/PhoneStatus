# PhoneStatus
- Git仓库地址
https://github.com/sufadi/PhoneStatus/tree/hongkong


## 1 需求
- user_id, type(walk, run, other), time, accelerometer（x,y,z）, linear_acceleration(x,y,z), gyroscope(x,y,z), magnetometer(x,y,z),orientation（azimuth,pitch,roll）
1. userid 只要能区分用户就行
2. type 可以测试的时候 让用户选择现在进行什么模式，然后开始采集对应数据，用0,1,2 表示
3. time  毫秒级时间戳
4. accelerometer（x,y,z）:accelerometer_x, accelerometer_y,accelerometer_z
5. linear_acceleration（x,y,z）:linear_acceleration_x, linear_acceleration_y,linear_acceleration_z
7. gyroscope（x,y,z）:gyroscope_x, gyroscope_y,gyroscope_z
8. magnetometer（x,y,z）:magnetometer_x, magnetometer_y,magnetometer_z
9. orientation（azimuth,pitch,roll）:azimuth, pitch,roll
PS：当传感器不存在时使用getOrientation(float[] R, float values[])代替

#### 1.1 其他要求
1. SensorManager.SENSOR_DELAY_GAME(20000微秒),即设定频率是50次/s;
2. 为了取数据训练模型,Sensor 使用wakeup类型;
3. 新增定时器

## 2 功能说明
实时监听所有传感器数据，并保存到 SD card 中，用于导出分析;

## 3.APK下载
https://github.com/sufadi/PhoneStatus/tree/hongkong/APK

## 4.测试运行
```
user_id,type,time,accelerometer_x,accelerometer_y,accelerometer_z,linear_acceleration_x,linear_acceleration_y,linear_acceleration_z,gyroscope_x,gyroscope_y,gyroscope_z,magnetometer_x,magnetometer_y,magnetometer_z,azimuth,pitch,roll
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613256,-1.7233124,0.3521519,9.680219,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613276,-1.7189629,0.34475023,9.70807,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613277,-1.7117807,0.35672072,9.705676,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613278,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613279,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,73.976944,-100.39596,46.608376,0.0,0.0,0.0
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613280,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,73.976944,-100.39596,46.608376,-2.468132,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613283,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,73.976944,-100.39596,46.608376,-2.468132,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613284,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,73.976944,-100.39596,46.608376,-2.468132,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613285,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,73.976944,-100.39596,46.608376,-2.468132,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613286,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,73.976944,-100.39596,46.608376,-2.468132,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613295,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,74.552055,-102.85731,44.518562,-2.468132,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613296,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,74.552055,-102.85731,44.518562,-2.4782183,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613313,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,74.26565,-102.66261,45.448143,-2.4782183,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613314,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,74.26565,-102.66261,45.448143,-2.4781826,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613333,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,73.1414,-102.44381,47.000553,-2.4781826,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613334,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,73.1414,-102.44381,47.000553,-2.4824665,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613353,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,74.53073,-102.45629,46.67853,-2.4824665,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613355,-1.721357,0.34475023,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613364,-1.7165687,0.35672072,9.710465,0.0,0.0,0.0,0.0,0.0,0.0,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613366,-1.7189629,0.35432664,9.720041,0.0,0.0,0.0,0.0,0.0,0.0,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613367,-1.7189629,0.35432664,9.720041,0.0,0.0,0.0,1.603514E-4,8.7048183E-4,-0.001076649,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613367,-1.7189629,0.35432664,9.720041,0.0,0.0,0.0,1.603514E-4,8.7048183E-4,-0.001076649,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613368,-1.7189629,0.35432664,9.720041,0.0,0.0,0.0,1.603514E-4,8.7048183E-4,-0.001076649,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613369,-1.7189629,0.34475023,9.710465,0.0,0.0,0.0,1.603514E-4,8.7048183E-4,-0.001076649,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613370,-1.7189629,0.34953842,9.710465,0.0,0.0,0.0,1.603514E-4,8.7048183E-4,-0.001076649,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613371,-1.7165687,0.3615089,9.720041,0.0,0.0,0.0,1.603514E-4,8.7048183E-4,-0.001076649,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613372,-1.7165687,0.3615089,9.720041,1.411438E-4,0.007665813,0.009602547,1.603514E-4,8.7048183E-4,-0.001076649,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613373,-1.7165687,0.3615089,9.720041,2.5963783E-4,-0.0015874207,4.673004E-5,1.603514E-4,8.7048183E-4,-0.001076649,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613374,-1.7165687,0.3615089,9.720041,3.6871433E-4,0.0027484,6.580353E-5,1.603514E-4,8.7048183E-4,-0.001076649,74.53073,-102.45629,46.67853,-2.474565,-0.03494372,0.17544565
ffffffff-fb16-2f2b-ffff-ffffef05ac4a,0,1565675613375,-1.7165687,0.3615089,9.720041,3.6871433E-4,0.0027484,6.580353E-5,1.603514E-4,8.7048183E-4,-0.001076649,74.11182,-102.44423,46.98995,-2.474565,-0.03494372,0.17544565
```
