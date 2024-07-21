package core.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import storify.MainViewModel


val appModule = module {
    singleOf(::MainViewModel)
}
