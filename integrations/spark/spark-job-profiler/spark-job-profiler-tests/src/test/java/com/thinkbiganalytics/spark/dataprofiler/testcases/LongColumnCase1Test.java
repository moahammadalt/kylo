package com.thinkbiganalytics.spark.dataprofiler.testcases;

/*-
 * #%L
 * thinkbig-spark-job-profiler-app
 * %%
 * Copyright (C) 2017 ThinkBig Analytics
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.thinkbiganalytics.spark.dataprofiler.columns.LongColumnStatistics;
import com.thinkbiganalytics.spark.dataprofiler.columns.StandardColumnStatistics;
import com.thinkbiganalytics.spark.dataprofiler.core.ProfilerTest;
import com.thinkbiganalytics.spark.dataprofiler.topn.TopNDataItem;
import com.thinkbiganalytics.spark.dataprofiler.topn.TopNDataList;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;


/**
 * Long Column Statistics Test Case
 */
public class LongColumnCase1Test extends ProfilerTest {

    private static StandardColumnStatistics columnStats;
    private static long nullCount;
    private static long totalCount;
    private static long uniqueCount;
    private static double percNullValues;
    private static double percUniqueValues;
    private static double percDuplicateValues;
    private static TopNDataList topNValues;
    private static long max;
    private static long min;
    private static long sum;
    private static double mean;
    private static double stddev;
    private static double variance;

    @AfterClass
    public static void tearDownClass() {
        System.out.println("\t*** Completed run for LongColumnCase1Test ***");
    }

    @Before
    public void setUp() {
        super.setUp();

        columnStats = columnStatsMap.get(9);        //phash
        nullCount = 2L;
        totalCount = 10L;
        uniqueCount = 7L;
        percNullValues = 20.0d;
        percUniqueValues = 70.0d;
        percDuplicateValues = 30.0d;
        topNValues = columnStats.getTopNValues();
        max = 8782348100L;
        min = 1456890911L;
        sum = 43837413346L;
        mean = 5479676668.25d;
        stddev = 2930123653.19747d;
        variance = 8585624623027292200d;

    }

    @Test
    public void testLongNullCount() {
        Assert.assertEquals(nullCount, columnStats.getNullCount());
    }

    @Test
    public void testLongTotalCount() {
        Assert.assertEquals(totalCount, columnStats.getTotalCount());
    }

    @Test
    public void testLongUniqueCount() {
        Assert.assertEquals(uniqueCount, columnStats.getUniqueCount());
    }

    @Test
    public void testLongPercNullValues() {
        assertEquals(percNullValues, columnStats.getPercNullValues(), epsilon);
    }

    @Test
    public void testLongPercUniqueValues() {
        assertEquals(percUniqueValues, columnStats.getPercUniqueValues(), epsilon);
    }

    @Test
    public void testLongPercDuplicateValues() {
        assertEquals(percDuplicateValues, columnStats.getPercDuplicateValues(), epsilon);
    }

    @Test
    public void testLongTopNValues() {
        TreeSet<TopNDataItem> items = topNValues.getTopNDataItemsForColumn();
        Iterator<TopNDataItem> iterator = items.descendingIterator();

        //Verify that there are 3 items
        Assert.assertEquals(3, items.size());

        //Verify the top 3 item counts
        int index = 1;
        while (iterator.hasNext()) {
            TopNDataItem item = iterator.next();
            if (index == 1) {
                Assert.assertEquals(2988626110L, item.getValue());
                Assert.assertEquals(Long.valueOf(3L), item.getCount());
            } else if (index == 2) {
                Assert.assertEquals(null, item.getValue());
                Assert.assertEquals(Long.valueOf(2L), item.getCount());
            } else if (index == 3) {
                                /*
                    Not checking value since it can be arbitrary.
                    All remaining values have count 1
                */
                Assert.assertEquals(Long.valueOf(1L), item.getCount());
            }

            index++;
        }
    }

    @Test
    public void testLongMax() {
        Assert.assertEquals(max, ((LongColumnStatistics) columnStats).getMax());
    }

    @Test
    public void testLongMin() {
        Assert.assertEquals(min, ((LongColumnStatistics) columnStats).getMin());
    }

    @Test
    public void testLongSum() {
        Assert.assertEquals(sum, ((LongColumnStatistics) columnStats).getSum());
    }

    @Test
    public void testLongMean() {
        assertEquals(mean, ((LongColumnStatistics) columnStats).getMean(), epsilon);
    }

    @Test
    public void testLongStddev() {
        assertEquals(stddev, ((LongColumnStatistics) columnStats).getStddev(), epsilon);
    }

    @Test
    public void testLongVariance() {
        //Due to the results being extremely large numbers, epsilon2 is used.
        assertEquals(variance, ((LongColumnStatistics) columnStats).getVariance(), epsilon2);
    }
}
