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

package org.opensearch.alerting.action

import org.opensearch.action.ActionRequest
import org.opensearch.action.ActionRequestValidationException
import org.opensearch.action.support.WriteRequest
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import java.io.IOException

class DeleteMonitorRequest : ActionRequest {

    val monitorId: String
    val refreshPolicy: WriteRequest.RefreshPolicy

    constructor(monitorId: String, refreshPolicy: WriteRequest.RefreshPolicy) : super() {
        this.monitorId = monitorId
        this.refreshPolicy = refreshPolicy
    }

    @Throws(IOException::class)
    constructor(sin: StreamInput) : super() {
        monitorId = sin.readString()
        refreshPolicy = WriteRequest.RefreshPolicy.readFrom(sin)
    }

    override fun validate(): ActionRequestValidationException? {
        return null
    }

    @Throws(IOException::class)
    override fun writeTo(out: StreamOutput) {
        out.writeString(monitorId)
        refreshPolicy.writeTo(out)
    }
}
