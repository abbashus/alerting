/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 *
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

/*
 *   Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License").
 *   You may not use this file except in compliance with the License.
 *   A copy of the License is located at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   or in the "license" file accompanying this file. This file is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *   express or implied. See the License for the specific language governing
 *   permissions and limitations under the License.
 */

package org.opensearch.alerting.core

import org.opensearch.action.ActionListener
import org.opensearch.action.admin.indices.create.CreateIndexRequest
import org.opensearch.action.admin.indices.create.CreateIndexResponse
import org.opensearch.alerting.core.model.ScheduledJob
import org.opensearch.client.AdminClient
import org.opensearch.cluster.health.ClusterIndexHealth
import org.opensearch.cluster.service.ClusterService
import org.opensearch.common.settings.Settings
import org.opensearch.common.xcontent.XContentType

/**
 * Initialize the OpenSearch components required to run [ScheduledJobs].
 *
 * [initScheduledJobIndex] is called before indexing a new scheduled job. It verifies that the index exists before
 * allowing the index to go through. This is to ensure the correct mappings exist for [ScheduledJob].
 */
class ScheduledJobIndices(private val client: AdminClient, private val clusterService: ClusterService) {

    companion object {
        @JvmStatic
        fun scheduledJobMappings(): String {
            return ScheduledJobIndices::class.java.classLoader.getResource("mappings/scheduled-jobs.json").readText()
        }
    }
    /**
     * Initialize the indices required for scheduled jobs.
     * First check if the index exists, and if not create the index with the provided callback listeners.
     *
     * @param actionListener A callback listener for the index creation call. Generally in the form of onSuccess, onFailure
     */
    fun initScheduledJobIndex(actionListener: ActionListener<CreateIndexResponse>) {
        if (!scheduledJobIndexExists()) {
            var indexRequest = CreateIndexRequest(ScheduledJob.SCHEDULED_JOBS_INDEX)
                .mapping(ScheduledJob.SCHEDULED_JOB_TYPE, scheduledJobMappings(), XContentType.JSON)
                .settings(Settings.builder().put("index.hidden", true).build())
            client.indices().create(indexRequest, actionListener)
        }
    }

    fun scheduledJobIndexExists(): Boolean {
        val clusterState = clusterService.state()
        return clusterState.routingTable.hasIndex(ScheduledJob.SCHEDULED_JOBS_INDEX)
    }

    /**
     * Check if the index exists. If the index does not exist, return null.
     */
    fun scheduledJobIndexHealth(): ClusterIndexHealth? {
        var indexHealth: ClusterIndexHealth? = null

        if (scheduledJobIndexExists()) {
            val indexRoutingTable = clusterService.state().routingTable.index(ScheduledJob.SCHEDULED_JOBS_INDEX)
            val indexMetaData = clusterService.state().metadata().index(ScheduledJob.SCHEDULED_JOBS_INDEX)

            indexHealth = ClusterIndexHealth(indexMetaData, indexRoutingTable)
        }
        return indexHealth
    }
}
