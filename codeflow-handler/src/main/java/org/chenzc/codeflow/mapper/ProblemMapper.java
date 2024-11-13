package org.chenzc.codeflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.chenzc.codeflow.entity.Problem;

@Mapper
public interface ProblemMapper extends BaseMapper<Problem> {
}
