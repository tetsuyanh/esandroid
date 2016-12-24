package com.tetsuyanh.esandroid.entity;

/**
 * Created by tetsuyanh on 2016/12/18.
 */

public class Team {
    private final Integer id;
    private final String name;

    public Team(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Team)) {
            return false;
        }
        Team other = (Team)obj;
        return this.name.equals(other.name);
    }
}
