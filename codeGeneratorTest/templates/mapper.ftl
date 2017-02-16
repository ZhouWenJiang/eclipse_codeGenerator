<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="${beanPackage}.${table.className}" >
  <resultMap id="BaseResultMap" type="${beanPackage}.${table.className}" >
	<#list table.primaryKeys as col>
	<id column="${col.columnName}" property="${col.javaProperty}" jdbcType="${col.jdbcTypeName}" />
	</#list>
	<#list table.baseColumns as col>
	<result column="${col.columnName}" property="${col.javaProperty}" jdbcType="${col.jdbcTypeName}" />
	</#list>
  </resultMap>
    <sql id="Base_Column_List" >
    <#list table.columns as col> 
    ${col.columnName}<#if col_has_next>,</#if>
    </#list>
  	</sql>
  
</mapper>