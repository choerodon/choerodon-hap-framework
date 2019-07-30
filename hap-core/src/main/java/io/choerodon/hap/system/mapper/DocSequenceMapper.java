/*
 * #{copyright}#
 */

package io.choerodon.hap.system.mapper;

import io.choerodon.hap.system.dto.DocSequence;

/**
 * @author wuyichu
 */
public interface DocSequenceMapper {

    DocSequence lockDocSequence(DocSequence docSequence);

    int insert(DocSequence docSequence);

    int update(DocSequence docSequence);
}