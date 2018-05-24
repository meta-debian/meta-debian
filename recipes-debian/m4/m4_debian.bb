require m4.inc

# Avoid warnings treated as error
EXTRA_OECONF += "--disable-gcc-warnings"

BBCLASSEXTEND = "nativesdk"
