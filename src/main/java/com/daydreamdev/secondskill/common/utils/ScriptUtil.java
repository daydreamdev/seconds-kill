package com.daydreamdev.secondskill.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @auther G.Fukang
 * @date 6/7 20:42
 */
public class ScriptUtil {

    // 解析 lua
    public static String getScript(String path) {
        StringBuilder stringBuilder = new StringBuilder();

        InputStream inputStream = ScriptUtil.class.getClassLoader().getResourceAsStream(path);

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str).append(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }

        return stringBuilder.toString();
    }
}
