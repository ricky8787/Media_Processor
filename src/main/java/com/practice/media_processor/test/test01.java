package com.practice.media_processor.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test01 {

    /*
     * 鴻揚科技面試專用，請勿外洩予他人
     * 題目0:
     * 將一個字串作以下處理
     * 1. 將",""."全部移除
     * 2. 切割為WORD後,將句子倒置
     * 3. 列印出不重複字元及其出現次數
     */

    public static void execute(String stInp) {
        if (stInp == null)
            return;
        String [] word = stInp.replace(",", " ").replace(".", " ").split(" ");
        ArrayList<String> arrWord = new ArrayList<>();
        HashMap<String, Integer> hm = new HashMap();

        System.out.println("Reversed String=");
        for( int i = word.length -1; i>=0 ; i--){
            System.out.print(word[i] + " ");
            if(word[i] != null && word[i].length() > 0){
                if(!hm.containsKey(word[i])){
                    hm.put(word[i],1);
                }
                else{
                    hm.put(word[i],hm.get(word[i])+1);
                }
            }

        }

        System.out.println();
        System.out.println("All Words=");

        for (Map.Entry<String, Integer> entry: hm.entrySet()){
            System.out.println("Word:"+ entry.getKey() + ", 次數:"+ entry.getValue() );
        }

//         String[] word = stInp.replace(",", " ").replace(".", " ").split(" ");
//         ArrayList<String> arrWord = new ArrayList<String>();
//         HashMap hm = new HashMap();
//
//         for (String st : word) {
//             if (st != null && st.length() > 0) {
//                 arrWord.add(0, st);
//                 if (!hm.containsKey(st)) {
//                     hm.put(st, st);
//                 }
//             }
//         }
//
//         System.out.println("Reversed String=");
//         for (String st : arrWord) {
//             System.out.print(st + " ");
//         }
//         System.out.println();
//         System.out.println();
//         System.out.println("All Words=");
//         for (Object key : hm.keySet()) {
//             System.out.print(key + " ");
//         }
    }

    /* You can test your program here */
    public static void main(String[] args) {
        execute("I, Jimmy, saw a saw saw a saw");
    }

}
