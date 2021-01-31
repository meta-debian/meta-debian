LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=7ffedb422684eb346c1fb5bb8fc5fe45"

inherit debian-package
require recipes-debian/sources/bro.inc

SRC_URI += " \
	file://remove-bro-config-h.patch \
	file://CMakeLists.txt \
	"

inherit native
inherit autotools cmake

DEBIAN_UNPACK_DIR = "${WORKDIR}/bro-${PV}-minimal"
S = "${WORKDIR}/bro-${PV}-minimal"

DEPENDS += "bison-native flex-native"

# Custom source tree to only build bifcl
S_BIFCL = "${S}/bifcl"

# cmake target path (See cmake.bbclass)
OECMAKE_SOURCEPATH = "${S_BIFCL}"

BIFCL_SRCS = " \
	cmake \
	src/bif_arg.cc \
	src/module_util.cc \
	src/bif_arg.h \
	src/module_util.h \
	src/builtin-func.y \
	src/builtin-func.l \
	src/bif_type.def \
	"

# Setup S_BIFCL
do_configure_prepend() {
	rm -rf ${S_BIFCL}
	mkdir -p ${S_BIFCL}

	# Custome CMakeLists.txt is required to only generate bifcl binary
	cp ${WORKDIR}/CMakeLists.txt ${S_BIFCL}

	# Other required sources
	for src in ${BIFCL_SRCS}; do
		cp -r ${S}/${src} ${S_BIFCL}
	done
}
