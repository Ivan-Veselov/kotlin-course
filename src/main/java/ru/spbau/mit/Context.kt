package ru.spbau.mit

class ContextSymbolOverwritingException(val symbol: String) : Exception()

class ContextUndefinedSymbolException(val symbol: String) : Exception()

class Context private constructor (
    private val parent: FixedContext?,
    val builtinsHandler: BuiltinsHandler
) {
    private val variables: MutableMap<String, Variable> = HashMap()

    private val functions: MutableMap<String, Function> = HashMap()

    constructor(builtinsHandler: BuiltinsHandler) : this(null, builtinsHandler)

    constructor(parent: FixedContext) : this(parent, parent.builtinsHandler)

    fun getVariable(name: String): Variable =
        variables.getOrElse(name) {
            parent?.getVariable(name)
        } ?: throw ContextUndefinedSymbolException(name)

    fun getFunction(name: String): Function =
        functions.getOrElse(name) {
            parent?.getFunction(name)
        } ?: throw ContextUndefinedSymbolException(name)

    fun addVariable(name: String, variable: Variable) {
        if (variables.containsKey(name)) {
            throw ContextSymbolOverwritingException(name)
        }

        variables.put(name, variable)
    }

    fun addFunction(name: String, function: Function) {
        if (variables.containsKey(name)) {
            throw ContextSymbolOverwritingException(name)
        }

        functions.put(name, function)
    }

    fun fixed() = FixedContext(this)

    class FixedContext(source: Context) {
        val builtinsHandler: BuiltinsHandler = source.builtinsHandler

        private val parent: FixedContext? = source.parent

        private val variables: Map<String, Variable> = HashMap(source.variables)

        private val functions: Map<String, Function> = HashMap(source.functions)

        fun getVariable(name: String): Variable =
                variables.getOrElse(name) {
                    parent?.getVariable(name)
                } ?: throw ContextUndefinedSymbolException(name)

        fun getFunction(name: String): Function =
                functions.getOrElse(name) {
                    parent?.getFunction(name)
                } ?: throw ContextUndefinedSymbolException(name)
    }
}
