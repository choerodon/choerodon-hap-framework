/*
 * Copyright Hand China Co.,Ltd.
 */

package io.choerodon.hap.extensible.base;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * @author shengyang.zhou@hand-china.com
 */
public class ExtensionConfigParser {

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;

    public ExtensionConfigParser() throws ParserConfigurationException {
        builder = factory.newDocumentBuilder();
    }

    public List<DtoExtension> parse(InputStream inputStream) throws IOException, SAXException {
        Document dom = builder.parse(inputStream);

        Element root = dom.getDocumentElement();

        List<DtoExtension> list = new ArrayList<>();

        doOverChildren(root, element -> {
            DtoExtension dtoExtension = new DtoExtension();

            String targetClass = getAttributeRequired(element, DtoExtension.TARGET);
            dtoExtension.setTarget(targetClass);

            dtoExtension.setExtension(getAttributeRequired(element, DtoExtension.EXTENSION));

            NodeList nodeList = element.getChildNodes();
            if (nodeList != null && nodeList.getLength() != 0) {
                List<ExtendedField> extendedFieldList = new ArrayList<>();
                boolean fieldsFind = false;
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if ("fields".equals(node.getNodeName())) {
                        if (fieldsFind) {
                            throw new IllegalConfigException("can only have one 'fields'", node);
                        }
                        fieldsFind = true;
                        doOverChildren(node, n -> {
                            if (!"field".equals(n.getNodeName()))
                                return;
                            extendedFieldList.add(createExtendedField(n));
                        });
                    }

                }
                dtoExtension.setExtendedFields(extendedFieldList);
            }

            list.add(dtoExtension);

        });

        return list;
    }

    private String getAttributeRequired(Node node, String name) {
        NamedNodeMap attMap = node.getAttributes();
        if (attMap == null) {
            throw new PropertyRequiredException(name, node);
        }
        Node item = attMap.getNamedItem(name);
        if (item == null) {
            throw new PropertyRequiredException(name, node);
        }
        String value = item.getNodeValue();
        if (StringUtils.isEmpty(value)) {
            throw new PropertyRequiredException(name, node);
        }
        return value;
    }

    private void doOverChildren(Node node, Consumer<Node> consumer) {
        NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node c = nodeList.item(i);
                if (c instanceof Text)
                    continue;
                consumer.accept(c);
            }
        }
    }

    private ExtendedField createExtendedField(Node node) {
        ExtendedField extendedField = new ExtendedField();

        String name = getAttributeRequired(node, "name");
        extendedField.setFieldName(name);

        String javaType = getAttributeRequired(node, "javaType");
        extendedField.setJavaType(javaType);

        String jdbcType = getAttributeRequired(node, "jdbcType");
        extendedField.setJavaType(jdbcType);

        return extendedField;
    }

    public static class IllegalConfigException extends RuntimeException {

        private Node node;

        public IllegalConfigException(String s, Node node) {
            super(s + ", node: " + node);
            this.node = node;
        }

        public Node getNode() {
            return node;
        }
    }

    public static class PropertyRequiredException extends RuntimeException {
        private Node node;

        public PropertyRequiredException(String p, Node node) {
            super("property '" + p + "' is required, node: " + node);
            this.node = node;
        }

        public Node getNode() {
            return node;
        }
    }
}
