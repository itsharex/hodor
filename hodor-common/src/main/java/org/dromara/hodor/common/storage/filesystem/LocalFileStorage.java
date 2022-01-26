/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.hodor.common.storage.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * LocalFileStorage
 *
 * @author tomgs
 * @since 2022/1/26
 */
public class LocalFileStorage implements FileStorage {

    @Override
    public InputStream fetchFile(Path path) throws IOException {
        return null;
    }

    @Override
    public void pushFile(StorageMetadata metadata, File localFile) throws IOException {

    }

    @Override
    public boolean deleteFile(Path path) {
        return false;
    }

}
