package com.tct.phonedata.bean;

import com.tct.phonedata.utils.UuidUtils;

public class CustomSensorInfo {

    /**
     * #define SENSOR_TYPE_ACCELEROMETER       1 //加速度
     * #define SENSOR_TYPE_MAGNETIC_FIELD      2 //磁力
     * #define SENSOR_TYPE_ORIENTATION         3 //方向
     * #define SENSOR_TYPE_GYROSCOPE           4 //陀螺仪
     * #define SENSOR_TYPE_LIGHT               5 //光线感应
     * #define SENSOR_TYPE_PRESSURE            6 //压力
     * #define SENSOR_TYPE_TEMPERATURE         7 //温度
     * #define SENSOR_TYPE_PROXIMITY           8 //接近
     * #define SENSOR_TYPE_GRAVITY             9 //重力
     * #define SENSOR_TYPE_LINEAR_ACCELERATION 10//线性加速度
     * #define SENSOR_TYPE_ROTATION_VECTOR     11//旋转矢量
     */
    public boolean isFistLoading;

    /**
     * UUID
     */
    public String user_id;

    /**
     * Scene Mode
     */
    public int type;

    /**
     * Sensor.TYPE_ACCELEROMETER 加速度
     */
    public float accelerometer_x;

    public float accelerometer_y;

    public float accelerometer_z;

    /**
     * SENSOR_TYPE_LINEAR_ACCELERATION 线性加速度
     */
    public float linear_acceleration_x;

    public float linear_acceleration_y;

    public float linear_acceleration_z;

    /**
     * SENSOR_TYPE_GYROSCOPE 陀螺仪
     */
    public float gyroscope_x;

    public float gyroscope_y;

    public float gyroscope_z;

    /**
     * SENSOR_TYPE_MAGNETIC_FIELD
     */
    public float magnetometer_x;

    public float magnetometer_y;

    public float magnetometer_z;

    /**
     * SENSOR_TYPE_ORIENTATION
     */
    public float azimuth;

    public float pitch;

    public float roll;

    /**
     * SENSOR_TYPE_ORIENTATION_VECTOR
     */
    public float rotation_vector_x;

    public float rotation_vector_y;

    public float rotation_vector_z;

    public CustomSensorInfo(){
        user_id = UuidUtils.getUUIDByBuildId();
    }
}
