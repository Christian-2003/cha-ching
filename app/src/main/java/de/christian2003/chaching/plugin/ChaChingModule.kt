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
import de.christian2003.chaching.domain.repository.AppsRepository
import de.christian2003.chaching.domain.repository.TransferRepository
import de.christian2003.chaching.domain.repository.TypeRepository
import de.christian2003.chaching.plugin.infrastructure.backup.JsonBackupService
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingDatabase
import de.christian2003.chaching.plugin.infrastructure.db.ChaChingRepository
import de.christian2003.chaching.plugin.infrastructure.db.TransferDao
import de.christian2003.chaching.plugin.infrastructure.db.TypeDao
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
    @Singleton
    fun provideChaChingDatabase(
        @ApplicationContext context: Context
    ): ChaChingDatabase = ChaChingDatabase.getInstance(context)

    @Provides
    fun provideTypeDao(db: ChaChingDatabase): TypeDao = db.typeDao

    @Provides
    fun provideTransferDao(db: ChaChingDatabase): TransferDao = db.transferDao

    @Provides
    fun provideAnalysisService(
        transferRepository: TransferRepository,
        typeRepository: TypeRepository
    ): AnalysisService = AnalysisSquasher(AnalysisServiceImpl(transferRepository, typeRepository))

    @Provides
    fun provideAppsRepository(@ApplicationContext context: Context): AppsRepository {
        val application: ChaChingApplication = context as ChaChingApplication
        return AppsRestRepository(context.packageName, application.getClient())
    }

}
