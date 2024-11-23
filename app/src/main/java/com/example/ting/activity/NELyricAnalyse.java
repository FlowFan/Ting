//
// Decompiled by Jadx - 777ms
//
package com.example.ting.activity;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NELyricLine {
    public int startTime;
    public int interval;
    public java.lang.String text;
    public java.util.List<NELyricWord> words;

    public NELyricLine() { /* compiled code */ }
}

class NELyricWord {
    public java.lang.String wordText;
    public long startTime;
    public long interval;
    public long offset;

    public NELyricWord() { /* compiled code */ }
}

enum NELyricType {
    NELyricTypeLrc("lrc"),
    NELyricTypeYrc("yrc"),
    NELyricTypeQrc("qrc"),
    NELyricTypeKas("kas");

    private final String type;

    NELyricType(String str) {
        this.type = str;
    }

    public String getType() {
        return this.type;
    }
}

public class NELyricAnalyse {
    private static final String LINE_FEED_REGEX = "\\r?\\n";
    private static final String LINE_REGEX = "\\[(-?[\\d,，]+)\\](.*)";

    public static List<NELyricLine> analyseWithLyric(String str, NELyricType nELyricType) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (nELyricType == NELyricType.NELyricTypeQrc) {
            return analyseQrcLyric(str);
        }
        if (nELyricType == NELyricType.NELyricTypeLrc) {
            return analyseLrcLyric(str);
        }
        if (nELyricType == NELyricType.NELyricTypeYrc) {
            return analyseYrcLyric(str);
        }
        return null;
    }

    public static List<NELyricLine> analyseYrcLyric(String str) {
        int i;
        int i2;
        ArrayList arrayList = new ArrayList();
        Pattern compile = Pattern.compile(LINE_REGEX, 34);
        Pattern compile2 = Pattern.compile("\\(([\\d,，]+)\\)(.*)", 34);
        String[] split = str.split(LINE_FEED_REGEX);
        int length = split.length;
        char c = 0;
        int i3 = 0;
        while (i3 < length) {
            Matcher matcher = compile.matcher(split[i3]);
            if (matcher.find()) {
                NELyricLine nELyricLine = new NELyricLine();
                int i4 = 1;
                String group = matcher.group(1);
                String group2 = matcher.group(2);
//                if (!TextUtils.isEmpty(group)) {
                String[] split2 = group.split(",");
                if (split2.length >= 1 && split2[c].matches("\\d+")) {
                    nELyricLine.startTime = Integer.parseInt(split2[c]);
                }
                if (split2.length >= 2 && split2[1].matches("\\d+")) {
                    nELyricLine.interval = Integer.parseInt(split2[1]);
                }
//                }
                nELyricLine.words = new ArrayList();
                for (String str2 : splitYrcWords(group2)) {
                    Matcher matcher2 = compile2.matcher(str2);
                    NELyricWord nELyricWord = new NELyricWord();
                    if (matcher2.find()) {
                        String[] split3 = matcher2.group(i4).split("[,，]");
                        String group3 = matcher2.group(2);
                        if (split3.length < i4 || !split3[c].matches("\\d+")) {
                            i2 = i3;
                        } else {
                            i2 = i3;
                            nELyricWord.startTime = Integer.parseInt(split3[c]);
                        }
                        if (split3.length >= 2 && split3[i4].matches("\\d+")) {
                            nELyricWord.interval = Integer.parseInt(split3[i4]);
                        }
                        nELyricWord.offset = nELyricWord.startTime - nELyricLine.startTime;
                        nELyricWord.wordText = group3;
                        nELyricLine.words.add(nELyricWord);
                    } else {
                        i2 = i3;
                        nELyricWord.wordText = str2;
                    }
                    i3 = i2;
                    c = 0;
                    i4 = 1;
                }
                i = i3;
                StringBuilder sb = new StringBuilder();
                Iterator it = nELyricLine.words.iterator();
                while (it.hasNext()) {
                    sb.append(((NELyricWord) it.next()).wordText);
                }
                nELyricLine.text = sb.toString();
                arrayList.add(nELyricLine);
            } else {
                i = i3;
            }
            i3 = i + 1;
            c = 0;
        }
        return arrayList;
    }

    private static List<NELyricLine> analyseLrcLyric(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        Pattern compile = Pattern.compile("((\\[\\d{1,2}:\\d{1,2}\\.\\d{1,3}])+)(.*)", 34);
        for (String str2 : str.split(LINE_FEED_REGEX)) {
            Matcher matcher = compile.matcher(str2);
            if (matcher.matches()) {
                String group = matcher.group(1);
                String group2 = matcher.group(3);
                Matcher matcher2 = Pattern.compile("\\[(\\d{1,2}):(\\d{1,2})\\.(\\d{1,3})]").matcher(group);
                while (matcher2.find()) {
                    String group3 = matcher2.group(1);
                    String group4 = matcher2.group(2);
                    String group5 = matcher2.group(3);
                    NELyricLine nELyricLine = new NELyricLine();
                    if (group2 != null && group2.length() != 0) {
                        nELyricLine.startTime = (Integer.parseInt(group3) * 60 * 1000) + (Integer.parseInt(group4) * 1000) + Integer.parseInt(group5);
                        nELyricLine.text = group2;
                        arrayList.add(nELyricLine);
                    }
                }
            }
        }
        return arrayList;
    }

    private static List<NELyricLine> analyseQrcLyric(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        Pattern compile = Pattern.compile(LINE_REGEX, 34);
        for (String str2 : str.split(LINE_FEED_REGEX)) {
            Matcher matcher = compile.matcher(str2);
            if (matcher.find()) {
                NELyricLine nELyricLine = new NELyricLine();
                int i = 1;
                String group = matcher.group(1);
                String group2 = matcher.group(2);
                if (!TextUtils.isEmpty(group)) {
                    String[] split = group.split(",");
                    if (split.length >= 1 && split[0].matches("\\d+")) {
                        nELyricLine.startTime = Integer.parseInt(split[0]);
                    }
                    if (split.length >= 2 && split[1].matches("\\d+")) {
                        nELyricLine.interval = Integer.parseInt(split[1]);
                    }
                }
                nELyricLine.words = new ArrayList();
                for (String str3 : splitWords(group2)) {
                    NELyricWord nELyricWord = new NELyricWord();
                    nELyricWord.wordText = str3.split("\\(")[0];
                    nELyricLine.words.add(nELyricWord);
                    Matcher matcher2 = Pattern.compile("\\(([^\\)]+)\\)").matcher(str3);
                    if (matcher2.find()) {
                        String[] split2 = matcher2.group(i).split(",");
                        if (split2.length >= i && split2[0].matches("\\d+")) {
                            nELyricWord.startTime = Integer.parseInt(split2[0]);
                        }
                        if (split2.length >= 2 && split2[i].matches("\\d+")) {
                            nELyricWord.interval = Integer.parseInt(split2[i]);
                        }
                        nELyricWord.offset = nELyricWord.startTime - nELyricLine.startTime;
                    }
                    i = 1;
                }
                StringBuilder sb = new StringBuilder();
                Iterator it = nELyricLine.words.iterator();
                while (it.hasNext()) {
                    sb.append(((NELyricWord) it.next()).wordText);
                }
                nELyricLine.text = sb.toString();
                arrayList.add(nELyricLine);
            }
        }
        return arrayList;
    }

    private static List<String> splitWords(String str) {
        if (TextUtils.isEmpty(str)) {
            return Collections.emptyList();
        }
        LinkedList linkedList = new LinkedList();
        String trim = str.trim();
        int i = 0;
        for (Integer num : searchAllIndex(")", trim)) {
            linkedList.add(trim.substring(i, num.intValue() + 1));
            i = num.intValue() + 1;
        }
        return linkedList;
    }

    private static List<Integer> searchAllIndex(String str, String str2) {
        ArrayList arrayList = new ArrayList();
        int indexOf = str2.indexOf(str);
        while (indexOf != -1) {
            arrayList.add(Integer.valueOf(indexOf));
            indexOf = str2.indexOf(str, indexOf + 1);
        }
        return arrayList;
    }

    public static String parseQrcLyricInputStream(InputStream inputStream) throws IOException {
        Pattern compile = Pattern.compile("(?<=LyricContent=\")[\\s\\S]*(?=\"/>)", 34);
        byte[] bArr = new byte[inputStream.available()];
        inputStream.read(bArr);
        Matcher matcher = compile.matcher(new String(bArr));
        return matcher.find() ? matcher.group(0) : "";
    }

    public static String parseLrcLyricInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read != -1) {
                byteArrayOutputStream.write(bArr, 0, read);
            } else {
                return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
            }
        }
    }

    public static List<String> splitYrcWords(String str) {
//        if (TextUtils.isEmpty(str)) {
//            return Collections.emptyList();
//        }
        LinkedList linkedList = new LinkedList();
        String trim = str.trim();
        int i = 0;
        boolean z = false;
        int i2 = 0;
        for (int i3 = 0; i3 < trim.length(); i3++) {
            char charAt = trim.charAt(i3);
            if (charAt == '(') {
                z = true;
                i2 = i3;
            } else {
                if (z && charAt == ')') {
                    if (i2 > i) {
                        linkedList.add(trim.substring(i, i2));
                        i = i2;
                    }
                } else if (charAt != ',') {
                    System.out.println("charAt = " + charAt);
                    if (charAt != '，') {
                        System.out.println("charAt2 = " + charAt);
                        if (charAt != ' ') {
                            System.out.println("charAt3 = " + charAt);
                            if (charAt >= '0' && charAt <= '9') {
                                System.out.println("charAt4 = " + charAt);
                            }
                        }
                    }
                }
                z = false;
            }
        }
        linkedList.add(trim.substring(i));
        return linkedList;
    }
}