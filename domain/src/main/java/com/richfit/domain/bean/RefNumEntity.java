package com.richfit.domain.bean;

/**
 * Created by monday on 2016/11/4.
 */

public class RefNumEntity {
    private String moveType;
    private String resCreator;
    private String reservationNum;

    public String getMoveType() {
        return moveType;
    }

    public void setMoveType(String moveType) {
        this.moveType = moveType;
    }

    public String getResCreator() {
        return resCreator;
    }

    public void setResCreator(String resCreator) {
        this.resCreator = resCreator;
    }

    public String getReservationNum() {
        return reservationNum;
    }

    public void setReservationNum(String reservationNum) {
        this.reservationNum = reservationNum;
    }

    @Override
    public String toString() {
        return "RefNumEntity{" +
                "moveType='" + moveType + '\'' +
                ", resCreator='" + resCreator + '\'' +
                ", reservationNum='" + reservationNum + '\'' +
                '}';
    }
}
