package com.eleven.celldetection.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MedicalRecord {

    private String department;
    private Date time;
    private String name;
    private String sex;
    private String phone_number;
    private String type;
    private String home;
    private String nation;
    private String chief_complaint;
    private String present_medical_history;
    private String past_history;
    private String physical_examination;
    private String preliminary_diagnosis;
    private String disposition;

    private String file_name;

}
