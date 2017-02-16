<#include "templates/copyright.ftl"/>
package ${targetPackage};

<#if (table.hasDateColumn)>
import java.util.Date;
</#if>
<#if (table.hasBigDecimalColumn)>
import java.math.BigDecimal;
</#if>


/**
 * <p>实体类</p>
 * <p>Table: ${table.tableName} - ${table.remarks}</p>
 *
 * @since ${.now}
 */
public class ${table.className}Service {
<#list table.primaryKeys as key>

    /** ${key.columnName} - ${key.remarks} */
    private ${key.javaType} ${key.javaProperty};
</#list>
<#list table.baseColumns as column>

    /** ${column.columnName} - ${column.remarks} */
    private ${column.javaType} ${column.javaProperty};
</#list>

<#list table.primaryKeys as key>

    public ${key.javaType} ${key.getterMethodName}(){
        return this.${key.javaProperty};
    }
    public void ${key.setterMethodName}(${key.javaType} ${key.javaProperty}){
        this.${key.javaProperty} = ${key.javaProperty};
    }
</#list>
<#list table.baseColumns as column>

    public ${column.javaType} ${column.getterMethodName}(){
        return this.${column.javaProperty};
    }
    public void ${column.setterMethodName}(${column.javaType} ${column.javaProperty}){
        this.${column.javaProperty} = ${column.javaProperty};
    }
</#list>
}