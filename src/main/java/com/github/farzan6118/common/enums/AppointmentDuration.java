package com.github.farzan6118.common.enums;

import lombok.Getter;

@Getter
public enum AppointmentDuration {

    TEN_MINUTES(10),
    FIFTEEN_MINUTES(15),
    THIRTY_MINUTES(30),
    FORTY_FIVE_MINUTES(45),
    ONE_HOUR(60),
    NINETY_MINUTES(90),
    TWO_HOURS(120);

    private final int minutes;

    AppointmentDuration(int minutes) {
        this.minutes = minutes;
    }

    public static AppointmentDuration fromMinutes(int minutes) {
        for (AppointmentDuration duration : values()) {
            if (duration.minutes == minutes) {
                return duration;
            }
        }
        throw new IllegalArgumentException("Unsupported appointment duration: " + minutes);
    }

}