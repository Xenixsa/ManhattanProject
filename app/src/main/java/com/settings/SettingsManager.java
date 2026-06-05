package com.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsManager {

    private static final String File = "settings.properties";
    private Properties properties = new Properties();

    public SettingsManager(){
        load();
    }

    private void load(){
        try {
            File file = new File(File);
            if (!file.exists()){
                save();
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(File);
            properties.load(fileInputStream);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void save(){
        try {
            FileOutputStream outputStream = new FileOutputStream(File);
            properties.store(outputStream,"SimulationSettings");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getStringSetting(String key,String defaultStringValue){
        return properties.getProperty(key,defaultStringValue);
    }

    public int getIntSetting(String key,int defaultIntValue){
        return Integer.parseInt(properties.getProperty(key,String.valueOf(defaultIntValue)));
    }

    public void set(String key,int value){
        properties.setProperty(key,String.valueOf(value));
    }
    public void set(String key, String value){
        properties.setProperty(key, value);
    }

}