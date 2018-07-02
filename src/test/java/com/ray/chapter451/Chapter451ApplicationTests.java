package com.ray.chapter451;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Chapter451ApplicationTests {

    // 获取JavaMailSender bean
    @Autowired
    private JavaMailSender javaMailSender;

    // 获取配置文件的username
    @Value("${spring.mail.username}")
    private String username;

    /**
     * 一个简单的邮件发送
     */
    @Test
    public void sendSimpleMail1(){
        SimpleMailMessage message = new SimpleMailMessage();
           // 设定邮件参数
           message.setFrom(username); //发送者
           message.setTo("target@qq.com"); //接受者
           message.setSubject("主题:邮件"); //主题
           message.setText("邮件内容"); //邮件内容

           // 发送邮件
           javaMailSender.send(message);
    }

    /**
     * 发送html格式Email
     */
    @Test
    public void sendSimpleMail2() throws MessagingException, UnsupportedEncodingException {
        //使用JavaMail的MimeMessage，支付更加复杂的邮件格式和内容
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        //创建MimeMessageHelper对象，处理MimeMessage的辅助类
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //使用辅助类MimeMessage设定参数
        helper.setFrom(new InternetAddress(username, "Ray", "UTF-8"));
        helper.setTo("target@qq.com");
        helper.setSubject("主题:HTML格式");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer
                .append("<html><body>")
                .append("<h1>Hello! Ray</h1>")
                .append("</body></html>");
        //邮件内容
        helper.setText(stringBuffer.toString(), true);

        // 发送邮件
        javaMailSender.send(mimeMessage);
    }

    /**
     * 发送html格式并带附件
     */
    @Test
    public void sendSimpleMail3() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(username);
        helper.setTo("target@qq.com");
        helper.setSubject("主题：嵌入静态资源");
        // 注意<img/>标签，src='cid:jpg'，'cid'是contentId的缩写，'jpg'是一个标记
        helper.setText("<html><body><img src=\"cid:jpg\"></body></html>", true);
        // 加载文件资源，作为附件
        FileSystemResource file = new FileSystemResource(new File("F:/test/2.jpg"));
        // 调用MimeMessageHelper的addInline方法替代成文件('jpg[标记]', file[文件])
        helper.addInline("jpg", file);

        // 发送邮件
        javaMailSender.send(mimeMessage);
    }

    @Test
    public void contextLoads() {
    }

}
