fun gcd (a, b) {
	println(a, b)
	if (b == 0) {
		return a
	} else {
		return gcd (b, a % b)
	}
}

var a = 8
var b = 13
println(gcd(a, b))
