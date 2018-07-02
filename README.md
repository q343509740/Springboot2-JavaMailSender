# Springboot2-JavaMailSender

### 1、认识JavaMailSender
  对MailSender进行扩展，提供专业的JavaMail特性，如对MIME消息的支持。
  
### 2、添加依赖
```
        <!-- 引入spring-boot-starter-mail依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
```
如其他自动化配置模块一样，在完成了依赖引入之后，只需要在application.yml中配置相应的属性内容。  
下面我们以QQ邮箱为例，在application.yml中加入如下配置（注意替换自己的用户名和密码）：  
```
spring:
  mail:
    host: smtp.qq.com
    username: username@qq.com
    password: your-password
```

### 3、编写邮件内容
JavaMailSenderImpl支持MimeMessages和SimpleMailMessages。  
MimeMessages为复杂邮件模板，支持文本、附件、html、图片等。  
SimpleMailMessages实现了MimeMessageHelper，为普通邮件模板，支持文本。

#### 1)简单邮件
下面先以SimpleMailMessages为例进行介绍：  
```
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
}
```

如此，我们便完成了一个简单邮件的编写  

#### 2)复杂邮件(附件)
对于复杂邮件，编写及发送如下： 
```
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
```
这里需要注意的是addInline函数中资源名称jpg需要与正文中cid:jpg对应起来

#### 3)复杂邮件(HTML)
对于复杂邮件，编写及发送如下： 
```
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
```
