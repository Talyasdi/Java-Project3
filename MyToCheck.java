package il.ac.tau.cs.sw1.ex8.wordsRank;

import il.ac.tau.cs.sw1.ex8.histogram.HashMapHistogram;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MyToCheck {
    public static void main (String[] args) throws IOException {
        File fromFile = new File("C:/Users/talya/MY_FILE.txt");
        List<String> st = FileUtils.readAllTokens(fromFile);

        System.out.println("reg ord:");
        for (String s : st){
            System.out.print(s +", ");
        }

        System.out.println("new ord:");
        HashMapHistogram hs = new HashMapHistogram();
        hs.addAll(st);
        Iterator it = hs.iterator();

        while (it.hasNext()){

            System.out.print(it.next().getClass().getName());

        }

    }
}
