package de.christian2003.chaching.plugin

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.christian2003.chaching.application.analysis.AnalysisService
import de.christian2003.chaching.application.analysis.AnalysisServiceImpl
import de.christian2003.chaching.application.analysis.AnalysisSquasher
import de.christian2003.chaching.application.backup.BackupImportRepository
import de.christian2003.chaching.application.backup.BackupService
import de.christian2003.chaching.application.repository.AnalysisRepository
import de.christian2003.chaching.application.repository.AppsRepository
import de.christian2003.chaching.application.repository.TransferRepository
import de.christian2003.chaching.application.repository.TypeRepository
import de.christian2003.chaching.application.services.ValueFormatterService
import de.christian2003.chaching.plugin.infrastructure.backup.JsonBackupService
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingRepository
import de.christian2003.chaching.plugin.infrastructure.rest.HttpClientProvider
import de.christian2003.chaching.plugin.infrastructure.rest.apps.AppsRestRepository
import okhttp3.OkHttpClient
import javax.inject.Singleton


/**
 * Hilt module for singleton-scoped bindings.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class BindingsModule {

    @Binds
    abstract fun bindTypeRepository(
        impl: ChaChingRepository
    ): TypeRepository

    @Binds
    abstract fun bindTransferRepository(
        impl: ChaChingRepository
    ): TransferRepository

    @Binds
    abstract fun bindImportRepository(
        impl: ChaChingRepository
    ): BackupImportRepository

    @Binds
    abstract fun bindAnalysisRepository(
        impl: ChaChingRepository
    ): AnalysisRepository

    @Binds
    abstract fun provideAppsRepository(
        impl: AppsRestRepository
    ): AppsRepository

    @Binds
    abstract fun bindBackupService(
        impl: JsonBackupService
    ): BackupService

}


/**
 * Hilt module for singleton-scoped providers
 */
@Module
@InstallIn(SingletonComponent::class)
class ProvidersModule {

    @Provides
    fun provideChaChingRepository(
        @ApplicationContext context: Context
    ): ChaChingRepository = (context as ChaChingApplication).getRepository()

    @Provides
    fun provideAnalysisService(
        transferRepository: TransferRepository,
        typeRepository: TypeRepository
    ): AnalysisService = AnalysisSquasher(AnalysisServiceImpl(transferRepository, typeRepository))

    @Provides
    fun provideValueFormatterService(): ValueFormatterService = ValueFormatterService()

    @Provides
    fun providePackageName(
        @ApplicationContext context: Context
    ): String = context.packageName

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context
    ): OkHttpClient = HttpClientProvider().provideOkHttpClient(context)

}
