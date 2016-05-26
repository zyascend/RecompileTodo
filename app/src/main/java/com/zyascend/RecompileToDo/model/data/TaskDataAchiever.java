/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zyascend.RecompileToDo.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 数据操作接口实现类，主要对本地数据库进行操作
 * Created by zyascend on 2016/5/14.
 */
public class TaskDataAchiever implements TaskDataListener {

    private static final String TAG = "TAG_TaskDataAchiever";
    private static TaskDataAchiever INSTANCE;

    private TaskDbHelper mDbHelper;

    //防止直接实例化对象
    private TaskDataAchiever(@NonNull Context context){
        checkNotNull(context);
        mDbHelper = new TaskDbHelper(context);

    }
    //间接获得类的引用
    public static TaskDataAchiever getInstance(@NonNull Context context){
        if (INSTANCE == null) {
            INSTANCE = new TaskDataAchiever(context);
        }
        return INSTANCE;
    }

    //获取所有的task
    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {

        List<Task> tasks = new ArrayList<Task>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                TaskDbHelper.COLUMN_NAME_ENTRY_ID,
                TaskDbHelper.COLUMN_NAME_TITLE,
                TaskDbHelper.COLUMN_NAME_DESCRIPTION,
                TaskDbHelper.COLUMN_NAME_COMPLETED
        };

        Cursor c = db.query(
                TaskDbHelper.TABLE_NAME, projection, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String itemId = c.getString(c.getColumnIndexOrThrow(TaskDbHelper.COLUMN_NAME_ENTRY_ID));
                String title = c.getString(c.getColumnIndexOrThrow(TaskDbHelper.COLUMN_NAME_TITLE));
                String description =
                        c.getString(c.getColumnIndexOrThrow(TaskDbHelper.COLUMN_NAME_DESCRIPTION));
                boolean completed =
                        c.getInt(c.getColumnIndexOrThrow(TaskDbHelper.COLUMN_NAME_COMPLETED)) == 1;
                Task task = new Task(title, description, itemId, completed);
                tasks.add(task);
            }
        }
        if (c != null) {
            c.close();
        }

        db.close();

        if (tasks.isEmpty()) {
            // 当表格内容为空时被回调
            callback.onDataNotAvailable();
        } else {
            callback.onTaskLoaded(tasks);

            Log.d(TAG, "getTasks: callback.onTaskLoaded(tasks)");
        }
    }


    //根据taskID获取单个task
    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                TaskDbHelper.COLUMN_NAME_ENTRY_ID,
                TaskDbHelper.COLUMN_NAME_TITLE,
                TaskDbHelper.COLUMN_NAME_DESCRIPTION,
                TaskDbHelper.COLUMN_NAME_COMPLETED
        };

        String selection = TaskDbHelper.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        Cursor c = db.query(
                TaskDbHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        Task task = null;

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String itemId = c.getString(c.getColumnIndexOrThrow(TaskDbHelper.COLUMN_NAME_ENTRY_ID));
            String title = c.getString(c.getColumnIndexOrThrow(TaskDbHelper.COLUMN_NAME_TITLE));
            String description =
                    c.getString(c.getColumnIndexOrThrow(TaskDbHelper.COLUMN_NAME_DESCRIPTION));
            boolean completed =
                    c.getInt(c.getColumnIndexOrThrow(TaskDbHelper.COLUMN_NAME_COMPLETED)) == 1;
            task = new Task(title, description, itemId, completed);
        }

        if (c != null) {
            c.close();
        }

        db.close();

        if (task != null) {
            callback.onTaskLoaded(task);
        } else {
            callback.onDataNotAvailable();
        }


    }

    //保存task信息到本地
    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TaskDbHelper.COLUMN_NAME_ENTRY_ID, task.getId());
        values.put(TaskDbHelper.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskDbHelper.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskDbHelper.COLUMN_NAME_COMPLETED, task.isCompleted());

        db.insert(TaskDbHelper.TABLE_NAME, null, values);

        db.close();

        Log.d(TAG, "saveTask: Id = "+ task.getId());
    }

    //点击完成task（更新task的isComplete属性）
    @Override
    public void completeTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TaskDbHelper.COLUMN_NAME_COMPLETED,true);

        String selection = TaskDbHelper.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { task.getId() };

        db.update(TaskDbHelper.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        //对于本地数据库不需要此方法，TasksRepository会利用缓存数据将Id转化为task对象实现操作
    }

    //激活task（更新isActive属性）
    @Override
    public void activateTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskDbHelper.COLUMN_NAME_COMPLETED, false);

        String selection = TaskDbHelper.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { task.getId() };

        db.update(TaskDbHelper.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        //对于本地数据库不需要此方法，TasksRepository会利用缓存数据将Id转化为task对象实现操作

    }

    //清除已完成的Task
    @Override
    public void clearCompletedTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskDbHelper.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = { "1" };

        db.delete(TaskDbHelper.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void refreshTasks() {
        //对于本地数据库不需要此方法，TasksRepository会利用缓存数据将Id转化为task对象实现操作

    }

    //删除所有的task
    @Override
    public void deleteAllTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(TaskDbHelper.TABLE_NAME, null, null);

        db.close();
    }

    //删除单个task
    @Override
    public void deleteTask(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskDbHelper.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = { taskId };

        db.delete(TaskDbHelper.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
