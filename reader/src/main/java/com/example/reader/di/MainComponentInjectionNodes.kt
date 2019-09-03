package com.example.reader.di

import com.example.reader.ui.article.ArticlesListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainComponentInjectionNodes {

    @FragmentScope
    @ContributesAndroidInjector
    internal abstract fun contributeArticlesListFragment(): ArticlesListFragment

}
