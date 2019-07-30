package io.choerodon.hap.task.controllers;

import io.choerodon.hap.attachment.UpConstants;
import io.choerodon.hap.attachment.exception.AttachmentException;
import io.choerodon.hap.task.dto.TaskExecution;
import io.choerodon.hap.task.dto.TaskExecutionDetail;
import io.choerodon.hap.task.service.ITaskExecutionDetailService;
import io.choerodon.hap.task.service.ITaskExecutionService;
import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.web.controller.BaseController;
import io.choerodon.web.core.IRequest;
import io.choerodon.web.dto.ResponseData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 任务执行记录控制器.
 *
 * @author lijian.yin@hand-china.com
 **/

@Controller
@RequestMapping({"/sys/task/execution", "api/sys/task/execution"})
public class TaskExecutionController extends BaseController {
    private final Logger logger = LoggerFactory.getLogger(TaskExecutionController.class);
    /**
     * buffer 大小.
     */
    private static final Integer BUFFER_SIZE = 1024;

    /**
     * 文件流编码
     */
    private static final String ENC = "UTF-8";

    @Autowired
    private ITaskExecutionService iTaskExecutionService;

    @Autowired
    private ITaskExecutionDetailService taskExecutionDetailService;

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/query")
    @ResponseBody
    public ResponseData query(TaskExecution dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        boolean isAdmin = Boolean.valueOf(request.getParameter("isAdmin"));
        return new ResponseData(iTaskExecutionService.queryExecutions(iRequest, dto, isAdmin, page, pageSize));
    }

    @Permission(type = ResourceType.SITE)
    @GetMapping(value = "/admin/task_execution.html")
    public ModelAndView adminQuery(HttpServletRequest request) {
        ModelAndView mv = new ModelAndView(getViewPath() + "/task/task_execution");
        mv.addObject("isAdmin", true);
        return mv;
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/selectExecutionGroup")
    @ResponseBody
    public ResponseData queryExecutionGroup(@RequestBody TaskExecution taskExecution, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(iTaskExecutionService.queryExecutionGroup(iRequest, taskExecution));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/detail")
    @ResponseBody
    public ResponseData detailByEId(@RequestBody TaskExecution taskExecution, HttpServletRequest request) {
        IRequest iRequest = createRequestContext(request);
        return new ResponseData(iTaskExecutionService.queryExecutionDetail(iRequest, taskExecution));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/getExecutionLog")
    @ResponseBody
    public ResponseData getExecutionLog(@RequestBody TaskExecutionDetail dto, HttpServletRequest request) {
        return new ResponseData(Collections.singletonList(taskExecutionDetailService.getExecutionLog(dto)));
    }

    @Permission(type = ResourceType.SITE)
    @PostMapping(value = "/cancleExecute")
    @ResponseBody
    public ResponseData cancleExecute(@RequestBody TaskExecution dto, HttpServletRequest request) {
        boolean result = iTaskExecutionService.cancelExecute(dto);
        return new ResponseData(result);
    }

    @Permission(type = ResourceType.SITE)
    @GetMapping(value = "/detailDownload")
    @ResponseBody
    public void detailDownload(Long executionId, HttpServletRequest request, HttpServletResponse response) {
        IRequest iRequest = createRequestContext(request);
        TaskExecution dto = new TaskExecution();
        dto.setExecutionId(executionId);
        List<TaskExecution> list = iTaskExecutionService.queryExecutionDetail(iRequest, dto);
        if (list != null) {
            TaskExecution taskExecution = list.get(0);
            //执行详情文件名
            String fileName = taskExecution.getExecutionNumber() + ".log";
            if (taskExecution.getParentId() != null) {
                dto.setExecutionId(taskExecution.getParentId());
                List<TaskExecution> parentExecution = iTaskExecutionService.queryExecutionDetail(iRequest, dto);
                if (parentExecution != null) {
                    fileName = parentExecution.get(0).getExecutionNumber() + "-" + taskExecution.getExecutionOrder() + ".log";
                    taskExecution.setExecutionNumber(parentExecution.get(0).getExecutionNumber());
                    taskExecution.setExecutionDescription(parentExecution.get(0).getExecutionDescription());
                    taskExecution.setLastExecuteDate(parentExecution.get(0).getLastExecuteDate());
                }
            }
            try {
                String content = iTaskExecutionService.generateString(taskExecution);
                response.addHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, ENC) + "\"");
                response.setContentType("text/plain;charset=" + ENC);
                response.setHeader("Accept-Ranges", "bytes");
                writeFileToResp(response, content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Permission(type = ResourceType.SITE)
    @GetMapping(value = "/resultDownload")
    @ResponseBody
    public void resultDownload(Long executionId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TaskExecution execution = new TaskExecution();
        execution.setExecutionId(executionId);
        execution = iTaskExecutionService.selectByPrimaryKey(execution);
        File file = new File(execution.getExecuteResultPath());
        Properties props = System.getProperties();
        String fileSep = props.getProperty("file.separator");
        String fileName = execution.getExecuteResultPath().substring(execution.getExecuteResultPath().lastIndexOf(fileSep) + 1);
        if (file.exists()) {
            response.addHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileName, ENC) + "\"");
            response.setContentType("application/octet-stream;charset=" + ENC);
            response.setHeader("Accept-Ranges", "bytes");
            int fileLength = (int) file.length();
            response.setContentLength(fileLength);
            if (fileLength > 0) {
                writeFileToResp(response, file);
            }
        } else {
            throw new AttachmentException(UpConstants.ERROR_DOWNLOAD_FILE_ERROR, UpConstants.ERROR_DOWNLOAD_FILE_ERROR, new Object[0]);
        }
    }

    /**
     * 将文件对象的流写入Response对象.
     *
     * @param response HttpServletResponse
     * @param content  string/file
     */
    private void writeFileToResp(HttpServletResponse response, Object content) {
        byte[] buf = new byte[BUFFER_SIZE];
        InputStream inStream = null;
        ServletOutputStream outputStream = null;
        try {
            if (content.getClass() == String.class) {
                inStream = new ByteArrayInputStream(content.toString().getBytes());
            } else if (content.getClass() == File.class) {
                inStream = new FileInputStream((File) content);
            }
            outputStream = response.getOutputStream();
            int readLength;
            if (inStream != null && outputStream != null) {
                while (((readLength = inStream.read(buf)) != -1)) {
                    outputStream.write(buf, 0, readLength);
                }
                outputStream.flush();
            } else {
                logger.warn("InputStream or OutputStream is null");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }


}