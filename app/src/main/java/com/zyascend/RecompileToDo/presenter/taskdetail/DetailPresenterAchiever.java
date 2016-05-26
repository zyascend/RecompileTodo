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
package com.zyascend.RecompileToDo.presenter.taskdetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zyascend.RecompileToDo.model.data.Task;
import com.zyascend.RecompileToDo.model.data.TaskDataListener;
import com.zyascend.RecompileToDo.model.data.TasksRepository;
import com.zyascend.RecompileToDo.view.taskdetail.DetailViewListener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Created by zyascend on 2016/5/20.
 */
public class DetailPresenterAchiever implements DetailPresenterListener {

    private final TasksRepository mTasksRepository;

    private final DetailViewListener mViewListener;

    @Nullable
    private String mTaskId;

    public DetailPresenterAchiever(@Nullable String taskId,
                               @NonNull TasksRepository tasksRepository,
                               @NonNull DetailViewListener listener) {
        this.mTaskId = taskId;
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
        mViewListener = checkNotNull(listener, "taskslistener cannot be null!");
        mViewListener.setPresenter(this);

    }


    @Override
    public void start() {
        openTask();
    }

    private void openTask() {
        if (mTaskId == null || mTaskId.isEmpty()) {
            mViewListener.showMissingTask();
        }

        mViewListener.setLoadingIndicator(true);
        mTasksRepository.getTask(mTaskId, new TaskDataListener.GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                // 判断Fragment是否处于活动状态（是否被添加到活动中）
                if (!mViewListener.isActive()) {
                    return;
                }
                mViewListener.setLoadingIndicator(false);
                if (null == task) {
                    mViewListener.showMissingTask();
                } else {
                    showTask(task);
                }
            }

            @Override
            public void onDataNotAvailable() {
                if (!mViewListener.isActive()) {
                    return;
                }
                mViewListener.showMissingTask();
            }
        });
    }

    private void showTask(Task task) {
        String title = task.getTitle();
        String description = task.getDescription();

        //输入的title无内容或没有输入
        if (title != null && title.isEmpty()) {
            mViewListener.hideTitle();
        } else {
            mViewListener.showTitle(title);
        }

        //输入的description无内容或没有输入
        if (description != null && description.isEmpty()) {
            mViewListener.hideDescription();
        } else {
            mViewListener.showDescription(description);
        }

        mViewListener.showCompletionStatus(task.isCompleted());
    }

    @Override
    public void editTask() {
        if (null == mTaskId || mTaskId.isEmpty()) {
            mViewListener.showMissingTask();
        }else{
            mViewListener.showEditTask(mTaskId);
        }
    }

    @Override
    public void deleteTask() {
        mTasksRepository.deleteTask(mTaskId);
        mViewListener.showTaskDeleted();
    }

    @Override
    public void completeTask() {
        if (null == mTaskId || mTaskId.isEmpty()) {
            mViewListener.showMissingTask();
            return;
        }
        mTasksRepository.completeTask(mTaskId);
        mViewListener.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {
        if (null == mTaskId || mTaskId.isEmpty()) {
            mViewListener.showMissingTask();
            return;
        }
        mTasksRepository.activateTask(mTaskId);
        mViewListener.showTaskMarkedActive();
    }

}
