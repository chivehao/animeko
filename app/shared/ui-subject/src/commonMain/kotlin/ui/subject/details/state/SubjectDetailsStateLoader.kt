/*
 * Copyright (C) 2024 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

package me.him188.ani.app.ui.subject.details.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import me.him188.ani.app.tools.MonoTasker
import me.him188.ani.app.ui.search.SearchProblem
import me.him188.ani.utils.coroutines.childScope
import me.him188.ani.utils.platform.annotations.TestOnly
import kotlin.coroutines.cancellation.CancellationException

/**
 * @see SubjectDetailsState
 */
@Stable
class SubjectDetailsStateLoader(
    private val subjectDetailsStateFactory: SubjectDetailsStateFactory,
    private val backgroundScope: CoroutineScope,
) {
    private var runningFlowScope: CoroutineScope? = null
    private val tasker = MonoTasker(backgroundScope)

    val isLoading get() = tasker.isRunning

    fun load(
        subjectId: Int
    ): Deferred<*> {
        if (subjectDetailsStateFlow?.value?.info?.subjectId == subjectId) {
            // 已经加载完成了
            return completedJob
        }
        return tasker.async {
            withContext(Dispatchers.Main) {
                subjectDetailsStateProblem = null
                subjectDetailsStateFlow = null
            }
            runningFlowScope?.cancel()
            val flowScope = backgroundScope.childScope()
            runningFlowScope = flowScope
            val resp = try {
                subjectDetailsStateFactory.create(subjectId).stateIn(flowScope)
            } catch (e: CancellationException) {
                flowScope.cancel()
                throw e
            } catch (e: Exception) {
                flowScope.cancel()
                withContext(Dispatchers.Main) {
                    subjectDetailsStateProblem = SearchProblem.fromException(e)
                    subjectDetailsStateFlow = null
                }
                throw e
                return@async
            }
            withContext(Dispatchers.Main) {
                subjectDetailsStateFlow = resp
            }
        }
    }

    fun clear() {
        tasker.cancel()
        subjectDetailsStateProblem = null
        runningFlowScope?.cancel()
        runningFlowScope = null
        subjectDetailsStateFlow = null
    }

    var subjectDetailsStateProblem: SearchProblem? by mutableStateOf(null)
        private set
    var subjectDetailsStateFlow: StateFlow<SubjectDetailsState>? by mutableStateOf(null)
        private set

    private companion object {
        private val completedJob: Deferred<*> = CompletableDeferred(Unit)
    }
}

@TestOnly
fun createTestSubjectDetailsLoader(
    backgroundScope: CoroutineScope,
    subjectDetailsStateFactory: SubjectDetailsStateFactory = TestSubjectDetailsStateFactory(),
): SubjectDetailsStateLoader {
    return SubjectDetailsStateLoader(subjectDetailsStateFactory, backgroundScope)
}
