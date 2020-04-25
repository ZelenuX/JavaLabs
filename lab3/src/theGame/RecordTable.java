package theGame;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class RecordTable {
    private Properties results = new Properties();
    String fileName;
    public RecordTable(String fileName) throws IOException {
        this.fileName = fileName;
        FileInputStream inputStream = new FileInputStream(fileName);
        results.load(inputStream);
        inputStream.close();
    }
    public void set(String name, String time, String result){
        results.setProperty(name, time + ' ' + result);
    }
    public void updateFile() throws IOException {
        FileOutputStream output = new FileOutputStream(fileName);
        results.store(output, null);
        output.close();
    }
    public Vector<Vector<String>> getStrs(){
        Vector<Vector<String>> res = new Vector<Vector<String>>();
        Vector<String> tmp;
        String curPropertyName;
        String[] curResult;
        Enumeration<?> cur = results.propertyNames();
        while (cur.hasMoreElements()){
            tmp = new Vector<String>();
            curPropertyName = (String)cur.nextElement();
            tmp.add(curPropertyName);
            curResult = results.getProperty(curPropertyName).split(" ");
            tmp.add(curResult[0]);
            tmp.add(curResult[1]);
            res.add(tmp);
        }
        res.sort(new Comparator<Vector<String>>() {
            @Override
            public int compare(Vector<String> sv1, Vector<String> sv2) {
                double time1 = Double.valueOf(sv1.elementAt(1).split("s")[0]);
                double time2 = Double.valueOf(sv2.elementAt(1).split("s")[0]);
                double res1 = Double.valueOf(sv1.elementAt(2).split("%")[0]);
                double res2 = Double.valueOf(sv2.elementAt(2).split("%")[0]);
                if (res1 < res2)
                    return 1;
                if (res1 > res2)
                    return -1;
                if (time1 > time2)
                    return 1;
                if (time1 < time2)
                    return -1;
                return 0;
            }
        });
        return res;
    }
    public String[] getTimeAndResult(String name){
        String res = results.getProperty(name);
        if (res == null)
            return null;
        return res.split(" ");
    }
    public void leaveBest(int number){
        if (number <= 0){
            results.clear();
            return;
        }
        if (number >= results.size())
            return;
        Vector<Vector<String>> strs = getStrs();
        for (int i = number; i < strs.size(); ++i)
            results.remove(strs.elementAt(i).elementAt(0));
    }
}
