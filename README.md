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

#### 1.1 其他要求
1. SensorManager.SENSOR_DELAY_GAME(20000微秒), 50Hz,即设定频率是50次/s;
2. 为了取数据训练模型,Sensor 使用wakeup类型;

## 2 功能说明
实时监听所有传感器数据，并保存到 SD card 中，用于导出分析;

## 3.APK下载
https://github.com/sufadi/PhoneStatus/tree/hongkong/APK
