<?xml version="1.0" encoding="UTF-8" ?>

<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${package}.mapper.${xmlName}">
    <resultMap id="BaseResultMap" type="${package}.dto.${dtoName}">
  <#list columnsInfo as infos>
        <result column="${infos.dBColumnsName!""}" property="${infos.tableColumnsName}" jdbcType="${infos.jdbcType}" />
    </#list>
    </resultMap>


</mapper>