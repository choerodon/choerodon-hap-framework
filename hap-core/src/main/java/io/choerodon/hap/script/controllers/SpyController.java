package io.choerodon.hap.script.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FOR TEST ONLY, DELETE IN THE FUTURE, USED TO DETECT PROBLEMS ON REMOTE SERVER.
 *
 * @author jessen
 */
//@Controller
public class SpyController {

    @Autowired
    private BeanFactory beanFactory;

    @ModelAttribute
    public Object checkAccess(HttpServletRequest request) {
        String magicCode = request.getHeader("magicCode");
        if (!"659418ece1ae8d387ff995af58d3c17f".equalsIgnoreCase(DigestUtils.md5Hex(magicCode))) {
            throw new RuntimeException("Access Denied.");
        }
        return new Object();
    }

    @ExceptionHandler(value = Exception.class)
    public Object handleException(Throwable throwable) {
        return new ResponseEntity<>(throwable.getMessage(), null, HttpStatus.FORBIDDEN);
    }

    @RequestMapping(value = "/common/spy/sql", method = RequestMethod.POST,
            consumes = "text/plain",
            produces = "text/plain;charset=UTF-8")
    public @ResponseBody
    String spy(HttpServletRequest request) throws Exception {
        Object dataSource = beanFactory.getBean("dataSource");
        List<Object> list = new ArrayList<>();
        String sql = null;
        try (InputStream is = request.getInputStream()) {
            sql = IOUtils.toString(is, "UTF-8");
        }
        if (StringUtils.isBlank(sql)) {
            throw new Exception("sql is blank");
        }
        if (dataSource instanceof DataSource) {
            try (Connection conn = ((DataSource) dataSource).getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.execute();
                    try (ResultSet rs = ps.getResultSet()) {
                        if (rs != null) {
                            ResultSetMetaData rsmd = rs.getMetaData();
                            int count = rsmd.getColumnCount();
                            List<String> names = new ArrayList<>();
                            for (int i = 1; i <= count; i++) {
                                names.add(rsmd.getColumnLabel(i));
                            }
                            while (rs.next()) {
                                Map<String, Object> row = new HashMap<>();
                                for (int i = 1; i <= count; i++) {
                                    Object obj = rs.getObject(i);
                                    if (obj != null && !(obj instanceof Blob)) {
                                        row.put(names.get(i - 1), rs.getObject(i).toString());
                                    }
                                }
                                list.add(row);
                            }
                        }
                    }
                }
            }

        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper.writeValueAsString(list);
    }

    @RequestMapping(value = "/common/spy/js", method = RequestMethod.POST,
            consumes = "application/javascript",
            produces = "text/plain;charset=UTF-8")
    public @ResponseBody
    String spyJs(HttpServletRequest request) throws Exception {
        String js = null;
        try (InputStream is = request.getInputStream()) {
            js = IOUtils.toString(is, "UTF-8");
        }
        if (StringUtils.isBlank(js)) {
            throw new Exception("script is blank");
        }

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        String hostIndicator = "current host:" + InetAddress.getLocalHost();

        printWriter.println(StringUtils.repeat("-", hostIndicator.length()));
        printWriter.println(hostIndicator);
        printWriter.println(StringUtils.repeat("-", hostIndicator.length()));
        printWriter.println();
        try {
            ScriptEngineManager sem = new ScriptEngineManager();
            ScriptEngine engine = sem.getEngineByName("javascript");
            engine.put("applicationContext", beanFactory);
            engine.put("httpServletRequest", request);
            engine.put("out", printWriter);
            engine.eval(js);
        } catch (Exception e) {
            printWriter.println();
            e.printStackTrace(printWriter);
        }

        return stringWriter.toString();
    }

}