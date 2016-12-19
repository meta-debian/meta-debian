SUMMARY = "YAML parser and emitter for Python"
DESCRIPTION = "\
 Python-yaml is a complete YAML 1.1 parser and emitter for Python.  It can parse \
 all examples from the specification. The parsing algorithm is simple enough to \
 be a reference for YAML parser implementors. A simple extension API is also \
 provided.  The package is built using libyaml for improved speed."

require pyyaml.inc
PR = "${INC_PR}.0"

do_install_append() {
	# remove unwanted files
	rm -rf ${D}${libdir}/python*/*/yaml/*.pyc
}
