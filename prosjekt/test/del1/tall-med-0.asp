# I Asp får tall ikke lov å starte med sifferet 0, men
# selve tallet 0 er selvfølgelig et unntak.

a = 0   # Dette er helt OK.
a = 04  # Men dette to tall: 0 og 4!
    	# Slikt er OK for skanneren,
	# men parseren (del 2) vil gi
	# en feilmelding.
