package com.hit.dao;

import java.io.IOException;
import java.util.List;

public interface IDao<ID extends java.io.Serializable, T> {
    void delete(T entity) throws IOException;

    T find(ID id) throws IOException;

    void save(T entity) throws IOException;

    List<T> getAll() throws IOException;
}
