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
 *   Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package org.opensearch.alerting.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.opensearch.action.get.GetRequest
import org.opensearch.action.get.GetResponse
import org.opensearch.alerting.core.model.ScheduledJob
import org.opensearch.alerting.elasticapi.suspendUntil
import org.opensearch.alerting.model.destination.Destination
import org.opensearch.alerting.model.destination.email.EmailAccount
import org.opensearch.alerting.model.destination.email.EmailGroup
import org.opensearch.client.Client
import org.opensearch.common.bytes.BytesReference
import org.opensearch.common.xcontent.LoggingDeprecationHandler
import org.opensearch.common.xcontent.NamedXContentRegistry
import org.opensearch.common.xcontent.XContentHelper
import org.opensearch.common.xcontent.XContentType

/**
 * This is an accessor class to retrieve documents/information from the Alerting config index.
 */
class AlertingConfigAccessor {
    companion object {
        suspend fun getDestinationInfo(client: Client, xContentRegistry: NamedXContentRegistry, destinationId: String): Destination {
            val jobSource = getAlertingConfigDocumentSource(client, "Destination", destinationId)
            return withContext(Dispatchers.IO) {
                val xcp = XContentHelper.createParser(
                    xContentRegistry, LoggingDeprecationHandler.INSTANCE,
                    jobSource, XContentType.JSON
                )
                val destination = Destination.parseWithType(xcp)
                destination
            }
        }

        suspend fun getEmailAccountInfo(client: Client, xContentRegistry: NamedXContentRegistry, emailAccountId: String): EmailAccount {
            val source = getAlertingConfigDocumentSource(client, "Email account", emailAccountId)
            return withContext(Dispatchers.IO) {
                val xcp = XContentHelper.createParser(xContentRegistry, LoggingDeprecationHandler.INSTANCE, source, XContentType.JSON)
                val emailAccount = EmailAccount.parseWithType(xcp)
                emailAccount
            }
        }

        suspend fun getEmailGroupInfo(client: Client, xContentRegistry: NamedXContentRegistry, emailGroupId: String): EmailGroup {
            val source = getAlertingConfigDocumentSource(client, "Email group", emailGroupId)
            return withContext(Dispatchers.IO) {
                val xcp = XContentHelper.createParser(xContentRegistry, LoggingDeprecationHandler.INSTANCE, source, XContentType.JSON)
                val emailGroup = EmailGroup.parseWithType(xcp)
                emailGroup
            }
        }

        private suspend fun getAlertingConfigDocumentSource(
            client: Client,
            type: String,
            docId: String
        ): BytesReference {
            val getRequest = GetRequest(ScheduledJob.SCHEDULED_JOBS_INDEX, docId).routing(docId)
            val getResponse: GetResponse = client.suspendUntil { client.get(getRequest, it) }
            if (!getResponse.isExists || getResponse.isSourceEmpty) {
                throw IllegalStateException("$type document with id $docId not found or source is empty")
            }

            return getResponse.sourceAsBytesRef
        }
    }
}
