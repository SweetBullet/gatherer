package com.bullet.lab.gatherer.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by pudongxu on 16/10/27.
 */

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MedicalDataEntity {
    private String name;
    private String phone;
    private int age;
}
