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

package org.opensearch.alerting.action

import org.opensearch.alerting.model.Table
import org.opensearch.common.io.stream.BytesStreamOutput
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.search.fetch.subphase.FetchSourceContext
import org.opensearch.test.OpenSearchTestCase

class GetDestinationsRequestTests : OpenSearchTestCase() {

    fun `test get destination request`() {

        val table = Table("asc", "sortString", null, 1, 0, "")
        val req = GetDestinationsRequest("1234", 1L, FetchSourceContext.FETCH_SOURCE, table, "slack")
        assertNotNull(req)

        val out = BytesStreamOutput()
        req.writeTo(out)
        val sin = StreamInput.wrap(out.bytes().toBytesRef().bytes)
        val newReq = GetDestinationsRequest(sin)
        assertEquals("1234", newReq.destinationId)
        assertEquals(1L, newReq.version)
        assertEquals(FetchSourceContext.FETCH_SOURCE, newReq.srcContext)
        assertEquals(table, newReq.table)
        assertEquals("slack", newReq.destinationType)
    }

    fun `test get destination request without src context`() {

        val table = Table("asc", "sortString", null, 1, 0, "")
        val req = GetDestinationsRequest("1234", 1L, null, table, "slack")
        assertNotNull(req)

        val out = BytesStreamOutput()
        req.writeTo(out)
        val sin = StreamInput.wrap(out.bytes().toBytesRef().bytes)
        val newReq = GetDestinationsRequest(sin)
        assertEquals("1234", newReq.destinationId)
        assertEquals(1L, newReq.version)
        assertEquals(null, newReq.srcContext)
        assertEquals(table, newReq.table)
        assertEquals("slack", newReq.destinationType)
    }

    fun `test get destination request without destinationId`() {

        val table = Table("asc", "sortString", null, 1, 0, "")
        val req = GetDestinationsRequest(null, 1L, FetchSourceContext.FETCH_SOURCE, table, "slack")
        assertNotNull(req)

        val out = BytesStreamOutput()
        req.writeTo(out)
        val sin = StreamInput.wrap(out.bytes().toBytesRef().bytes)
        val newReq = GetDestinationsRequest(sin)
        assertEquals(null, newReq.destinationId)
        assertEquals(1L, newReq.version)
        assertEquals(FetchSourceContext.FETCH_SOURCE, newReq.srcContext)
        assertEquals(table, newReq.table)
        assertEquals("slack", newReq.destinationType)
    }

    fun `test get destination request with filter`() {

        val table = Table("asc", "sortString", null, 1, 0, "")
        val req = GetDestinationsRequest(null, 1L, FetchSourceContext.FETCH_SOURCE, table, "slack")
        assertNotNull(req)

        val out = BytesStreamOutput()
        req.writeTo(out)
        val sin = StreamInput.wrap(out.bytes().toBytesRef().bytes)
        val newReq = GetDestinationsRequest(sin)
        assertEquals(null, newReq.destinationId)
        assertEquals(1L, newReq.version)
        assertEquals(FetchSourceContext.FETCH_SOURCE, newReq.srcContext)
        assertEquals(table, newReq.table)
        assertEquals("slack", newReq.destinationType)
    }
}
