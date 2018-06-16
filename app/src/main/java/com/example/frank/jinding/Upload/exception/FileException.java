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
package com.example.frank.jinding.Upload.exception;

/**
 * Created by lyy on 2017/1/18.
 * Aria 文件异常
 */
public class FileException extends NullPointerException {
  private static final String ARIA_FILE_EXCEPTION = "Aria Exception:";

  public FileException(String detailMessage) {
    super(ARIA_FILE_EXCEPTION + detailMessage);
  }
}