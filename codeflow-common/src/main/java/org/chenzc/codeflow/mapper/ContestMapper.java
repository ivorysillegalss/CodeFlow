package org.chenzc.codeflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.chenzc.codeflow.domain.Contest;

@Mapper
public interface ContestMapper extends BaseMapper<Contest> {
}
