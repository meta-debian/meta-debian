SUMMARY = "OpenGL Extension Wrangler"
DESCRIPTION = "The OpenGL Extension Wrangler, GLEW for short, is a library that \
handles initialization of OpenGL extensions in a portable and simple \
way. Once the program initializes the library and checks the \
availability of extensions, it can safely call the entry points defined \
by the extension. Currently GLEW supports almost all the extensions \
found in the OpenGL extension registry (http://www.opengl.org/registry)."
HOMEPAGE = "http://glew.sourceforge.net"

inherit debian-package
PV = "1.10.0"

LICENSE = "BSD-3-Clause & MIT & GPLv2+"
LIC_FILES_CHKSUM = " \
    file://LICENSE.txt;md5=2ac251558de685c6b9478d89be3149c2 \
    file://auto/src/khronos_license.h;md5=d976c98fb34518cc0ba7dcf096a724cf \
    file://auto/src/mesa_license.h;md5=a966d4b81f6f6aba352ec340d243a0bd \
    file://auto/src/glew_license.h;md5=9f2e6c2fa175feb4f4ab1e6c0872120b \
    file://auto/bin/parse_spec.pl;beginline=2;endline=8;md5=4bdfac46910d144bdc6b4f71638116b0 \
"

# Avoid strip files to pass do_package_qa
SRC_URI += "file://disable-strip.patch"

DEPENDS = "virtual/libx11 virtual/libgl libglu libxi libxmu"

inherit pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

# use headers from ${S}/include
CFLAGS_append = " -Iinclude"

do_compile() {
	sed -i -e "s@-L/usr@-L${STAGING_DIR_HOST}${prefix}@g" config/Makefile.linux

	# Add -fpic to avoid warning
	# WARNING: QA Issue: ELF binary '.../libGLEW.so.1.10.0' has relocations in .text [textrel]
	# WARNING: QA Issue: ELF binary '.../libGLEWmx.so.1.10.0' has relocations in .text [textrel]
	oe_runmake LD='${CC}' CFLAGS.SO='-fpic' \
		GL_LDFLAGS=-lGL \
		GLU_LDFLAGS=-lGLU \
		GLUT_LDFLAGS=-lglut \
		LIBDIR=${libdir}
}

do_install() {
	oe_runmake install.all \
		GLEW.DEST=${D}${prefix} \
		LIBDIR=${D}${libdir} \
		INCDIR=${D}${includedir}/GL \
		BINDIR=${D}${bindir}
}

PACKAGES =+ "${PN}-utils lib${PN}mx lib${PN}mx-dev"

FILES_${PN}-utils = "${bindir}/*"
FILES_lib${PN}mx = "${libdir}/libGLEWmx${SOLIBS}"
FILES_lib${PN}mx-dev = "${libdir}/libGLEWmx${SOLIBSDEV} \
                        ${libdir}/pkgconfig/glewmx.pc \
                        "

DEBIAN_NOAUTONAME_${PN}-utils = "1"
DEBIAN_NOAUTONAME_lib${PN}mx-dev = "1"

RPROVIDES_${PN} += "lib${PN}"
RPROVIDES_${PN}-dev += "lib${PN}-dev"
RPROVIDES_${PN}-dbg += "lib${PN}-dbg"
