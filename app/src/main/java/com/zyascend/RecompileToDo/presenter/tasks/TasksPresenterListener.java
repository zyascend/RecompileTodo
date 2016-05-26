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

import android.support.annotation.NonNull;

import com.zyascend.RecompileToDo.model.data.Task;
import com.zyascend.RecompileToDo.utils.TasksFilterType;

/**
 * Created by zyascend on 2016/5/15.
 */
public interface TasksPresenterListener {

    void start();

    void result(int requestCode, int resultCode);

    void loadTasks(boolean forceUpdate);

    void addNewTask();

    void openTaskDetails(@NonNull Task requestedTask);

    void completeTask(@NonNull Task completedTask);

    void activateTask(@NonNull Task activeTask);

    void clearCompletedTasks();

    void setFiltering(TasksFilterType requestType);

    TasksFilterType getFiltering();
}
