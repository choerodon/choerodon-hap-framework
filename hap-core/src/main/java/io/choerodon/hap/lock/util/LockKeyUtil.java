package io.choerodon.hap.lock.util;

import io.choerodon.hap.lock.exception.LockException;
import io.choerodon.hap.system.dto.DTOClassInfo;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tk.mybatis.mapper.entity.EntityField;
import tk.mybatis.mapper.util.StringUtil;

/**
 * Created by Qixiangyu on 2017/1/10.
 */
public class LockKeyUtil {

    private static final Logger logger = LoggerFactory.getLogger(LockKeyUtil.class);

    public static final char UNDERLINE='_';

    /**
     *  驼峰式转下划线
     * @param param 驼峰命名的字符串
     * @return 数据库标准的下划线字段名
     */
    public static String camelToUnderline(String param){
        if (param==null||"".equals(param.trim())){
            return "";
        }
        int len=param.length();
        StringBuilder sb=new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c=param.charAt(i);
            if (Character.isUpperCase(c)){
                sb.append(UNDERLINE);
                sb.append(Character.toLowerCase(c));
            }else{
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     *  返回一个  表名(类限定名):id1=xxx&id2=xxx...
     * @param lockKey
     * @return 生成lockKey错误会抛异常，保证返回是正确的
     */
    public static String getLockKey(Object lockKey){
        StringBuffer lockKeyStr = new StringBuffer();
        String tableName = DTOClassInfo.getTableName(lockKey.getClass());
        if(StringUtil.isEmpty(tableName)){
            tableName = lockKey.getClass().getName();
        }
        lockKeyStr.append(tableName+":");
        EntityField[] fields = DTOClassInfo.getIdFields(lockKey.getClass());
        if(fields.length == 0){
            logger.error("getIdFields error, maybe not have @ID");
            throw new RuntimeException(new LockException(LockException.ERROR_LOCK_FAILURE,null));
        }
        String ids = "";
        for(EntityField field : fields){
            try {
                Object object =  PropertyUtils.getProperty(lockKey, field.getName());
                if(object == null){
                    logger.error("get id's value failure,confirm Object has id value");
                    throw new RuntimeException(new LockException(LockException.ERROR_LOCK_FAILURE,null));
                }else{
                    ids += field.getName()+"="+object.toString() +"&";
                }
            } catch (Exception e){
                logger.error(LockException.ERROR_LOCKKEY_FAILURE,e);
                throw new RuntimeException(new LockException(LockException.ERROR_LOCK_FAILURE,null));
            }
        }
        ids = ids.substring(0, ids.length() - 1);
        lockKeyStr.append(ids);
        return lockKeyStr.toString();
    }

}
