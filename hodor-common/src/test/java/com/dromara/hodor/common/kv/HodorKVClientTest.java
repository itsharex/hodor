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

package com.dromara.hodor.common.kv;

import java.util.Arrays;
import org.dromara.hodor.common.raft.kv.core.HodorKVClient;

/**
 * HodorKVClientTest
 *
 * @author tomgs
 * @since 2022/4/6
 */
public class HodorKVClientTest {

    public static void main(String[] args) {
        HodorKVClient kvClient = new HodorKVClient("127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083");
        kvClient.defaultKvOperator()
            .put("a".getBytes(), "1".getBytes());

        byte[] bytes = kvClient.defaultKvOperator().get("a".getBytes());
        System.out.println(new String(bytes));

        kvClient.defaultKvOperator().delete("a".getBytes());

        bytes = kvClient.defaultKvOperator().get("a".getBytes());
        System.out.println(Arrays.toString(bytes));

        final Boolean aBoolean = kvClient.defaultKvOperator().containsKey("a".getBytes());
        System.out.println(aBoolean);

        final Boolean bBoolean = kvClient.defaultKvOperator().containsKey("b".getBytes());
        System.out.println(bBoolean);
    }

}
