/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.concurrent.lock.client;

import com.hazelcast.client.KeyBasedClientRequest;
import com.hazelcast.client.SecureRequest;
import com.hazelcast.concurrent.lock.IsLockedOperation;
import com.hazelcast.concurrent.lock.LockService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;

import java.io.IOException;

/**
 * @author mdogan 5/3/13
 */
public abstract class AbstractIsLockedRequest extends KeyBasedClientRequest
        implements Portable, SecureRequest {

    protected Data key;

    private long threadId;

    public AbstractIsLockedRequest() {
    }

    public AbstractIsLockedRequest(Data key) {
        this.key = key;
        this.threadId = -1;
    }

    protected AbstractIsLockedRequest(Data key, long threadId) {
        this.key = key;
        this.threadId = threadId;
    }

    @Override
    protected final Operation prepareOperation() {
        return new IsLockedOperation(getNamespace(), key, threadId);
    }

    @Override
    protected final Object getKey() {
        return key;
    }

    protected abstract ObjectNamespace getNamespace();

    @Override
    public final String getServiceName() {
        return LockService.SERVICE_NAME;
    }

    public void write(PortableWriter writer) throws IOException {
        writer.writeLong("tid", threadId);
        ObjectDataOutput out = writer.getRawDataOutput();
        key.writeData(out);
    }

    public void read(PortableReader reader) throws IOException {
        threadId = reader.readLong("tid");
        ObjectDataInput in = reader.getRawDataInput();
        key = new Data();
        key.readData(in);
    }
}
