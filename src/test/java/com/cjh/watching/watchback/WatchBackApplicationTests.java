package com.cjh.watching.watchback;

import cn.dev33.satoken.secure.SaBase64Util;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class WatchBackApplicationTests {

    public static void main(String[] args) throws IOException {
// 文本
        String text = "test123";

// 使用Base64编码
        String base64Text = SaBase64Util.encode(text);
        System.out.println("Base64编码后：" + base64Text);

// 使用Base64解码
        String text2 = SaBase64Util.decode(base64Text);
        System.out.println("Base64解码后：" + text2);

    }

}
