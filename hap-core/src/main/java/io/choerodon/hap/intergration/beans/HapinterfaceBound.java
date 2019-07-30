package io.choerodon.hap.intergration.beans;

import io.choerodon.hap.intergration.dto.HapInterfaceInbound;
import io.choerodon.hap.intergration.dto.HapInterfaceOutbound;

import java.io.Serializable;

/**
 * @author xiangyu.qi@hand-china.com on 2017/9/23.
 */
public class HapinterfaceBound implements Serializable {

    private HapInterfaceInbound inbound;

    private HapInterfaceOutbound outbound;


    public HapinterfaceBound(){

    }

    public HapinterfaceBound(HapInterfaceInbound inbound){
        this.inbound = inbound;
    }

    public HapinterfaceBound(HapInterfaceOutbound outbound){
        this.outbound = outbound;
    }

    public HapInterfaceInbound getInbound() {
        return inbound;
    }

    public void setInbound(HapInterfaceInbound inbound) {
        this.inbound = inbound;
    }

    public HapInterfaceOutbound getOutbound() {
        return outbound;
    }

    public void setOutbound(HapInterfaceOutbound outbound) {
        this.outbound = outbound;
    }
}
