/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.mob.compactions;

import java.io.IOException;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

/**
 * The mapper of a sweep job.
 * The key of the output is the value of a KeyValue which is actually a mob file name,
 * and the value is this KeyValue.
 */
@InterfaceAudience.Private
public class SweepMapper extends TableMapper<Text, KeyValue> {

  @Override
  public void map(ImmutableBytesWritable r, Result columns, Context context) throws IOException,
      InterruptedException {
    if (columns != null) {
      KeyValue[] kvList = columns.raw();
      if (kvList != null && kvList.length > 0) {
        for (KeyValue kv : kvList) {
          if (kv.getValueLength() > Bytes.SIZEOF_LONG) {
            String fileName = Bytes.toString(kv.getValueArray(), kv.getValueOffset()
                + Bytes.SIZEOF_LONG, kv.getValueLength() - Bytes.SIZEOF_LONG);
            context.write(new Text(fileName), kv);
          }
        }
      }
    }
  }
}
