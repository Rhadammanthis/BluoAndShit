package mx.triolabs.pp.util.database.models;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by hugomedina on 12/7/16.
 */

/**
 * Abstract class that provides basic implementation of methods to read and write data into the database
 * @param <Object> A class that provides a model to insert and retrieve data
 */
public abstract class BaseTableObject<Object> {

    /**
     * The application;s database
     */
    SQLiteDatabase database;

    /**
     * The name of the table to work with
     */
    String tableName;

    /**
     * Generic list to feed with items from teh table
     */
    List<Object> list;

    /**
     * Generic constructor to access database methods
     * @param database the application's database
     * @param tableName the name of the table
     */
    public BaseTableObject(SQLiteDatabase database, String tableName){
        this.database = database;
        this.tableName = tableName;
    }

    /**
     * Default empty constructor
     */
    public BaseTableObject(){}

    /**
     * Retrieves all rows from the table
     * @return List with all rows
     */
    abstract public List<Object> getAll();

    /**
     * Adds a new entry into the database
     * @param newObject The object to add to the database
     * @return the newly created object's id
     */
    abstract long insert(Object newObject);

    /**
     * Retrieves a single row from the table
     * @param id The id of the object to look for
     * @return The row converted into an object of the supplied class
     */
    abstract public Object selectOne(String id);

}
