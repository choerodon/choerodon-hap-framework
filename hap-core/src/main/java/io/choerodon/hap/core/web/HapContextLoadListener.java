/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.core.web;

import io.choerodon.hap.extensible.base.DtoExtensionManager;
import org.apache.ibatis.javassist.CannotCompileException;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.NotFoundException;
import tk.mybatis.mapper.util.OGNL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class HapContextLoadListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try {
            DtoExtensionManager.scanExtendConfig();
            dynamicCreateOGNL();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * dynamic create a class OGNL, extends OGNL
     * <p>
     * purpose: can use
     * 
     * <pre>
     * &#64;OGNL@xx()
     * </pre>
     * 
     * in ognl expression
     * <p>
     * short for
     * 
     * <pre>
     * &#64;OGNL@xx()
     * </pre>
     * 
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private void dynamicCreateOGNL() throws NotFoundException, CannotCompileException {
        ClassPool classPool = ClassPool.getDefault();
        CtClass ctClass = classPool.get(OGNL.class.getName());
        CtClass newOgnl = classPool.makeClass("OGNL", ctClass);
        newOgnl.toClass();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
