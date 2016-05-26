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
package com.zyascend.RecompileToDo.presenter.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zyascend.RecompileToDo.model.data.Task;
import com.zyascend.RecompileToDo.model.data.TaskDataListener;
import com.zyascend.RecompileToDo.model.data.TasksRepository;
import com.zyascend.RecompileToDo.utils.TasksFilterType;
import com.zyascend.RecompileToDo.view.addtasks.AddEditActivity;
import com.zyascend.RecompileToDo.view.tasks.TasksListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 *
 * Created by zyascend on 2016/5/15.
 */
public class TasksPresenterAchiever implements TasksPresenterListener {

    private static final String TAG = "TAG_TasksPresenter";
    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;
    /**
     * 连接View端
     */
    private final TasksListener mTasksListener;
    /**
     * 连接model端
     */
    private TasksRepository mTaskRepository;
    
    public TasksPresenterAchiever( @NonNull TasksRepository tasksRepository,@NonNull TasksListener tasksView) {
        mTaskRepository = checkNotNull(tasksRepository);
        mTasksListener = checkNotNull(tasksView, "tasksView cannot be null!");
        mTasksListener.setPresenter(this);
    }
    
    @Override
    public void start() {
        loadTasks(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {

        if (AddEditActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
        }
        Log.d(TAG, "result: showSuccessfullySavedMessage");
        mTasksListener.showSuccessfullySavedMessage();
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
       loadTasks(forceUpdate || mFirstLoad,true);
        mFirstLoad = false;

    }
    
    private void loadTasks(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI){
            mTasksListener.setLoadingIndicator(true);
        }
        
        if (forceUpdate){
            mTaskRepository.refreshTasks();
        }
        

        
        mTaskRepository.getTasks(new TaskDataListener.LoadTasksCallback() {
            @Override
            public void onTaskLoaded(List<Task> tasks) {
                List<Task> tasksToShow = new ArrayList<Task>();
                
                //根据请求类型加载task数据
                for(Task task : tasks){
                    switch (mCurrentFiltering){
                        case ALL_TASKS:
                            tasksToShow.add(task);
                            break;
                        case ACTIVE_TASKS:
                            if (task.isActive()){
                                tasksToShow.add(task);
                            }
                            break;
                        case COMPLETED_TASKS:
                            if (task.isCompleted()){
                                tasksToShow.add(task);
                            }
                            break;
                        default:
                            tasksToShow.add(task);
                            break;
                    }
                }

                if (!mTasksListener.isActive()) {
                    return;
                }
                if (showLoadingUI) {
                    mTasksListener.setLoadingIndicator(false);
                }
                Log.d(TAG, "onTaskLoaded: tasksToShow已加载");
                processTasks(tasksToShow);

            }

            @Override
            public void onDataNotAvailable() {
                Log.d(TAG, "onDataNotAvailable: ");
            }
        });
    }

    private void processTasks(List<Task> tasks) {

        if (tasks.isEmpty()) {
            // 抛出一个没有此类型数据的message
            processEmptyTasks();
        } else {
            // 显示任务列表
            mTasksListener.showTasks(tasks);
            // 设置这条数据的label
            showFilterLabel();
            Log.d(TAG, "processTasks: ");
        }
    }

    private void showFilterLabel() {

        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksListener.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTasksListener.showCompletedFilterLabel();
                break;
            default:
                mTasksListener.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksListener.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTasksListener.showNoCompletedTasks();
                break;
            default:
                mTasksListener.showNoTasks();
                break;
        }
    }

    @Override
    public void addNewTask() {
        mTasksListener.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestedTask cannot be null!");
        mTasksListener.showTaskDetailsUi(requestedTask.getId());
        Log.d(TAG, "openTaskDetails: id = "+requestedTask.getId());

    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask, "completedTask cannot be null!");
        mTaskRepository.completeTask(completedTask);
        mTasksListener.showTaskMarkedComplete();
        loadTasks(false, false);

    }

    @Override
    public void activateTask(@NonNull Task activeTask) {
        checkNotNull(activeTask, "activeTask cannot be null!");
        mTaskRepository.activateTask(activeTask);
        mTasksListener.showTaskMarkedActive();
        loadTasks(false, false);
    }

    @Override
    public void clearCompletedTasks() {
        mTaskRepository.clearCompletedTasks();
        mTasksListener.showCompletedTasksCleared();
        loadTasks(false, false);
    }

    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }
}
