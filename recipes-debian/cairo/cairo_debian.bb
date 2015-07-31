#
# base recipe: meta/recipes-graphics/cairo/cairo_1.12.16.bb
# base branch: daisy
#

require cairo.inc

PR = "${INC_PR}.0"

PACKAGES =+ " \
	cairo-gobject \
	cairo-script-interpreter \
	cairo-perf-utils \
"

SUMMARY_cairo-gobject = "The Cairo library GObject wrapper library"
DESCRIPTION_cairo-gobject = "A GObject wrapper library for the Cairo API."

SUMMARY_cairo-script-interpreter = "The Cairo library script interpreter"
DESCRIPTION_cairo-script-interpreter = "The Cairo script interpreter implements \
CairoScript.  CairoScript is used by tracing utilities to enable the ability \
to replay rendering."

DESCRIPTION_cairo-perf-utils = "The Cairo library performance utilities"

FILES_${PN} = "${libdir}/libcairo.so.*"
FILES_${PN}-dev += "${libdir}/cairo/*.la ${libdir}/cairo/*.so"
FILES_cairo-gobject = "${libdir}/libcairo-gobject.so.*"
FILES_cairo-script-interpreter = "${libdir}/libcairo-script-interpreter.so.*"
FILES_cairo-perf-utils = "${bindir}/cairo-* ${libdir}/cairo/libcairo-trace.so*"

do_install_append () {
	rm -rf ${D}${bindir}/cairo-sphinx
	rm -rf ${D}${libdir}/cairo/cairo-fdr*
	rm -rf ${D}${libdir}/cairo/cairo-sphinx*
	rm -rf ${D}${libdir}/cairo/.debug/cairo-fdr*
	rm -rf ${D}${libdir}/cairo/.debug/cairo-sphinx*
}

DEBIANNAME_cairo-perf-utils = "cairo-perf-utils"

# Skip the QA check for symbolic link .so files in cairo-perf-utils.
# In debian, libcairo-trace.so is shipped to cairo-perf-utils.
INSANE_SKIP_cairo-perf-utils = "dev-so"
