package service;

import java.sql.SQLException;
import java.util.List;


public interface BaseService<T, ID> {


    void add(T entity) throws SQLException;


    T findById(ID id) throws SQLException;


    List<T> findAll() throws SQLException;


    void update(T entity) throws SQLException;


    void remove(ID id) throws SQLException;
}

