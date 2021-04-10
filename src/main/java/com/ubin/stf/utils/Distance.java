package com.ubin.stf.utils;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

public class Distance {
    public static double getDistance(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid){

        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);
        return geoCurve.getEllipsoidalDistance();
    }
    public static boolean isTrue(double lon1,double lat1,double lon2,double lat2,String radius){
        //我需要传入的经度 维度都小数点后保留6位
        //lat1，lon1为第一个人的维度和经度;lat2，lon2为第一个人的维度和经度
        GlobalCoordinates source = new GlobalCoordinates(lat1, lon1);
        GlobalCoordinates target = new GlobalCoordinates(lat2, lon2);
        //Sphere坐标系计算结果
        double distance = getDistance(source, target, Ellipsoid.WGS84);
        double r=Double.parseDouble(radius);

        if(distance>r){
            return false;
        }
        return true;
    }

    public static double getDistance(double lon1,double lat1,double lon2,double lat2){
        GlobalCoordinates source = new GlobalCoordinates(lat1, lon1);
        GlobalCoordinates target = new GlobalCoordinates(lat2, lon2);
        //Sphere坐标系计算结果
        return getDistance(source, target, Ellipsoid.WGS84);
    }


}
