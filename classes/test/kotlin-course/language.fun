fun foo(arg1, arg2) {
    fun bar(arg) {
        var local = arg1 + arg2 * arg // a comment
    }

    if (arg1 == arg2 + 5) {
        return 0
    } else {
        while (arg1 != arg2) {
            arg1 = bar(arg1 * 2)
        }
    }
}