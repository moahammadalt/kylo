package com.thinkbiganalytics.nifi.provenance.cache;

/*-
 * #%L
 * thinkbig-nifi-provenance-repo
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

import com.thinkbiganalytics.nifi.provenance.FeedFlowFileNotFoundException;
import com.thinkbiganalytics.nifi.provenance.KyloProcessorFlowType;
import com.thinkbiganalytics.nifi.provenance.StartingFeedFlowFileUtil;
import com.thinkbiganalytics.nifi.provenance.model.FeedFlowFile;
import com.thinkbiganalytics.nifi.provenance.model.ProvenanceEventRecordDTO;
import com.thinkbiganalytics.nifi.provenance.model.util.ProvenanceEventUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility to build the FlowFile graph from an incoming Provenance Event and cache the FlowFile Graph.
 */
public class FeedFlowFileCacheUtil {

    private static final Logger log = LoggerFactory.getLogger(FeedFlowFileCacheUtil.class);

    @Autowired
    FeedFlowFileGuavaCache flowFileGuavaCache;

    @Autowired
    StartingFeedFlowFileUtil startingFeedFlowFileUtil;


    AtomicLong skippedFeedFlowFiles = new AtomicLong(0);

    public FeedFlowFileCacheUtil() {

    }



    /**
     * Create the FlowFile graph and cache the FlowFile with event into the GuavaCache for processing
     */
    public void cacheAndBuildFlowFileGraph(ProvenanceEventRecordDTO event) {

        // Get the FlowFile from the Cache.
        FeedFlowFileGuavaCache flowFileCache = flowFileGuavaCache;

        //An event is the very first in the flow if it is a CREATE or RECEIVE event and if there are no Parent flow files
        //This indicates the start of a Job.
        if (ProvenanceEventUtil.isFirstEvent(event) && (event.getParentUuids() == null || (event.getParentUuids() != null && event.getParentUuids().isEmpty()))) {
            //we only need to store references to the root feed flow file.
            FeedFlowFile flowFile = null;
            if (flowFileCache.isCached(event.getFlowFileUuid())) {
                flowFile = flowFileCache.getEntry(event.getFlowFileUuid());
            } else {
            //    if(startingFeedFlowFileUtil.process(event)) {
                    flowFile = new FeedFlowFile(event.getFlowFileUuid());
                    flowFileCache.add(event.getFlowFileUuid(), flowFile);
                    flowFile.setFirstEvent(event);
                    event.setIsStartOfJob(true);
             //   }
             //   else {
                    //skip processing
           //         skippedFeedFlowFiles.incrementAndGet();
           //         return;
           //     }

            }
            event.setFeedFlowFile(flowFile);
        }

        FeedFlowFile feedFlowFile = null;

        if (flowFileCache.isCached(event.getFlowFileUuid())) {
            feedFlowFile = flowFileCache.getEntry(event.getFlowFileUuid());
            event.setFeedFlowFile(feedFlowFile);
        }
        FeedFlowFile parentFlowFile = null;
        //Build the graph of parent/child flow files
        if (event.getParentUuids() != null && !event.getParentUuids().isEmpty()) {
            for (String parent : event.getParentUuids()) {
                if (flowFileCache.isCached(parent)) {
                    //set this flowfileid pointing to the parent
                    parentFlowFile = flowFileCache.getEntry(parent);
                    flowFileCache.add(event.getFlowFileUuid(), parentFlowFile);
                    //if the parent == the id of the flowfile in the cache it means this is a starting flow that relates to another starting feed flow
                    //likely the flow files got merged and are linked.
                    //track this relationship
                    if (parentFlowFile.getId().equals(parent) && event.isStartOfJob()) {
                        //relate them
                        parentFlowFile.addChildFlowFile(event.getFeedFlowFile().getId());
                    }
                    //assign the event flow as a child to the feed flow
                    if (!event.getFlowFileUuid().equals(parentFlowFile.getId())) {
                        parentFlowFile.assignFlowFileToParent(event.getFlowFileUuid(), parentFlowFile.getId());
                    }
                } else {
                    //UNABLE TO FIND PARENT!
                }
            }
        }

        if (feedFlowFile == null && parentFlowFile != null) {
            feedFlowFile = parentFlowFile;
        }

        if (feedFlowFile == null) {
            //this is sometimes ok.
            //it is observed that sometimes a CONTENT_MODIFIED event will come in before the CREATE/RECEIVE provenance Event.
            //this will result in the CONTENT_MODIFIED not able to find the feed flow file.  That is ok since the CREATE event will come in and pick up the feed flow file
            throw new FeedFlowFileNotFoundException("Unable to find Feed Flow File for event " + event.getEventId() + ", Processor: " + event.getComponentId());
        }
        event.setFeedFlowFile(feedFlowFile);

        event.setFirstEventProcessorId(feedFlowFile.getFirstEventProcessorId());
        if (event.getChildUuids() != null && !event.getChildUuids().isEmpty()) {
            for (String child : event.getChildUuids()) {
                flowFileCache.add(child, feedFlowFile);
                //set the child activity
                feedFlowFile.assignFlowFileToParent(child, event.getFlowFileUuid());
                feedFlowFile.assignChildFlowFileStartTime(child, event.getEventTime().getMillis());
                feedFlowFile.addChildFlowFile(child);
            }
        }
        if (event.getProcessorType() == null) {
            if (event.isTerminatedByFailureRelationship()) {
                event.setProcessorType(KyloProcessorFlowType.FAILURE);
                event.setIsFailure(true);
            }
        }

        event.setJobFlowFileId(feedFlowFile.getId());

        feedFlowFile.addEvent(event);
        feedFlowFile.checkIfEventStartsTheFlowFile(event);
        feedFlowFile.checkAndMarkComplete(event);

        if (event.isEndingFlowFileEvent() && feedFlowFile.isFeedComplete()) {
            event.setIsEndOfJob(true);
            event.setIsFinalJobEvent(true);
        }
    }

    public void completeFlowFile(FeedFlowFile feedFlowFile){
        flowFileGuavaCache.invalidate(feedFlowFile);
    }


}
