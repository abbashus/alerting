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

package org.opensearch.alerting.script

import org.opensearch.alerting.model.Monitor
import org.opensearch.alerting.model.MonitorRunResult
import org.opensearch.alerting.model.Trigger
import java.time.Instant

abstract class TriggerExecutionContext(
    open val monitor: Monitor,
    open val results: List<Map<String, Any>>,
    open val periodStart: Instant,
    open val periodEnd: Instant,
    open val error: Exception? = null
) {

    constructor(monitor: Monitor, trigger: Trigger, monitorRunResult: MonitorRunResult<*>) :
        this(
            monitor, monitorRunResult.inputResults.results, monitorRunResult.periodStart,
            monitorRunResult.periodEnd, monitorRunResult.scriptContextError(trigger)
        )

    /**
     * Mustache templates need special permissions to reflectively introspect field names. To avoid doing this we
     * translate the context to a Map of Strings to primitive types, which can be accessed without reflection.
     */
    open fun asTemplateArg(): Map<String, Any?> {
        return mapOf(
            "monitor" to monitor.asTemplateArg(),
            "results" to results,
            "periodStart" to periodStart,
            "periodEnd" to periodEnd,
            "error" to error
        )
    }
}
