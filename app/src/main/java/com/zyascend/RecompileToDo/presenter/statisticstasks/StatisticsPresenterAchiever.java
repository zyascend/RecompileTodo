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
package com.zyascend.RecompileToDo.presenter.statisticstasks;


import android.support.annotation.NonNull;

import com.zyascend.RecompileToDo.model.data.Task;
import com.zyascend.RecompileToDo.model.data.TaskDataListener;
import com.zyascend.RecompileToDo.model.data.TasksRepository;
import com.zyascend.RecompileToDo.view.statisticstasks.StatisticsViewListener;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Created by zyascend on 2016/5/21.
 */
public class StatisticsPresenterAchiever implements StatisticsPresenterListener {


    private final TasksRepository mTasksRepository;

    private final StatisticsViewListener mViewListener;

    public StatisticsPresenterAchiever(@NonNull TasksRepository tasksRepository,
                               @NonNull StatisticsViewListener statisticsView) {
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null");
        mViewListener = checkNotNull(statisticsView, "StatisticsView cannot be null!");
        mViewListener.setPresenter(this);
    }


    @Override
    public void start() {
        loadStatistics();
    }

    private void loadStatistics() {
        mViewListener.setProgressIndicator(true);
        mTasksRepository.getTasks(new TaskDataListener.LoadTasksCallback() {
            @Override
            public void onTaskLoaded(List<Task> tasks) {
                int activeTasks = 0;
                int completedTasks = 0;

                for (Task task : tasks) {
                    if (task.isCompleted()) {
                        completedTasks += 1;
                    } else {
                        activeTasks += 1;
                    }
                }

                if (!mViewListener.isActive()) {
                    return;
                }
                mViewListener.setProgressIndicator(false);

                mViewListener.showStatistics(activeTasks, completedTasks);

            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mViewListener.isActive()) {
                    return;
                }
                mViewListener.showLoadingStatisticsError();
            }
        });




    }
}
