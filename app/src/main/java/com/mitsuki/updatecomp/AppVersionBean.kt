package com.mitsuki.updatecomp

/**
 *   private String publishTime;
private String updateLog;
private long timeStamp;
private boolean hasNewVersion;
private String newVersion;
private int newVersionCode;
private int compatibleVersionCode;
private boolean forceUpdate;
private String accessToken;
private String accessUrl;
private String fileName;
private String extension;
 */
data class AppVersionBean(
    val updateLog: String = "log",
    val hasNewVersion: Boolean = true,
    var forceUpdate: Boolean = false,
    val accessUrl: String = "http://static.122.gov.cn/group1/M00/07/A8/wJ4BOV2Ea8qAHpccAanh4Dvrnzo580.apk",
    var isSimulation: Boolean = true
)