package com.ruoyi.gateway.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import com.google.code.kaptcha.Producer;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.exception.CaptchaException;
import com.ruoyi.common.core.utils.IdUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.sign.Base64;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.gateway.config.properties.CaptchaProperties;
import com.ruoyi.gateway.service.ValidateCodeService;

/**
 * 验证码实现处理
 *
 * @author ruoyi
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService {
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CaptchaProperties captchaProperties;

    /**
     * 生成验证码
     */
    @Override
    public AjaxResult createCapcha() throws IOException, CaptchaException {
        AjaxResult ajax = AjaxResult.success();  //统一响应体
        System.out.println("captchaProperties初始值----" + "captchaProperties.getType()---" + captchaProperties.getType() + "---captchaProperties.getEnabled()---" + captchaProperties.getEnabled());
        //打印结果：captchaProperties初始值----captchaProperties.getType()---math---captchaProperties.getEnabled()---true
        boolean captchaOnOff = captchaProperties.getEnabled();  //boolean的默认值为false
        ajax.put("captchaOnOff", captchaOnOff); //这就是前端需要的验证码开关，返回默认值 false,那么前端就关闭验证码请求
        if (!captchaOnOff) {   //我至今不知道这段代码什么意思，我读的是  永远执行 ，因为没初始化，默认值为false
            return ajax;  //如果有童鞋找到了，请评论区指点迷津
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID(); //生成唯一UUID
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid; //作为redis存储的键

        String capStr = null, code = null;  //验证码字符串，验证码
        BufferedImage image = null;

        String captchaType = captchaProperties.getType();  //验证码类型（math 数组计算 char 字符）一样，没找到在哪赋值的
        // 生成验证码
        if ("math".equals(captchaType)) {  //math类型
            String capText = captchaProducerMath.createText();
            System.out.println("captchaProducerMath.createText()初始值---"+capText);
            //打印结果：captchaProducerMath.createText()初始值---8-5=?@3
            capStr = capText.substring(0, capText.lastIndexOf("@")); //  lastIndexOf  返回指定字符在此字符串中最后一次出现处的索引
            // captchaProducerMath.createText()是创建一个数学字符串  比如 8-5=?@3
            // 字符串根据 @ 进行分割
            //  要显示的图片为 @ 前面的字符串生成的图片
            //  验证码应该为 @ 后面的字符串生成的字符
            //  妙啊秒啊
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
            //通过这个 数学验证码生产者 ，根据 capStr ，就是 8-5=?  给BufferedImage对象赋值
        } else if ("char".equals(captchaType)) {  //char类型
            capStr = code = captchaProducer.createText();
            /**
             *captchaProducer.createImage(capStr)
             * 生成图像，图像的样子是 capStr 的内容
             * 是字符 转 图像
             */
            image = captchaProducer.createImage(capStr);
        }
/**
 * 最终capStr就是根据验证码生成的编码
 */
        redisService.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);  //redis存储验证码，键为  captcha_codes+UUID 值为 验证码的值 ，Constants.CAPTCHA_EXPIRATION 是验证码存储的世时间，单位是分钟
        // 转换流信息写出
        /**
         * 字节数组输出流 在内存中创建一个字节数组缓冲区，所有发送到输出流的数据保存在该字节数组缓冲区中。
         * FastByteArrayOutputStream内部实现由一个LinkedList<byte[]>组成，每一次扩容中分配一个数组的空间，
         * 并当该数据放入到List中。需要分配的数组长度为调用FastByteArrayOutputStream的write方法决定。
         * 而ByteArrayOutputStream内部实现为一个数组每一次扩容需要重新分配空间并将数据复制到新数组中，
         * 这就是FastByteArrayOutputStream比ByteArrayOutputStream主要区别。
         */
        // 快速字节数组输出流
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            //
            /**
             * 使用支持给定格式的任意 ImageWriter 将一个图像写入 File。如果已经有一个 File 存在，则丢弃其内容。
             * im - 要写入的 RenderedImage。
             * formatName - 包含格式非正式名称的 String。
             * output - 将在其中写入数据的 File。
             * 如果没有找到合适的 writer，则返回 false。
             */
            //显然是把captchaProducerMath创建的图像image 以jpg格式 写入 输出流
            //这样做的目的是 为了接下来把图像转成字符
            //是 图像 转 图像字符传
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            return AjaxResult.error(e.getMessage());
        }

        ajax.put("uuid", uuid);
           // 图像字符串 通过Base64编码传输
           // Base64编码是从二进制到字符的过程，可用于在HTTP环境下传递较长的标识信息。采用Base64编码具有不可读性，需要解码后才能阅读。
           // 为了加密
        ajax.put("img", Base64.encode(os.toByteArray()));
        return ajax;
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkCapcha(String code, String uuid) throws CaptchaException {
        if (StringUtils.isEmpty(code)) {
            throw new CaptchaException("验证码不能为空");
        }
        if (StringUtils.isEmpty(uuid)) {
            throw new CaptchaException("验证码已失效");
        }
        //上述内容涛涛表示有手就行

        //根据传来的UUID，拼接上 验证码存储在Redis中的键的前缀，得到这个用户 在 redis 中 验证码 对应的键
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        // 根据这个键，在Redis中获取对应的存储的值
        String captcha = redisService.getCacheObject(verifyKey);
        //Redis 删除这个记录
        redisService.deleteObject(verifyKey);
        //如果传来的验证码，和后端根据UUID拼接，在Redis中存储的验证码，在忽略大小写后，不一样
        //说明验证码错误，抛出自定义异常 验证码错误
        //否则不做处理，上个方法没有捕捉到内部方法有异常抛出，正常执行，即放行。
        if (!code.equalsIgnoreCase(captcha)) {
            throw new CaptchaException("验证码错误");
        }
    }

    public void setCaptchaProducerMath(Producer captchaProducerMath) {
        this.captchaProducerMath = captchaProducerMath;
    }
}
