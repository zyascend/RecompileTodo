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
package com.zyascend.RecompileToDo.presenter.addtasks;

import android.util.Log;

import com.zyascend.RecompileToDo.model.data.Task;
import com.zyascend.RecompileToDo.model.data.TaskDataListener;
import com.zyascend.RecompileToDo.view.addtasks.AddEditViewListener;
import static com.google.common.base.Preconditions.checkNotNull;



/**
 *
 * Created by zyascend on 2016/5/20.
 */
public class AddEditPresenterAchiever implements AddEditPresenterListener,TaskDataListener.GetTaskCallback {

    private static final String TAG = "TAG_AddPresenter";
    private TaskDataListener mTaskDataListener;
    private AddEditViewListener mAddEditViewListener;
    private String mTaskId;

    public AddEditPresenterAchiever(String taskId,TaskDataListener taskDataListener,
                                    AddEditViewListener viewListener){
        mTaskDataListener = checkNotNull(taskDataListener);
        mAddEditViewListener = checkNotNull(viewListener);
        mAddEditViewListener.setPresenter(this);

        mTaskId = taskId;

    }

    @Override
    public void start() {
        if (mTaskId != null) {
            populateTask();
        }
    }


    @Override
    public void createTask(String title, String description) {
        Task newTask = new Task(title,description);
        if (newTask.isEmpty()){
            mAddEditViewListener.showEmptyTaskError();
        }else {
            mTaskDataListener.saveTask(newTask);
            mAddEditViewListener.showTasksList();
        }
        Log.d(TAG, "createTask: title = "+title+"description = "+description);

    }

    @Override
    public void updateTask(String title, String description) {
        if (mTaskId == null) {
            throw new RuntimeException("updateTask() was called but task is new.");
        }
        mTaskDataListener.saveTask(new Task(title, description, mTaskId));
        mAddEditViewListener.showTasksList(); //编辑之后回到列表
    }

    /***
     *  编辑任务
     */
    @Override
    public void populateTask() {
        if (mTaskId == null) {
            throw new RuntimeException("populateTask() was called but task is new.");
        }
        mTaskDataListener.getTask(mTaskId, this);
    }


    @Override
    public void onTaskLoaded(Task task) {
        //至此，UI更新完毕
        if (mAddEditViewListener.isActive()) {
            mAddEditViewListener.setTitle(task.getTitle());
            mAddEditViewListener.setDescription(task.getDescription());
        }
    }


    @Override
    public void onDataNotAvailable() {
        //至此，UI更新完毕
        if (mAddEditViewListener.isActive()) {
            mAddEditViewListener.showEmptyTaskError();
        }
    }
}
