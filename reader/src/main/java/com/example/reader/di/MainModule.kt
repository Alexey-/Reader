package com.example.reader.di

import com.example.base.model.network.ApiManager
import com.example.base.model.network.IApiManager
import com.example.reader.BuildConfig
import com.example.reader.MainApplication
import com.example.reader.db.MainDatabase
import com.example.reader.model.article.ArticleManager
import com.example.reader.model.article.IArticleManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MainModule {

    @Singleton
    @Provides
    fun mainDatabase(): MainDatabase {
        return MainDatabase.createDatabase(MainApplication.context)
    }

    @Singleton
    @Provides
    fun apiManager(): IApiManager {
        if (BuildConfig.SERVER_BUILD_TYPE == "fake") {
            return Class.forName("com.example.reader.fakeserver.FakeApiManager")?.newInstance() as IApiManager
        } else {
            return ApiManager(BuildConfig.HOST)
        }
    }

    @Singleton
    @Provides
    fun articlesManager(database: MainDatabase, apiManager: IApiManager): IArticleManager {
        return ArticleManager(database.wrapper, apiManager)
    }

}