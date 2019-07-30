package io.choerodon.hap.intergration.mapper;

import io.choerodon.hap.intergration.dto.HapInterfaceHeader;
import io.choerodon.hap.intergration.dto.HapInterfaceLine;
import io.choerodon.mybatis.common.Mapper;

import java.util.List;

/**
 * Created by user on 2016/7/26.
 */
public interface HapInterfaceLineMapper extends Mapper<HapInterfaceLine> {

    /*
    * 根据lineId和语言 获取LineAndLineTl
    * */
    public List<HapInterfaceLine> getLineAndLineTl(HapInterfaceLine interfaceLine);


    /*
    * 根据lineId获取headerAndLine
    * */
    HapInterfaceHeader getHeaderLineByLineId(String lineId);

    /*
    * 根据headerId 获取lines
    * */
    List<HapInterfaceLine> getLinesByHeaderId(HapInterfaceLine interfaceLine);

    int deleteByHeaderId(HapInterfaceLine hapInterfaceLine);

    int deleteTlByHeaderId(HapInterfaceLine hapInterfaceLine);

}
