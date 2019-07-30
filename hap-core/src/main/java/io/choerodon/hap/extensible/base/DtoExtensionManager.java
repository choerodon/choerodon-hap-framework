/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.extensible.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.javassist.CannotCompileException;
import org.apache.ibatis.javassist.ClassClassPath;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.javassist.Modifier;
import org.apache.ibatis.javassist.NotFoundException;
import org.apache.ibatis.javassist.bytecode.AnnotationsAttribute;
import org.apache.ibatis.javassist.bytecode.AttributeInfo;
import org.apache.ibatis.javassist.bytecode.ConstPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.SAXException;

import io.choerodon.mybatis.entity.BaseDTO;


/**
 * @author shengyang.zhou@hand-china.com
 */
public class DtoExtensionManager {

    private static Map<String, List<DtoExtension>> dtoExtensionMap = new HashMap<>();

    static {
        ClassPool.getDefault().insertClassPath(new ClassClassPath(BaseDTO.class));
    }

    private static Logger logger = LoggerFactory.getLogger(DtoExtensionManager.class);

    public static void scanExtendConfig() throws Exception {
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] configs = patternResolver.getResources("classpath*:/extension/**/*.extend.xml");

        ClassPool classPool = ClassPool.getDefault();

        for (Resource resource : configs) {
            logger.debug("find dto extend config:{}", resource);
            List<DtoExtension> dtoExtensionList = parseConfig(resource.getInputStream());
            for (DtoExtension dtoExtension : dtoExtensionList) {
                List<DtoExtension> extensionList = dtoExtensionMap.get(dtoExtension.getTarget());
                if (extensionList == null) {
                    extensionList = new ArrayList<>();
                    dtoExtensionMap.put(dtoExtension.getTarget(), extensionList);
                }
                extensionList.add(dtoExtension);
            }
        }

        Map<String, CtClass> ctClassMap = new HashMap<>();

        dtoExtensionMap.forEach((dto, extensionList) -> {
            logger.debug("process dto extending : {} , {} extensions", dto, extensionList.size());
            try {
                CtClass ctClass = ctClassMap.get(dto);
                //1. get ctclass once
                if (ctClass == null) {
                    ctClass = classPool.getCtClass(dto);
                    ctClassMap.put(dto, ctClass);
                }
                ConstPool constPool = ctClass.getClassFile2().getConstPool();

                Set<String> existsMethods = new HashSet<>();
                //2. and do extends multi times
                for (DtoExtension dtoExtension : extensionList) {
                    logger.debug("+ interface {} -> {}", dtoExtension.getExtension(), dto);
                    CtClass ctClass2 = classPool.getCtClass(dtoExtension.getExtension());

                    CtMethod[] newMethod = ctClass2.getMethods();

                    for (CtMethod nm : newMethod) {
                        if (nm.getDeclaringClass() != ctClass2)
                            continue;
                        logger.debug("+    method {} -> {}", nm.getName(), dto);
                        CtMethod methodCopy = new CtMethod(nm.getReturnType(), nm.getName(), nm.getParameterTypes(),
                                ctClass);
                        List<AttributeInfo> attributes = nm.getMethodInfo().getAttributes();
                        for (AttributeInfo ai : attributes) {
                            if (ai instanceof AnnotationsAttribute) {
//                                byte[] info = ai.get();
//                                ((AnnotationsAttribute) ai).getAnnotations()[0].
//                                AnnotationsAttribute ai2 = new AnnotationsAttribute(constPool, ai.getName());
//                                ai2.setAnnotations(((AnnotationsAttribute) ai).getAnnotations());
                                methodCopy.getMethodInfo().addAttribute(ai);
                            }
                        }
                        // CtMethod methodCopy=CtMethod.make(nm.getMethodInfo(),ctClass2);

                        String name = methodCopy.getName();
                        String attributeName = StringUtils.uncapitalize(name.substring(3));

                        existsMethods.add(methodCopy.getName());
                        if (name.startsWith("set") && methodCopy.getParameterTypes().length == 1) {
                            methodCopy.setBody("innerSet(\"" + attributeName + "\",$1);");
                        } else if (name.startsWith("get") && methodCopy.getParameterTypes().length == 0) {
                            methodCopy.setBody("return (" + methodCopy.getReturnType().getName() + ")innerGet(\""
                                    + attributeName + "\");");
                        }
                        methodCopy.setModifiers(Modifier.PUBLIC);
                        ctClass.addMethod(methodCopy);

                    }

                    for (ExtendedField extendedField : dtoExtension.getExtendedFields()) {
                        addSetterGetter(ctClass, extendedField, existsMethods);
                    }
                    ctClass.addInterface(ctClass2);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // 前面可能会有多次修改，最后统一生成 class
        ctClassMap.forEach((k, v) -> {
            try {
                logger.debug("create class:" + k);
                v.toClass();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        });
    }

    private static void addSetterGetter(CtClass ctClass, ExtendedField extendedField, Set<String> existsMethods)
            throws NotFoundException, CannotCompileException {
        CtClass type = ClassPool.getDefault().get(extendedField.getJavaType());
        String getterName = "get" + StringUtils.capitalize(extendedField.getFieldName());
        if (!existsMethods.contains(getterName)) {
            logger.debug("+    getter for {} -> {}", extendedField.getFieldName(), ctClass.getName());
            CtMethod getter = new CtMethod(type, getterName, new CtClass[0], ctClass);
            getter.setBody(
                    "return (" + extendedField.getJavaType() + ")innerGet(\"" + extendedField.getFieldName() + "\");");
            ctClass.addMethod(getter);
            existsMethods.add(getterName);
        }

        String setterName = "set" + StringUtils.capitalize(extendedField.getFieldName());
        if (!existsMethods.contains(setterName)) {
            logger.debug("+    setter for {} -> {}", extendedField.getFieldName(), ctClass.getName());
            CtMethod setter = new CtMethod(CtClass.voidType, setterName, new CtClass[] { type }, ctClass);
            setter.setBody("innerSet(\"" + extendedField.getFieldName() + "\",$1);");
            ctClass.addMethod(setter);
            existsMethods.add(setterName);
        }
    }

    private static List<DtoExtension> parseConfig(InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException {
        try (InputStream ignore = inputStream) {
            return new ExtensionConfigParser().parse(inputStream);
        }
    }

    public static List<DtoExtension> getExtensionConfig(Class clazz) {
        return dtoExtensionMap.get(clazz.getName());
    }

}
