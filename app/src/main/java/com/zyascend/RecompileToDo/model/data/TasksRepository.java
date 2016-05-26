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

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 *  数据操作接口实现类，主要对缓存与远程数据进行操作
 * Created by zyascend on 2016/5/17.
 */
public class TasksRepository implements TaskDataListener {

    private static final String TAG = "TAG_TasksRepository";
    private static TasksRepository INSTANCE = null;

    private final TaskDataListener mTaskDataListener;

    Map<String, Task> mCachedTasks;

    //缓存无效的标志
    boolean mCacheIsDirty = false;

    private TasksRepository (TaskDataListener listener){
        mTaskDataListener = checkNotNull(listener);
    }

    //间接返回类的引用，防止多次生成TaskDataListener对象(?)
    public static TasksRepository getInstance(TaskDataListener listener){
        if (INSTANCE == null){
            INSTANCE = new TasksRepository(listener);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    /**
     * 从缓存，本地数据库，或远程端获取Tasks
     * 如果获取数据失败，将会销毁callBack对象
     * @param callback
     */
    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        checkNotNull(callback);
        Log.d(TAG, "getTasks: ");
        //如果缓存可用，就从缓存中加载数据
        if (mCachedTasks != null && !mCacheIsDirty) {
            callback.onTaskLoaded(new ArrayList<>(mCachedTasks.values()));
        }

        if(mCacheIsDirty){
            Log.d(TAG, "getTasks: 缓存不可用 只是没处理");
            //缓存不可用，从远程加载数据
            //getTasksFromRemoteDataSource(callback);
            //TODO
            //如果本地数据可用，加载，否则使用远程+
            Log.d(TAG, "getTasks: 加载本地数据 ");
            mTaskDataListener.getTasks(new LoadTasksCallback() {
                @Override
                public void onTaskLoaded(List<Task> tasks) {
                    //如果加载完成，就会把task对象返回这里的List<Task>,然后存入缓存
                    Log.d(TAG, "onTaskLoaded: ");
                    refreshCache(tasks);
                    callback.onTaskLoaded(new ArrayList<Task>(mCachedTasks.values()));

                }
                @Override
                public void onDataNotAvailable() {
                    //从远程获取数据
//                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }

    /**
     * 重新为缓存存入数据，使之可用
     * @param tasks
     */
    private void refreshCache(List<Task> tasks) {
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
        for (Task task : tasks) {
            mCachedTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;

        Log.d(TAG, "refreshCache: 数据存入缓存");

    }

    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        checkNotNull(taskId);
        checkNotNull(callback);

        //尝试从缓存中获取task
        Task cachedTask = getTaskWithId(taskId);

        // 如果缓存可用，直接回调加载成功的方法
        if (cachedTask != null) {
            callback.onTaskLoaded(cachedTask);
            return;
        }

        //如果缓存不可用，从本地获取
        mTaskDataListener.getTask(taskId, new GetTaskCallback() {

            @Override
            public void onTaskLoaded(Task task) {
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                //如果从本地无法获取,就从远端获取
//                mTasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
//                    @Override
//                    public void onTaskLoaded(Task task) {
//                        callback.onTaskLoaded(task);
//                    }
//
//                    @Override
//                    public void onDataNotAvailable() {
//                        callback.onDataNotAvailable();
//                    }
//                });
            }
        });

    }

    private Task getTaskWithId(String taskId) {
        checkNotNull(taskId);

        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(taskId);
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);

        //为本地数据库添加数据
        mTaskDataListener.saveTask(task);
        //TODO  mRemoteDataListener.saveTasks(task);

        // 为缓存添加数据
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);

        //mTasksRemoteDataListener.completeTask(task);
        mTaskDataListener.completeTask(task);

        //更新之后重新获取Task对象,以便存入缓存
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);

        //将数据存入缓存
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        completeTask(getTaskWithId(taskId));
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        mTaskDataListener.activateTask(task);

        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());

        // 为缓存添加数据
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTaskDataListener.clearCompletedTasks();
        //清除缓存中的数据
        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
        Log.d(TAG, "refreshTasks: ");
    }

    //清除所有task数据，包括数据库，缓存
    @Override
    public void deleteAllTasks() {

//        mTasksRemoteDataSource.deleteAllTasks();
        mTaskDataListener.deleteAllTasks();

        if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    //清除单个Task对象，包括数据库，缓存
    @Override
    public void deleteTask(@NonNull String taskId) {
        mTaskDataListener.deleteTask(checkNotNull(taskId));
        mCachedTasks.remove(taskId);
    }

    /**
     * 从远端获取data的方法
     * 此处省略
     */
//    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
//        mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
//            @Override
//            public void onTasksLoaded(List<Task> tasks) {
//                refreshCache(tasks);
//                refreshLocalDataSource(tasks);
//                callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                callback.onDataNotAvailable();
//            }
//        });
//    }
//
//    private void refreshCache(List<Task> tasks) {
//        if (mCachedTasks == null) {
//            mCachedTasks = new LinkedHashMap<>();
//        }
//        mCachedTasks.clear();
//        for (Task task : tasks) {
//            mCachedTasks.put(task.getId(), task);
//        }
//        mCacheIsDirty = false;
//    }
//
//    private void refreshLocalDataSource(List<Task> tasks) {
//        mTasksLocalDataSource.deleteAllTasks();
//        for (Task task : tasks) {
//            mTasksLocalDataSource.saveTask(task);
//        }
//    }
}
