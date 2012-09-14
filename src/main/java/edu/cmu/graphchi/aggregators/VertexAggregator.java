package edu.cmu.graphchi.aggregators;

/**
 * Copyright [2012] [Aapo Kyrola, Guy Blelloch, Carlos Guestrin / Carnegie Mellon University]
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


import edu.cmu.graphchi.ChiFilenames;
import edu.cmu.graphchi.datablocks.BytesToValueConverter;
import edu.cmu.graphchi.datablocks.IntConverter;

import java.io.*;


/**
 * Compute aggregates over the vertex values.
 */
public class VertexAggregator {


    public static <VertexDataType> void  foreach(String baseFilename, BytesToValueConverter<VertexDataType> conv,
                                                 ForeachCallback<VertexDataType> callback) throws IOException {

        File vertexDataFile = new File(ChiFilenames.getFilenameOfVertexData(baseFilename, conv));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(vertexDataFile), 1024 * 1024);

        int i = 0;
        byte[] tmp = new byte[conv.sizeOf()];
        System.out.println("read: " + vertexDataFile);
        try {
            while (true) {

                int rd = bis.read(tmp);

                if(rd != tmp.length) break;
                VertexDataType value = conv.getValue(tmp);
                callback.callback(i, value);
                i++;
            }
        } catch (EOFException e) {}
        bis.close();

    }

    private static class SumCallbackInt implements ForeachCallback<Integer> {
        long sum = 0;
        @Override
        public void callback(int vertexId, Integer vertexValue) {
            sum += vertexValue;
        }

        public long getSum() {
            return sum;
        }
    }

    public static long sumInt(String baseFilename) throws IOException {
        SumCallbackInt callback = new SumCallbackInt();
        foreach(baseFilename, new IntConverter(), callback);
        return callback.getSum();
    }

}
