package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Comment;
import com.gk.study.entity.RechargeRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RechargeRecordMapper extends BaseMapper<RechargeRecord> {

    List<RechargeRecord> getList();
}
