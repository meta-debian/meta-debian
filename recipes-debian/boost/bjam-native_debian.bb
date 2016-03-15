include boost.inc

SUMMARY = "Portable Boost.Jam build tool for boost"
SECTION = "devel"

PR = "r1"

# bjam is stripped by default, this causes QA warning
# while stripping it from do_populate_sysroot()
SRC_URI += "file://bjam-native-build-bjam.debug.patch"

inherit native

do_compile() {
	./bootstrap.sh --with-toolset=gcc
}

do_install() {
	install -d ${D}${bindir}
	# install unstripped version for bjam
	install -c -m 755 bjam.debug ${D}${bindir}/bjam
}
