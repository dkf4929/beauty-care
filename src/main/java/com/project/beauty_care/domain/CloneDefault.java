package com.project.beauty_care.domain;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public class CloneDefault implements Cloneable{
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
