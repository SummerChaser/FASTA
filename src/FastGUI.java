

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FastGUI extends JFrame {

    private JPanel contentPane;
    private JTextField sdr;
    private JTextField kdr;
    private JTextField tdr;
    static JTextArea out;
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

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    FastGUI frame = new FastGUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
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

    public FastGUI() {
        setTitle("FASTA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 692, 486);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JLabel lblS = new JLabel("s串路径：");

        sdr = new JTextField();
        sdr.setText("/Users/summerchaser/Desktop/fs.txt");
        sdr.setColumns(10);

        JLabel lblK = new JLabel("k值：");

        kdr = new JTextField();
        kdr.setText("2");
        kdr.setColumns(10);

        JLabel lblNewLabel = new JLabel("运行结果 ：");

        JButton button = new JButton("运行");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                s = new StringBuffer();
                t = new StringBuffer();
                sl = new ArrayList<String>();
                tl = new ArrayList<String>();
                disList = new ArrayList<Integer>();
                indexTable = new HashMap<String, List<Integer>>();
                shiftVertex = new HashMap<Integer, Integer>();
                k = 0;

                out.setText("");
                // 子串读入
                InputStream fs = null;
                try {
                    fs = new FileInputStream(sdr.getText());
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                InputStream ft = null;
                try {
                    ft = new FileInputStream(tdr.getText());
                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                int c;
                try {
                    while ((c = fs.read()) != -1) {
                        s.append((char) c);
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    while ((c = ft.read()) != -1) {
                        t.append((char) c);
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                out.append("s串为： " + s + '\n');
                out.append("t串为： " + t + '\n');
                k = Integer.valueOf(kdr.getText());// 输入k
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
                out.append("位移向量：");
                for (Entry<Integer, Integer> s : shiftVertex.entrySet()) {
                    // System.out.println(s);
                    dl.add(s.getValue());

                }

                maxCnt = Collections.max(dl);
                out.append("最大数量：" + maxCnt + "\n");

                Set set = shiftVertex.entrySet();
                out.append("\n");
                Iterator it = set.iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    if (entry.getValue().equals(maxCnt)) {
                        dis = (int) entry.getKey();
                        disList.add(dis);
                        out.append("对应位移:" + dis + "\n");
                    }
                }
                out.append("\n");
                for (int eachDis : disList) {

                    out.append("序列串：\n");
                    if (eachDis > 0) {
                        for (int i = 0; i < t.length(); i++) {
                            if ((i + eachDis) >= s.length()) {
                                break;
                            }
                            if (t.charAt(i) == s.charAt(i + eachDis)) {
                                // System.out.println("=="+i+"=="+(i+eachDis));
                                out.append(String.valueOf(t.charAt(i)));
                            }
                        }
                    } else {
                        for (int i = 0; i < s.length(); i++) {
                            if ((i - eachDis) >= t.length()) {
                                break;
                            }
                            if (s.charAt(i) == t.charAt(i - eachDis)) {
                                // System.out.println("=="+i+"=="+(i+eachDis));
                                out.append(String.valueOf(s.charAt(i)));
                            }
                        }
                    }

                    out.append("\n");// 换行
                }

                try {
                    // 请在这修改文件输出路径
                    File fo = new File("/Users/summerchaser/Desktop/fastout.txt");
                    FileWriter fileWriter = new FileWriter(fo);
                    fileWriter.write(out.getText());
                    fileWriter.close(); // 关闭数据流
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        });

        JScrollPane scrollPane = new JScrollPane();

        JLabel lblT = new JLabel("t串路径：");

        tdr = new JTextField();
        tdr.setText("/Users/summerchaser/Desktop/ft.txt");
        tdr.setColumns(10);
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING).addGroup(gl_contentPane
                .createSequentialGroup()
                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup().addGap(53).addGroup(gl_contentPane
                                .createParallelGroup(Alignment.LEADING)
                                .addGroup(gl_contentPane.createSequentialGroup()
                                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                .addComponent(lblS).addComponent(lblK).addComponent(lblT,
                                                        GroupLayout.PREFERRED_SIZE, 59, GroupLayout.PREFERRED_SIZE))
                                        .addGap(22)
                                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                .addComponent(tdr, GroupLayout.PREFERRED_SIZE, 352,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addComponent(sdr, GroupLayout.PREFERRED_SIZE, 352,
                                                        GroupLayout.PREFERRED_SIZE)
                                                .addGroup(gl_contentPane.createSequentialGroup()
                                                        .addComponent(kdr, GroupLayout.PREFERRED_SIZE, 107,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addGap(24).addComponent(button))))
                                .addComponent(lblNewLabel)))
                        .addGroup(gl_contentPane.createSequentialGroup().addGap(32).addComponent(scrollPane,
                                GroupLayout.PREFERRED_SIZE, 621, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(29, Short.MAX_VALUE)));
        gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane
                .createSequentialGroup().addGap(25)
                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblS).addComponent(sdr,
                        GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(ComponentPlacement.RELATED)
                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                        .addComponent(tdr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblT))
                .addGap(7)
                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(button).addComponent(lblK)
                        .addComponent(kdr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
                                GroupLayout.PREFERRED_SIZE))
                .addGap(18).addComponent(lblNewLabel).addGap(18)
                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE)));

        out = new JTextArea();
        scrollPane.setRowHeaderView(out);
        contentPane.setLayout(gl_contentPane);
    }
}
