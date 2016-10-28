package com.bullet.lab.gatherer.dao;

import com.bullet.lab.gatherer.dao.entity.MedicalDataEntity;

/**
 * Created by pudongxu on 16/10/27.
 */
public interface MedicalDataDao {
    void insertMedicalData(MedicalDataEntity entity);

    MedicalDataEntity getMedicalData(int id);
}
