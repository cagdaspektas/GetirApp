package com.example.getirapp.common.domain

interface MultiParameterUseCase<In,Out>{
    fun execute(vararg params: In): Out
}

interface SingleParaMeterUseCase<In,Out> {
    fun execute(param: In): Out
}

interface NoParaMeterUseCase<Out> {
    fun execute(): Out
}