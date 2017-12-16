package org.de_studio.recentappswitcher.utils

import rx.Observable
import rx.subjects.PublishSubject

/**
 * Created by HaiNguyen on 12/16/17.
 */
object EventBus {
    private val eventRelay = PublishSubject.create<String>()
    fun fireEvent(action: String) = eventRelay.onNext(action)
    fun observeEvent(): Observable<String> = eventRelay
}