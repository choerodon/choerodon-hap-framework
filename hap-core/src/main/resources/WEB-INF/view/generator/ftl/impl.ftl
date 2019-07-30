package ${package}.service.impl;

<#list import as e>
import ${e};
</#list>
import ${package}.dto.${dtoName};
import ${package}.service.${serviceName};
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class ${implName} extends BaseServiceImpl<${dtoName}> implements ${serviceName}{

}