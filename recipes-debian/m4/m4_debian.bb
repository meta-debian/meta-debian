require m4.inc

PR = "r1"

# Avoid warnings treated as error
EXTRA_OECONF += "--disable-gcc-warnings"

BBCLASSEXTEND = "nativesdk"
