package io.choerodon.hap.api.gateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;

import io.choerodon.hap.api.ApiConstants;
import io.choerodon.hap.api.gateway.dto.ApiInterface;
import io.choerodon.hap.api.gateway.dto.ApiServer;
import io.choerodon.hap.api.gateway.info.OperationInfo;
import io.choerodon.hap.api.gateway.info.ServiceInfo;

import io.choerodon.hap.api.gateway.util.WsdlUtils;
import com.ibm.wsdl.ServiceImpl;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * WSDL解析类.
 *
 * @author peng.jiang@hand-china.com on 2017/9/23.
 */
public class WsdlParser {

	public ApiServer parseWSDL(ApiServer result , String wsdlURI) throws WSDLException {
		WSDLFactory wsdlFactory = WSDLFactory.newInstance();
		WSDLReader reader = wsdlFactory.newWSDLReader();
		Definition defintion = reader.readWSDL(wsdlURI);
		Map servicesMap = defintion.getAllServices();
		Set serviceKeys = servicesMap.keySet();

		//解析elementFormDefault值
		Types types = defintion.getTypes();
		List elements = types.getExtensibilityElements();
		// 默认types标签中只包含一个直接子标签
		ExtensibilityElement extensibilityElement = (ExtensibilityElement)elements.get(0);
		Element element = ((Schema) extensibilityElement).getElement();
		NamedNodeMap namedNodeMap = element.getAttributes();
		Node node = namedNodeMap.getNamedItem("elementFormDefault");
		String elementFormDefault = "unqualified";
		if (node != null){
			elementFormDefault = node.getNodeValue();
		}

		for (Iterator iterator = serviceKeys.iterator(); iterator.hasNext();) {
			Object key = iterator.next();
			ServiceImpl serviceImpl = (ServiceImpl)servicesMap.get(key);
			List<ServiceInfo> services = processService(serviceImpl);
			result.setCode(serviceImpl.getQName().getLocalPart());
			result.setNamespace(defintion.getTargetNamespace());
			result.setElementFormDefault(elementFormDefault);

			if ( services.size()==1 ){
				getApiServer(result, services.get(0));
			}else {
				//如果存在多个版本的soap接口，默认取soap1.2版本接口
				for(ServiceInfo serviceInfo : services) {
					if ( serviceInfo.getSoapVersion().equals(ApiConstants.SOAP12) ){
						getApiServer(result, serviceInfo);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 处理解析结果
	 * @param result
	 * @param serviceInfo
	 */
	private void getApiServer(ApiServer result, ServiceInfo serviceInfo){

		result.setName(serviceInfo.getName());
		result.setDomainUrl(serviceInfo.getLocationURI());
		result.setMappingUrl(result.getCode());
		result.setWssPasswordType(ApiConstants.WSS_PASSWORD_TYPE_NONE);

		List<ApiInterface> interfaces = result.getInterfaces();
		if (interfaces == null){
			interfaces = new ArrayList<>();
		}

		for (int i = 0; i < serviceInfo.getOperations().size(); i++) {
			ApiInterface srInterface = new ApiInterface();
			srInterface.setCode(serviceInfo.getOperations().get(i).getName());
			srInterface.setName(serviceInfo.getOperations().get(i).getName());
			srInterface.setInterfaceUrl(serviceInfo.getOperations().get(i).getName());
			srInterface.setSoapVersion(serviceInfo.getSoapVersion());
			srInterface.setSoapAction(serviceInfo.getOperations().get(0).getSoapActionURI());
			srInterface.setEnableFlag("Y");
			srInterface.setMappingUrl(srInterface.getCode());
			srInterface.setInvokeRecordDetails("N");
			interfaces.add(srInterface);
		}
		result.setInterfaces(interfaces);
	}

	/**
	 * 解析服务
	 * @param service
	 * @return
	 */
	private List<ServiceInfo> processService(ServiceImpl service) {
		List<ServiceInfo> services = new ArrayList();
		Collection ports = service.getPorts().values();
		for (Iterator iterator = ports.iterator(); iterator.hasNext();) {
			ServiceInfo serviceInfo = new ServiceInfo();
			Port port = (Port) iterator.next();
			//解析服务名称
			serviceInfo.setName(port.getName());
			Binding binding = port.getBinding();
			//解析soap版本
			String soapVersion = WsdlUtils.getSOAPVersion(binding);
			serviceInfo.setSoapVersion(soapVersion);

			if (soapVersion != null){
				//解析服务地址
				List elements = port.getExtensibilityElements();
				if (soapVersion.equals(ApiConstants.SOAP11)){
					SOAPAddress soapAddress = (SOAPAddress)elements.get(0);
					serviceInfo.setLocationURI(soapAddress.getLocationURI());
				}else {
					SOAP12Address soap12Address = (SOAP12Address)elements.get(0);
					serviceInfo.setLocationURI(soap12Address.getLocationURI());
				}

				List operations = binding.getBindingOperations();
				for (Iterator iterator2 = operations.iterator(); iterator2.hasNext(); ) {
					OperationInfo operationInfo = new OperationInfo();

					BindingOperation bindingOperation = (BindingOperation) iterator2.next();
					//解析接口名
					Operation operation = bindingOperation.getOperation();
					operationInfo.setName(operation.getName());

					//解析soapAction
					List operationElements = bindingOperation.getExtensibilityElements();
					if (soapVersion.equals(ApiConstants.SOAP11)){
						SOAPOperation soapOperation =  (SOAPOperation)operationElements.get(0);
						operationInfo.setSoapActionURI(soapOperation.getSoapActionURI());
					}else {
						SOAP12Operation soap12Operation =  (SOAP12Operation)operationElements.get(0);
						operationInfo.setSoapActionURI(soap12Operation.getSoapActionURI());
					}

					serviceInfo.addOperation(operationInfo);
				}
				services.add(serviceInfo);
			}
		}
		return services;
	}


}
