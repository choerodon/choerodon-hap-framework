package io.choerodon.hap.api.gateway.util;

import io.choerodon.hap.api.ApiConstants;

import javax.wsdl.Binding;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap12.SOAP12Binding;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * WSDL工具类.
 *
 * @author peng.jiang@hand-china.com
 * @since 2017/9/23.
 */
public class WsdlUtils {

    public static String getSOAPVersion( Binding binding ) {
        if (isSoap11(binding)){
            return ApiConstants.SOAP11;
        }else if (isSoap12(binding)){
            return ApiConstants.SOAP12;
        }
        return null;
    }

    public static boolean isSoap11(Binding binding){
        List<?> list = binding.getExtensibilityElements();
        SOAPBinding soapBinding = getExtensiblityElement(list, SOAPBinding.class);
        return soapBinding == null ? false
                : (soapBinding.getTransportURI().startsWith(ApiConstants.SOAP_HTTP_TRANSPORT) || soapBinding
                .getTransportURI().startsWith(ApiConstants.SOAP_MICROSOFT_TCP));
    }

    public static boolean isSoap12(Binding binding){
        List<?> list = binding.getExtensibilityElements();
        SOAP12Binding soapBinding = getExtensiblityElement(list, SOAP12Binding.class);
        return soapBinding == null ? false : soapBinding.getTransportURI().startsWith(ApiConstants.SOAP_HTTP_TRANSPORT)
                || soapBinding.getTransportURI().startsWith(ApiConstants.SOAP12_HTTP_BINDING_NS)
                || soapBinding.getTransportURI().startsWith(ApiConstants.SOAP_MICROSOFT_TCP);
    }

    public static <T extends ExtensibilityElement> T getExtensiblityElement(List<?> list, Class<T> clazz) {
        List<T> elements = getExtensiblityElements(list, clazz);
        return elements.isEmpty() ? null : elements.get(0);
    }

    public static <T extends ExtensibilityElement> List<T> getExtensiblityElements(List list, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        for (Iterator<T> i = list.iterator(); i.hasNext(); ) {
            T elm = i.next();
            if (clazz.isAssignableFrom(elm.getClass())) {
                result.add(elm);
            }
        }
        return result;
    }


}
