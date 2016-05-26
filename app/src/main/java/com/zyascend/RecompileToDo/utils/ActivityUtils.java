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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * 为Activity绑定Fragment的工具类
 * Created by zyascend on 2016/5/20.
 */
public class ActivityUtils {

    public static void addFragmentToActivity(android.support.v4.app.FragmentManager manager, Fragment fragment, int frameId){
        checkNotNull(manager);
        checkNotNull(fragment);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(frameId,fragment);
        transaction.commit();
    }
}
