package com.eleven.celldetection.mapper;

import com.eleven.celldetection.entity.Img;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eleven.celldetection.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author pjqdyd
 * @since 2024-05-12
 */
@Mapper
public interface ImgMapper extends BaseMapper<Img> {

    @Select("select filepath from img where userid = #{userid} ; ")
    List<String> getFilepathByUserid(int userid);

    @Select("select username from cell_detection.user where id = #{userid} ; ")
    String getUserNameByUserid(int userid);

    @Select("select age from cell_detection.user where id = #{userid} ; ")
    Integer getageByUserid(int userid);

}
