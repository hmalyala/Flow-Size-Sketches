import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class BitMap {
    Map<String, int[]> virtual_bitmap;
    List<Integer> random_list;
    int physical_bitmap[];
    double v_b = 0;
    double estimated_spread[];
    int actual_spread[];

    public BitMap() {
        random_list = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            random_list.add(i);
        }

        virtual_bitmap = new HashMap<>();

        physical_bitmap = new int[500000];
        estimated_spread = new double[8507];
        actual_spread = new int[8507];
    }

    public static void main(String[] args) {

        BitMap bm = new BitMap();

        bm.readFile();
        bm.calculate_vb();
        bm.coordinates();
        try {
            bm.write_to_file();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bm.print();
    }

    public void readFile() {
        try {
            File myObj = new File("C:/Users/mhema/Desktop/Desktop/Network data streaming/project4input.txt");
            Scanner myReader = new Scanner(myObj);
            String data[] = myReader.nextLine().split(" ");
            Random rand = new Random();
            int index = 0, total = 0;
            while (myReader.hasNextLine()) {
                data = myReader.nextLine().split("\\s+");
                int hashes[] = new int[500];
                Collections.shuffle(random_list);
                int count = Integer.parseInt(data[1]);
                for (int i = 0; i < count; i++) {
                    int randomNumber = (rand.nextInt(5000));
                    hashes[(randomNumber ^ random_list.get(i)) % 500] = 1;
                    randomNumber = (rand.nextInt(50000));
                    physical_bitmap[randomNumber ^ random_list.get(i)] = 1;
                }
                virtual_bitmap.put(data[0], hashes);

                double numerator = 0;
                for (int i : hashes) {
                    if (i == 0)
                        numerator++;
                }
                estimated_spread[index] = numerator;
                actual_spread[index] = count;
                index += 1;
                total += count;
            }
            System.out.println(total);
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Invalid file name or file could not be opened");
            e.printStackTrace();
        }
    }

    public void calculate_vb() {
        double numerator = 0;

        for (int i : physical_bitmap) {
            if (i == 0)
                ++numerator;
        }
        v_b = (numerator / 500000);
        System.out.println(numerator);
    }

    public void coordinates() {
        for (int i = 0; i < estimated_spread.length; i++) {
            double v_f = estimated_spread[i] / 500;
            estimated_spread[i] = 500 * (Math.log(v_b) - Math.log(v_f));
        }
    }

    public void write_to_file() throws IOException {
        File file = new File("C:/Users/mhema/Desktop/Desktop/Network data streaming/project4output.txt");
        FileOutputStream outputStream = new FileOutputStream(file);
        for(int i = 0; i < 8507; i++){
            String str = actual_spread[i]+"     "+estimated_spread[i]+"\n";        
            byte[] strToBytes = str.getBytes();
            outputStream.write(strToBytes);
        }
        outputStream.close();
    }
    public void print(){
        
    }
}