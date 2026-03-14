package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Dynamic;
import com.gk.study.entity.Thing;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DynamicMapper extends BaseMapper<Dynamic> {


}
