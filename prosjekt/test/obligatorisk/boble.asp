# Boblesortering
# (Den enkleste men akk så ineffektive sorteringsmetoden)

def boblesorter (a):
    global ombyttinger
    while True:
        endret = False
        for i in range(1,len(a)):
            if a[i-1] > a[i]:
                t = a[i-1];  a[i-1] = a[i];  a[i] = t
                ombyttinger = ombyttinger + 1
                endret = True
        if not endret: return 0

data = [ 3, 17, -3, 0, 3, 1, 12 ]
ombyttinger = 0
boblesorter(data)
print("Resultatet etter", ombyttinger, "ombyttinger er", data)
