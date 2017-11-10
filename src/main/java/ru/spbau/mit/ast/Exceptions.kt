package ru.spbau.mit.ast

class WrongNumberOfFunctionArgumentsException(val functionName: String) : Exception()

class PrintlnRedefinitionException : Exception()
