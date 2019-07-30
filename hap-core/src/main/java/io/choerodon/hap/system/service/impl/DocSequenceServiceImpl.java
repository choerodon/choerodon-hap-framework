package io.choerodon.hap.system.service.impl;

import io.choerodon.hap.system.dto.DocSequence;
import io.choerodon.hap.system.mapper.DocSequenceMapper;
import io.choerodon.hap.system.service.IDocSequenceService;
import io.choerodon.web.core.IRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author runbai.chen
 */
@Service
@Transactional
public class DocSequenceServiceImpl implements IDocSequenceService {
    @Autowired
    private DocSequenceMapper docSequenceMapper;

    @Override
    public DocSequence lockDocSequence(IRequest iRequest, DocSequence docSequence) {
        return docSequenceMapper.lockDocSequence(docSequence);
    }

    @Override
    public DocSequence insertDocSequence(IRequest iRequest, DocSequence docSequence) {
        docSequenceMapper.insert(docSequence);
        return docSequence;
    }

    @Override
    public DocSequence updateDocSequence(IRequest iRequest, DocSequence docSequence) {
        docSequenceMapper.update(docSequence);
        return docSequence;
    }

    @Override
    public DocSequence processDocSequence(IRequest iRequest, DocSequence docSequence, Long initValue) {
        DocSequence resultDocSequence = self().lockDocSequence(iRequest, docSequence);
        if (resultDocSequence != null && resultDocSequence.getNextSeqNumber() != null) {
            resultDocSequence.setNextSeqNumber(resultDocSequence.getNextSeqNumber() + 1);
            self().updateDocSequence(iRequest, resultDocSequence);
        } else {
            resultDocSequence = docSequence;
            resultDocSequence.setNextSeqNumber(initValue);
            self().insertDocSequence(iRequest, resultDocSequence);
        }
        return resultDocSequence;
    }

    @Override
    public String getSequence(IRequest iRequest, DocSequence docSequence, String docPrefix, int seqLength,
                              Long initValue) {
        String sequence;
        // 数据库更新

        docSequence = self().processDocSequence(iRequest, docSequence, initValue);

        // 获取序列
        String seqNumber = docSequence.getNextSeqNumber().toString();

        // 如果输出序列长度小于0，或者数据库序列长度>0
        if (seqLength <= 0 || seqNumber.length() > seqLength) {
            // 将前缀+序列
            sequence = docPrefix + seqNumber;
        } else {
            // 前缀+000+序列
            // sequence = docPrefix + getFixLengthString(seqLength, "0",
            // seqNumber);
            sequence = docPrefix + StringUtils.leftPad(seqNumber, seqLength, '0');
        }
        return sequence;
    }
}
