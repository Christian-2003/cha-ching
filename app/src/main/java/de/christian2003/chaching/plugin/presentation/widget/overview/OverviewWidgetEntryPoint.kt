package de.christian2003.chaching.plugin.presentation.widget.overview

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.christian2003.chaching.application.analysis.small.SmallAnalysisUseCase
import de.christian2003.chaching.application.services.ValueFormatterService


/**
 * Custom entry point for the overview glance widget.
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface OverviewWidgetEntryPoint {

    /**
     * Gets the use case for the small analysis.
     *
     * @return  Use case for the small analysis.
     */
    fun getSmallAnalysisUseCase(): SmallAnalysisUseCase

    /**
     * Gets the value formatter service.
     *
     * @return  Value formatter service.
     */
    fun getValueFormatterService(): ValueFormatterService

}
