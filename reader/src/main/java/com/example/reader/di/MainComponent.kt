package com.example.reader.di

import com.example.reader.MainApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    MainModule::class,
    MainComponentInjectionNodes::class
])
interface MainComponent {

    fun inject(application: MainApplication)

}
