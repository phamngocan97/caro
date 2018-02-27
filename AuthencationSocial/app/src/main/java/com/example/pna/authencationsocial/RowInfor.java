package com.example.pna.authencationsocial;

/**
 * Created by PNA on 27/02/2018.
 */

public class RowInfor {
    private int cnt;
    private String name;

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RowInfor(int cnt, String name) {

        this.cnt = cnt;
        this.name = name;
    }
}
