package com.facturasff.sff;

public class DownloadInfo {

    private String DownloadString;
    private String DownloadType;
    private String DownloadName;

    public DownloadInfo(String downloadString, String downloadType,String downloadName) {
        DownloadString = downloadString;
        DownloadType = downloadType;
        DownloadName = downloadName;
    }

    public String getDownloadString() {
        return DownloadString;
    }

    public void setDownloadString(String downloadString) {
        DownloadString = downloadString;
    }

    public String getDownloadType() {
        return DownloadType;
    }

    public void setDownloadType(String downloadType) {
        DownloadType = downloadType;
    }

    public String getDownloadName() {
        return DownloadName;
    }

    public void setDownloadName(String downloadName) {
        DownloadName = downloadName;
    }
}
