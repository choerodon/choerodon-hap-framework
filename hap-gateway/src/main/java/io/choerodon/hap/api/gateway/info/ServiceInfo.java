package io.choerodon.hap.api.gateway.info;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输类 - Service.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/21.
 **/

public class ServiceInfo {

    private String name;

    private String soapVersion;

    private String locationURI;

    private List<OperationInfo> operations = new ArrayList();


    public List<OperationInfo> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationInfo> operations) {
        this.operations = operations;
    }

    public void addOperation(OperationInfo operation) {
        operations.add(operation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSoapVersion() {
        return soapVersion;
    }

    public void setSoapVersion(String soapVersion) {
        this.soapVersion = soapVersion;
    }

    public String getLocationURI() {
        return locationURI;
    }

    public void setLocationURI(String locationURI) {
        this.locationURI = locationURI;
    }
}
