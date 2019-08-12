# PhoneStatus
## 1 功能说明
实时监听所有传感器数据，并保存到 SD card 中，用于导出分析;

## 2.APK下载
https://github.com/sufadi/PhoneStatus/tree/master/APK

## 3.运行截图
#### 3.1 主界面
![主界面](https://raw.githubusercontent.com/sufadi/PhoneStatus/master/screenshot/20190812.png)
#### 3.2 数据结果
![数据结果](https://raw.githubusercontent.com/sufadi/PhoneStatus/master/screenshot/20190812-savePath2.png)

## 4. 运行时数据
#### 4.1 传感器参数信息
例如 SensorInfo.log
'''
SensorType: 1, Name: lsm6ds3c Accelerometer Wakeup, Description: {Sensor name="lsm6ds3c Accelerometer Wakeup", vendor="STMicro", version=140549, type=1, maxRange=78.4532, resolution=0.0023928226, power=0.15, minDelay=2404}
SensorType: 1, Name: lsm6ds3c Accelerometer Non-wakeup, Description: {Sensor name="lsm6ds3c Accelerometer Non-wakeup", vendor="STMicro", version=140549, type=1, maxRange=78.4532, resolution=0.0023928226, power=0.15, minDelay=2404}
SensorType: 2, Name: ak0991x Magnetometer Wakeup, Description: {Sensor name="ak0991x Magnetometer Wakeup", vendor="akm", version=131118, type=2, maxRange=4912.0, resolution=0.15, power=1.1, minDelay=10000}
SensorType: 2, Name: ak0991x Magnetometer Non-wakeup, Description: {Sensor name="ak0991x Magnetometer Non-wakeup", vendor="akm", version=131118, type=2, maxRange=4912.0, resolution=0.15, power=1.1, minDelay=10000}
SensorType: 4, Name: lsm6ds3c Gyroscope Wakeup, Description: {Sensor name="lsm6ds3c Gyroscope Wakeup", vendor="STMicro", version=140549, type=4, maxRange=34.905556, resolution=0.0012216945, power=0.555, minDelay=2404}
SensorType: 4, Name: lsm6ds3c Gyroscope Non-wakeup, Description: {Sensor name="lsm6ds3c Gyroscope Non-wakeup", vendor="STMicro", version=140549, type=4, maxRange=34.905556, resolution=0.0012216945, power=0.555, minDelay=2404}
SensorType: 5, Name: tcs3707 Ambient Light Sensor Wakeup, Description: {Sensor name="tcs3707 Ambient Light Sensor Wakeup", vendor="ams AG", version=256, type=5, maxRange=1.0, resolution=0.1, power=0.09, minDelay=0}
SensorType: 5, Name: tcs3707 Ambient Light Sensor Non-wakeup, Description: {Sensor name="tcs3707 Ambient Light Sensor Non-wakeup", vendor="ams AG", version=256, type=5, maxRange=1.0, resolution=0.1, power=0.09, minDelay=0}
SensorType: 8, Name: stk_stk3x3x Proximity Sensor Wakeup, Description: {Sensor name="stk_stk3x3x Proximity Sensor Wakeup", vendor="sensortek", version=319, type=8, maxRange=5.0, resolution=0.0, power=0.1, minDelay=0}
SensorType: 8, Name: stk_stk3x3x Proximity Sensor Non-wakeup, Description: {Sensor name="stk_stk3x3x Proximity Sensor Non-wakeup", vendor="sensortek", version=319, type=8, maxRange=5.0, resolution=0.0, power=0.1, minDelay=0}
SensorType: 9, Name: gravity  Wakeup, Description: {Sensor name="gravity  Wakeup", vendor="qualcomm", version=1, type=9, maxRange=156.99008, resolution=0.1, power=0.515, minDelay=5000}
SensorType: 9, Name: gravity  Non-wakeup, Description: {Sensor name="gravity  Non-wakeup", vendor="qualcomm", version=1, type=9, maxRange=156.99008, resolution=0.1, power=0.515, minDelay=5000}
SensorType: 10, Name: linear_acceleration_wakeup, Description: {Sensor name="linear_acceleration_wakeup", vendor="qualcomm", version=1, type=10, maxRange=156.99008, resolution=0.1, power=0.515, minDelay=5000}
SensorType: 10, Name: linear_acceleration, Description: {Sensor name="linear_acceleration", vendor="qualcomm", version=1, type=10, maxRange=156.99008, resolution=0.1, power=0.515, minDelay=5000}
SensorType: 11, Name: Rotation Vector  Wakeup, Description: {Sensor name="Rotation Vector  Wakeup", vendor="qualcomm", version=1, type=11, maxRange=1.0, resolution=0.01, power=1.415, minDelay=5000}
SensorType: 11, Name: Rotation Vector  Non-wakeup, Description: {Sensor name="Rotation Vector  Non-wakeup", vendor="qualcomm", version=1, type=11, maxRange=1.0, resolution=0.01, power=1.415, minDelay=5000}
SensorType: 14, Name: ak0991x Magnetometer-Uncalibrated Wakeup, Description: {Sensor name="ak0991x Magnetometer-Uncalibrated Wakeup", vendor="akm", version=131118, type=14, maxRange=4912.0, resolution=0.15, power=1.1, minDelay=10000}
SensorType: 14, Name: ak0991x Magnetometer-Uncalibrated Non-wakeup, Description: {Sensor name="ak0991x Magnetometer-Uncalibrated Non-wakeup", vendor="akm", version=131118, type=14, maxRange=4912.0, resolution=0.15, power=1.1, minDelay=10000}
SensorType: 15, Name: Game Rotation Vector  Wakeup, Description: {Sensor name="Game Rotation Vector  Wakeup", vendor="qualcomm", version=1, type=15, maxRange=1.0, resolution=0.01, power=0.515, minDelay=5000}
SensorType: 15, Name: Game Rotation Vector  Non-wakeup, Description: {Sensor name="Game Rotation Vector  Non-wakeup", vendor="qualcomm", version=1, type=15, maxRange=1.0, resolution=0.01, power=0.515, minDelay=5000}
SensorType: 16, Name: lsm6ds3c Gyroscope-Uncalibrated Wakeup, Description: {Sensor name="lsm6ds3c Gyroscope-Uncalibrated Wakeup", vendor="STMicro", version=140549, type=16, maxRange=34.905556, resolution=0.0012216945, power=0.555, minDelay=2404}
SensorType: 16, Name: lsm6ds3c Gyroscope-Uncalibrated Non-wakeup, Description: {Sensor name="lsm6ds3c Gyroscope-Uncalibrated Non-wakeup", vendor="STMicro", version=140549, type=16, maxRange=34.905556, resolution=0.0012216945, power=0.555, minDelay=2404}
SensorType: 17, Name: sns_smd  Wakeup, Description: {Sensor name="sns_smd  Wakeup", vendor="qualcomm", version=1, type=17, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}
SensorType: 18, Name: pedometer  Wakeup, Description: {Sensor name="pedometer  Wakeup", vendor="qualcomm", version=1, type=18, maxRange=1.0, resolution=1.0, power=0.15, minDelay=0}
SensorType: 18, Name: pedometer  Non-wakeup, Description: {Sensor name="pedometer  Non-wakeup", vendor="qualcomm", version=1, type=18, maxRange=1.0, resolution=1.0, power=0.15, minDelay=0}
SensorType: 19, Name: pedometer  Wakeup, Description: {Sensor name="pedometer  Wakeup", vendor="qualcomm", version=1, type=19, maxRange=4.2949673E9, resolution=1.0, power=0.15, minDelay=0}
SensorType: 19, Name: pedometer  Non-wakeup, Description: {Sensor name="pedometer  Non-wakeup", vendor="qualcomm", version=1, type=19, maxRange=4.2949673E9, resolution=1.0, power=0.15, minDelay=0}
SensorType: 20, Name: sns_geomag_rv  Wakeup, Description: {Sensor name="sns_geomag_rv  Wakeup", vendor="qualcomm", version=1, type=20, maxRange=1.0, resolution=0.01, power=1.05, minDelay=10000}
SensorType: 20, Name: sns_geomag_rv  Non-wakeup, Description: {Sensor name="sns_geomag_rv  Non-wakeup", vendor="qualcomm", version=1, type=20, maxRange=1.0, resolution=0.01, power=1.05, minDelay=10000}
SensorType: 22, Name: sns_tilt  Wakeup, Description: {Sensor name="sns_tilt  Wakeup", vendor="qualcomm", version=1, type=22, maxRange=1.0, resolution=1.0, power=0.025, minDelay=0}
SensorType: 27, Name: Device Orientation  Wakeup, Description: {Sensor name="Device Orientation  Wakeup", vendor="qualcomm", version=1, type=27, maxRange=1.0, resolution=1.0, power=0.025, minDelay=0}
SensorType: 27, Name: Device Orientation  Non-wakeup, Description: {Sensor name="Device Orientation  Non-wakeup", vendor="qualcomm", version=1, type=27, maxRange=1.0, resolution=1.0, power=0.025, minDelay=0}
SensorType: 29, Name: stationary_detect_wakeup, Description: {Sensor name="stationary_detect_wakeup", vendor="qualcomm", version=1, type=29, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}
SensorType: 29, Name: stationary_detect, Description: {Sensor name="stationary_detect", vendor="qualcomm", version=1, type=29, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}
SensorType: 30, Name: motion_detect_wakeup, Description: {Sensor name="motion_detect_wakeup", vendor="qualcomm", version=1, type=30, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}
SensorType: 30, Name: motion_detect, Description: {Sensor name="motion_detect", vendor="qualcomm", version=1, type=30, maxRange=1.0, resolution=1.0, power=0.025, minDelay=-1}
SensorType: 35, Name: lsm6ds3c Accelerometer-Uncalibrated Wakeup, Description: {Sensor name="lsm6ds3c Accelerometer-Uncalibrated Wakeup", vendor="STMicro", version=140549, type=35, maxRange=78.4532, resolution=0.0023928226, power=0.15, minDelay=2404}
SensorType: 35, Name: lsm6ds3c Accelerometer-Uncalibrated Non-wakeup, Description: {Sensor name="lsm6ds3c Accelerometer-Uncalibrated Non-wakeup", vendor="STMicro", version=140549, type=35, maxRange=78.4532, resolution=0.0023928226, power=0.15, minDelay=2404}
SensorType: 33171002, Name: bu52053nvx Hall Effect Sensor Wakeup, Description: {Sensor name="bu52053nvx Hall Effect Sensor Wakeup", vendor="ROHM", version=393221, type=33171002, maxRange=1.0, resolution=1.0, power=0.005, minDelay=0}
SensorType: 33171002, Name: bu52053nvx Hall Effect Sensor Non-wakeup, Description: {Sensor name="bu52053nvx Hall Effect Sensor Non-wakeup", vendor="ROHM", version=393221, type=33171002, maxRange=1.0, resolution=1.0, power=0.005, minDelay=0}
'''
#### 4.2 传感器数据
例如 tcs3707 Ambient Light Sensor Wakeup_value.log
'''
Time    Screen    Value[0]    Value[1]    Value[2]    Value[3]    Value[4]    Value[5]    Value[6]    Value[7]
2019-08-12 15:58:38.978,on,94.954056,85.272,35.571,29.160002,28.664999,99.0,512.0,5685.6436
2019-08-12 15:58:39.090,on,107.41739,84.645004,34.719,28.44,27.884998,99.0,512.0,5697.833
2019-08-12 15:58:39.212,on,92.88224,85.063,35.571,29.160002,28.664999,99.0,512.0,5681.8726
2019-08-12 15:58:39.318,on,92.92357,85.272,35.571,29.340002,28.664999,99.0,512.0,5682.397
2019-08-12 15:58:39.433,on,94.995384,85.481,35.571,29.340002,28.664999,99.0,512.0,5686.165
2019-08-12 15:58:39.549,on,104.81953,84.854004,34.932,28.62,28.079998,99.0,512.0,5695.7173
2019-08-12 15:58:39.663,on,96.100266,85.69,35.571,29.340002,28.859999,99.0,512.0,5718.3965
2019-08-12 15:58:39.776,on,94.42798,85.69,35.784,29.340002,28.859999,99.0,512.0,5687.287
2019-08-12 15:58:39.889,on,106.89134,85.063,34.932,28.62,28.079998,99.0,512.0,5699.427
2019-08-12 15:58:40.009,on,94.02845,85.481,35.571,29.340002,28.859999,99.0,512.0,5714.7363
'''



