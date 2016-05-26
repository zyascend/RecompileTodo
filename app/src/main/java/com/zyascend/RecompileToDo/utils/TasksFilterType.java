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
package com.zyascend.RecompileToDo.utils;

/**
 * 存放请求类型常量的类
 * Created by zyascend on 2016/5/15.
 */
public enum TasksFilterType {
    /**
     * 所有tasks.
     */
    ALL_TASKS,

    /**
     * 未完成的task
     */
    ACTIVE_TASKS,

    /**
     * 已完成的task
     */
    COMPLETED_TASKS
}
