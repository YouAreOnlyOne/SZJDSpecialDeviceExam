/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
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
package com.example.frank.jinding.Upload.core.command;

//import com.arialyy.aria.core.inf.AbsTaskEntity;

import com.example.frank.jinding.Upload.core.inf.AbsTaskEntity;

/**
 * Created by AriaL on 2017/6/29.
 * 抽象命令工厂
 */
public abstract class AbsCmdFactory<TASK_ENTITY extends AbsTaskEntity, CMD extends AbsCmd> {

  /**
   * @param target 创建任务的对象
   * @param entity 下载实体
   * @param taskType {@link ICmd#TASK_TYPE_DOWNLOAD}、{@link ICmd#TASK_TYPE_DOWNLOAD_GROUP}、{@link
   * ICmd#TASK_TYPE_UPLOAD}
   */
  public abstract CMD createCmd(String target, TASK_ENTITY entity, int type, int taskType);
}
