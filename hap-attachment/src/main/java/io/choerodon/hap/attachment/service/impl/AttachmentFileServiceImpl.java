package io.choerodon.hap.attachment.service.impl;

import io.choerodon.hap.attachment.dto.SysFile;
import io.choerodon.hap.attachment.service.ISysFileService;
import io.choerodon.base.annotation.Dataset;
import io.choerodon.hap.dataset.exception.DatasetException;
import io.choerodon.hap.dataset.service.IDatasetService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static io.choerodon.hap.system.dto.DTOStatus.DELETE;

/**
 * @author jiameng.cao
 * @since 2019/1/9
 */
@Service
@Dataset("AttachFile")
public class AttachmentFileServiceImpl implements IDatasetService<SysFile> {
    @Autowired
    private ISysFileService iSysFileService;

    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        SysFile sysFile = new SysFile();
        Long categoryId;
        try {
            if (body.get("categoryId") == null) {
                categoryId = 0L;
            } else {
                categoryId = Long.valueOf(String.valueOf(body.get("categoryId")));
            }
            BeanUtils.populate(sysFile, body);
            sysFile.setSortname(sortname);
            sysFile.setSortorder(isDesc ? "desc" : "asc");
            return iSysFileService.selectFilesByCategoryId(null, sysFile, categoryId, page, pageSize);

        } catch (Exception e) {
            throw new DatasetException("dataset.error", e);
        }
    }

    @Override
    public List<SysFile> mutations(List<SysFile> sysFiles) {
        for (SysFile sysFile : sysFiles) {
            switch (sysFile.get__status()) {
                case DELETE:
                    iSysFileService.delete(null, sysFile);
                    break;
                default:
                    break;
            }
        }
        return sysFiles;
    }
}
