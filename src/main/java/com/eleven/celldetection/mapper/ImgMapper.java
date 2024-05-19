package com.eleven.celldetection.mapper;

import com.eleven.celldetection.entity.Img;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eleven.celldetection.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

}
