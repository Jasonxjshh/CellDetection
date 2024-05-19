package com.eleven.celldetection.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author pjqdyd
 * @since 2024-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value ="img",schema ="cell_detection")
public class Img implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String fileName;

    private String time;

    private String state;

    private String filepath;


    public void setFile_name(String fileName) {

        this.fileName = fileName ; 

    }

    public void setFilePath(String filePath) {

        this.filepath = filePath ;

    }
}
