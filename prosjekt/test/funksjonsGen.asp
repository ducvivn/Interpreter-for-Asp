# Eksempel 1
print("")
print("--- eksempel 1 --- ")

def x2 (x):
	return x * x

f = x2

print("f(8)", "=", f(8))


# Eksempel 2
print("")
print("--- eksempel 2 --- ")

def make_add_n (n):
	def f (v):
		return v + n

	return f

add1 = make_add_n(1)
add5 = make_add_n(5)

print("add1(2)", "=", add1(2))
print("add5(2)", "=", add5(2))
