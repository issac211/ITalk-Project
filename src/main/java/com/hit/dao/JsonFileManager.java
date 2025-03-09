package com.hit.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;

class JsonFileManager<ID extends java.io.Serializable, T> {
    private final String pathFile;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type typeToken;

    public JsonFileManager(String pathFile, Class<ID> idClass, Class<T> typeClass) {
        this.pathFile = pathFile;
        this.typeToken = TypeToken.getParameterized(HashMap.class, idClass, typeClass).getType();
    }

    public void setFileData(HashMap<ID, T> data) throws IOException {
        try (Writer writer = new FileWriter(pathFile)) {
            gson.toJson(data, writer);
        }
    }

    public HashMap<ID, T> getFileData() throws IOException {
        boolean emptyData = checkData(pathFile);
        File file = new File(pathFile);

        if (emptyData) {
            setFileData(new HashMap<>());
        }

        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, typeToken);
        }
    }

    private boolean checkData(String pathFile) throws IOException {
        File file = new File(pathFile);
        return file.createNewFile() || file.length() == 0;
    }
}
