package com.health.pengfei.chestlearn2;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AccountRecord {


    public static final String fileName = "TB_account_information";
    public static final String fileDirectory = "/TB_TEST";

    public File createFile() {

        String state = Environment.getExternalStorageState();
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), fileDirectory);
        File file = new File(dir.getPath() + File.separator + fileName);

        try {
            if (!dir.exists()) {
                dir.mkdirs();
                String path = dir.getPath();
                System.out.println("path:" + path);
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static void saveAccounts(List<Account> accounts) throws IOException {

        // make account record string
        String data = "";
        for (Account account : accounts) {
            data += getAccountString(account) + "\n";
        }
        // save account records in file
        FileWriter fileWriter = new FileWriter(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                + "/" + fileDirectory + "/"+ fileName, false);
        fileWriter.write(data);
        fileWriter.close();
        System.out.println("account information saved");
    }

    // get all patients info string
    public static String getAllAccountString(List<Account> accounts){
        String data = "";
        for (Account account : accounts) {
            data += getAccountString(account) + "/n";
        }
        return data;
    }

    // get single patient info
    private static String getAccountString(Account account) {
        return account.getPatientName() + "\t" + account.isFirstPic() + "\t" + account.isSecondPic() + "\t" + account.isThirdPic();
    }

    // read patients info from file
    public static ArrayList readFile() throws IOException {
        ArrayList result = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
               + "/" + fileDirectory + "/"+ fileName));
        String line;
        // read file by line
        while ((line = reader.readLine()) != null) {
            result.add(getAccountFromString(line));
        }

        return result;
    }

    // get patient object from the string in file
    private static Account getAccountFromString(String line) {
        String[] parts = line.split("\t");

        return new Account(
                parts[0],
                Boolean.parseBoolean(parts[1]),
                Boolean.parseBoolean(parts[2]),
                Boolean.parseBoolean(parts[3])
        );
    }

    //update patients info
    public static ArrayList updateAccount(ArrayList<Account> results, Account patient){
        //update if patient exists
        if (results.contains(patient)){
            for (Account result:results) {
                if(result.getPatientName().equals(patient.getPatientName())) {
                    result.setFirstPic(patient.isFirstPic());
                    result.setSecondPic(patient.isSecondPic());
                    result.setThirdPic(patient.isThirdPic());
                    break;
                }
            }
        }
        //add new patient to list
        else {
            results.add(patient);
        }

        return results;
    }
}
