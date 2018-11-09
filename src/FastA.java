

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class FastA {
    static StringBuffer s = new StringBuffer(); // s串
    static StringBuffer t = new StringBuffer(); // t串
    static List<String> sl = new ArrayList<String>(); // 按k分割的s串数组
    static List<String> tl = new ArrayList<String>(); // 按k分割的t串数组
    static ArrayList<Integer> disList = new ArrayList<Integer>();// 存放所有最大编辑距离的列表
    // 存放s片段对应的索引 （索引表）
    static HashMap<String, List<Integer>> indexTable = new HashMap<String, List<Integer>>();
    // 位移向量 位移->数量
    static HashMap<Integer, Integer> shiftVertex = new HashMap<Integer, Integer>();

    static int k;

    public static void main(String[] args) {
        // 子串读入
        InputStream fs = null;
        try {
            fs = new FileInputStream("/Users/summerchaser/Desktop/fs.txt");
        } catch (FileNotFoundException e) { 
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        InputStream ft = null;
        try {
            ft = new FileInputStream("/Users/summerchaser/Desktop/ft.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        int c;
        try {
            while ((c = fs.read()) != -1) {
                s.append((char) c);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            while ((c = ft.read()) != -1) {
                t.append((char) c);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("s串为： " + s);
        System.out.println("t串为： " + t);
        System.out.println(s.length() + " " + t.length());
        Scanner sc = new Scanner(System.in);
        System.out.println("input k :");
        k = sc.nextInt(); // 输入k
        SplitByK(s, sl);
        SplitByK(t, tl);
        createIndexTable();
        int dis = 0, cnt, maxDis = 0, maxCnt = 0;
        String part;
        for (int i = 0; i < tl.size(); i++) {
            part = tl.get(i); // 当前t串片段
            // System.out.println(part);
            if (indexTable.containsKey(part)) { // 如果索引表中有相同片段

                List<Integer> indexList = indexTable.get(part);// 获取片段索引列表 一一比对
                for (int s_index : indexList) { // i是t的index和s index一一比对得到位移量
                    dis = s_index - i;
                    // System.out.println("s中" + part + "dis:" + dis);
                    if (shiftVertex.containsKey(dis)) {// 位移向量包含这个位置
                        cnt = shiftVertex.get(dis);
                        cnt++;
                        shiftVertex.put(dis, cnt);// 更新该dis下的cnt
                    } else {
                        shiftVertex.put(dis, 1); // 否则加一

                    }
                }
            }
        }
        List<Integer> dl = new ArrayList<Integer>();
        System.out.println("位移向量");
        for (Entry<Integer, Integer> s : shiftVertex.entrySet()) {
            // System.out.println(s);
            dl.add(s.getValue());

        }

        maxCnt = Collections.max(dl);
        System.out.println("最大数量：" + maxCnt);

        Set set = shiftVertex.entrySet();

        Iterator it = set.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue().equals(maxCnt)) {
                dis = (int) entry.getKey();
                disList.add(dis);
                System.out.println("对应位移:" + dis);
            }
        }
        printSeq();
        
        

    }

    static void SplitByK(StringBuffer str, List<String> sl) {
        for (int i = 0; i < str.length() - k + 1; i++) {
            sl.add(str.substring(i, i + k));
        }
    }

    static void createIndexTable() {// 创建索引表
        String part;
        for (int i = 0; i < sl.size(); i++) {
            part = sl.get(i);
            // System.out.println(part);
            if (indexTable.containsKey(part)) {
                indexTable.get(part).add(i);
                // System.out.println(part + "索引" + i);
            } else {
                // 片段对应的索引列表
                List<Integer> indexList = new ArrayList<Integer>();
                // System.out.println(part + "索引" + i);
                indexList.add(i);
                indexTable.put(part, indexList);
            }
        }

    }

    static void printSeq() {// 打印所有最大编辑距离下的子序列

        for (int eachDis : disList) {
            System.out.println("序列串：");
            if (eachDis > 0) {
                for (int i = 0; i < t.length(); i++) {
                    if ((i + eachDis) >= s.length()) {
                        break;
                    }
                    if (t.charAt(i) == s.charAt(i + eachDis)) {
                        // System.out.println("=="+i+"=="+(i+eachDis));
                        System.out.print(t.charAt(i));
                    }
                }
            }else {
                for (int i = 0; i < s.length(); i++) {
                    if ((i - eachDis) >= t.length()) {
                        break;
                    }
                    if (s.charAt(i) == t.charAt(i - eachDis)) {
                        // System.out.println("=="+i+"=="+(i+eachDis));
                        System.out.print(s.charAt(i));
                    }
                }
            }

            System.out.println("");// 换行
        }
    }

}
