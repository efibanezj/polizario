package com.ij.polizario.Util;

import com.ij.polizario.persistence.entities.FileType2Entity;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;

class ReadAFileLineByLine {

    @Value("${fileType1.files.path}")
    private Resource[] fileType1FilesPath;

    public static void main(String args[]) throws IOException {

        List<FileType2Entity> fileType2EntityList = new ArrayList<>();


        File dir = new File("C:/Develop/Projects/Polizario/polizarioFileLoader/src/main/resources/data/");
        FileFilter fileFilter = new WildcardFileFilter("POLIZARI*.*");
        File[] files = dir.listFiles(fileFilter);
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);


            try {
                FileInputStream fstream = new FileInputStream(files[i]);
//            FileInputStream fstream = new FileInputStream("C:\\Develop\\Projects\\Polizario\\polizarioFileLoader\\src\\main\\resources\\data\\POLIZARI.UG.F210719.LEY1116.TXT");
                BufferedReader br = new BufferedReader(new InputStreamReader(fstream));


                String strLine;
                //Loop through and check if a header or footer line, if not
                //equate a substring to a temp variable and print it....
                while ((strLine = br.readLine()) != null) {
//      if (!(strLine.charAt(1) == "h" || strLine.charAt(1) == "f"))
                    if (strLine.contains("FECHA CONTABLE :")) {

                        String fecha = strLine.substring(17, 27);// 2021-07-19
                        System.out.println(fecha);
                        //save date
                        DODN:
                        while ((strLine = br.readLine()) != null) {
                            if (strLine.contains("SEQ. CUENTA ")) {
                                while ((strLine = br.readLine()) != null) {

                                    if (strLine.contains("TOTAL")) {
                                        break DODN;
                                    }
                                    if (!strLine.contains("-----")) {
                                        System.out.println(strLine);
                                        fileType2EntityList.add(new FileType2Entity(fecha, strLine));
                                    }
                                }

                            }
                        }
                    }
                }
                //Close the input stream
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}