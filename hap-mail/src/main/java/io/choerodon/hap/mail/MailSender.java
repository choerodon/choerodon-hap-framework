package io.choerodon.hap.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

/**
 * 消息服务,继承了Spring mail sender.
 *
 * @author Clerifen Li
 */
@Component
@Scope(value = "prototype")
public class MailSender extends JavaMailSenderImpl {

    private Integer tryTimes = 3;

    private String environment;

    private List<String> whiteList;

    private String messageAccount;

    {
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.auth", "true");
        setJavaMailProperties(javaMailProperties);
    }

    public String getMessageAccount() {
        return messageAccount;
    }

    public void setMessageAccount(String messageAccount) {
        this.messageAccount = messageAccount;
    }

    public String getEnvironment() {
        return environment;
    }

    @Value("${env.code:SIT}")
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public Integer getTryTimes() {
        return tryTimes;
    }

    public void setTryTimes(Integer tryTimes) {
        this.tryTimes = tryTimes;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

}
