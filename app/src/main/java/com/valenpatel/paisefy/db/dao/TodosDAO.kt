package com.valenpatel.paisefy.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.valenpatel.paisefy.db.entities.TodoEntity


@Dao
interface TodosDAO {
    @Query("Select * FROM todos")
    fun getAllTodos(): LiveData<List<TodoEntity>>

    @Query("SELECT * FROM todos WHERE id = :id")
    fun getTodosById(id: Int): LiveData<TodoEntity>

    @Insert
    fun insertTodo(todoEntity: TodoEntity)

    @Update
    fun updateTodo(todoEntity: TodoEntity)

    @Delete
    fun deleteTodos(todoEntity: TodoEntity)
}